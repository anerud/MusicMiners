package main;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import com.echonest.api.v4.EchoNestException;

import data.DataCollector;

public class CollectData {
	
	public static void main(String[] args){
    	System.setProperty("ECHO_NEST_API_KEY", "WT12J9ZGKIZLVZVSJ");
    	int nArtists = 3;
    	int nRelated = 10;
    	String delimitor = " --------------------------------------------- \n ";
    	String seed = "rolling stones";
		
		DataCollector cd;
		double startTime = System.currentTimeMillis();
		try {
			cd = new DataCollector();
			cd.randomWalkBioData(seed, nArtists, nRelated, delimitor);
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
