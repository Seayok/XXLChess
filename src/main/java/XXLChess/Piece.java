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
  // public static final double BUFFER_SPEED = 0.5;
  public static final int FPS = 60;
  public static final int DIRECTION_NUMBER = 4;
  public static final int HORSE_RANGE = 2;
  public static final int CAMEL_RANGE = 3;
  public static final int[] Y_STRAIGHT_DIRECTION = {-1, 0, 1, 0}; 
  public static final int[] X_STRAIGHT_DIRECTION = {0, 1, 0, -1};
  public static final int[] Y_DIAGONAL_DIRECTION = {-1, -1, 1, 1}; 
  public static final int[] X_DIAGONAL_DIRECTION = {1, -1, -1, 1};
  private static double  movementSpeed;
  private static int movementTime;

  private double overrideSpeed;
  private float xDest;
  private float yDest;
  private boolean isMoved;
  private double direction;
  private String code;
  private PImage sprite;
  private ArrayList<Square> validMove = new ArrayList<Square>();
  private ArrayList<Square> validCapture = new ArrayList<Square>();
  private ArrayList<Square> preLegalMove = new ArrayList<Square>();
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

  public String getCode() {
    return code;
  }

  public void moved() {
    this.isMoved = true;
  }

  public boolean isMoved() {
    return isMoved;
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
    float xDist = this.x - xDest;
    float yDist = this.y - yDest;
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
  
  public void straightMove(Board curBoard, int xdir, int ydir, int range) {
    Square[][] squares = curBoard.getSquareMat();
    ConcurrentHashMap<Square, Piece> boardMap = curBoard.getBoardMap();
    for(int i = 1; i <= range; i++) {
      int xChange = i * xdir;
      int yChange = i * ydir;
      int xRes = (int)xDest / GRIDSIZE + xChange;
      int yRes = (int)yDest / GRIDSIZE + yChange;
      if(xRes >= 0 && xRes < GRIDNUM && (xChange != 0 || yChange != 0) && yRes >= 0 && yRes < GRIDNUM) {
        Square s = squares[xRes][yRes];
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
  
  public void updateValidMove(Board curBoard) {
    preLegalMove.removeAll(preLegalMove);
    validCapture.removeAll(validCapture);
    validMove.removeAll(validMove);
    if(code.equals("P") || code.equals("wp")) {
      int dir = 1;
      if(code.equals("wp")){ 
        dir = -1;
      }
      int range = 1; 
      if(((int)yDest/GRIDSIZE == 1 && !isWhite )||((int)yDest/GRIDSIZE == GRIDNUM - 2 && isWhite)) 
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
      setHorseMove(curBoard, HORSE_RANGE);
    }
    
    if(code.equals("K") || code.equals("wk")) {
      setKingMove(curBoard);
      setCastleMove(curBoard);
    }

    if(code.equals("G") || code.equals("wg")) {
      setKingMove(curBoard);
      setHorseMove(curBoard, HORSE_RANGE);
    }

    if(code.equals("E") || code.equals("we")) {
      setHorseMove(curBoard, HORSE_RANGE);
      setRookMove(curBoard);
    }

    if(code.equals("H") || code.equals("wh")) {
      setHorseMove(curBoard, HORSE_RANGE);
      setBishopMove(curBoard);
    }

    if(code.equals("C") || code.equals("wc")) {
      setHorseMove(curBoard, CAMEL_RANGE);
    }

    if(code.equals("Q") || code.equals("wq")) {
      setBishopMove(curBoard);
      setRookMove(curBoard);
    }

    if(code.equals("A") || code.equals("wa")) {
      setBishopMove(curBoard);
      setRookMove(curBoard);
      setHorseMove(curBoard, HORSE_RANGE);
    }

    preLegalMove.addAll(validMove);
    preLegalMove.addAll(validCapture);
  }

  public void setCastleMove(Board curBoard) {
    Square[][] squares = curBoard.getSquareMat();
    ConcurrentHashMap<Square, Piece> boardMap = curBoard.getBoardMap();
    Square rookRight = squares[(int)xDest/GRIDSIZE][13];
    Square rookLeft = squares[(int)xDest/GRIDSIZE][0];
    if(!this.isMoved && boardMap.contains(rookLeft) && !boardMap.get(rookLeft).isMoved()) {
    }
    if(!this.isMoved && !boardMap.get(rookRight).isMoved() && boardMap.contains(rookRight)){

    }
  }

  public void removeIllegalMove(Board curBoard) {
    ArrayList<Square> illegalMoves = new ArrayList<Square>();
    ConcurrentHashMap<Square, Piece> boardMap = curBoard.getBoardMap();
    Square[][] squareMat = curBoard.getSquareMat();
    for(Square s : preLegalMove) {
      Piece p = boardMap.get(s);
      Square oldSquare = squareMat[(int)xDest/GRIDSIZE][(int)yDest/GRIDSIZE];
      boardMap.compute(s, (k, v) -> this);
      boardMap.remove(oldSquare);
      if(!Piece.checkKing(curBoard, this.isWhite)) {
        illegalMoves.add(s);
      }
      boardMap.put(oldSquare, this);
      boardMap.compute(s, (k, v) -> p);
    }
    validMove.removeAll(illegalMoves);
    validCapture.removeAll(illegalMoves);
  }


  /*
   * Check if the king is being checked
   */
  public static boolean checkKing(Board curBoard, boolean isWhite) {
    ConcurrentHashMap<Square, Piece> boardMap = curBoard.getBoardMap();
    for(Piece p : boardMap.values()) {
      if(p.getWhitePieces() != isWhite) {
        p.updateValidMove(curBoard);
        for(Square s : p.getValidCapture()) {
          if(boardMap.get(s).getCode().toLowerCase().contains("k")){
            return false;
          }
        }
      }
    }
    return true;
  }

  public void setHorseMove(Board curBoard, int range) {
    Square[][] squares = curBoard.getSquareMat();
    ConcurrentHashMap<Square, Piece> boardMap = curBoard.getBoardMap();
    List<Integer> xChange = Arrays.asList(1, range);
    List<Integer> yChange = Arrays.asList(range, 1);
    for(int i = 0; i < DIRECTION_NUMBER; i++) {
      for(int j = 0; j < xChange.size(); j++) {
        int xRes = (int)xDest/GRIDSIZE + X_DIAGONAL_DIRECTION[i] * xChange.get(j);
        int yRes = (int)yDest/GRIDSIZE + Y_DIAGONAL_DIRECTION[i] * yChange.get(j);
        if(xRes >= 0 && xRes < GRIDNUM && yRes >= 0 && yRes < GRIDNUM){
          Square s = squares[xRes][yRes];
          if(!boardMap.containsKey(s)) {
            validMove.add(s);
          } else if(boardMap.get(s).getWhitePieces() != this.isWhite){
            validCapture.add(s);
          }
        }
      }
    }
  }

  public boolean checkPreLegalMove(Square s) {
    return preLegalMove.contains(s);
  }
  
  public void setRookMove(Board curBoard) {
    for(int i = 0; i < DIRECTION_NUMBER; i++) {
      int xDir = X_STRAIGHT_DIRECTION[i];
      int yDir = Y_STRAIGHT_DIRECTION[i];
      straightMove(curBoard, xDir, yDir, GRIDNUM); 
    }
  }
  
  public void setBishopMove(Board curBoard) {
    for(int i = 0; i < DIRECTION_NUMBER; i++) {
      int xDir = X_DIAGONAL_DIRECTION[i];
      int yDir = Y_DIAGONAL_DIRECTION[i];
      straightMove(curBoard, xDir, yDir, GRIDNUM); 
    }
  }

  public void setKingMove(Board curBoard) {
    for(int i = 0; i < DIRECTION_NUMBER; i++) {
      int xDir = X_DIAGONAL_DIRECTION[i];
      int yDir = Y_DIAGONAL_DIRECTION[i];
      straightMove(curBoard, xDir, yDir, 1); 
      xDir = X_STRAIGHT_DIRECTION[i];
      yDir = Y_STRAIGHT_DIRECTION[i];
      straightMove(curBoard, xDir, yDir, 1);
    }
  }

  public void setDestination(float x, float y){
    this.xDest = x;
    this.yDest = y;
    this.direction = Math.atan2(yDest - this.y, xDest - this.x);
    double distance = Math.sqrt(Math.pow(xDest - this.x, 2) + Math.pow(yDest - this.y, 2));
    double time = distance/movementSpeed;
    if(time >= movementTime * FPS) {
      overrideSpeed = distance/((movementTime*FPS) - 1);
    }
  }

  public float getDesX() {
    return xDest;
  }

  public float getDesY() {
    return yDest;
  }

  public void promotion(PApplet app){
    if(this.code.equals("P") && (int)this.yDest/GRIDSIZE == 7){
      this.code = "Q";
      setSprite(app);
    } 
    if(this.code.equals("wp") && (int)this.yDest/GRIDSIZE == 6){
      this.code = "wq";
      setSprite(app);
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
