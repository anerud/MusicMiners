package main;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import com.echonest.api.v4.EchoNestException;

import dataCollection.DataCollector;

public class CollectData {
	
	public static void main(String[] args){
    	System.setProperty("ECHO_NEST_API_KEY", "WT12J9ZGKIZLVZVSJ");
    	int nArtists = 3;
    	int nRelated = 10;
    	String delimitor = " --------------------------------------------- \n ";
    	String seed = "rolling stones";
    	
    	PrintWriter pw = null;
		try {
			pw = new PrintWriter("data/ArtistBiographies.dat");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			System.exit(0);
		}
		
		DataCollector cd;
		double startTime = System.currentTimeMillis();
		try {
			cd = new DataCollector();
			cd.randomWalkBioData(seed, nArtists, nRelated, delimitor ,pw);
			double seconds = (System.currentTimeMillis() - startTime)/1000;
			pw.close();
			System.out.println("Ended successfully after " + seconds + " seconds.");
		} catch (EchoNestException e) {
			pw.close();
			double seconds = (System.currentTimeMillis() - startTime)/1000;
			System.out.println("Ended with exception after " + seconds + " seconds.");
			e.printStackTrace();
		}
		
    }

}
