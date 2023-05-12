package XXLChess;

/**
 * Class representing the Rook.
 */
public class Rook extends Piece {
  public Rook(float x, float y, String code, Square curSquare) {
    super(x, y, code, curSquare);
    this.value = 5.25 * (this.isWhite ? 1 : -1);
  }

  @Override
  public void generateMove(Board curBoard) {
    setRookMove(curBoard);
  }
}
