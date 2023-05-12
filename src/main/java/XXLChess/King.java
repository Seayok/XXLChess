package XXLChess;

/**
 * class of the King.
 */
public class King extends Piece {
  public King(float x, float y, String code, Square curSquare) {
    super(x, y, code, curSquare);
    this.value = 10000 * (this.isWhite ? 1 : -1);
  }

  @Override
  public void generateMove(Board curBoard) {
    setKingMove(curBoard);
    if (!this.isMoved) {
      setCastleMove(curBoard);
    }
  }

  private void setCastleMove(Board curBoard) {
    Square[][] squares = curBoard.getSquareMat();
    for (int i = 0; i < 2; i++) {
      int weight = i == 0 ? 1 : -1;
      int col = (int) curSquare.getX() / GRIDSIZE;
      int row = (int) curSquare.getY() / GRIDSIZE;
      int offset = col + weight;
      Piece piece = null;
      while (offset >= 0 && offset < GRIDNUM) {
        Square horizontalSquare = squares[offset][row];
        piece = horizontalSquare.getPiece();
        if (piece != null) {
          break;
        }
        offset += weight;
      }
      if (Math.abs(offset - col) > 2 && piece != null && piece.isWhitePiece() == isWhite
          && !piece.isMoved && piece.getCode().contains("r")) {
        Move castle = new Move(curSquare, squares[col + 2 * weight][row], Move.CASTLE, this, null);
        Move submove =
            new Move(piece.getSquare(), squares[col + weight][row], Move.NORMAL, piece, null);
        castle.setSubMove(submove);
        preLegalMoves.add(castle);
      }
    }
  }
}
