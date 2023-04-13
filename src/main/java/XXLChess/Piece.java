package XXLChess;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import processing.core.PApplet;
import processing.core.PImage;


public class Piece extends MovingObject {
  public static final int GRIDNUM = Board.GRIDNUM;
  public static final int GRIDSIZE = Board.GRIDSIZE;
  public static final double BUFFER_SPEED = 0.5;
  public static final int FPS = 60;
  public static final int DIRECTION_NUMBER = 4;
  public static final List<Integer> Y_STRAIGHT_DIRECTION = Arrays.asList(-1, 0, 1, 0); 
  public static final List<Integer> X_STRAIGHT_DIRECTION = Arrays.asList(0, 1, 0, -1);
  public static final List<Integer> Y_DIAGONAL_DIRECTION = Arrays.asList(-1, -1, 1, 1); 
  public static final List<Integer> X_DIAGONAL_DIRECTION = Arrays.asList(1, -1, -1, 1);
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
    Piece.movementSpeed = movementSpeed + BUFFER_SPEED;
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
      this.x += (int)(Math.cos(direction) * movementSpeed);
      this.y += (int)(Math.sin(direction) * movementSpeed);
    } else {
      this.x = this.xDest;
      this.y = this.yDest;
      overrideSpeed = 0;
    }
  }

  public boolean getWhitePieces() {
    return isWhite;
  }
  
  public void straightMove(Board curBoard, int xdir, int ydir, int range) {
    Square[][] squares = curBoard.getSquareMat();
    ConcurrentHashMap<Square, Piece> boardMap = curBoard.getBoardMap();
    for(int i = 1; i <= range; i++) {
      int xChange = i * xdir;
      int yChange = i * ydir;
      if(yDest/GRIDSIZE + yChange >= 0 && yDest/GRIDSIZE + yChange < GRIDNUM && (xChange != 0 || yChange != 0) && xDest/GRIDSIZE + xChange >= 0 && xDest/GRIDSIZE + xChange < GRIDNUM) {
        Square s = squares[xDest/GRIDSIZE + xChange][yDest/GRIDSIZE + yChange];
        if(!boardMap.containsKey(s)) {
          if(!(this.code.toLowerCase().contains("p") && xdir != 0)){
            validMove.add(s);
          }
        } else {
          if(boardMap.get(s).getWhitePieces() != this.isWhite) {
            if(!(this.code.toLowerCase().contains("p") && xdir == 0)){
              validCapture.add(s);
            }
          }  
          break;
        }
      }
    }
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
      int range = 1; 
      if((yDest/GRIDSIZE == 1 && !isWhite )||(yDest/GRIDSIZE == GRIDNUM - 2 && isWhite)) 
        range = 2;
      straightMove(curBoard, 0, dir, range);
      straightMove(curBoard, dir, dir, 1);
      straightMove(curBoard, -dir, dir, 1);
    } 
    
    if(code.equals("R") || code.equals("wr")) {
      setRookMove(curBoard);
    }


    if(code.equals("B") || code.equals("wb")) {
      setBishopMove(curBoard);
    }

    if(code.equals("N") || code.equals("wn")) {
      setHorseMove(curBoard);
    }
    
    if(code.equals("K") || code.equals("wk")) {
      setKingMove(curBoard);
    }
  }

  public void setHorseMove(Board curBoard) {
  }
  
  public void setRookMove(Board curBoard) {
    for(int i = 0; i < DIRECTION_NUMBER; i++) {
      int xDir = X_STRAIGHT_DIRECTION.get(i);
      int yDir = Y_STRAIGHT_DIRECTION.get(i);
      straightMove(curBoard, xDir, yDir, GRIDNUM); 
    }
  }
  
  public void setBishopMove(Board curBoard) {
    for(int i = 0; i < DIRECTION_NUMBER; i++) {
      int xDir = X_DIAGONAL_DIRECTION.get(i);
      int yDir = Y_DIAGONAL_DIRECTION.get(i);
      straightMove(curBoard, xDir, yDir, GRIDNUM); 
    }
  }

  public void setKingMove(Board curBoard) {
    for(int i = 0; i < DIRECTION_NUMBER; i++) {
      int xDir = X_DIAGONAL_DIRECTION.get(i);
      int yDir = Y_DIAGONAL_DIRECTION.get(i);
      straightMove(curBoard, xDir, yDir, 1); 
      xDir = X_STRAIGHT_DIRECTION.get(i);
      yDir = Y_STRAIGHT_DIRECTION.get(i);
      straightMove(curBoard, xDir, yDir, 1);
    }
  }

  public void setDestination(int x, int y){
    this.xDest = x;
    this.yDest = y;
    System.out.println(xDest + " " + yDest);
    this.direction = Math.atan2(yDest - this.y, xDest - this.x);
    System.out.println(Math.cos(direction) + " " + Math.sin(direction));
    double distance = Math.sqrt(Math.pow(xDest - this.x, 2) + Math.pow(yDest - this.y, 2));
    double time = distance/movementSpeed;
    System.out.println(time);
    if(time >= movementTime * FPS) {
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
