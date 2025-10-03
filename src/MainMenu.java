import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MainMenu extends JPanel implements MouseListener {
    public MainMenu(){
        this.setPreferredSize(new Dimension(Board.WIDTH, Board.HEIGHT));
        this.setBackground(Color.BLACK);
        this.setLayout(null);
        this.setFocusable(true);

    }
    private void update(){}
    public void paintComponent(Graphics g){
        super.paintComponent(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
