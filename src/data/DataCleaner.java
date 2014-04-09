package data;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DataCleaner {
	
	/**
	 * Deletes all occurrences of the words in stopword of the list data.
	 * 
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
	
	/**
	 * A first clean of the data before removing stop words.
	 * OBS!!! Call this before removing stop words. 
	 * Removes empty words
	 * and the following characters: 
	 * '.' 
	 * ','
	 * ':'
	 * ';'
	 * '('
	 * ')'
	 * ' '
	 * '\t'
	 * '\n'
	 * '\r'
	 * '\"';
	 * 
	 * @param text the list of text
	 * @return a list of the cleaned text
	 */
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

	/**
	 * A second clean of the data after removing the stop words. 
	 * OBS!!! Call this after removing the stop words.
	 * Removes occurrences of:
	 * '\''
	 * '´'
	 * '`'
	 * "'s"
	 * "´s"
	 * "`s"
	 * 
	 * @param text the text to clean.
	 * @return the cleaned text.
	 */
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
