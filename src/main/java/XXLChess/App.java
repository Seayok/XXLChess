package XXLChess;

import java.io.File;
import processing.core.PApplet;
import processing.data.JSONObject;
import processing.event.MouseEvent;

/**
 * Main entry point for the application.
 */
public class App extends PApplet {

  public static final int SPRITESIZE = 480;
  public static final int CELLSIZE = 48;
  public static final int SIDEBAR = 120;
  public static final int BOARD_WIDTH = 14;

  public static int WIDTH = CELLSIZE * BOARD_WIDTH + SIDEBAR;
  public static int HEIGHT = BOARD_WIDTH * CELLSIZE;

  public static final int FPS = 60;

  public String configPath;

  public Game game;
  public Board board;
  public Player player1;
  public Player player2;
  public Helper helper;
  public Message textBox;

  /**
   * App constructor.
   */
  public App() {
    this.player1 = new Player();
    this.player2 = new Player();
    this.configPath = "config.json";
    this.helper = new Helper();
    this.textBox = new Message(680, 300, 48);
  }

  /**
   * Initialise the setting of the window size.
   */
  public void settings() {
    size(WIDTH, HEIGHT);
  }

  /**
   * Load all resources such as images. Initialise the elements such as the player, enemies and map
   * elements.
   */
  public void setup() {
    frameRate(FPS);

    // load config
    JSONObject conf = loadJSONObject(new File(this.configPath));
    helper.setConfig(conf);
    helper.initTimeAndSide(player1, player2);
    helper.updateMoveStatus(conf, player1.isWhite());
    Clock clockWhite = player1.isWhite() ? player1.getClock() : player2.getClock();
    clockWhite.start(this);
    board = new Board(helper.loadBoard(), this);
    game = new Game(board, this, player1, player2, textBox);
    board.setSpriteAndDisplay(this);
  }

  /**
   * Receive key pressed signal from the keyboard.
   */
  public void keyPressed() {
    if (key == 'r') {
      textBox.hide();
      this.player1 = new Player();
      this.player2 = new Player();
      setup();
    }
    if (key == 'e') {
      game.resign();
    }
  }

  /**
   * Receive key released signal from the keyboard.
   */
  public void keyReleased() {}

  @Override
  public void mousePressed(MouseEvent e) {
    game.mouseClicked(e.getX(), e.getY());
  }

  @Override
  public void mouseDragged(MouseEvent e) {}

  /**
   * Draw all elements in the game by current frame.
   */
  public void draw() {
    // background(155);
    fill(155);
    stroke(155);
    rect(672, 0, 150, 800);
    game.tick(this);
  }

  // Add any additional methods or attributes you want. Please put classes in
  // different files.
  public void updateGameStatus(int status) {
    game.updateGameStatus(status);
  }

  public static void main(String[] args) {
    PApplet.main("XXLChess.App");
  }
}
