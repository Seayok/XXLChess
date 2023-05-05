package XXLChess;

public class Archbishop extends Piece {

  public Archbishop(int x, int y, String code, Square curSquare) {
    super(x, y, code, curSquare);
    this.value = 7.5 * (this.isWhite ? 1 : -1);
  }

  @Override
  public void generateMove(Board curBoard) {
    setBishopMove(curBoard);
    setHorseMove(curBoard, HORSE_RANGE);
  }
}

