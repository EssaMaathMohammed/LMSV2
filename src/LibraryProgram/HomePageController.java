package LibraryProgram;

import LibraryProgram.fxmlFiles.FxmlLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Optional;


public class HomePageController {

    public Button BookingPage;
    @FXML private Pane homePagePane;
    @FXML private Button booksPage;
    @FXML private Button borrowsPage;
    @FXML private Button membersPage;
    @FXML private Button logoutButton;
    private Button usersButton;

    FxmlLoader generalFxmlLoader = new FxmlLoader();

    public void initialize() {
        //make the member type cast to lower case
        String memberType = LoginController.typeOfMember.toLowerCase();
        if (memberType.equals("admin")) {
            usersButton = new Button("Users");
            homePagePane.getChildren().add(usersButton);
            usersButton.setPrefHeight(50);
            usersButton.setPrefWidth(140);
            usersButton.setTranslateX(15);
            usersButton.setTranslateY(80);
            usersButton.setStyle("-fx-text-fill: #ABEEFE;\n" +
                    "    -fx-font: 15pt \"Times New Roman\";\n" +
                    "    -fx-font-weight: bold;\n" +
                    "    -fx-background-color: #354F91;\n" +
                    "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");

            usersButton.setOnMouseEntered(mouseEvent -> {
                    usersButton.setStyle(
                            "    -fx-background-color: #ABEEFE;\n" +
                                    "    -fx-font: 15pt \"Times New Roman\";\n" +
                                    "    -fx-font-weight: bold;\n" +
                                    "    -fx-text-fill:#354F91;\n");});

            usersButton.setOnMouseExited(mouseEvent -> {
                usersButton.setStyle("    -fx-text-fill: #ABEEFE;\n" +
                        "    -fx-font: 15pt \"Times New Roman\";\n" +
                        "    -fx-font-weight: bold;\n" +
                        "    -fx-background-color: #354F91;\n" +
                        "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );"); });

            usersButton.setOnAction(this::pagesMouseClicked);
        }

    }

    public void pagesMouseClicked(ActionEvent  mouseEvent)  {

        if (mouseEvent.getSource().toString().equals(booksPage.toString())) {
            Pane pane = generalFxmlLoader.getView("BooksPage");
            Scene scene = new Scene(pane);

            LibLauncher.applicationStage.setScene(scene);
        }else if (mouseEvent.getSource().toString().equals(borrowsPage.toString())) {
            Pane pane = generalFxmlLoader.getView("BorrowsPage");
            Scene scene = new Scene(pane);
            LibLauncher.applicationStage.setScene(scene);
        }else if (mouseEvent.getSource().toString().equals(membersPage.toString())){
            Pane pane = generalFxmlLoader.getView("MembersPage");
            Scene scene = new Scene(pane);
            LibLauncher.applicationStage.setScene(scene);
        }else if (mouseEvent.getSource().toString().equals(BookingPage.toString())) {
            Pane pane = generalFxmlLoader.getView("bookingPage");
            Scene scene = new Scene(pane);
            LibLauncher.applicationStage.setScene(scene);
        }else if (mouseEvent.getSource().toString().equals(logoutButton.toString())) {
            Pane pane = generalFxmlLoader.getView("LoginForm");
            Scene scene = new Scene(pane);
            LibLauncher.applicationStage.setScene(scene);
        }else if (mouseEvent.getSource().toString().equals(usersButton.toString())){

            String password = LoginController.password;
            Dialog<ButtonType> dialog = new Dialog<>();

            dialog.initOwner(homePagePane.getScene().getWindow());
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/LibraryProgram/fxmlFiles/CheckUserPassword.fxml"));

            try {
                dialog.getDialogPane().setContent(fxmlLoader.load());
            } catch (IOException e) {
                displayAlert(Alert.AlertType.ERROR , "DIALOG ERROR" ,e.getMessage());
            }

            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

            dialog.setTitle("Checking");

            Optional<ButtonType> result = dialog.showAndWait();

            if(result.isPresent() && result.get() == ButtonType.OK) {
                CheckUserPasswordController dialogController = fxmlLoader.getController();

                if (dialogController.getPasswordTextField().equals(password)) {
                    Pane pane = generalFxmlLoader.getView("UsersTable");
                    Scene scene = new Scene(pane);
                    LibLauncher.applicationStage.setScene(scene);
                }else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Wrong Password");
                    alert.setHeaderText("The Password is incorrect");
                    alert.showAndWait();
                }
            }
        }

    }

    private void displayAlert(Alert.AlertType alertType ,String title , String  message){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
