package game;

import errors.GameRuntimeException;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;

public class Board {
    private final int totalPlayers;
    private final Set<Cell> passedCells;
    private final Set<Cell> destsCache;
    private int currentPlayerID;  // start from 0
    private TreeMap<Integer, TreeMap<Integer, Cell>> cellPositionLookupMap;
    private Cell selectedCell;
    private boolean deselectionLocked = false;

    public Board(int totalPlayers) {
        currentPlayerID = 0;
        this.totalPlayers = totalPlayers;
        passedCells = new HashSet<>();
        destsCache = new HashSet<>();
        selectedCell = null;
        generateCells(totalPlayers);
    }

    public LinkedList<Cell> getAllCells() {
        var list = new LinkedList<Cell>();
        for (var eOuter : cellPositionLookupMap.entrySet()) {
            for (var e : eOuter.getValue().entrySet()) {
                list.add(e.getValue());
            }
        }

        return list;
    }

    private void generateCells(int totalPlayers) {
        // first just generate cells
        cellPositionLookupMap = new TreeMap<>();
        for (int y = 4; y < 17; y++) {
            for (int x = y - 4; x < 25 - (y - 4); x += 2) {
                cellPositionLookupMap.putIfAbsent(x, new TreeMap<>());
                cellPositionLookupMap.get(x).put(y, new Cell(x, y));
            }
        }

        for (int y = 0; y < 13; y++) {
            for (int x = 12 - y; x < 25 - (12 - y); x += 2) {
                cellPositionLookupMap.get(x).putIfAbsent(y, new Cell(x, y));
            }
        }

        // now set ownership and player
        // player 0 always set
        for (int y = 13; y < 17; y++) {
            for (int x = 9; x < 16; x++) {
                var cell = getCell(x, y);
                if (cell != null) {
                    cell.setInitialPlayer(0);
                }
            }
        }

        if (totalPlayers == 2 || totalPlayers == 6) {
            for (int y = 0; y < 4; y++) {
                for (int x = 9; x < 16; x++) {
                    var cell = getCell(x, y);
                    if (cell != null) {
                        cell.setInitialPlayer(1);
                    }
                }
            }
        }
        if (totalPlayers == 3 || totalPlayers == 6) {
            for (int y = 4; y < 8; y++) {
                for (int x = y % 2; x < 25; x++) {
                    if (x > 6 - (y - 4) && x < 18 + (y - 4)) {
                        continue;
                    }
                    var cell = getCell(x, y);
                    if (cell != null) {
                        cell.setInitialPlayer(totalPlayers == 3 ? (x < 12 ? 1 : 2) : (x < 12 ? 3 : 4));
                    }
                }
            }
        }
        if (totalPlayers == 6) {
            for (int y = 12; y > 8; y--) {
                for (int x = y % 2; x < 25; x++) {
                    if (x > 7 - (12 - y) && x < 17 + (12 - y)) {
                        continue;
                    }
                    var cell = getCell(x, y);
                    if (cell != null) {
                        cell.setInitialPlayer(x < 12 ? 5 : 2);
                    }
                }
            }
        }
    }

    private Cell getCell(int x, int y) {
        if (!cellPositionLookupMap.containsKey(x)) {
            return null;
        }

        return cellPositionLookupMap.get(x).getOrDefault(y, null);
    }

    public void handleClick(int x, int y) {
        var cell = getCell(x, y);
        if (cell == null) {
            System.out.println("Attempt to click empty space");
            return;
        }

        if (cell.isDestination()) {
            selectedCell.free(currentPlayerID);
            cell.occupy(currentPlayerID);
            passedCells.add(selectedCell);
            var wasAdjacent = selectedCell.isAdjacent(cell);
            if (!wasAdjacent) {
                deselectionLocked = true;
            }
            selectedCell = cell;
            checkValidMovesAfterMove(wasAdjacent);
        } else if (cell.isSelected() && !deselectionLocked) {
            selectedCell.deselect();
            selectedCell = null;
            clearDestsCache();
        }
        // double condition for readability
        else if (cell.isOccupied() && cell.isOccupiedBy(currentPlayerID) && !deselectionLocked) {
            cell.select(currentPlayerID);
            selectedCell = cell;
            checkValidMovesAfterSelection();
        }
    }

    private void switchTurn() {
        currentPlayerID++;
        if (currentPlayerID >= totalPlayers) {
            currentPlayerID = 0;
        }
        passedCells.clear();
        deselectionLocked = false;
    }

    private void checkValidMovesAfterMove(boolean wasAdjacent) {
        checkValidMoves(false, wasAdjacent);
    }

    private void checkValidMovesAfterSelection() {
        checkValidMoves(true, false);
    }

    private void checkValidMoves(boolean newTurn, boolean wasAdjacent) {
        clearDestsCache();
        if (!newTurn && wasAdjacent) {
            switchTurn();
            selectedCell.deselect();
            selectedCell = null;
            return;
        }

        var validMoveFound = false;
        int[][] directions = {
                {2, 0},
                {-2, 0},
                {1, 1},
                {1, -1},
                {-1, 1},
                {-1, -1}
        };
        var x = selectedCell.getX();
        var y = selectedCell.getY();
        for (var d : directions) {
            var targetCell = getCell(x + d[0], y + d[1]);
            if (targetCell == null) {
                continue;
            }
            if (!newTurn && targetCell.isEmpty()) {
                continue;
            }
            if (newTurn && targetCell.isEmpty()) {
                targetCell.setDest();
                destsCache.add(targetCell);
                validMoveFound = true;
                continue;
            }
            if (targetCell.isOccupied()) {
                var jumperCell = getCell(
                        targetCell.getX() + d[0],
                        targetCell.getY() + d[1]
                );
                if (jumperCell == null || !jumperCell.isEmpty()) {
                    continue;
                }
                if (jumperCell.isEmpty()) {
                    if (passedCells.contains(jumperCell)) {
                        continue;
                    }
                    jumperCell.setDest();
                    destsCache.add(jumperCell);
                    validMoveFound = true;
                    continue;
                }
            }
            throw new GameRuntimeException("Unhandled situation at checking valid moves");
        }

        if (!validMoveFound) {
            selectedCell.deselect();
            selectedCell = null;
            switchTurn();
        }
    }

    private void clearDestsCache() {
        for (var cell : destsCache) {
            cell.unsetDest();
        }
        destsCache.clear();
    }
}
