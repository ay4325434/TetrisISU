
import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        SongConstants.addAllElements(); // Initialize song constants
        JFrame frame = new JFrame("Tetris");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Board board = new Board();
        frame.add(board);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        board.startGame();
    }
}