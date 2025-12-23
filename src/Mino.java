/*
 * Manages all pieces. Rotation, wall kicks, and piece type are all kept track in this class.
 */

import javax.swing.*;
import java.awt.*;

public abstract class Mino {

    public Block[] b = new Block[4]; // One piece, which is composed of four blocks
    public Block[] temp = new Block[4]; // Use to determine if a move is valid

    int autoDropCounter = 0;
    public int direction = 1; // 1: up, 2: right, 3: down, 4: left

    boolean leftCollision, rightCollision, bottomCollision; // collision flags

    // Piece properties
    public boolean active = true;
    public boolean deactivating;
    int deactivateCounter = 0;
    public boolean justRotated = false;
    public String type;
    public boolean spin = false;
    public boolean rotatedDuringLockDelay = false;

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
    private void rotateTempCW() { // Temporarily rotates CW, but doesn't register yet
        int px = b[0].x;
        int py = b[0].y;
        temp[0].x = px; temp[0].y = py;
        for (int i = 1; i < 4; i++) {
            int relX = b[i].x - px;
            int relY = b[i].y - py;
            temp[i].x = px - relY;
            temp[i].y = py + relX;
        }
    }

    private void rotateTempCCW() { // Temporarily rotates CCW, but doesn't register yet
        int px = b[0].x;
        int py = b[0].y;
        temp[0].x = px; temp[0].y = py;
        for (int i = 1; i < 4; i++) {
            int relX = b[i].x - px;
            int relY = b[i].y - py;
            temp[i].x = px + relY;
            temp[i].y = py - relX;
        }
    }

    /**
     * Rotates a piece in a certain direction.
     * @param rotationType the direction of the rotation
     */
    public void rotate(int rotationType) {
        if(type.equals("O")) return; // the O piece doesn't rotate
        justRotated = true;
        if (active) rotatedDuringLockDelay = true; // this is useful for piece spins

        // Save current direction (optional, useful for wall kick tables)
        int fromDir = direction;

        // Copy current positions to temp as baseline
        for (int i = 0; i < 4; i++) {
            temp[i].x = b[i].x;
            temp[i].y = b[i].y;
        }

        // Rotate temp[] according to rotation type
        switch(rotationType) {
            case 0: // CW 90°
                rotateTempCW();
                direction = (direction % 4) + 1; // cycle 1→2→3→4→1
                break;
            case 1: // CCW 90°
                rotateTempCCW();
                direction = (direction == 1) ? 4 : direction - 1;
                break;
            case 2: // 180°
                rotateTempCW();
                rotateTempCW();
                direction = (direction + 1) % 4 + 1; // simple way to update, optional
                break;
        }

        // Attempt rotation + wall kicks
        if (!attemptRotationWithKicks(fromDir, direction, rotationType)) {
            // rotation failed - restore b[] from temp baseline
            for (int i = 0; i < 4; i++) {
                temp[i].x = b[i].x;
                temp[i].y = b[i].y;
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
        int[][] kicks = getWallKicks(fromDir, toDir, rotationType); // obtain wall kick table

        for (int[] offset : kicks) {
            // Apply kick to temp[] (already rotated)
            for (int i = 0; i < 4; i++) {
                temp[i].x += offset[0] * Block.SIZE;
                temp[i].y += offset[1] * Block.SIZE;
            }

            // Check for collisions using temp[]
            leftCollision = rightCollision = bottomCollision = false;
            checkRotationCollision();      // wall boundaries
            checkRotationBlockCollision(); // collision with placed blocks

            if (!leftCollision && !rightCollision && !bottomCollision && !rotationBlockCollision) {
                // Kick successful - pass from temp[] to b[]
                for (int i = 0; i < 4; i++) {
                    b[i].x = temp[i].x;
                    b[i].y = temp[i].y;
                }
                return true;
            }

            // Undo this kick if invalid (restore rotated shape without offset)
            for (int i = 0; i < 4; i++) {
                temp[i].x -= offset[0] * Block.SIZE;
                temp[i].y -= offset[1] * Block.SIZE;
            }
        }

        return false; // no kick worked
    }

    /**
     * Provides a wall kick table for a certain piece.
     * @param from the initial position
     * @param to the rotated position
     * @param rotationType the type of rotation
     * @return the wall kick table, in a 2D array
     */
    private int[][] getWallKicks(int from, int to, int rotationType) {
        boolean isI = type.equals("I"); // the I-piece has different offsets.

        // IMPORTANT: These are not the official SRS wall kick tables for Tetris.
        // As a result, triples cannot be obtained from a spin.

        // JLSTZ pieces - follow these offsets
        int[][] JLSTZ_0R = {{0,0}, {-1,0}, {-1,1}, {0,-2}, {-1,-2}};
        int[][] JLSTZ_R0 = {{0,0}, {1,0}, {1,-1}, {0,2}, {1,2}};
        int[][] JLSTZ_R2 = {{0,0}, {1,0}, {1,-1}, {0,2}, {1,2}};
        int[][] JLSTZ_2R = {{0,0}, {-1,0}, {-1,1}, {0,-2}, {-1,-2}};
        int[][] JLSTZ_2L = {{0,0}, {1,0}, {1,1}, {0,-2}, {1,-2}};
        int[][] JLSTZ_L2 = {{0,0}, {-1,0}, {-1,-1}, {0,2}, {-1,2}};
        int[][] JLSTZ_L0 = {{0,0}, {-1,0}, {-1,-1}, {0,2}, {-1,2}};
        int[][] JLSTZ_0L = {{0,0}, {1,0}, {1,1}, {0,-2}, {1,-2}};

        // I piece
        int[][] I_0R = {{0,0}, {-2,0}, {1,0}, {-2,-1}, {1,2}};
        int[][] I_R0 = {{0,0}, {2,0}, {-1,0}, {2,1}, {-1,-2}};
        int[][] I_R2 = {{0,0}, {-1,0}, {2,0}, {-1,2}, {2,-1}};
        int[][] I_2R = {{0,0}, {1,0}, {-2,0}, {1,-2}, {-2,1}};
        int[][] I_2L = {{0,0}, {2,0}, {-1,0}, {2,1}, {-1,-2}};
        int[][] I_L2 = {{0,0}, {-2,0}, {1,0}, {-2,-1}, {1,2}};
        int[][] I_L0 = {{0,0}, {1,0}, {-2,0}, {1,-2}, {-2,1}};
        int[][] I_0L = {{0,0}, {-1,0}, {2,0}, {-1,2}, {2,-1}};

        // Match the rotation with its corresponding offsets
        if (isI) {
            switch(from) {
                case 1: if (to == 2) return I_0R; if (to == 4) return I_0L; break;
                case 2: if (to == 3) return I_R2; if (to == 1) return I_R0; break;
                case 3: if (to == 4) return I_2R; if (to == 2) return I_2L; break;
                case 4: if (to == 1) return I_L0; if (to == 3) return I_L2; break;
            }
            if (rotationType == 2) return new int[][]{{0,0}};
        } else {
            switch(from) {
                case 1: if (to == 2) return JLSTZ_0R; if (to == 4) return JLSTZ_0L; break;
                case 2: if (to == 3) return JLSTZ_R2; if (to == 1) return JLSTZ_R0; break;
                case 3: if (to == 4) return JLSTZ_2R; if (to == 2) return JLSTZ_2L; break;
                case 4: if (to == 1) return JLSTZ_L0; if (to == 3) return JLSTZ_L2; break;
            }
            if (rotationType == 2) return new int[][]{{0,0}};
        }

        return new int[][]{{0,0}};
    }

    // --- DEACTIVATION / T-SPIN LOGIC ---
    private void deactivate() {
        if(KeyHandler.spacePressed) active = false;
        updateCurrentCollisions();
        int delay = 90; // piece lock timer
        if(isOnFloor()) delay = 45; // balancing the lock delay because there is more delay on the floor
        if (!canMoveDown()) {
            deactivateCounter++; // start counting frames
            if (deactivateCounter >= delay) {
                active = false; // pieces has locked
                if (rotatedDuringLockDelay) { // piece rotated during deactivation
                    spin = true;
                    System.out.println("T-spin detected");
                    if (direction == 1) {
                        gm.setSpinMessage("Mini" + type + "-Spin");
                    } else {
                        gm.setSpinMessage(type + "-Spin");
                    }
                    gm.setSpinMessageTimer(120);
                }
                deactivateCounter = 0; // reset all values
                deactivating = false;
                rotatedDuringLockDelay = false;
            }
        } else { // piece can still move down
            deactivateCounter = 0;
            deactivating = false;
            spin = false;
            rotatedDuringLockDelay = false;
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
                justRotated = false;
            }
            KeyHandler.leftPressed = false; // only allow one input at a time
        }

        // right
        if (KeyHandler.rightPressed) {
            if (canMove(Block.SIZE, 0)) {
                for (Block blk : b) blk.x += Block.SIZE;
                justRotated = false;
            }
            KeyHandler.rightPressed = false;
        }
        // Soft drop
        if (KeyHandler.downPressed && !deactivating) {
            if (movePieceDown()) {
                gm.increment();
                deactivateCounter = 0;
                deactivating = false; }
            else { checkMovementCollision(); checkBlockCollision(); deactivating = true; }
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

    private boolean isOnFloor() {
        for (int i = 0; i < 4; i++) if (b[i].y + Block.SIZE >= GameManager.bottomY) return true;
        return false;
    }

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
        for (int i = 0; i < 4; i++) {
            int newY = b[i].y + Block.SIZE;
            if (newY >= GameManager.bottomY) return false;
            for (Block placed : gm.getPlacedBlocks())
                if (placed.x == b[i].x && placed.y == newY) return false;
        }
        return true;
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
        int safety = 0;
        while (true) {
            boolean willCollide = false;
            for (Block blk : b) {
                int nextY = blk.y + Block.SIZE;
                if (nextY >= GameManager.bottomY) {
                    willCollide = true;
                    break;
                }
                for (Block placed : gm.getPlacedBlocks())
                    if (blk.x == placed.x && nextY == placed.y) {
                        willCollide = true;
                        break;
                    }
                if (willCollide) break;
            }
            if (willCollide || safety > 100) break;
            for (Block blk : b)
                blk.y += Block.SIZE;
            safety++;
            gm.increment();
        }
        deactivateCounter = 45; bottomCollision = true; deactivating = true; deactivate();
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
}
