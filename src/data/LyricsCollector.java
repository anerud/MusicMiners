package data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.jmusixmatch.MusixMatch;
import org.jmusixmatch.MusixMatchException;
import org.jmusixmatch.entity.lyrics.Lyrics;
import org.jmusixmatch.entity.track.Track;
import org.jmusixmatch.entity.track.TrackData;

import com.google.gson.JsonSyntaxException;

import data.util.DataCleaner;

public class LyricsCollector {
	
	private MusixMatch musixMatch;
	private String outputFolder;
	private String inputFileName;
	private String split;
	
	public LyricsCollector(String inputFileName, String outputFolder, String split, String apiKey){
		musixMatch = new MusixMatch(apiKey);
		this.outputFolder = outputFolder;
		this.inputFileName = inputFileName;
		this.split = split;
	}
	
	public void collectLyrics(int startPos, int tryNumber) throws FileNotFoundException, IOException{
		
		try(BufferedReader br = new BufferedReader(new FileReader(inputFileName))) {
			String line = br.readLine();
			int lineNumber = 0;
			while(lineNumber < startPos && line != null) {
				line = br.readLine();
				lineNumber++;
			}
	        
	        while (line != null) {
	        	String[] songData = line.split(split);
	            String artistName = songData[0];
	            String songName = songData[1];

	            Track track = null;
				try {
					track = musixMatch.getMatchingTrack(songName, artistName);
					TrackData data = track.getTrack();
		            int trackID = data.getTrackId();
		            Lyrics lyrics = null;
		            try {
		            	lyrics = musixMatch.getLyrics(trackID);
		            } catch(JsonSyntaxException e) {
		            	collectLyrics(lineNumber + 1, 0);
		            	break;
		            }
		            PrintWriter pw = new PrintWriter(outputFolder + "/" + 
		            cleanString(artistName) + " - " + cleanString(songName) + ".lyric");
		            pw.print(lyrics.getLyricsBody());
		            pw.close();
		            System.out.println(cleanString(artistName) + " - " + 
		            		cleanString(songName) + ": " + lineNumber);
		            
		            line = br.readLine();
		            lineNumber++;
		            
				} catch (MusixMatchException e) {
					System.out.println("Exception... try again...");
					System.out.println(line + ", " + lineNumber);
					if(tryNumber < 3) {
						collectLyrics(lineNumber, tryNumber + 1);
						break;
					} else {
						collectLyrics(lineNumber + 1, 0);
						break;
					}
				}
				
	        }
	        
	    }
		
	}
	
	public static String cleanString(String s) {
		StringBuilder sb = new StringBuilder();
		for(char c : s.toCharArray()) {
			if(isCleanChar(c)) {
				sb.append(c);
			}
			
		}
		return sb.toString();
	}
	
	private static boolean isCleanChar(char c){
		return !( 
				 c == ',' ||
				 c == ':' ||
				 c == ';' ||
				 c == '|' ||
				 c == '%' ||
				 c == '[' ||
				 c == ']' ||
				 c == '}' ||
				 c == '{' ||
				 c == '*' ||
				 c == '$' ||
				 c == '!' ||
				 c == '?' ||
				 c == '#' ||
				 c == '¤' ||
				 c == '£' ||
				 c == '“' ||
				 c == '”' ||
				 c == '…' ||
				 c == '\"' ||
				 c == '\\' ||
				 c == '/' ||
				 c == '\t' ||
				 c == '\n' ||
				 c == '\r');
	}

}
