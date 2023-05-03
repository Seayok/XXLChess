package XXLChess;

import processing.core.PApplet;

public class Game {
  private Board board;
  private App app;
  private Clock clockBlack;
  private Clock clockWhite;
  private Player player1;
  private boolean gameOver;
  private Player player2;
  private Player curPlayer;
  private int gameState;
  private int moveState; // 0 = start, 1 = choose, 2 = success, 3 = illegal
  private int prevVal;
  private Message textBox;

  public Game(Board board, App app, Player player1, Player player2, Clock clockBlack,
      Clock clockWhite, Message textBox) {
    this.app = app;
    this.board = board;
    this.player1 = player1;
    this.player2 = player2;
    this.clockBlack = clockBlack;
    this.clockWhite = clockWhite;
    this.textBox = textBox;
    this.curPlayer = player1.isWhite() ? player1 : player2;
  }

  public void updateGameStatus(int status) {
    switch (status) {
      case 0:
        textBox.hide();
        break;
      case 1:
        textBox.checkMessage();
        break;
      case 2:
        textBox.checkWarning();
        break;
      case 3:
        textBox.checkMate(curPlayer);
        clockBlack.stop(false);
        clockWhite.stop(false);
        break;
      case 4:
        gameOver = true;
        textBox.timeOver(curPlayer);
        clockBlack.stop(false);
        clockWhite.stop(false);
        break;
      case 5:
        textBox.resign();
        clockBlack.stop(false);
        clockWhite.stop(false);
        break;
      case 6:
        textBox.drawMessage();
        clockBlack.stop(false);
        clockWhite.stop(false);
        break;
      default:
        textBox.hide();
    }
  }

  public void resign() {
    curPlayer.lose();
    gameOver = true;
    updateGameStatus(5);
  }

  public void processMoveState(int moveState) {
    if (moveState == 2) {
      gameState = 0;
      this.moveState = 0;
      switchTurn();
      if (board.checkCheck()) {
        gameState = 1;
        if (board.checkCheckMate()) {
          gameOver = true;
          gameState = 3;
          board.displayCheckMatePiece();
        }
      } else if (board.checkCheckMate()) {
        gameOver = true;
        gameState = 6;
      }
    }
    if (moveState == 3) {
      gameState = 2;
    }
    updateGameStatus(gameState);
  }

  public void mouseClicked(int x, int y) {
    if (!gameOver && x < board.getSize() && y < board.getSize() && !curPlayer.isBot()) {
      if (moveState == 0) {
        moveState = board.startClick(x, y);
      } else {
        moveState = board.selectClick(x, y);
      }
    }
    processMoveState(moveState);
  }

  public void switchTurn() {
    curPlayer.getClock().stop(true);
    curPlayer = (curPlayer == player1) ? player2 : player1;
    curPlayer.getClock().start(app);
    prevVal = curPlayer.getClock().getCountDown();
    board.newMoveSet();
  }

  public void draw(PApplet app) {
    if (curPlayer.isBot() && !gameOver) {
      processMoveState(curPlayer.makeRandomMove(board, prevVal));
    }
  }
}
