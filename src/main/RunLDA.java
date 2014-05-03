package main;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import util.ArrayIndexComparator;

import latentDirichletAnalysis.LDAGibbsSampler;
import data.DataInitiator;
import data.WordCountTuple;


public class RunLDA {
	
	private static List<List<WordCountTuple>> wordcount;
	private static ArrayList<String> vocabulary;
	private static ArrayList<String> documentOrder;
	private static HashMap<String,String> documentNames;
	private static int nTopics = 10;
	private static double alpha = 50/nTopics;
	private static double eta = 0.1;
	private static int nIterations = 100;
	private static int nBurnInIterations = 10;
	private static int nReadInIterations = 10;
	private static int nWords;
	private static boolean printProgress = true;
	
	private static double[][] beta;
	private static double[][] theta;
	
	/**
	 * Run LDA gibbs sampler.
	 * @param args Arguments as follows:
	 * "-nTopics x"
	 * "-alpha x"
	 * "-eta x"
	 * "-nIterations x"
	 * "-nReadInIterations x"
	 * "-nBurnInIterations x"
	 * "-printProgress true/false"
	 * 
	 */
	public static void main(String[] args) {
		
		// Parse input arguments
		if(args.length > 0) {
			try {
				if(args.length >= 2) {
					for(int i = 0; i < args.length; i = i + 2) {
						if(args[i].equals("-nTopics")) {
							nTopics = Integer.parseInt(args[i+1]);
						} else if(args[i].equals("-alpha")) {
							alpha = Double.parseDouble(args[i+1]);
						} else if(args[i].equals("-eta")) {
							eta = Double.parseDouble(args[i+1]);
						} else if(args[i].equals("-nIterations")) {
							nIterations = Integer.parseInt(args[i+1]);
						} else if(args[i].equals("-nReadInIterations")) {
							nReadInIterations = Integer.parseInt(args[i+1]);
						} else if(args[i].equals("-nBurnInIterations")) {
							nBurnInIterations = Integer.parseInt(args[i+1]);
						} else if(args[i].equals("-printProgress")) {
							printProgress = Boolean.parseBoolean(args[i+1]);
						}
					}
				} else {
					if(args[0].equals("help")) {
						System.out.println("Usage: RunLDA [parameters] \n\n" + 
										   "parameters: \n" + 
										   "\t -nTopics x \n" +
										   "\t -nTopics x \n" +
										   "\t -alpha x \n" +
										   "\t -eta x \n" +
										   "\t -nIterations x \n" +
										   "\t -nReadInIterations x \n" +
										   "\t -nBurnInIterations x \n");
					}
				}
				
			} catch(NumberFormatException e) {
				System.out.println("Wrong format of input parameters. Please see documentation or type \"RunLDA help\".");
			}
		}
		
		//Import words and count them
		DataInitiator dataInit = new DataInitiator("wordcount_lyrics.txt", "vocabulary_lyrics.txt");
		wordcount = dataInit.getWordcount();
		vocabulary = dataInit.getVocabulary();
		documentOrder = dataInit.getDocumentOrder();
		documentNames = dataInit.getDocumentNames();
		
		nWords = vocabulary.size();
		
		LDAGibbsSampler ldags = new LDAGibbsSampler(wordcount, nTopics, alpha, eta, 
				nIterations, nReadInIterations, nBurnInIterations, nWords, printProgress);
		ldags.run();
		
		beta = ldags.getBeta();
		theta = ldags.getTheta();
		double[][] distances = computeDistances();
		
		printNBestWords(5);
//		printNRelatedDocuments(distances,10);
		printLyricsStats(3,5);
		
		
	}
	
	private static void printLyricsStats(int nTopicsToPrint, int nWordsToPrint) {
		int nDocuments = theta.length;
		int nTopics = theta[0].length;
		
		for(int d = 0; d < nDocuments; d++) {
			ArrayIndexComparator comparator = new ArrayIndexComparator(theta[d]);
			Integer[] indexes = comparator.createIndexArray();
			Arrays.sort(indexes, comparator);
			System.out.println(d);
			for(int i = indexes.length - 1; i > indexes.length - 1 - nTopicsToPrint; i--) {
				ArrayIndexComparator comparator2 = new ArrayIndexComparator(beta[indexes[i]]);
				Integer[] indexes2 = comparator2.createIndexArray();
				Arrays.sort(indexes2, comparator2);
				String fileName = documentNames.get(documentOrder.get(d));
				PrintWriter pw = null;
				try {
					pw = new PrintWriter("lyricsdata/" + fileName + ".stats");
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				for(int j = indexes2.length - 1; j > indexes2.length - 1 - nWordsToPrint; j--) {
					pw.println(vocabulary.get(indexes2[j]));// + ": " + beta[indexes[i]][indexes2[j]]);
				}
				pw.close();
			}
		}
	}

	private static double[][] computeDistances() {
		int nDocuments = theta.length;
		int nTopics = theta[0].length;
		double[][] distances = new double[nDocuments][nDocuments];
		for(int d = 0; d < nDocuments; d++) {
			for(int d2 = d + 1 ; d2 < nDocuments; d2++) {
				double dist = 0;
				for(int k = 0; k < nTopics; k++) {
					dist += (theta[d][k] - theta[d2][k])*(theta[d][k] - theta[d2][k]);
				}
				distances[d][d2] = dist;
				distances[d2][d] = dist;
			}
		}
		return distances;
	}
	
	private static void printNRelatedDocuments(double[][] distances, int nRelatedDoc) {
		int nDocuments = distances.length;
		for(int d = 0; d < nDocuments; d++) {
			
			ArrayIndexComparator comparator = new ArrayIndexComparator(distances[d]);
			Integer[] indexes = comparator.createIndexArray();
			Arrays.sort(indexes, comparator);
			PrintWriter pw = null;
			try {
				pw = new PrintWriter("lyricsdata/" + documentNames.get(documentOrder.get(d)) + ".relatedsongs");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			for(int i = 1; i <= nRelatedDoc; i++) {
				pw.println(documentNames.get(documentOrder.get(indexes[i])) + " (" + 
						((double)Math.round(distances[d][indexes[i]] * 100000) / 100000)  + ")");
			}
			pw.close();
//			System.out.println("\n-----------------------------------------\n");
		}
	}
	
	private static void printNBestWords(int nBestWords) {
		for(int k = 0; k < nTopics; k++) {
			
			ArrayIndexComparator comparator = new ArrayIndexComparator(beta[k]);
			Integer[] indexes = comparator.createIndexArray();
			Arrays.sort(indexes, comparator);
			for(int i = indexes.length - 1; i > indexes.length - 1 - nBestWords; i--) {
				System.out.println(vocabulary.get(indexes[i]) + ": " + beta[k][indexes[i]]);
			}
			
			System.out.println("\n-----------------------------------------\n");
		}
	}
	
}


