/*
 * Manages all pieces. Rotation, wall kicks, and piece type are all kept track in this class.
 */

import java.awt.*;
import java.util.*;

public abstract class Mino {

    public static final Map<String, Color> PIECE_COLORS = Map.of(
        "I", Color.CYAN,
        "J", Color.BLUE,
        "L", Color.ORANGE,
        "O", Color.YELLOW,
        "S", Color.GREEN,
        "T", Color.MAGENTA,
        "Z", Color.RED
    );

    public static final Map<String, Integer> MINI_SPIN_DIRECTIONS = Map.of(
        "I", 0,
        "J", 2,
        "L", 4,
        "S", 1,
        "Z", 1,
        "T", 1,
        "O", 0
    );

    public Block[] b = new Block[4]; // One piece, which is composed of four blocks
    public Block[] temp = new Block[4]; // Use to determine if a move is valid

    int autoDropCounter = 0;
    public int direction = 1; // 1: up, 2: right, 3: down, 4: left

    boolean leftCollision, rightCollision, bottomCollision; // collision flags

    // Piece properties
    private boolean active = true;
    private boolean deactivating;
    int deactivateCounter = 0;
    protected String type;
    private boolean spin = false;

    protected GameManager gm;

    public Mino(GameManager gm){
        // we have to pass this GM to all the major classes to prevent multiple instances from running.
        this.gm = gm;
    }

    public void create(Color c){ // Set distinct colors for each piece
        for(int i=0; i<4; i++){
            b[i] = new Block(c);
            temp[i] = new Block(c);
        }
    }

    /**
     * Sets the piece on a certain spot on the graphics window.
     * @param x the x-coordinate of the piece
     * @param y the y-coordinate of the piece
     */
    public abstract void setXY(int x, int y);

    // --- COLLISIONS ---
    public void checkMovementCollision() { // Check for collision with the walls
        leftCollision = rightCollision = bottomCollision = false;
        for (int i = 0; i < b.length; i++) {
            if (b[i].x <= GameManager.leftX) leftCollision = true;
            if (b[i].x + Block.SIZE >= GameManager.rightX) rightCollision = true;
            if (b[i].y + Block.SIZE >= GameManager.bottomY) bottomCollision = true;
        }
    }

    public void checkBlockCollision() { // Check for collision with blocks
        leftCollision = rightCollision = bottomCollision = false;

        int[] pieceGridX = new int[4];
        int[] pieceGridY = new int[4];
        for (int i = 0; i < 4; i++) {
            pieceGridX[i] = b[i].x / Block.SIZE;
            pieceGridY[i] = b[i].y / Block.SIZE;
        }
        // Scale block positions to match indices, then check for collision
        for (Block placed : gm.getPlacedBlocks()) {
            int targetGridX = placed.x / Block.SIZE;
            int targetGridY = placed.y / Block.SIZE;

            for (int i = 0; i < 4; i++) {
                if (pieceGridX[i] - 1 == targetGridX && pieceGridY[i] == targetGridY) leftCollision = true;
                if (pieceGridX[i] + 1 == targetGridX && pieceGridY[i] == targetGridY) rightCollision = true;
                if (pieceGridX[i] == targetGridX && pieceGridY[i] + 1 == targetGridY) bottomCollision = true;
            }
        }

        for (int i = 0; i < 4; i++) {
            if (pieceGridY[i] >= (GameManager.bottomY / Block.SIZE) - 1) bottomCollision = true;
        }
    }

    private boolean rotationBlockCollision;
    public void checkRotationBlockCollision() { // check rotation collision with blocks
        rotationBlockCollision = false;
        for (Block placed : gm.getPlacedBlocks()) {
            for (int j = 0; j < 4; j++) {
                if (temp[j].x == placed.x && temp[j].y == placed.y) {
                    rotationBlockCollision = true;
                    return;
                }
            }
        }
    }

    // PIVOT ROTATIONS
    private void rotateTempCW() {
        // Pivot center in grid space
        double px = (b[0].x + Block.SIZE / 2.0) / Block.SIZE;
        double py = (b[0].y + Block.SIZE / 2.0) / Block.SIZE;

        for (int i = 0; i < 4; i++) {
            // Block center in grid space
            double gx = (b[i].x + Block.SIZE / 2.0) / Block.SIZE;
            double gy = (b[i].y + Block.SIZE / 2.0) / Block.SIZE;

            double relX = gx - px;
            double relY = gy - py;

            // CW rotation
            double nx = px - relY;
            double ny = py + relX;

            // Convert back to pixel top-left
            temp[i].x = (int)Math.round(nx * Block.SIZE - Block.SIZE / 2.0);
            temp[i].y = (int)Math.round(ny * Block.SIZE - Block.SIZE / 2.0);
        }
    }
    private void rotateTempCCW() {
        double px = (b[0].x + Block.SIZE / 2.0) / Block.SIZE;
        double py = (b[0].y + Block.SIZE / 2.0) / Block.SIZE;

        for (int i = 0; i < 4; i++) {
            double gx = (b[i].x + Block.SIZE / 2.0) / Block.SIZE;
            double gy = (b[i].y + Block.SIZE / 2.0) / Block.SIZE;

            double relX = gx - px;
            double relY = gy - py;

            double nx = px + relY;
            double ny = py - relX;

            temp[i].x = (int)Math.round(nx * Block.SIZE - Block.SIZE / 2.0);
            temp[i].y = (int)Math.round(ny * Block.SIZE - Block.SIZE / 2.0);
        }
    }
    private void rotateTemp180() {
        double px = (b[0].x + Block.SIZE / 2.0) / Block.SIZE;
        double py = (b[0].y + Block.SIZE / 2.0) / Block.SIZE;

        for (int i = 0; i < 4; i++) {
            double gx = (b[i].x + Block.SIZE / 2.0) / Block.SIZE;
            double gy = (b[i].y + Block.SIZE / 2.0) / Block.SIZE;

            double nx = 2 * px - gx;
            double ny = 2 * py - gy;

            temp[i].x = (int)Math.round(nx * Block.SIZE - Block.SIZE / 2.0);
            temp[i].y = (int)Math.round(ny * Block.SIZE - Block.SIZE / 2.0);
        }
    }


    /**
     * Rotates a piece in a certain direction.
     * @param rotationType the direction of the rotation
     */

    public void rotate(int rotationType) {
        if (type.equals("O")) return; // O piece does not rotate
        int fromDir = direction;

        // Rotate into temp
        switch (rotationType) {
            case 0: rotateTempCW(); break;
            case 1: rotateTempCCW(); break;
            case 2: rotateTemp180(); break;
        }

        int toDir = switch (rotationType) {
            case 0 -> (fromDir % 4) + 1;
            case 1 -> (fromDir == 1 ? 4 : fromDir - 1);
            case 2 -> ((fromDir + 1) % 4) + 1;
            default -> fromDir;
        };

        // Try kicks WITHOUT committing direction yet
        if (attemptRotationWithKicks(fromDir, toDir, rotationType)) {
            // Commit rotation
            direction = toDir;
            for (int i = 0; i < 4; i++) {
                b[i].x = temp[i].x;
                b[i].y = temp[i].y;
            }
        }
    }

    /**
     * Attempts to rotate a piece using wall kicks. Fails if all resulting cases still result in collisions.
     * @param fromDir The original direction of the piece
     * @param toDir The direction of intended rotation
     * @param rotationType The type of rotation
     * @return whether the rotation can be executed
     */
    private boolean attemptRotationWithKicks(int fromDir, int toDir, int rotationType) {
        int[][] kicks = getWallKicks(fromDir, toDir, rotationType);

        // Save rotated state once
        int[] baseX = new int[4];
        int[] baseY = new int[4];
        for (int i = 0; i < 4; i++) {
            baseX[i] = temp[i].x;
            baseY[i] = temp[i].y;
        }

        for (int[] offset : kicks) {
            int dx = offset[0] * Block.SIZE;
            int dy = offset[1] * Block.SIZE;

            // Apply kick ON TOP of rotated shape
            for (int i = 0; i < 4; i++) {
                temp[i].x = baseX[i] + dx;
                temp[i].y = baseY[i] + dy;
            }

            leftCollision = rightCollision = bottomCollision = false;
            rotationBlockCollision = false;

            checkRotationCollision();
            checkRotationBlockCollision();

            if (!leftCollision && !rightCollision && !bottomCollision && !rotationBlockCollision) {
                return true; // temp[] is valid and rotated
            }
        }

        return false;
    }


    /**
     * Provides a wall kick table for a certain piece.
     * @param from the initial position
     * @param to the rotated position
     * @param rotationType the type of rotation
     * @return the wall kick table, in a 2D array
     */
    private int[][] getWallKicks(int from, int to, int rotationType) {
        boolean isI = type.equals("I");

        int[][] JLSTZ_0R = {{0,0}, {-1,0}, {-1,1}, {0,2}, {-1,2}};
        int[][] JLSTZ_R0 = {{0,0}, {1,0}, {1,1}, {0,-2}, {1,-2}};
        int[][] JLSTZ_R2 = {{0,0}, {1,0}, {1,1}, {0,-2}, {1,-2}};
        int[][] JLSTZ_2R = {{0,0}, {-1,0}, {-1,-1}, {0,2}, {-1,2}};
        int[][] JLSTZ_2L = {{0,0}, {1,0}, {1,-1}, {0,2}, {1,2}};
        int[][] JLSTZ_L2 = {{0,0}, {-1,0}, {-1,1}, {0,-2}, {-1,-2}};
        int[][] JLSTZ_L0 = {{0,0}, {-1,0}, {-1,1}, {0,-2}, {-1,-2}};
        int[][] JLSTZ_0L = {{0,0}, {1,0}, {1,-1}, {0,2}, {1,2}};

        int[][] I_0R = { {0,0}, {-2,0}, {1,0}, {-2,1}, {1,-2} };
        int[][] I_R0 = { {0,0}, {2,0}, {-1,0}, {2,-1}, {-1,2} };
        int[][] I_R2 = { {0,0}, {-1,0}, {2,0}, {-1,-2}, {2,1} };
        int[][] I_2R = { {0,0}, {1,0}, {-2,0}, {1,2}, {-2,-1} };
        int[][] I_2L = { {0,0}, {2,0}, {-1,0}, {2,-1}, {-1,2} };
        int[][] I_L2 = { {0,0}, {-2,0}, {1,0}, {-2,1}, {1,-2} };
        int[][] I_L0 = { {0,0}, {1,0}, {-2,0}, {1,2}, {-2,-1} };
        int[][] I_0L = { {0,0}, {-1,0}, {2,0}, {-1,-2}, {2,1} };


        // === 180° rotation offsets (simplified) ===
        int[][] JLSTZ_180 = {{0,0}, {-1,0}, {1,0}, {0,-1}, {0,1}, {-1,-1}, {1,-1}, {-1,1}, {1,1}};
        int[][] I_180 = {{0,0}, {-2,0}, {2,0}, {0,-1}, {0,1}, {-2,-1}, {2,-1}, {-2,1}, {2,1}};

        // Handle 180° rotation
        if (rotationType == 2) {
            return isI ? I_180 : JLSTZ_180;
        }

        // Handle 90° rotations
        if (isI) {
            switch(from) {
                case 1: if (to == 2) return I_0R; if (to == 4) return I_0L; break;
                case 2: if (to == 3) return I_R2; if (to == 1) return I_R0; break;
                case 3: if (to == 4) return I_2R; if (to == 2) return I_2L; break;
                case 4: if (to == 1) return I_L0; if (to == 3) return I_L2; break;
            }
        } else {
            switch(from) {
                case 1: if (to == 2) return JLSTZ_0R; if (to == 4) return JLSTZ_0L; break;
                case 2: if (to == 3) return JLSTZ_R2; if (to == 1) return JLSTZ_R0; break;
                case 3: if (to == 4) return JLSTZ_2R; if (to == 2) return JLSTZ_2L; break;
                case 4: if (to == 1) return JLSTZ_L0; if (to == 3) return JLSTZ_L2; break;
            }
        }

        // Fallback: no offset
        return new int[][]{{0,0}};
    }

    // --- DEACTIVATION / T-SPIN LOGIC ---
    private void deactivate() {
        int lockDelay = 90; // frames until piece locks

        updateCurrentCollisions();

        // HARD DROP: immediately start lock
        if (KeyHandler.spacePressed && active) {
            deactivateCounter = lockDelay; // force immediate lock
            deactivating = true;
        }

        // If piece cannot move down
        if (!canMoveDown()) {

            // Start lock timer if not already started
            if (!deactivating) {
                deactivating = true;
                deactivateCounter = 0;
            }

            // Increment lock timer
            deactivateCounter++;

            // Lock the piece if timer expires
            if (deactivateCounter >= lockDelay) {
                active = false;
                if (!canMove()) {
                    gm.setPreviousMino(this);
                    spin = true;
                    System.out.println(type + " spin detected");
                    if (direction == MINI_SPIN_DIRECTIONS.get(type)) {
                        gm.setSpinMessage("Mini " + type + "-Spin");
                    } else {
                        gm.setSpinMessage(type + "-Spin");
                    }
                    gm.setSpinMessageTimer(120);
                }

                // Reset lock-related state for next piece
                deactivating = false;
                deactivateCounter = 0;
            }

        } else {
            // Piece is no longer resting → reset lock timer
            deactivating = false;
            deactivateCounter = 0;
        }
    }

    public void update() {
        // ignore when user is not playing
        if (!gm.isPlaying()) return;

        leftCollision = rightCollision = bottomCollision = false;
        rotationBlockCollision = false;
        checkMovementCollision();
        checkBlockCollision();

        if (deactivating) deactivate(); // piece cannot move down any further

        // check for rotation
        if (KeyHandler.upPressed) { rotate(0); KeyHandler.upPressed = false; }
        if (KeyHandler.zPressed) { rotate(1); KeyHandler.zPressed = false; }
        if (KeyHandler.aPressed) { rotate(2); KeyHandler.aPressed = false; }

        // move left
        if (KeyHandler.leftPressed) {
            if (canMove(-Block.SIZE, 0)) {
                for (Block blk : b) blk.x -= Block.SIZE;
            }
            KeyHandler.leftPressed = false; // only allow one input at a time
        }

        // right
        if (KeyHandler.rightPressed) {
            if (canMove(Block.SIZE, 0)) {
                for (Block blk : b) blk.x += Block.SIZE;
            }
            KeyHandler.rightPressed = false;
        }
        // Soft drop
        if (KeyHandler.downPressed && !deactivating) {
            if (movePieceDown()) {
                gm.increment(); // increase score for soft drop
                deactivateCounter = 0;
                deactivating = false;
            }
            else {
                checkMovementCollision();
                checkBlockCollision();
                deactivating = true;
            }
            KeyHandler.downPressed = false;
        }

        checkMovementCollision();
        checkBlockCollision();
        if (bottomCollision || rotationBlockCollision) deactivating = true;

        if (KeyHandler.shiftPressed) {
            gm.setHold(false);
            KeyHandler.shiftPressed = false;
        }

        autoDropCounter++;
        if (autoDropCounter >= gm.getDropInterval()) {
            if (!movePieceDown()) {
                checkMovementCollision();
                checkBlockCollision();
                deactivating = true;
            }
            else {
                deactivateCounter = 0;
                deactivating = false;
            }
            autoDropCounter = 0;
        }

        if (deactivating) deactivate();
    }

    /**
     * Check if the piece can move in any direction (down, left, right, up).
     * @return whether the piece can move
     */
    // Returns true if the piece can move at least one block in any direction
    private boolean canMove() {
        if(canMove(0, Block.SIZE) ) return true; // down
        if(canMove(-Block.SIZE, 0) ) return true; // left
        if(canMove(Block.SIZE, 0) ) return true; // right
        if(canMove(0, -Block.SIZE) ) return true; // up
        return false;
    }

    /**
     * Check if the piece can move left or right.
     * @param dx the change in x (negative for left, positive for right)
     * @return whether the piece can move in that direction
     */
    private boolean canMove(int dx, int dy) {
        for (Block blk : b) {
            int newX = blk.x + dx;
            int newY = blk.y + dy;

            // Walls
            if (newX < GameManager.leftX || newX >= GameManager.rightX) return false;

            // Floor / ceiling
            if (newY >= GameManager.bottomY) return false;

            // Placed blocks
            for (Block placed : gm.getPlacedBlocks()) {
                if (placed.x == newX && placed.y == newY) return false;
            }
        }
        return true;
    }


    private boolean movePieceDown() {
        for (int i = 0; i < 4; i++) { temp[i].x = b[i].x; temp[i].y = b[i].y + Block.SIZE; }
        bottomCollision = leftCollision = rightCollision = false;
        checkRotationCollision();
        checkRotationBlockCollision();
        if (!bottomCollision && !rotationBlockCollision) {
            for (int i = 0; i < 4; i++) b[i].y = temp[i].y;
            return true;
        }
        return false;
    }

    private boolean canMoveDown() {
        boolean result = true;
        for (int i = 0; i < 4; i++) {
            int newY = b[i].y + Block.SIZE;

            // Check bottom of the field
            if (newY >= GameManager.bottomY) result = false;

            // Check collisions with placed blocks
            for (Block placed : gm.getPlacedBlocks()) {
                if (placed.x == b[i].x && placed.y <= newY && newY < placed.y + Block.SIZE) {
                    result = false;
                }
            }
        }
        return result;
    }


    private void updateCurrentCollisions() {
        leftCollision = rightCollision = bottomCollision = false;
        for (int i = 0; i < 4; i++) {
            if (b[i].x < GameManager.leftX) leftCollision = true;
            if (b[i].x + Block.SIZE > GameManager.rightX) rightCollision = true;
            if (b[i].y + Block.SIZE >= GameManager.bottomY) bottomCollision = true;
        }
        checkRotationBlockCollision();
    }

    public void hardDrop() {
        boolean willCollide = false;
        int safety = 0;

        while (!willCollide) {
            // Check if next move would collide
            for (Block blk : b) {
                int nextY = blk.y + Block.SIZE;

                if (nextY >= GameManager.bottomY) {
                    willCollide = true;
                    break;
                }

                for (Block placed : gm.getPlacedBlocks()) {
                    if (blk.x == placed.x && nextY == placed.y) {
                        willCollide = true;
                        break;
                    }
                }
                if (willCollide) break;
            }

            // If collision detected, STOP — do not move
            if (willCollide) break;

            // Safe to move down
            for (Block blk : b)
                blk.y += Block.SIZE;

            gm.increment();
            if (++safety > 100) break;
        }

        // Immediately lock after hard drop
        deactivateCounter = 45;
        bottomCollision = true;
        deactivating = true;
        deactivate();
    }


    private void checkRotationCollision() {
        leftCollision = rightCollision = bottomCollision = false;
        int playfieldWidth = GameManager.rightX - GameManager.leftX;
        int playfieldHeight = GameManager.bottomY - GameManager.topY;
        int cols = playfieldWidth / Block.SIZE;
        int rows = playfieldHeight / Block.SIZE;

        for (Block block : temp) {
            int gridX = (block.x - GameManager.leftX) / Block.SIZE;
            int gridY = (block.y - GameManager.topY) / Block.SIZE;
            if (gridX < 0) leftCollision = true;
            if (gridX >= cols) rightCollision = true;
            if (gridY >= rows) bottomCollision = true;
        }
    }

    public void draw(Graphics2D g2){
        int margin = 2;
        g2.setColor(b[0].color);
        for(int i=0; i<4; i++){
            g2.fillRect(b[i].x + margin, b[i].y + margin, Block.SIZE - 2*margin, Block.SIZE - 2*margin);
        }
    }

    public String getType() {
        return type;
    }
    public boolean isActive() {
        return active;
    }
    public boolean isSpin() {
        return spin;
    }

    public void setDeactivating(boolean deactivating) {
        this.deactivating = deactivating;
    }
    public void setSpin(boolean spin) {
        this.spin = spin;
    }
}
