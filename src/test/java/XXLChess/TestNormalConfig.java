package XXLChess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.data.JSONObject;

public class TestNormalConfig {
  Player player1;
  Player player2;
  Clock clockWhite;
  Clock clockBlack;
  String configPath;
  Helper helper;
  Message textBox;
  Board board;
  Game game;
  App app;

  @BeforeEach
  public void setup() {
    app = new App();
    player1 = new Player();
    player2 = new Player();
    configPath = "config.json";
    helper = new Helper();
    textBox = new Message(680, 300, 24);

    JSONObject conf = PApplet.loadJSONObject(new File(configPath));
    helper.setConfig(conf);
    helper.initTimeAndSide(player1, player2);
    helper.updateMoveStatus(conf, player1.isWhite());
    clockWhite = player1.isWhite() ? player1.getClock() : player2.getClock();
    clockBlack = player1.isWhite() ? player2.getClock() : player1.getClock();
    clockWhite.start(app);
    board = new Board(helper.loadBoard(), app);
    game = new Game(board, app, player1, player2, textBox);

  }

  @Test
  public void testMessage() {
    assertEquals(24, textBox.getSize());
    assertEquals("", textBox.getText());
    textBox.checkWarning();
    assertEquals("Your king is\nin check!\n\n\nYou must defend\nyour king!", textBox.getText());
    app.keyPressed();
    game.resign();
    assertEquals("You resign!\n\n\nPress 'r' to restart\nthe game", textBox.getText());
  }

  @Test
  public void testClock() {
    assertEquals(180, clockWhite.getCountDown());
    assertEquals(180, clockBlack.getCountDown());
    clockWhite.stop(true);
    assertEquals(182, clockWhite.getCountDown());
  }

  @Test
  public void testBoard() {
    assertEquals(56, board.getPieceList().size());
    assertEquals(672, board.getSize());
  }

  @Test
  public void testPlayer() {
    assertFalse(player1.isBot());
    assertTrue(player1.isWhite());
    assertTrue(player2.isBot());
    assertFalse(player2.isWhite());
    assertFalse(player1.isCalculating());
    assertFalse(player2.isCalculating());
  }

  @Test
  public void testClick() {
    Square[][] squares = board.getSquareMat();
    // Click on square without piece
    assertEquals(0, board.startClick(60, 260));

    // Click on square of opoment piece
    assertEquals(0, board.startClick(60, 672 - 586));

    // Click on square with piece
    game.mouseClicked(60, 586);
    assertTrue(squares[60 / 48][538 / 48].isOnPieceWay());
    assertTrue(squares[60 / 48][490 / 48].isOnPieceWay());
    game.mouseClicked(100, 586);
    assertTrue(squares[100 / 48][538 / 48].isOnPieceWay());
    assertTrue(squares[100 / 48][490 / 48].isOnPieceWay());
    assertFalse(squares[60 / 48][538 / 48].isOnPieceWay());
    assertFalse(squares[60 / 48][490 / 48].isOnPieceWay());
    game.mouseClicked(10, 586);
    assertFalse(squares[100 / 48][538 / 48].isOnPieceWay());
    assertFalse(squares[100 / 48][490 / 48].isOnPieceWay());
    game.mouseClicked(100, 586);
    game.mouseClicked(100, 538);
  }

  @Test
  public void testMakeMove() {
    board.makeMove(board.getAllMoves(true, false).get(0), true, false);
    game.processMoveState(2);
    player2.guessMove(board);
    assertFalse(player1.isCalculating());
    assertTrue(player2.isCalculating());
    player2.guessMove(board);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    int moveState = player2.guessMove(board);
    assertEquals(2, moveState);
    game.processMoveState(moveState);
    game.mouseClicked(300, 624 + 10);
    game.mouseClicked(300 - 38, 624 + 10 - 48 * 2);
    player2.guessMove(board);
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    moveState = player2.guessMove(board);
    assertEquals(2, moveState);
  }

  @Test
  public void testOutOfTime() {
    game.updateGameStatus(4);
    assertTrue(textBox.getText().equals("You lost\non time!\n\n\nPress 'r' to restart\nthe game"));
  }
}
