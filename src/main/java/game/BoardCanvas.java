package game;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.LinkedList;

public class BoardCanvas extends Canvas {
    private final static int CANVAS_WIDTH = 800;
    private final static int CANVAS_HEIGHT = 600;
    private final static int CANVAS_OFFSET_X = 100;
    private final static int CANVAS_OFFSET_Y = 10;
    private final static int CANVAS_SCALE_FACTOR_X = 23;
    private final static int CANVAS_SCALE_FACTOR_Y = 35;
    private final static int CELL_SIZE = 20;

    private final static Color[] PLAYER_COLORS = {Color.GREEN, Color.BLUE, Color.YELLOW, Color.RED, Color.MAGENTA, Color.CYAN};

    private Board board;
    private LinkedList<Cell> cells;
    private GraphicsContext gc;

    public BoardCanvas(Board board) {
        super(CANVAS_WIDTH, CANVAS_HEIGHT);
        this.board = board;
        cells = board.getAllCells();
        gc = getGraphicsContext2D();
        setOnMouseClicked(this::mouseClickHandler);
        update();
    }

    private void update() {
        gc.clearRect(0, 0, getWidth(), getHeight());
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, getWidth(), getHeight());

        gc.setStroke(Color.BLACK);
        for (var cell : cells) {
            var x = cell.getX();
            var y = cell.getY();
            x = CANVAS_OFFSET_X + x * CANVAS_SCALE_FACTOR_X;
            y = CANVAS_OFFSET_Y + y * CANVAS_SCALE_FACTOR_Y;
            gc.strokeOval(
                    x,
                    y,
                    CELL_SIZE, CELL_SIZE
            );
            if (cell.getOwnerID() >= 0) {
                gc.setFill(PLAYER_COLORS[cell.getOwnerID()].deriveColor(0, 1, 1, 0.2));
                gc.fillRect(x - 5, y - 5, CELL_SIZE + 10, CELL_SIZE + 10);
            }
            if (cell.isOccupied()) {
                gc.setFill(PLAYER_COLORS[cell.getUnitPlayerID()]);
                gc.fillOval(x, y, CELL_SIZE, CELL_SIZE);
            }
            if (cell.isSelected()) {
                gc.setFill(new Color(0.7, 0.7, 0.7, 0.7));
                gc.fillRect(x - 2.5, y - 2.5, CELL_SIZE + 5, CELL_SIZE + 5);
                gc.setFill(PLAYER_COLORS[cell.getUnitPlayerID()]);
                gc.fillOval(x, y, CELL_SIZE, CELL_SIZE);
            }
            if (cell.isDestination()) {
                gc.setFill(Color.GREENYELLOW);
                gc.fillOval(x + 5, y + 5, CELL_SIZE - 10, CELL_SIZE - 10);
            }
        }
    }

    private void mouseClickHandler(MouseEvent e) {
        board.handleClick(
                (int) (e.getX() - CANVAS_OFFSET_X) / CANVAS_SCALE_FACTOR_X,
                (int) (e.getY() - CANVAS_OFFSET_Y) / CANVAS_SCALE_FACTOR_Y
        );
        update();
    }
}
