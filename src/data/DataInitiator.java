package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class DataInitiator {
	
	private int nWords = 0;
	List<List<String>> data;
	List<List<WordCountTuple>> wordcount;
	HashMap<String, Integer> vocabulary;
	
	public DataInitiator() {
		data = importData();
		wordcount = calculateWordcount();
	}
	
	private List<List<String>> importData(){
		List<List<String>> artistData = new ArrayList<List<String>>();
		
		// Find all .artistcleaned.
		List<String> dataFiles = new ArrayList<String>();
		File[] files = new File("data").listFiles();
		for (File file : files) {
		    if (file.isFile()) {
		    	String fileName = file.getName();
		    	String fileEnd = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
		    	if(fileEnd.equals("artistcleaned")) {
		    		dataFiles.add(fileName);
		    	}
		    }
		}
		
		//Read all files
		for(String fileName : dataFiles) {
			ArrayList<String> artistBio = new ArrayList<String>();
			artistData.add(artistBio);
			try(BufferedReader br = new BufferedReader(new FileReader("data/" + fileName))) {
		        String line = br.readLine();
		        while (line != null) {
		        	artistBio.add(line);
		            line = br.readLine();
		        }
		    } catch(IOException e) {
		    	e.printStackTrace();
		    }
		}
		
		return artistData;
	}
	
	private List<List<WordCountTuple>> calculateWordcount(){
		vocabulary = new HashMap<String, Integer>();
		List<List<WordCountTuple>> wordcount = new ArrayList<List<WordCountTuple>>();
		int id = 0;
		for(List<String> artist : data) {
			//Count the words
			HashMap<String,Integer> counts = new HashMap<String,Integer>();
			
			
			for(String word : artist) {
				int count = 1;
				if(counts.containsKey(word)) {
					count = counts.get(word).intValue() + 1;
				}
				counts.put(word, new Integer(count));
				
				if(!vocabulary.containsKey(word)) {
					vocabulary.put(word,id);
					id++;
				}
				
			}
			
			List<WordCountTuple> artistCounts = new ArrayList<WordCountTuple>();
			wordcount.add(artistCounts);
			for(String word : counts.keySet()) {
				artistCounts.add(new WordCountTuple(word, vocabulary.get(word), counts.get(word)));
			}
			
		}
		this.nWords = vocabulary.keySet().size();
		return wordcount;
	}
	
	public HashMap<String, Integer> getVocabulary() {
		return vocabulary;
	}
	
	public List<List<WordCountTuple>> getWordcount() {
		return wordcount;
	}

	public int getnWords() {
		return nWords;
	}
	
	public List<List<String>> getData() {
		return data;
	}

}
