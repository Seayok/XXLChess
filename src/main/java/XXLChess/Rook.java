package XXLChess;

public class Rook extends Piece {
  public Rook(int x, int y, String code, Square curSquare) {
    super(x, y, code, curSquare);
    this.value = 5.25 * (this.isWhite ? 1 : -1);
  }

  @Override
  public void generateMove(Board curBoard) {
    setRookMove(curBoard);
  }
}
