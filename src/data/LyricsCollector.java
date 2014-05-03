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
	
	public void collectLyrics(int startPos) throws FileNotFoundException, IOException{
		
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
		            Lyrics lyrics = musixMatch.getLyrics(trackID);
		            PrintWriter pw = new PrintWriter(outputFolder + "/" + artistName + " - " + songName + ".lyric");
		            pw.print(lyrics.getLyricsBody());
		            System.out.println(lyrics.getLyricsBody());
		            pw.close();
		            
		            line = br.readLine();
		            lineNumber++;
		            
				} catch (Exception e) {
					System.out.println("Exception... try again...");
					System.out.println(line + ", " + lineNumber);
					collectLyrics(lineNumber);
				}
				
	        }
	        
	    }
		
	}

}
