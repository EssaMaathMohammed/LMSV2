package LibraryProgram.fxmlFiles;

import LibraryProgram.LibLauncher;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;

import javafx.scene.layout.Pane;

import java.net.URL;

public class FxmlLoader {
    private Pane view;

    public Pane getView(String fileName) {

        try {
            URL fileUrl = LibLauncher.class.getResource("/LibraryProgram/fxmlFiles/" + fileName + ".fxml");

            if (fileUrl == null) {
                throw new Exception("There Is No File");
            }

            view = FXMLLoader.load(fileUrl);

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("PATH ERROR");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }

        return view;
    }

    
}
