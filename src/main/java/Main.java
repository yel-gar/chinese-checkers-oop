import javafx.application.Application;
import javafx.stage.Stage;
import ui.MainController;

public class Main extends Application {
    private MainController controller;

    @Override
    public void start(Stage stage) throws Exception {
        controller = new MainController(stage);
        controller.showStartPopup();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
