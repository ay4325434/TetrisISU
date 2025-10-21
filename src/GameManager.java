import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class GameManager{

    //Game window properties
    final int WIDTH = 300;
    final int HEIGHT = 660;
    public static int leftX;
    public static int topY;
    public static int rightX;
    public static int bottomY;

    public int lines = 0;
    public int linesCleared = 0;
    public int level = 1;
    public int score = 0;

    //Piece properties
    private Mino currentMino;
    private int startX;
    private int startY;

    private Mino nextMino1, nextMino2, nextMino3, nextMino4, nextMino5, nextMino6;
    private int nextX1, nextY1;
    private int nextX2, nextY2;
    private int nextX3, nextY3;
    private int nextX4, nextY4;
    private int nextX5, nextY5;

    //Other properties
    public static int dropInterval = 60;

    public static ArrayList<Block> placedBlocks = new ArrayList<>();
    public static ArrayList<Mino> startingMinos = new ArrayList<>();
    private ArrayList<Mino> minos = new ArrayList<>(startingMinos);

    public static boolean hold = false;
    private static boolean alreadyHeld = false;
    private Mino holdMino = null;
    public static boolean selectionActivated = false;

    //Game states
    public static final int MENU = 0;
    public static final int PLAYING = 1;
    public static final int GAME_OVER = 2;
    public static final int INSTRUCTIONS = 3;
    public static final int MUSIC_SELECT = 4;
    public static final int CREDITS = 5;
    public static final int SONGS = 6;
    public static final int OTHER = 7;
    public static final int INITIALIZE = 8;
    public static int gameState = MENU;

    public static int currentBackground = 1; // Iterates through the music collections
    public static int song = 1;
    public static int collection = 1;

    private MusicManager mm = new MusicManager();
    public String currentSong = "";
    private ImageManager im = new ImageManager();

    public Rectangle musicSelectButton = new Rectangle(0, 0, 170, 30);
    public Rectangle backButton = new Rectangle(0, 0, 50, 30);
    public Rectangle credsButton = new Rectangle(180, 0, 100, 30);
    public Rectangle insButton = new Rectangle(330, 0, 140, 30);
    public Rectangle otherButton = new Rectangle(480, 0, 100, 30);
    public Rectangle mc1 = new Rectangle(0, 40, 500, 80);
    public Rectangle mc2 = new Rectangle(0, 140, 500, 80);
    public Rectangle mc3 = new Rectangle(0, 240, 500, 80);
    public Rectangle mc4 = new Rectangle(0, 340, 500, 80);
    public Rectangle mc5 = new Rectangle(0, 440, 500, 80);
    public Rectangle mc6 = new Rectangle(0, 540, 500, 80);
    public Rectangle mc7 = new Rectangle(0, 640, 500, 80);
    public Rectangle select = new Rectangle(80, 0, 80, 30);

    public Rectangle leftButton = new Rectangle(20, 220, 50, 200);
    public Rectangle rightButton = new Rectangle(1200, 220, 50, 200);
    public Rectangle placeholder = new Rectangle(80, 280, 380, 100);
    public Rectangle stuff = new Rectangle(360, 280, 100, 100);

    public Image menu, musicSelect, credits;

    public int page = 1;
    public static int playCollection = 1;

    public boolean hover = false;
    public static int hoveredCollection = 1;

    private int b2b = -1;
    private boolean surgeSent = false;

    public GameManager(){
        leftX = (Board.WIDTH / 2) - (WIDTH / 2);
        topY = 30;
        rightX = leftX + WIDTH;
        bottomY = HEIGHT + topY;

        startX = leftX + (WIDTH / 2) - Block.SIZE;
        startY = 0;

        // Setting dimensions for the next mino (where it will be displayed)
        nextX1 = rightX + 150;
        nextY1 = topY + 80;
        nextX2 = rightX + 150;
        nextY2 = topY + 180;
        nextX3 = rightX + 150;
        nextY3 = topY + 280;
        nextX4 = rightX + 150;
        nextY4 = topY + 380;
        nextX5 = rightX + 150;
        nextY5 = topY + 480;

        reset();

        try{
            menu = ImageIO.read(new File("Images/menu.png"));
            musicSelect = ImageIO.read(new File("Images/musicselect.png"));
            credits = ImageIO.read(new File("Images/credits.png"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image");
        }

        try{
            BufferedReader br = new BufferedReader(new FileReader("save.txt"));
            playCollection = Integer.parseInt(br.readLine());
        } catch (NumberFormatException e){
            playCollection = 1;
        } catch (IOException e){
            playCollection = 1;
        }
    }
    /**
     * Picks a random piece after the starting bag has been used up.
     * @return The next piece in queue that is not displayed yet
     */
    private Mino pickMino(){
        Mino m;
        if(!minos.isEmpty()){
            m = minos.getFirst();
            minos.removeFirst();
        }
        else{
            minos.add(new LPiece());
            minos.add(new JPiece());
            minos.add(new IPiece());
            minos.add(new SPiece());
            minos.add(new ZPiece());
            minos.add(new TPiece());
            minos.add(new OPiece());
            m = minos.getFirst();
            minos.removeFirst();
        }
        return m;
    }

    private String lineClearMessage = ""; // message to display
    private String spinMessage = "";
    private String pcMessage = "";
    private int messageTimer = 0;         // counts frames or updates
    private final int MESSAGE_DURATION = 120; // frames to display (2 seconds at 60 FPS)

    // --- Call this when clearing lines ---
    public void handleLineClear(int linesCleared) {
        if(checkForTSpin()){
            if(currentMino.direction == 1){
                spinMessage = "Mini T-Spin";
            }
            else{
                spinMessage = "T-Spin";
            }
        }
        if(checkForAllClear()){
            pcMessage = "ALL CLEAR";
        }
        switch(linesCleared) {
            case 1:
                lineClearMessage = "SINGLE";
                if(checkForTSpin() && checkForAllClear()) b2b++;
                else {
                    if(b2b > 4){
                        surgeSent = true;
                        b2b = -1;
                    }
                }
                break;
            case 2:
                lineClearMessage = "DOUBLE";
                if(checkForTSpin() && checkForAllClear()) b2b++;
                else {
                    if(b2b > 4){
                        surgeSent = true;
                        b2b = -1;
                    }
                }
                break;
            case 3:
                lineClearMessage = "TRIPLE";
                if(checkForTSpin() && checkForAllClear()) b2b++;
                else {
                    if(b2b > 4){
                        surgeSent = true;
                        b2b = -1;
                    }
                }
                break;
            case 4:
                lineClearMessage = "QUAD";
                b2b++;
                break;
            default:
                lineClearMessage = "";
                spinMessage = "";
                pcMessage = "";
            break;
        }
        messageTimer = MESSAGE_DURATION; // reset timer
    }

    private boolean checkForTSpin(){
        return currentMino.justRotated;
    }

    private boolean checkForAllClear(){
        return placedBlocks.isEmpty();
    }

    public void update() throws Exception {
        if (gameState == MENU && !currentSong.equals("menu")){
            mm.stop();
            mm.loop("menu");
            currentSong = "menu";
        }
        if (gameState == PLAYING) {
            mm.stop();
            currentSong = "";
            if (!KeyHandler.pausePressed) {
                if(playCollection == 1){
                    mm.loop("collectionA" + (level/2 + 1));
                    currentSong = "collectionA" + (level/2 + 1);
                }
                if(playCollection == 2){
                    mm.loop("collectionB" + (level/2 + 1));
                    currentSong = "collectionB" + (level/2 + 1);
                }
                if(playCollection == 3){
                    mm.loop("collectionC" + (level/2 + 1));
                    currentSong = "collectionC" + (level/2 + 1);
                }
                if(playCollection == 4){
                    mm.loop("collectionD" + (level/2 + 1));
                    currentSong = "collectionD" + (level/2 + 1);
                }
                if(playCollection == 5){
                    mm.loop("collectionE" + (level/2 + 1));
                    currentSong = "collectionE" + (level/2 + 1);
                }
                if(playCollection == 6){
                    mm.loop("collectionF" + (level/2 + 1));
                    currentSong = "collectionF" + (level/2 + 1);
                }

                if (!currentMino.active) {
                    alreadyHeld = false;
                    for (int i = 0; i < 4; i++) {
                        placedBlocks.add(currentMino.b[i]);
                    }

                    // Check for game over
                    if (currentMino.b[0].x == startX && currentMino.b[0].y == startY) {
                        gameState = GAME_OVER;
                    }
                    else {
                        currentMino.deactivating = false;
                        //Shift all pieces in the "Next" column up one unit
                        currentMino = nextMino1;
                        currentMino.setXY(startX, startY);
                        nextMino1 = nextMino2;
                        nextMino1.setXY(nextX1, nextY1);
                        nextMino2 = nextMino3;
                        nextMino2.setXY(nextX2, nextY2);
                        nextMino3 = nextMino4;
                        nextMino3.setXY(nextX3, nextY3);
                        nextMino4 = nextMino5;
                        nextMino4.setXY(nextX4, nextY4);
                        nextMino5 = nextMino6;
                        nextMino5.setXY(nextX5, nextY5);
                        nextMino6 = pickMino();
                        nextMino6.setXY(0, 0); // Position off-screen until it becomes the 5th next piece

                        clearLines();
                    }
                } else {
                    if (KeyHandler.shiftPressed && !alreadyHeld) { // Hold piece
                        alreadyHeld = true; // Player can only hold once until the current piece is dropped
                        if (!hold) {
                            holdMino = currentMino;
                            holdMino.setXY(leftX - 150, topY + 80);
                            currentMino = nextMino1;
                            currentMino.setXY(startX, startY);
                            nextMino1 = nextMino2;
                            nextMino1.setXY(nextX1, nextY1);
                            nextMino2 = nextMino3;
                            nextMino2.setXY(nextX2, nextY2);
                            nextMino3 = nextMino4;
                            nextMino3.setXY(nextX3, nextY3);
                            nextMino4 = nextMino5;
                            nextMino4.setXY(nextX4, nextY4);
                            nextMino5 = nextMino6;
                            nextMino5.setXY(nextX5, nextY5);
                            nextMino6 = pickMino();
                            nextMino6.setXY(0, 0); // Position off-screen until it becomes the 5th next piece
                            hold = true;
                        } else { // Switch the current piece with the piece in the hold slot
                            Mino temp = currentMino;
                            currentMino = holdMino;
                            currentMino.setXY(startX, startY);
                            holdMino = temp;
                            holdMino.setXY(leftX - 150, topY + 80);
                        }
                    }
                    if (gameState == PLAYING && KeyHandler.spacePressed) {
                        currentMino.hardDrop();
                        KeyHandler.spacePressed = false;  // reset so it only triggers once
                    }
                    currentMino.update();
                }
            }
            // Update message timer
            if (messageTimer > 0) {
                messageTimer--;
                if (messageTimer == 0) {
                    lineClearMessage = ""; // Clear message after duration
                }
            }
        }
        else if (gameState == MUSIC_SELECT){
            mm.stop();
            currentSong = "";
        }
        else if (gameState == GAME_OVER){
            mm.stop();
            currentSong = "";
            reset();
        }
        else if (gameState == SONGS){
            if(collection == 1){
                if (!"collectionA5".equals(currentSong) && song == 1) {
                    mm.stopLoop();
                    mm.loopSnippet("collectionA5", 40, 65, 1500);
                    currentSong = "collectionA5";
                }
                if (!"collectionA1".equals(currentSong) && song == 2){
                    mm.stopLoop();
                    mm.loopSnippet("collectionA1", 80, 110, 1500);
                    currentSong = "collectionA1";
                }
                if (!"collectionA2".equals(currentSong) && song == 3){
                    mm.stopLoop();
                    mm.loopSnippet("collectionA2", 40, 70, 1500);
                    currentSong = "collectionA2";
                }
                if (!"collectionA3".equals(currentSong) && song == 4){
                    mm.stopLoop();
                    mm.loopSnippet("collectionA3", 238, 268, 1500);
                    currentSong = "collectionA3";
                }
                if (!"collectionA4".equals(currentSong) && song == 5){
                    mm.stopLoop();
                    mm.loopSnippet("collectionA4", 191, 220, 1500);
                    currentSong = "collectionA4";
                }
                if (!"collectionA6".equals(currentSong) && song == 6){
                    mm.stopLoop();
                    mm.loopSnippet("collectionA6", 60, 90, 1500);
                    currentSong = "collectionA6";
                }
                if (!"collectionA7".equals(currentSong) && song == 7){
                    mm.stopLoop();
                    mm.loopSnippet("collectionA7", 150, 180, 1500);
                    currentSong = "collectionA7";
                }
                if (!"collectionA8".equals(currentSong) && song == 8){
                    mm.stopLoop();
                    mm.loopSnippet("collectionA8", 195, 225, 1500);
                    currentSong = "collectionA8";
                }
                if (!"collectionA9".equals(currentSong) && song == 9){
                    mm.stopLoop();
                    mm.loopSnippet("collectionA9", 245, 275, 1500);
                    currentSong = "collectionA9";
                }
                if (!"collectionA10".equals(currentSong) && song == 10){
                    mm.stopLoop();
                    mm.loopSnippet("collectionA10", 90, 120, 1500);
                    currentSong = "collectionA10";
                }
            }
            else if (collection == 2){
                if (!"collectionB1".equals(currentSong) && song == 1){
                    mm.stopLoop();
                    mm.loopSnippet("collectionB1", 30, 60, 1500);
                    currentSong = "collectionB1";
                }
                if (!"collectionB2".equals(currentSong) && song == 2){
                    mm.stopLoop();
                    mm.loopSnippet("collectionB2", 110, 140, 1500);
                    currentSong = "collectionB2";
                }
                if (!"collectionB3".equals(currentSong) && song == 3){
                    mm.stopLoop();
                    mm.loopSnippet("collectionB3", 105, 135, 1500);
                    currentSong = "collectionB3";
                }
                if (!"collectionB4".equals(currentSong) && song == 4){
                    mm.stopLoop();
                    mm.loopSnippet("collectionB4", 30, 60, 1500);
                    currentSong = "collectionB4";
                }
                if (!"collectionB5".equals(currentSong) && song == 5){
                    mm.stopLoop();
                    mm.loopSnippet("collectionB5", 90, 120, 1500);
                    currentSong = "collectionB5";
                }
                if (!"collectionB6".equals(currentSong) && song == 6){
                    mm.stopLoop();
                    mm.loopSnippet("collectionB6", 90, 120, 1500);
                    currentSong = "collectionB6";
                }
                if (!"collectionB7".equals(currentSong) && song == 7){
                    mm.stopLoop();
                    mm.loopSnippet("collectionB7", 165, 195, 1500);
                    currentSong = "collectionB7";
                }
                if (!"collectionB8".equals(currentSong) && song == 8){
                    mm.stopLoop();
                    mm.loopSnippet("collectionB8", 90, 120, 1500);
                    currentSong = "collectionB8";
                }
                if (!"collectionB9".equals(currentSong) && song == 9){
                    mm.stopLoop();
                    mm.loopSnippet("collectionB9", 90, 120, 1500);
                    currentSong = "collectionB9";
                }
                if (!"collectionB10".equals(currentSong) && song == 10){
                    mm.stopLoop();
                    mm.loopSnippet("collectionB10", 90, 120, 1500);
                    currentSong = "collectionB10";
                }
            }
            else if (collection == 3){
                if (!"collectionC1".equals(currentSong) && song == 1){
                    mm.stopLoop();
                    mm.loopSnippet("collectionC1", 90, 120, 1500);
                    currentSong = "collectionC1";
                }
                if (!"collectionC2".equals(currentSong) && song == 2){
                    mm.stopLoop();
                    mm.loopSnippet("collectionC2", 20, 50, 1500);
                    currentSong = "collectionC2";
                }
                if (!"collectionC3".equals(currentSong) && song == 3){
                    mm.stopLoop();
                    mm.loopSnippet("collectionC3", 50, 80, 1500);
                    currentSong = "collectionC3";
                }
                if (!"collectionC4".equals(currentSong) && song == 4){
                    mm.stopLoop();
                    mm.loopSnippet("collectionC4", 50, 80, 1500);
                    currentSong = "collectionC4";
                }
                if (!"collectionC5".equals(currentSong) && song == 5){
                    mm.stopLoop();
                    mm.loopSnippet("collectionC5", 85, 115, 1500);
                    currentSong = "collectionC5";
                }
                if (!"collectionC6".equals(currentSong) && song == 6){
                    mm.stopLoop();
                    mm.loopSnippet("collectionC6", 105, 135, 1500);
                    currentSong = "collectionC6";
                }
                if (!"collectionC7".equals(currentSong) && song == 7){
                    mm.stopLoop();
                    mm.loopSnippet("collectionC7", 105, 135, 1500);
                    currentSong = "collectionC7";
                }
                if (!"collectionC8".equals(currentSong) && song == 8){
                    mm.stopLoop();
                    mm.loopSnippet("collectionC8", 130, 160, 1500);
                    currentSong = "collectionC8";
                }
                if (!"collectionC9".equals(currentSong) && song == 9){
                    mm.stopLoop();
                    mm.loopSnippet("collectionC9", 110, 140, 1500);
                    currentSong = "collectionC9";
                }
                if (!"collectionC10".equals(currentSong) && song == 10){
                    mm.stopLoop();
                    mm.loopSnippet("collectionC10", 200, 230, 1500);
                    currentSong = "collectionC10";
                }
            }
            else if (collection == 4){
                if (!"collectionD2".equals(currentSong) && song == 1){
                    mm.stopLoop();
                    mm.loopSnippet("collectionD2", 90, 120, 1500);
                    currentSong = "collectionD2";
                }
                if (!"collectionD3".equals(currentSong) && song == 2){
                    mm.stopLoop();
                    mm.loopSnippet("collectionD3", 90, 120, 1500);
                    currentSong = "collectionD3";
                }
                if (!"collectionD4".equals(currentSong) && song == 3){
                    mm.stopLoop();
                    mm.loopSnippet("collectionD4", 75, 105, 1500);
                    currentSong = "collectionD4";
                }
                if (!"collectionD5".equals(currentSong) && song == 4){
                    mm.stopLoop();
                    mm.loopSnippet("collectionD5", 90, 120, 1500);
                    currentSong = "collectionD5";
                }
                if (!"collectionD6".equals(currentSong) && song == 5){
                    mm.stopLoop();
                    mm.loopSnippet("collectionD6", 90, 120, 1500);
                    currentSong = "collectionD6";
                }
                if (!"collectionD7".equals(currentSong) && song == 6){
                    mm.stopLoop();
                    mm.loopSnippet("collectionD7", 110, 140, 1500);
                    currentSong = "collectionD7";
                }
                if (!"collectionD1".equals(currentSong) && song == 7){
                    mm.stopLoop();
                    mm.loopSnippet("collectionD1", 90, 120, 1500);
                    currentSong = "collectionD1";
                }
                if (!"collectionD8".equals(currentSong) && song == 8){
                    mm.stopLoop();
                    mm.loopSnippet("collectionD8", 90, 120, 1500);
                    currentSong = "collectionD8";
                }
                if (!"collectionD9".equals(currentSong) && song == 9){
                    mm.stopLoop();
                    mm.loopSnippet("collectionD9", 80, 110, 1500);
                    currentSong = "collectionD9";
                }
                if (!"collectionD10".equals(currentSong) && song == 10){
                    mm.stopLoop();
                    mm.loopSnippet("collectionD10", 30, 60, 1500);
                    currentSong = "collectionD10";
                }
            }
            else if (collection == 5){
                if (!"collectionE1".equals(currentSong) && song == 1){
                    mm.stopLoop();
                    mm.loopSnippet("collectionE1", 90, 120, 1500);
                    currentSong = "collectionE1";
                }
                if (!"collectionE2".equals(currentSong) && song == 2){
                    mm.stopLoop();
                    mm.loopSnippet("collectionE2", 60, 90, 1500);
                    currentSong = "collectionE2";
                }
                if (!"collectionE3".equals(currentSong) && song == 3){
                    mm.stopLoop();
                    mm.loopSnippet("collectionE3", 55, 85, 1500);
                    currentSong = "collectionE3";
                }
                if (!"collectionE4".equals(currentSong) && song == 4){
                    mm.stopLoop();
                    mm.loopSnippet("collectionE4", 200, 230, 1500);
                    currentSong = "collectionE4";
                }
                if (!"collectionE5".equals(currentSong) && song == 5){
                    mm.stopLoop();
                    mm.loopSnippet("collectionE5", 45, 75, 1500);
                    currentSong = "collectionE5";
                }
                if (!"collectionE6".equals(currentSong) && song == 6){
                    mm.stopLoop();
                    mm.loopSnippet("collectionE6", 60, 90, 1500);
                    currentSong = "collectionE6";
                }
                if (!"collectionE7".equals(currentSong) && song == 7){
                    mm.stopLoop();
                    mm.loopSnippet("collectionE7", 90, 120, 1500);
                    currentSong = "collectionE7";
                }
                if (!"collectionE8".equals(currentSong) && song == 8){
                    mm.stopLoop();
                    mm.loopSnippet("collectionE8", 90, 120, 1500);
                    currentSong = "collectionE8";
                }
                if (!"collectionE9".equals(currentSong) && song == 9){
                    mm.stopLoop();
                    mm.loopSnippet("collectionE9", 90, 120, 1500);
                    currentSong = "collectionE9";
                }
                if (!"collectionE10".equals(currentSong) && song == 10){
                    mm.stopLoop();
                    mm.loopSnippet("collectionE10", 60, 90, 1500);
                    currentSong = "collectionE10";
                }
            }
            else if(collection == 6){
                if (!"collectionF1".equals(currentSong) && song == 1){
                    mm.stopLoop();
                    mm.loopSnippet("collectionF1", 100, 130, 1500);
                    currentSong = "collectionF1";
                }
                if (!"collectionF2".equals(currentSong) && song == 2){
                    mm.stopLoop();
                    mm.loopSnippet("collectionF2", 20, 50, 1500);
                    currentSong = "collectionF2";
                }
                if (!"collectionF3".equals(currentSong) && song == 3){
                    mm.stopLoop();
                    mm.loopSnippet("collectionF3", 100, 130, 1500);
                    currentSong = "collectionF3";
                }
                if (!"collectionF4".equals(currentSong) && song == 4){
                    mm.stopLoop();
                    mm.loopSnippet("collectionF4", 60, 90, 1500);
                    currentSong = "collectionF4";
                }
                if (!"collectionF5".equals(currentSong) && song == 5){
                    mm.stopLoop();
                    mm.loopSnippet("collectionF5", 60, 90, 1500);
                    currentSong = "collectionF5";
                }
                if (!"collectionF6".equals(currentSong) && song == 6){
                    mm.stopLoop();
                    mm.loopSnippet("collectionF6", 60, 90, 1500);
                    currentSong = "collectionF6";
                }
                if (!"collectionF7".equals(currentSong) && song == 7){
                    mm.stopLoop();
                    mm.loopSnippet("collectionF7", 60, 90, 1500);
                    currentSong = "collectionF7";
                }
                if (!"collectionF8".equals(currentSong) && song == 8){
                    mm.stopLoop();
                    mm.loopSnippet("collectionF8", 60, 90, 1500);
                    currentSong = "collectionF8";
                }
                if (!"collectionF9".equals(currentSong) && song == 9){
                    mm.stopLoop();
                    mm.loopSnippet("collectionF9", 75, 105, 1500);
                    currentSong = "collectionF9";
                }
                if (!"collectionF10".equals(currentSong) && song == 10){
                    mm.stopLoop();
                    mm.loopSnippet("collectionF10", 70, 100, 1500);
                    currentSong = "collectionF10";
                }
            }
            else if (collection == 7){
                if (!"collectionG1".equals(currentSong) && song == 1){
                    mm.stopLoop();
                    mm.loopSnippet("collectionG1", 60, 90, 1500);
                    currentSong = "collectionG1";
                }
                if (!"collectionG2".equals(currentSong) && song == 2){
                    mm.stopLoop();
                    mm.loopSnippet("collectionG2", 90, 120, 1500);
                    currentSong = "collectionG2";
                }
                if (!"collectionG3".equals(currentSong) && song == 3){
                    mm.stopLoop();
                    mm.loopSnippet("collectionG3", 90, 120, 1500);
                    currentSong = "collectionG3";
                }
                if (!"collectionG4".equals(currentSong) && song == 4){
                    mm.stopLoop();
                    mm.loopSnippet("collectionG4", 110, 140, 1500);
                    currentSong = "collectionG4";
                }
                if (!"collectionG5".equals(currentSong) && song == 5){
                    mm.stopLoop();
                    mm.loopSnippet("collectionG5", 90, 120, 1500);
                    currentSong = "collectionG5";
                }
                if (!"collectionG6".equals(currentSong) && song == 6){
                    mm.stopLoop();
                    mm.loopSnippet("collectionG6", 90, 120, 1500);
                    currentSong = "collectionG6";
                }
                if (!"collectionG7".equals(currentSong) && song == 7){
                    mm.stopLoop();
                    mm.loopSnippet("collectionG7", 45, 75, 1500);
                    currentSong = "collectionG7";
                }
                if (!"collectionG8".equals(currentSong) && song == 8){
                    mm.stopLoop();
                    mm.loopSnippet("collectionG8", 30, 60, 1500);
                    currentSong = "collectionG8";
                }
                if (!"collectionG9".equals(currentSong) && song == 9){
                    mm.stopLoop();
                    mm.loopSnippet("collectionG9", 90, 120, 1500);
                    currentSong = "collectionG9";
                }
                if (!"collectionG10".equals(currentSong) && song == 10){
                    mm.stopLoop();
                    mm.loopSnippet("collectionG10", 90, 120, 1500);
                    currentSong = "collectionG10";
                }
            }
        }
        else if (gameState == CREDITS){
            if(!currentSong.equals("credits")) {
                mm.stop();
                mm.loop("credits");
                currentSong = "credits";
            }
        }
        else if (gameState == INSTRUCTIONS){
            if(!currentSong.equals("instructions")) {
                mm.stop();
                mm.loop("instructions");
                currentSong = "instructions";
            }
        }
        else if(gameState == OTHER){
            if(!currentSong.equals("other")) {
                mm.stop();
                mm.loop("other");
                currentSong = "other";
            }
        }
    }
    public void clearLines() {
        int y = bottomY - Block.SIZE;
        while (y > topY) {
            int blockCount = 0;

            // Count blocks and mark which indices to remove
            for (int i = 0; i < placedBlocks.size(); i++) {
                if (placedBlocks.get(i).y == y) {
                    blockCount++;
                }
            }

            if (blockCount == 10) { // full row
                // Remove blocks in this row (loop backwards)
                for (int i = placedBlocks.size() - 1; i >= 0; i--) {
                    if (placedBlocks.get(i).y == y) {
                        placedBlocks.remove(i);
                    }
                }
                lines++;
                linesCleared++;
                handleLineClear(linesCleared);
                if(lines % 10 == 0 && dropInterval > 1){
                    level++;
                    if(dropInterval > 10) dropInterval -= 10;
                    else if (dropInterval > 5) dropInterval -= 5;
                    else dropInterval--;
                }
                // Drop blocks above
                for (int i = 0; i < placedBlocks.size(); i++) {
                    if (placedBlocks.get(i).y < y) {
                        placedBlocks.get(i).y += Block.SIZE;
                    }
                }
            } else {
                y -= Block.SIZE; // move up
            }
        }
        linesCleared = 0; // Reset for next potential clear
    }
    public void draw(Graphics2D g2) {
        Image img;
        if(gameState == MENU){
            if(menu != null){
                g2.drawImage(menu, 0, 0 , 1280, 720, null);
            }
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(4f));
            g2.setFont(new Font("Arial", Font.PLAIN, 20));
            g2.drawString("Music Select", 20, 20);
            g2.drawString("Credits", 200, 20);
            g2.drawString("Instructions", 350, 20);
            g2.drawString("Other", 500, 20);
            g2.setFont(new Font("Sans Serif Collection", Font.PLAIN, 16));
            g2.drawString("Now Playing:", 880, 580);
            g2.drawString("by uma vs. Morimori Atsushi", 880, 680);
            g2.setFont(new Font("Sans Serif Collection", Font.BOLD, 40));
            g2.drawString("Re: End of a Dream", 880, 635);
            g2.setColor(Color.BLUE);
            g2.drawRect(musicSelectButton.x, musicSelectButton.y, musicSelectButton.width, musicSelectButton.height);
            g2.drawRect(credsButton.x, credsButton.y, credsButton.width, credsButton.height);
            g2.drawRect(insButton.x, insButton.y, insButton.width, insButton.height);
            g2.drawRect(otherButton.x, otherButton.y, otherButton.width, otherButton.height);
        }
        if(gameState == PLAYING) {
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(4f));
            g2.drawRect(leftX - 4, topY - 4, WIDTH + 8, HEIGHT + 8);

            int x = rightX + 100;
            int y = topY;
            g2.drawRect(x, y, 200, 570);
            g2.setFont(new Font("Arial", Font.PLAIN, 30));
            g2.drawString("NEXT", x + 20, y + 40);

            int holdX = leftX - 210;
            int holdY = topY;
            g2.drawRect(holdX, holdY, 200, 200);
            g2.setFont(new Font("Arial", Font.PLAIN, 30));
            g2.drawString("HOLD", holdX + 20, holdY + 40);

            //Draw level and lines cleared
            g2.setFont(new Font("Arial", Font.PLAIN, 30));
            g2.drawString("LINES: " + lines, leftX - 250, topY + 550);
            g2.drawString("LEVEL: " + level, leftX - 250, topY + 600);
            g2.drawString("SCORE: " + score, leftX - 250, topY + 650);

            // Draw line clear message if active
            if (!lineClearMessage.isEmpty()) {
                g2.setFont(new Font("Arial", Font.BOLD, 40));
                g2.setColor(Color.WHITE);
                g2.drawString(lineClearMessage, leftX - 200, topY + 300);
            }

            if(!spinMessage.isEmpty()){
                g2.setColor(Color.MAGENTA);
                g2.setFont(new Font("Arial", Font.BOLD, 25));
                g2.drawString(spinMessage, leftX - 200, topY + 250);
            }

            if(!pcMessage.isEmpty()){
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 50));
                g2.drawString(pcMessage, leftX + 20, topY + 400);
            }

            if(b2b > 0){
                if(b2b < 5) {
                    g2.setColor(Color.WHITE);
                }
                else if (b2b < 10){
                    g2.setColor(Color.CYAN);
                }
                else if (b2b < 15){
                    g2.setColor(Color.GREEN);
                }
                else if (b2b < 25){
                    g2.setColor(Color.ORANGE);
                }
                else if (b2b < 30){
                    g2.setColor(Color.RED);
                }
                else if (b2b < 40){
                    g2.setColor(Color.PINK);
                }
                else{
                    g2.setColor(Color.MAGENTA);
                }
                g2.setFont(new Font("Arial", Font.BOLD, 25));
                g2.drawString("B2B x" + b2b, leftX - 200, topY + 350);
            }

            if (currentMino != null) {
                currentMino.draw(g2);
            }
            if (nextMino1 != null) {
                nextMino1.draw(g2);
            }
            if (nextMino2 != null) {
                nextMino2.draw(g2);
            }
            if (nextMino3 != null) {
                nextMino3.draw(g2);
            }
            if (nextMino4 != null) {
                nextMino4.draw(g2);
            }
            if (nextMino5 != null) {
                nextMino5.draw(g2);
            }
            for (Block block : placedBlocks) {
                block.draw(g2);
            }
            if (KeyHandler.pausePressed) {
                g2.setColor(new Color(0, 0, 0, 200));
                g2.fillRect(0, 0, Board.WIDTH, Board.HEIGHT);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.PLAIN, 80));
                g2.drawString("PAUSED", Board.WIDTH / 2 - 150, Board.HEIGHT / 2);
            }
            if (holdMino != null) {
                holdMino.draw(g2);
            }
            if(collection == 1){}
            if(collection == 2){}
            if(collection == 3){}
            if(collection == 4){}
            if(collection == 5){}
            if(collection == 6){}

        }
        if (gameState == MUSIC_SELECT) {
            g2.drawImage(musicSelect, 0, 0, 1280, 720, null);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.PLAIN, 20));
            g2.drawString("Back", 20, 20);
            g2.drawString("Select", select.x + 5, select.y + 20);
            g2.drawRect(select.x, select.y, select.width, select.height);
            g2.setFont(new Font("Arial", Font.BOLD, 50));
            g2.drawString("Music Collection 1", 20, 100);
            g2.drawString("Music Collection 2", 20, 200);
            g2.drawString("Music Collection 3", 20, 300);
            g2.drawString("Music Collection 4", 20, 400);
            g2.drawString("Music Collection 5", 20, 500);
            g2.drawString("Music Collection 6", 20, 600);
            g2.drawString("Music Collection 7", 20, 700);
            g2.setFont(new Font("Tahoma", Font.BOLD, 100));
            g2.drawString("--- MUSIC", 640, 150);
            g2.drawString("SELECT---", 720, 270);
            if(selectionActivated){
                g2.setFont(new Font("Tahoma", Font.BOLD, 60));
                g2.drawString("Click to select", 640, 500);
                if(hover){
                    g2.setColor(Color.BLUE);
                    switch (hoveredCollection){
                        case 1:
                            g2.drawRect(mc1.x, mc1.y, mc1.width, mc1.height);
                            break;
                        case 2:
                            g2.drawRect(mc2.x, mc2.y, mc2.width, mc2.height);
                            break;
                        case 3:
                            g2.drawRect(mc3.x, mc3.y, mc3.width, mc3.height);
                            break;
                        case 4:
                            g2.drawRect(mc4.x, mc4.y, mc4.width, mc4.height);
                            break;
                        case 5:
                            g2.drawRect(mc5.x, mc5.y, mc5.width, mc5.height);
                            break;
                        case 6:
                            g2.drawRect(mc6.x, mc6.y, mc6.width, mc6.height);
                            break;
                        case 7:
                            g2.drawRect(mc7.x, mc7.y, mc7.width, mc7.height);
                            break;
                    }
                }
            }
        }
        if(gameState == SONGS){
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 20));
            g2.drawRect(placeholder.x, placeholder.y, placeholder.width, placeholder.height);
            g2.drawRect(stuff.x, stuff.y, stuff.width, stuff.height);
            g2.drawString("Track", stuff.x + 25, stuff.y + 20);
            g2.setFont(new Font("SansSerif", Font.BOLD, 60));
            g2.drawString(song + "", stuff.x + 30, stuff.y + 80);
            g2.setFont(new Font("Arial", Font.PLAIN, 20));

            if(collection == 1){
                img = im.getImage("A" + currentBackground);
                g2.drawImage(img, 470, 150, 720, 405, null);
                g2.drawString("Music Collection 1", 560, 25);
                g2.setFont(new Font("Tahoma", Font.PLAIN, 25));
                g2.drawString("by Camellia", placeholder.x+15, placeholder.y + 85);
                g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                if(song == 1) g2.drawString("#1f1e33", placeholder.x + 15, placeholder.y + 50);
                if(song == 2) g2.drawString("crystallized", placeholder.x + 15, placeholder.y + 50);
                if(song == 3) {
                    g2.setFont(new Font("Tahoma", Font.BOLD, 25));
                    g2.drawString("Dance With Silence", placeholder.x + 15, placeholder.y + 35);
                }
                if(song == 4){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 20));
                    g2.drawString("Exit This Earth's", placeholder.x + 15, placeholder.y + 30);
                    g2.drawString("Atomosphere", placeholder.x + 15, placeholder.y + 52);
                }
                if(song == 5) g2.drawString("GHOST", placeholder.x + 15, placeholder.y + 50);
                if(song == 6) g2.drawString("GHOUL", placeholder.x + 15, placeholder.y + 50);
                if(song == 7) g2.drawString("Light it up", placeholder.x + 15, placeholder.y + 50);
                if(song == 8) g2.drawString("Quaoar", placeholder.x + 15, placeholder.y + 50);
                if(song == 9){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 20));
                    g2.drawString("+ERABY+E", placeholder.x + 15, placeholder.y + 25);
                    g2.drawString("C0NNEC+10N", placeholder.x + 15, placeholder.y + 50);
                }
                if(song == 10){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 20));
                    g2.drawString("We Magicians Still", placeholder.x + 15, placeholder.y + 25);
                    g2.drawString("Alive in 2021", placeholder.x + 15, placeholder.y + 50);
                }
            }
            if(collection == 2){
                img = im.getImage("B" + currentBackground);
                g2.drawImage(img, 470, 150, 720, 405, null);
                g2.drawString("Music Collection 2", 560, 30);
                if(song == 1){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("Ai no Sukima", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by MIMI ft. Hatsune Miku", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 2){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 25));
                    g2.drawString("Ascension to Heaven", placeholder.x + 15, placeholder.y + 35);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by xi", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 3){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("Blue Zenith", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by xi", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 4){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("CENSORED!!!", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by t+pazolite", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 5){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 20));
                    g2.drawString("Everything Will", placeholder.x + 15, placeholder.y + 30);
                    g2.drawString("Freeze", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Undead Corporation", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 6){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 30));
                    g2.drawString("FREEDOM DiVE", placeholder.x + 15, placeholder.y + 40);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by xi", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 7){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 25));
                    g2.drawString("Galaxy Collapse", placeholder.x + 15, placeholder.y + 35);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Kurokotei", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 8){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 25));
                    g2.drawString("Harumachi Clover", placeholder.x + 15, placeholder.y + 35);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Ichimichi Mao", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 9){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("Lagtrain", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by inabakumori & Kaai Yuki", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 10){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("No Title", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Reol", placeholder.x + 15, placeholder.y + 85);
                }
            }
            if(collection == 3){
                img = im.getImage("C" + currentBackground);
                g2.drawImage(img, 470, 150, 720, 405, null);
                g2.drawString("Music Collection 3", 560, 30);
                if(song == 1){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 25));
                    g2.drawString("DESTRUCTION 3,2,1", placeholder.x + 15, placeholder.y + 35);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 15));
                    g2.drawString("by Normal1zer vs. Broken Nerdz", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 2){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 30));
                    g2.drawString("Distorted Fate", placeholder.x + 15, placeholder.y + 40);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Sakuzyo", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 3){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("Igallta", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Se-U-Ra", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 4){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 35));
                    g2.drawString("GOODWORLD", placeholder.x + 15, placeholder.y + 30);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by EBIMAYO", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 5){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("GOODRAGE", placeholder.x + 15, placeholder.y + 35);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by EBIMAYO", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 6){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("Rrhar'il", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Team Grimoire", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 7){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 30));
                    g2.drawString("Stardust: RAY", placeholder.x + 15, placeholder.y + 40);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by kanone vs. BlackY", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 8){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("WATER", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by A-39 ft. Hatsune Miku", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 9){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 35));
                    g2.drawString("Wavetapper", placeholder.x + 15, placeholder.y + 45);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Frums", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 10){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 20));
                    g2.drawString("You are the", placeholder.x + 15, placeholder.y + 30);
                    g2.drawString("Miserable", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by t+pazolite", placeholder.x + 15, placeholder.y + 85);
                }
            }
            if(collection == 4){
                img = im.getImage("D" + currentBackground);
                g2.drawImage(img, 470, 150, 720, 405, null);
                g2.drawString("Music Collection 4", 560, 30);
                if(song == 1){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("Isolation", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Nighthawk22", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 2){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("Nhelv", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Silentroom", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 3){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("Pagoda", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Xomu & Amidst", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 4){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("Reanimate", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Warak", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 5){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 30));
                    g2.drawString("Shiawase - VIP", placeholder.x + 15, placeholder.y + 40);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Dion Timmer", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 6){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("Skystrike", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Hinkik", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 7){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 20));
                    g2.drawString("At the Speed", placeholder.x + 15, placeholder.y + 30);
                    g2.drawString("of Light", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Dimrain47", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 8){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("The Calling", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 18));
                    g2.drawString("by TheFatRat x Laura Brehm", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 9){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 35));
                    g2.drawString("Time Leaper", placeholder.x + 15, placeholder.y + 45);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Hinkik", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 10){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("Unity", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by TheFatRat", placeholder.x + 15, placeholder.y + 85);
                }
            }
            if(collection == 5){
                img = im.getImage("E" + currentBackground);
                g2.drawImage(img, 470, 150, 720, 405, null);
                g2.drawString("Music Collection 5", 560, 30);
                if(song == 1){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 20));
                    g2.drawString("Brain Fluid", placeholder.x + 15, placeholder.y + 30);
                    g2.drawString("Explosion Girl", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by rerulili ft. GUMI", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 2){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 20));
                    g2.drawString("Golden (from KPop", placeholder.x + 15, placeholder.y + 30);
                    g2.drawString("Demon Hunters)", placeholder.x+15, placeholder.y+50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by HUNTR/X", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 3){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("Idol", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by YOASOBI", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 4){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 35));
                    g2.drawString("Into the Night", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by YOASOBI", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 5){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 35));
                    g2.drawString("Kaikai Kitan", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Eve", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 6){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 35));
                    g2.drawString("Kyu-kurarin", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by iyowa ft. Kafu", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 7){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 20));
                    g2.drawString("Machine Gun Poem Doll", placeholder.x + 15, placeholder.y + 30);
                    g2.drawString("(from Project Sekai)", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 15));
                    g2.drawString("by cosMo@Bousou-P ft. Hatsune Miku", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 8){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 35));
                    g2.drawString("Night Dancer", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by imase", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 9){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("Otonoke", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Creepy Nuts", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 10){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("ROKI", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 18));
                    g2.drawString("by mikitoP ft. Kagamine Rin", placeholder.x + 15, placeholder.y + 85);
                }
            }
            if(collection == 6){
                img = im.getImage("F" + currentBackground);
                g2.drawImage(img, 470, 150, 720, 405, null);
                g2.drawString("Music Collection 6", 560, 30);
            }
            if(collection == 7){
                img = im.getImage("G" + currentBackground);
                g2.drawImage(img, 470, 150, 720, 405, null);
                g2.drawString("Music Collection 7", 560, 30);
            }
            g2.setFont(new Font("Arial", Font.BOLD, 80));
            g2.drawString("<", 20, 350);
            g2.drawString(">", 1200, 350);
            g2.setFont(new Font("Arial", Font.PLAIN, 20));
            g2.drawString("Back", 20, 20);

            g2.setColor(Color.GREEN);
            g2.drawRect(leftButton.x, leftButton.y, leftButton.width, leftButton.height);
            g2.drawRect(rightButton.x, rightButton.y, rightButton.width, rightButton.height);
            g2.setFont(new Font("Arial", Font.PLAIN, 20));
            g2.setColor(Color.WHITE);

        }
        if (gameState == GAME_OVER) {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.PLAIN, 80));
            g2.drawString("GAME OVER", Board.WIDTH / 2 - 220, Board.HEIGHT / 2);
        }
        if(gameState == INSTRUCTIONS){
            if(page == 1){
                img = im.getImage("ins1");
                g2.drawImage(img, 0, 0, 1280, 720, null);
            }
            if(page == 2){
                img = im.getImage("ins2");
                g2.drawImage(img, 0, 0, 1280, 720, null);
            }
            if(page == 3){
                img = im.getImage("ins3");
                g2.drawImage(img, 0, 0, 1280, 720, null);
            }
            if(page == 4){
                img = im.getImage("ins4");
                g2.drawImage(img, 0, 0, 1280, 720, null);
            }
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 80));
            g2.drawString("<", 20, 350);
            g2.drawString(">", 1200, 350);
            g2.setFont(new Font("Arial", Font.PLAIN, 20));
            g2.drawString("Back", 20, 20);
        }
        if(gameState == CREDITS){
            if(page == 1) {
                g2.drawImage(credits, 0, 0, 1280, 720, null);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Sans Serif Collection", Font.BOLD, 100));
                g2.drawString("CREDITS!!!", 100, 100);
                g2.setFont(new Font("Sans Serif Collection", Font.BOLD, 20));
                g2.drawString("Now Playing:", 1000, 550);
                g2.drawString("by LeaF", 1000, 650);
                g2.setFont(new Font("Sans Serif Collection", Font.BOLD, 60));
                g2.drawString("Aleph-0", 1000, 615);
            }
            if(page == 2){
                img = im.getImage("credits1");
                g2.drawImage(img, 0, 0, 1280, 720, null);
            }
            if(page == 3){
                img = im.getImage("credits2");
                g2.drawImage(img, 0, 0, 1280, 720, null);
            }
            if(page == 4){
                img = im.getImage("credits3");
                g2.drawImage(img, 0, 0, 1280, 720, null);
            }
            if(page == 5){
                img = im.getImage("credits4");
                g2.drawImage(img, 0, 0, 1280, 720, null);
            }
            if(page == 6){
                img = im.getImage("credits5");
                g2.drawImage(img, 0, 0, 1280, 720, null);
            }
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 80));
            g2.drawString("<", 20, 350);
            g2.drawString(">", 1200, 350);
            g2.setFont(new Font("Arial", Font.PLAIN, 20));
            g2.drawString("Back", 20, 20);
        }
        if(gameState == OTHER){
            if(page == 1){
                img = im.getImage("other1");
                g2.drawImage(img, 0, 0, 1280, 720, null);
            }
            if(page == 2){
                img = im.getImage("other2");
                g2.drawImage(img, 0, 0, 1280, 720, null);
            }
            if(page == 3){
                img = im.getImage("other3");
                g2.drawImage(img, 0, 0, 1280, 720, null);
            }
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 80));
            g2.drawString("<", 20, 350);
            g2.drawString(">", 1200, 350);
            g2.setFont(new Font("Arial", Font.PLAIN, 20));
            g2.drawString("Back", 20, 20);
        }
        else if (gameState == INITIALIZE){
            img = im.getImage("initialize1");
            g2.drawImage(img, 0, 0, 1280, 720, null);
        }
    }
    public void reset(){
        placedBlocks.clear();
        startingMinos.clear();
        currentMino = null;
        lines = 0;
        level = 1;
        // Adding the starting bag of pieces (each piece must appear )
        startingMinos.add(new LPiece());
        startingMinos.add(new JPiece());
        startingMinos.add(new IPiece());
        startingMinos.add(new SPiece());
        startingMinos.add(new ZPiece());
        startingMinos.add(new TPiece());
        startingMinos.add(new OPiece());

        Collections.shuffle(startingMinos); // Randomizing the starting bag

        currentMino = startingMinos.get(0); // Starting piece
        currentMino.setXY(startX, startY);

        nextMino1 = startingMinos.get(1);
        nextMino1.setXY(nextX1, nextY1);
        nextMino2 = startingMinos.get(2);
        nextMino2.setXY(nextX2, nextY2);
        nextMino3 = startingMinos.get(3);
        nextMino3.setXY(nextX3, nextY3);
        nextMino4 = startingMinos.get(4);
        nextMino4.setXY(nextX4, nextY4);
        nextMino5 = startingMinos.get(5);
        nextMino5.setXY(nextX5, nextY5);
        nextMino6 = startingMinos.get(6);
    }
    public void saveSongCollection(int collection) {
        try (PrintWriter out = new PrintWriter(new FileWriter("save.txt"))) {
            out.println(collection);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
