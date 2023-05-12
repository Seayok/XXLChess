package XXLChess;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import processing.data.JSONObject;

/**
 * Class handling initialization of the game.
 */
public class Helper {
  private JSONObject config;

  public void setConfig(JSONObject config) {
    this.config = config;
  }

  /**
   * Setup the side of each player and their clocks.
   *
   * @param player1 is the first player.
   * @param player2 is the second player.
   */
  public void initTimeAndSide(Player player1, Player player2) {
    JSONObject timeConfig = config.getJSONObject("time_controls");
    boolean playerIsWhite = (config.getString("player_colour").equals("white"));
    player1.setupSide(playerIsWhite, false);
    player2.setupSide(!playerIsWhite, true);
    final JSONObject player1Time = timeConfig.getJSONObject("player");
    final JSONObject player2Time = timeConfig.getJSONObject("cpu");
    player1.setDepth(2);
    player2.setDepth(2);
    player1.setUpClock(player1Time);
    player2.setUpClock(player2Time);
  }

  /**
   * Setup move stat such as movement speed, movement time, and pawn direction.
   *
   * @param conf is the file config.
   * @param playerIsWhite is the flag indicate if the real player is playing white or not.
   */
  public void updateMoveStatus(JSONObject conf, boolean playerIsWhite) {
    int pawndir = playerIsWhite ? 1 : -1;
    Piece.setMoveStat(conf.getDouble("piece_movement_speed"), conf.getInt("max_movement_time"),
        pawndir);
  }

  /**
   * Parse the level file into matrix.
   *
   * @return a matrix containing the containing the pieces' codes within the corresponding positions
   */
  public String[][] loadBoard() {
    int gridSize = 14;
    String[][] arr = new String[gridSize][gridSize];

    try (Scanner scan = new Scanner(new File(config.getString("layout")))) {

      for (int i = 0; i < gridSize; i++) {
        String line = "";

        if (scan.hasNextLine()) {
          line = scan.nextLine();
        }

        for (int j = 0; j < gridSize; j++) {

          if (j > line.length() - 1) {
            arr[i][j] = " ";
          } else {
            char lineChar = line.charAt(j);
            if (lineChar == ' ') {
              arr[i][j] = " ";
            } else if (Character.isUpperCase(lineChar)) {
              arr[i][j] = "b" + Character.toString(line.charAt(j)).toLowerCase();
            } else {
              arr[i][j] = "w" + Character.toString(line.charAt(j));
            }
          }
        }
      }
    } catch (IOException e) {

      e.printStackTrace();
    }
    return arr;
  }
}
