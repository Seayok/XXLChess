package XXLChess;

public class Amazon extends Piece {

  public Amazon(int x, int y, String code, Square curSquare) {
    super(x, y, code, curSquare);
    this.value = 12 * (this.isWhite ? 1 : -1);
  }

  @Override
  public void generateMove(Board curBoard) {
    setBishopMove(curBoard);
    setRookMove(curBoard);
    setHorseMove(curBoard, HORSE_RANGE);
  }
}
