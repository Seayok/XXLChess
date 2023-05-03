package XXLChess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
  private static double movementSpeed;
  private static int pawnDirection;
  private static int movementTime;

  private double overrideSpeed;
  private float destX;
  private float destY;
  private boolean updatedMoveSet;
  private Square curSquare;
  private boolean isMoved;
  private double direction;
  private String code;
  private PImage sprite;
  private PImage queenImage;
  private ArrayList<Square> validMove = new ArrayList<Square>();
  private ArrayList<Square> validCapture = new ArrayList<Square>();
  private ArrayList<Square> preLegalMove = new ArrayList<Square>();
  private boolean isWhite;

  public Piece(int x, int y, String code, Square curSquare) {
    super(x, y);
    this.curSquare = curSquare;
    destX = x;
    destY = y;
    direction = 0;
    this.code = code;
    if (code.charAt(0) == 'w') {
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

    String queencode = code.length() == 1 ? "Q" : "wq";
    this.queenImage = app.loadImage("src/main/resources/XXLChess/" + queencode + ".png");
    this.queenImage.resize(GRIDSIZE, GRIDSIZE);
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

  public void displayMoveSet() {
    for (Square s : validMove) {
      s.setOnPieceWay(true);
    }
    for (Square s : validCapture) {
      s.setOnCapture(true);
    }
  }

  public void tick() {
    double movementSpeed = Piece.movementSpeed;
    if (overrideSpeed > 0) {
      movementSpeed = overrideSpeed;
    }
    float distX = this.x - destX;
    float distY = this.y - destY;
    double distance = Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));
    if (distance > movementSpeed) {
      this.x += Math.cos(direction) * movementSpeed;
      this.y += Math.sin(direction) * movementSpeed;
    } else {
      this.x = this.destX;
      this.y = this.destY;
      overrideSpeed = 0;
    }
  }

  public boolean isWhitePiece() {
    return isWhite;
  }

  public void straightMove(Board curBoard, int dirX, int dirY, int range) {
    Square[][] squares = curBoard.getSquareMat();
    ConcurrentHashMap<Square, Piece> boardMap = curBoard.getBoardMap();
    for (int i = 1; i <= range; i++) {
      int changeX = i * dirX;
      int changeY = i * dirY;
      int resX = (int) destX / GRIDSIZE + changeX;
      int resY = (int) destY / GRIDSIZE + changeY;
      if (resX >= 0 && resX < GRIDNUM && (changeX != 0 || changeY != 0) && resY >= 0
          && resY < GRIDNUM) {
        Square s = squares[resX][resY];
        if (!boardMap.containsKey(s)) {
          if (!(this.code.toLowerCase().contains("p") && dirX != 0)) {
            validMove.add(s);
          }
        } else {
          if (boardMap.get(s).isWhitePiece() != this.isWhite) {
            if (!(this.code.toLowerCase().contains("p") && dirX == 0)) {
              validCapture.add(s);
            }
          }
          break;
        }
      }
    }
  }

  public void updateValidMove(Board curBoard) {
    if (updatedMoveSet) {
      return;
    }
    preLegalMove.removeAll(preLegalMove);
    validCapture.removeAll(validCapture);
    validMove.removeAll(validMove);

    char codePiece = code.toLowerCase().charAt(code.length() - 1);

    switch (codePiece) {
      case 'p':
        int dir = 1;
        if (code.equals("wp")) {
          dir = -1;
        }
        dir *= pawnDirection;
        int range = 1;
        int rowDest = (int) destY / GRIDSIZE;
        if ((rowDest == 1 || rowDest == GRIDNUM - 2) && !this.isMoved()) {
          range = 2;
        }
        straightMove(curBoard, 0, dir, range);
        straightMove(curBoard, dir, dir, 1);
        straightMove(curBoard, -dir, dir, 1);
        break;
      case 'r':
        setRookMove(curBoard);
        break;
      case 'b':
        setBishopMove(curBoard);
        break;
      case 'n':
        setHorseMove(curBoard, HORSE_RANGE);
        break;
      case 'k':
        setKingMove(curBoard);
        if (!this.isMoved && curBoard.isWhiteTurn() == isWhite) {
          setCastleMove(curBoard);
        }
        break;
      case 'g':
        setKingMove(curBoard);
        setHorseMove(curBoard, HORSE_RANGE);
        break;
      case 'e':
        setHorseMove(curBoard, HORSE_RANGE);
        setRookMove(curBoard);
        break;
      case 'h':
        setHorseMove(curBoard, HORSE_RANGE);
        setBishopMove(curBoard);
        break;
      case 'c':
        setHorseMove(curBoard, CAMEL_RANGE);
        break;
      case 'q':
        setBishopMove(curBoard);
        setRookMove(curBoard);
        break;
      case 'a':
        setBishopMove(curBoard);
        setRookMove(curBoard);
        setHorseMove(curBoard, HORSE_RANGE);
        break;
      default:
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
    Square[] rook = new Square[2];
    rook[0] = squares[rightRookX][(int) destY / GRIDSIZE];
    rook[1] = squares[leftRookX][(int) destY / GRIDSIZE];
    for (int i = 0; i < 2; i++) {
      Square rookCur = rook[i];
      if (boardMap.containsKey(rookCur) && !boardMap.get(rookCur).isMoved()) {
        boolean pieceBetween = false;
        int lowerBound = (i == 0) ? kingX + 1 : leftRookX + 1;
        int upperBound = (i == 0) ? rightRookX : kingX;
        int offset = (i == 0) ? 1 : -1;
        for (int j = lowerBound; j < upperBound; j++) {
          Square s = squares[j][(int) destY / GRIDSIZE];
          Piece p = boardMap.get(s);
          if (p != null || curBoard.squareUnderAttack(isWhite, s) != null) {
            pieceBetween = true;
            break;
          }
        }
        if (!pieceBetween && curBoard.squareUnderAttack(isWhite, curSquare) == null) {
          Square square = squares[(int) destX / GRIDSIZE + 2 * offset][(int) destY / GRIDSIZE];
          validMove.add(square);
        }
      }
    }
  }

  public void removeIllegalMove(Board curBoard) {
    if (updatedMoveSet) {
      return;
    }
    List<Square> illegalMoves = new ArrayList<Square>();
    ConcurrentHashMap<Square, Piece> boardMap = curBoard.getBoardMap();
    Square[][] squareMat = curBoard.getSquareMat();
    for (Square s : preLegalMove) {
      final Piece p = boardMap.get(s);
      Square oldSquare = squareMat[(int) destX / GRIDSIZE][(int) destY / GRIDSIZE];
      boardMap.compute(s, (k, v) -> this);
      this.curSquare = s;
      boardMap.remove(oldSquare);
      if (curBoard.squareUnderAttack(this.isWhite, curBoard.getKing().getSquare()) != null) {
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

  public List<Square> getPreLegalMove() {
    return preLegalMove;
  }

  public void setHorseMove(Board curBoard, int range) {
    Square[][] squares = curBoard.getSquareMat();
    ConcurrentHashMap<Square, Piece> boardMap = curBoard.getBoardMap();
    List<Integer> changeX = Arrays.asList(1, range);
    List<Integer> changeY = Arrays.asList(range, 1);
    for (int i = 0; i < DIRECTION_NUMBER; i++) {
      for (int j = 0; j < changeX.size(); j++) {
        int resX = (int) destX / GRIDSIZE + X_DIAGONAL_DIRECTION[i] * changeX.get(j);
        int resY = (int) destY / GRIDSIZE + Y_DIAGONAL_DIRECTION[i] * changeY.get(j);
        if (resX >= 0 && resX < GRIDNUM && resY >= 0 && resY < GRIDNUM) {
          Square s = squares[resX][resY];
          if (!boardMap.containsKey(s)) {
            validMove.add(s);
          } else if (boardMap.get(s).isWhitePiece() != this.isWhite) {
            validCapture.add(s);
          }
        }
      }
    }
  }

  public void setRookMove(Board curBoard) {
    for (int i = 0; i < DIRECTION_NUMBER; i++) {
      int dirX = X_STRAIGHT_DIRECTION[i];
      int dirY = Y_STRAIGHT_DIRECTION[i];
      straightMove(curBoard, dirX, dirY, GRIDNUM);
    }
  }

  public void setBishopMove(Board curBoard) {
    for (int i = 0; i < DIRECTION_NUMBER; i++) {
      int dirX = X_DIAGONAL_DIRECTION[i];
      int dirY = Y_DIAGONAL_DIRECTION[i];
      straightMove(curBoard, dirX, dirY, GRIDNUM);
    }
  }

  public void setKingMove(Board curBoard) {
    for (int i = 0; i < DIRECTION_NUMBER; i++) {
      int dirX = X_DIAGONAL_DIRECTION[i];
      int dirY = Y_DIAGONAL_DIRECTION[i];
      straightMove(curBoard, dirX, dirY, 1);
      dirX = X_STRAIGHT_DIRECTION[i];
      dirY = Y_STRAIGHT_DIRECTION[i];
      straightMove(curBoard, dirX, dirY, 1);
    }
  }

  public void setDestination(Square target) {
    this.curSquare = target;
    this.destX = target.getX();
    this.destY = target.getY();
    this.direction = Math.atan2(destY - this.y, destX - this.x);
    double distance = Math.sqrt(Math.pow(destX - this.x, 2) + Math.pow(destY - this.y, 2));
    double time = distance / movementSpeed;
    if (time >= movementTime * FPS) {
      overrideSpeed = distance / ((movementTime * FPS) - 1);
    }
  }

  public float getDesX() {
    return destX;
  }

  public float getDesY() {
    return destY;
  }

  public boolean checkPreLegalMove(Square s) {
    return preLegalMove.contains(s);
  }

  public void newMoveSet() {
    this.updatedMoveSet = false;
  }

  public void promotion() {
    if (this.code.equals("P") && (int) this.destY / GRIDSIZE == 6 + Math.max(pawnDirection, 0)) {
      this.code = "Q";
      this.sprite = this.queenImage;
    }
    if (this.code.equals("wp") && (int) this.destY / GRIDSIZE == 7 - Math.max(pawnDirection, 0)) {
      this.code = "wq";
      this.sprite = this.queenImage;
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
