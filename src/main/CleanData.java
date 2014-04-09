package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import data.DataCleaner;
import data.StopWord;

public class CleanData {
	
	public static void main(String[] args) throws FileNotFoundException, IOException{
		Set<String> sw = StopWord.getStopWords();
		List<String> dataFiles = new ArrayList<String>();
		File[] files = new File("data").listFiles();
		
		// Detect what files to clean.
		for (File file : files) {
		    if (file.isFile()) {
		    	String fileName = file.getName();
		    	String fileEnd = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
		    	if(fileEnd.equals("artist")) {
		    		dataFiles.add(fileName);
		    	}
		    }
		}
		
		// Clean all files and save them to new file 'fileName.artistcleaned'.
		for(String fileName : dataFiles) {
			List<String> cleanedBio = new LinkedList<String>();
			try(BufferedReader br = new BufferedReader(new FileReader("data/" + fileName))) {
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
			System.out.println(cleanedBio.size());
			
			//Print to new file
			PrintWriter pw = new PrintWriter("data/" + fileName + "cleaned");
			for(String s : cleanedBio) {
				pw.println(s);
			}
			pw.close();
		}
	}

}
