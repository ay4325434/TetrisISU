/*
 * Keeps track of keyboard input.
 */
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener{
    private boolean upPressed, downPressed, leftPressed, rightPressed,
            zPressed, aPressed, shiftPressed, spacePressed, pausePressed, escPressed;

    private GameManager gm;
    private int frames;
    private boolean leftHeld = false;
    private boolean rightHeld = false;
    private boolean leftWasPressedLastFrame = false;
    private boolean rightWasPressedLastFrame = false;


    public KeyHandler(GameManager gm){
        this.gm = gm;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if(gm.isPlaying()) {
            if (code == KeyEvent.VK_UP || code == KeyEvent.VK_X) {
                upPressed = true;
            }
            if (code == KeyEvent.VK_DOWN) {
                downPressed = true;
            }
            if (code == KeyEvent.VK_LEFT) {
                leftPressed = true;
            }
            if (code == KeyEvent.VK_RIGHT) {
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
            if (code == KeyEvent.VK_SPACE) {
                if (gm.isPlaying()) {
                    spacePressed = true;
                }
            }
            if (code == KeyEvent.VK_P) {
                if (!pausePressed) {
                    pausePressed = true;
                    frames = gm.pause();
                } else {
                    pausePressed = false;
                    gm.resume(frames);
                }
            }

            if (code == KeyEvent.VK_ESCAPE) {
                if (gm.isPaused()) {
                    escPressed = true;
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(gm.isPlaying()){
            int code = e.getKeyCode();
            if (code == KeyEvent.VK_UP || code == KeyEvent.VK_X) {
                upPressed = false;
            }
            if (code == KeyEvent.VK_DOWN) {
                downPressed = false;
            }
            if (code == KeyEvent.VK_LEFT) {
                leftPressed = false;
            }
            if (code == KeyEvent.VK_RIGHT) {
                rightPressed = false;
            }
            if (code == KeyEvent.VK_Z) {
                zPressed = false;
            }
            if (code == KeyEvent.VK_A) {
                aPressed = false;
            }
            if (code == KeyEvent.VK_SHIFT) {
                shiftPressed = false;
            }
            if (code == KeyEvent.VK_SPACE) {
                spacePressed = false;
            }
            if (code == KeyEvent.VK_ESCAPE) {
                escPressed = false;
            }
        }
    }
    public boolean isUpPressed() {
        return upPressed;
    }
    public boolean isDownPressed() {
        return downPressed;
    }
    public boolean isLeftPressed() {
        return leftPressed;
    }
    public boolean isRightPressed() {
        return rightPressed;
    }
    public boolean isLeftHeld() { return leftHeld; }
    public boolean isRightHeld() { return rightHeld; }
    public boolean isZPressed() {
        return zPressed;
    }

    public boolean isLeftWasPressedLastFrame() {
        return leftWasPressedLastFrame;
    }
    public boolean isRightWasPressedLastFrame() {
        return rightWasPressedLastFrame;
    }

    public boolean isAPressed() {
        return aPressed;
    }
    public boolean isShiftPressed() {
        return shiftPressed;
    }
    public boolean isSpacePressed() {
        return spacePressed;
    }
    public boolean isPausePressed() {
        return pausePressed;
    }
    public boolean isEscPressed() {
        return escPressed;
    }
    public void resetPausePressed(){
        this.pausePressed = false;
    }
    public void resetEscPressed(){
        this.escPressed = false;
    }
    public void resetAPressed(){
        this.aPressed = false;
    }
    public void resetZPressed(){
        this.zPressed = false;
    }
    public void resetSpacePressed(){
        this.spacePressed = false;
    }
    public void resetShiftPressed(){
        this.shiftPressed = false;
    }
    public void resetUpPressed(){
        this.upPressed = false;
    }
    public void resetDownPressed(){
        this.downPressed = false;
    }
    public void resetLeftPressed(){
        this.leftPressed = false;
    }
    public void resetRightPressed(){
        this.rightPressed = false;
    }
    public void setGameManager(GameManager gm){
        this.gm = gm;
    }

    public void setLeftWasPressedLastFrame(boolean leftWasPressedLastFrame) {
        this.leftWasPressedLastFrame = leftWasPressedLastFrame;
    }
    public void setRightWasPressedLastFrame(boolean rightWasPressedLastFrame) {
        this.rightWasPressedLastFrame = rightWasPressedLastFrame;
    }
}
