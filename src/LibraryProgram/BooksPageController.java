package LibraryProgram;

import LibraryProgram.fxmlFiles.FxmlLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.*;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BooksPageController {
    public Button addButton;
    public GridPane sectionGridPane;
    private static int row = 0;
    private static int column = 1;


    public static String nameOfButton;

    public BorderPane booksPageBorderPane;
    public Button homePage;

    private PreparedStatement GET_SECTION;

    List<ButtonSections> buttons = new ArrayList<>();
    FxmlLoader fxmlLoader = new FxmlLoader();

    private Connection connection;

    public void initialize(){

        column = 2;
        row = 0;
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/spulibdb","root","");

            GET_SECTION = connection.prepareStatement("SELECT DISTINCT SECTION FROM BOOKS");
            Statement statement = connection.createStatement();
            ResultSet resultSet = GET_SECTION.executeQuery();
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

            for (int i = 0; i < 4; i++) { // creates a specific number of columns
                ColumnConstraints column = new ColumnConstraints();
                sectionGridPane.getColumnConstraints().add(column);
            }

            ButtonSections allBooksButton = new ButtonSections("All Books");
            sectionGridPane.add(allBooksButton,1,0);
            allBooksButton.setOnAction(actionEvent -> {
                try {
                    sectionButtonPressed(actionEvent);
                } catch (IOException e) {
                    displayAlert(Alert.AlertType.ERROR , "PATH ERROR" , e.getMessage());
                }
            });
            buttons.add(allBooksButton);

            while (resultSet.next()) {
                for (int counter =1; counter <= resultSetMetaData.getColumnCount(); counter++) {
                    // creates a button with the section name as the button content
                    ButtonSections buttonSection = new ButtonSections(resultSet.getObject(counter).toString());
                    // adds an action to the button
                    buttonSection.setOnAction(actionEvent -> {
                        try {
                            sectionButtonPressed(actionEvent);
                        } catch (IOException e) {
                            displayAlert(Alert.AlertType.ERROR , "PATH ERROR" , e.getMessage());
                        }
                    });
                    if (column == sectionGridPane.getColumnCount()) {
                        row++;
                        column = 0;
                    }
                    sectionGridPane.add(buttonSection,column++,row);
                    buttons.add(buttonSection);
                }
            }

        }catch (SQLException | ClassNotFoundException sqlException) {
            displayAlert(Alert.AlertType.ERROR , "DATABASE ERROR" , sqlException.getMessage());
        }
    }

    public void addButtonPressed(ActionEvent actionEvent) {

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.initOwner(sectionGridPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/LibraryProgram/fxmlFiles/AddSectionDialog.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            displayAlert(Alert.AlertType.ERROR , "DIALOG ERROR" , e.getMessage());
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        dialog.setTitle("Creating a new Section");
        Optional<ButtonType> result = dialog.showAndWait();


        if(result.isPresent() && result.get() == ButtonType.OK) {
            AddSectionDialogController dialogController = fxmlLoader.getController();
            if (dialogController.nameTextField.getText().isEmpty()) {
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Section Name");
                alert1.setHeaderText("You need to fill the field before adding a new section");
                alert1.showAndWait();
            }else {
                ButtonSections newButton = dialogController.newButton();
                newButton.setOnAction(actionEvent1 -> {
                    try {
                        sectionButtonPressed(actionEvent1);
                    } catch (IOException e) {
                        displayAlert(Alert.AlertType.ERROR , "PATH ERROR" , e.getMessage());
                    }
                });

                if (column == sectionGridPane.getColumnCount()) {
                    row++;
                    column = 0;
                }
                sectionGridPane.add(newButton,column++,row);
                buttons.add(newButton);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Insert Information");
                alert.setContentText("A new Section has been inserted");
                alert.showAndWait();
            }

        }

    }

    private void sectionButtonPressed(ActionEvent actionEvent1) throws IOException{

        for (ButtonSections button : buttons) {
            if (actionEvent1.getSource().toString().equals(button.toString())) {
                nameOfButton = button.getText();
                Pane pane = fxmlLoader.getView("BooksTable");
                booksPageBorderPane.getChildren().clear();
                booksPageBorderPane.setCenter(pane);
            }
        }
    }

    public void navigation(ActionEvent actionEvent) {

        Pane pane = fxmlLoader.getView("HomePage");
        booksPageBorderPane.getChildren().setAll(pane);
    }

    private void displayAlert(Alert.AlertType alertType ,String title , String  message){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
