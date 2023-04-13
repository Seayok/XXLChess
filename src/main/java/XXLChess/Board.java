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
          newPiece.setSprite(app);
          boardMap.put(squareMat[i][j], newPiece);
        }
      }
    }
    updateValidMove();
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
    selSquare.onSelected();
    selPiece.displayMoveSet(); 
  }

  public void updateValidMove() {
    for(Piece piece : boardMap.values()) {
      piece.updateValidMove(this);
    }
  }

  public ConcurrentHashMap<Square, Piece> getBoardMap() {
    return boardMap;
  }

  public void selecClick(int x, int y) {
    Square target = squareMat[x/GRIDSIZE][y/GRIDSIZE];
    if(!target.isOnPieceWay() && !target.isOnCaptured()) {
      reset();
      if(boardMap.containsKey(target)){
        startClick(x, y);
      }
    } else{
      boardMap.compute(target, (k, v) -> selPiece);
      boardMap.remove(selSquare);
      selPiece.setDestination(target.getX(), target.getY());
      updateValidMove();
      reset();
    }
  }

  public void reset() {
    for(int i = 0; i < GRIDNUM; i++) {
      for(int j = 0; j < GRIDNUM; j++) {
        Square square = squareMat[i][j];
        square.setOnPieceWay(false);
        square.setOnCapture(false);
      }
    }
    selSquare.deselect();
    selSquare = null;
    selPiece = null;
  }
  
  public void onClick(int x, int y) {
    if(selSquare == null) {
      startClick(x, y);
    } else {
      selecClick(x, y);
    }
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
