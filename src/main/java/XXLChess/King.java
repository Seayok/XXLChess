package XXLChess;

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
    Square[] rook = new Square[2];
    rook[0] = squares[rightRookX][(int) destY / GRIDSIZE];
    rook[1] = squares[leftRookX][(int) destY / GRIDSIZE];
    for (int i = 0; i < 2; i++) {
      Square rookCur = rook[i];
      if (rookCur.getPiece() == null || !rookCur.getPiece().code.contains("r")) {
        continue;
      }
      if (rookCur.getPiece() != null && !rookCur.getPiece().isMoved()) {
        boolean pieceBetween = false;
        int lowerBound = (i == 0) ? kingX + 1 : leftRookX + 1;
        int upperBound = (i == 0) ? rightRookX : kingX;
        int offset = (i == 0) ? 1 : -1;
        for (int j = lowerBound; j < upperBound; j++) {
          Square s = squares[j][(int) destY / GRIDSIZE];
          Piece p = s.getPiece();
          if (p != null || curBoard.squareUnderAttack(isWhite, s) != null) {
            pieceBetween = true;
            break;
          }
        }
        if (!pieceBetween && curBoard.squareUnderAttack(isWhite, curSquare) == null) {
          Square square = squares[(int) destX / GRIDSIZE + 2 * offset][(int) destY / GRIDSIZE];
          Move castle = new Move(curSquare, square, Move.CASTLE, this, null);
          castle.setSubMove(new Move(rookCur,
              squares[(int) square.getX() / GRIDSIZE + (i == 0 ? -1 : 1)][(int) destY / GRIDSIZE],
              Move.NORMAL, rookCur.getPiece(), null));
          preLegalMoves.add(castle);
        }
      }
    }
  }
}
