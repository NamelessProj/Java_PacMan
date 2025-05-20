import javax.swing.*;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        final int ROW_COUNT = 21;
        final int COLUMN_COUNT = 19;
        final int TILE_SIZE = 32;
        final int BOARD_WIDTH = TILE_SIZE * COLUMN_COUNT;
        final int BOARD_HEIGHT = TILE_SIZE * ROW_COUNT;

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(Main.class.getResource("images/pacmanRight.png")));

        JFrame frame = new JFrame("Pac Man");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(icon.getImage());
        frame.setSize(BOARD_WIDTH, BOARD_HEIGHT);
        frame.setResizable(false);

        PacMan pacmanGame = new PacMan(BOARD_WIDTH, BOARD_HEIGHT, TILE_SIZE);
        frame.add(pacmanGame);
        frame.pack();
        pacmanGame.requestFocus();
        frame.setVisible(true);
    }
}