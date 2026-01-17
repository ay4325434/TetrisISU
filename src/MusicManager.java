/*
 * Manages all music files and audio snippets.
 */

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MusicManager {

    private Clip currentClip = null;
    private final Map<String, String> songPaths = new HashMap<>();
    private Thread loopThread;
    private String currentSong = "";
    private final Map<String, int[]> snippetRanges = new HashMap<>();
    private final Map<String, Map<Integer, Snippet>> snippetMap = new HashMap<>();

    public MusicManager() {
        // Menu and sidebar music
        songPaths.put("menu", "Re End of a Dream.wav");
        songPaths.put("credits", "Doppelganger.wav");
        songPaths.put("instructions", "Nulctrl EX.wav");
        songPaths.put("other", "Chronomia.wav");
        songPaths.put("scores", "Protoflicker.wav");
        songPaths.put("settings", "Calamity Fortune.wav");

        // Collection A (Music Collection 1)
        songPaths.put("collectionA1", "Music Collection 1/#1f1e33.wav");
        songPaths.put("collectionA2", "Music Collection 1/Crystallized.wav");
        songPaths.put("collectionA3", "Music Collection 1/Dance With Silence.wav");
        songPaths.put("collectionA4", "Music Collection 1/Exit This Earth's Atomosphere.wav");
        songPaths.put("collectionA5", "Music Collection 1/Ghost.wav");
        songPaths.put("collectionA6", "Music Collection 1/Ghoul.wav");
        songPaths.put("collectionA7", "Music Collection 1/Light it up.wav");
        songPaths.put("collectionA8", "Music Collection 1/Quaoar.wav");
        songPaths.put("collectionA9", "Music Collection 1/Terabyte Connection.wav");
        songPaths.put("collectionA10", "Music Collection 1/We Magicians Still Alive in 2021.wav");

        // Collection B (Music Collection 2)
        songPaths.put("collectionB1", "Music Collection 2/Marshmary.wav");
        songPaths.put("collectionB2", "Music Collection 2/Ascension to Heaven.wav");
        songPaths.put("collectionB3", "Music Collection 2/Blue Zenith.wav");
        songPaths.put("collectionB4", "Music Collection 2/Censored.wav");
        songPaths.put("collectionB5", "Music Collection 2/Everything Will Freeze.wav");
        songPaths.put("collectionB6", "Music Collection 2/Freedom Dive.wav");
        songPaths.put("collectionB7", "Music Collection 2/Galaxy Collapse.wav");
        songPaths.put("collectionB8", "Music Collection 2/Harumachi Clover.wav");
        songPaths.put("collectionB9", "Music Collection 2/Don't Fight The Music.wav");
        songPaths.put("collectionB10", "Music Collection 2/Synthesis.wav");

        // Collection C (Music Collection 3)
        songPaths.put("collectionC1", "Music Collection 3/Destruction 3,2,1.wav");
        songPaths.put("collectionC2", "Music Collection 3/Distorted Fate.wav");
        songPaths.put("collectionC3", "Music Collection 3/Igallta.wav");
        songPaths.put("collectionC4", "Music Collection 3/GOODWORLD.wav");
        songPaths.put("collectionC5", "Music Collection 3/GOODRAGE.wav");
        songPaths.put("collectionC6", "Music Collection 3/Rrhar'il.wav");
        songPaths.put("collectionC7", "Music Collection 3/Stardust Ray.wav");
        songPaths.put("collectionC8", "Music Collection 3/Water.wav");
        songPaths.put("collectionC9", "Music Collection 3/Wavetapper.wav");
        songPaths.put("collectionC10", "Music Collection 3/You are the Miserable.wav");

        // Collection D (Music Collection 4)
        songPaths.put("collectionD1", "Music Collection 4/Lost Memory.wav");
        songPaths.put("collectionD2", "Music Collection 4/Nhelv.wav");
        songPaths.put("collectionD3", "Music Collection 4/Pagoda.wav");
        songPaths.put("collectionD4", "Music Collection 4/Reanimate.wav");
        songPaths.put("collectionD5", "Music Collection 4/Shiawase - VIP.wav");
        songPaths.put("collectionD6", "Music Collection 4/Skystrike.wav");
        songPaths.put("collectionD7", "Music Collection 4/At the Speed of Light.wav");
        songPaths.put("collectionD8", "Music Collection 4/Realms.wav");
        songPaths.put("collectionD9", "Music Collection 4/Time Leaper.wav");
        songPaths.put("collectionD10", "Music Collection 4/Solar Wind.wav");

        // Collection E (Music Collection 5)
        songPaths.put("collectionE1", "Music Collection 5/Brain Fluid Explosion Girl.wav");
        songPaths.put("collectionE2", "Music Collection 5/Golden.wav");
        songPaths.put("collectionE3", "Music Collection 5/Idol.wav");
        songPaths.put("collectionE4", "Music Collection 5/Into the Night.wav");
        songPaths.put("collectionE5", "Music Collection 5/Kaikai Kitan.wav");
        songPaths.put("collectionE6", "Music Collection 5/Kyu-kurarin.wav");
        songPaths.put("collectionE7", "Music Collection 5/Machine Gun Poem Doll.wav");
        songPaths.put("collectionE8", "Music Collection 5/Night Dancer.wav");
        songPaths.put("collectionE9", "Music Collection 5/Otonoke.wav");
        songPaths.put("collectionE10", "Music Collection 5/Roki.wav");

        songPaths.put("collectionF1", "Music Collection 6/Aegleseeker.wav");
        songPaths.put("collectionF2", "Music Collection 6/BBKKBKK.wav");
        songPaths.put("collectionF3", "Music Collection 6/Brain Power.wav");
        songPaths.put("collectionF4", "Music Collection 6/Chou Night of Knights.wav");
        songPaths.put("collectionF5", "Music Collection 6/Grievous Lady.wav");
        songPaths.put("collectionF6", "Music Collection 6/Mesmerizer.wav");
        songPaths.put("collectionF7", "Music Collection 6/PANDORA PARADOXXX.wav");
        songPaths.put("collectionF8", "Music Collection 6/PUPA.wav");
        songPaths.put("collectionF9", "Music Collection 6/Sage.wav");
        songPaths.put("collectionF10", "Music Collection 6/the EmpErroR.wav");

        songPaths.put("collectionG1", "Music Collection 7/Angelic Jelly.wav");
        songPaths.put("collectionG2", "Music Collection 7/Chaos Time.wav");
        songPaths.put("collectionG3", "Music Collection 7/Cheatreal.wav");
        songPaths.put("collectionG4", "Music Collection 7/Chrome VOX.wav");
        songPaths.put("collectionG5", "Music Collection 7/Garakuta Doll Play.wav");
        songPaths.put("collectionG6", "Music Collection 7/Twisted Drop Party.wav");
        songPaths.put("collectionG7", "Music Collection 7/Oshama Scramble.wav");
        songPaths.put("collectionG8", "Music Collection 7/QZKago Requiem.wav");
        songPaths.put("collectionG9", "Music Collection 7/Tempestissimo.wav");
        songPaths.put("collectionG10", "Music Collection 7/TO THE COSMIC!!.wav");

        songPaths.put("collectionH1", "Music Collection 8/Bring it on.wav");
        songPaths.put("collectionH2", "Music Collection 8/Gimme x Gimme.wav");
        songPaths.put("collectionH3", "Music Collection 8/Hibana.wav");
        songPaths.put("collectionH4", "Music Collection 8/Lagtrain.wav");
        songPaths.put("collectionH5", "Music Collection 8/Luka Luka Night Fever.wav");
        songPaths.put("collectionH6", "Music Collection 8/Monitoring.wav");
        songPaths.put("collectionH7", "Music Collection 8/Override.wav");
        songPaths.put("collectionH8", "Music Collection 8/Senbonzakura.wav");
        songPaths.put("collectionH9", "Music Collection 8/Six Trillion Years and Overnight Story.wav");
        songPaths.put("collectionH10", "Music Collection 8/Tokyo Teddy Bear.wav");

        songPaths.put("collectionI1", "Music Collection 9/Climax.wav");
        songPaths.put("collectionI2", "Music Collection 9/Cthugha.wav");
        songPaths.put("collectionI3", "Music Collection 9/Cyaegha.wav");
        songPaths.put("collectionI4", "Music Collection 9/Dead or Die.wav");
        songPaths.put("collectionI5", "Music Collection 9/Invisible Frenzy.wav");
        songPaths.put("collectionI6", "Music Collection 9/IZANA.wav");
        songPaths.put("collectionI7", "Music Collection 9/Knight Rider.wav");
        songPaths.put("collectionI8", "Music Collection 9/OUTRAGE.wav");
        songPaths.put("collectionI9", "Music Collection 9/Red to Red.wav");
        songPaths.put("collectionI10", "Music Collection 9/Saikyo Stronger.wav");

        songPaths.put("collectionJ1", "Music Collection 10/4 Challenges.wav");
        songPaths.put("collectionJ2", "Music Collection 10/CYBERPUNK.wav");
        songPaths.put("collectionJ3", "Music Collection 10/Exitium.wav");
        songPaths.put("collectionJ4", "Music Collection 10/Hajimari Beat.wav");
        songPaths.put("collectionJ5", "Music Collection 10/Internet Yamero.wav");
        songPaths.put("collectionJ6", "Music Collection 10/Let you DIVE.wav");
        songPaths.put("collectionJ7", "Music Collection 10/Lyrical Strike.wav");
        songPaths.put("collectionJ8", "Music Collection 10/Mirror.wav");
        songPaths.put("collectionJ9", "Music Collection 10/New Year's Entropy.wav");
        songPaths.put("collectionJ10", "Music Collection 10/round and round.wav");

        songPaths.put("collectionK1", "Music Collection 11/And Revive The Melody.wav");
        songPaths.put("collectionK2", "Music Collection 11/Arcana Eden.wav");
        songPaths.put("collectionK3", "Music Collection 11/Buchigire Berserker.wav");
        songPaths.put("collectionK4", "Music Collection 11/Chronostasis.wav");
        songPaths.put("collectionK5", "Music Collection 11/Cyberfantasia.wav");
        songPaths.put("collectionK6", "Music Collection 11/Highscore.wav");
        songPaths.put("collectionK7", "Music Collection 11/Infinity Heaven.wav");
        songPaths.put("collectionK8", "Music Collection 11/Mobius.wav");
        songPaths.put("collectionK9", "Music Collection 11/Pragmatism Resurrection.wav");
        songPaths.put("collectionK10", "Music Collection 11/White Aura.wav");

        // Collection A snippets
        snippetRanges.put("collectionA1", new int[]{40, 65});
        snippetRanges.put("collectionA2", new int[]{80, 110});
        snippetRanges.put("collectionA3", new int[]{40, 70});
        snippetRanges.put("collectionA4", new int[]{238, 268});
        snippetRanges.put("collectionA5", new int[]{191, 220});
        snippetRanges.put("collectionA6", new int[]{60, 90});
        snippetRanges.put("collectionA7", new int[]{150, 180});
        snippetRanges.put("collectionA8", new int[]{195, 225});
        snippetRanges.put("collectionA9", new int[]{245, 275});
        snippetRanges.put("collectionA10", new int[]{90, 120});

        // Collection B
        snippetRanges.put("collectionB1", new int[]{30, 60});
        snippetRanges.put("collectionB2", new int[]{110, 140});
        snippetRanges.put("collectionB3", new int[]{105, 135});
        snippetRanges.put("collectionB4", new int[]{30, 60});
        snippetRanges.put("collectionB5", new int[]{90, 120});
        snippetRanges.put("collectionB6", new int[]{90, 120});
        snippetRanges.put("collectionB7", new int[]{165, 195});
        snippetRanges.put("collectionB8", new int[]{90, 120});
        snippetRanges.put("collectionB9", new int[]{90, 120});
        snippetRanges.put("collectionB10", new int[]{60, 90});

        // Collection C
        snippetRanges.put("collectionC1", new int[]{90, 120});
        snippetRanges.put("collectionC2", new int[]{20, 50});
        snippetRanges.put("collectionC3", new int[]{50, 80});
        snippetRanges.put("collectionC4", new int[]{50, 80});
        snippetRanges.put("collectionC5", new int[]{85, 115});
        snippetRanges.put("collectionC6", new int[]{105, 135});
        snippetRanges.put("collectionC7", new int[]{105, 135});
        snippetRanges.put("collectionC8", new int[]{130, 160});
        snippetRanges.put("collectionC9", new int[]{110, 140});
        snippetRanges.put("collectionC10", new int[]{200, 230});

        // Collection D
        snippetRanges.put("collectionD1", new int[]{80, 120});
        snippetRanges.put("collectionD2", new int[]{90, 120});
        snippetRanges.put("collectionD3", new int[]{90, 120});
        snippetRanges.put("collectionD4", new int[]{75, 105});
        snippetRanges.put("collectionD5", new int[]{90, 120});
        snippetRanges.put("collectionD6", new int[]{145, 175});
        snippetRanges.put("collectionD7", new int[]{110, 140});
        snippetRanges.put("collectionD8", new int[]{90, 120});
        snippetRanges.put("collectionD9", new int[]{80, 110});
        snippetRanges.put("collectionD10", new int[]{30, 60});

        // Collection E
        snippetRanges.put("collectionE1", new int[]{40, 70});
        snippetRanges.put("collectionE2", new int[]{60, 90});
        snippetRanges.put("collectionE3", new int[]{55, 85});
        snippetRanges.put("collectionE4", new int[]{200, 230});
        snippetRanges.put("collectionE5", new int[]{45, 75});
        snippetRanges.put("collectionE6", new int[]{60, 90});
        snippetRanges.put("collectionE7", new int[]{90, 120});
        snippetRanges.put("collectionE8", new int[]{90, 120});
        snippetRanges.put("collectionE9", new int[]{90, 120});
        snippetRanges.put("collectionE10", new int[]{60, 90});

        // Collection F
        snippetRanges.put("collectionF1", new int[]{90, 120});
        snippetRanges.put("collectionF2", new int[]{20, 50});
        snippetRanges.put("collectionF3", new int[]{100, 130});
        snippetRanges.put("collectionF4", new int[]{30, 60});
        snippetRanges.put("collectionF5", new int[]{90, 120});
        snippetRanges.put("collectionF6", new int[]{60, 90});
        snippetRanges.put("collectionF7", new int[]{60, 90});
        snippetRanges.put("collectionF8", new int[]{60, 90});
        snippetRanges.put("collectionF9", new int[]{75, 105});
        snippetRanges.put("collectionF10", new int[]{70, 100});

        // Collection G
        snippetRanges.put("collectionG1", new int[]{60, 90});
        snippetRanges.put("collectionG2", new int[]{90, 120});
        snippetRanges.put("collectionG3", new int[]{90, 120});
        snippetRanges.put("collectionG4", new int[]{110, 140});
        snippetRanges.put("collectionG5", new int[]{90, 120});
        snippetRanges.put("collectionG6", new int[]{90, 120});
        snippetRanges.put("collectionG7", new int[]{45, 75});
        snippetRanges.put("collectionG8", new int[]{30, 60});
        snippetRanges.put("collectionG9", new int[]{90, 120});
        snippetRanges.put("collectionG10", new int[]{90, 120});

        // Collection H
        snippetRanges.put("collectionH1", new int[]{60, 90});
        snippetRanges.put("collectionH2", new int[]{60, 90});
        snippetRanges.put("collectionH3", new int[]{60, 90});
        snippetRanges.put("collectionH4", new int[]{60, 90});
        snippetRanges.put("collectionH5", new int[]{60, 90});
        snippetRanges.put("collectionH6", new int[]{60, 90});
        snippetRanges.put("collectionH7", new int[]{30, 60});
        snippetRanges.put("collectionH8", new int[]{60, 90});
        snippetRanges.put("collectionH9", new int[]{50, 80});
        snippetRanges.put("collectionH10", new int[]{40, 70});

        // Collection I
        snippetRanges.put("collectionI1", new int[]{60, 90});
        snippetRanges.put("collectionI2", new int[]{90, 120});
        snippetRanges.put("collectionI3", new int[]{60, 90});
        snippetRanges.put("collectionI4", new int[]{60, 90});
        snippetRanges.put("collectionI5", new int[]{60, 90});
        snippetRanges.put("collectionI6", new int[]{60, 90});
        snippetRanges.put("collectionI7", new int[]{40, 70});
        snippetRanges.put("collectionI8", new int[]{60, 90});
        snippetRanges.put("collectionI9", new int[]{60, 90});
        snippetRanges.put("collectionI10", new int[]{90, 120});

        snippetRanges.put("collectionJ1", new int[]{60, 90});
        snippetRanges.put("collectionJ2", new int[]{30, 60});
        snippetRanges.put("collectionJ3", new int[]{60, 90});
        snippetRanges.put("collectionJ4", new int[]{60, 90});
        snippetRanges.put("collectionJ5", new int[]{120, 150});
        snippetRanges.put("collectionJ6", new int[]{60, 90});
        snippetRanges.put("collectionJ7", new int[]{80, 110});
        snippetRanges.put("collectionJ8", new int[]{60, 90});
        snippetRanges.put("collectionJ9", new int[]{60, 90});
        snippetRanges.put("collectionJ10", new int[]{80, 110});

        snippetRanges.put("collectionK1", new int[]{60, 90});
        snippetRanges.put("collectionK2", new int[]{60, 90});
        snippetRanges.put("collectionK3", new int[]{60, 90});
        snippetRanges.put("collectionK4", new int[]{90, 120});
        snippetRanges.put("collectionK5", new int[]{60, 90});
        snippetRanges.put("collectionK6", new int[]{40, 70});
        snippetRanges.put("collectionK7", new int[]{100, 130});
        snippetRanges.put("collectionK8", new int[]{60, 90});
        snippetRanges.put("collectionK9", new int[]{90, 120});
        snippetRanges.put("collectionK10", new int[]{25, 55});

        initSnippets(); // load the snippets
    }

    // Stop any music
    public void stop() {
        stopLoop();
    }

    public void stopLoop() {
        if (loopThread != null) {
            loopThread.interrupt();
            loopThread = null;
        }
        if (currentClip != null) {
            currentClip.stop();
            currentClip.close();
            currentClip = null;
        }
        currentSong = "";
    }

    // Full song loop
    public void loop(String songId) throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        if (songId.equals(currentSong)) return;
        stop();
        String path = songPaths.get(songId);
        if (path == null) return;

        AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(path));
        currentClip = AudioSystem.getClip();
        currentClip.open(audioStream);
        currentClip.loop(Clip.LOOP_CONTINUOUSLY);
        currentSong = songId;
    }

    // --------------------
    // Loop snippet with start/stop in seconds + gap
    // --------------------
    public void loopSnippet(String songId, double start, double stop, int gapMs) throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        if (songId.equals(currentSong)) return;
        stopLoop();

        String path = songPaths.get(songId);
        if (path == null) throw new IllegalArgumentException("Song ID not found: " + songId);

        AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(path));
        currentClip = AudioSystem.getClip();
        currentClip.open(audioStream);

        AudioFormat format = currentClip.getFormat();
        float frameRate = format.getFrameRate();

        int startFrame = (int) (start * frameRate);
        int stopFrame = (int) (stop * frameRate);

        long snippetDurationMs = (long) ((stopFrame - startFrame) / frameRate * 1000);

        loopThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    System.out.println("Song start=" + start + " stop=" + stop);
                    System.out.println(snippetDurationMs + " " + gapMs);
                    currentClip.setFramePosition(startFrame);
                    currentClip.start();
                    Thread.sleep(snippetDurationMs);
                    currentClip.stop();
                    Thread.sleep(gapMs);
                }
            } catch (InterruptedException e) {
                currentClip.stop();
            }
        });
        loopThread.setDaemon(true);
        loopThread.start();
        currentSong = songId;
    }
    public int pause() {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop();
            return currentClip.getFramePosition();
        }
        return -1;
    }
    public void resume(int framePosition) {
        if (currentClip != null) {
            currentClip.setFramePosition(framePosition);
            currentClip.start();
        }
    }

    private void initSnippets() {
        // Collection A
        Map<Integer, Snippet> collectionA = new HashMap<>();
        for(int i = 1; i <= 10; i++){
            collectionA.put(i, new Snippet(snippetRanges.get("collectionA" + i)[0], snippetRanges.get("collectionA" + i)[1], 1500));
        }
        snippetMap.put("A", collectionA);

        // Collection B
        Map<Integer, Snippet> collectionB = new HashMap<>();
        for(int i = 1; i <= 10; i++){
            collectionB.put(i, new Snippet(snippetRanges.get("collectionB" + i)[0], snippetRanges.get("collectionB" + i)[1], 1500));
        }
        snippetMap.put("B", collectionB);

        // Collection C
        Map<Integer, Snippet> collectionC = new HashMap<>();
        for(int i = 1; i <= 10; i++){
            collectionC.put(i, new Snippet(snippetRanges.get("collectionC" + i)[0], snippetRanges.get("collectionC" + i)[1], 1500));
        }
        snippetMap.put("C", collectionC);

        // Collection D
        Map<Integer, Snippet> collectionD = new HashMap<>();
        for(int i = 1; i <= 10; i++){
            collectionD.put(i, new Snippet(snippetRanges.get("collectionD" + i)[0], snippetRanges.get("collectionD" + i)[1], 1500));
        }
        snippetMap.put("D", collectionD);

        // Collection E
        Map<Integer, Snippet> collectionE = new HashMap<>();
        for(int i = 1; i <= 10; i++){
            collectionE.put(i, new Snippet(snippetRanges.get("collectionE" + i)[0], snippetRanges.get("collectionE" + i)[1], 1500));
        }
        snippetMap.put("E", collectionE);

        // Collection F
        Map<Integer, Snippet> collectionF = new HashMap<>();
        for(int i = 1; i <= 10; i++){
            collectionF.put(i, new Snippet(snippetRanges.get("collectionF" + i)[0], snippetRanges.get("collectionF" + i)[1], 1500));
        }
        snippetMap.put("F", collectionF);

        // Collection G
        Map<Integer, Snippet> collectionG = new HashMap<>();
        for(int i = 1; i <= 10; i++){
            collectionG.put(i, new Snippet(snippetRanges.get("collectionG" + i)[0], snippetRanges.get("collectionG" + i)[1], 1500));
        }
        snippetMap.put("G", collectionG);

        // Collection H
        Map<Integer, Snippet> collectionH = new HashMap<>();
        for(int i = 1; i <= 10; i++){
            collectionH.put(i, new Snippet(snippetRanges.get("collectionH" + i)[0], snippetRanges.get("collectionH" + i)[1], 1500));
        }
        snippetMap.put("H", collectionH);

        // Collection I
        Map<Integer, Snippet> collectionI = new HashMap<>();
        for(int i = 1; i <= 10; i++){
            collectionI.put(i, new Snippet(snippetRanges.get("collectionI" + i)[0], snippetRanges.get("collectionI" + i)[1], 1500));
        }
        snippetMap.put("I", collectionI);
        Map<Integer, Snippet> collectionJ = new HashMap<>();
        for(int i = 1; i <= 10; i++){
            collectionJ.put(i, new Snippet(snippetRanges.get("collectionJ" + i)[0], snippetRanges.get("collectionJ" + i)[1], 1500));
        }
        snippetMap.put("J", collectionJ);

        // Collection K
        Map<Integer, Snippet> collectionK = new HashMap<>();
        for(int i = 1; i <= 10; i++) {
            collectionK.put(i, new Snippet(snippetRanges.get("collectionK" + i)[0], snippetRanges.get("collectionK" + i)[1], 1500));
        }
        snippetMap.put("K", collectionK);
    }

    /**
     * Loads the snippet for a given song.
     * @param collectionKey The music collection
     * @param songNumber The nth song in the collection
     * @return The snippet of the song
     */
    public Snippet getSnippet(String collectionKey, int songNumber) {
        Map<Integer, Snippet> collection = snippetMap.get(collectionKey);
        if (collection != null) {
            return collection.get(songNumber);
        }
        return null;
    }
    public String getCurrentSong() {
        return currentSong;
    }
}
