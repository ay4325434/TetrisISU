/*
This game is a slightly modified version of Tetris, but keeps the original game
logic. The background music has been changed to mostly rhythm game songs to help the
player have a slight sense of rhythm while playing the game. Make sure to turn down your volume
because some songs are loud and noisy.
 */
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