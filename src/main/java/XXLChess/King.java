package XXLChess;

import java.util.HashMap;

public class King extends Piece {
  public King(int x, int y, String code, Square curSquare) {
    super(x, y, code, curSquare);
    this.value = 10000 * (this.isWhite ? 1 : -1);
  }

  @Override
  public void generateMove(Board curBoard) {
    setKingMove(curBoard);
    if (!this.isMoved && curBoard.isWhiteTurn() == isWhite) {
      setCastleMove(curBoard);
    }
  }

  private void setCastleMove(Board curBoard) {
    int kingX = 7;
    int rightRookX = 13;
    int leftRookX = 0;
    Square[][] squares = curBoard.getSquareMat();
    HashMap<Square, Piece> boardMap = curBoard.getBoardMap();
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
          preLegalMoves.add(new Move(curSquare, square, Move.CASTLE, this, null));

        }
      }
    }
  }
}
