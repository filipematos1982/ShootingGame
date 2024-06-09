package io.codeforall.forsome;

import io.codeforall.forsome.characters.Enemy;
import io.codeforall.forsome.characters.NormalEnemy;
import io.codeforall.forsome.characters.Player;
import io.codeforall.forsome.grid.Grid;
import io.codeforall.forsome.level.GameOverScreen;
import io.codeforall.forsome.level.Level;
import io.codeforall.forsome.level.LevelFactory;
import io.codeforall.forsome.level.StartScreen;
import org.academiadecodigo.simplegraphics.keyboard.Keyboard;
import org.academiadecodigo.simplegraphics.keyboard.KeyboardEvent;
import org.academiadecodigo.simplegraphics.keyboard.KeyboardEventType;
import org.academiadecodigo.simplegraphics.keyboard.KeyboardHandler;
import org.academiadecodigo.simplegraphics.graphics.Color;
import org.academiadecodigo.simplegraphics.graphics.Text;

public class Game implements KeyboardHandler {
    private Grid grid;

    private Level level;

    public static int score = 0;
    private Collideable player;
    private Keyboard keyboard;

    private GameOverScreen gameOverScreen;
    private StartScreen startScreen;

    public static GameState gameState;


    private int delay;

    public Game(int width, int height, int delay) {
        this.grid = new Grid(width, height);

        this.level = LevelFactory.createLevel(this.grid, 0, 0, 0, 0, 0, true);

        this.player = new Player(this.grid);

        this.delay = delay;
        this.gameState = GameState.STARTMENU;
        this.gameOverScreen = new GameOverScreen(this.grid);
        this.startScreen = new StartScreen(this.grid);
        this.startScreen.show();

        this.keyboard = new Keyboard(this);
        addKeyboard();

        CollideableManager.addCollideable(player);

    }

    public void start() throws InterruptedException {
        while (gameState == GameState.STARTMENU) {
            Thread.sleep(this.delay);
            startScreen.show();
            //System.out.println("Estou no main menu");
            if(gameState == GameState.INGAME) {
                break;
            }
        }

        level.draw();
        level.scoreBoard();
        level.highestScoreBoard();
        player.show();
        Player p = (Player) this.player;

        while (gameState == GameState.INGAME) {
            Thread.sleep(this.delay);
            //CollideableManager.show();
            CollideableManager.move();
            CollideableManager.detectCollisions(this);
            level.scoreBoard();
            level.highestScoreBoard();
            level.createEnemies();
            p.getWeapon().reload();

            if (score < 0) {
                player.kill();
                //System.out.println("F Player: " + score);
            }

            if (player.isDead()) {
                gameState = GameState.GAMEOVER;
            }
        }

        while (gameState == GameState.GAMEOVER) {
            Thread.sleep(this.delay);
            this.gameOverScreen.show();

            if (gameState == GameState.INGAME) {
                restart(false);
                break;
            }

            if(gameState == GameState.STARTMENU) {
                restart(true);
                break;
            }
        }
    }

    private void addKeyboard() {
        KeyboardEvent escape = new KeyboardEvent();
        escape.setKey(KeyboardEvent.KEY_ESC);
        escape.setKeyboardEventType(KeyboardEventType.KEY_PRESSED);
        keyboard.addEventListener(escape);


        KeyboardEvent startGame = new KeyboardEvent();
        startGame.setKey(KeyboardEvent.KEY_ENTER);
        startGame.setKeyboardEventType(KeyboardEventType.KEY_PRESSED);
        keyboard.addEventListener(startGame);

    }

    private void restart(boolean startMenu) throws InterruptedException {
        if (startMenu) {
            resetVariables(true);
            gameState = GameState.STARTMENU;
            start();
            return;
        }

        System.out.println("entrei e não devia");
        gameState = GameState.INGAME;
        resetVariables(true);
        start();
    }

    private void resetVariables(boolean firstLevel) {
        setLevel(LevelFactory.createLevel(this.grid, 0, 0, 0, 0, 0, firstLevel));
        score = 0;
        this.gameOverScreen.remove();
    }

    public Grid getGrid() {
        return this.grid;
    }

    public Collideable getPlayer() {
        return this.player;
    }

    public Level getLevel() {
        return this.level;
    }

    public void setLevel(Level level) {
        Player p = (Player) player;
        p.increaseSpeed();
        p.reset();
        this.level = level;
        level.draw();level.scoreBoard();
        level.highestScoreBoard();
        CollideableManager.clearCollideableList();
        p.show();
        CollideableManager.addCollideable(p);
    }

    @Override
    public void keyPressed(KeyboardEvent keyboardEvent) {
        int key = keyboardEvent.getKey();

        if (key == KeyboardEvent.KEY_ENTER) {
            if (gameState == GameState.GAMEOVER) {
                gameState = GameState.INGAME;
            }

            if(gameState == GameState.STARTMENU) {
                gameState = GameState.INGAME;
            }
        }

        if(key == KeyboardEvent.KEY_ESC) {
            if(gameState == GameState.GAMEOVER) {
                gameState = GameState.STARTMENU;
            }
        }
    }

    @Override
    public void keyReleased(KeyboardEvent keyboardEvent) {

    }
}
