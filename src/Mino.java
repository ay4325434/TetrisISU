import javax.swing.*;
import java.awt.*;

public abstract class Mino {

    public Block b[] = new Block[4];
    public Block temp[] = new Block[4];

    int autoDropCounter = 0;
    public int direction = 1; // 1: up, 2: right, 3: down, 4: left

    boolean leftCollision, rightCollision, bottomCollision;

    public boolean active = true;
    public boolean deactivating;
    int deactivateCounter = 0;
    public boolean justRotated = false;
    public String type;
    public boolean spin = false;
    public boolean rotatedDuringLockDelay = false;

    public void create(Color c){
        for(int i=0; i<4; i++){
            b[i] = new Block(c);
            temp[i] = new Block(c);
        }
    }

    public abstract void setXY(int x, int y);
    // Direction methods removed — pivot-based rotation will handle everything

    // --- COLLISIONS ---
    public void checkMovementCollision() {
        leftCollision = rightCollision = bottomCollision = false;
        for (int i = 0; i < b.length; i++) {
            if (b[i].x <= GameManager.leftX) leftCollision = true;
            if (b[i].x + Block.SIZE >= GameManager.rightX) rightCollision = true;
            if (b[i].y + Block.SIZE >= GameManager.bottomY) bottomCollision = true;
        }
    }

    public void checkBlockCollision() {
        leftCollision = rightCollision = bottomCollision = false;

        int[] pieceGridX = new int[4];
        int[] pieceGridY = new int[4];
        for (int i = 0; i < 4; i++) {
            pieceGridX[i] = b[i].x / Block.SIZE;
            pieceGridY[i] = b[i].y / Block.SIZE;
        }

        for (Block placed : GameManager.placedBlocks) {
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

    boolean rotationBlockCollision;
    public void checkRotationBlockCollision() {
        rotationBlockCollision = false;
        for (Block placed : GameManager.placedBlocks) {
            for (int j = 0; j < 4; j++) {
                if (temp[j].x == placed.x && temp[j].y == placed.y) {
                    rotationBlockCollision = true;
                    return;
                }
            }
        }
    }

    // --- PIVOT ROTATIONS ---
    private void rotateTempCW() {
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

    private void rotateTempCCW() {
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

    public void rotate(int rotationType) {
        if(type.equals("O")) return;
        justRotated = true;

        if (active) rotatedDuringLockDelay = true;

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
            // rotation failed → restore b[] from temp baseline
            for (int i = 0; i < 4; i++) {
                temp[i].x = b[i].x;
                temp[i].y = b[i].y;
            }
        }
    }
    private boolean attemptRotationWithKicks(int fromDir, int toDir, int rotationType) {
        int[][] kicks = getWallKicks(fromDir, toDir, rotationType);

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
                // Kick successful → commit temp[] to b[]
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

    private int[][] getWallKicks(int from, int to, int rotationType) {
        boolean isI = type.equals("I");

        // JLSTZ
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
        int delay = 90;
        if(isOnFloor()) delay = 45;
        if (!canMoveDown()) {
            deactivateCounter++;
            if (deactivateCounter >= delay) {
                active = false;
                if (rotatedDuringLockDelay) {
                    spin = true;
                    System.out.println("T-spin detected");
                    if (direction == 1) {
                        GameManager.spinMessage = "Mini T-Spin";
                    } else {
                        GameManager.spinMessage = "T-Spin";
                    }
                    GameManager.spinMessageTimer = 120;
                }
                deactivateCounter = 0;
                deactivating = false;
                rotatedDuringLockDelay = false;
            }
        } else {
            deactivateCounter = 0;
            deactivating = false;
            spin = false;
            rotatedDuringLockDelay = false;
        }
    }

    public void update() {
        if (GameManager.gameState != GameManager.PLAYING) return;

        leftCollision = rightCollision = bottomCollision = false;
        rotationBlockCollision = false;
        checkMovementCollision();
        checkBlockCollision();

        if (deactivating) deactivate();

        if (KeyHandler.upPressed) { rotate(0); KeyHandler.upPressed = false; }
        if (KeyHandler.zPressed) { rotate(1); KeyHandler.zPressed = false; }
        if (KeyHandler.aPressed) { rotate(2); KeyHandler.aPressed = false; }

        if (KeyHandler.leftPressed) {
            if (canMove(-Block.SIZE, 0)) {
                for (Block blk : b) blk.x -= Block.SIZE;
                justRotated = false;
            }
            KeyHandler.leftPressed = false;
        }

// RIGHT
        if (KeyHandler.rightPressed) {
            if (canMove(Block.SIZE, 0)) {
                for (Block blk : b) blk.x += Block.SIZE;
                justRotated = false;
            }
            KeyHandler.rightPressed = false;
        }

        if (KeyHandler.downPressed && !deactivating) {
            if (movePieceDown()) { GameManager.score++; deactivateCounter = 0; deactivating = false; }
            else { checkMovementCollision(); checkBlockCollision(); deactivating = true; }
            KeyHandler.downPressed = false;
        }

        checkMovementCollision();
        checkBlockCollision();
        if (bottomCollision || rotationBlockCollision) deactivating = true;

        if (KeyHandler.shiftPressed) { GameManager.hold = true; KeyHandler.shiftPressed = false; }

        autoDropCounter++;
        if (autoDropCounter >= GameManager.dropInterval) {
            if (!movePieceDown()) { checkMovementCollision(); checkBlockCollision(); deactivating = true; }
            else { deactivateCounter = 0; deactivating = false; }
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
            for (Block placed : GameManager.placedBlocks) {
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
            for (Block placed : GameManager.placedBlocks) if (placed.x == b[i].x && placed.y == newY) return false;
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
                if (nextY >= GameManager.bottomY) { willCollide = true; break; }
                for (Block placed : GameManager.placedBlocks) if (blk.x == placed.x && nextY == placed.y) { willCollide = true; break; }
                if (willCollide) break;
            }
            if (willCollide || safety > 100) break;
            for (Block blk : b) blk.y += Block.SIZE;
            safety++; GameManager.score++;
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
