package LibraryProgram;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class LibLauncher extends Application {

    public static Stage applicationStage;

    @Override
    public void start(Stage stage) throws Exception {
        applicationStage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("fxmlFiles/LoginForm.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("SPU Library");

        stage.setWidth(1535.0);
        stage.setHeight(800);
        stage.getIcons().add(new Image("/resources/image0.png"));
        stage.setIconified(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
