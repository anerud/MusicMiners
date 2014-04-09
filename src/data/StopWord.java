package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class StopWord {
	
	/**
	 * Imports and the data from all the .stop-files in the stopwords folder
	 * and returns it as a set.
	 * @return A set of stopwords.
	 */
	public static Set<String> getStopWords(){
		HashSet<String> stopWords = new HashSet<String>();
		List<String> stopLists = new ArrayList<String>();
		File[] files = new File("stopwords").listFiles();

		for (File file : files) {
		    if (file.isFile()) {
		    	String fileName = file.getName();
		    	String fileEnd = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
		    	if(fileEnd.equals("stop")) {
		    		stopLists.add(fileName);
		    	}
		    }
		}
		for(String stopList : stopLists) {
			try {
			    BufferedReader in = new BufferedReader(new FileReader("stopwords/" + stopList));
			    String stopWord;
			    while ((stopWord = in.readLine()) != null)
			        stopWords.add(stopWord.toLowerCase());
			    in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return stopWords;
	}

}
