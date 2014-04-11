package main;

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
	private static HashMap<String,Integer> vocabulary;
	private static int nTopics = 10;
	private static double alpha = 52.0/nTopics;
	private static double eta = 0.1;
	private static int nIterations = 500;
	private static int nReadInIterations = 10;
	private static int nBurnInIterations = 200;
	private static int nWords;
	
	/**
	 * Run LDA gibbs sampler.
	 * @param args Arguments as follows:
	 * "-nTopics x"
	 * "-alpha x"
	 * "-eta x"
	 * "-nIterations x"
	 * "-nReadInIterations x"
	 * "-nBurnInIterations x"
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
		DataInitiator dataInit = new DataInitiator();
		wordcount = dataInit.getWordcount();
		nWords = dataInit.getnWords();
		vocabulary = dataInit.getVocabulary();
		ArrayList<String> idWords = new ArrayList<String>(nWords);
		for(int i = 0; i < nWords; i++) {
			idWords.add("");
		}
		
		for(String word : vocabulary.keySet()) {
			idWords.set(vocabulary.get(word), word);
		}
		
		LDAGibbsSampler ldags = new LDAGibbsSampler(wordcount, nTopics, alpha, eta, 
				nIterations, nReadInIterations, nBurnInIterations, nWords, false);
		ldags.run();
		
		double[][] beta = ldags.getBeta();
		double[][] theta = ldags.getTheta();
		
		int nBestWords = 5;
		for(int k = 0; k < nTopics; k++) {
			
			ArrayIndexComparator comparator = new ArrayIndexComparator(beta[k]);
			Integer[] indexes = comparator.createIndexArray();
			Arrays.sort(indexes, comparator);
			for(int i = indexes.length - 1; i > indexes.length - 1 - nBestWords; i--) {
				System.out.println(idWords.get(indexes[i]) + ": " + beta[k][indexes[i]]);
			}
			
			System.out.println("-------------------");
		}
	}
	
}


