package XXLChess;

import java.util.concurrent.ConcurrentHashMap;
import processing.core.PApplet;
import processing.data.JSONObject;

/**
 * Represents a board.
 */
public class Board extends GameObject {
  public static final int GRIDSIZE = 48;
  public static final int GRIDNUM = 14;
  public static double MAX_MOVEMENT_TIME;
  private boolean whiteTurn = true;
  private boolean switchTurn = false;
  private Piece whiteKing;
  private Piece blackKing;
  private Piece selPiece;
  private Square selSquare;
  private ConcurrentHashMap<Square, Piece> boardMap;
  private Square[][] squareMat;

  /**
   * Creates a new board with coordinates (0, 0)
   */
  public Board(String[][] levelArr, PApplet app) {
    super(0, 0);
    squareMat = new Square[GRIDNUM][GRIDNUM];
    boardMap = new ConcurrentHashMap<>();
    for (int i = 0; i < GRIDNUM; i++) {
      for (int j = 0; j < GRIDNUM; j++) {
        squareMat[i][j] = new Square(i, j, app);
        if(levelArr[j][i] != "") {
          Piece newPiece = new Piece(i * GRIDSIZE, j * GRIDSIZE, levelArr[j][i]);
          if(levelArr[j][i].equals("K")){
            blackKing = newPiece;
          }
          if(levelArr[j][i].equals("wk")){
            whiteKing = newPiece;
          }
          newPiece.setSprite(app);
          boardMap.put(squareMat[i][j], newPiece);
        }
      }
    }
  }

  public static void updateMoveStatus(JSONObject conf) {
    Piece.setMoveStat(conf.getDouble("piece_movement_speed"), conf.getInt("max_movement_time"));
    MAX_MOVEMENT_TIME = conf.getDouble("max_movement_time");
  }
  
  public void startClick(int x, int y) {
    //overflow check
    if(x > GRIDSIZE * GRIDNUM || y > GRIDSIZE * GRIDNUM || !boardMap.containsKey(squareMat[x/GRIDSIZE][y/GRIDSIZE]))
      return;
    selSquare = squareMat[x/GRIDSIZE][y/GRIDSIZE];
    selPiece = boardMap.get(selSquare);
    if(selPiece.getWhitePieces() != whiteTurn){
      selPiece = null;
      return;
    }
    selPiece.updateValidMove(this);
    selPiece.removeIllegalMove(this);
    selSquare.onSelected();
    selPiece.displayMoveSet(); 
  }

  public ConcurrentHashMap<Square, Piece> getBoardMap() {
    return boardMap;
  }

  public void selectClick(int x, int y, PApplet app) {
    Square target = squareMat[x/GRIDSIZE][y/GRIDSIZE];
    if(!target.isOnPieceWay() && !target.isOnCaptured()) {
      Square kingSquare;
      if(whiteTurn){
        kingSquare = getSquareFromPiece(whiteKing);
      } else {
        kingSquare = getSquareFromPiece(blackKing);
      }
      if(kingSquare.isKingChecked() && selPiece.checkPreLegalMove(target)){
        warning(); 
      }
      reset(null);
      if(boardMap.containsKey(target)){
        startClick(x, y);
      }
    } else{
      boardMap.compute(target, (k, v) -> selPiece);
      boardMap.remove(selSquare);
      selPiece.setDestination(target.getX(), target.getY());
      selPiece.promotion(app);
      selPiece.moved();
      reset(target);
      whiteTurn = !whiteTurn;
      switchTurn = true;
      checkCheck();
    }
  }


  public void warning(){
    System.out.println("WARNING");
  }

  public void checkCheck() {
    Square kingSquare = getSquareFromPiece(whiteKing);
    kingSquare.setKingChecked(!Piece.checkKing(this, true));
    kingSquare = getSquareFromPiece(blackKing);
    kingSquare.setKingChecked(!Piece.checkKing(this, false));
  }

  public Square getSquareFromPiece(Piece piece) {
    return squareMat[(int)piece.getDesX()/GRIDSIZE][(int)piece.getDesY()/GRIDSIZE];
  }


  public void reset(Square target) {
    for(int i = 0; i < GRIDNUM; i++) {
      for(int j = 0; j < GRIDNUM; j++) {
        Square square = squareMat[i][j];
        square.setOnPieceWay(false);
        square.setOnCapture(false);
        if(target != null)
          square.setPrevMove(false);
      }
    }
    if(target != null){
      selSquare.setPrevMove(true);
      target.setPrevMove(true);
    }
    selSquare.deselect();
    selPiece = null;
    selSquare = null;
  }
  
  public boolean isWhiteTurn() {
    return whiteTurn;
  }
  
  public void onClick(int x, int y, PApplet app) {
    if(selSquare == null) {
      startClick(x, y);
    } else {
      selectClick(x, y, app);
    }
  }

  public boolean switchedTurn() {
    return this.switchTurn;
  }

  public void unSwitchTurn() {
    this.switchTurn = false;
  }


  public Square[][] getSquareMat() {
    return squareMat;
  }


  public void draw(PApplet app) {
    for (int i = 0; i < GRIDNUM; i++) {
      for (int j = 0; j < GRIDNUM; j++) {
        squareMat[i][j].draw(app);
      }
    }
    for(Piece piece : boardMap.values()) {
      piece.draw(app);
    }
  }
}
