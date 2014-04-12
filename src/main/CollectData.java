package main;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import com.echonest.api.v4.EchoNestException;

import data.DataCollector;

public class CollectData {
	
	public static void main(String[] args){
    	System.setProperty("ECHO_NEST_API_KEY", "WT12J9ZGKIZLVZVSJ");
    	int nArtists = 10;
    	int nRelated = 5;
    	String delimitor = " --------------------------------------------- \n ";
    	String[] seeds = {"rolling stones", "deep purple", 				// 60-70's rock
    				      "NICKELBACK", "RISE AGAINST",					// modern rock
    				      "run dmc", "beastie boys",					// 80's hip-hop
    					  "Kanye west", "Eminem",						// modern hip-hop
    					  "depeche mode", "joy division"};				// 80's electro/punk
		
		DataCollector cd;
		double startTime = System.currentTimeMillis();
		try {
			cd = new DataCollector();
			cd.randomWalkBioData(seeds, nArtists, nRelated, delimitor);
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
