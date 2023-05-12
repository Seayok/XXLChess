package XXLChess;

/**
 * Class representing the Queen.
 */
public class Queen extends Piece {
  public Queen(float x, float y, String code, Square curSquare) {
    super(x, y, code, curSquare);
    this.value = 9.5 * (this.isWhite ? 1 : -1);
  }

  @Override
  public void generateMove(Board curBoard) {
    setBishopMove(curBoard);
    setRookMove(curBoard);
  }
}
