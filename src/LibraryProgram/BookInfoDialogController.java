package LibraryProgram;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.MalformedURLException;

public class BookInfoDialogController {

    public TextField BookTitle;
    public TextField BookAuthor;
    public TextField BookDate;
    public Label BookIsbn;
    public TextField BookPublisher;
    public ImageView ImageSource;
    public DialogPane infoDialogPane;
    public static String updatedImageSource;
    public TextField quantityTextField;

    public String getQuantityTextField() {
        return quantityTextField.getText();
    }

    public void setQuantityTextField(String quantityTextField) {
        this.quantityTextField.setText(quantityTextField);
    }

    public void setBookTitle(String bookTitle) {
        BookTitle.setText(bookTitle);
    }

    public void setBookAuthor(String bookAuthor) {
        BookAuthor.setText(bookAuthor);
    }

    public void setBookDate(String bookDate) {
        BookDate.setText(bookDate);
    }

    public void setBookIsbn(String bookIsbn) {
        BookIsbn.setText(bookIsbn);
    }

    public void setBookPublisher(String bookPublisher) {
        BookPublisher.setText(bookPublisher);
    }

    public void setImageSource(String imageSource) {
        if (imageSource == null) {
            ImageSource.setImage(new Image("resources/noImgFound.jpg"));
        }else {
            File file = new File(imageSource);
            Image image = new Image(file.toURI().toString());
            ImageSource.setImage(image);
            updatedImageSource = imageSource;
        }
    }

    public String getBookTitle() {
        return BookTitle.getText();
    }

    public String getBookAuthor() {
        return BookAuthor.getText();
    }

    public String getBookDate() {
        return BookDate.getText();
    }

    public String getBookIsbn() {
        return BookIsbn.getText();
    }

    public String getBookPublisher() {
        return BookPublisher.getText();
    }

    public void updateImageButtonPressed(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose A Book Image");
        fileChooser.setInitialDirectory(new File("."));
        File file = fileChooser.showOpenDialog(infoDialogPane.getScene().getWindow());
        if (file != null) {
                updatedImageSource = file.getAbsolutePath();
                setImageSource(updatedImageSource);
        }
    }
}
