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
    public void checkBlockCollision(){
        for(int i = 0; i < GameManager.placedBlocks.size(); i++){
            int targetX = GameManager.placedBlocks.get(i).x;
            int targetY = GameManager.placedBlocks.get(i).y;

            for(int j = 0; j < 4; j++){
                if(b[j].x - Block.SIZE == targetX && b[j].y == targetY) leftCollision = true;
                if(b[j].x + Block.SIZE == targetX && b[j].y == targetY) rightCollision = true;
                if(b[j].x == targetX && b[j].y + Block.SIZE == targetY) bottomCollision = true;
            }

        }
    }
    private void wallKick(int rotationType) {
        // rotationType: 0 = clockwise, 1 = counterclockwise, 2 = 180 degrees

        // Copy current positions into temp
        for (int i = 0; i < 4; i++) {
            temp[i].x = b[i].x;
            temp[i].y = b[i].y;
        }

        // Rotate temp blocks based on type
        switch(rotationType) {
            case 0: // clockwise
                if(direction == 1) rightDirection();
                else if(direction == 2) downDirection();
                else if(direction == 3) leftDirection();
                else if(direction == 4) upDirection();
                break;
            case 1: // counterclockwise
                if(direction == 1) downDirection();
                else if(direction == 2) leftDirection();
                else if(direction == 3) upDirection();
                else if(direction == 4) rightDirection();
                break;
            case 2: // 180 degrees
                if(direction == 1) leftDirection();
                else if(direction == 2) upDirection();
                else if(direction == 3) rightDirection();
                else if(direction == 4) downDirection();
                break;
        }
//
//        // Wall kick offsets to try
//        int[][] kicks = {
//                {0, 0},
//                {-Block.SIZE, 0},
//                {Block.SIZE, 0},
//                {0, -Block.SIZE},
//        };
//
//        for (int[] kick : kicks) {
//            boolean canRotate = true;
//
//            for (Block t : temp) {
//                int newX = t.x + kick[0];
//                int newY = t.y + kick[1];
//
//                if (newX < GameManager.leftX || newX + Block.SIZE > GameManager.rightX || newY + Block.SIZE > GameManager.bottomY) {
//                    canRotate = false;
//                    break;
//                }
//
//                for (Block placed : GameManager.placedBlocks) {
//                    if (newX == placed.x && newY == placed.y) {
//                        canRotate = false;
//                        break;
//                    }
//                }
//
//                if (!canRotate) break;
//            }
//
//            if (canRotate) {
//                for (int i = 0; i < 4; i++) {
//                    b[i].x = temp[i].x + kick[0];
//                    b[i].y = temp[i].y + kick[1];
//                }
//
//                // Update direction state
//                if(rotationType == 0) { // clockwise
//                    direction = direction % 4 + 1;
//                } else if (rotationType == 1) { // counterclockwise
//                    direction = (direction - 2 + 4) % 4 + 1;
//                } else if (rotationType == 2) { // 180 degrees
//                    direction = (direction + 1) % 4 + 1;
//                }
//
//                return; // rotation applied
//            }
//        }
    }

    private void deactivate(){
        deactivateCounter++;
        if(deactivateCounter == 45){
            deactivateCounter = 0;
            checkMovementCollision();
            if(bottomCollision){
                active = false;
            }
            else{
                deactivating = false;
            }
            checkBlockCollision();
            if(bottomCollision){
                active = false;
            }
            else{
                deactivating = false;
            }
        }
    }
    public void update(){
        if(GameManager.gameState == GameManager.PLAYING) {
            if (deactivating) deactivate();
            if (KeyHandler.upPressed) {
                wallKick(0); // clockwise rotation
                KeyHandler.upPressed = false;
            }
            checkMovementCollision();
            checkBlockCollision();
            if (KeyHandler.leftPressed) {
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
                wallKick(2);
                KeyHandler.zPressed = false;
            }
            if (KeyHandler.aPressed) {
                wallKick(1);
                KeyHandler.aPressed = false;
            }
            if (KeyHandler.spacePressed) { // hard drop
                while (!bottomCollision && !deactivating) {
                    for (int i = 0; i < 4; i++) {
                        b[i].y += Block.SIZE;
                    }
                    checkMovementCollision();
                    checkBlockCollision();
                }
                active = false;
                KeyHandler.spacePressed = false;
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
    public void draw(Graphics2D g2){
        int margin = 2;
        g2.setColor(b[0].color);
        for(int i=0; i<4; i++){
            g2.fillRect(b[i].x + margin, b[i].y + margin, Block.SIZE - 2*margin, Block.SIZE - 2*margin);
        }
    }
}
