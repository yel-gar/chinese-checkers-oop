import javafx.application.Application;
import javafx.stage.Stage;
import ui.MainController;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        new MainController(stage).showStartPopup();
    }
}
