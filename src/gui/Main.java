package gui;

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

    private Set<String> songsSet = new HashSet<>();

    public static void main(String[] args) throws IOException {
        new Main();
    }

    public Main() throws IOException {
        initSongs();

        JFrame fr = new JFrame("Music Miners");
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fr.setLocation(300, 300);
        fr.setSize(new Dimension(800,800));
        ImageIcon icon = new ImageIcon("");
        fr.setIconImage(ImageIO.read(new File("resources/lemming.jpg")));
        fr.setVisible(true);

        JPanel panel = new JPanel(new MigLayout("debug, fill", "", "[growprio 0][fill, growprio 200]"));
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
                            jta.setText(result);
                            field.setText("");
                            field.requestFocus();
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        } catch (ExecutionException e1) {
                            e1.printStackTrace();
                        }

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
        panel.add(field, "growx, span 2");
        field.requestFocus();

        JButton searchButton = new JButton("Search");
        panel.add(searchButton, "wrap");
        searchButton.addActionListener(listener);
        searchButton.addKeyListener(keyListener);

        jta = new JTextArea("lyrics here");
        jta.setEditable(false);
        jta.setOpaque(false);
        panel.add(jta, "grow");

        JTextArea jta2 = new JTextArea("stats here");
        jta2.setEditable(false);
        jta2.setOpaque(false);
        panel.add(jta2, "grow");

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
        BufferedReader br = new BufferedReader(new FileReader("data/songs.txt"));

        while(br.ready()){
            String song = br.readLine();
            if(song != ""){
                songsSet.add(song);
            }
        }
        br.close();
    }
}
