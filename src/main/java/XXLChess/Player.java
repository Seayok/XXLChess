package XXLChess;

import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import processing.core.PApplet;
import processing.data.JSONObject;


public class Player {
  private boolean isWhite;
  private boolean isBot;
  private boolean isLose;
  private Clock clock;

  public void setupSide(boolean isWhite, boolean isBot) {
    this.isWhite = isWhite;
    this.isBot = isBot;
  }

  public void lose() {
    this.isLose = true;
  }

  public boolean isBot() {
    return isBot;
  }

  public boolean isLose() {
    return isLose;
  }

  public boolean isWhite() {
    return isWhite;
  }

  public void setUpClock(JSONObject playertime) {
    float clockY = isBot ? 56 : 634;
    float clockX = 690;
    clock = new Clock(clockX, clockY);
    clock.setConfig(playertime.getInt("seconds"), playertime.getInt("increment"));
  }

  public Clock getClock() {
    return clock;
  }

  public void endTurn() {
    this.clock.stop(true);
  }

  public void startTurn(PApplet app) {
    this.clock.start(app);
  }

  public int guessMove(Board curBoard, int prevVal) {
    if (clock.getCountDown() != prevVal) {
      List<Move> moveList = curBoard.getAllMoves(isWhite, true);
      Move resMove = null;
      double miniMax = isWhite ? -Double.POSITIVE_INFINITY : Double.POSITIVE_INFINITY;
      for (Move move : moveList) {
        double moveScore = curBoard.evaluateMove(move, 3, -Double.POSITIVE_INFINITY,
            Double.POSITIVE_INFINITY, !isWhite);
        if ((isWhite && moveScore > miniMax) || (!isWhite && moveScore < miniMax)) {
          resMove = move;
          miniMax = moveScore;
        }

      }
      System.out.println(miniMax);
      curBoard.makeMove(resMove, true, true);
      return 2;
    } else {
      return 0;
    }
  }


}
