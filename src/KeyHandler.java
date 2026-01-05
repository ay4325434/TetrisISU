/*
 * Keeps track of keyboard input.
 */
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener{
    public static boolean upPressed, downPressed, leftPressed, rightPressed,
            zPressed, aPressed, shiftPressed, spacePressed, pausePressed, escPressed = false;

    private GameManager gm;
    private int frames;

    public KeyHandler(GameManager gm){
        this.gm = gm;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if(code == KeyEvent.VK_UP || code == KeyEvent.VK_X){
            upPressed = true;
        }
        if(code == KeyEvent.VK_DOWN){
            downPressed = true;
        }
        if(code == KeyEvent.VK_LEFT) {
            leftPressed = true;
        }
        if(code == KeyEvent.VK_RIGHT) {
            rightPressed = true;
        }
        if (code == KeyEvent.VK_Z) {
            zPressed = true;
        }
        if (code == KeyEvent.VK_A) {
            aPressed = true;
        }
        if (code == KeyEvent.VK_SHIFT) {
            shiftPressed = true;
        }
        if(code == KeyEvent.VK_SPACE){
            if(gm.isPlaying()) {
                spacePressed = true;
            }
        }
        if (code == KeyEvent.VK_P) {
            if(!pausePressed) {
                pausePressed = true;
                frames = gm.pause();
            }
            else{
                pausePressed = false;
                gm.resume(frames);
            }
        }

        if(code == KeyEvent.VK_ESCAPE){
            if(gm.isPaused() && gm.isPlaying()) {
                escPressed = true;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
