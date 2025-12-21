/*
Checks to see if an image can load properly.
 */
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FileChecker {
    public static void main(String[] args) {
        BufferedImage img;
        try {
            File f = new File("Images/Collection 9/cthugha.png");
            System.out.println("Looking for: " + f.getAbsolutePath());
            img = ImageIO.read(f);
            System.out.println(f.exists());
            System.out.println(f.getAbsolutePath());
            if (img == null) {
                System.err.println("Failed to load image: returned null");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
