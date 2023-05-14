package XXLChess;

import processing.core.PApplet;

/**
 * Class representing the game.
 */
public class Game {
  private Board board;
  private App app;
  private Player player1;
  private boolean gameOver;
  private Player player2;
  private Player curPlayer;
  private int gameState;
  private int moveState; // 0 = start, 1 = choose, 2 = success, 3 = illegal
  private Message textBox;

  /**
   * Constructor for creating new game.
   *
   * @param board is a board we run our game on.
   * @param app is the application we run our game on.
   * @param player1 is the first player.
   * @param player2 is the second player.
   * @param textBox is the message box that displays messages.
   */
  public Game(Board board, App app, Player player1, Player player2, Message textBox) {
    this.app = app;
    this.board = board;
    this.player1 = player1;
    this.player2 = player2;
    this.textBox = textBox;
    this.curPlayer = player1.isWhite() ? player1 : player2;
  }

  /**
   * Display appropriate message and timer based on the state of the game.
   *
   * @param status is the status from the board. 0 is normal, 1 is being checked, 2 is playing legal
   *        move while in check, 3 is checkmate, 4 is timer over, 5 is resign, 6 is draw.
   */
  public void updateGameStatus(int status) {
    switch (status) {
      case 1:
        textBox.checkMessage();
        break;
      case 2:
        textBox.checkWarning();
        break;
      case 3:
        textBox.checkMate(curPlayer);
        break;
      case 4:
        gameOver = true;
        textBox.timeOver(curPlayer);
        break;
      case 5:
        textBox.resign();
        break;
      case 6:
        textBox.drawMessage();
        break;
      default:
        textBox.hide();
    }

    if (status >= 3) {
      player1.getClock().stop(false);
      player2.getClock().stop(false);
    }
  }

  public boolean isOver() {
    return gameOver;
  }

  /**
   * Updates the game state after player resign.
   */
  public void resign() {
    gameOver = true;
    updateGameStatus(5);
  }

  /**
   * Update the game state base on the move information from the previous move.
   *
   * @param moveState is the status of the previous legal move, 2 is success, 3 is failure since the
   *        previous legal move did not protect the kings
   */
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

  /**
   * Process the mouse click event from user.
   *
   * @param x is the x coordinate of the click.
   * @param y is the y coordinate of the click.
   */
  public void mouseClicked(int x, int y) {
    if (!gameOver && x < board.getSize() && y < board.getSize() && !curPlayer.isBot()) {
      if (moveState == 0) {
        moveState = board.startClick(x, y);
      } else {
        moveState = board.selectClick(x, y);
      }
      processMoveState(moveState);
    }
  }

  /**
   * Switch turn of the player.
   */
  public void switchTurn() {
    curPlayer.getClock().stop(true);
    curPlayer = (curPlayer == player1) ? player2 : player1;
    curPlayer.getClock().start(app);
  }

  /**
   * Update game state and draw components.
   *
   * @param app the main application.
   */
  public void tick(PApplet app) {
    textBox.draw(app);
    if (!curPlayer.isCalculating()) {
      board.draw(app);
    }
    player1.getClock().draw(app);
    player2.getClock().draw(app);
    if (curPlayer.isBot() && !gameOver && Piece.movingPieces == 0) {
      processMoveState(curPlayer.guessMove(board));
    }
  }
}
