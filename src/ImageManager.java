import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ImageManager {
    private Map<String, String> paths = new HashMap<>();
    private Map<String, Image> cache = new HashMap<>();
    public ImageManager(){
        paths.put("A1", "Images/Collection 1/1f1e33.png");
        paths.put("A2", "Images/Collection 1/crystallized.png");
        paths.put("A3", "Images/Collection 1/dws.png");
        paths.put("A4", "Images/Collection 1/etea.png");
        paths.put("A5", "Images/Collection 1/ghost.png");
        paths.put("A6", "Images/Collection 1/ghoul.png");
        paths.put("A7", "Images/Collection 1/lightitup.png");
        paths.put("A8", "Images/Collection 1/quaoar.png");
        paths.put("A9", "Images/Collection 1/tera.png");
        paths.put("A10", "Images/Collection 1/wmsa.png");

        paths.put("B1", "Images/Collection 2/marshmary.png");
        paths.put("B2", "Images/Collection 2/ath.png");
        paths.put("B3", "Images/Collection 2/bluezenith.png");
        paths.put("B4", "Images/Collection 2/censored.png");
        paths.put("B5", "Images/Collection 2/ewf.png");
        paths.put("B6", "Images/Collection 2/freedomdive.png");
        paths.put("B7", "Images/Collection 2/gc.png");
        paths.put("B8", "Images/Collection 2/haruclover.png");
        paths.put("B9", "Images/Collection 2/dftm.png");
        paths.put("B10", "Images/Collection 2/notitle.png");

        paths.put("C1", "Images/Collection 3/des321.png");
        paths.put("C2", "Images/Collection 3/distortedfate.png");
        paths.put("C3", "Images/Collection 3/igallta.png");
        paths.put("C4", "Images/Collection 3/likww.png");
        paths.put("C5", "Images/Collection 3/pp.png");
        paths.put("C6", "Images/Collection 3/rrh.png");
        paths.put("C7", "Images/Collection 3/stardustray.png");
        paths.put("C8", "Images/Collection 3/water.png");
        paths.put("C9", "Images/Collection 3/wavetapper.png");
        paths.put("C10", "Images/Collection 3/yatm.png");

        paths.put("D1", "Images/Collection 4/isolation.png");
        paths.put("D2", "Images/Collection 4/nhelv.png");
        paths.put("D3", "Images/Collection 4/pagoda.png");
        paths.put("D4", "Images/Collection 4/reanimate.png");
        paths.put("D5", "Images/Collection 4/shiawasevip.png");
        paths.put("D6", "Images/Collection 4/skystrike.png");
        paths.put("D7", "Images/Collection 4/speedoflight.png");
        paths.put("D8", "Images/Collection 4/thecalling.png");
        paths.put("D9", "Images/Collection 4/timeleaper.png");
        paths.put("D10", "Images/Collection 4/unity.png");

        paths.put("E1", "Images/Collection 5/bfeg.png");
        paths.put("E2", "Images/Collection 5/golden.png");
        paths.put("E3", "Images/Collection 5/idol.png");
        paths.put("E4", "Images/Collection 5/intothenight.png");
        paths.put("E5", "Images/Collection 5/kaikaikitan.png");
        paths.put("E6", "Images/Collection 5/kkr.png");
        paths.put("E7", "Images/Collection 5/mgpd.png");
        paths.put("E8", "Images/Collection 5/nightdancer.png");
        paths.put("E9", "Images/Collection 5/otonoke.png");
        paths.put("E10", "Images/Collection 5/roki.png");

        paths.put("F1", "Images/Collection 6/aegleseeker.png");
        paths.put("F2", "Images/Collection 6/bbkkbkk.png");
        paths.put("F3", "Images/Collection 6/brain power.png");
        paths.put("F4", "Images/Collection 6/cnok.png");
        paths.put("F5", "Images/Collection 6/grievous lady.png");
        paths.put("F6", "Images/Collection 6/mesmerizer.png");
        paths.put("F7", "Images/Collection 6/pp.png");
        paths.put("F8", "Images/Collection 6/pupa.png");
        paths.put("F9", "Images/Collection 6/sage.png");
        paths.put("F10", "Images/Collection 6/the emperror.png");

        paths.put("G1", "Images/Collection 7/angelic jelly.png");
        paths.put("G2", "Images/Collection 7/chaos time.png");
        paths.put("G3", "Images/Collection 7/cheatreal.png");
        paths.put("G4", "Images/Collection 7/chrome vox.png");
        paths.put("G5", "Images/Collection 7/gdp.png");
        paths.put("G6", "Images/Collection 7/kabooooom.png");
        paths.put("G7", "Images/Collection 7/oshama scramble.png");
        paths.put("G8", "Images/Collection 7/qzkr.png");
        paths.put("G9", "Images/Collection 7/tempestissimo.png");
        paths.put("G10", "Images/Collection 7/to the cosmic.png");

        paths.put("H1", "Images/Collection 8/bring it on.png");
        paths.put("H2", "Images/Collection 8/gimme x gimme.png");
        paths.put("H3", "Images/Collection 8/hibana.png");
        paths.put("H4", "Images/Collection 8/llnf.png");
        paths.put("H5", "Images/Collection 8/monitoring.png");
        paths.put("H6", "Images/Collection 8/override.png");
        paths.put("H7", "Images/Collection 8/sbz.png");
        paths.put("H8", "Images/Collection 8/styaos.png");
        paths.put("H9", "Images/Collection 8/lagtrain.png");
        paths.put("H10", "Images/Collection 8/ttb.png");

        // New music collection - add images here
        paths.put("I1", "Images/Collection 9/climax.png");
        paths.put("I2", "Images/Collection 9/cthugha.png");
        paths.put("I3", "Images/Collection 9/cyaegha.png");
        paths.put("I4", "Images/Collection 9/dead or die.png");
        paths.put("I5", "Images/Collection 9/invisible frenzy.png");
        paths.put("I6", "Images/Collection 9/izana.png");
        paths.put("I7", "Images/Collection 9/knight rider.png");
        paths.put("I8", "Images/Collection 9/outrage.png");
        paths.put("I9", "Images/Collection 9/red to red.png");
        paths.put("I10", "Images/Collection 9/vs.png");

        paths.put("credits1", "Images/Credits/credits1.png");
        paths.put("credits2", "Images/Credits/credits2.png");
        paths.put("credits3", "Images/Credits/credits3.png");
        paths.put("credits4", "Images/Credits/credits4.png");
        paths.put("credits5", "Images/Credits/credits5.png");

        paths.put("ins1", "Images/Instructions/ins1.png");
        paths.put("ins2", "Images/Instructions/ins2.png");
        paths.put("ins3", "Images/Instructions/ins3.png");
        paths.put("ins4", "Images/Instructions/ins4.png");

        paths.put("other1", "Images/Other/other1.png");
        paths.put("other2", "Images/Other/other2.png");
        paths.put("other3", "Images/Other/other3.png");

        paths.put("initialize1", "Images/Initialize/initialize1.png");
    }

    /**
     * Loads an image for the GUI.
     * @param imageId The name of the image. Stores it in a cache to prevent repeated I/O reading.
     * @return The image
     */
    public Image getImage(String imageId) {
        if (!cache.containsKey(imageId)) {
            String path = paths.get(imageId);
            if (path == null) {
                return null;
            }
            try {
                Image img = ImageIO.read(new File(path));
                cache.put(imageId, img);
            } catch (IOException e) {
                System.err.println("Failed to load: " + path);
                e.printStackTrace();
                return null;
            }
        }
        return cache.get(imageId);
    }

    /** Optional: remove a single image from cache */
    public void unloadImage(String imageId) {
        cache.remove(imageId);
    }

    /** Optional: clear all cached images */
    public void clearCache() {
        cache.clear();
    }
}
