package game;

import errors.GameRuntimeException;
import json.JsonBoard;
import json.JsonCell;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameLogicHandler {
    private final Board board;
    private final Set<Cell> passedCells;
    private final Set<Cell> destsCache;
    private final int totalPlayers;
    private Cell selectedCell;
    private boolean deselectionLocked = false;
    private int currentPlayerID;  // start from 0
    private int turnNumber;

    public GameLogicHandler(Board board, int totalPlayers) {
        this.board = board;
        currentPlayerID = 0;
        turnNumber = 0;
        this.totalPlayers = totalPlayers;
        passedCells = new HashSet<>();
        destsCache = new HashSet<>();
        selectedCell = null;
    }

    public GameLogicHandler(Board board, JsonBoard j, Cell selectedCell) {
        this.board = board;
        currentPlayerID = j.currentPlayerID;
        totalPlayers = j.totalPlayers;
        turnNumber = j.turnNumber;
        passedCells = new HashSet<>();
        destsCache = new HashSet<>();
        this.selectedCell = selectedCell;
    }

    public JsonBoard serialize(Map<Integer, Map<Integer, JsonCell>> jCells) {
        return new JsonBoard(
                totalPlayers,
                currentPlayerID,
                turnNumber,
                jCells,
                selectedCell == null ? -1 : selectedCell.getX(),
                selectedCell == null ? -1 : selectedCell.getY()
        );
    }

    public boolean getSaveable() {
        return !deselectionLocked;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public void handleClick(int x, int y) {
        var cell = board.getCell(x, y);
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
        turnNumber++;
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
            var targetCell = board.getCell(x + d[0], y + d[1]);
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
                var jumperCell = board.getCell(
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
