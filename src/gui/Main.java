package gui;

import data.LyricsCollector;
import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Roland on 2014-05-03.
 */
public class Main extends JFrame{

    private JTextArea jta;
    private JTextArea jta2;
    private JTextArea jta3;

    private Set<String> songsSet = new HashSet<>();

    public static void main(String[] args) throws IOException {
        new Main();
    }

    public Main() throws IOException {
        initSongs();

        final JFrame fr = new JFrame("Music Miners");
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fr.setLocation(300, 300);
        fr.setSize(new Dimension(800,800));
        ImageIcon icon = new ImageIcon("");
        fr.setIconImage(ImageIO.read(new File("resources/lemming.jpg")));
        fr.setVisible(true);

        JPanel panel = new JPanel(new MigLayout("debug, fill", "", "[growprio 0][fill, growprio 200][growprio 150]"));
        final JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(1000, 25));
        final ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                search for things...
                SwingWorker<String, String> w = new SwingWorker() {
                    @Override
                    protected String doInBackground() throws Exception {
                        Set<String> searchTerms = new HashSet<>(Arrays.asList(field.getText().split(" ")));
                        return searchForTerms(searchTerms);
                    }

                    @Override
                    protected void done() {
                        try {
                            String result = (String)get();
                            StringBuilder b = new StringBuilder();

                            if(result == null){
                                return;
                            }
                            String[] songData = result.split(";");
                            String artistName = songData[0];
                            String songName = songData[1];

                            String lyricFileName = LyricsCollector.cleanString(artistName) + " - " + LyricsCollector.cleanString(songName);
                            b.append(lyricFileName + "\n\n");
                            String IOFileName = "lyricsdata/" + lyricFileName + ".lyric";
                            BufferedReader br = null;
                            try {
                                br = new BufferedReader(new FileReader(IOFileName));

                            while(br.ready()){
                                b.append(br.readLine() + "\n");
                            }
                            br.close();
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }

                            jta.setText(b.toString());

//                            _________________________________
                            StringBuilder b2 = new StringBuilder();
                            String IOFileName2 = "lyricsdata/" + lyricFileName + ".stats";
                            BufferedReader br2 = null;
                            try {
                                br2 = new BufferedReader(new FileReader(IOFileName2));

                                while(br2.ready()){
                                    b2.append(br2.readLine() + "\n");
                                }
                                br2.close();
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }

                            jta2.setText(b2.toString());

//                            _________________________________
                            StringBuilder b3 = new StringBuilder();
                            String IOFileName3 = "lyricsdata/" + lyricFileName + ".relatedsongs";
                            BufferedReader br3 = null;
                            try {
                                br3 = new BufferedReader(new FileReader(IOFileName3));

                                while(br3.ready()){
                                    b3.append(br3.readLine() + "\n");
                                }
                                br3.close();
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }

                            jta3.setText(b3.toString());


                            field.setText("");
                            field.requestFocus();
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        } catch (ExecutionException e1) {
                            e1.printStackTrace();
                        }
                        fr.pack();
                    }

                };
                w.execute();
            }
        };
        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(KeyEvent.getExtendedKeyCodeForChar(e.getKeyChar()) == KeyEvent.VK_ENTER){
                    listener.actionPerformed(new ActionEvent(this, KeyEvent.KEY_TYPED, "herp"));
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        };

        field.addKeyListener(keyListener);
        panel.add(field, "growx, span 3");
        field.requestFocus();

        JButton searchButton = new JButton("Search");
        panel.add(searchButton, "wrap");
        searchButton.addActionListener(listener);
        searchButton.addKeyListener(keyListener);

        jta = new JTextArea("lyrics here");
        jta.setEditable(false);
        jta.setOpaque(false);
        JScrollPane sp = new JScrollPane(jta);
        panel.add(sp, "grow");
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        jta2 = new JTextArea("stats here");
        jta2.setEditable(false);
        jta2.setOpaque(false);
        JScrollPane sp2 = new JScrollPane(jta2);
        panel.add(sp2, "grow");
        sp2.setPreferredSize(new Dimension(400, 800));
        sp2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        jta3 = new JTextArea("stats here");
        jta3.setEditable(false);
        jta3.setOpaque(false);
        JScrollPane sp3 = new JScrollPane(jta3);
        panel.add(sp3, "span 2, grow");
        sp3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);


        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Lyrics", panel);
        fr.add(tabs);
        fr.pack();
        field.requestFocus();
    }

    private String searchForTerms(Set<String> searchTerms) {

        final HashMap<String,Integer> count = new HashMap<>();
        PriorityQueue<String> pq = new PriorityQueue<>(100, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int count1 = count.get(o1);
                int count2 = count.get(o2);
                return count2 - count1;
            }
        });
        for(String s : songsSet){
            for(String term : searchTerms){
                String trimmed = s.replaceAll(";", " ");
                trimmed = trimmed.toLowerCase();
                String[] split = trimmed.split(" ");
                for(String st : split){
                    if(st.contains(term.toLowerCase())){
                        if(count.get(s) == null){
                            count.put(s, 2);
                            pq.add(s);
                        } else {
                            count.put(s, count.get(s) + 2);
                            pq.remove(s);
                            pq.add(s);
                        }
                    }
                }
            }
        }
        return pq.poll();
    }

    private void initSongs() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("lyricsdata/songsinput.txt"));

        while(br.ready()){
            String song = br.readLine();
            if(song != ""){
                songsSet.add(song);
            }
        }
        br.close();
    }
}
