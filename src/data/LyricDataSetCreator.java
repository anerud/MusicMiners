package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class LyricDataSetCreator {
	
	List<List<String>> data;
	List<List<WordCountTuple>> wordcount;
	HashMap<String, Integer> vocabulary;
	String dataSetName;
	String vocabularyName;
	
	public LyricDataSetCreator(String dataSetName, String vocabularyName) {
		this.dataSetName = dataSetName;
		this.vocabularyName = vocabularyName;
		data = importData();
		createVocabulary();
		createCounts();
	}
	
	private List<List<String>> importData(){
		List<List<String>> artistData = new ArrayList<List<String>>();
		
		// Find all .artistcleaned.
		StringBuilder sb = new StringBuilder();
		List<String> dataFiles = new ArrayList<String>();
		File[] files = new File("lyricsdata").listFiles();
		for (File file : files) {
		    if (file.isFile()) {
		    	String fileName = file.getName();
		    	String artistID = fileName.substring(0,fileName.lastIndexOf("."));
		    	String fileEnd = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
		    	if(fileEnd.equals("lyriccleaned")) {
		    		sb.append(artistID + "\n");
		    		dataFiles.add(fileName);
		    	}
		    }
		}
		
		try(PrintWriter pw = new PrintWriter("lyricsdata/document_order.txt")) {
			pw.print(sb);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//Read all files
		for(String fileName : dataFiles) {
			ArrayList<String> artistBio = new ArrayList<String>();
			artistData.add(artistBio);
			try(BufferedReader br = new BufferedReader(new FileReader("lyricsdata/" + fileName))) {
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
	
	private void createVocabulary(){
		StringBuilder sb = new StringBuilder();
		vocabulary = new HashMap<String, Integer>();
		HashSet<String> words = new HashSet<String>();
		
		for(List<String> artist : data) {
			for(String word : artist) {
				words.add(word);
			}
		}
		
		ArrayList<String> sortedWords = new ArrayList<String>(words);
		Collections.sort(sortedWords);
		
		for(int i = 0; i < sortedWords.size(); i++) {
			vocabulary.put(sortedWords.get(i), i);
			sb.append(sortedWords.get(i) + "\n");
		}
		
		try(PrintWriter pw = new PrintWriter("lyricsdata/" + vocabularyName)) {
			pw.print(sb);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void createCounts(){
		StringBuilder sb = new StringBuilder();
		for(List<String> artist : data) {
			//Count the words
			HashMap<String,Integer> counts = new HashMap<String,Integer>();
			
			for(String word : artist) {
				int count = 1;
				if(counts.containsKey(word)) {
					count = counts.get(word).intValue() + 1;
				}
				counts.put(word, new Integer(count));
			}
			
			for(String word : counts.keySet()) {
				int id = vocabulary.get(word);
				int count = counts.get(word);
				sb.append(id + ":" + count + " ");
			}
			sb.append("\n");
		}
		
		try(PrintWriter pw = new PrintWriter("lyricsdata/" + dataSetName)) {
			pw.print(sb);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
