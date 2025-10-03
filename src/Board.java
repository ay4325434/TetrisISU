
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

public class Board extends JPanel implements Runnable, MouseListener {
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;

    final int FPS = 60;

    public Image menu;

    public Rectangle screen = new Rectangle(0, 0, WIDTH, HEIGHT);

    Thread gameThread;
    GameManager gm;
    MusicManager mm = new MusicManager();
    public Board(){
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);
        this.setLayout(null);
        this.addKeyListener(new KeyHandler());
        this.setFocusable(true);
        addMouseListener(this);
        gm = new GameManager();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;
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
                drawCount++;
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
        if(gm.musicSelectButton.contains(p) && gm.gameState == gm.MENU){
            gm.gameState = gm.MUSIC_SELECT;
            mm.stop();
            gm.currentSong = "";
        }
        else if(gm.credsButton.contains(p) && gm.gameState == gm.MENU){
            gm.page = 0;
            gm.gameState = gm.CREDITS;
            mm.stop();
            gm.currentSong = "";
        }
        else if(gm.insButton.contains(p) && gm.gameState == gm.MENU){
            gm.page = 0;
            gm.gameState = gm.INSTRUCTIONS;
            mm.stop();
            gm.currentSong = "";
        }
        else if(gm.otherButton.contains(p) && gm.gameState == gm.MENU){
            gm.page = 1;
            gm.gameState = gm.OTHER;
            mm.stop();
            gm.currentSong = "";
        }
        else if (gm.gameState == gm.MENU){
            mm.stop();
            gm.gameState = gm.PLAYING;
        }
        if(gm.backButton.contains(p)){
            if(gm.gameState == gm.MUSIC_SELECT || gm.gameState == gm.CREDITS || gm.gameState == gm.INSTRUCTIONS) {
                mm.stop();
                gm.currentSong = "";
                gm.gameState = gm.MENU;
                try {
                    mm.loop(gm.currentSong);
                } catch (UnsupportedAudioFileException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (LineUnavailableException ex) {
                    throw new RuntimeException(ex);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
            else if (gm.gameState == gm.SONGS){
                gm.gameState = gm.MUSIC_SELECT;
            }
        }
        if(gm.gameState == gm.MUSIC_SELECT) {
            if (gm.mc1.contains(p)) {
                gm.gameState = gm.SONGS;
                gm.collection = 1;
                gm.song = 1;
                gm.currentBackground = 1;
            }
            if (gm.mc2.contains(p)) {
                gm.gameState = gm.SONGS;
                gm.collection = 2;
                gm.song = 1;
                gm.currentBackground = 1;
            }
            if (gm.mc3.contains(p)) {
                gm.gameState = gm.SONGS;
                gm.collection = 3;
                gm.song = 1;
                gm.currentBackground = 1;
            }
            if (gm.mc4.contains(p)) {
                gm.gameState = gm.SONGS;
                gm.collection = 4;
                gm.song = 1;
                gm.currentBackground = 1;
            }
            if (gm.mc5.contains(p)) {
                gm.gameState = gm.SONGS;
                gm.collection = 5;
                gm.song = 1;
                gm.currentBackground = 1;
            }
            if (gm.mc6.contains(p)) {
                gm.gameState = gm.SONGS;
                gm.collection = 6;
                gm.song = 1;
                gm.currentBackground = 1;
            }
        }
        if(gm.leftButton.contains(p) && gm.gameState == gm.SONGS){
            gm.currentBackground--;
            gm.song--;
            if(gm.collection != 6) {
                if (gm.song < 1) {
                    gm.song = 10;
                }
                if (gm.currentBackground < 1) {
                    gm.currentBackground = 10;
                }
            }
            else{
                if(gm.song < 1){
                    gm.song = 5;
                }
                if (gm.currentBackground < 1) {
                    gm.currentBackground = 5;
                }
            }
        }
        if(gm.rightButton.contains(p) && gm.gameState == gm.SONGS){
            gm.currentBackground++;
            gm.song++;
            if(gm.collection != 6) {
                if (gm.song > 10) {
                    gm.song = 1;
                }
                if (gm.currentBackground > 10) {
                    gm.currentBackground = 1;
                }
            }
            else{
                if (gm.song > 5) {
                    gm.song = 1;
                }
                if (gm.currentBackground > 5) {
                    gm.currentBackground = 1;
                }
            }
        }
        if(gm.rightButton.contains(p) && gm.gameState == gm.CREDITS){
            gm.page++;
            if(gm.page > 6) gm.page = 1;
        }
        if(gm.leftButton.contains(p) && gm.gameState == gm.CREDITS){
            gm.page--;
            if(gm.page < 1) gm.page = 6;
        }
        if(gm.rightButton.contains(p) && gm.gameState == gm.INSTRUCTIONS){
            gm.page++;
            if(gm.page > 4) gm.page = 1;
        }
        if(gm.leftButton.contains(p) && gm.gameState == gm.INSTRUCTIONS){
            gm.page--;
            if(gm.page < 1) gm.page = 4;
        }
        if(gm.rightButton.contains(p) && gm.gameState == gm.OTHER){
            gm.page++;
            if(gm.page > 3) gm.page = 1;
        }
        if(gm.leftButton.contains(p) && gm.gameState == gm.OTHER){
            gm.page--;
            if(gm.page < 1) gm.page = 3;
        }
        if (gm.gameState == gm.GAME_OVER) {
            gm.gameState = gm.MENU;
        }
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
}
