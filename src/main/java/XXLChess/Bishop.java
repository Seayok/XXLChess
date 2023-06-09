package XXLChess;

/**
 * Class of the Bishop.
 */
public class Bishop extends Piece {

  public Bishop(float x, float y, String code, Square curSquare) {
    super(x, y, code, curSquare);
    this.value = 3.625 * (this.isWhite ? 1 : -1);
  }

  @Override
  public void generateMove(Board curBoard) {
    setBishopMove(curBoard);
  }
}
