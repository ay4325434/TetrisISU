import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MusicManager {

    public Clip currentClip = null;
    Map<String, String> songPaths = new HashMap<>();
    private Thread loopThread;

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

        songPaths.put("collectionH1", "Music Collection 8/");
        songPaths.put("collectionH2", "Music Collection 8/");
        songPaths.put("collectionH3", "Music Collection 8/");
        songPaths.put("collectionH4", "Music Collection 8/");
        songPaths.put("collectionH5", "Music Collection 8/");
        songPaths.put("collectionH6", "Music Collection 8/");
        songPaths.put("collectionH7", "Music Collection 8/");
        songPaths.put("collectionH8", "Music Collection 8/");
        songPaths.put("collectionH9", "Music Collection 8/");
        songPaths.put("collectionH10", "Music Collection 8/");
    }

    public void stop() {
        if (currentClip != null && currentClip.isOpen()) {
            currentClip.stop();
            currentClip.close();
            currentClip = null;
        }
    }

    public void loop(String songId) throws Exception {
        stop();

        String path = songPaths.get(songId);
        if (path == null) return;

        AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(path));
        currentClip = AudioSystem.getClip();
        currentClip.open(audioStream);
        currentClip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    public void loopSnippet(String songId, double startSec, double stopSec, int gapMs) throws Exception {
        stopLoop(); // stop any existing clip/thread

        String path = songPaths.get(songId);
        if (path == null) throw new IllegalArgumentException("Song ID not found: " + songId);

        AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(path));
        currentClip = AudioSystem.getClip();
        currentClip.open(audioStream);

        AudioFormat format = currentClip.getFormat();
        float frameRate = format.getFrameRate();

        int startFrame = (int) (startSec * frameRate);
        int stopFrame  = (int) (stopSec  * frameRate);

        int snippetFrames = stopFrame - startFrame;
        long snippetDurationMs = (long) (snippetFrames / frameRate * 1000);

        // Run the loop in a separate thread
        loopThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    currentClip.setFramePosition(startFrame);
                    currentClip.start();
                    Thread.sleep(snippetDurationMs); // wait for snippet
                    currentClip.stop();               // stop playback
                    Thread.sleep(gapMs);             // silent gap
                }
            } catch (InterruptedException e) {
                currentClip.stop();
            }
        });
        loopThread.setDaemon(true);
        loopThread.start();
    }

    /** Stop the currently looping snippet */
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
    }
}