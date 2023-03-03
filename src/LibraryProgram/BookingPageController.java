package LibraryProgram;

import LibraryProgram.databaseClasses.Booking;
import LibraryProgram.databaseClasses.Borrow;
import LibraryProgram.databaseClasses.Member;
import LibraryProgram.fxmlFiles.FxmlLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

public class BookingPageController {
    public TextField searchMID;
    public TableColumn<Booking,Long> orderId;
    public TableColumn<Booking,String> memberIdColumn;
    public TableColumn<Booking,String> firstNameColumn;
    public TableColumn<Booking,String> middleNameColumn;
    public TableColumn<Booking,Long> bookIsbncolumn;
    public TableColumn<Booking,Date> booking_datecolumn;
    public TableColumn<Booking,Date> due_bookingDateColumn;
    public TableColumn<Booking,String> bookNameColumn;
    public BorderPane bookingBorderPane;
    public TableView<Booking> bookingTableView;
    public TextField searchIsbn;

    private PreparedStatement SELECT ;
    private PreparedStatement TOTAL_ROWS;
    private PreparedStatement INSERT;
    private PreparedStatement DELETE;
    private PreparedStatement SEARCH_MID;
    private PreparedStatement SEARCH_ISBN;
    private PreparedStatement SEARCH_BOTH;
    private PreparedStatement SELECT_BOOK_NAME;
    private PreparedStatement INSERT_INTO_BORROWS;
    private PreparedStatement SELECT_BOOK_QUANTITY;
    private PreparedStatement UPDATE_BOOK_AMOUNT;
    private PreparedStatement SELECT_BORROW_DUE_DATES;
    private PreparedStatement SELECT_BORROW_DUE_DATES_COUNT;
    private PreparedStatement SELECT_BOOKING_INFORMATION;
    Connection connection;
    private static ObservableList<Booking> data;
    String employeeLog = "logs\\" + LoginController.employeeFileName;
    Calendar cal = Calendar.getInstance();


    public void initialize(){
        bookingBorderPane.getCenter().maxHeight(500);

        orderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        memberIdColumn.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        middleNameColumn.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        booking_datecolumn.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));
        due_bookingDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueBookingDate"));
        bookIsbncolumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        bookNameColumn.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        data = FXCollections.observableArrayList();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/spulibdb","root","");

            TOTAL_ROWS = connection.prepareStatement("SELECT COUNT(*) FROM booking");
            INSERT = connection.prepareStatement("INSERT INTO booking (BOOKING_DATE, DUE_BOOKING_DATE, ISBN, M_ID) VALUES " +
                    "(?,?,?,?)");
            DELETE = connection.prepareStatement("DELETE FROM booking WHERE ORDER_ID = ?");

            SELECT = connection.prepareStatement("SELECT m.FIRST_NAME,m.MIDDLE_NAME, ORDER_ID, BOOKING_DATE, DUE_BOOKING_DATE, booking.ISBN, booking.M_ID FROM booking " +
                    "JOIN members m on m.M_ID = booking.M_ID" );

            SELECT_BOOK_NAME = connection.prepareStatement("SELECT NAME FROM BOOKS WHERE ISBN = ?");

            SEARCH_MID = connection.prepareStatement("SELECT m.FIRST_NAME,m.MIDDLE_NAME, ORDER_ID, BOOKING_DATE, DUE_BOOKING_DATE, booking.ISBN, booking.M_ID FROM booking " +
                    "JOIN members m on m.M_ID = booking.M_ID " +
                    "WHERE m.M_ID = ?" );
            SEARCH_ISBN = connection.prepareStatement("SELECT m.FIRST_NAME,m.MIDDLE_NAME, ORDER_ID, BOOKING_DATE, DUE_BOOKING_DATE, booking.ISBN, booking.M_ID FROM booking " +
                    "JOIN members m on m.M_ID = booking.M_ID " +
                    "WHERE ISBN = ?" );

            SEARCH_BOTH = connection.prepareStatement("SELECT m.FIRST_NAME,m.MIDDLE_NAME, ORDER_ID, BOOKING_DATE, DUE_BOOKING_DATE, booking.ISBN, booking.M_ID FROM booking " +
                    "JOIN members m on m.M_ID = booking.M_ID " +
                    "WHERE ISBN = ? AND m.M_ID =?" );

            INSERT_INTO_BORROWS = connection.prepareStatement("INSERT INTO BORROW (M_ID, BORROW_DATE, DUE_DATE, QUANTITY, ISBN) " +
                    "VALUES (?,?,?,?,?)" );

            SELECT_BOOK_QUANTITY = connection.prepareStatement("SELECT QUANTITY FROM BOOKS WHERE ISBN = ?");
            UPDATE_BOOK_AMOUNT = connection.prepareStatement("UPDATE BOOKS SET QUANTITY = ? WHERE ISBN = ?");

            SELECT_BORROW_DUE_DATES = connection.prepareStatement("SELECT DUE_DATE FROM borrow WHERE ISBN = ?  AND RETURN_DATE IS NULL");
            SELECT_BORROW_DUE_DATES_COUNT = connection.prepareStatement("SELECT COUNT(*) FROM borrow WHERE ISBN = ?");
            SELECT_BOOKING_INFORMATION = connection.prepareStatement("SELECT BOOKING_DATE, DUE_BOOKING_DATE FROM booking WHERE ISBN = ?");

            ResultSet resultSet = SELECT.executeQuery();
            updateTable(resultSet);

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // Done - checked
    public void searchByIDButtonPressed(ActionEvent actionEvent){
        String M_ID = searchMID.getText().trim();
        try {
            if(searchIsbn.getText().isEmpty() && searchMID.getText().isEmpty()){
                ResultSet resultSet = SELECT.executeQuery();
                updateTable(resultSet);
            }else if(!searchIsbn.getText().isEmpty() && searchMID.getText().isEmpty()){
                long ISBN = Long.parseLong(searchIsbn.getText().trim());
                SEARCH_ISBN.setLong(1,ISBN);
                ResultSet resultSet = SEARCH_ISBN.executeQuery();
                updateTable(resultSet);
            }else if(searchIsbn.getText().isEmpty() && !searchMID.getText().isEmpty()){
                SEARCH_MID.setString(1,M_ID);
                ResultSet resultSet = SEARCH_MID.executeQuery();
                updateTable(resultSet);
            }else {
                long ISBN = Long.parseLong(searchIsbn.getText().trim());
                SEARCH_BOTH.setLong(1,ISBN);
                SEARCH_BOTH.setString(2,M_ID);
                ResultSet resultSet = SEARCH_BOTH.executeQuery();
                updateTable(resultSet);
            }
        }catch (SQLException e){
            displayAlert(Alert.AlertType.ERROR,"DATABASE ERROR", e.getMessage());
        }


    }

    public void insertButtonPressed(ActionEvent actionEvent) {
        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.initOwner(bookingBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/LibraryProgram/fxmlFiles/InsertBooking.fxml"));

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
            InsertBookingController dialogController = fxmlLoader.getController();
            if(dialogController.memberIdTextField.getText().isEmpty()||
                    dialogController.bookingDateDay.getText().isEmpty()||
                    dialogController.bookingDateMonth.getText().isEmpty() ||
                    dialogController.bookingDateYear.getText().isEmpty() ||
                    dialogController.dueDateDay.getText().isEmpty()||
                    dialogController.dueDateMonth.getText().isEmpty()||
                    dialogController.dueDateYear.getText().isEmpty() ||
                    dialogController.isbnTextField.getText().isEmpty()) {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Enter All Entries");
                alert.setHeaderText("You must fill all the fields to add a new member");
                alert.showAndWait();
            }else {
                try {

                    boolean insertBook = true;
                    long bookISBN = Long.parseLong(dialogController.isbnTextField.getText());
                    Date[] datesArray = new Date[0];
                    boolean isAfterDate = false;


                    String dateBookingString = dialogController.getBookingDateYear()+"-"+dialogController.getBookingDateMonth()+"-"+dialogController.getBookingDateDay();
                    String dateDueBooking = dialogController.getDueDateYear()+"-"+dialogController.getDueDateMonth()+"-"+dialogController.getDueDateDay();
                    Date date = Date.valueOf(dateBookingString);
                    Date dueDate = Date.valueOf(dateDueBooking);

                    Date currentDate = new Date(System.currentTimeMillis());
                    String currentDateCompare = String.valueOf(currentDate);
                    Date currentDateCompared = Date.valueOf(currentDateCompare);


                    // the total number of the book in the library currently, to check whether we can book the book or not.
                    SELECT_BOOK_QUANTITY.setLong(1, bookISBN);
                    ResultSet quantityBook = SELECT_BOOK_QUANTITY.executeQuery();
                    quantityBook.next();
                    int currentBookQuantity = quantityBook.getInt("QUANTITY");
                    quantityBook.close();

                    // check the book count if book count is 0 or 1, else insert normally
                    if (currentBookQuantity == 0 || currentBookQuantity == 1) {
                        if (currentBookQuantity == 0) {
                            // bring the due dates of the borrow table.
                            SELECT_BORROW_DUE_DATES.setLong(1,bookISBN);
                            ResultSet BORROW_DUE_DATES_RESUL_SET = SELECT_BORROW_DUE_DATES.executeQuery();

                            // get the number of elements in the result set (how many due dates)
                            SELECT_BORROW_DUE_DATES_COUNT.setLong(1,bookISBN);
                            ResultSet DUE_DATES_COUNT_RESULT_SET = SELECT_BORROW_DUE_DATES_COUNT.executeQuery();
                            DUE_DATES_COUNT_RESULT_SET.next();
                            int datesArrayLength = DUE_DATES_COUNT_RESULT_SET.getInt(1);
                            DUE_DATES_COUNT_RESULT_SET.close();

                            datesArray = new Date[datesArrayLength];
                            for (int count = 0; count < datesArrayLength; count++) {
                                BORROW_DUE_DATES_RESUL_SET.next();
                                datesArray[count] = BORROW_DUE_DATES_RESUL_SET.getDate("DUE_DATE");
                            }
                            BORROW_DUE_DATES_RESUL_SET.close();

                            // check if the date that the user entered is after one of the due dates

                            for (Date borrowDueDate :
                                    datesArray) {
                                if (dueDate.after(borrowDueDate)) {
                                    isAfterDate = true;
                                    break;
                                }
                            }

                        }
                        // if the date that the user entered is not after one of the due dates we give the user an insert error
                        if (!isAfterDate) {
                            String dates = "";
                            for (Date borrowDueDate :
                                    datesArray) {
                               dates += borrowDueDate.toString()+ " ";
                            }
                            System.out.printf("here");

                            displayAlert(Alert.AlertType.ERROR,"Insert Error","The Book is not available in the dates you entered," +
                                    "\nThe book is available after the following dates: " + dates);
                            // do not let the user insert the book
                            insertBook = false;
                        }else {
                            // otherwise we need to check for duplicates in the booking table


                        }
                        // check the booking table for duplicate dates, if the dates are duplicates don't let the user insert

                    }

                    // otherwise create the booking normally
                    if (insertBook) {


                        if (date.before(currentDateCompared) || dueDate.before(currentDateCompared) || dueDate.before(date)) {

                            displayAlert(Alert.AlertType.INFORMATION,"Date Error","The date you inputted is either before\nthe current date or the due date is before the\nbooking date");

                        }else{
                            INSERT.setDate(1,date);
                            INSERT.setDate(2,dueDate);
                            INSERT.setLong(3,Long.parseLong(dialogController.getIsbnTextField()));
                            INSERT.setString(4,dialogController.getMemberIdTextField());
                            INSERT.executeUpdate();

                            ResultSet resultSet = SELECT.executeQuery();
                            updateTable(resultSet);

                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Insert Information");
                            alert.setContentText("A new Member has been inserted");
                            alert.showAndWait();
                        }


                    }

//                    try {
//                        FileWriter fileWriter = new FileWriter(employeeLog,true);
//                        fileWriter.append("Inserted ").append("\"").append(dialogController.getMemberIdTextField()).append(" ")
//                                .append(dialogController.getMiddleName()).append(" ").append(dialogController.getLastName())
//                                .append("\"").append(" From ").append(dialogController.getDepartment()).append(" At ")
//                                .append(String.valueOf(cal.get(Calendar.HOUR))).append(":").append(String.valueOf(cal.get(Calendar.MINUTE)))
//                                .append(":").append(String.valueOf(cal.get(Calendar.SECOND))).append("\n");
//
//                        fileWriter.close();
//                    } catch (IOException e) {
//                        displayAlert(Alert.AlertType.ERROR,"File Writing Error", e.getMessage());
//                    }



                } catch (IllegalArgumentException e){
                    displayAlert(Alert.AlertType.ERROR,"FORMAT ERROR","The date format you entered is not acceptable ");
                    e.printStackTrace();
                }
                catch (SQLException e) {
                    displayAlert(Alert.AlertType.ERROR,"DATABASE ERROR", e.getMessage());
                    e.printStackTrace();
                }

            }
        }
    }

    public void deleteButtonPressed(ActionEvent actionEvent) {
        Booking booking = bookingTableView.getSelectionModel().getSelectedItem();
        String memberType = LoginController.typeOfMember.toLowerCase();

        if (memberType.equals("admin")) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete ");
            alert.setHeaderText("Are you sure that you want to delete this Member?\nPress Ok to confirm");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (booking == null) {
                    Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Select an item");
                    alert.setHeaderText("You need to select a Member first by \nclicking on it then pressing delete");
                    alert.showAndWait();
                } else {
                    try {
                        long order = booking.getOrderId();
                        DELETE.setLong(1, order);
                        DELETE.execute();
                        bookingTableView.getItems().remove(bookingTableView.getSelectionModel().getSelectedItem());
                        bookingTableView.getSelectionModel().select(null);

                        try {
                           FileWriter fileWriter = new FileWriter(employeeLog,true);
                           fileWriter.append("Deleted ").append("\"").append(booking.getFirstName()).append(" ")
                                   .append(booking.getMiddleName()).append("who was booking ").append(String.valueOf(booking.getIsbn())).append(" At ")
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
            data.clear();

            ResultSet totalRowsResultSet = TOTAL_ROWS.executeQuery();
            totalRowsResultSet.next();
            int totalRows = totalRowsResultSet.getInt(1);
            totalRowsResultSet.close();

            Booking[] bookings = new Booking[totalRows];
            int counter = 0;

            while (resultSet.next()) {

                SELECT_BOOK_NAME.setLong(1, resultSet.getLong("ISBN"));
                ResultSet bookNameRS = SELECT_BOOK_NAME.executeQuery();
                bookNameRS.next();
                String bookName = bookNameRS.getString(1);

                bookings[counter] = new Booking(resultSet.getString("FIRST_NAME"),
                        resultSet.getString("MIDDLE_NAME"),
                        resultSet.getLong("ORDER_ID"),
                        bookName,
                        resultSet.getDate("BOOKING_DATE"),
                        resultSet.getDate("DUE_BOOKING_DATE"),
                        resultSet.getLong("ISBN"),
                        resultSet.getString("M_ID"));

                data.add(bookings[counter]);
                counter++;
            }
            bookingTableView.setItems(data);
        } catch (SQLException e) {
          e.printStackTrace();
        }

    }

    private void displayAlert(Alert.AlertType alertType ,String title , String  message){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.setWidth(400);
        alert.setHeight(350);
        alert.showAndWait();
    }

    public void navigation(ActionEvent actionEvent) {
        FxmlLoader generalFxmlLoader = new FxmlLoader();
        Pane pane = generalFxmlLoader.getView("HomePage");
        Scene scene = new Scene(pane);
        LibLauncher.applicationStage.setScene(scene);
    }


    public void startBookingButtonClicked(ActionEvent actionEvent) {
        Booking selectedItem = bookingTableView.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            displayAlert(Alert.AlertType.INFORMATION,"Item not found","Please select an item to start booking");
        }else {

            try {

                SELECT_BOOK_QUANTITY.setLong(1, selectedItem.getIsbn());
                ResultSet remainingBooksRS = SELECT_BOOK_QUANTITY.executeQuery();
                remainingBooksRS.next();
                int remainingBooks = remainingBooksRS.getInt(1);
                remainingBooksRS.close();
                System.out.println(remainingBooks);

                if (remainingBooks == 0) {
                    displayAlert(Alert.AlertType.INFORMATION,"Book not available","You don't have any copies of this\nbook currently check when it gets\navailable in the borrow/books pages");
                    }else {
                    Date currentDate = new Date(System.currentTimeMillis());
                    String currentDateCompare = String.valueOf(currentDate);
                    Date currentDateCompared = Date.valueOf(currentDateCompare);

                    Date dueDate = new Date(selectedItem.getDueBookingDate().getTime());
                    String dueDateString = String.valueOf(dueDate);
                    Date dueDateUsed = Date.valueOf(dueDateString);

                    Borrow movingItem = new Borrow(selectedItem.getFirstName(), selectedItem.getMiddleName(),
                            selectedItem.getBookName(),selectedItem.getMemberId(),currentDateCompared,
                            dueDateUsed,1, selectedItem.getIsbn(), "");

                    UPDATE_BOOK_AMOUNT.setInt(1, remainingBooks-1);
                    UPDATE_BOOK_AMOUNT.setLong(2,selectedItem.getIsbn());
                    UPDATE_BOOK_AMOUNT.executeUpdate();

                    INSERT_INTO_BORROWS.setString(1, movingItem.getM_id());
                    INSERT_INTO_BORROWS.setDate(2, movingItem.getBorrowDate());
                    INSERT_INTO_BORROWS.setDate(3, movingItem.getDueDate());
                    INSERT_INTO_BORROWS.setInt(4, 1); // needs to be changed
                    INSERT_INTO_BORROWS.setLong(5, movingItem.getIsbn());
                    INSERT_INTO_BORROWS.executeUpdate();

                    DELETE.setLong(1, selectedItem.getOrderId());
                    DELETE.execute();
                    bookingTableView.getItems().remove(selectedItem);
                    bookingTableView.getSelectionModel().select(null);

                    displayAlert(Alert.AlertType.INFORMATION,"Book Moved Successfully","The Book moved to the borrow page");
                    }
                } catch (SQLException throwable) {
                displayAlert(Alert.AlertType.ERROR,"Duplicate entry","This entry already exists in the borrow table");
            }


        }
    }
}
