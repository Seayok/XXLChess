package XXLChess;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import processing.data.JSONObject;

public class Helper {
  private JSONObject config;

  public void setConfig(JSONObject config) {
    this.config = config;
  }

  public void initTimeAndSide(Player player1, Player player2) {
    JSONObject timeConfig = config.getJSONObject("time_controls");
    boolean playerIsWhite = (config.getString("player_colour").equals("white"));
    player1.setupSide(playerIsWhite, false);
    player2.setupSide(!playerIsWhite, true);
    JSONObject player1Time = timeConfig.getJSONObject("player");
    JSONObject player2Time = timeConfig.getJSONObject("cpu");
    player1.setUpClock(player1Time);
    player2.setUpClock(player2Time);
  }

  public void updateMoveStatus(JSONObject conf, boolean playerIsWhite) {
    int pawndir = playerIsWhite ? 1 : -1;
    Piece.setMoveStat(conf.getDouble("piece_movement_speed"), conf.getInt("max_movement_time"),
        pawndir);
  }

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

            if (Character.isUpperCase(lineChar) || lineChar == ' ') {
              arr[i][j] = Character.toString(line.charAt(j));
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
