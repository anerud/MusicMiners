package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;


public class DataInitiator {
	
	List<List<WordCountTuple>> wordcount;
	ArrayList<String> vocabulary;
	ArrayList<String> documentOrder;
	HashMap<String,String> documentNames;
	
   	
	public DataInitiator(String dataSetName, String vocabularyName) {
		importData(dataSetName, vocabularyName);
	}
	
	private void importData(String dataSetName, String vocabularyName){
		
		//Read documents
		documentOrder = new ArrayList<String>();
		try(BufferedReader br = new BufferedReader(new FileReader("data/document_order.txt"))) {
	        String line = br.readLine();
	        while (line != null) {
	        	documentOrder.add(line);
	            line = br.readLine();
	        }
	    } catch(IOException e) {
	    	e.printStackTrace();
	    }
		
		//Read documents names
		documentNames = new HashMap<String,String>();
		try(BufferedReader br = new BufferedReader(new FileReader("data/artistNames.txt"))) {
	        String line = br.readLine();
	        while (line != null) {
	        	String[] split = line.split(",");
	        	documentNames.put(split[0], split[1]);
	            line = br.readLine();
	        }
	    } catch(IOException e) {
	    	e.printStackTrace();
	    }
		
		//Read vocabulary
		vocabulary = new ArrayList<String>();
		try(BufferedReader br = new BufferedReader(new FileReader("data/" + vocabularyName))) {
	        String line = br.readLine();
	        while (line != null) {
	        	vocabulary.add(line);
	            line = br.readLine();
	        }
	    } catch(IOException e) {
	    	e.printStackTrace();
	    }
		
		//Read counts
		wordcount = new ArrayList<List<WordCountTuple>>();
		try(BufferedReader br = new BufferedReader(new FileReader("data/" + dataSetName))) {
	        String line = br.readLine();
	        while (line != null) {
	        	ArrayList<WordCountTuple> doc = new ArrayList<WordCountTuple>();
	        	wordcount.add(doc);
	        	String[] wordCounts = line.split(" ");
	        	for(String wc : wordCounts) {
	        		String[] idCnt = wc.split(":");
	        		doc.add(new WordCountTuple(Integer.parseInt(idCnt[0]), Integer.parseInt(idCnt[1])));
	        	}
	            line = br.readLine();
	        }
	    } catch(IOException e) {
	    	e.printStackTrace();
	    }
		
	}
		
	public ArrayList<String> getVocabulary() {
		return vocabulary;
	}
	
	public ArrayList<String> getDocumentOrder() {
		return documentOrder;
	}
	
	public HashMap<String,String> getDocumentNames() {
		return documentNames;
	}
	
	public List<List<WordCountTuple>> getWordcount() {
		return wordcount;
	}

}
