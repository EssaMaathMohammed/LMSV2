package LibraryProgram;

import LibraryProgram.databaseClasses.User;
import LibraryProgram.fxmlFiles.FxmlLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.time.LocalDate;
import java.util.Calendar;

public class LoginController  {
    public Label welcomeLabel;
    public Hyperlink link;
    public Label signInLabel;
    public TextField usernameField;
    public PasswordField passwordField;
    public BorderPane loginPage;
    private static  String DATABASE_URL = "jdbc:derby:SPULibDB";
    private static Connection connection ;
    private ObservableList<User> data;

    private PreparedStatement SELECT;
    private PreparedStatement TOTAL_ROWS;

    public static String typeOfMember; // new part
    public static String loginsFile = "logs/LoginFile.txt";
    public static String employeeFileName; // new part
    public static String password;

    public void initialize(){
        data = FXCollections.observableArrayList();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/spulibdb","root","");

            TOTAL_ROWS = connection.prepareStatement("SELECT COUNT(*) FROM USERS");
            SELECT = connection.prepareStatement("SELECT * FROM USERS ");
            ResultSet resultSet = SELECT.executeQuery();
            updateTable(resultSet);

        } catch (SQLException | ClassNotFoundException throwable) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("DATABASE ERROR");
            alert.setContentText(throwable.getMessage());
            alert.showAndWait();
        }
    }

    public void uniLinkClicked(ActionEvent actionEvent) {
        try {
            Desktop.getDesktop().browse(new URI("http://www.spu.edu.iq/ku/"));
        } catch (URISyntaxException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("PATH ERROR");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void LoginButtonPressed(ActionEvent actionEvent)  {
        boolean present = false;
        String username;

        Calendar cal = Calendar.getInstance();

        for (int count = 0 ; count < data.size() ; count ++) {

            username = usernameField.getText().toLowerCase();// make it because we need to use it a lot.
            password = passwordField.getText();
            if (username.equals(data.get(count).getUserName().toLowerCase())
                    && password.equals(data.get(count).getPassword())) {

                typeOfMember = data.get(count).getType(); // new part

                password = passwordField.getText();
                employeeFileName = username + ".txt"; // making the filename as the user
                // name so we store the login infos in it if it doesn't already exists

                try {
                    FileWriter fileWriter = new FileWriter(loginsFile,true);
                    fileWriter.append(username).append(" Logged In ").append(String.valueOf(LocalDate.now())).append(" At ")
                            .append(String.valueOf(cal.get(Calendar.HOUR))).append(":").append(String.valueOf(cal.get(Calendar.MINUTE)))
                            .append(":").append(String.valueOf(cal.get(Calendar.SECOND))).append("\n");

                    fileWriter.close();
                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("File Writing Error");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }


                FxmlLoader fxmlLoader = new FxmlLoader();
                Pane pane = fxmlLoader.getView("HomePage");
                loginPage.getChildren().setAll(pane);
                present = true;
            }
        }
        if(!present) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Wrong INFO");
            alert.setContentText("your username or password is incorrect");
            alert.showAndWait();
        }
    }

    public void updateTable(ResultSet resultSet){
        try {

            ResultSet totalRowsResultSet = TOTAL_ROWS.executeQuery();
            totalRowsResultSet.next();
            int totalRows = totalRowsResultSet.getInt(1);
            totalRowsResultSet.close();

            User[] users = new User[totalRows];
            int counter = 0;

            while (resultSet.next()) {
                users[counter] = new User(resultSet.getString("USERNAME"),
                        resultSet.getString("PASSWORD"),
                        resultSet.getInt("ID"),
                        resultSet.getString("TYPE"));

                data.add(users[counter]);
                counter++;
            }
        }catch (SQLException throwable) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("DataBase ERROR");
            alert.setContentText(throwable.getMessage());
            alert.showAndWait();
        }
    }
}
