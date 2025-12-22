import java.awt.*;

public class OPiece extends Mino{
    public OPiece(GameManager gm){
        super(gm);
        create(Color.YELLOW);
        type = "O";
    }
    public void setXY(int x, int y) {
        //o o
        //o o
        b[0].x = x;
        b[0].y = y;
        b[1].x = x + Block.SIZE;
        b[1].y = y;
        b[2].x = x;
        b[2].y = y + Block.SIZE;
        b[3].x = x + Block.SIZE;
        b[3].y = y + Block.SIZE;
    }
    // No rotation needed for O piece
}
