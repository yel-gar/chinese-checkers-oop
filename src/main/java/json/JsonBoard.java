package json;

import java.util.Map;

public class JsonBoard {
    public int totalPlayers;
    public int currentPlayerID;
    public int turnNumber;
    public Map<Integer, Map<Integer, JsonCell>> cells;
    public int selectedCellX;
    public int selectedCellY;

    public JsonBoard(int totalPlayers, int currentPlayerID, int turnNumber, Map<Integer, Map<Integer, JsonCell>> cells, int selectedCellX, int selectedCellY) {
        this.totalPlayers = totalPlayers;
        this.currentPlayerID = currentPlayerID;
        this.turnNumber = turnNumber;
        this.cells = cells;
        this.selectedCellX = selectedCellX;
        this.selectedCellY = selectedCellY;
    }
}
