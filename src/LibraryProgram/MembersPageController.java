package LibraryProgram;


import LibraryProgram.databaseClasses.Member;
import LibraryProgram.fxmlFiles.FxmlLoader;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Optional;
import java.util.ResourceBundle;

public class MembersPageController implements Initializable {

    public TextField searchFirstName;
    public TextField searchLastName;
    public Button findByName;
    public TextField searchDepartment;
    public Button insertButton;
    public Button delete;


    public BorderPane memberBorderPane;
    private static final String DATABASE_URL = "jdbc:derby:SPULibDB;";

    private  PreparedStatement SEARCH1 ;
    private  PreparedStatement SEARCH2 ;
    private  PreparedStatement SEARCH3 ;
    private  PreparedStatement SEARCH4 ;
    private  PreparedStatement SEARCH5 ;
    private  PreparedStatement SEARCH6 ;
    private  PreparedStatement SEARCH7 ;
    private  PreparedStatement DELETE;
    private  PreparedStatement INSERT;
    private  PreparedStatement SELECT_QUERY;
    private  PreparedStatement TOTAL_ROWS;
    private  PreparedStatement EDIT_MID ;
    private  PreparedStatement EDIT_F_NAME ;
    private  PreparedStatement EDIT_M_NAME ;
    private  PreparedStatement EDIT_L_NAME ;
    private  PreparedStatement EDIT_TYPE ;
    private  PreparedStatement EDIT_COLLEGE ;
    private  PreparedStatement EDIT_DEPARTMENT ;
    public Button homePage;
    public TableColumn<Member,Integer> memberTimesBorrowed;
    LibraryProgram.fxmlFiles.FxmlLoader fxmlLoader = new FxmlLoader();

    @FXML TableView<Member> tableView;
    ChangeListener<Member> listener;

    public TableColumn<Member,String> firstNameColumn;
    public TableColumn<Member,String> middleNameColumn;
    public TableColumn<Member,String> lastNameColumn;
    public TableColumn<Member,String> memberTypeColumn;
    public TableColumn<Member,String> collegeColumn;
    public TableColumn<Member,String> departmentColumn;
    public TableColumn<Member, Date> expireDateColumn;
    public TableColumn<Member,String> memberIdColumn;

    Connection connection;
    private static ObservableList<Member> data;
    String employeeLog = "logs\\" + LoginController.employeeFileName;
    Calendar cal = Calendar.getInstance();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        data = FXCollections.observableArrayList();
        memberIdColumn.setCellValueFactory(new PropertyValueFactory<>("m_id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("first_name"));
        middleNameColumn.setCellValueFactory(new PropertyValueFactory<>("middle_name"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("last_name"));
        memberTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        collegeColumn.setCellValueFactory(new PropertyValueFactory<>("college"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        expireDateColumn.setCellValueFactory(new PropertyValueFactory<>("exp_date"));
        memberTimesBorrowed.setCellValueFactory(new PropertyValueFactory<>("timesBorrowed"));

        tableView.setEditable(true);
        memberIdColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        firstNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        middleNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        memberTypeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        collegeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        departmentColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        listener = (observableValue, book, t1) -> {
            tableView.getSelectionModel().selectedItemProperty().removeListener(listener);
            tableView.getSelectionModel().select(null);
            tableView.getSelectionModel().selectedItemProperty().addListener(listener);
        };


        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/spulibdb","root","");

            TOTAL_ROWS = connection.prepareStatement("SELECT COUNT(*) FROM MEMBERS");

            SEARCH1 = connection.prepareStatement("SELECT * FROM MEMBERS WHERE FIRST_NAME =? AND MIDDLE_NAME = ?");
            SEARCH2 = connection.prepareStatement("SELECT * FROM MEMBERS WHERE FIRST_NAME = ? OR MIDDLE_NAME = ?");

            SEARCH3 = connection.prepareStatement("SELECT * FROM MEMBERS WHERE FIRST_NAME = ? AND DEPARTMENT = ?");
            SEARCH4 = connection.prepareStatement("SELECT * FROM MEMBERS WHERE FIRST_NAME = ? OR DEPARTMENT = ?");

            SEARCH5 = connection.prepareStatement("SELECT * FROM MEMBERS WHERE MIDDLE_NAME = ? AND DEPARTMENT = ?");
            SEARCH6 = connection.prepareStatement("SELECT * FROM MEMBERS WHERE MIDDLE_NAME = ? OR DEPARTMENT = ?");

            SEARCH7 = connection.prepareStatement("SELECT * FROM MEMBERS WHERE FIRST_NAME = ? AND MIDDLE_NAME = ? AND DEPARTMENT = ?");

            DELETE = connection.prepareStatement("DELETE FROM MEMBERS WHERE M_ID = ?");
            INSERT = connection.prepareStatement("INSERT INTO MEMBERS (M_ID, FIRST_NAME, MIDDLE_NAME, LAST_NAME, TYPE, COLLEGE, DEPARTMENT, EXP_DATE) VALUES " +
                    "(?,?,?,?,?,?,?,?)");

            EDIT_MID = connection.prepareStatement("UPDATE MEMBERS SET M_ID = ? WHERE M_ID =?");
            EDIT_F_NAME=  connection.prepareStatement("UPDATE MEMBERS SET FIRST_NAME = ? WHERE M_ID =?");
            EDIT_M_NAME = connection.prepareStatement("UPDATE MEMBERS SET MIDDLE_NAME = ? WHERE M_ID =?");
            EDIT_L_NAME = connection.prepareStatement("UPDATE MEMBERS SET LAST_NAME = ? WHERE M_ID =?");
            EDIT_TYPE = connection.prepareStatement("UPDATE MEMBERS SET TYPE = ? WHERE M_ID =?");
            EDIT_COLLEGE = connection.prepareStatement("UPDATE MEMBERS SET COLLEGE = ? WHERE M_ID =?");
            EDIT_DEPARTMENT = connection.prepareStatement("UPDATE MEMBERS SET DEPARTMENT = ? WHERE M_ID =?");

            SELECT_QUERY = connection.prepareStatement("SELECT * FROM MEMBERS");

            ResultSet resultSet = SELECT_QUERY.executeQuery();
            updateTable(resultSet);

        } catch (SQLException | ClassNotFoundException sqlException) {
           displayAlert(Alert.AlertType.ERROR,"DATABASE ERROR", sqlException.getMessage());
        }
    }

    public void searchByNameButtonPressed(ActionEvent actionEvent){
        String firstName = searchFirstName.getText().trim();
        String lastName = searchLastName.getText().trim();
        String department = searchDepartment.getText().trim();
        try {
            if (searchFirstName.getText().isEmpty()
                    && searchLastName.getText().isEmpty()
                    && searchDepartment.getText().isEmpty()) {
                ResultSet resultSet = SELECT_QUERY.executeQuery();
                updateTable(resultSet);
            } else {
                if (searchDepartment.getText().isEmpty()) {
                    if (!searchFirstName.getText().isEmpty() && !searchLastName.getText().isEmpty()) {
                        SEARCH1.setString(1, firstName);
                        SEARCH1.setString(2, lastName);
                        ResultSet resultSet = SEARCH1.executeQuery();
                        updateTable(resultSet);
                    } else {
                        SEARCH2.setString(1, firstName);
                        SEARCH2.setString(2, lastName);
                        ResultSet resultSet = SEARCH2.executeQuery();
                        updateTable(resultSet);
                    }
                } else if (searchLastName.getText().isEmpty()) {
                    if (!searchFirstName.getText().isEmpty() && !searchDepartment.getText().isEmpty()) {
                        SEARCH3.setString(1, firstName);
                        SEARCH3.setString(2, department);
                        ResultSet resultSet = SEARCH3.executeQuery();
                        updateTable(resultSet);

                    } else {
                        SEARCH4.setString(1, firstName);
                        SEARCH4.setString(2, department);
                        ResultSet resultSet = SEARCH4.executeQuery();
                        updateTable(resultSet);

                    }
                } else if (searchFirstName.getText().isEmpty()) {
                    if (!searchLastName.getText().isEmpty() && !searchDepartment.getText().isEmpty()) {
                        SEARCH5.setString(1, lastName);
                        SEARCH5.setString(2, department);
                        ResultSet resultSet = SEARCH5.executeQuery();
                        updateTable(resultSet);

                    } else {
                        SEARCH6.setString(1, lastName);
                        SEARCH6.setString(2, department);
                        ResultSet resultSet = SEARCH6.executeQuery();
                        updateTable(resultSet);
                    }
                } else {
                    SEARCH7.setString(1, firstName);
                    SEARCH7.setString(2, lastName);
                    SEARCH7.setString(3, department);
                    ResultSet resultSet = SEARCH7.executeQuery();
                    updateTable(resultSet);
                }
            }
        } catch (SQLException e) {
            displayAlert(Alert.AlertType.ERROR,"DATABASE ERROR", e.getMessage());
        }
    }

    public void insertButtonPressed(ActionEvent actionEvent){

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.initOwner(memberBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/LibraryProgram/fxmlFiles/MemberInsertDialog.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            displayAlert(Alert.AlertType.ERROR,"DIALOG ERROR", e.getMessage());
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        dialog.setTitle("Adding a new member: ");
        Optional<ButtonType> result = dialog.showAndWait();

        if(result.isPresent() && result.get() == ButtonType.OK) {
            MemberInsertDialogController dialogController = fxmlLoader.getController();
            if(dialogController.firstName.getText().isEmpty()||
                    dialogController.lastName.getText().isEmpty()||
                    dialogController.MiddleName.getText().isEmpty() ||
                    dialogController.type.getText().isEmpty() ||
                    dialogController.college.getText().isEmpty()||
                    dialogController.department.getText().isEmpty()||
                    dialogController.year.getText().isEmpty() ||
                    dialogController.month.getText().isEmpty()||
                    dialogController.day.getText().isEmpty()||
                    dialogController.m_id.getText().isEmpty()) {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Enter All Entries");
                alert.setHeaderText("You must fill all the fields to add a new member");
                alert.showAndWait();
            }else {
                try {
                    String dateString = dialogController.getYear()+"-"+dialogController.getMonth()+"-"+dialogController.getDay();
                    Date date = Date.valueOf(dateString);

                    INSERT.setString(1,dialogController.getM_id());
                    INSERT.setString(2,dialogController.getFirstName());
                    INSERT.setString(3,dialogController.getMiddleName());
                    INSERT.setString(4,dialogController.getLastName());
                    INSERT.setString(5,dialogController.getType());
                    INSERT.setString(6,dialogController.getCollege());
                    INSERT.setString(7,dialogController.getDepartment());
                    INSERT.setDate(8,date);
                    INSERT.executeUpdate();

                    try {
                        FileWriter fileWriter = new FileWriter(employeeLog,true);
                        fileWriter.append("Inserted ").append("\"").append(dialogController.getFirstName()).append(" ")
                                .append(dialogController.getMiddleName()).append(" ").append(dialogController.getLastName())
                                .append("\"").append(" From ").append(dialogController.getDepartment()).append(" At ")
                                .append(String.valueOf(cal.get(Calendar.HOUR))).append(":").append(String.valueOf(cal.get(Calendar.MINUTE)))
                                .append(":").append(String.valueOf(cal.get(Calendar.SECOND))).append("\n");

                        fileWriter.close();
                    } catch (IOException e) {
                        displayAlert(Alert.AlertType.ERROR,"File Writing Error", e.getMessage());
                    }

                    ResultSet resultSet = SELECT_QUERY.executeQuery();
                    updateTable(resultSet);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Insert Information");
                    alert.setContentText("A new Member has been inserted");
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

    public void deleteButtonPressed(ActionEvent actionEvent){

        Member member = tableView.getSelectionModel().getSelectedItem();
        String memberType = LoginController.typeOfMember.toLowerCase();

        if (memberType.equals("admin")) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete ");
            alert.setHeaderText("Are you sure that you want to delete this Member?\nPress Ok to confirm");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (member == null) {
                    Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Select an item");
                    alert.setHeaderText("You need to select a Member first by \nclicking on it then pressing delete");
                    alert.showAndWait();
                } else {
                    try {
                        String id = member.getM_id();
                        DELETE.setString(1, id);
                        DELETE.execute();
                        tableView.getItems().remove(tableView.getSelectionModel().getSelectedItem());
                        tableView.getSelectionModel().select(null);

                        try {
                            FileWriter fileWriter = new FileWriter(employeeLog,true);
                            fileWriter.append("Deleted ").append("\"").append(member.getFirst_name()).append(" ")
                                    .append(member.getMiddle_name()).append(" ").append(member.getLast_name())
                                    .append("\"").append(" From ").append(member.getDepartment()).append(" At ")
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
        }else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Type Of User ");
            alert.setHeaderText("Only the Administrator can use this feature" );
            alert.showAndWait();
        }
    }

    public void updateTable(ResultSet resultSet) {


        try {
            long mills = System.currentTimeMillis();
            Date currentDate = new Date(mills);
            data.clear();

            ResultSet totalRowsResultSet = TOTAL_ROWS.executeQuery();
            totalRowsResultSet.next();
            int totalRows = totalRowsResultSet.getInt(1);
            totalRowsResultSet.close();

            Member[] members = new Member[totalRows];
            int counter = 0;

            while (resultSet.next()) {
                members[counter] = new Member(resultSet.getString(1)
                        ,resultSet.getString(2)
                        ,resultSet.getString(3)
                        ,resultSet.getString(4)
                        ,resultSet.getString(5)
                        ,resultSet.getString(6)
                        ,resultSet.getString(7)
                        ,resultSet.getDate(8)
                        ,resultSet.getInt(9));

                data.add(members[counter]);
                counter++;
            }
            tableView.setRowFactory(new Callback<TableView<Member>, TableRow<Member>>() {
                @Override
                public TableRow<Member> call(TableView<Member> borrowTableView) {
                    return new TableRow<>() {
                        @Override
                        protected void updateItem(Member member, boolean b) {
                            super.updateItem(member, b);

                            if (!b) {
                                if (member.getExp_date().compareTo(currentDate) < 0) {
                                    setStyle("-fx-background-color: rgba(255,124,124,0.5)");
                                }
                            }else{
                                setStyle(null);
                            }
                        }
                    };
                }
            });
            tableView.setOnSort(
                    new EventHandler<SortEvent<TableView<Member>>>() {
                        @Override
                        public void handle(SortEvent<TableView<Member>> tableViewSortEvent) {
                            tableView.setRowFactory(new Callback<TableView<Member>, TableRow<Member>>() {
                                @Override
                                public TableRow<Member> call(TableView<Member> borrowTableView) {
                                    return new TableRow<>() {
                                        @Override
                                        protected void updateItem(Member member, boolean b) {
                                            super.updateItem(member, b);

                                            if (!b) {
                                                if (member.getExp_date().compareTo(currentDate) < 0) {
                                                    setStyle("-fx-background-color: rgba(255,124,124,0.5)");
                                                }
                                            }else{
                                                setStyle(null);
                                            }
                                        }
                                    };
                                }
                            });
                        }
                    }
            );
            tableView.setItems(data);
        } catch (SQLException e) {
            displayAlert(Alert.AlertType.ERROR,"DATABASE ERROR", e.getMessage());
        }

    }

    public void editCell(TableColumn.CellEditEvent<Member, String> memberStringCellEditEvent) {

        Member member = tableView.getSelectionModel().getSelectedItem();
        String oldMID = member.getM_id();

            if(memberStringCellEditEvent.getTableColumn().equals(firstNameColumn)){
                member.setFirst_name(memberStringCellEditEvent.getNewValue());
                editTableRow(EDIT_F_NAME,oldMID,member);
            }else if(memberStringCellEditEvent.getTableColumn().equals(middleNameColumn)){
                member.setMiddle_name(memberStringCellEditEvent.getNewValue());
                editTableRow(EDIT_M_NAME,oldMID,member);
            }else if(memberStringCellEditEvent.getTableColumn().equals(lastNameColumn)){
                member.setLast_name(memberStringCellEditEvent.getNewValue());
                editTableRow(EDIT_L_NAME,oldMID,member);
            }else if(memberStringCellEditEvent.getTableColumn().equals(memberTypeColumn)){
                member.setType(memberStringCellEditEvent.getNewValue());
                editTableRow(EDIT_TYPE,oldMID,member);
            }else if(memberStringCellEditEvent.getTableColumn().equals(collegeColumn)){
                member.setCollege(memberStringCellEditEvent.getNewValue());
                editTableRow(EDIT_COLLEGE,oldMID,member);
            }else if(memberStringCellEditEvent.getTableColumn().equals(departmentColumn)){
                member.setDepartment(memberStringCellEditEvent.getNewValue());
                editTableRow(EDIT_DEPARTMENT,oldMID,member);
            }else if(memberStringCellEditEvent.getTableColumn().equals(memberIdColumn)){
                member.setM_id(memberStringCellEditEvent.getNewValue());
                editTableRow(EDIT_MID,oldMID,member);
            }
    }

    public void editTableRow(PreparedStatement EditType,String oldMId,Member member){
        try{
            if(EditType.equals(EDIT_MID)){
                EDIT_MID.setString(1,member.getM_id());
                EDIT_MID.setString(2,oldMId);
                EditType = EDIT_MID;
            }else if(EditType.equals(EDIT_F_NAME)){
                EDIT_F_NAME.setString(1,member.getFirst_name());
                EDIT_F_NAME.setString(2,oldMId);
                EditType = EDIT_F_NAME;
            }else if(EditType.equals(EDIT_M_NAME)){
                EDIT_M_NAME.setString(1,member.getMiddle_name());
                EDIT_M_NAME.setString(2,oldMId);
                EditType = EDIT_M_NAME;
            }else if(EditType.equals(EDIT_L_NAME)){
                EDIT_L_NAME.setString(1,member.getLast_name());
                EDIT_L_NAME.setString(2,oldMId);
                EditType = EDIT_L_NAME;
            }else if(EditType.equals(EDIT_TYPE)){
                EDIT_TYPE.setString(1,member.getType());
                EDIT_TYPE.setString(2,oldMId);
                EditType = EDIT_TYPE;
            }else if(EditType.equals(EDIT_DEPARTMENT)){
                EDIT_DEPARTMENT.setString(1,member.getDepartment());
                EDIT_DEPARTMENT.setString(2,oldMId);
                EditType = EDIT_DEPARTMENT;
            }else if(EditType.equals(EDIT_COLLEGE)){
                EDIT_COLLEGE.setString(1,member.getCollege());
                EDIT_COLLEGE.setString(2,oldMId);
                EditType = EDIT_COLLEGE;
            }
            EditType.executeUpdate();
            ResultSet resultSet =SELECT_QUERY.executeQuery();
            updateTable(resultSet);
        } catch (SQLException e) {
            displayAlert(Alert.AlertType.ERROR,"DATABASE ERROR", e.getMessage());
        }
    }

    public void navigation(ActionEvent actionEvent) {
            Pane pane = fxmlLoader.getView("HomePage");
            memberBorderPane.getChildren().setAll(pane);
    }

    private void displayAlert(Alert.AlertType alertType ,String title , String  message){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
