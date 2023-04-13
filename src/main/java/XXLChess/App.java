package XXLChess;

// import org.reflections.Reflections;
// import org.reflections.scanners.Scanners;
// import java.awt.Font;
import java.io.*;
import java.util.*;
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

  public Timer timer;
  public Helper helper;

  public App() {
    this.configPath = "config.json";
    this.timer = new Timer();
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
    board.onClick(e.getX(), e.getY());
  }

  @Override
  public void mouseDragged(MouseEvent e) {}

  /**
   * Draw all elements in the game by current frame.
   */
  public void draw() { 
    background(155);
    board.draw(this); 
  }

  // Add any additional methods or attributes you want. Please put classes in
  // different files.

  public static void main(String[] args) { PApplet.main("XXLChess.App"); }
}
