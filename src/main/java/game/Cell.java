package game;

import errors.GameRuntimeException;

public class Cell {
    private final int x;
    private final int y;
    private int ownedByPlayerID;  // if this is home area, ID of owner player, -1 if shared
    private int unitPlayerID;  // ID of a player who has unit, -1 if unoccupied
    private CellState state;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        ownedByPlayerID = -1;
        unitPlayerID = -1;
        state = CellState.EMPTY;
    }

    public void setInitialPlayer(int playerID) {
        state = CellState.OCCUPIED;
        ownedByPlayerID = playerID;
        unitPlayerID = playerID;
    }

    public int getUnitPlayerID() {
        return unitPlayerID;
    }

    public int getOwnerID() {
        return ownedByPlayerID;
    }

    public boolean isOccupiedBy(int playerID) {
        return unitPlayerID == playerID;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isAdjacent(Cell other) {
        return Math.abs(other.getX() - this.x) <= 2 && Math.abs(other.getY() - this.y) <= 1;
    }

    public boolean isEmpty() {
        return this.state == CellState.EMPTY;
    }

    public boolean isSelected() {
        return this.state == CellState.SELECTED;
    }

    public boolean isDestination() {
        return this.state == CellState.DESTINATION;
    }

    public boolean isOccupied() {
        return this.state == CellState.OCCUPIED;
    }

    public void select(int currentPlayerID) {
        if (!isOccupied()) {
            throw new GameRuntimeException("Attempt to select unoccupied cell");
        }
        if (currentPlayerID != unitPlayerID) {
            throw new GameRuntimeException("Attempt to select another player cell");
        }

        this.state = CellState.SELECTED;
    }

    public void deselect() {
        if (!isSelected()) {
            throw new GameRuntimeException("Attempt to deselect not selected cell");
        }

        this.state = this.unitPlayerID < 0 ? CellState.EMPTY : CellState.OCCUPIED;
    }

    public void occupy(int newPlayerID) {
        if (!isDestination()) {
            throw new GameRuntimeException("Attempt to move to invalid destination");
        }

        this.unitPlayerID = newPlayerID;
        this.state = CellState.SELECTED;
    }

    public void free(int currentPlayerID) {
        if (!isSelected()) {
            throw new GameRuntimeException("Attempt to move unselected piece");
        }
        if (currentPlayerID != unitPlayerID) {
            throw new GameRuntimeException("Attempt to move another player cell");
        }

        this.unitPlayerID = -1;
        this.state = CellState.EMPTY;
    }

    public void setDest() {
        if (!isEmpty()) {
            throw new GameRuntimeException("Attempt to select occupied cell as destination");
        }

        this.state = CellState.DESTINATION;
    }

    public void unsetDest() {
        if (!isDestination()) {
            return;
        }

        this.state = CellState.EMPTY;
    }
}
