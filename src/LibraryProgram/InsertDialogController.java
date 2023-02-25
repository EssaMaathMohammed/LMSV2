package LibraryProgram;

import javafx.event.ActionEvent;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;

public class InsertDialogController {
    public TextField nameTextField;
    public TextField priceTextField;
    public TextField quantityTextField;
    public TextField isbnTextField;
    public DialogPane BookInsertDialog;
    public String imageSource;
    public TextField authorTextField;
    public TextField publisherTextField;
    public TextField dateTextField;

    public void chooseImageButtonPressed(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose A Book Image");
        fileChooser.setInitialDirectory(new File("."));
        File file = fileChooser.showOpenDialog(BookInsertDialog.getScene().getWindow());
        if (file != null) {
            imageSource = file.getAbsolutePath();
        }
    }

    public String getImageSource() {
        return imageSource;
    }

    public String getAuthorTextField() {
        return authorTextField.getText();
    }

    public String getPublisherTextField() {
        return publisherTextField.getText();
    }

    public String getDateTextField() {
        return dateTextField.getText();
    }

    public String getNameTextField() {
        return nameTextField.getText().trim();
    }

    public double getPriceTextField() {
        return Double.parseDouble(priceTextField.getText().trim());
    }

    public int getQuantityTextField() {
        return Integer.parseInt(quantityTextField.getText().trim());
    }

    public long getIsbnTextField() {
        return Long.parseLong(isbnTextField.getText().trim().replaceAll("-",""));
    }

}
