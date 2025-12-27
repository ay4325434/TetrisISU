
import javax.swing.*;
public class Main {
    public static void main(String[] args) {
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