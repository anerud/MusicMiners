package main;

import java.util.Set;

import data.StopWord;

public class ImportStopWords {
	
	public static void main(String[] args){
		Set<String> sw = StopWord.getStopWords();
		for(String stopword : sw) {
			System.out.println(stopword);
		}
		System.out.println("Number of stop words: " + sw.size());
	}

}
