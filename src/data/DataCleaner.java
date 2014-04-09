package data;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DataCleaner {
	
	/**
	 * Deletes all occurrences of the words in stopword of the list data.
	 * @param stopwords the words to delete.
	 * @param data the list of words to delete from.
	 */
	public static void removeStopWords(Set<String> stopwords, List<String> data) {
		Iterator<String> it = data.iterator();
		while(it.hasNext()) {
			if(stopwords.contains(it.next().toLowerCase())) {
				it.remove();
			}
		}
	}
	
	public static List<String> firstCleanOfText(List<String> text) {
		List<String> newTest = new LinkedList<String>();
		for(int i = 0; i < text.size(); i++) {
			String cs = cleanString(text.get(i));
			if(cs.length() > 0) {
	    		newTest.add(cs);
	    	}
		}
		return newTest;
	}
	
	private static String cleanString(String s) {
		StringBuilder sb = new StringBuilder();
		for(char c : s.toCharArray()) {
			if(isCleanChar(c)) {
				sb.append(c);
			}
			
		}
		return sb.toString();
	}
	
	private static boolean isCleanChar(char c){
		return !(c == '.' || 
				 c == ',' ||
				 c == ':' ||
				 c == ';' ||
				 c == '(' ||
				 c == ')' ||
				 c == ' ' ||
				 c == '\t' ||
				 c == '\n' ||
				 c == '\r' ||
				 c == '\"');
	}

	public static List<String> secondCleanOfText(List<String> text) {
		List<String> newTest = new LinkedList<String>();
		for(int i = 0; i < text.size(); i++) {
			String cs = removeApostropheFollowedByS(text.get(i));
			cs = removeApostrophes(cs);
			if(cs.length() > 0) {
	    		newTest.add(cs);
	    	}
		}
		return newTest;
	}
	
	private static String removeApostrophes(String s) {
		StringBuilder sb = new StringBuilder();
		for(char c : s.toCharArray()) {
			if(!(c == '\'' || c == '´' || c == '`')) {
				sb.append(c);
			}
			
		}
		return sb.toString();
	}

	private static String removeApostropheFollowedByS(String s) {
		if(s.endsWith("'s") || s.endsWith("´s") || s.endsWith("`s")) {
			String[] split = s.split("'s|´s|`s");
			return split[0];
		}
		return s;
	}

}
