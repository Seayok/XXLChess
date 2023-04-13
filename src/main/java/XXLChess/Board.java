package XXLChess;

import processing.core.PApplet;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import processing.data.JSONObject;

/**
 * Represents a board.
 */
public class Board extends GameObject {
  public static final int GRIDSIZE = 48;
  public static final int GRIDNUM = 14;
  public static double MAX_MOVEMENT_TIME;
  private Pieces selPieces;
  private Square selSquare;
  private ArrayList<Pieces> pieces;
  private Square[][] squareMat;

  /**
   * Creates a new board with coordinates (0, 0)
   */
  public Board(String[][] levelArr, PApplet app) {
    super(0, 0);
    squareMat = new Square[GRIDNUM][GRIDNUM];
    pieces = new ArrayList<>();
    for (int i = 0; i < GRIDNUM; i++) {
      for (int j = 0; j < GRIDNUM; j++) {
        squareMat[i][j] = new Square(i, j, levelArr[j][i], app);
        if(squareMat[i][j].getPiece() !=null) {
          pieces.add(squareMat[i][j].getPiece());
        }
      }
    }
    updateValidMove();
  }

  public static void updateMoveStatus(JSONObject conf) {
    Pieces.setMoveStat(conf.getDouble("piece_movement_speed"), conf.getInt("max_movement_time"));
    MAX_MOVEMENT_TIME = conf.getDouble("max_movement_time");
  }

  public boolean checkCheck() {
    return false;
  }

  public void startClick(int x, int y) {
    //overflow check
    if(x > GRIDSIZE * GRIDNUM || y > GRIDSIZE * GRIDNUM || squareMat[x/GRIDSIZE][y/GRIDSIZE].getPiece() == null) 
      return;
    selSquare = squareMat[x/GRIDSIZE][y/GRIDSIZE];
    selPieces = selSquare.getPiece();
    selSquare.onSelected();
    selPieces.displayMoveSet(); 
  }

  public void updateValidMove() {
    for(Pieces piece : pieces) {
      piece.updateValidMove(this);
    }
  }

  public void selecClick(int x, int y) {
    Square target = squareMat[x/GRIDSIZE][y/GRIDSIZE];
    if(!target.isOnPieceWay() && !target.isOnCaptured()) {
      reset();
      if(target.getPiece() != null) {
        startClick(x, y);
      }
    } else{
      Pieces removePieces = target.getPiece();
      selSquare.movePiece(target);
      if(removePieces != null) {
        pieces.remove(removePieces);
      }
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
    selPieces = null;
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
    for(Pieces piece : pieces) {
      piece.draw(app);
    }
  }
}
