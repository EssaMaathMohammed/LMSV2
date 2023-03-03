package LibraryProgram;

import LibraryProgram.databaseClasses.Borrow;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import LibraryProgram.fxmlFiles.FxmlLoader;
import javafx.util.Callback;


import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.Calendar;
import java.util.Optional;

public class BorrowsPageController {


    @FXML private BorderPane borrowBorderPane;
    @FXML private TableView <Borrow>BorrowedTableView;
    @FXML private TableColumn<Borrow,String> mId;
    @FXML private TableColumn<Borrow, Date> borrowDate;
    @FXML private TableColumn<Borrow,Integer> quantity;
    @FXML private TableColumn<Borrow, Date> dueDate;
    @FXML private TableColumn<Borrow,Long> isbnId;
    @FXML private TableColumn<Borrow,String> note;


    @FXML private TableView <Borrow> ReturnedTableView;
    @FXML private TableColumn<Borrow,String> mIdR;
    @FXML private TableColumn<Borrow, Date> borrowDateR;
    @FXML private TableColumn<Borrow, Date>  dueDateR;
    @FXML private TableColumn<Borrow, Date>  returnDateR;
    @FXML private TableColumn<Borrow,Integer> quantityR;
    @FXML private TableColumn<Borrow,String> isbnIdR;

    @FXML private TableColumn<Borrow,String> firstName;
    @FXML private TableColumn<Borrow,String> lastName;
    @FXML private TableColumn<Borrow,String> firstNameR;
    @FXML private TableColumn<Borrow,String> lastNameR;

    public Button searchOne;
    public Button searchTwo;
    public TextField searchTextField;
    public TextField search2TextField;
    public TableColumn<Borrow,String> bookName;
    public TableColumn<Borrow,String> bookNameR;
    public Button homePage;
    public Button searchBetween;

    private ObservableList<Borrow> data;
    private ObservableList<Borrow> data2;

    private PreparedStatement SEARCH ;
    private PreparedStatement SEARCH2;
    private PreparedStatement DELETE ;
    private PreparedStatement INSERT ;
    private PreparedStatement SELECT_QUERY;
    private PreparedStatement SELECT_QUERY2;
    private PreparedStatement EditTable1;
    private PreparedStatement EditTime;
    private PreparedStatement SELECT_BOOK_QUANTITY;
    private PreparedStatement UPDATE_BOOK_AMOUNT;
    private PreparedStatement SEARCH_BETWEEN;
    private PreparedStatement GET_TIMES_BORROWED;
    private PreparedStatement UPDATE_TIMES_BORROWED;
    private PreparedStatement GET_MEMBER_TIMES_BORROWED;
    private PreparedStatement UPDATE_MEMBER_TIMES_BORROWED;
    private PreparedStatement SELECT_MEMBER_TIMES_BORROWED;
    private PreparedStatement SELECT_BOOK_NAME;
    private PreparedStatement SELECT_NAME;
    private PreparedStatement TOTAL_BOOKS_BORROWED_BY;
    private PreparedStatement TOTAL_ROWS;
    private PreparedStatement CURRENTLY_IN_BOOKING;

    ChangeListener<Borrow> listenerBorrow;
    ChangeListener<Borrow> listenerReturn;
    FxmlLoader fxmlLoader = new FxmlLoader();
    private final int maxQuantity = 2;
    String employeeLog = "logs\\" + LoginController.employeeFileName;
    Calendar cal = Calendar.getInstance();
    private Connection connection;

    public void initialize(){
        borrowBorderPane.getCenter().maxHeight(500);

        data = FXCollections.observableArrayList();
        data2 = FXCollections.observableArrayList();

        firstNameR.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameR.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        bookNameR.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        mIdR.setCellValueFactory(new PropertyValueFactory<>("m_id"));
        borrowDateR.setCellValueFactory(new PropertyValueFactory<>("BorrowDate"));
        returnDateR.setCellValueFactory(new PropertyValueFactory<>("ReturnDate"));
        dueDateR.setCellValueFactory(new PropertyValueFactory<>("DueDate"));
        quantityR.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
        isbnIdR.setCellValueFactory(new PropertyValueFactory<>("Isbn"));


        firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastName.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        bookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        mId.setCellValueFactory(new PropertyValueFactory<>("m_id"));
        borrowDate.setCellValueFactory(new PropertyValueFactory<>("BorrowDate"));
        note.setCellValueFactory(new PropertyValueFactory<>("Note"));
        dueDate.setCellValueFactory(new PropertyValueFactory<>("DueDate"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
        isbnId.setCellValueFactory(new PropertyValueFactory<>("Isbn"));


        BorrowedTableView.setEditable(true);
        note.setCellFactory(TextFieldTableCell.forTableColumn());

        listenerBorrow = (observableValue, o, t1) -> { selectedTable(BorrowedTableView); };
        listenerReturn = (observableValue, o, t1) -> { selectedTable(ReturnedTableView); };

        BorrowedTableView.getSelectionModel().selectedItemProperty().addListener(listenerBorrow);
        ReturnedTableView.getSelectionModel().selectedItemProperty().addListener(listenerReturn);



        try{

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/spulibdb","root","");

            TOTAL_ROWS = connection.prepareStatement("SELECT COUNT(*) FROM BORROW");

            SELECT_BOOK_QUANTITY = connection.prepareStatement("SELECT QUANTITY FROM BOOKS WHERE ISBN = ?");
            UPDATE_BOOK_AMOUNT = connection.prepareStatement("UPDATE BOOKS SET QUANTITY = ? WHERE ISBN = ?");
            SELECT_BOOK_NAME = connection.prepareStatement("SELECT NAME FROM BOOKS WHERE ISBN = ?");
            TOTAL_BOOKS_BORROWED_BY = connection.prepareStatement("SELECT SUM(QUANTITY) FROM BORROW WHERE M_ID = ? AND RETURN_DATE IS NULL");

            GET_TIMES_BORROWED = connection.prepareStatement("SELECT TIMESBORROWED FROM BOOKS WHERE ISBN = ?");
            UPDATE_TIMES_BORROWED = connection.prepareStatement("UPDATE BOOKS SET TIMESBORROWED = ? WHERE ISBN = ?");

            GET_MEMBER_TIMES_BORROWED = connection.prepareStatement("SELECT TIMESBORROWED FROM MEMBERS WHERE M_ID = ?");
            UPDATE_MEMBER_TIMES_BORROWED = connection.prepareStatement("UPDATE MEMBERS SET TIMESBORROWED = ? WHERE M_ID = ?");
            SELECT_MEMBER_TIMES_BORROWED = connection.prepareStatement("SELECT TIMESBORROWED FROM MEMBERS WHERE M_ID = ?");

            //SEARCH is for the BORROWED TABLE WHICH IS THE FIRST TABLE
            SEARCH = connection.prepareStatement("SELECT FIRST_NAME,MIDDLE_NAME ,NAME,BORROW.M_ID, BORROW_DATE, DUE_DATE, BORROW.QUANTITY, BORROW.ISBN,NOTE FROM BORROW " +
                    "JOIN BOOKS B ON B.ISBN = BORROW.ISBN " +
                    "JOIN MEMBERS M on M.M_ID = BORROW.M_ID" +
                    " WHERE RETURN_DATE IS NULL AND M.M_ID = ?");

            //SEARCH2 is for the RETURNED TABLE WHICH IS THE SECOND TABLE
            SEARCH2 = connection.prepareStatement("SELECT FIRST_NAME,MIDDLE_NAME ,NAME,BORROW.M_ID, BORROW_DATE, DUE_DATE,RETURN_DATE, BORROW.QUANTITY, BORROW.ISBN FROM BORROW " +
                    "JOIN BOOKS B ON B.ISBN = BORROW.ISBN " +
                    "JOIN MEMBERS M on M.M_ID = BORROW.M_ID" +
                    " WHERE RETURN_DATE IS NOT NULL AND M.M_ID = ?");

            SELECT_NAME = connection.prepareStatement("SELECT FIRST_NAME , MIDDLE_NAME FROM MEMBERS Join BORROW B on MEMBERS.M_ID = B.M_ID WHERE ISBN = ? AND B.M_ID = ?");

            CURRENTLY_IN_BOOKING = connection.prepareStatement("SELECT ISBN ,BOOKING_DATE , DUE_BOOKING_DATE FROM booking WHERE ISBN = ?");

            DELETE = connection.prepareStatement("DELETE FROM BORROW WHERE ISBN = ? AND M_ID = ? AND BORROW_DATE = ? AND RETURN_DATE IS NOT NULL ");
            INSERT = connection.prepareStatement("INSERT INTO BORROW (M_ID, BORROW_DATE, DUE_DATE, RETURN_DATE, QUANTITY, ISBN) " +
                    "VALUES (?,?,?,?,?,?)" );
//            FIND_ITEM = connection.prepareStatement("");

            EditTable1 = connection.prepareStatement("UPDATE BORROW SET " +
                    " NOTE = ?,M_ID=? ,DUE_DATE=?,BORROW_DATE=?,QUANTITY=? " +
                    "WHERE M_ID = ?");
            EditTime = connection.prepareStatement("UPDATE BORROW SET RETURN_DATE = ? WHERE M_ID = ? AND ISBN = ? AND BORROW_DATE = ?");

            SEARCH_BETWEEN = connection.prepareStatement("SELECT FIRST_NAME,MIDDLE_NAME ,NAME,BORROW.M_ID, BORROW_DATE, DUE_DATE,RETURN_DATE, BORROW.QUANTITY, BORROW.ISBN FROM BORROW " +
                    "JOIN BOOKS B ON B.ISBN = BORROW.ISBN " +
                    "JOIN MEMBERS M on M.M_ID = BORROW.M_ID" +
                    " WHERE BORROW_DATE BETWEEN ? AND ?");

            //SELECT_QUERY2 is for BORROWED TABLE WHICH IS THE FIRST TABLE
            SELECT_QUERY2 =  connection.prepareStatement("SELECT FIRST_NAME,MIDDLE_NAME ,NAME,BORROW.M_ID, BORROW_DATE, DUE_DATE, BORROW.QUANTITY, BORROW.ISBN,NOTE FROM BORROW " +
                    "JOIN BOOKS B ON B.ISBN = BORROW.ISBN " +
                    "JOIN MEMBERS M on M.M_ID = BORROW.M_ID" +
                    " WHERE RETURN_DATE IS NULL");
            ResultSet resultSet2 = SELECT_QUERY2.executeQuery();
            updateTable(resultSet2,BorrowedTableView);
            resultSet2.close();
            //SELECT_QUERY one is for RETURNED TABLE WHICH IS THE SECOND TABLE
            SELECT_QUERY =  connection.prepareStatement("SELECT FIRST_NAME,MIDDLE_NAME ,NAME,BORROW.M_ID, BORROW_DATE, DUE_DATE,RETURN_DATE, BORROW.QUANTITY, BORROW.ISBN FROM BORROW " +
                    "JOIN BOOKS B ON B.ISBN = BORROW.ISBN " +
                    "JOIN MEMBERS M on M.M_ID = BORROW.M_ID" +
                    " WHERE RETURN_DATE IS NOT NULL ");
            ResultSet resultSet = SELECT_QUERY.executeQuery();
            updateTable(resultSet,ReturnedTableView);
            resultSet.close();

        } catch (SQLException | ClassNotFoundException exception) {
           displayAlert(Alert.AlertType.ERROR,"DATABASE ERROR",exception.getMessage());
        }
    }

    private void selectedTable(TableView<Borrow> selectedTable) {
        if (selectedTable.equals(BorrowedTableView)) {
            ReturnedTableView.getSelectionModel().selectedItemProperty().removeListener(listenerReturn);
            ReturnedTableView.getSelectionModel().select(null);
            ReturnedTableView.getSelectionModel().selectedItemProperty().addListener(listenerReturn);
        }else if (selectedTable.equals(ReturnedTableView)) {
            BorrowedTableView.getSelectionModel().selectedItemProperty().removeListener(listenerBorrow);
            BorrowedTableView.getSelectionModel().select(null);
            BorrowedTableView.getSelectionModel().selectedItemProperty().addListener(listenerBorrow);
        }
    }

    public void insertButtonPressed(ActionEvent actionEvent){
        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.initOwner(borrowBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/LibraryProgram/fxmlFiles/BorrowInsertDialog.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            displayAlert(Alert.AlertType.ERROR,"DIALOG ERROR",e.getMessage());
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        dialog.setTitle("Add Borrow");
        Optional<ButtonType> result = dialog.showAndWait();


        if(result.isPresent() && result.get() == ButtonType.OK) {
            BorrowInsertDialogController dialogController = fxmlLoader.getController();

            int fieldsFilled = 0;
            fieldsFilled += dialogController.returnDateDay.getText().isEmpty() ? 0 : 1;
            fieldsFilled += dialogController.returnDateMonth.getText().isEmpty() ? 0 : 1;
            fieldsFilled += dialogController.returnDateYear.getText().isEmpty() ? 0 : 1;


            if(dialogController.memberIdTextField.getText().isEmpty() ||
                    dialogController.borrowDateDay.getText().isEmpty() ||
                    dialogController.borrowDateMonth.getText().isEmpty() ||
                    dialogController.borrowDateYear.getText().isEmpty() ||
                    dialogController.dueDateDay.getText().isEmpty() ||
                    dialogController.dueDateMonth.getText().isEmpty() ||
                    dialogController.dueDateYear.getText().isEmpty() ||
                    dialogController.quantityTextField.getText().isEmpty() ||
                    dialogController.isbnTextField.getText().isEmpty() ||
                    fieldsFilled == 2 || fieldsFilled == 1) {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Enter All Entries");
                alert.setHeaderText("You must fill all the fields to add a new borrow field");
                alert.showAndWait();
            }else {

                try {

                    String borrowString = dialogController.getBorrowDateYear() + "-"
                            + dialogController.getBorrowDateMonth() + "-"
                            + dialogController.getBorrowDateDay();
                    Date borrowDate = Date.valueOf(borrowString);

                    String dueString = dialogController.getDueDateYear() + "-"
                            + dialogController.getDueDateMonth() + "-"
                            + dialogController.getDueDateDay();
                    Date dueDate = Date.valueOf(dueString);

                    //if book amount is one
                    SELECT_BOOK_QUANTITY.setLong(1, Long.parseLong(dialogController.getIsbnString()));
                    ResultSet quantityBook = SELECT_BOOK_QUANTITY.executeQuery();
                    quantityBook.next();
                    int totalQuantity = quantityBook.getInt("QUANTITY");
                    quantityBook.close();

                    CURRENTLY_IN_BOOKING.setLong(1, Long.parseLong(dialogController.getIsbnString()));
                    ResultSet available = CURRENTLY_IN_BOOKING.executeQuery();

                    boolean passed = true;
                    // checker if the remaining books is 1 and that book is being booked in the future
                    if (totalQuantity == 1 && available.next() && fieldsFilled != 3) {
                        if (dueDate.after(available.getDate("BOOKING_DATE"))) {
                            displayAlert(Alert.AlertType.INFORMATION, "Date Error1", "The date you inputted should be before the booking date of\nthis book check the booking page for more info");
                            passed = false;
                        } else if (borrowDate.after(available.getDate("BOOKING_DATE")) && borrowDate.before(available.getDate("DUE_BOOKING_DATE"))) {
                            displayAlert(Alert.AlertType.INFORMATION, "Date Error2", "This book is currently in the timezone of\nbeing booked in the booking page check the booking page for more info");
                            passed = false;
                        }
                    }


                    if (passed) {
                        Date returnDate;
                        if (fieldsFilled == 0) {
                            returnDate = null;
                        }else {
                            String returnString = dialogController.getReturnDateYear() + "-"
                                    + dialogController.getReturnDateMonth() + "-"
                                    + dialogController.getReturnDateDay();
                            returnDate = Date.valueOf(returnString);
                        }


                        if (dueDate.compareTo(borrowDate) < 0) {
                            displayAlert(Alert.AlertType.INFORMATION, "Date Insert Error",
                                    "The Due Date is before the Borrow Date");
                        } else {


                            INSERT.setString(1, dialogController.getMemberIdString());
                            INSERT.setDate(2, borrowDate);
                            INSERT.setDate(3, dueDate);
                            INSERT.setDate(4, returnDate);
                            INSERT.setInt(5, Integer.parseInt(dialogController.getQuantityString()));
                            INSERT.setLong(6, Long.parseLong(dialogController.getIsbnString()));


                            // here was the code for the quantity of the book
                            int currentQuantity = Integer.parseInt(dialogController.getQuantityString());
                            SELECT_MEMBER_TIMES_BORROWED.setString(1, dialogController.getMemberIdString());

                            ResultSet memberTimes = SELECT_MEMBER_TIMES_BORROWED.executeQuery();
                            memberTimes.next();
                            int totalMemberBorrowed = memberTimes.getInt("TIMESBORROWED");
                            memberTimes.close();
                            GET_TIMES_BORROWED.setLong(1, Long.parseLong(dialogController.getIsbnString()));
                            ResultSet timesBorrowedResultSet = GET_TIMES_BORROWED.executeQuery();
                            timesBorrowedResultSet.next();


                            //???????????????????
//                            GET_MEMBER_TIMES_BORROWED.setString(1, dialogController.getMemberIdString());
//                            ResultSet memberTimesBorrowed = GET_MEMBER_TIMES_BORROWED.executeQuery();
//                            memberTimesBorrowed.next();
//                            memberTimesBorrowed.close();


                            if (currentQuantity > maxQuantity || currentQuantity < 0) { // the number is not safe
                                displayAlert(Alert.AlertType.INFORMATION, "Books Quantity Error",
                                        "Please insert less than " + maxQuantity + " Books\nor more than 0 books for each member at a current time");
                            }else { // the number is safe
                                TOTAL_BOOKS_BORROWED_BY.setString(1,dialogController.getMemberIdString()); // brings quantity times this member has borrowed
                                ResultSet totalBooksResultSet = TOTAL_BOOKS_BORROWED_BY.executeQuery();
                                totalBooksResultSet.next();
                                int totalBooks = totalBooksResultSet.getInt(1);
                                totalBooksResultSet.close();
                                // if they changed the amount we need to manually update this ***************************************************************************
                                if (totalBooks >= 2) { // if the total quantity is equal to two
                                    displayAlert(Alert.AlertType.INFORMATION, "Books Quantity Error",
                                            "The member reached his/her borrow limit");
                                }else if (totalBooks == 1 && currentQuantity > 1) {
                                    displayAlert(Alert.AlertType.INFORMATION, "Books Quantity Error",
                                            "The member can borrow only one more book to reach \nhis/her limit");
                                } else { // this is the second else

                                    if (fieldsFilled == 0) { // in case of not filling the return date ?
                                        if (totalQuantity - currentQuantity < 0 || currentQuantity == 0 || totalQuantity == 0) {
                                            displayAlert(Alert.AlertType.INFORMATION, "Books Quantity Error",
                                                    "The number of books left in the library is\nless than the required amount");
                                        } else {
                                            UPDATE_BOOK_AMOUNT.setInt(1, totalQuantity - currentQuantity);
                                            UPDATE_BOOK_AMOUNT.setLong(2, Long.parseLong(dialogController.getIsbnString()));
                                            UPDATE_BOOK_AMOUNT.executeUpdate();

                                            UPDATE_TIMES_BORROWED.setInt(1, currentQuantity + timesBorrowedResultSet.getInt("TIMESBORROWED"));
                                            UPDATE_TIMES_BORROWED.setLong(2, Long.parseLong(dialogController.getIsbnString()));
                                            UPDATE_TIMES_BORROWED.executeUpdate();

                                            UPDATE_MEMBER_TIMES_BORROWED.setInt(1, 1 + totalMemberBorrowed);
                                            UPDATE_MEMBER_TIMES_BORROWED.setString(2, dialogController.getMemberIdString());
                                            UPDATE_MEMBER_TIMES_BORROWED.executeUpdate();

                                            INSERT.executeUpdate();
                                            ResultSet resultSet = SELECT_QUERY.executeQuery();
                                            updateTable(resultSet, ReturnedTableView);
                                            resultSet.close();
                                            ResultSet resultSet2 = SELECT_QUERY2.executeQuery();
                                            updateTable(resultSet2, BorrowedTableView);
                                            resultSet2.close();
                                            try {

                                                SELECT_BOOK_NAME.setLong(1, Long.parseLong(dialogController.getIsbnString()));
                                                ResultSet resultSet3 = SELECT_BOOK_NAME.executeQuery();
                                                resultSet3.next();

                                                SELECT_NAME.setLong(1, Long.parseLong(dialogController.getIsbnString()));
                                                SELECT_NAME.setString(2, dialogController.getMemberIdString());
                                                ResultSet resultSet4 = SELECT_NAME.executeQuery();
                                                resultSet4.next();


                                                FileWriter fileWriter = new FileWriter(employeeLog, true);
                                                fileWriter.append("Inserted ").append("\"").append(resultSet4.getString(1)).append(" ").append(resultSet4.getString(2)).append("\"").append(" ")
                                                        .append(" That Borrowed ").append("\"").append(resultSet3.getString(1)).append("\"").append(" At ")
                                                        .append(String.valueOf(cal.get(Calendar.HOUR))).append(":").append(String.valueOf(cal.get(Calendar.MINUTE)))
                                                        .append(":").append(String.valueOf(cal.get(Calendar.SECOND))).append("\n");
                                                resultSet3.close();
                                                resultSet4.close();
                                                fileWriter.close();

                                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                                alert.setTitle("Insert Information");
                                                alert.setContentText("A new Borrow process has been inserted");
                                                alert.showAndWait();
                                            } catch (IOException e) {
                                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                                alert.setTitle("PATH ERROR");
                                                alert.setContentText(e.getMessage());
                                                alert.showAndWait();

                                            }
                                        }
                                    } else {
                                        if (borrowDate.compareTo(returnDate) > 0) {
                                            displayAlert(Alert.AlertType.INFORMATION, "Date Insert Error",
                                                    "The return date is before the borrow date");
                                        } else {
                                            if (totalQuantity - currentQuantity < 0 || currentQuantity == 0 || totalQuantity == 0) {
                                                displayAlert(Alert.AlertType.INFORMATION, "Books Quantity Error",
                                                        "The number of books left in the library is\nless than the required amount");
                                            } else {
                                                UPDATE_TIMES_BORROWED.setInt(1, currentQuantity + timesBorrowedResultSet.getInt("TIMESBORROWED"));
                                                UPDATE_TIMES_BORROWED.setLong(2, Long.parseLong(dialogController.getIsbnString()));
                                                UPDATE_TIMES_BORROWED.executeUpdate();
                                                timesBorrowedResultSet.close();

                                                UPDATE_MEMBER_TIMES_BORROWED.setInt(1, 1 + totalMemberBorrowed);
                                                UPDATE_MEMBER_TIMES_BORROWED.setString(2, dialogController.getMemberIdString());
                                                UPDATE_MEMBER_TIMES_BORROWED.executeUpdate();
                                            }
                                            System.out.println("Hello9");

                                            INSERT.executeUpdate();
                                            ResultSet resultSet = SELECT_QUERY.executeQuery();
                                            updateTable(resultSet, ReturnedTableView);
                                            resultSet.close();
                                            ResultSet resultSet2 = SELECT_QUERY2.executeQuery();
                                            updateTable(resultSet2, BorrowedTableView);
                                            resultSet2.close();

                                            System.out.println("Hello10");
                                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                            alert.setTitle("Insert Information");
                                            alert.setContentText("A new Borrow process has been inserted");
                                            alert.showAndWait();
                                        }

                                    }
                                }
                            }
                        }
                    }

                }catch (SQLException e) {
                    displayAlert(Alert.AlertType.ERROR,"DATABASE ERROR",e.getMessage());
                    e.printStackTrace();
                }catch (IllegalArgumentException e){
                    displayAlert(Alert.AlertType.ERROR,"FORMAT ERROR ",e.getMessage()+" OR THE DATE FORMAT IS NOT VALID");
                    e.printStackTrace();
                }
            }
        }
    }

    public void searchButtonPressed(ActionEvent actionEvent){
        try {
            if (actionEvent.getSource().toString().equals(searchOne.toString())) {
                if (searchTextField.getText().isEmpty()) {
                    ResultSet resultSet = SELECT_QUERY2.executeQuery();
                    updateTable(resultSet,BorrowedTableView);
                    resultSet.close();
                }else {
                    SEARCH.setString(1,searchTextField.getText());
                    ResultSet resultSet = SEARCH.executeQuery();
                    updateTable(resultSet,BorrowedTableView);
                    resultSet.close();
                }
            }else if (actionEvent.getSource().toString().equals(searchTwo.toString())) {
                if (search2TextField.getText().isEmpty()) {
                    ResultSet resultSet = SELECT_QUERY.executeQuery();
                    updateTable(resultSet,ReturnedTableView);
                    resultSet.close();
                }else {
                    SEARCH2.setString(1,search2TextField.getText());
                    ResultSet resultSet = SEARCH2.executeQuery();
                    updateTable(resultSet,ReturnedTableView);
                    resultSet.close();
                }
            }
        } catch (SQLException e) {
            displayAlert(Alert.AlertType.ERROR,"DATABASE ERROR",e.getMessage());
        }
    }

    public void returnButtonPressed(ActionEvent actionEvent){
        long mills = System.currentTimeMillis();
        Date currentDate = new Date(mills);

            if (BorrowedTableView.getSelectionModel().getSelectedItem() == null) {
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Select an item");
                alert1.setHeaderText("You need to select an item first ");
                alert1.showAndWait();
            }else {
                Borrow borrow = BorrowedTableView.getSelectionModel().getSelectedItem();
                if (currentDate.compareTo(borrow.getBorrowDate()) < 0) {
                    displayAlert(Alert.AlertType.INFORMATION,"Current - Return Date Error"
                            ,"The Current date is before the borrow date");
                }else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Return Date");
                    alert.setHeaderText("The Returned Day Of This Member is : " + currentDate) ;
                    alert.showAndWait();
                    try {
                        EditTime.setDate(1,currentDate);
                        EditTime.setString(2,borrow.getM_id());
                        EditTime.setLong(3,borrow.getIsbn());
                        EditTime.setDate(4,borrow.getBorrowDate());
                        EditTime.executeUpdate();
                        SELECT_BOOK_QUANTITY.setLong(1,borrow.getIsbn());
                        ResultSet quantityBook = SELECT_BOOK_QUANTITY.executeQuery();
                        quantityBook.next();
                        int totalQuantity = quantityBook.getInt("QUANTITY");
                        int currentQuantity = borrow.getQuantity();
                        quantityBook.close();
                        UPDATE_BOOK_AMOUNT.setInt(1,totalQuantity + currentQuantity);
                        UPDATE_BOOK_AMOUNT.setLong(2,borrow.getIsbn());
                        UPDATE_BOOK_AMOUNT.executeUpdate();

                        ResultSet resultSet2 = SELECT_QUERY2.executeQuery();
                        updateTable(resultSet2,BorrowedTableView);
                        resultSet2.close();
                        ResultSet resultSet = SELECT_QUERY.executeQuery();
                        updateTable(resultSet,ReturnedTableView);
                        resultSet.close();

                        try {
                            SELECT_BOOK_NAME.setLong(1,borrow.getIsbn());
                            ResultSet resultSet3 = SELECT_BOOK_NAME.executeQuery();
                            resultSet3.next();

                            FileWriter fileWriter = new FileWriter(employeeLog,true);
                            fileWriter.append("Assigned ").append("\"").append(borrow.getFirstName()).append(" ").append(borrow.getMiddleName()).append("\"")
                                    .append(" that borrowed ").append("\"")
                                    .append(resultSet3.getString(1)).append("\"").append(" On ").append(String.valueOf(borrow.getBorrowDate()))
                                    .append(" And Returned It").append(" On ").append(String.valueOf(borrow.getBorrowDate())).append(" At ")
                                    .append(String.valueOf(cal.get(Calendar.HOUR))).append(":").append(String.valueOf(cal.get(Calendar.MINUTE)))
                                    .append(":").append(String.valueOf(cal.get(Calendar.SECOND))).append("\n");

                            resultSet3.close();
                            fileWriter.close();
                        } catch (IOException e) {
                            Alert alert2 = new Alert(Alert.AlertType.ERROR);
                            alert2.setTitle("PATH ERROR");
                            alert2.setContentText(e.getMessage());
                            alert2.showAndWait();
                        }

                    } catch (SQLException e) {
                        displayAlert(Alert.AlertType.ERROR,"DATABASE ERROR",e.getMessage());
                    }
                }
            }
        }

    public void deleteButtonPressed(ActionEvent actionEvent){


        String memberType = LoginController.typeOfMember.toLowerCase();

        if (memberType.equals("admin")) {

            Borrow borrow2 = ReturnedTableView.getSelectionModel().getSelectedItem();
            try {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Borrow: ");
                alert.setHeaderText("Are you sure that you want to delete this Member log?\nPress Ok to confirm");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    if (borrow2 != null) {
                        DELETE.setLong(1, borrow2.getIsbn());
                        DELETE.setString(2, borrow2.getM_id());
                        DELETE.setDate(3,borrow2.getBorrowDate());
                        DELETE.execute();
                        ReturnedTableView.getItems().remove(ReturnedTableView.getSelectionModel().getSelectedItem());
                        ReturnedTableView.getSelectionModel().select(null);

                        // gets the times borrowed then updates it to times borrowed - 1
                        SELECT_MEMBER_TIMES_BORROWED.setString(1,borrow2.getM_id());
                        ResultSet totalTimesBorrowedResultSet = SELECT_MEMBER_TIMES_BORROWED.executeQuery();
                        totalTimesBorrowedResultSet.next();
                        int timesBorrowed = totalTimesBorrowedResultSet.getInt(1) - 1;
                        System.out.println(timesBorrowed);
                        UPDATE_MEMBER_TIMES_BORROWED.setInt(1,timesBorrowed);
                        UPDATE_MEMBER_TIMES_BORROWED.setString(2,borrow2.getM_id());
                        UPDATE_MEMBER_TIMES_BORROWED.executeUpdate();
                        System.out.println("done1");
                        totalTimesBorrowedResultSet.close();
                        // gets the book amount then updates it to total quantity - current
//                        GET_TIMES_BORROWED = connection.prepareStatement("SELECT TIMESBORROWED FROM BOOKS WHERE ISBN = ?");
//                        UPDATE_TIMES_BORROWED = connection.prepareStatement("UPDATE BOOKS SET TIMESBORROWED = ? WHERE ISBN = ?");

                        GET_TIMES_BORROWED.setLong(1, borrow2.getIsbn());
                        System.out.println("done2");
                        ResultSet quantityBook = GET_TIMES_BORROWED.executeQuery();
                        System.out.println("done3");
                        quantityBook.next();
                        System.out.println("done4");
                        int totalTimesBorrowed = quantityBook.getInt(1);
                        quantityBook.close();
                        System.out.println("done");
                        System.out.println(totalTimesBorrowed);
                        UPDATE_TIMES_BORROWED.setInt(1, totalTimesBorrowed - borrow2.getQuantity());
                        System.out.println("done11");
                        UPDATE_TIMES_BORROWED.setLong(2, borrow2.getIsbn());
                        System.out.println("done22");
                        int result1 = UPDATE_TIMES_BORROWED.executeUpdate();
                        System.out.println(result1);
                        System.out.println(totalTimesBorrowed - borrow2.getQuantity());
                        try {
                            SELECT_BOOK_NAME.setLong(1,borrow2.getIsbn());
                            ResultSet resultSet = SELECT_BOOK_NAME.executeQuery();
                            resultSet.next();

                            FileWriter fileWriter = new FileWriter(employeeLog,true);
                            fileWriter.append("Deleted ").append("\"").append(borrow2.getFirstName()).append(" ").append(borrow2.getMiddleName()).append("\"")
                                    .append(" that was borrowed on ").append(String.valueOf(borrow2.getBorrowDate())).append(" At ")
                                    .append(String.valueOf(cal.get(Calendar.HOUR))).append(":").append(String.valueOf(cal.get(Calendar.MINUTE)))
                                    .append(":").append(String.valueOf(cal.get(Calendar.SECOND))).append("\n");

                            resultSet.close();
                            fileWriter.close();
                        } catch (IOException e) {
                            Alert alert2 = new Alert(Alert.AlertType.ERROR);
                            alert2.setTitle("PATH ERROR");
                            alert2.setContentText(e.getMessage());
                            alert2.showAndWait();
                        }
                    }else {
                        Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                        alert1.setTitle("Select an item");
                        alert1.setHeaderText("You need to select an item first by \nclicking on it then pressing delete");
                        alert1.showAndWait();
                    }
                }

            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("DATABASE ERROR");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Type Of User ");
            alert.setHeaderText("Only the Administrator can use this feature" );
            alert.showAndWait();
        }
    }

    public void editCell(TableColumn.CellEditEvent<Borrow, String> borrowStringCellEditEvent){
     Borrow borrowItem = BorrowedTableView.getSelectionModel().getSelectedItem();
     String oldValue1 = borrowItem.getM_id();
     borrowItem.setNote(borrowStringCellEditEvent.getNewValue());
     updateRowTable(oldValue1,borrowItem);
    }

    public void updateRowTable(String oldValue1,Borrow borrowItem){
        try{
            EditTable1.setString(1, borrowItem.getNote());
            EditTable1.setString(2, borrowItem.getM_id());
            EditTable1.setDate(3,borrowItem.getDueDate());
            EditTable1.setDate(4,borrowItem.getBorrowDate());
            EditTable1.setInt(5,borrowItem.getQuantity());
            EditTable1.setString(6,oldValue1);
            EditTable1.executeUpdate();
            ResultSet resultSet = SELECT_QUERY2.executeQuery();
            updateTable(resultSet,BorrowedTableView);
            resultSet.close();
        } catch (SQLException e) {
            displayAlert(Alert.AlertType.ERROR,"DATABASE ERROR",e.getMessage());
        }

    }

    public void updateTable(ResultSet resultSet, TableView<Borrow> tableView){
        try {
            ResultSet totalRowsResultSet = TOTAL_ROWS.executeQuery();
            totalRowsResultSet.next();
            int totalRows = totalRowsResultSet.getInt(1);
            totalRowsResultSet.close();

            Borrow[] borrows = new Borrow[totalRows];

            int counter = 0;
            if (tableView.equals(ReturnedTableView)) {
                data2.clear();
                while (resultSet.next()) {
                    borrows[counter] = new Borrow(
                            resultSet.getString("FIRST_NAME"),
                            resultSet.getString("MIDDLE_NAME"),
                            resultSet.getString("NAME"),
                            resultSet.getString("M_ID"),
                            resultSet.getDate("BORROW_DATE"),
                            resultSet.getDate("DUE_DATE"),
                            resultSet.getDate("RETURN_DATE"),
                            resultSet.getInt("QUANTITY"),
                            resultSet.getLong("ISBN"));
                    data2.add(borrows[counter]);
                    counter++;
                }
                tableView.setRowFactory(new Callback<TableView<Borrow>, TableRow<Borrow>>() {
                    @Override
                    public TableRow<Borrow> call(TableView<Borrow> borrowTableView) {
                        return new TableRow<>() {
                            @Override
                            protected void updateItem(Borrow borrow, boolean b) {
                                super.updateItem(borrow, b);

                                if (!b) {
                                    if (borrow.getDueDate().compareTo(borrow.getReturnDate())< 0) {
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
                        new EventHandler<SortEvent<TableView<Borrow>>>() {
                            @Override
                            public void handle(SortEvent<TableView<Borrow>> tableViewSortEvent) {
                                tableView.setRowFactory(new Callback<TableView<Borrow>, TableRow<Borrow>>() {
                                    @Override
                                    public TableRow<Borrow> call(TableView<Borrow> borrowTableView) {
                                        return new TableRow<>() {
                                            @Override
                                            protected void updateItem(Borrow borrow, boolean b) {
                                                super.updateItem(borrow, b);

                                                if (!b) {
                                                    if (borrow.getDueDate().compareTo(borrow.getReturnDate())< 0) {
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
                tableView.setItems(data2);

            } else {
                data.clear();
                while (resultSet.next()) {

                    borrows[counter] = new Borrow(
                            resultSet.getString("FIRST_NAME"),
                            resultSet.getString("MIDDLE_NAME"),
                            resultSet.getString("NAME"),
                            resultSet.getString("M_ID"),
                            resultSet.getDate("BORROW_DATE"),
                            resultSet.getDate("DUE_DATE"),
                            resultSet.getInt("QUANTITY"),
                            resultSet.getLong("ISBN"),
                            resultSet.getString("NOTE"));

                    data.add(borrows[counter]);
                    counter++;
                }

                tableView.setItems(data);

            }
            resultSet.close();
        } catch (SQLException e) {
            displayAlert(Alert.AlertType.ERROR,"DATABASE ERROR",e.getMessage());
        }
    }

    public void navigation(ActionEvent actionEvent) {
        FxmlLoader generalFxmlLoader = new FxmlLoader();
        Pane pane = generalFxmlLoader.getView("HomePage");
        Scene scene = new Scene(pane);
        LibLauncher.applicationStage.setScene(scene);
    }

    private void displayAlert(Alert.AlertType alertType ,String title , String  message){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void searchBetweenButtonPressed(ActionEvent actionEvent)  {
        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.initOwner(borrowBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/LibraryProgram/fxmlFiles/SearchDialog.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            displayAlert(Alert.AlertType.ERROR,"DIALOG ERROR",e.getMessage());
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        dialog.setTitle("Search Between For Borrows:");
        Optional<ButtonType> result = dialog.showAndWait();

        SearchDialogController searchDialogController = fxmlLoader.getController();

        if(result.isPresent() && result.get() == ButtonType.OK) {
            String fromBorrowString = searchDialogController.getYearFromDate() + "-"
                    + searchDialogController.getMonthFromDate()+ "-"
                    + searchDialogController.getDayFromDate();
            Date fromBorrowDate = Date.valueOf(fromBorrowString);

            String toBorrowString = searchDialogController.getYearToDate() + "-"
                    + searchDialogController.getMonthToDate()+ "-"
                    + searchDialogController.getDayToDate();
            Date toBorrowDate = Date.valueOf(toBorrowString);
            try {
                SEARCH_BETWEEN.setDate(1,fromBorrowDate);
                SEARCH_BETWEEN.setDate(2,toBorrowDate);
                ResultSet resultSet = SEARCH_BETWEEN.executeQuery();
                updateTable(resultSet,ReturnedTableView);
                resultSet.close();
                }catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("DATABASE ERROR");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }

        }
    }
}
