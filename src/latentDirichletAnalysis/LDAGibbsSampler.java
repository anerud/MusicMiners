package latentDirichletAnalysis;

import java.util.ArrayList;
import java.util.List;

import data.WordCountTuple;

public class LDAGibbsSampler {
	
	//Input parameters
	private List<List<WordCountTuple>> wordcount;
	private int nTopics;
	private double alpha;
	private double eta;
	private int nIterations;
	private int nReadInIterations;
	private int nBurnInIterations;
	private int nWords;
	
	//Internal variables
	private int[][] topic_word; // How many times a term has been assigned a topic
	private int[] topic_cumul; // How many words a topic has.
	private int[][] document_topic; // How many words have been assigned to a topic in a document
	private int[] document_cumul; // Total number of words in a document.
	private ArrayList<ArrayList<ArrayList<Integer>>> topics; // The topic assignments
	private int nDocuments;
	private int nReads;
	private boolean printProgression;
	
	//Output variables
	private double[][] beta;
	private double[][] theta;
	
	public LDAGibbsSampler(List<List<WordCountTuple>> wordcount, int nTopics, 
			double alpha, double eta, int nIterations,
		    int nReadInIterations, int nBurnInIterations, int nWords, boolean printProgression) {
		//Input parameters
		this.wordcount = wordcount;
		this.nTopics = nTopics;
		this.alpha = alpha;
		this.eta = eta;
		this.nIterations = nIterations;
		this.nReadInIterations = nReadInIterations;
		this.nBurnInIterations = nBurnInIterations;
		this.nWords = nWords;
		this.printProgression = printProgression;
		
		//Internal variables
		nReads = (int) ((((double)(nIterations-nBurnInIterations))/((double)nReadInIterations)));
		nDocuments = wordcount.size();
		topic_word = new int[nTopics][nWords]; // How many times a term has been assigned a topic
		topic_cumul = new int[nTopics]; // How many words a topic has.
		document_topic = new int[nDocuments][nTopics]; // How many words have been assigned to a topic in a document
		document_cumul = new int[nDocuments]; // Total number of words in a document.
		topics = new ArrayList<ArrayList<ArrayList<Integer>>>(nDocuments);
		
		//Output variables
		beta = new double[nTopics][nWords];
		theta = new double[nDocuments][nTopics];
		
	}

	
	/**
	 * Runs the Gibbs sampler with the parameters specified when creating
	 * the LDAGibbsSampler
	 */
	public void run() {
		double startTime = System.currentTimeMillis();
		init();
		
		double p_cumul[] = new double[nTopics];
		
		//Loop over all iterations
		for(int iteration = 0; iteration < nIterations; iteration++) {
			
			//Loop over all documents
			for(int d = 0; d < nDocuments; d++) {
				ArrayList<ArrayList<Integer>> document = topics.get(d);
				List<WordCountTuple> wctList = wordcount.get(d);
				int nWordsInDoc = document.size();
				
				//Loop over all words
				for(int i = 0; i < nWordsInDoc ; i++) {
					ArrayList<Integer> word = document.get(i);
					int nRepetitions = word.size();
					int term = wctList.get(i).getID();
					
					//Loop over all repetitions of the word
					for(int j = 0; j < nRepetitions; j++) {
						int currentTopic = word.get(j);
						
						//Calculate probabilities
						for(int k = 0; k < nTopics; k++) {
							int correction = currentTopic == k ? 1 : 0;
							p_cumul[k] = ((double)(topic_word[k][term] - correction + eta)) * 
									   	  ((double)(document_topic[d][k] - correction + alpha)) /
									   	  ((double)(topic_cumul[k] - correction + ((double)nWords)*eta));
							if(k>0) {
								p_cumul[k] += p_cumul[k-1];
							}
						}
						
						//Sample new topic
						int newTopic = 0;
						double r = Math.random();
						for(int k = 0; k < nTopics; k++) {
							if(r < p_cumul[k]/p_cumul[nTopics-1]) {
								newTopic = k;
								break;
							}
						}
						
//						//Update counts
						if(currentTopic != newTopic) {
							// Decrease counts of old topic
							topic_word[currentTopic][term] -= 1;
				            topic_cumul[currentTopic] -= 1;
				            document_topic[d][currentTopic] -= 1;
				            
				            // Increase counts of new topic
							topic_word[newTopic][term] += 1;
				            topic_cumul[newTopic] += 1;
				            document_topic[d][newTopic] += 1;
				            word.set(j, newTopic);
						}
					}
				}
			}
			
			// Eventual read out of parameters
		    if(((iteration-nBurnInIterations)%nReadInIterations) == 0) {
		        if(iteration < nBurnInIterations) {
		            //Still burn in
		        	if(printProgression) {
			            System.out.println("Burn in iteration " + (iteration + 1) + " of " +
			                nBurnInIterations + " completed (sampling iteration " + (iteration + 1) +
			                " of " + nIterations + ')');
		        	}
		        } else {
		            //Read out parameters
		        	int read_nr = (int) ((((double)(iteration-nBurnInIterations))/((double)nReadInIterations)));
		        	
		        	//Read out beta
		        	for(int k = 0; k < nTopics; k++) {
		        		for(int i = 0; i < nWords; i++) {
		        			beta[k][i] += ((double)(topic_word[k][i] + eta))/
										  ((double)(topic_cumul[k] + ((double)nWords)*eta));
		        		}
		        	}
		        		
		        	//Read out theta
		        	for(int d = 0; d < nDocuments; d++) {
		        		for(int k = 0; k < nTopics; k++) {
		        			theta[d][k] += ((double)(document_topic[d][k] + alpha))/
										  ((double)(document_cumul[d] + ((double)nTopics)*alpha));
		        			
		        		}
		        	}
		        	
		        	if(printProgression) {
			        	System.out.println("Read out " + (read_nr + 1) + " of " + nReads +
				                " completed (sampling iteration " + iteration +
				                " of " + nIterations + ')');
		        	}
		        }
		    }
		}
		
		//Calculate average beta
    	for(int k = 0; k < nTopics; k++) {
    		for(int i = 0; i < nWords; i++) {
    			beta[k][i] /= ((double)nReads);
    		}
    	}
    		
    	//Calculate average theta
    	for(int d = 0; d < nDocuments; d++) {
    		for(int k = 0; k < nTopics; k++) {
    			theta[d][k] /= ((double)nReads);
    		}
    	}
    	
    	if(printProgression) {
    		System.out.println("LDA Gibbs sampling finished after " + 
    						  (System.currentTimeMillis() - startTime) + " milliseconds! \n");
    	}

	}
	
	/*
	 * Init topics at random
	 */
	private void init(){
		//For every document
		for(int d = 0; d < nDocuments; d++) {
			List<WordCountTuple> wctList = wordcount.get(d);
			int nWordsInDoc = wctList.size();
			ArrayList<ArrayList<Integer>> wordList = new ArrayList<ArrayList<Integer>>(nWordsInDoc);
			topics.add(d, wordList);
			
			//For every word in the document
			for(int i = 0; i < nWordsInDoc ; i++) {
				WordCountTuple wct = wctList.get(i);
				int nRepetitions = wct.getCount();
				int term = wct.getID();
				ArrayList<Integer> repetitions = new ArrayList<Integer>(nRepetitions);
				wordList.add(i, repetitions);
				
				//For every repetition of the word
				for(int j = 0; j < nRepetitions; j++) {
					//randimize initial topic
					int newTopic = (int) (nTopics*Math.random());
					repetitions.add(j, newTopic);
					
					// update counts
					topic_word[newTopic][term] += 1;
		            topic_cumul[newTopic] += 1;
		            document_topic[d][newTopic] += 1;
		            document_cumul[d] += 1;
				}
			}
		}
	}
	
	/**
	 * beta[nTopics][nWords]
	 * @return the estimate topic-word distribution
	 */
	public double[][] getBeta() {
		return beta;
	}

	/**
	 * theta[nDocuments][nTopics]
	 * @return the estimated document-topic distribution
	 */
	public double[][] getTheta() {
		return theta;
	}

	
}
