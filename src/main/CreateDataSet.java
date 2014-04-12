package main;

import data.DataSetCreator;

public class CreateDataSet {
	
	public static void main(String[] args){
		new DataSetCreator("wordcount_artist.txt", "vocabulary_artist.txt");
		System.out.println("Data sets created!");
	}

}
