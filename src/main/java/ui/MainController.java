package ui;

import game.Board;
import game.BoardCanvas;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainController {
    private final static int SCENE_WIDTH = 900;
    private final static int SCENE_HEIGHT = 700;

    private Stage primaryStage;
    private BoardCanvas canvas;
    private int playerCount = 2;

    public MainController(Stage stage) {
        primaryStage = stage;
    }

    public void showStartPopup() {
        StartPopup popup = new StartPopup(this);
        popup.show();
    }

    public void startGame(int playerCount) {
        this.playerCount = playerCount;
        showMainWindow();
    }

    private void showMainWindow() {
        BorderPane root = new BorderPane();

        var board = new Board(playerCount);
        canvas = new BoardCanvas(board);

        Button newGameButton = new Button("New Game");
        newGameButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        newGameButton.setOnAction(e -> showStartPopup());

        VBox topPanel = new VBox(newGameButton);
        root.setTop(topPanel);
        root.setCenter(canvas);

        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        primaryStage.setTitle("ChineseCheckers");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
