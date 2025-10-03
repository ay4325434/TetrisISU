import java.awt.*;

public class TPiece extends Mino{
    public TPiece(){
        create(Color.MAGENTA);
    }
    public void setXY(int x, int y) {
        //  o
        //o o o
        //
        b[0].x = x;
        b[0].y = y;
        b[1].x = x - Block.SIZE;
        b[1].y = y;
        b[2].x = x;
        b[2].y = y - Block.SIZE;
        b[3].x = x + Block.SIZE;
        b[3].y = y;
    }
    public void upDirection(){
        //  o
        //o o o
        //
        temp[0].x = b[0].x;
        temp[0].y = b[0].y;
        temp[1].x = b[0].x - Block.SIZE;
        temp[1].y = b[0].y;
        temp[2].x = b[0].x;
        temp[2].y = b[0].y - Block.SIZE;
        temp[3].x = b[0].x + Block.SIZE;
        temp[3].y = b[0].y;
        updateXY(1);
    }
    public void rightDirection(){
        //  o
        //  o o
        //  o
        temp[0].x = b[0].x;
        temp[0].y = b[0].y;
        temp[1].x = b[0].x;
        temp[1].y = b[0].y - Block.SIZE;
        temp[2].x = b[0].x + Block.SIZE;
        temp[2].y = b[0].y;
        temp[3].x = b[0].x;
        temp[3].y = b[0].y + Block.SIZE;
        updateXY(2);
    }
    public void downDirection(){
        //
        //o o o
        //  o
        temp[0].x = b[0].x;
        temp[0].y = b[0].y;
        temp[1].x = b[0].x + Block.SIZE;
        temp[1].y = b[0].y;
        temp[2].x = b[0].x;
        temp[2].y = b[0].y + Block.SIZE;
        temp[3].x = b[0].x - Block.SIZE;
        temp[3].y = b[0].y;
        updateXY(3);
    }
    public void leftDirection(){
        //  o
        //o o
        //  o
        temp[0].x = b[0].x;
        temp[0].y = b[0].y;
        temp[1].x = b[0].x;
        temp[1].y = b[0].y + Block.SIZE;
        temp[2].x = b[0].x - Block.SIZE;
        temp[2].y = b[0].y;
        temp[3].x = b[0].x;
        temp[3].y = b[0].y - Block.SIZE;
        updateXY(4);
    }
}
