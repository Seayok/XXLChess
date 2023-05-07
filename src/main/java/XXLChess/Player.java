package XXLChess;

import java.util.List;
import processing.core.PApplet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import processing.data.JSONObject;


public class Player {
  private boolean isWhite;
  private boolean isBot;
  private Board curBoard;
  private Move moveToPlay;
  private boolean calculating;
  private boolean isLose;
  private int depth = 2;
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

  public boolean isCalculating() {
    return calculating;
  }

  public int guessMove(Board curBoard, int prevVal) {
    if (clock.getCountDown() == prevVal) {
      return 0;
    }
    if (!calculating) {
      this.curBoard = curBoard;
      calculating = true;
      calculateMove();
      return 0;
    } else if (this.moveToPlay == null) {
      return 0;
    } else {
      curBoard.makeMove(moveToPlay, true, true, false);
      this.curBoard = null;
      this.calculating = false;
      this.moveToPlay = null;
      return 2;
    }
  }

  public void calculateMove() {
    Runnable job = new CalculateMove();
    ExecutorService executor = Executors.newSingleThreadExecutor();
    executor.execute(job);

    executor.shutdown();
  }

  class CalculateMove implements Runnable {
    public void run() {
      List<Move> moveList = curBoard.getAllMoves(isWhite, true);
      Move resMove = null;
      double miniMax = isWhite ? -Double.POSITIVE_INFINITY : Double.POSITIVE_INFINITY;
      double alpha = -Double.POSITIVE_INFINITY;
      double beta = Double.POSITIVE_INFINITY;
      for (Move move : moveList) {
        double moveScore = curBoard.evaluateMove(move, depth, alpha, beta, !isWhite);
        if (isWhite) {
          alpha = Math.max(alpha, moveScore);
        } else {
          beta = Math.min(beta, moveScore);
        }
        if ((isWhite && moveScore > miniMax) || (!isWhite && moveScore < miniMax)) {
          resMove = move;
          miniMax = moveScore;
        }

      }
      if (Math.abs(miniMax) == Double.POSITIVE_INFINITY) {
        depth--;
      }
      moveToPlay = resMove;
      System.out.println(miniMax);
    }
  }

}
