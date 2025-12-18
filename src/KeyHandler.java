/*
 * Keeps track of keyboard input.
 */
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener{
    public static boolean upPressed, downPressed, leftPressed, rightPressed, zPressed, aPressed, shiftPressed, spacePressed, pausePressed = false;
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
            if(GameManager.gameState == GameManager.PLAYING) {
                spacePressed = true;
            }
        }

        if (code == KeyEvent.VK_P && !pausePressed) {
            pausePressed = true;
        }
        else{
            pausePressed = false;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
