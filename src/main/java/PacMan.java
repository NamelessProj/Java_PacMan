import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Random;

public class PacMan extends JPanel implements ActionListener, KeyListener {
    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;

        private final int VELOCITY;

        char direction = 'U'; // U = Up, D = Down, L = Left, R = Right
        int velocityX = 0;
        int velocityY = 0;

        /**
         * Constructor for the Block class.
         * @param image Image to be used for the block
         * @param x The x-coordinate of the block
         * @param y The y-coordinate of the block
         * @param width Width of the block
         * @param height Height of the block
         */
        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;

            this.VELOCITY = width / 4;

            this.startX = x;
            this.startY = y;
        }

        /**
         * Updates the position in which the block is moving.
         */
        public void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();

            this.x += this.velocityX;
            this.y += this.velocityY;

            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        /**
         * Updates the velocity of the block based on its direction.
         */
        private void updateVelocity() {
            switch (this.direction) {
                case 'U' -> {
                    this.velocityX = 0;
                    this.velocityY = -this.VELOCITY;
                }
                case 'D' -> {
                    this.velocityX = 0;
                    this.velocityY = this.VELOCITY;
                }
                case 'L' -> {
                    this.velocityX = -this.VELOCITY;
                    this.velocityY = 0;
                }
                case 'R' -> {
                    this.velocityX = this.VELOCITY;
                    this.velocityY = 0;
                }
            }
        }

        public void reset() {
            this.x = startX;
            this.y = startY;
        }
    }



    private final int ROW_COUNT;
    private final int COLUMN_COUNT;
    private final int TILE_SIZE;
    private final int BOARD_WIDTH;
    private final int BOARD_HEIGHT;

    private final Image WALL_IMAGE;
    private final Image BLUE_GHOST_IMAGE;
    private final Image ORANGE_GHOST_IMAGE;
    private final Image PINK_GHOST_IMAGE;
    private final Image RED_GHOST_IMAGE;

    private final Image PACMAN_UP_IMAGE;
    private final Image PACMAN_DOWN_IMAGE;
    private final Image PACMAN_LEFT_IMAGE;
    private final Image PACMAN_RIGHT_IMAGE;

    int level = 1;

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    Timer gameLoop;
    int frameCount = 0;
    char[] directions = {'U', 'D', 'L', 'R'};
    Random random = new Random();
    int score = 0;
    int lives = 3;
    boolean paused = true;
    boolean gameOver = false;

    /**
     * Constructor for the PacMan class.
     * @param boardWidth Width of the board
     * @param boardHeight Height of the board
     * @param tileSize Size of each tile
     */
    PacMan(int boardWidth, int boardHeight, int tileSize) {
        this.BOARD_WIDTH = boardWidth;
        this.BOARD_HEIGHT = boardHeight;
        this.TILE_SIZE = tileSize;
        this.ROW_COUNT = boardHeight / tileSize;
        this.COLUMN_COUNT = boardWidth / tileSize;

        // Set the size of the JPanel
        this.setPreferredSize(new Dimension(boardWidth, boardHeight));
        this.setBackground(Color.BLACK);
        this.addKeyListener(this);
        this.setFocusable(true);

        // Load images
        WALL_IMAGE = new ImageIcon("src/main/resources/wall.png").getImage();
        BLUE_GHOST_IMAGE = new ImageIcon("src/main/resources/blueGhost.png").getImage();
        ORANGE_GHOST_IMAGE = new ImageIcon("src/main/resources/orangeGhost.png").getImage();
        PINK_GHOST_IMAGE = new ImageIcon("src/main/resources/pinkGhost.png").getImage();
        RED_GHOST_IMAGE = new ImageIcon("src/main/resources/redGhost.png").getImage();

        PACMAN_UP_IMAGE = new ImageIcon("src/main/resources/pacmanUp.png").getImage();
        PACMAN_DOWN_IMAGE = new ImageIcon("src/main/resources/pacmanDown.png").getImage();
        PACMAN_LEFT_IMAGE = new ImageIcon("src/main/resources/pacmanLeft.png").getImage();
        PACMAN_RIGHT_IMAGE = new ImageIcon("src/main/resources/pacmanRight.png").getImage();

        // Load the map
        loadMap();

        // Making the ghosts move in random directions
        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(directions.length)];
            ghost.updateDirection(newDirection);
        }

        // Set up the game loop
        gameLoop = new Timer(50, this);
        gameLoop.start();
    }

    /**
     * Loads the map from the tileMap array.
     */
    public void loadMap() {
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();

        String[] tileMap = Level.getLevel(level);

        int tileMapLength = tileMap.length;

        for (int r = 0; r < ROW_COUNT; r++) {
            if (r >= tileMapLength)
                break; // Prevents ArrayIndexOutOfBoundsException

            String row = tileMap[r];
            int y = r * TILE_SIZE;

            int rowLength = row.length();

            for (int c = 0; c < COLUMN_COUNT; c++) {
                if (c >= rowLength)
                    break; // Prevents ArrayIndexOutOfBoundsException

                char tileMapChar = row.charAt(c);

                int x = c * TILE_SIZE;

                switch (tileMapChar) {
                    case 'X' -> { // Wall
                        Block wall = new Block(WALL_IMAGE, x, y, TILE_SIZE, TILE_SIZE);
                        walls.add(wall);
                    }
                    case 'b' -> { // Blue Ghost
                        Block ghost = new Block(BLUE_GHOST_IMAGE, x, y, TILE_SIZE, TILE_SIZE);
                        ghosts.add(ghost);
                    }
                    case 'o' -> { // Orange Ghost
                        Block ghost = new Block(ORANGE_GHOST_IMAGE, x, y, TILE_SIZE, TILE_SIZE);
                        ghosts.add(ghost);
                    }
                    case 'p' -> { // Pink Ghost
                        Block ghost = new Block(PINK_GHOST_IMAGE, x, y, TILE_SIZE, TILE_SIZE);
                        ghosts.add(ghost);
                    }
                    case 'r' -> { // Red Ghost
                        Block ghost = new Block(RED_GHOST_IMAGE, x, y, TILE_SIZE, TILE_SIZE);
                        ghosts.add(ghost);
                    }
                    case 'P' -> pacman = new Block(PACMAN_RIGHT_IMAGE, x, y, TILE_SIZE, TILE_SIZE); // Pacman
                    case ' ' -> { // Food
                        Block food = new Block(null, x + 14, y + 14, 4, 4);
                        foods.add(food);
                    }
                }
            }
        }
    }

    /**
     * Paints the components of the game.
     * @param g Graphics object used for painting
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    /**
     * Draws the game components.
     * @param g Graphics object used for drawing
     */
    public void draw(Graphics g) {
        // Draw Pacman
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        // Draw each ghost
        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        // Draw each wall
        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        // Draw each food (dot)
        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }

        // Draw score
        int scorePosX = TILE_SIZE / 2;
        int scorePosY = (int) (TILE_SIZE / 1.5);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        if (gameOver) {
            g.drawString("Game Over: " + score, scorePosX, scorePosY);
        } else {
            g.drawString("x" + lives + " Score: " + score, scorePosX, scorePosY);
        }

        // Draw level
        g.drawString("Level: " + level, BOARD_WIDTH - 150, scorePosY);

        // Draw pause message
        if (paused) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("PAUSED", BOARD_WIDTH / 2 - 100, BOARD_HEIGHT / 2);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Press SPACE to continue", BOARD_WIDTH / 2 - 120, BOARD_HEIGHT / 2 + 50);
        }
    }

    /**
     * Moves the Pacman character and the ghosts.
     */
    public void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        // Check for collision between Pacman and walls
        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        // Check if Pacman get out of the screen and teleport to the other side
        if (pacman.x <= -pacman.width) {
            pacman.x = BOARD_WIDTH;
        } else if (pacman.x >= BOARD_WIDTH) {
            pacman.x = -pacman.width;
        }
        if (pacman.y <= -pacman.height) {
            pacman.y = BOARD_HEIGHT;
        } else if (pacman.y >= BOARD_HEIGHT) {
            pacman.y = -pacman.height;
        }

        for (Block ghost : ghosts) {
            // Check for collision between ghost and Pacman
            if (collision(ghost, pacman)) {
                lives--;
                if (lives == 0)
                    gameOver = true;
                resetPosition();
            }

            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            // Make the ghost move in a random direction every 50 frames
            if (frameCount % 50 == 0) {
                char newDirection = directions[random.nextInt(directions.length)];
                ghost.updateDirection(newDirection);
            }

            // Check for collision between ghost and walls
            for (Block wall : walls) {
                if (collision(ghost, wall)) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                    break;
                }
            }

            // Check if the ghost gets out of the screen and teleports to the other side
            if (ghost.x <= -ghost.width) {
                ghost.x = BOARD_WIDTH;
            } else if (ghost.x >= BOARD_WIDTH) {
                ghost.x = -ghost.width;
            }
            if (ghost.y <= -ghost.height) {
                ghost.y = BOARD_HEIGHT;
            } else if (ghost.y >= BOARD_HEIGHT) {
                ghost.y = -ghost.height;
            }
        }

        // Check for collision between Pacman and food
        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten);

        // Check if all food is eaten
        if (foods.isEmpty()) {
            level++;
            loadMap();
            resetPosition();
            frameCount = 0;
            score += 100;
        }
    }

    /**
     * Checks for collisions between two blocks.
     * @param a First block
     * @param b Second block
     * @return True if there is a collision, false otherwise
     */
    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public void resetPosition() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;

        for (Block ghost : ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(directions.length)];
            ghost.updateDirection(newDirection);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (paused)
            return;

        frameCount++;
        move();
        this.repaint();
        if (gameOver)
            gameLoop.stop();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) {
            level = 1;
            loadMap();
            resetPosition();
            lives = 3;
            score = 0;
            gameOver = false;
            frameCount = 0;
            gameLoop.start();
        }

        // Pausing the game when the space bar is pressed
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            paused = !paused;
            if (paused) {
                gameLoop.stop();
                frameCount = 0;
            } else {
                gameLoop.start();
            }
            this.repaint();
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> pacman.updateDirection('U');
            case KeyEvent.VK_DOWN -> pacman.updateDirection('D');
            case KeyEvent.VK_LEFT -> pacman.updateDirection('L');
            case KeyEvent.VK_RIGHT -> pacman.updateDirection('R');
        }

        switch (pacman.direction) {
            case 'U' -> pacman.image = PACMAN_UP_IMAGE;
            case 'D' -> pacman.image = PACMAN_DOWN_IMAGE;
            case 'L' -> pacman.image = PACMAN_LEFT_IMAGE;
            case 'R' -> pacman.image = PACMAN_RIGHT_IMAGE;
        }
    }
}