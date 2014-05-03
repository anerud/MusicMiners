package main;

import java.io.IOException;

import data.LyricsCollector;

public class CollectLyrics {

	public static void main(String[] args){
		
		String apiKey = "5110a75d5580ca408e0fea32cdbaf94f";
		String inputFile = "lyricsdata/songsinput.txt";
		String outputFolder = "lyricsdata";
		LyricsCollector lc = new LyricsCollector(inputFile, outputFolder, ";", apiKey);
		try {
			lc.collectLyrics(4503,0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
