package XXLChess;

public class Chancellor extends Piece {
  public Chancellor(int x, int y, String code, Square curSquare) {
    super(x, y, code, curSquare);
    this.value = 8.5 * (this.isWhite ? 1 : -1);
  }

  @Override
  public void generateMove(Board curBoard) {
    setHorseMove(curBoard, HORSE_RANGE);
    setRookMove(curBoard);
  }
}
