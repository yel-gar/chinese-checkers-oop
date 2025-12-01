package ui;

import errors.GameRuntimeException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.stream.Stream;

public class StartPopup {
    private final MainController mainController;
    private Stage popupStage;

    public StartPopup(MainController controller) {
        mainController = controller;
    }

    public void show() {
        popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("New Game");
        popupStage.setResizable(false);

        Label titleLabel = new Label("Players number");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ToggleGroup group = new ToggleGroup();

        var buttons = Stream.of("2", "3", "6").map(v -> new RadioButton(String.format("%s players", v))).toList();
        buttons.forEach(b -> b.setToggleGroup(group));
        buttons.getFirst().setSelected(true);  // default

        var startButton = new Button("START GAME");
        startButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 15px 30px;");
        startButton.setDefaultButton(true);

        var loadButton = new Button("LOAD GAME");
        loadButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 15px 30px;");

        var layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        var children = layout.getChildren();
        children.addAll(titleLabel);
        children.addAll(buttons);
        children.addAll(startButton);
        children.addAll(loadButton);

        startButton.setOnAction(_ -> {
            int playerCount = 2;
            for (var b : buttons) {
                if (b.isSelected()) {
                    playerCount = Integer.parseInt(b.getText().substring(0, 1));
                    break;
                }
            }

            mainController.startGame(playerCount);
            popupStage.close();
        });

        loadButton.setOnAction(_ -> {
            var chooser = new FileChooser();
            chooser.setTitle("Select game file");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json")
            );

            var selectedFile = chooser.showOpenDialog(popupStage);
            if (selectedFile == null) {
                return;
            }
            try {
                mainController.loadGame(selectedFile);
                popupStage.close();
            } catch (IOException e) {
                throw new GameRuntimeException("Can't find game file");
            }
        });

        var scene = new Scene(layout);
        popupStage.setScene(scene);
        popupStage.sizeToScene();
        popupStage.showAndWait();
    }
}
