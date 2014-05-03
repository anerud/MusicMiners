package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import com.echonest.api.v4.EchoNestException;

import data.DataCollector;

public class CollectData {
	
	public static void main(String[] args) throws IOException {
    	System.setProperty("ECHO_NEST_API_KEY", "WT12J9ZGKIZLVZVSJ");
    	int nArtists = 50;
    	int nRelated = 15;
    	String[] seeds = {"rolling stones", "deep purple", 				// 60-70's rock
    				      "NICKELBACK", "RISE AGAINST",					// modern rock
    				      "run dmc", "beastie boys",					// 80's hip-hop
    					  "Kanye west", "Eminem",						// modern hip-hop
    					  "depeche mode", "joy division",				// 80's electro/punk
    					  "britney spears", "aqua",						// 90/2000 pop
    					  "slipknot", "opeth",							// 90/2000 Death metal
    					  "lumineers", "mumford and sons",				// new popular music
    					  "justin bieber", "nicki minaj",				// GOOD SHIT!!!!
    					  "rihanna", "beyonce"};				        // more fantastic music...
		
		DataCollector cd;
		double startTime = System.currentTimeMillis();
		try {
			cd = new DataCollector();
//			cd.randomWalkBioData(seeds, nArtists, nRelated, delimitor);
            cd.randomWalkSongData(seeds, nArtists, nRelated);
			double seconds = (System.currentTimeMillis() - startTime)/1000;
			System.out.println("Ended successfully after " + seconds + " seconds.");
		} catch (EchoNestException e) {
			double seconds = (System.currentTimeMillis() - startTime)/1000;
			System.out.println("Ended with exception after " + seconds + " seconds.");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
    }

}
