import java.awt.*;

public class Mino {

    public Block b[] = new Block[4];
    public Block temp[] = new Block[4];

    int autoDropCounter = 0;
    public int direction = 1; // 1: up, 2: right, 3: down, 4: left

    boolean leftCollision, rightCollision, bottomCollision;

    public boolean active = true;
    public boolean deactivating;
    int deactivateCounter = 0;
    public boolean justRotated = false;

    public void create(Color c){
        for(int i=0; i<4; i++){
            b[i] = new Block(c);
            temp[i] = new Block(c);
        }
    }
    public void setXY(int x, int y){}
    public void updateXY(int direction){
        checkRotationCollision();
        if(!leftCollision && !rightCollision && !bottomCollision){
            this.direction = direction;
            b[0].x = temp[0].x;
            b[0].y = temp[0].y;
            b[1].x = temp[1].x;
            b[1].y = temp[1].y;
            b[2].x = temp[2].x;
            b[2].y = temp[2].y;
            b[3].x = temp[3].x;
            b[3].y = temp[3].y;
        }
    }
    public void upDirection(){}
    public void rightDirection(){}
    public void downDirection(){}
    public void leftDirection(){}

    public void checkMovementCollision() {
        leftCollision = false;
        rightCollision = false;
        bottomCollision = false;
        for (int i = 0; i < b.length; i++) {
            if (b[i].x <= GameManager.leftX) leftCollision = true;
        }
        for (int i = 0; i < b.length; i++) {
            if (b[i].x + Block.SIZE >= GameManager.rightX) rightCollision = true;
        }
        for (int i = 0; i < b.length; i++) {
            if (b[i].y + Block.SIZE >= GameManager.bottomY) bottomCollision = true;
        }
    }
    public void checkRotationCollision(){
        leftCollision = false;
        rightCollision = false;
        bottomCollision = false;
        for (int i = 0; i < b.length; i++) {
            if(temp[i].x < GameManager.leftX) leftCollision = true;
        }
        for (int i = 0; i < b.length; i++) {
            if(temp[i].x + Block.SIZE > GameManager.rightX) rightCollision = true;
        }
        for (int i = 0; i < b.length; i++) {
            if (temp[i].y + Block.SIZE > GameManager.bottomY) bottomCollision = true;
        }
    }
    public void checkBlockCollision() {
        // Reset all movement collisions
        leftCollision = false;
        rightCollision = false;
        bottomCollision = false;

        for (int i = 0; i < GameManager.placedBlocks.size(); i++) {
            int targetX = GameManager.placedBlocks.get(i).x;
            int targetY = GameManager.placedBlocks.get(i).y;

            for (int j = 0; j < 4; j++) {
                // Check left side
                if (b[j].x - Block.SIZE == targetX && b[j].y == targetY)
                    leftCollision = true;

                // Check right side
                if (b[j].x + Block.SIZE == targetX && b[j].y == targetY)
                    rightCollision = true;

                // Check bottom
                if (b[j].x == targetX && b[j].y + Block.SIZE == targetY)
                    bottomCollision = true;
            }
        }
    }

    boolean rotationBlockCollision;
    public void checkRotationBlockCollision() {
        rotationBlockCollision = false; // reset

        for (int i = 0; i < GameManager.placedBlocks.size(); i++) {
            int targetX = GameManager.placedBlocks.get(i).x;
            int targetY = GameManager.placedBlocks.get(i).y;

            for (int j = 0; j < 4; j++) {
                if (temp[j].x == targetX && temp[j].y == targetY) {
                    rotationBlockCollision = true;
                    return; // no need to check further
                }
            }
        }
    }

    public void rotate(int rotationType) {
        // Copy current positions into temp
        for (int i = 0; i < 4; i++) {
            temp[i].x = b[i].x;
            temp[i].y = b[i].y;
        }

        int newDirection = direction;

        // Apply rotation preview
        switch(rotationType) {
            case 0: // clockwise
                if(direction == 1) { rightDirection(); newDirection = 2; }
                else if(direction == 2) { downDirection(); newDirection = 3; }
                else if(direction == 3) { leftDirection(); newDirection = 4; }
                else if(direction == 4) { upDirection(); newDirection = 1; }
                break;
            case 1: // counterclockwise
                if(direction == 1) { downDirection(); newDirection = 4; }
                else if(direction == 2) { leftDirection(); newDirection = 1; }
                else if(direction == 3) { upDirection(); newDirection = 2; }
                else if(direction == 4) { rightDirection(); newDirection = 3; }
                break;
            case 2: // 180 degrees
                if(direction == 1) { leftDirection(); newDirection = 3; }
                else if(direction == 2) { upDirection(); newDirection = 4; }
                else if(direction == 3) { rightDirection(); newDirection = 1; }
                else if(direction == 4) { downDirection(); newDirection = 2; }
                break;
        }

        // Check all collision types
        checkRotationCollision();       // walls/floor
        checkRotationBlockCollision();  // placed blocks

        if (!leftCollision && !rightCollision && !bottomCollision && !rotationBlockCollision) {
            // Safe to apply rotation
            for (int i = 0; i < 4; i++) {
                b[i].x = temp[i].x;
                b[i].y = temp[i].y;
            }
            direction = newDirection;
        }

        justRotated = true;
    }


    private void deactivate() {
        // Count frames of being stationary
        deactivateCounter++;

        // Run both checks before using the result
        leftCollision = rightCollision = bottomCollision = false;
        checkMovementCollision();
        checkBlockCollision();

        if (bottomCollision) {
            // If touching something, start grace countdown
            if (deactivateCounter >= 45) {
                active = false;
                deactivateCounter = 0;
            }
        } else {
            // Reset if no longer touching ground
            deactivateCounter = 0;
            deactivating = false;
        }
    }
    public void update(){
        if(GameManager.gameState == GameManager.PLAYING) {
            leftCollision = rightCollision = bottomCollision = false;
            rotationBlockCollision = false; // if you use it
            checkMovementCollision();
            checkBlockCollision();
            if (deactivating) deactivate();
            if (KeyHandler.upPressed) {
                rotate(0); // clockwise rotation
                KeyHandler.upPressed = false;
            }
            checkMovementCollision();
            checkBlockCollision();
            if (KeyHandler.leftPressed) {
                justRotated = false;
                boolean canMoveLeft = true;

                for (Block blk : b) {
                    // Wall collision
                    if (blk.x - Block.SIZE < GameManager.leftX) {
                        canMoveLeft = false;
                        break;
                    }
                    // Collision with placed blocks
                    for (Block placed : GameManager.placedBlocks) {
                        if (blk.x - Block.SIZE == placed.x && blk.y == placed.y) {
                            canMoveLeft = false;
                            break;
                        }
                    }
                    if (!canMoveLeft) break;
                }

// Apply move only if valid
                if (canMoveLeft) {
                    for (Block blk : b) {
                        blk.x -= Block.SIZE;
                    }
                }
                KeyHandler.leftPressed = false;
            }
            if (KeyHandler.rightPressed) {
                justRotated = false;
                // compute proposed position
                boolean canMoveRight = true;
                for (Block blk : b) {
                    if (blk.x + Block.SIZE >= GameManager.rightX) canMoveRight = false;
                    for (Block placed : GameManager.placedBlocks) {
                        if (blk.x + Block.SIZE == placed.x && blk.y == placed.y) canMoveRight = false;
                    }
                }

                // apply move only if valid
                if (canMoveRight) {
                    for (Block blk : b) {
                        blk.x += Block.SIZE;
                    }
                }

                KeyHandler.rightPressed = false;
            }

            if (KeyHandler.downPressed && !bottomCollision && !deactivating) {
                for (int i = 0; i < 4; i++) {
                    b[i].y += Block.SIZE;
                }
                KeyHandler.downPressed = false;
                autoDropCounter = 0;
            }

            if (KeyHandler.zPressed) {
                rotate(2);
                KeyHandler.zPressed = false;
            }
            if (KeyHandler.aPressed) {
                rotate(1);
                KeyHandler.aPressed = false;
            }
            if (bottomCollision) {
                deactivating = true;
            }
            if (KeyHandler.shiftPressed) {
                GameManager.hold = true;
                KeyHandler.shiftPressed = false;
            } else {
                autoDropCounter++;
                if (autoDropCounter == GameManager.dropInterval) {
                    for (int i = 0; i < 4; i++) {
                        b[i].y += Block.SIZE;
                    }
                    autoDropCounter = 0;
                }
            }
        }
    }
    public void hardDrop() {
        int safety = 0;

        while (true) {
            boolean willCollide = false;

            // Predict collision before moving
            for (Block blk : b) {
                int nextY = blk.y + Block.SIZE;

                // Bottom of board
                if (nextY >= GameManager.bottomY) {
                    willCollide = true;
                    break;
                }

                // Collision with placed blocks
                for (Block placed : GameManager.placedBlocks) {
                    if (blk.x == placed.x && nextY == placed.y) {
                        willCollide = true;
                        break;
                    }
                }
                if (willCollide) break;
            }

            if (willCollide || safety > 100) break;

            // Safe to move
            for (int i = 0; i < 4; i++) {
                b[i].y += Block.SIZE;
            }

            safety++;
        }

        active = false;
        deactivating = false;
        bottomCollision = true;
        System.out.println("Hard drop finished after " + safety + " steps");
    }


    public void draw(Graphics2D g2){
        int margin = 2;
        g2.setColor(b[0].color);
        for(int i=0; i<4; i++){
            g2.fillRect(b[i].x + margin, b[i].y + margin, Block.SIZE - 2*margin, Block.SIZE - 2*margin);
        }
    }
}
