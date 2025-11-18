import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MusicManager {

    public Clip currentClip = null;
    private Map<String, String> songPaths = new HashMap<>();
    private Thread loopThread;
    private String currentSong = "";
    private Map<String, double[]> snippetRanges = new HashMap<>();
    private Map<String, Map<Integer, Snippet>> snippetMap = new HashMap<>();

    public MusicManager() {
        songPaths.put("menu", "Re End of a Dream.wav");
        songPaths.put("credits", "Aleph-0.wav");
        songPaths.put("instructions", "Nulctrl EX.wav");
        songPaths.put("other", "Chronomia.wav");

        // Collection A (Music Collection 1)
        songPaths.put("collectionA1", "Music Collection 1/Crystallized.wav");
        songPaths.put("collectionA2", "Music Collection 1/Dance With Silence.wav");
        songPaths.put("collectionA3", "Music Collection 1/Exit This Earth's Atomosphere.wav");
        songPaths.put("collectionA4", "Music Collection 1/Ghost.wav");
        songPaths.put("collectionA5", "Music Collection 1/#1f1e33.wav");
        songPaths.put("collectionA6", "Music Collection 1/Ghoul.wav");
        songPaths.put("collectionA7", "Music Collection 1/Light it up.wav");
        songPaths.put("collectionA8", "Music Collection 1/Quaoar.wav");
        songPaths.put("collectionA9", "Music Collection 1/Terabyte Connection.wav");
        songPaths.put("collectionA10", "Music Collection 1/We Magicians Still Alive in 2021.wav");

        // Collection B (Music Collection 2)
        songPaths.put("collectionB1", "Music Collection 2/Ai no Sukima.wav");
        songPaths.put("collectionB2", "Music Collection 2/Ascension to Heaven.wav");
        songPaths.put("collectionB3", "Music Collection 2/Blue Zenith.wav");
        songPaths.put("collectionB4", "Music Collection 2/Censored.wav");
        songPaths.put("collectionB5", "Music Collection 2/Everything Will Freeze.wav");
        songPaths.put("collectionB6", "Music Collection 2/Freedom Dive.wav");
        songPaths.put("collectionB7", "Music Collection 2/Galaxy Collapse.wav");
        songPaths.put("collectionB8", "Music Collection 2/Harumachi Clover.wav");
        songPaths.put("collectionB9", "Music Collection 2/Lagtrain.wav");
        songPaths.put("collectionB10", "Music Collection 2/No Title.wav");

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
        songPaths.put("collectionD1", "Music Collection 4/At the Speed of Light.wav");
        songPaths.put("collectionD2", "Music Collection 4/Isolation.wav");
        songPaths.put("collectionD3", "Music Collection 4/Nhelv.wav");
        songPaths.put("collectionD4", "Music Collection 4/Pagoda.wav");
        songPaths.put("collectionD5", "Music Collection 4/Reanimate.wav");
        songPaths.put("collectionD6", "Music Collection 4/Shiawase - VIP.wav");
        songPaths.put("collectionD7", "Music Collection 4/Skystrike.wav");
        songPaths.put("collectionD8", "Music Collection 4/The Calling.wav");
        songPaths.put("collectionD9", "Music Collection 4/Time Leaper.wav");
        songPaths.put("collectionD10", "Music Collection 4/Unity.wav");

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
        songPaths.put("collectionG6", "Music Collection 7/KABOOOOOM!!!!.wav");
        songPaths.put("collectionG7", "Music Collection 7/Oshama Scramble.wav");
        songPaths.put("collectionG8", "Music Collection 7/QZKago Requiem.wav");
        songPaths.put("collectionG9", "Music Collection 7/Tempestissimo.wav");
        songPaths.put("collectionG10", "Music Collection 7/TO THE COSMIC!!.wav");

        songPaths.put("collectionH1", "Music Collection 8/Bring it on.wav");
        songPaths.put("collectionH2", "Music Collection 8/Gimme x Gimme.wav");
        songPaths.put("collectionH3", "Music Collection 8/Hibana.wav");
        songPaths.put("collectionH4", "Music Collection 8/Luka Luka Night Fever.wav");
        songPaths.put("collectionH5", "Music Collection 8/Monitoring.wav");
        songPaths.put("collectionH6", "Music Collection 8/Override.wav");
        songPaths.put("collectionH7", "Music Collection 8/Senbonzakura.wav");
        songPaths.put("collectionH8", "Music Collection 8/Six Trillion Years and Overnight Story.wav");
        songPaths.put("collectionH9", "Music Collection 8/tivohm.wav");
        songPaths.put("collectionH10", "Music Collection 8/Tokyo Teddy Bear.wav");

        // Collection A
        snippetRanges.put("collectionA1", new double[]{40, 65});
        snippetRanges.put("collectionA2", new double[]{80, 110});
        snippetRanges.put("collectionA3", new double[]{40, 70});
        snippetRanges.put("collectionA4", new double[]{238, 268});
        snippetRanges.put("collectionA5", new double[]{191, 220});
        snippetRanges.put("collectionA6", new double[]{60, 90});
        snippetRanges.put("collectionA7", new double[]{150, 180});
        snippetRanges.put("collectionA8", new double[]{195, 225});
        snippetRanges.put("collectionA9", new double[]{245, 275});
        snippetRanges.put("collectionA10", new double[]{90, 120});

// Collection B
        snippetRanges.put("collectionB1", new double[]{30, 60});
        snippetRanges.put("collectionB2", new double[]{110, 140});
        snippetRanges.put("collectionB3", new double[]{105, 135});
        snippetRanges.put("collectionB4", new double[]{30, 60});
        snippetRanges.put("collectionB5", new double[]{90, 120});
        snippetRanges.put("collectionB6", new double[]{90, 120});
        snippetRanges.put("collectionB7", new double[]{165, 195});
        snippetRanges.put("collectionB8", new double[]{90, 120});
        snippetRanges.put("collectionB9", new double[]{90, 120});
        snippetRanges.put("collectionB10", new double[]{90, 120});

// Collection C
        snippetRanges.put("collectionC1", new double[]{90, 120});
        snippetRanges.put("collectionC2", new double[]{20, 50});
        snippetRanges.put("collectionC3", new double[]{50, 80});
        snippetRanges.put("collectionC4", new double[]{50, 80});
        snippetRanges.put("collectionC5", new double[]{85, 115});
        snippetRanges.put("collectionC6", new double[]{105, 135});
        snippetRanges.put("collectionC7", new double[]{105, 135});
        snippetRanges.put("collectionC8", new double[]{130, 160});
        snippetRanges.put("collectionC9", new double[]{110, 140});
        snippetRanges.put("collectionC10", new double[]{200, 230});

// Collection D
        snippetRanges.put("collectionD1", new double[]{90, 120});
        snippetRanges.put("collectionD2", new double[]{90, 120});
        snippetRanges.put("collectionD3", new double[]{90, 120});
        snippetRanges.put("collectionD4", new double[]{75, 105});
        snippetRanges.put("collectionD5", new double[]{90, 120});
        snippetRanges.put("collectionD6", new double[]{90, 120});
        snippetRanges.put("collectionD7", new double[]{110, 140});
        snippetRanges.put("collectionD8", new double[]{90, 120});
        snippetRanges.put("collectionD9", new double[]{80, 110});
        snippetRanges.put("collectionD10", new double[]{30, 60});

// Collection E
        snippetRanges.put("collectionE1", new double[]{40, 70});
        snippetRanges.put("collectionE2", new double[]{60, 90});
        snippetRanges.put("collectionE3", new double[]{55, 85});
        snippetRanges.put("collectionE4", new double[]{200, 230});
        snippetRanges.put("collectionE5", new double[]{45, 75});
        snippetRanges.put("collectionE6", new double[]{60, 90});
        snippetRanges.put("collectionE7", new double[]{90, 120});
        snippetRanges.put("collectionE8", new double[]{90, 120});
        snippetRanges.put("collectionE9", new double[]{90, 120});
        snippetRanges.put("collectionE10", new double[]{60, 90});

// Collection F
        snippetRanges.put("collectionF1", new double[]{100, 130});
        snippetRanges.put("collectionF2", new double[]{20, 50});
        snippetRanges.put("collectionF3", new double[]{100, 130});
        snippetRanges.put("collectionF4", new double[]{30, 60});
        snippetRanges.put("collectionF5", new double[]{60, 90});
        snippetRanges.put("collectionF6", new double[]{60, 90});
        snippetRanges.put("collectionF7", new double[]{60, 90});
        snippetRanges.put("collectionF8", new double[]{60, 90});
        snippetRanges.put("collectionF9", new double[]{75, 105});
        snippetRanges.put("collectionF10", new double[]{70, 100});

// Collection G
        snippetRanges.put("collectionG1", new double[]{60, 90});
        snippetRanges.put("collectionG2", new double[]{90, 120});
        snippetRanges.put("collectionG3", new double[]{90, 120});
        snippetRanges.put("collectionG4", new double[]{110, 140});
        snippetRanges.put("collectionG5", new double[]{90, 120});
        snippetRanges.put("collectionG6", new double[]{90, 120});
        snippetRanges.put("collectionG7", new double[]{45, 75});
        snippetRanges.put("collectionG8", new double[]{30, 60});
        snippetRanges.put("collectionG9", new double[]{90, 120});
        snippetRanges.put("collectionG10", new double[]{90, 120});

// Collection H
        snippetRanges.put("collectionH1", new double[]{60, 90});
        snippetRanges.put("collectionH2", new double[]{60, 90});
        snippetRanges.put("collectionH3", new double[]{60, 90});
        snippetRanges.put("collectionH4", new double[]{60, 90});
        snippetRanges.put("collectionH5", new double[]{60, 90});
        snippetRanges.put("collectionH6", new double[]{60, 90});
        snippetRanges.put("collectionH7", new double[]{60, 90});
        snippetRanges.put("collectionH8", new double[]{60, 90});
        snippetRanges.put("collectionH9", new double[]{60, 90});
        snippetRanges.put("collectionH10", new double[]{60, 90});

        initSnippets();
    }

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

    // --------------------
    // Full song loop
    // --------------------
    public void loop(String songId) throws Exception {
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
    public void loopSnippet(String songId, double startSec, double stopSec, int gapMs) throws Exception {
        if (songId.equals(currentSong)) return;
        stopLoop();

        String path = songPaths.get(songId);
        if (path == null) throw new IllegalArgumentException("Song ID not found: " + songId);

        AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(path));
        currentClip = AudioSystem.getClip();
        currentClip.open(audioStream);

        AudioFormat format = currentClip.getFormat();
        float frameRate = format.getFrameRate();

        int startFrame = (int) (startSec * frameRate);
        int stopFrame = (int) (stopSec * frameRate);
        long snippetDurationMs = (long) ((stopFrame - startFrame) / frameRate * 1000);

        loopThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
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

    // --------------------
    // Auto-loop snippet if range exists, else full loop
    // --------------------
    public void loopSnippet(String songId) throws Exception {
        double[] range = snippetRanges.get(songId);
        if (range != null) {
            loopSnippet(songId, range[0], range[1], 1500);
        } else {
            loop(songId);
        }
    }
    private void initSnippets() {
        // --------------------
        // Collection A
        // --------------------
        Map<Integer, Snippet> collectionA = new HashMap<>();
        collectionA.put(1, new Snippet(80, 110, 1500));
        collectionA.put(2, new Snippet(40, 70, 1500));
        collectionA.put(3, new Snippet(238, 268, 1500));
        collectionA.put(4, new Snippet(191, 220, 1500));
        collectionA.put(5, new Snippet(40, 65, 1500));
        collectionA.put(6, new Snippet(60, 90, 1500));
        collectionA.put(7, new Snippet(150, 180, 1500));
        collectionA.put(8, new Snippet(195, 225, 1500));
        collectionA.put(9, new Snippet(245, 275, 1500));
        collectionA.put(10, new Snippet(90, 120, 1500));
        snippetMap.put("A", collectionA);

        // --------------------
        // Collection B
        // --------------------
        Map<Integer, Snippet> collectionB = new HashMap<>();
        collectionB.put(1, new Snippet(30, 60, 1500));
        collectionB.put(2, new Snippet(110, 140, 1500));
        collectionB.put(3, new Snippet(105, 135, 1500));
        collectionB.put(4, new Snippet(30, 60, 1500));
        collectionB.put(5, new Snippet(90, 120, 1500));
        collectionB.put(6, new Snippet(90, 120, 1500));
        collectionB.put(7, new Snippet(165, 195, 1500));
        collectionB.put(8, new Snippet(90, 120, 1500));
        collectionB.put(9, new Snippet(90, 120, 1500));
        collectionB.put(10, new Snippet(90, 120, 1500));
        snippetMap.put("B", collectionB);

        // --------------------
        // Collection C
        // --------------------
        Map<Integer, Snippet> collectionC = new HashMap<>();
        collectionC.put(1, new Snippet(90, 120, 1500));
        collectionC.put(2, new Snippet(20, 50, 1500));
        collectionC.put(3, new Snippet(50, 80, 1500));
        collectionC.put(4, new Snippet(50, 80, 1500));
        collectionC.put(5, new Snippet(85, 115, 1500));
        collectionC.put(6, new Snippet(105, 135, 1500));
        collectionC.put(7, new Snippet(105, 135, 1500));
        collectionC.put(8, new Snippet(130, 160, 1500));
        collectionC.put(9, new Snippet(110, 140, 1500));
        collectionC.put(10, new Snippet(200, 230, 1500));
        snippetMap.put("C", collectionC);

        // --------------------
        // Collection D
        // --------------------
        Map<Integer, Snippet> collectionD = new HashMap<>();
        collectionD.put(1, new Snippet(90, 120, 1500));
        collectionD.put(2, new Snippet(90, 120, 1500));
        collectionD.put(3, new Snippet(75, 105, 1500));
        collectionD.put(4, new Snippet(90, 120, 1500));
        collectionD.put(5, new Snippet(90, 120, 1500));
        collectionD.put(6, new Snippet(110, 140, 1500));
        collectionD.put(7, new Snippet(90, 120, 1500));
        collectionD.put(8, new Snippet(90, 120, 1500));
        collectionD.put(9, new Snippet(80, 110, 1500));
        collectionD.put(10, new Snippet(30, 60, 1500));
        snippetMap.put("D", collectionD);

        // --------------------
        // Collection E
        // --------------------
        Map<Integer, Snippet> collectionE = new HashMap<>();
        collectionE.put(1, new Snippet(40, 70, 1500));
        collectionE.put(2, new Snippet(60, 90, 1500));
        collectionE.put(3, new Snippet(55, 85, 1500));
        collectionE.put(4, new Snippet(200, 230, 1500));
        collectionE.put(5, new Snippet(45, 75, 1500));
        collectionE.put(6, new Snippet(60, 90, 1500));
        collectionE.put(7, new Snippet(90, 120, 1500));
        collectionE.put(8, new Snippet(90, 120, 1500));
        collectionE.put(9, new Snippet(90, 120, 1500));
        collectionE.put(10, new Snippet(60, 90, 1500));
        snippetMap.put("E", collectionE);

        // --------------------
        // Collection F
        // --------------------
        Map<Integer, Snippet> collectionF = new HashMap<>();
        collectionF.put(1, new Snippet(100, 130, 1500));
        collectionF.put(2, new Snippet(20, 50, 1500));
        collectionF.put(3, new Snippet(100, 130, 1500));
        collectionF.put(4, new Snippet(30, 60, 1500));
        collectionF.put(5, new Snippet(60, 90, 1500));
        collectionF.put(6, new Snippet(60, 90, 1500));
        collectionF.put(7, new Snippet(60, 90, 1500));
        collectionF.put(8, new Snippet(60, 90, 1500));
        collectionF.put(9, new Snippet(75, 105, 1500));
        collectionF.put(10, new Snippet(70, 100, 1500));
        snippetMap.put("F", collectionF);

        // --------------------
        // Collection G
        // --------------------
        Map<Integer, Snippet> collectionG = new HashMap<>();
        collectionG.put(1, new Snippet(60, 90, 1500));
        collectionG.put(2, new Snippet(90, 120, 1500));
        collectionG.put(3, new Snippet(90, 120, 1500));
        collectionG.put(4, new Snippet(110, 140, 1500));
        collectionG.put(5, new Snippet(90, 120, 1500));
        collectionG.put(6, new Snippet(90, 120, 1500));
        collectionG.put(7, new Snippet(45, 75, 1500));
        collectionG.put(8, new Snippet(30, 60, 1500));
        collectionG.put(9, new Snippet(90, 120, 1500));
        collectionG.put(10, new Snippet(90, 120, 1500));
        snippetMap.put("G", collectionG);

        // --------------------
        // Collection H
        // --------------------
        Map<Integer, Snippet> collectionH = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            collectionH.put(i, new Snippet(60, 90, 1500));
        }
        snippetMap.put("H", collectionH);
    }
    public Snippet getSnippet(String key) {
        return (Snippet) snippetMap.get(key); // returns null if the key doesn't exist
    }
    public String getCurrentSong() {
        return currentSong;
    }
}
