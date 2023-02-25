package LibraryProgram;

import LibraryProgram.databaseClasses.User;
import LibraryProgram.fxmlFiles.FxmlLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.Calendar;
import java.util.Optional;

public class UsersTableController {
    public TableView<User> userTable;
    public TableColumn<User,Integer> id;
    public TableColumn<User,String> userName;
    public TableColumn<User,String> password;
    public TableColumn<User,String> type;
    public BorderPane usersBorderPane;
    public Button searchButton;
    public TextField searchUsername;

    ObservableList<User> userList;
    private PreparedStatement SELECT_USERS;

    private PreparedStatement SEARCH ;
    private PreparedStatement DELETE;
    private PreparedStatement INSERT;
    private PreparedStatement TOTAL_ROWS;

    String employeeLog = "logs\\" + LoginController.employeeFileName;
    Calendar cal = Calendar.getInstance();

    private Connection connection;
    public void initialize() {
        userList = FXCollections.observableArrayList();

        id.setCellValueFactory(new PropertyValueFactory<>("Id"));
        userName.setCellValueFactory(new PropertyValueFactory<>("UserName"));
        password.setCellValueFactory(new PropertyValueFactory<>("Password"));
        type.setCellValueFactory(new PropertyValueFactory<>("Type"));

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/spulibdb","root","");

            TOTAL_ROWS = connection.prepareStatement("SELECT COUNT(*) FROM USERS");
            DELETE = connection.prepareStatement("DELETE FROM USERS WHERE ID = ?");
            INSERT = connection.prepareStatement("INSERT INTO USERS (USERNAME, PASSWORD, TYPE,ID) VALUES " +
                    "(?,?,?,?)");
            SEARCH = connection.prepareStatement("SELECT * FROM USERS WHERE USERNAME LIKE ?");

            SELECT_USERS = connection.prepareStatement("SELECT * FROM USERS");
            ResultSet selectUserResultSet = SELECT_USERS.executeQuery();
            updateTable(selectUserResultSet);

        } catch (SQLException | ClassNotFoundException e) {
            displayAlert(Alert.AlertType.ERROR,"Database ERROR", e.getMessage());
        }

    }

    private void updateTable(ResultSet selectUserResultSet) {
        try {

            ResultSet totalRowsResultSet = TOTAL_ROWS.executeQuery();
            totalRowsResultSet.next();
            int totalRows = totalRowsResultSet.getInt(1);
            totalRowsResultSet.close();

            User[] users = new User[totalRows];
            int counter = 0;
            userList.clear();

                while (selectUserResultSet.next()) {
                    users[counter] = new User(selectUserResultSet.getString("USERNAME"),
                            selectUserResultSet.getString("PASSWORD"),
                            selectUserResultSet.getInt("ID"),
                            selectUserResultSet.getString("TYPE"));
                    userList.add(users[counter]);
                    counter++;
                }
                userTable.setItems(userList);
        } catch (SQLException e) {
            displayAlert(Alert.AlertType.ERROR,"Database ERROR", e.getMessage());
        }
    }

    public void navigation(ActionEvent actionEvent) {
        FxmlLoader fxmlLoader = new FxmlLoader();
        Pane pane = fxmlLoader.getView("HomePage");
        usersBorderPane.getChildren().setAll(pane);
    }

    public void deleteButtonPressed(ActionEvent actionEvent) {

        User user = userTable.getSelectionModel().getSelectedItem();


            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete ");
            alert.setHeaderText("Are you sure that you want to delete this Member?\nPress Ok to confirm");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (user == null) {
                    Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Select an item");
                    alert.setHeaderText("You need to select a Member first by \nclicking on it then pressing delete");
                    alert.showAndWait();
                } else {
                    try {
                        String id = String.valueOf(user.getId());
                        DELETE.setString(1, id);
                        DELETE.execute();
                        userTable.getItems().remove(userTable.getSelectionModel().getSelectedItem());
                        userTable.getSelectionModel().select(null);
                        try {
                            FileWriter fileWriter = new FileWriter(employeeLog,true);
                            fileWriter.append("Deleted user ").append("\"").append(user.getUserName()).append(" Type: ")
                                    .append(user.getType())
                                    .append("\"").append(" At ")
                                    .append(String.valueOf(cal.get(Calendar.HOUR))).append(":").append(String.valueOf(cal.get(Calendar.MINUTE)))
                                    .append(":").append(String.valueOf(cal.get(Calendar.SECOND))).append("\n");

                            fileWriter.close();
                        } catch (IOException e) {
                            Alert alert2 = new Alert(Alert.AlertType.ERROR);
                            alert2.setTitle("PATH ERROR");
                            alert2.setContentText(e.getMessage());
                            alert2.showAndWait();
                        }

                    } catch (SQLException e) {
                        displayAlert(Alert.AlertType.ERROR, "DATABASE ERROR", e.getMessage());
                    }
                }
            }

    }

    public void InsertButtonPressed(ActionEvent actionEvent) {
        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.initOwner(usersBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/LibraryProgram/fxmlFiles/InsertNewUser.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            displayAlert(Alert.AlertType.ERROR,"DIALOG ERROR", e.getMessage());
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        dialog.setTitle("Adding a new User: ");
        Optional<ButtonType> result = dialog.showAndWait();

        if(result.isPresent() && result.get() == ButtonType.OK) {
            InsertNewUserContoller dialogController = fxmlLoader.getController();
            if(dialogController.getUserNameTextField().isEmpty() ||
               dialogController.getTypeTextField().isEmpty()||
                dialogController.getPasswordTextField().isEmpty()) {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Enter All Entries");
                alert.setHeaderText("You must fill all the fields to add a new User");
                alert.showAndWait();
            }else {
                try {
                    INSERT.setString(1,dialogController.getUserNameTextField());
                    INSERT.setString(2,dialogController.getPasswordTextField());
                    INSERT.setString(3,dialogController.getTypeTextField());
                    INSERT.setInt(4,dialogController.getIdTextField());
                    INSERT.executeUpdate();

                    try {
                        FileWriter fileWriter = new FileWriter(employeeLog,true);
                        fileWriter.append("Inserted  new User ").append("\"").append(dialogController.getUserNameTextField()).append(" ")
                                .append("Type:").append(" ").append(dialogController.getTypeTextField())
                                .append("\"").append(" At ")
                                .append(String.valueOf(cal.get(Calendar.HOUR))).append(":").append(String.valueOf(cal.get(Calendar.MINUTE)))
                                .append(":").append(String.valueOf(cal.get(Calendar.SECOND))).append("\n");

                        fileWriter.close();
                    } catch (IOException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("PATH ERROR");
                        alert.setContentText(e.getMessage());
                        alert.showAndWait();
                    }

                    ResultSet resultSet = SELECT_USERS.executeQuery();
                    updateTable(resultSet);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Insert Information");
                    alert.setContentText("A new User has been inserted");
                    alert.showAndWait();

                } catch (IllegalArgumentException e){
                    displayAlert(Alert.AlertType.ERROR,"FORMAT ERROR","The date format you entered is not acceptable ");
                }
                catch (SQLException e) {
                    displayAlert(Alert.AlertType.ERROR,"DATABASE ERROR", e.getMessage());
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

    public void SearchButtonPressed(ActionEvent actionEvent) {
        try {
            String name = "%"+searchUsername.getText().trim()+"%";
            SEARCH.setString(1,name);
            ResultSet resultSet = SEARCH.executeQuery();
            updateTable(resultSet);
        }catch (SQLException e){
            displayAlert(Alert.AlertType.ERROR , "DATABASE ERROR" ,e.getMessage());
        }
    }
}
