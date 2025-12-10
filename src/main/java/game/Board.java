package game;

import json.JsonBoard;
import json.JsonCell;
import util.CellUtils;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class Board {
    private final TreeMap<Integer, TreeMap<Integer, Cell>> cellPositionLookupMap;
    private final GameLogicHandler logicHandler;


    public Board(int totalPlayers) {
        cellPositionLookupMap = CellUtils.generateCells(totalPlayers);
        logicHandler = new GameLogicHandler(this, totalPlayers);
    }

    public Board(JsonBoard j) {
        cellPositionLookupMap = new TreeMap<>();
        for (var x : j.cells.keySet()) {
            var yMap = new TreeMap<Integer, Cell>();
            cellPositionLookupMap.put(x, yMap);
            for (var e : j.cells.get(x).entrySet()) {
                yMap.put(e.getKey(), new Cell(e.getValue(), x, e.getKey()));
            }
        }
        Cell selectedCell = null;
        if (j.selectedCellX != -1) {
            selectedCell = getCell(j.selectedCellX, j.selectedCellY);
        }

        logicHandler = new GameLogicHandler(this, j, selectedCell);
    }

    public JsonBoard serialize() {
        Map<Integer, Map<Integer, JsonCell>> jCells = new TreeMap<>();
        for (var x : cellPositionLookupMap.keySet()) {
            var yMap = new TreeMap<Integer, JsonCell>();
            jCells.put(x, yMap);
            for (var e : cellPositionLookupMap.get(x).entrySet()) {
                yMap.put(e.getKey(), e.getValue().serialize());
            }
        }
        return logicHandler.serialize(jCells);
    }

    public LinkedList<Cell> getAllCells() {
        return CellUtils.collectCells(cellPositionLookupMap);
    }

    public boolean isSaveable() {
        return logicHandler.getSaveable();
    }

    public int getTurnNumber() {
        return logicHandler.getTurnNumber();
    }

    public Cell getCell(int x, int y) {
        return CellUtils.getCell(cellPositionLookupMap, x, y);
    }

    public void handleClick(int x, int y) {
        logicHandler.handleClick(x, y);
    }
}
