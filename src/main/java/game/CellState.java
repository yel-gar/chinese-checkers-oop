package game;

public enum CellState {
    EMPTY(0),
    OCCUPIED(1),
    SELECTED(2),
    DESTINATION(3);

    public final int val;

    CellState(int val) {
        this.val = val;
    }

    public static CellState getByVal(int val) {
        for (var c : CellState.values()) {
            if (c.val == val) {
                return c;
            }
        }

        return null;
    }
}
