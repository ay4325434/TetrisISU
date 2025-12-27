import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.StringTokenizer;

public class GameManager{

    //Game window properties
    public static final int WIDTH = 300;
    public static final int HEIGHT = 660;
    public static int leftX;
    public static int topY;
    public static int rightX;
    public static int bottomY;

    private int lines = 0;
    private int linesCleared = 0;
    private int level = 1;
    private int score = 0;

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
    private int dropInterval = 60;

    private ArrayList<Block> placedBlocks = new ArrayList<>();
    private ArrayList<Mino> startingMinos = new ArrayList<>();
    private ArrayList<Mino> minos = new ArrayList<>(startingMinos);

    private boolean hold = false;
    private boolean alreadyHeld = false;
    private Mino holdMino = null;
    private boolean selectionActivated = false;

    private Map<GameState, Integer> pages = new HashMap<>();
    private Map<Rectangle, Integer> collectionAreas = new HashMap<>();

    private ArrayList<Player> scores = new ArrayList<>();


    //Game states
    public enum GameState {
        MENU,
        PLAYING,
        GAME_OVER,
        INSTRUCTIONS,
        MUSIC_SELECT,
        CREDITS,
        SONGS,
        OTHER,
        INITIALIZE
    }
    private GameState gameState = GameState.MENU;

    private int currentBackground = 1; // Iterates through the music collections
    private int song = 1;
    private int collection = 1;

    private static final int SONGS = 10;

    private MusicManager mm = new MusicManager();
    private String currentSong = "";
    private ImageManager im = new ImageManager();

    // Buttons for user selection
    public Rectangle musicSelectButton = new Rectangle(0, 0, 170, 30);
    public Rectangle backButton = new Rectangle(0, 0, 50, 30);
    public Rectangle credsButton = new Rectangle(180, 0, 100, 30);
    public Rectangle insButton = new Rectangle(330, 0, 140, 30);
    public Rectangle otherButton = new Rectangle(480, 0, 100, 30);
    public Rectangle scoreButton = new Rectangle(600, 0, 100, 30);
    public Rectangle mc1 = new Rectangle(0, 40, 500, 80);
    public Rectangle mc2 = new Rectangle(0, 140, 500, 80);
    public Rectangle mc3 = new Rectangle(0, 240, 500, 80);
    public Rectangle mc4 = new Rectangle(0, 340, 500, 80);
    public Rectangle mc5 = new Rectangle(0, 440, 500, 80);
    public Rectangle mc6 = new Rectangle(0, 540, 500, 80);
    public Rectangle mc7 = new Rectangle(0, 640, 500, 80);
    public Rectangle mc8 = new Rectangle(550, 40, 500, 80);
    public Rectangle mc9 = new Rectangle(550, 140, 500, 80);
    public Rectangle mc10 = new Rectangle(550, 240, 500, 80);
    public Rectangle select = new Rectangle(80, 0, 80, 30);

    public Rectangle leftButton = new Rectangle(20, 220, 50, 200);
    public Rectangle rightButton = new Rectangle(1200, 220, 50, 200);
    public Rectangle placeholder = new Rectangle(80, 280, 380, 100);
    public Rectangle stuff = new Rectangle(360, 280, 100, 100);

    public Image menu, musicSelect, credits;

    // When in the playing state, keeps track of the current song.
    private int page = 1;
    private int playCollection = 1;

    // Music selection properties
    private boolean hover = false;
    private int hoveredCollection = 1;

    // In Tetris, the B2B combo and normal combos actually start at -1, not 0.
    private int b2b = -1;
    private int combo = -1;

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

        pages.put(GameState.CREDITS, 6);
        pages.put(GameState.INSTRUCTIONS, 4);
        pages.put(GameState.OTHER, 3);

        initCollections();
        reset();
        readScores();

        try{
            menu = ImageIO.read(new File("Images/menu.png"));
            musicSelect = ImageIO.read(new File("Images/musicselect.png"));
            credits = ImageIO.read(new File("Images/credits.png"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image");
        }

    }
    /**
     * Picks a random piece after the starting bag has been used up.
     * @return The next piece in queue that is not displayed yet
     */
    private Mino pickMino(){
        Mino m;
        if(!minos.isEmpty()){
            m = minos.removeFirst();
        }
        else{
            // Restablishing the bag of 7 different pieces
            minos.add(new LPiece(this));
            minos.add(new JPiece(this));
            minos.add(new IPiece(this));
            minos.add(new SPiece(this));
            minos.add(new ZPiece(this));
            minos.add(new TPiece(this));
            minos.add(new OPiece(this));
            m = minos.removeFirst();
        }
        return m;
    }

    private String lineClearMessage = ""; // message to display
    private String spinMessage = "";
    private String pcMessage = "";
    private int messageTimer = 0;         // counts frames or updates
    private int spinMessageTimer = 0;
    private final int MESSAGE_DURATION = 120; // frames to display (2 seconds at 60 FPS)

    /**
     * Handles line clearing and perfect clears.
     * @param linesCleared
     */
    public void handleLineClear(int linesCleared) {
        if(checkForTSpin()){ // Piece spin detected
            if(currentMino.direction == 1){
                spinMessage = "Mini " + currentMino.type + "-Spin";
            }
            else{
                spinMessage = currentMino.type + "-Spin";
            }
        }
        if(checkForAllClear()){
            pcMessage = "ALL CLEAR";
        }
        StringTokenizer st = new StringTokenizer(spinMessage, " "); // check to see if a mini or a regular spin occurred
        switch(linesCleared) {
            case 1:
                lineClearMessage = "SINGLE";
                // Score calculation
                if(checkForTSpin() || checkForAllClear()){
                    b2b++;
                    if(!st.nextToken().equalsIgnoreCase("Mini")){
                        if(b2b > 1) {
                            score += (100 * b2b * level * 5);
                        }
                        else{
                            score += 100 * level * 5;
                        }
                    }
                    if(checkForAllClear()){
                        if(b2b > 1) {
                            score += (100 * b2b * level * 20);
                        }
                        else {
                            score += (100 * level * 20);
                        }
                    }
                }
                else {
                    score += 100 * level;
                    if(b2b > 4){
                        score *= b2b;
                        b2b = -1;
                    }
                }
                combo++;
                if(combo > 1){
                    score += combo * level;
                }
                break;
            case 2:
                lineClearMessage = "DOUBLE";
                if(checkForTSpin() || checkForAllClear()){
                    b2b++;
                    if(!st.nextToken().equalsIgnoreCase("Mini")){
                        if(b2b > 1) {
                            score += (200 * b2b * level * 5);
                        }
                        else{
                            score += 200 * level * 5;
                        }
                    }
                    if(checkForAllClear()){
                        if(b2b > 1) {
                            score += (200 * b2b * level * 20);
                        }
                        else {
                            score += (200 * level * 20);
                        }
                    }
                }
                else {
                    if(b2b > 4){
                        score *= b2b;
                        b2b = -1;
                    }
                }
                combo++;
                if(combo > 1){
                    score += combo * level;
                }
                break;
            case 3:
                if(!st.nextToken().equalsIgnoreCase("Mini")){
                    b2b++;
                    if(spinMessage.equals("T-Spin")){
                        if(b2b > 1) {
                            score += (400 * b2b * level * 5);
                        }
                        else{
                            score += 400 * level * 5;
                        }
                    }
                    if(checkForAllClear()){
                        if(b2b > 1) {
                            score += (400 * b2b * level * 20);
                        }
                        else {
                            score += (400 * level * 20);
                        }
                    }
                }
                else {
                    if(b2b > 4){
                        score *= b2b;
                        b2b = -1;
                    }
                }
                combo++;
                if(combo > 1){
                    score += combo * level;
                }
                break;
            case 4:
                lineClearMessage = "QUAD";
                b2b++;
                if(checkForAllClear()){
                    score += (1000 * level * 20);
                }
                combo++;
                if(combo > 1){
                    score += combo * level;
                }
                break;
            default:
                lineClearMessage = "";
                spinMessage = "";
                pcMessage = "";
                combo = -1;
                break;
        }
        messageTimer = MESSAGE_DURATION; // reset timer
    }

    private boolean checkForTSpin(){
        return currentMino.rotatedDuringLockDelay && currentMino.type.equals("T");
    }

    /**
     * Check to see if a perfect clear has occurred (line clear results in an empty board).
     * @return true if board is empty
     */
    private boolean checkForAllClear(){
        return placedBlocks.isEmpty(); // only works mid-game
    }

    public void update() throws Exception {
        // Menu music
        if (gameState == GameState.MENU && !"menu".equals(currentSong)) {
            mm.stop();
            mm.loop("menu");
            currentSong = "menu";
        }

        // Gameplay
        if (gameState == GameState.PLAYING) {
            if (!KeyHandler.pausePressed) {
                // --- Background music per collection/level ---
                String levelKey = "collection" + (char)('A' + playCollection - 1) + (level / 2 + 1);
                if (!levelKey.equals(currentSong)) {
                    mm.loop(levelKey);
                    currentSong = levelKey;
                }

                // Piece has landed
                if (!currentMino.active) {
                    alreadyHeld = false;

                    // Add blocks to placed
                    for (int i = 0; i < 4; i++) {
                        placedBlocks.add(currentMino.b[i]);
                    }

                    // Check for game over
                    if (currentMino.b[0].x == startX && currentMino.b[0].y == startY) {
                        gameState = GameState.GAME_OVER;
                    } else {
                        currentMino.deactivating = false;

                        // Shift next pieces
                        currentMino = nextMino1;
                        currentMino.spin = false;
                        currentMino.justRotated = false;
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
                        nextMino6.setXY(0, 0);

                        clearLines();
                    }
                } else {
                    // Holding logic
                    if (KeyHandler.shiftPressed && !alreadyHeld) {
                        alreadyHeld = true; // user can only hold once per piece
                        if (!hold) { // no piece in hold slot
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
                            nextMino6.setXY(0, 0);

                            hold = true;
                        } else { // swap the previously held piece with the current piece
                            Mino temp = currentMino;
                            currentMino = holdMino;
                            currentMino.setXY(startX, startY);
                            holdMino = temp;
                            holdMino.setXY(leftX - 150, topY + 80);
                        }
                    }

                    // Hard drop
                    if (KeyHandler.spacePressed) {
                        currentMino.hardDrop();
                        KeyHandler.spacePressed = false;
                    }

                    // Update current piece
                    currentMino.update();
                }
            }

            // Message timer
            if (messageTimer > 0) {
                messageTimer--;
                if (messageTimer == 0) {
                    lineClearMessage = "";
                    pcMessage = "";
                }
            }
        }

        // Play the audio snippet to preview song
        if (gameState == GameState.SONGS) {
            String collectionKey = String.valueOf((char)('A' + collection - 1)); // "A", "B", etc.
            Snippet snippet = mm.getSnippet(collectionKey, song); // lookup in nested map

            if (snippet != null) {
                String key = "collection" + collectionKey + song; // for tracking/looping
                if (!key.equals(currentSong)) {
                    mm.stopLoop();
                    mm.loopSnippet(key, snippet.start, snippet.end, snippet.loopDelay);
                    currentSong = key;
                    System.out.println("Playing: " + key);
                }
            } else {
                System.out.println("Snippet not found: " + collectionKey + song);
            }
        }

        // OTHER GAME STATES
        else if (gameState == GameState.MUSIC_SELECT) {
            mm.stop();
            currentSong = "";
        }
        else if (gameState == GameState.GAME_OVER) {
            mm.stop();
            currentSong = "";
        }
        else if (gameState == GameState.CREDITS && !"credits".equals(currentSong)) {
            mm.stop();
            mm.loop("credits");
            currentSong = "credits";
        }
        else if (gameState == GameState.INSTRUCTIONS && !"instructions".equals(currentSong)) {
            mm.stop();
            mm.loop("instructions");
            currentSong = "instructions";
        }
        else if (gameState == GameState.OTHER && !"other".equals(currentSong)) {
            mm.stop();
            mm.loop("other");
            currentSong = "other";
        }
    }

    /**
     * Clears lines when a row is fully filled.
     */
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
                linesCleared++; // use this to determine how many lines were cleared at once
                if(lines % 10 == 0 && dropInterval > 1){
                    level++;
                    if(dropInterval > 10) dropInterval -= 5;
                    else if (dropInterval > 5) dropInterval -= 3;
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
            handleLineClear(linesCleared);
        }
        linesCleared = 0; // Reset for next potential clear
    }
    public void draw(Graphics2D g2) {
        Image img;
        int alpha = 200;
        if(gameState == GameState.MENU){
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
            g2.drawString("Scores", 620, 20);
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
            g2.drawRect(scoreButton.x, scoreButton.y, scoreButton.width, scoreButton.height);
        }
        if(gameState == GameState.PLAYING) {
            g2.setColor(new Color(0, 0, 0, 200));
            String collectionKey = String.valueOf((char)('A' + playCollection - 1)) + (level / 2 + 1);
            img = im.getImage(collectionKey);
            g2.drawImage(img, 0, 0, 1280, 720, null);
            g2.fillRect(0, 0, 1280, 720);
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

            if(!spinMessage.isEmpty() && spinMessageTimer > 0){
                g2.setColor(Color.MAGENTA);
                g2.setFont(new Font("Arial", Font.BOLD, 25));
                g2.drawString(spinMessage, leftX - 200, topY + 250);
                spinMessageTimer--;
            }
            if (spinMessageTimer <= 0) {
                spinMessage = "";
            }

            if(!pcMessage.isEmpty()){
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 50));
                g2.drawString(pcMessage, leftX + 15, topY + 400);
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
            if(combo > 1){
                g2.setFont(new Font("Arial", Font.BOLD, 30));
                g2.drawString(combo + " COMBO", leftX - 200, topY + 400);
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
        }
        if (gameState == GameState.MUSIC_SELECT) {
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
            g2.drawString("Music Collection 8", 570, 100);
            g2.drawString("Music Collection 9", 570, 200);
            g2.drawString("Music Collection 10", 570, 300);
            if(selectionActivated){
                g2.setFont(new Font("Tahoma", Font.BOLD, 60));
                g2.drawString("Click to select", 670, 550);
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
                        case 8:
                            g2.drawRect(mc8.x, mc8.y, mc8.width, mc8.height);
                            break;
                        case 9:
                            g2.drawRect(mc9.x, mc9.y, mc9.width, mc9.height);
                            break;
                        case 10:
                            g2.drawRect(mc10.x, mc10.y, mc10.width, mc10.height);
                            break;
                    }
                }
            }
            else{
                g2.setFont(new Font("Tahoma", Font.BOLD, 60));
                g2.drawString("TURN DOWN", 700, 550);
                g2.drawString("YOUR VOLUME!", 700, 620);
            }
        }
        if(gameState == GameState.SONGS){
            if(collection == 1){
                img = im.getImage("A" + currentBackground);
                g2.drawImage(img, 0, 0, 1280, 720, null);
                g2.setColor(new Color(0,0,0, alpha));
                g2.fillRect(0, 0, 1280, 720);
                g2.drawImage(img, 470, 150, 720, 405, null);
                g2.setColor(Color.WHITE);
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
                g2.drawImage(img, 0, 0, 1280, 720, null);
                g2.setColor(new Color(0,0,0, alpha));
                g2.fillRect(0, 0, 1280, 720);
                g2.drawImage(img, 470, 150, 720, 405, null);
                g2.setColor(Color.WHITE);
                g2.drawString("Music Collection 2", 560, 30);
                if(song == 1){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("Marshmary", placeholder.x + 15, placeholder.y + 50);
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
                    g2.setFont(new Font("Tahoma", Font.BOLD, 25));
                    g2.drawString("Don't Fight the Music", placeholder.x + 15, placeholder.y + 35);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Chroma", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 10){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("Synthesis.", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by tn-shi", placeholder.x + 15, placeholder.y + 85);
                }
            }
            if(collection == 3){
                img = im.getImage("C" + currentBackground);
                g2.drawImage(img, 0, 0, 1280, 720, null);
                g2.setColor(new Color(0,0,0, alpha));
                g2.fillRect(0, 0, 1280, 720);
                g2.drawImage(img, 470, 150, 720, 405, null);
                g2.setColor(Color.WHITE);
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
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("GOODWORLD", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by EBIMAYO", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 5){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("GOODRAGE", placeholder.x + 15, placeholder.y + 50);
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
                g2.drawImage(img, 0, 0, 1280, 720, null);
                g2.setColor(new Color(0,0,0, alpha));
                g2.fillRect(0, 0, 1280, 720);
                g2.drawImage(img, 470, 150, 720, 405, null);
                g2.setColor(Color.WHITE);
                g2.drawString("Music Collection 4", 560, 30);
                if(song == 1){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("Lost Memory", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Sakuzyo", placeholder.x + 15, placeholder.y + 85);
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
                g2.drawImage(img, 0, 0, 1280, 720, null);
                g2.setColor(new Color(0,0,0, alpha));
                g2.fillRect(0, 0, 1280, 720);
                g2.drawImage(img, 470, 150, 720, 405, null);
                g2.setColor(Color.WHITE);
                g2.drawString("Music Collection 5", 560, 30);
                if(song == 1){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 20));
                    g2.drawString("Brain Fluid", placeholder.x + 15, placeholder.y + 30);
                    g2.drawString("Explosion Girl", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by rerulili ft. GUMI & Miku", placeholder.x + 15, placeholder.y + 85);
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
                g2.drawImage(img, 0, 0, 1280, 720, null);
                g2.setColor(new Color(0,0,0, alpha));
                g2.fillRect(0, 0, 1280, 720);
                g2.drawImage(img, 470, 150, 720, 405, null);
                g2.setColor(Color.WHITE);
                g2.drawString("Music Collection 6", 560, 30);
                if(song == 1){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 35));
                    g2.drawString("Aegleseeker", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Silentroom vs. Frums", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 2){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 35));
                    g2.drawString("B.B.K.K.B.K.K", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by nora2r", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 3){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 35));
                    g2.drawString("Brain Power", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by NOMA", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 4){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 20));
                    g2.drawString("Chou Night of Knights", placeholder.x + 15, placeholder.y + 30);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by beatMARIO x MARON", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 5){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 35));
                    g2.drawString("Grievous Lady", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Team Grimoire vs. Laur", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 6){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 35));
                    g2.drawString("Mesmerizer", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 17));
                    g2.drawString("by 32ki ft. Hatsune Miku & Kasane Teto", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 7){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 25));
                    g2.drawString("PANDORA PARADOXXX", placeholder.x + 15, placeholder.y + 35);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Sakuzyo", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 8){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("PUPA", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Morimori Atsushi", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 9){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("Sage", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Camellia", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 10){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 35));
                    g2.drawString("the EmpErroR", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by sasakure.UK", placeholder.x + 15, placeholder.y + 85);
                }
            }
            if(collection == 7){
                img = im.getImage("G" + currentBackground);
                g2.drawImage(img, 0, 0, 1280, 720, null);
                g2.setColor(new Color(0,0,0, alpha));
                g2.fillRect(0, 0, 1280, 720);
                g2.drawImage(img, 470, 150, 720, 405, null);
                g2.setColor(Color.WHITE);
                g2.drawString("Music Collection 7", 560, 30);
                g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                g2.drawString("by t+pazolite", placeholder.x + 15, placeholder.y + 85);
                if(song == 1){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 35));
                    g2.drawString("Angelic Jelly", placeholder.x + 15, placeholder.y + 50);
                }
                if(song == 2){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 35));
                    g2.drawString("Chaos Time", placeholder.x + 15, placeholder.y + 50);
                }
                if(song == 3){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("Cheatreal", placeholder.x + 15, placeholder.y + 50);
                }
                if(song == 4){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 35));
                    g2.drawString("Chrome VOX", placeholder.x + 15, placeholder.y + 50);
                }
                if(song == 5){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 30));
                    g2.drawString("Garakuta Doll Play", placeholder.x + 15, placeholder.y + 40);
                }
                if(song == 6){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 30));
                    g2.drawString("Twisted Drop Party", placeholder.x + 15, placeholder.y + 40);
                }
                if(song == 7){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 30));
                    g2.drawString("Oshama Scramble", placeholder.x + 15, placeholder.y + 40);
                }
                if(song == 8){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 30));
                    g2.drawString("QZKago Requiem", placeholder.x + 15, placeholder.y + 40);
                }
                if(song == 9){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 35));
                    g2.drawString("Tempestissimo", placeholder.x + 15, placeholder.y + 50);
                }
                if(song == 10){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 25));
                    g2.drawString("FLY AWAY! TO THE COSMIC!!", placeholder.x + 15, placeholder.y + 50);
                }
            }
            if(collection == 8){
                img = im.getImage("H" + currentBackground);
                g2.drawImage(img, 0, 0, 1280, 720, null);
                g2.setColor(new Color(0,0,0, alpha));
                g2.fillRect(0, 0, 1280, 720);
                g2.drawImage(img, 470, 150, 720, 405, null);
                g2.setColor(Color.WHITE);
                g2.drawString("Music Collection 8", 560, 30);
                if(song == 1){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("Bring it on", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Giga", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 2){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 35));
                    g2.drawString("Gimme x Gimme", placeholder.x + 15, placeholder.y + 45);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Hachioji P x Giga", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 3){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("Hibana", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by DECO*27", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 4){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("Lagtrain", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by inabakumori", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 5){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 20));
                    g2.drawString("Luka Luka ☆", placeholder.x + 15, placeholder.y + 30);
                    g2.drawString("Night Fever", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by samfree", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 6){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("Monitoring", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by DECO*27", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 7){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("Override", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Yoshida Yasei", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 8){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 35));
                    g2.drawString("Senbonzakura", placeholder.x + 15, placeholder.y + 45);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Kurousa-P", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 9){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 20));
                    g2.drawString("Six Trillion Years", placeholder.x + 15, placeholder.y + 30);
                    g2.drawString("and Overnight Story", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by kemu ft. IA", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 10){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 30));
                    g2.drawString("Tokyo Teddy Bear", placeholder.x + 15, placeholder.y + 40);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Neru", placeholder.x + 15, placeholder.y + 85);
                }
            }
            if(collection == 9){
                img = im.getImage("I" + currentBackground);
                g2.drawImage(img, 0, 0, 1280, 720, null);
                g2.setColor(new Color(0,0,0, alpha));
                g2.fillRect(0, 0, 1280, 720);
                g2.drawImage(img, 470, 150, 720, 405, null);
                g2.setColor(Color.WHITE);
                g2.drawString("Music Collection 9", 560, 30);
                g2.setFont(new Font("Tahoma", Font.PLAIN, 25));
                if(song == 1){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("Climax", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by USAO", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 2){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("Cthugha", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by USAO", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 3){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("Cyaegha", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by USAO", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 4){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("DEAD or DIE", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by REDALiCE", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 5){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 30));
                    g2.drawString("Invisible Frenzy", placeholder.x + 15, placeholder.y + 40);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by Kobaryo", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 6){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("IZANA", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by t+pazolite vs. P*Light", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 7){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("Knight Rider", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by USAO", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 8){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("OUTRAGE", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by USAO vs. DJ Myosuke", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 9){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 40));
                    g2.drawString("RED to RED", placeholder.x + 15, placeholder.y + 50);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by REDALiCE", placeholder.x + 15, placeholder.y + 85);
                }
                if(song == 10){
                    g2.setFont(new Font("Tahoma", Font.BOLD, 30));
                    g2.drawString("Saikyo Stronger", placeholder.x + 15, placeholder.y + 40);
                    g2.setFont(new Font("Tahoma", Font.PLAIN, 20));
                    g2.drawString("by REDALiCE vs. USAO", placeholder.x + 15, placeholder.y + 85);
                }
            }
            if(collection == 10){
                if(song == 1){}
                if(song == 2){}
                if(song == 3){}
                if(song == 4){}
                if(song == 5){}
                if(song == 6){}
                if(song == 7){}
                if(song == 8){}
                if(song == 9){}
                if(song == 10){}
            }
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 20));
            g2.drawRect(placeholder.x, placeholder.y, placeholder.width, placeholder.height);
            g2.drawRect(stuff.x, stuff.y, stuff.width, stuff.height);
            g2.drawString("Track", stuff.x + 25, stuff.y + 20);
            g2.setFont(new Font("SansSerif", Font.BOLD, 60));
            g2.drawString(song + "", stuff.x + 30, stuff.y + 80);
            g2.setFont(new Font("Arial", Font.PLAIN, 20));

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
        if (gameState == GameState.GAME_OVER) {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.PLAIN, 80));
            g2.drawString("GAME OVER", Board.WIDTH / 2 - 220, Board.HEIGHT / 2);
            g2.setFont(new Font("Arial", Font.BOLD, 25));
            g2.drawString("Score:" + score, Board.WIDTH / 2 - 80, Board.HEIGHT / 2 + 80);
        }
        if(gameState == GameState.INSTRUCTIONS){
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
        if(gameState == GameState.CREDITS){
            if(page == 1) {
                g2.drawImage(credits, 0, 0, 1280, 720, null);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Sans Serif Collection", Font.BOLD, 100));
                g2.drawString("CREDITS!!!", 100, 100);
                g2.setFont(new Font("Sans Serif Collection", Font.BOLD, 20));
                g2.drawString("Now Playing:", 700, 350);
                g2.drawString("by LeaF", 700, 450);
                g2.setFont(new Font("Sans Serif Collection", Font.BOLD, 60));
                g2.drawString("Doppelganger", 700, 415);
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
        if(gameState == GameState.OTHER){
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
        else if (gameState == GameState.INITIALIZE){
            img = im.getImage("initialize1");
            g2.drawImage(img, 0, 0, 1280, 720, null);
        }
    }
    public void reset(){
        placedBlocks.clear();
        startingMinos.clear();
        holdMino = null;
        hold = false;
        lines = 0;
        score = 0;
        level = 1;
        // Adding the starting bag of pieces (each piece must appear )
        startingMinos.add(new LPiece(this));
        startingMinos.add(new JPiece(this));
        startingMinos.add(new IPiece(this));
        startingMinos.add(new SPiece(this));
        startingMinos.add(new ZPiece(this));
        startingMinos.add(new TPiece(this));
        startingMinos.add(new OPiece(this));

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

        try{
            BufferedReader br = new BufferedReader(new FileReader("save.txt"));
            playCollection = Integer.parseInt(br.readLine().trim());
            br.close();
        } catch (NumberFormatException e){
            playCollection = 1;
        } catch (IOException e) {
            playCollection = 1;
        } catch (NullPointerException e) {

        }
    }
    public void saveSongCollection(int collection) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter("save.txt"));
            out.println(collection);
            selectionActivated = false;
            out.close();
            reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveScore(String name) throws IOException {
        Player p = new Player(name, score);
        scores.add(p);
        scores.sort(new SortByScore());
        PrintWriter out = new PrintWriter(new FileWriter("scores.txt"));
        for(Player q : scores){
            out.println(q);
        }
        out.close();
    }

    public void readScores() {
        try{
            Scanner sc = new Scanner(new File("scores.txt"));
            while(sc.hasNextLine()){
                StringTokenizer st = new StringTokenizer(sc.nextLine(), " ");
                String name = st.nextToken();
                int score = Integer.parseInt(st.nextToken());
                Player p = new Player(name, score);
                scores.add(p);
            }
        } catch (IOException e){

        } catch (NoSuchElementException e){

        } catch (NumberFormatException e) {

        }
    }

    public void resetPage(){
        page = 1;
    }

    public void selectCollection(int collectionId) {
        collection = collectionId;
        song = 1;
        currentBackground = 1;
        gameState = GameState.SONGS;
    }

    public void previousSong(){
        song--;
        currentBackground--;
        if(song < 1) song = SONGS;
        if(currentBackground < 1) currentBackground = SONGS;
    }

    public void nextSong(){
        song++;
        currentBackground++;
        if(song > SONGS) song = 1;
        if(currentBackground > SONGS) currentBackground = 1;
    }
    public void nextPage() {
        int max = pages.getOrDefault(gameState, 1);
        if (max <= 1) return;

        page++;
        if (page > max) page = 1;
    }

    public void previousPage() {
        int max = pages.getOrDefault(gameState, 1);
        if (max <= 1) return;

        page--;
        if (page < 1) page = max;
    }

    public void initCollections() {
        collectionAreas.put(mc1, 1);
        collectionAreas.put(mc2, 2);
        collectionAreas.put(mc3, 3);
        collectionAreas.put(mc4, 4);
        collectionAreas.put(mc5, 5);
        collectionAreas.put(mc6, 6);
        collectionAreas.put(mc7, 7);
        collectionAreas.put(mc8, 8);
        collectionAreas.put(mc9, 9);
        collectionAreas.put(mc10, 10);
    }

    public void updateHover(Point p) {
        if (!selectionActivated) {
            hover = false;
            hoveredCollection = 0;
            return;
        }

        hover = false;
        hoveredCollection = 0;

        // Iterate over each rectangle in the map
        for (Rectangle rect : collectionAreas.keySet()) {
            if (rect.contains(p)) {
                hover = true;
                hoveredCollection = collectionAreas.get(rect);
                break; // stop after finding the first match
            }
        }
    }

    public void increment(){
        score++;
    }

    // * Getters and Setters *
    public int getScore() {
        return score;
    }

    public int getDropInterval(){
        return dropInterval;
    }

    public ArrayList<Block> getPlacedBlocks(){
        return placedBlocks;
    }

    public boolean isInitializing() {
        return gameState == GameState.INITIALIZE;
    }

    public boolean isPlaying() {
        return gameState == GameState.PLAYING;
    }

    public boolean isInMenu() {
        return gameState == GameState.MENU;
    }

    public boolean isInSongs(){
        return gameState == GameState.SONGS;
    }

    public boolean isGameOver() {
        return gameState == GameState.GAME_OVER;
    }

    public boolean isSelectingSong(){
        return gameState == GameState.MUSIC_SELECT;
    }

    public boolean isSelectionActivated(){
        return selectionActivated;
    }

    // These go-to methods act as setters because they change the game state.
    // However, they only execute during certain conditions.
    public void goToMenu(){
        if(gameState != GameState.PLAYING){
            gameState = GameState.MENU;
        }
    }
    public void goToSelection(){
        if(gameState != GameState.PLAYING){
            gameState = GameState.MUSIC_SELECT;
        }
    }
    public void goToSongs(){
        if(gameState != GameState.PLAYING){
            gameState = GameState.SONGS;
        }
    }
    public void goToCredits(){
        if(gameState != GameState.PLAYING){
            gameState = GameState.CREDITS;
        }
    }
    public void goToInstructions(){
        if(gameState != GameState.PLAYING){
            gameState = GameState.INSTRUCTIONS;
        }
    }
    public void goToOther(){
        if(gameState != GameState.PLAYING){
            gameState = GameState.OTHER;
        }
    }
    public void play(){
        gameState = GameState.PLAYING;
    }

    public void select(){
        if (gameState == GameState.MUSIC_SELECT) {
            selectionActivated = true;
        }
    }

    public void setSpinMessage(String spinMessage){
        this.spinMessage = spinMessage;
    }

    public void setSpinMessageTimer(int spinMessageTimer){
        this.spinMessageTimer = spinMessageTimer;
    }

    public void setHold(boolean hold){
        this.hold = hold;
    }
}
