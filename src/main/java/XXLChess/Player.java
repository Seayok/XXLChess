package XXLChess;

import java.util.List;
import java.util.Random;
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
  private int depth;
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

  public void setDepth(int depth) {
    this.depth = depth;
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
      curBoard.makeMove(moveToPlay, true, false);
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
      Random generator = new Random();
      List<Move> moveList = curBoard.getAllMoves(isWhite, true);
      Move resMove = null;
      double miniMax = isWhite ? -Double.POSITIVE_INFINITY : Double.POSITIVE_INFINITY;
      double alpha = -Double.POSITIVE_INFINITY;
      double beta = Double.POSITIVE_INFINITY;
      for (Move move : moveList) {
        double moveScore = curBoard.evaluateMove(move, depth, alpha, beta, !isWhite);
        if ((isWhite && moveScore > miniMax) || (!isWhite && moveScore < miniMax)) {
          resMove = move;
          miniMax = moveScore;
        }
        if (moveScore == miniMax) {
          if (resMove == null || generator.nextInt(2) == 1) {
            resMove = move;
          }
        }

      }
      if (Math.abs(miniMax) == Double.POSITIVE_INFINITY) {
        depth--;
      }
      moveToPlay = resMove;
    }
  }

}
