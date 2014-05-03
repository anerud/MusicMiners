package data;

import java.io.*;
import java.util.*;

import com.echonest.api.v4.*;

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
     * @param seedNames The start artists.
     * @param nArtists How many artists to collect data from.
     * @param nRelated How many related artists to consider when
     * doing the random walk.
     * @param delimiter The delimiter to separate the artists.
     * @throws EchoNestException
     * @throws FileNotFoundException 
     */
	public void randomWalkBioData(String[] seedNames, int nArtists, int nRelated, 
			String delimiter) throws EchoNestException, FileNotFoundException {
		PrintWriter pw_names = new PrintWriter("data/artistNames.txt");
		for(String seedName : seedNames) {
			appearedArtists = new HashSet<String>(nArtists);
	        List<Artist> artists = en.searchArtists(seedName);
	        StringBuilder biographies;
	        if (artists.size() > 0) {
	            Artist seed = artists.get(0);
	            for (int i = 0; i < nArtists; i++) {
	            	//Print artist and bio data to file
	            	if(!hasAppeared(seed)) {
		            	addArtist(seed);
		            	biographies = new StringBuilder();
		            	String artistName = seed.getName();
		            	String artistID = seed.getID();
		            	System.out.println("Artist " + (i + 1) + ": " + artistName);
		            	for(Biography b : seed.getBiographies()) {
		            		biographies.append(b.getText() + " ");
		            	}
		            	PrintWriter pw = new PrintWriter("data/" + artistID + ".artist");
		            	pw.println(biographies.toString());
		            	pw.close();
		            	pw_names.println(artistID + "," + artistName);
	            	} else {
	            		i--;
	            	}
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
		            	} 
	                } else {
	                	seed = artists.get(0);
	                }
	            }
	        }
		}
		pw_names.close();
    }

    /**
     * Selects one song from an artist at the time..
     * @param seedNames
     * @param nArtists
     * @param nRelated
     * @param delimiter
     * @throws EchoNestException
     * @throws FileNotFoundException
     */
    public void randomWalkSongData(String[] seedNames, int nArtists, int nRelated) 
    			throws EchoNestException, IOException {
    	String IOFileName = "lyricsdata/songsinput.txt";
        Set<String> songsSet = new HashSet<>();
        BufferedReader br = new BufferedReader(new FileReader(IOFileName));

        while(br.ready()){
            String song = br.readLine();
            if(song != ""){
                songsSet.add(song);
            }
        }
        br.close();

        for(String seedName : seedNames) {
            appearedArtists = new HashSet<String>(nArtists);
            List<Artist> artists = en.searchArtists(seedName);
            if (artists.size() > 0) {
                Artist seed = artists.get(0);
                for (int i = 0; i < nArtists; i++) {
                    //Print artist and bio data to file
                    if(!hasAppeared(seed)) {
                        addArtist(seed);
                        String artistName = seed.getName();
                        System.out.println("Artist " + (i + 1) + ": " + artistName);
                        for(Song s : seed.getSongs()) {
                            String songStr = s.getArtistName() + ";" + s.getTitle();
                            songsSet.add(songStr);
                        }
                    } else {
                        i--;
                    }
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
                        }
                    } else {
                        seed = artists.get(0);
                    }
                }
            }
        }
        PrintWriter pw_songs = new PrintWriter(IOFileName);
        for(String s : songsSet){
            pw_songs.println(s);
        }
        pw_songs.close();
    }

    private void addArtist(Artist a) {
    	appearedArtists.add(a.getID());
	}

	private boolean hasAppeared(Artist a) {
		return appearedArtists.contains(a.getID());
	}

}
