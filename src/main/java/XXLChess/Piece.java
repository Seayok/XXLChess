package XXLChess;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import processing.core.PApplet;
import processing.core.PImage;


public class Piece extends GameObject {
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
  private static int pawnDirection;
  private static int movementTime;

  private double overrideSpeed;
  private float xDest;
  private float yDest;
  private boolean updatedMoveSet;
  private Square curSquare;
  private boolean isMoved;
  private double direction;
  private String code;
  private PImage sprite;
  private ArrayList<Square> validMove = new ArrayList<Square>();
  private ArrayList<Square> validCapture = new ArrayList<Square>();
  private ArrayList<Square> preLegalMove = new ArrayList<Square>();
  private boolean isWhite;

  public Piece(int x, int y, String code, Square curSquare) {
    super(x, y);
    this.curSquare = curSquare;
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
    isMoved = false;
  }

  public static void setMoveStat(double movementSpeed, int movementTime, int pawnDirection) {
    Piece.pawnDirection = pawnDirection;
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

  public Square getSquare() {
    return curSquare;
  }

  public List<Square> getValidMove() {
    return validMove;
  }
  
  public List<Square> getValidCapture() {
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

  public boolean isWhitePiece() {
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
          if(boardMap.get(s).isWhitePiece() != this.isWhite) {
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
    if(updatedMoveSet){
      return;
    }
    preLegalMove.removeAll(preLegalMove);
    validCapture.removeAll(validCapture);
    validMove.removeAll(validMove);
    if(code.equals("P") || code.equals("wp")) {
      int dir = 1;
      if(code.equals("wp")){ 
        dir = -1;
      }
      dir *= pawnDirection;
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
      if(!this.isMoved && curBoard.isWhiteTurn() == isWhite)
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
    int kingX = 7;
    int rightRookX = 13;
    int leftRookX = 0;
    Square[][] squares = curBoard.getSquareMat();
    ConcurrentHashMap<Square, Piece> boardMap = curBoard.getBoardMap();
    Square rook[] = new Square[2];
    rook[0] = squares[rightRookX][(int)yDest/GRIDSIZE];
    rook[1] = squares[leftRookX][(int)yDest/GRIDSIZE];
    for(int i = 0; i < 2; i++) {
      Square rookCur = rook[i];
      if(boardMap.containsKey(rookCur) && !boardMap.get(rookCur).isMoved()) {
        boolean pieceBetween = false;
        int lowerBound = (i == 0)?kingX + 1:leftRookX + 1;
        int upperBound = (i == 0)?rightRookX:kingX;
        int offset = (i == 0)?1:-1;
        for(int j = lowerBound; j < upperBound; j++) {
          Square s = squares[j][(int)yDest/GRIDSIZE];
          Piece p = boardMap.get(s);
          if(p != null || curBoard.squareUnderAttack(isWhite, s, false) != null) {
            pieceBetween = true;
            break;
          }
        }
        if(!pieceBetween && curBoard.squareUnderAttack(isWhite, curSquare, false) == null) {
          Square square = squares[(int)xDest/GRIDSIZE + 2*offset][(int)yDest/GRIDSIZE];
          validMove.add(square);
        }
      }
    }
  }

  public void removeIllegalMove(Board curBoard) {
    if(updatedMoveSet){
      return;
    }
    List<Square> illegalMoves = new ArrayList<Square>();
    ConcurrentHashMap<Square, Piece> boardMap = curBoard.getBoardMap();
    Square[][] squareMat = curBoard.getSquareMat();
    for(Square s : preLegalMove) {
      Piece p = boardMap.get(s);
      Square oldSquare = squareMat[(int)xDest/GRIDSIZE][(int)yDest/GRIDSIZE];
      boardMap.compute(s, (k, v) -> this);
      this.curSquare = s;
      boardMap.remove(oldSquare);
      if(curBoard.squareUnderAttack(this.isWhite, curBoard.getKing().getSquare(), true) != null) {
        illegalMoves.add(s);
      }
      this.curSquare = oldSquare;
      boardMap.put(oldSquare, this);
      boardMap.compute(s, (k, v) -> p);
    }
    validMove.removeAll(illegalMoves);
    validCapture.removeAll(illegalMoves);
    updatedMoveSet = true;
  }


  public List<Square> getPreLegalMove(){
    return preLegalMove;
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
          } else if(boardMap.get(s).isWhitePiece() != this.isWhite){
            validCapture.add(s);
          }
        }
      }
    }
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

  public void setDestination(Square target){
    this.curSquare = target;
    this.xDest = target.getX();
    this.yDest = target.getY();
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

  public boolean checkPreLegalMove(Square s) {
    return preLegalMove.contains(s);
  }
  
  public void newMoveSet(){
    this.updatedMoveSet = false;
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
    tick();
    app.image(this.sprite, this.x, this.y);
  }
}
