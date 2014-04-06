package data;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import com.echonest.api.v4.Artist;
import com.echonest.api.v4.Biography;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;

public class DataCollector {
	
	private EchoNestAPI en;
    private static boolean trace = false;
    private HashSet<String> appearedArtists;

    public DataCollector() throws EchoNestException {
        en = new EchoNestAPI();
        en.setTraceSends(trace);
        en.setTraceRecvs(trace);
    }
    
    public DataCollector(String APIKey) throws EchoNestException {
        en = new EchoNestAPI(APIKey);
        en.setTraceSends(trace);
        en.setTraceRecvs(trace);
    }
	
    /**
     * Prints biography data of artists using the given PrintWriter.
     * Given a seed artist the algorithms makes a random walk among
     * the top nRelated related artists.
     * @param seedName The start artists.
     * @param nArtists How many artists to collect data from.
     * @param nRelated How many related artists to consider when
     * doing the random walk.
     * @param delimiter The delimiter to separate the artists.
     * @param pw The PrintWriter to print the data to.
     * @throws EchoNestException
     */
	public void randomWalkBioData(String seedName, int nArtists, int nRelated, 
			String delimiter, PrintWriter pw) throws EchoNestException {
		appearedArtists = new HashSet<String>(nArtists);
        List<Artist> artists = en.searchArtists(seedName);
        StringBuilder biographies;
        if (artists.size() > 0) {
            Artist seed = artists.get(0);
            for (int i = 0; i < nArtists; i++) {
            	//Print artist and bio data to file
            	biographies = new StringBuilder();
            	String artistName = seed.getName();
            	String artistID = seed.getID();
            	System.out.println("Artist " + (i + 1) + ": " + artistName);
            	pw.print(artistID + delimiter + artistName);
            	for(Biography b : seed.getBiographies()) {
            		biographies.append(b.getText() + " ");
            	}
            	pw.println(delimiter + biographies.toString());
            	
            	//Find new related artist which have not appeared yet.
            	List<Artist> sims = seed.getSimilar(nRelated);
            	if (sims.size() > 0) {
	            	Collections.shuffle(sims);
	            	seed = sims.get(0);
	            	int attempt = 1;
	            	while(hasAppeared(seed) && attempt < sims.size()) {
	            		seed = sims.get(attempt);
	            		attempt++;
	            	}
	            	if(attempt >= sims.size()) {
	            		seed = artists.get(0);
	            		i -= 1;
	            	} else {
	            		addArtist(seed);
	            	}
                } else {
                    break;
                }
            }
        }
    }

    private void addArtist(Artist a) {
    	appearedArtists.add(a.getID());
	}

	private boolean hasAppeared(Artist a) {
		return appearedArtists.contains(a.getID());
	}

}
