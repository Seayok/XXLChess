package XXLChess;

public class Guard extends Piece {

  public Guard(int x, int y, String code, Square curSquare) {
    super(x, y, code, curSquare);
    this.value = 5 * (this.isWhite ? 1 : -1);
  }

  @Override
  public void generateMove(Board curBoard) {
    setKingMove(curBoard);
    setHorseMove(curBoard, HORSE_RANGE);
  }
}
