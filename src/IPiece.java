import java.awt.*;

public class IPiece extends Mino{
    public IPiece(){
        create(Color.CYAN);
    }
    public void setXY(int x, int y) {
        //o o o o
        b[0].x = x;
        b[0].y = y;
        b[1].x = b[0].x - Block.SIZE;
        b[1].y = b[0].y;
        b[2].x = b[0].x + Block.SIZE;
        b[2].y = b[0].y;
        b[3].x = b[0].x + (2 * Block.SIZE);
        b[3].y = b[0].y;
    }
    public void upDirection(){
        //
        //o o o o
        //
        temp[0].x = b[0].x;
        temp[0].y = b[0].y;
        temp[1].x = b[0].x - Block.SIZE;
        temp[1].y = b[0].y;
        temp[2].x = b[0].x + Block.SIZE;
        temp[2].y = b[0].y;
        temp[3].x = b[0].x + (2 * Block.SIZE);
        temp[3].y = b[0].y;

        updateXY(1);
    }
    public void rightDirection(){
        //    o
        //    o
        //    o
        //    o
        temp[0].x = b[0].x;
        temp[0].y = b[0].y;
        temp[1].x = b[0].x;
        temp[1].y = b[0].y - Block.SIZE;
        temp[2].x = b[0].x;
        temp[2].y = b[0].y + Block.SIZE;
        temp[3].x = b[0].x;
        temp[3].y = b[0].y + (2 * Block.SIZE);

        updateXY(2);
    }
    public void downDirection(){
        upDirection();
    }
    public void leftDirection(){
        rightDirection();
    }
}
