package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import data.util.DataCleaner;
import data.util.StopWord;

public class CleanLyricsData {
	
	public static void main(String[] args) throws FileNotFoundException, IOException{
		Set<String> sw = StopWord.getStopWords();
		List<String> dataFiles = new ArrayList<String>();
		File[] files = new File("lyricsdata").listFiles();
		
		// Detect what files to clean.
		for (File file : files) {
		    if (file.isFile()) {
		    	String fileName = file.getName();
		    	String fileEnd = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
		    	if(fileEnd.equals("lyric")) {
		    		dataFiles.add(fileName);
		    	}
		    }
		}
		
		// Clean all files and save them to new file 'fileName.artistcleaned'.
		for(String fileName : dataFiles) {
			List<String> cleanedBio = new LinkedList<String>();
			try(BufferedReader br = new BufferedReader(new FileReader("lyricsdata/" + fileName))) {
		        StringBuilder sb = new StringBuilder();
		        String line = br.readLine();
		        while (line != null) {
		            sb.append(line);
		            sb.append(System.lineSeparator());
		            line = br.readLine();
		        }
		        String bio = sb.toString();
		        
		        //Separate all words
		        String[] words = bio.split(" |\t|\n"); 
		        for(String s : words) {
	        		cleanedBio.add(s);
		        }
		    }
			
			//Clean the bio and remove stop words.
			cleanedBio = DataCleaner.firstCleanOfText(cleanedBio);
			DataCleaner.removeStopWords(sw, cleanedBio);
			cleanedBio = DataCleaner.secondCleanOfText(cleanedBio);
			
			//Print to new file
			PrintWriter pw = new PrintWriter("lyricsdata/" + fileName + "cleaned");
			for(String s : cleanedBio) {
				pw.println(s.toLowerCase());
			}
			pw.close();
		}
		System.out.println("Data is now cleaned!");
	}

}
