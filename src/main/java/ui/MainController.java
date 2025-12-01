package ui;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import errors.GameRuntimeException;
import game.Board;
import game.BoardCanvas;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import json.JsonBoard;

import java.io.*;

public class MainController {
    private final static Gson GSON = new Gson();
    private final static int SCENE_WIDTH = 900;
    private final static int SCENE_HEIGHT = 700;

    private final Stage primaryStage;
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

    public void loadGame(File file) throws IOException {
        try (var r = new FileReader(file)) {
            JsonBoard jBoard = GSON.fromJson(r, JsonBoard.class);
            showMainWindow(new Board(jBoard));
        }
    }

    public void saveGame(File file, Board board) throws IOException {
        try (var w = new FileWriter(file)) {
            GSON.toJson(board.serialize(), w);
        }
    }

    private void showMainWindow() {
        showMainWindow(new Board(playerCount));
    }

    private void showMainWindow(Board board) {
        BorderPane root = new BorderPane();
        var canvas = new BoardCanvas(board);

        Button newGameButton = new Button("New Game");
        newGameButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        newGameButton.setOnAction(_ -> showStartPopup());

        var saveGameButton = new Button("Save Game");
        saveGameButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        saveGameButton.setOnAction(_ -> {
            if (!board.isSaveable()) {
                throw new GameRuntimeException("Cannot save right now, please finish turn");
            }
            var chooser = new FileChooser();
            chooser.setTitle("Select game file");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json")
            );

            var selectedFile = chooser.showSaveDialog(primaryStage);
            if (selectedFile == null) {
                return;
            }
            try {
                saveGame(selectedFile, board);
            } catch (IOException e) {
                throw new GameRuntimeException("Failed to save game");
            }
        });

        VBox topPanel = new VBox(newGameButton, saveGameButton);
        root.setTop(topPanel);
        root.setCenter(canvas);

        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        primaryStage.setTitle("ChineseCheckers");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
