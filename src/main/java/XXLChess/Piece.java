package XXLChess;

import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PImage;


public class Piece extends MovingObject {
  public static final int GRIDNUM = Board.GRIDNUM;
  public static final int GRIDSIZE = Board.GRIDSIZE;
  public static final int FPS = 60;
  private static double  movementSpeed;
  private static int movementTime;

  private double overrideSpeed;
  private int xDest;
  private int yDest;
  private double direction;
  private String code;
  private PImage sprite;
  private ArrayList<Square> validMove = new ArrayList<Square>();
  private ArrayList<Square> validCapture = new ArrayList<Square>();
  private boolean isWhite;

  public Piece(int x, int y, String code) {
    super(x, y);
    xDest = x;
    yDest = y;
    direction = 0;
    this.code = code;
    if(code.charAt(0) == 'w') {
      this.isWhite = true;
    } else {
      this.isWhite = false;
    }
    this.overrideSpeed = 0;
  }

  public static void setMoveStat(double movementSpeed, int movementTime) {
    Piece.movementSpeed = movementSpeed;
    Piece.movementTime = movementTime;
  }

  /**
   * Sets the object's sprite.
   *
   * @param sprite The new sprite to use.
   */
  public void setSprite(PApplet app) {
    String dir = "src/main/resources/XXLChess/" + code + ".png";
    this.sprite = app.loadImage(dir);
    this.sprite.resize(GRIDSIZE, GRIDSIZE);
  }

  public ArrayList<Square> getValidMove() {
    return validMove;
  }
  
  public ArrayList<Square> getValidCapture() {
    return validCapture;
  }

  public void displayMoveSet(){
    for(Square s : validMove) {
      s.setOnPieceWay(true);
    }
    for(Square s : validCapture) {
      s.setOnCapture(true);
    }
  }

  public void tick() {
    double movementSpeed = Piece.movementSpeed;
    if(overrideSpeed > 0){
      movementSpeed = overrideSpeed;
    }
    int xDist = this.x - xDest;
    int yDist = this.y - yDest;
    double distance = Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
    if(distance > movementSpeed){
      this.x += Math.cos(direction) * movementSpeed;
      this.y += Math.sin(direction) * movementSpeed;
    } else {
      this.x = this.xDest;
      this.y = this.yDest;
      overrideSpeed = 0;
    }
  }

  public boolean getWhitePieces() {
    return isWhite;
  }
  
  public void horizontalMove() {
  }

  public void verticalMove(Board curBoard, int dir, int range) {
    Square[][] squares = curBoard.getSquareMat();
    for(int i = 1; i <= range; i++) {
      int realChange = i * dir;
      if(y/GRIDSIZE + realChange >= 0 && y/GRIDSIZE + realChange < GRIDNUM && realChange != 0) {
        Square s = squares[x/GRIDSIZE][y/GRIDSIZE + realChange];
        if(s.getPiece() == null) {
          validMove.add(s);
        } else {
          if(s.getPiece().getWhitePieces() != this.isWhite) {
            validCapture.add(s);
            break;
          }  
        }
      }
    }
  }

  public void DiagonalMove() {

  }

  public void horseMove() {

  }
  
  public void updateValidMove(Board curBoard) {
    validCapture.removeAll(validCapture);
    validMove.removeAll(validMove);
    if(code.equals("P") || code.equals("wp")) {
      int dir = 1;
      if(code.equals("wp")){ 
        dir = -1;
      }
      int range = 19; 
      if((y/GRIDSIZE == 1 && !isWhite )||(y/GRIDSIZE == GRIDNUM - 2 && isWhite)) 
        range = 19;
      verticalMove(curBoard, dir, range);
    } 
  }
  
  public void onSelected() {
    System.out.println(code); 
    displayMoveSet();
  }

  public void setDestination(int x, int y){
    this.xDest = x;
    this.yDest = y;
    this.direction = Math.atan2(yDest - this.y, xDest - this.x);
    double distance = Math.sqrt(Math.pow(xDest - this.x, 2) + Math.pow(yDest - this.y, 2));
    double time = distance/movementSpeed;
    System.out.println(time);
    if(time > movementTime * FPS) {
      overrideSpeed = distance/((movementTime*FPS) - 1);
      System.out.println("here is " + overrideSpeed);
    }
  }

  /**
   * Draws the object to the screen.
   *
   * @param app The window to draw onto.
   */
  public void draw(PApplet app) {
    // The image() method is used to draw PImages onto the screen.
    // The first argument is the image, the second and third arguments are
    // coordinates
    tick();
    app.image(this.sprite, this.x, this.y);
  }
}
