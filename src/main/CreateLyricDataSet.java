package main;

import data.LyricDataSetCreator;

public class CreateLyricDataSet {
	
	public static void main(String[] args){
		new LyricDataSetCreator("wordcount_lyrics.txt", "vocabulary_lyrics.txt");
		System.out.println("Data sets created!");
	}

}
