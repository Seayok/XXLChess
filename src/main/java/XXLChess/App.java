package XXLChess;

// import org.reflections.Reflections;
// import org.reflections.scanners.Scanners;
// import java.awt.Font;
import java.io.*;
//import java.util.*;
// import java.util.concurrent.ConcurrentHashMap;
// import java.util.concurrent.TimeUnit;
import processing.core.PApplet;
// import processing.core.PFont;
// import processing.core.PImage;
// import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.MouseEvent;

public class App extends PApplet {

  public static final int SPRITESIZE = 480;
  public static final int CELLSIZE = 48;
  public static final int SIDEBAR = 120;
  public static final int BOARD_WIDTH = 14;

  public static int WIDTH = CELLSIZE * BOARD_WIDTH + SIDEBAR;
  public static int HEIGHT = BOARD_WIDTH * CELLSIZE;

  public static final int FPS = 60;

  public String configPath;

  public Board board;

  public Clock clockWhite;
  public Clock clockBlack;
  public Helper helper;

  public App() {
    this.clockWhite = new Clock(690, 634);
    this.clockBlack = new Clock(690, 56);
    this.configPath = "config.json";
    this.helper = new Helper();
  }

  /**
   * Initialise the setting of the window size.
   */
  public void settings() { size(WIDTH, HEIGHT); }

  /**
   * Load all resources such as images. Initialise the elements such as the
   * player, enemies and map elements.
   */
  public void setup() {
    frameRate(FPS);

    // Load images during setup

    // PImage spr = loadImage("src/main/resources/XXLChess/"+...);

    // load config
    JSONObject conf = loadJSONObject(new File(this.configPath));
    helper.setConfig(conf);
    this.board = new Board(helper.loadBoard(), this);
    Board.updateMoveStatus(conf);
    helper.initTime(conf, clockWhite, clockBlack);
    clockWhite.start(this);
  }

  /**
   * Receive key pressed signal from the keyboard.
   */
  public void keyPressed() {}

  /**
   * Receive key released signal from the keyboard.
   */
  public void keyReleased() {}

  @Override
  public void mouseClicked(MouseEvent e) {
    board.onClick(e.getX(), e.getY(), this);
    boolean switchedTurn = board.switchedTurn();
    if(switchedTurn && board.isWhiteTurn()){
      clockWhite.start(this);
      clockBlack.stop();
      board.unSwitchTurn();
    }
    if(switchedTurn && !board.isWhiteTurn()){
      clockBlack.start(this);
      clockWhite.stop();
      board.unSwitchTurn();
    }
  }

  @Override
  public void mouseDragged(MouseEvent e) {}

  /**
   * Draw all elements in the game by current frame.
   */
  public void draw() { 
    background(155);
    board.draw(this); 
    clockBlack.draw(this);
    clockWhite.draw(this);
  }

  // Add any additional methods or attributes you want. Please put classes in
  // different files.

  public static void main(String[] args) { PApplet.main("XXLChess.App"); }
}
