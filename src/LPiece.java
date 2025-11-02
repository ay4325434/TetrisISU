import java.awt.*;

public class LPiece extends Mino{
    public LPiece() {
        create(Color.ORANGE);
        type = "L";
    }
    public void setXY (int x, int y) {
        //o o o
        //o
        b[0].x = x;
        b[0].y = y;
        b[1].x = b[0].x + Block.SIZE;
        b[1].y = b[0].y;
        b[2].x = b[0].x - Block.SIZE;
        b[2].y = b[0].y;
        b[3].x = b[0].x - Block.SIZE;
        b[3].y = b[0].y + Block.SIZE;
    }
}
