import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;

public class PacMan extends JPanel implements ActionListener, KeyListener {

    /**
     * Block class represents a block in the game, such as walls, ghosts, and Pacman.
     */
    public class Block {
        int x;
        int y;
        final int width;
        final int height;
        Image image;

        boolean isScared = false;

        final int startX;
        final int startY;

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
         * @param direction The direction in which the block is moving
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

        /**
         * Resets the position of the block to its starting position.
         */
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
    private final Image SCARED_GHOST_IMAGE;

    private final Image CHERRY_IMAGE;
    private final Image POWER_FOOD_IMAGE;

    private final Image PACMAN_UP_IMAGE;
    private final Image PACMAN_DOWN_IMAGE;
    private final Image PACMAN_LEFT_IMAGE;
    private final Image PACMAN_RIGHT_IMAGE;

    int level = 1;

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    HashSet<Block> cherries;
    Block pacman;
    Block powerFood;

    int frameCount = 0;
    
    final Timer gameLoop;
    final char[] directions = {'U', 'D', 'L', 'R'};
    final Random random = new Random();

    int highScore = 0;
    int score = 0;
    int lives = 3;
    int ghostScareTime = 0;
    final int GHOST_SCARED_DURATION = 10_000; // Duration for which ghosts are scared
    boolean ghostsScared = false;
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
        WALL_IMAGE = new ImageIcon(Objects.requireNonNull(getClass().getResource("images/wall.png"))).getImage();
        BLUE_GHOST_IMAGE = new ImageIcon(Objects.requireNonNull(getClass().getResource("images/blueGhost.png"))).getImage();
        ORANGE_GHOST_IMAGE = new ImageIcon(Objects.requireNonNull(getClass().getResource("images/orangeGhost.png"))).getImage();
        PINK_GHOST_IMAGE = new ImageIcon(Objects.requireNonNull(getClass().getResource("images/pinkGhost.png"))).getImage();
        RED_GHOST_IMAGE = new ImageIcon(Objects.requireNonNull(getClass().getResource("images/redGhost.png"))).getImage();
        SCARED_GHOST_IMAGE = new ImageIcon(Objects.requireNonNull(getClass().getResource("images/scaredGhost.png"))).getImage();

        PACMAN_UP_IMAGE = new ImageIcon(Objects.requireNonNull(getClass().getResource("images/pacmanUp.png"))).getImage();
        PACMAN_DOWN_IMAGE = new ImageIcon(Objects.requireNonNull(getClass().getResource("images/pacmanDown.png"))).getImage();
        PACMAN_LEFT_IMAGE = new ImageIcon(Objects.requireNonNull(getClass().getResource("images/pacmanLeft.png"))).getImage();
        PACMAN_RIGHT_IMAGE = new ImageIcon(Objects.requireNonNull(getClass().getResource("images/pacmanRight.png"))).getImage();

        CHERRY_IMAGE = new ImageIcon(Objects.requireNonNull(getClass().getResource("images/cherry.png"))).getImage();
        POWER_FOOD_IMAGE = new ImageIcon(Objects.requireNonNull(getClass().getResource("images/powerFood.png"))).getImage();

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
        walls = new HashSet<>();
        foods = new HashSet<>();
        ghosts = new HashSet<>();
        cherries = new HashSet<>();

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
                    case 'F' -> powerFood = new Block(POWER_FOOD_IMAGE, x, y, TILE_SIZE, TILE_SIZE); // Power food
                    case ' ' -> { // Food and Cherry
                        // Maximum number of cherries on the board
                        int MAX_CHERRIES = 5;
                        if (cherries.size() <= MAX_CHERRIES && random.nextInt(100) == 0) {
                            Block cherry = new Block(CHERRY_IMAGE, x, y, TILE_SIZE, TILE_SIZE);
                            cherries.add(cherry);
                        } else {
                            Block food = new Block(null, x + 14, y + 14, 4, 4);
                            foods.add(food);
                        }
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
            Image ghostImage = (ghostsScared && ghost.isScared) ? SCARED_GHOST_IMAGE : ghost.image;
            g.drawImage(ghostImage, ghost.x, ghost.y, ghost.width, ghost.height, null);
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

        // Draw cherries
        if (!cherries.isEmpty()) {
            for (Block cherry : cherries) {
                g.drawImage(cherry.image, cherry.x, cherry.y, cherry.width, cherry.height, null);
            }
        }

        // Draw power food
        if (powerFood != null)
            g.drawImage(powerFood.image, powerFood.x, powerFood.y, powerFood.width, powerFood.height, null);


        // ==== Draw HUD ====


        // Draw score
        int scorePosX = TILE_SIZE / 2;
        int scorePosY = (int) (TILE_SIZE / 1.5);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("x" + lives + " Score: " + score, scorePosX, scorePosY);

        // Draw level
        g.drawString("Level: " + level, BOARD_WIDTH - 150, scorePosY);

        // Draw high score
        g.drawString("High Score: " + highScore, BOARD_WIDTH - 380, scorePosY);

        if (gameOver) { // Game Over message
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("GAME OVER", BOARD_WIDTH / 2 - 150, BOARD_HEIGHT / 2);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Press R to restart", BOARD_WIDTH / 2 - 80, BOARD_HEIGHT / 2 + 50);
        } else if (paused) { // Draw pause message
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
        if (gameOver || paused)
            return;

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
        checkIfOutOfBound(pacman);

        for (Block ghost : ghosts) {
            // Check for collision between ghost and Pacman
            if (collision(ghost, pacman)) {
                if (ghostsScared && ghost.isScared) {
                    addScore(200);
                    ghost.isScared = false;
                    ghost.reset();
                    char newDirection = directions[random.nextInt(directions.length)];
                    ghost.updateDirection(newDirection);
                } else {
                    lives--;
                    if (lives <= 0)
                        gameOver = true;
                    resetPosition();
                }
            }

            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            // Make the ghost move in a random direction every x frames
            // Ghosts move every 50 frames
            int GHOST_FRAME_RATE = 50;
            if (frameCount == GHOST_FRAME_RATE) {
                frameCount = 0;
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
            checkIfOutOfBound(ghost);
        }

        // Check for collision between Pacman and power food
        if (powerFood != null && collision(pacman, powerFood)) {
            addScore(100);
            for (Block ghost : ghosts) {
                ghost.isScared = true;
            }
            ghostsScared = true;
            powerFood = null; // Remove power food from the board
        }

        // Check for collision between Pacman and cherries
        Block cherryEaten = null;
        for (Block cherry : cherries) {
            if (collision(pacman, cherry)) {
                cherryEaten = cherry;
                addScore(50);
                break;
            }
        }
        cherries.remove(cherryEaten);

        // Check for collision between Pacman and food
        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                addScore(10);
                break;
            }
        }
        foods.remove(foodEaten);

        // Check if all food is eaten
        if (foods.isEmpty()) {
            level++;
            loadMap();
            resetPosition();
            frameCount = 0;
            addScore(100);
        }
    }

    /**
     * Adds score to the current score and updates the high score if necessary.
     * @param score The score to be added, default is 20
     */
    private void addScore(int score) {
        this.score += score;
        if (this.score >= highScore)
            highScore = this.score;
    }

    /**
     * Checks if a block is out of bounds and teleports it to the other side of the screen.
     * @param block The block to check
     */
    private void checkIfOutOfBound(Block block) {
        if (block.x <= -block.width) {
            block.x = BOARD_WIDTH;
        } else if (block.x >= BOARD_WIDTH) {
            block.x = -block.width;
        }
        if (block.y <= -block.height) {
            block.y = BOARD_HEIGHT;
        } else if (block.y >= BOARD_HEIGHT) {
            block.y = -block.height;
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

    /**
     * Resets the position of Pacman and the ghosts.
     */
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
        if (paused || gameOver) {
            gameLoop.stop();
            return;
        }

        // Scare the ghosts for a limited time
        if (ghostsScared) {
            ghostScareTime += 50;
            if (ghostScareTime >= GHOST_SCARED_DURATION) {
                ghostsScared = false;
                ghostScareTime = 0;
            }
        }

        frameCount++;
        move();
        this.repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_R && gameOver) {
            level = 1;
            loadMap();
            resetPosition();
            lives = 3;
            score = 0;
            gameOver = false;
            frameCount = 0;
            gameLoop.start();
            return;
        }

        // Pausing the game when the space bar is pressed
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !gameOver) {
            paused = !paused;
            if (paused) {
                gameLoop.stop();
                frameCount = 0;
            } else {
                gameLoop.start();
            }
            this.repaint();
            return;
        }

        // Preventing user to change the direction when the game is paused or game over
        if (paused || gameOver)
            return;

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