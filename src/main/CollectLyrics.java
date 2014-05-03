package main;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.jmusixmatch.MusixMatch;
import org.jmusixmatch.MusixMatchException;
import org.jmusixmatch.entity.lyrics.Lyrics;
import org.jmusixmatch.entity.track.Track;
import org.jmusixmatch.entity.track.TrackData;

import data.LyricsCollector;

public class CollectLyrics {

	public static void main(String[] args){
		
		String apiKey = "e4f4cc6d99b091dfb401c8eff2a2e6a9";
		String inputFile = "lyricsdata/songsinput.txt";
		String outputFolder = "lyricsdata";
		LyricsCollector lc = new LyricsCollector(inputFile, outputFolder, ";", apiKey);
		try {
			lc.collectLyrics(0,0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
