
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Board extends JPanel implements Runnable, MouseListener, MouseMotionListener {
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;

    final int FPS = 60;

    public Rectangle screen = new Rectangle(0, 0, WIDTH, HEIGHT);

    Thread gameThread;
    GameManager gm;
    public Board(){
        gm = new GameManager();
        KeyHandler k = new KeyHandler(gm);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);
        this.setLayout(null);
        this.addKeyListener(k);
        this.setFocusable(true);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {
                // 1 update: update information such as character positions
                // 2 draw: draw the screen with the updated information
                try {
                    update();
                } catch (UnsupportedAudioFileException e) {
                    throw new RuntimeException(e);
                } catch (LineUnavailableException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                repaint();
                delta--;
            }
            if (timer >= 1_000_000_000) {   // 1 second
                timer = 0;
            }
        }
    }
    public void startGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }
    private void update() throws Exception {
        gm.update();
        repaint();
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        try {
            gm.draw(g2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();
        if(gm.isInitializing() && screen.contains(p)){
            gm.goToMenu();
            return;
        }
        if (gm.isInMenu()) { // outer check
            if (gm.musicSelectButton.contains(p)) {
                gm.goToSelection();
                return;
            }
            else if (gm.credsButton.contains(p)) {
                gm.resetPage();
                gm.goToCredits();
            }
            else if (gm.insButton.contains(p)) {
                gm.resetPage();
                gm.goToInstructions();
            }
            else if (gm.otherButton.contains(p)) {
                gm.resetPage();
                gm.goToOther();
            }
            else if (gm.scoreButton.contains(p)) {
                // Add functions later
            }
            else {
                gm.play();
            }
        }
        if(gm.backButton.contains(p)){
            if (gm.isInSongs()){
                gm.goToSelection();
            }
            else if(!gm.isPlaying()) {
                gm.goToMenu();
            }
        }
        if(gm.isSelectingSong()) {
            if (gm.mc1.contains(p)) {
                if(gm.isSelectionActivated()){
                    gm.saveSongCollection(1);
                }
                else {
                    gm.selectCollection(1);
                }
            }

            if (gm.mc2.contains(p)) {
                if(gm.isSelectionActivated()){
                    gm.saveSongCollection(2);
                }
                else {
                    gm.selectCollection(2);
                }
            }

            if (gm.mc3.contains(p)) {
                if(gm.isSelectionActivated()){
                    gm.saveSongCollection(3);
                }
                else {
                    gm.selectCollection(3);
                }
            }
            if (gm.mc4.contains(p)) {
                if(gm.isSelectionActivated()){
                    gm.saveSongCollection(4);
                }
                else {
                    gm.selectCollection(4);
                }
            }
            if (gm.mc5.contains(p)) {
                if(gm.isSelectionActivated()){
                    gm.saveSongCollection(5);
                }
                else {
                    gm.selectCollection(5);
                }
            }
            if (gm.mc6.contains(p)) {
                if(gm.isSelectionActivated()){
                    gm.saveSongCollection(6);
                }
                else {
                    gm.selectCollection(6);
                }
            }
            if (gm.mc7.contains(p)) {
                if(gm.isSelectionActivated()){
                    gm.saveSongCollection(7);
                }
                else {
                    gm.selectCollection(7);
                }
            }
            if (gm.mc8.contains(p)) {
                if(gm.isSelectionActivated()){
                    gm.saveSongCollection(8);
                }
                else {
                    gm.selectCollection(8);
                }
            }
            if (gm.mc9.contains(p)) {
                if(gm.isSelectionActivated()){
                    gm.saveSongCollection(9);
                }
                else {
                    gm.selectCollection(9);
                }
            }
//            if (gm.mc10.contains(p)) {
//                if(gm.isSelectionActivated()){
//                    gm.saveSongCollection(10);
//                }
//                else {
//                    gm.selectCollection(10);
//                }
//            }
            if(gm.select.contains(p) && gm.isSelectingSong()){
                gm.select();
            }
        }
        if(gm.leftButton.contains(p) && gm.isInSongs()){
            gm.previousSong();
        }
        if(gm.rightButton.contains(p) && gm.isInSongs()){
            gm.nextSong();
        }
        else if(gm.rightButton.contains(p)){
            gm.nextPage();
        }
        else if(gm.leftButton.contains(p)){
            gm.previousPage();
        }

        if (gm.isGameOver()) {
            try {
                saveScore();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            gm.goToMenu();
        }
    }

    public void saveScore() throws IOException {
        String name = JOptionPane.showInputDialog(this, "Enter your name:").trim();
        if(name.isEmpty()) name = "Player";
        gm.saveScore(name);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Point p = e.getPoint();
        if(gm.isSelectionActivated()){
            gm.updateHover(p);
        }
    }
}
