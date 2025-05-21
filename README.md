<p align="center">
<img src="https://img.shields.io/badge/Java-v.23.0.2-orange" alt="Java version"/>
</p>

<p align="center">
<img src="https://img.shields.io/github/license/NamelessProj/Java_PacMan" alt="licence"/>
<img src="https://img.shields.io/github/repo-size/NamelessProj/Java_PacMan" alt="repo size"/>
</p>

# Pac-Man Java

This project is a simple reimplementation of the classic **Pac-Man** game in Java, using Swing for the graphical interface.

## Features

- Move Pac-Man with the arrow keys
- Randomly moving ghosts
- Collectible dots and bonus cherries
- Score, lives, and level management
- Pause (Space key) and restart (R key)

## Controls

- **Arrow keys**: Move Pac-Man
- **Space**: Pause / resume the game
- **R**: Restart the game after Game Over

## Requirements

- Java 17 or higher
- Maven

## Installation and Running

1. Clone this repository
    ```bash
    git clone https://github.com/NamelessProj/Java_PacMan.git
   ```
2. Navigate to the project directory:
   ```bash
   cd Java_PacMan
   ```
3. Run the following command to build the project:
   ```bash
   jar -cvf PacMan.jar src/main/java/com/example/pacman/*.java
   ```
4. Run the game using the following command:
   ```bash
    javaw.exe -jar PacMan.jar
   # or
    java -jar PacMan.jar
    ```

## Resources

Game images must be placed in `src/main/java/images/`:
- `pacmanRight.png`, `pacmanLeft.png`, `pacmanUp.png`, `pacmanDown.png`
- `blueGhost.png`, `orangeGhost.png`, `pinkGhost.png`, `redGhost.png`
- `wall.png`, `cherry.png`

## Licence

This project is licensed under the MIT License. See the [`LICENCE`](./LICENCE) file for details.