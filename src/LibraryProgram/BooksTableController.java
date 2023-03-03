package LibraryProgram;

import LibraryProgram.databaseClasses.Book;
import LibraryProgram.databaseClasses.BookMakerAPI;
import LibraryProgram.fxmlFiles.FxmlLoader;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Optional;


public class BooksTableController {


    public TextField searchByName;
    public Button findByName;
    public Button insertButton;
    public Button deleteButton;
    public Label sectionName;

    public TableView<Book> bookTableView;
    public TableColumn<Book,Long> isbnId;
    public TableColumn<Book,String> nameColumn;
    public TableColumn<Book,Double> price;
    public TableColumn<Book,Integer> quantity;
    public TableColumn<Book,String> section;
    public TableColumn<Book,Boolean> availability;
    public TableColumn<Book,Integer> timesBorrowed;
    public Button insertButtonApi;

    private ObservableList<Book> data;

    private Connection connection;
    @FXML
    private BorderPane tableBorderPane;
    ChangeListener<Book> listener;
    private static HttpURLConnection con;

    private PreparedStatement SEARCH ;
    private PreparedStatement DELETE;
    private PreparedStatement INSERT_BOOK;
    private PreparedStatement EDIT_SECTION;
    private PreparedStatement EDIT_NAME;
    private PreparedStatement TOTAL_ROWS;
    private PreparedStatement SELECT_USING_ISBN;
    private PreparedStatement SAVE_CHANGES;
    private String SELECT_QUERY;
    public String nameOfButton;

    private ContextMenu listContextMenu;

    public void initialize(){
        tableBorderPane.getCenter().maxHeight(500);

        // right click to copy the isbn of an item
        listContextMenu = new ContextMenu();

        MenuItem copyBookIsbn = new MenuItem("Copy ISBN");
        copyBookIsbn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Book selectedBook = bookTableView.getSelectionModel().getSelectedItem();
                copyIsbn(selectedBook.getIsbn());
            }
        });
        listContextMenu.getItems().addAll(copyBookIsbn);

        bookTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                if(t.getButton() == MouseButton.SECONDARY) {
                    listContextMenu.show(bookTableView, t.getScreenX(), t.getScreenY());
                }
            }
        });

        nameOfButton = BooksPageController.nameOfButton;

        sectionName.setText(nameOfButton);
        data = FXCollections.observableArrayList();

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        availability.setCellValueFactory(new PropertyValueFactory<>("available"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        section.setCellValueFactory(new PropertyValueFactory<>("section"));
        isbnId.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        timesBorrowed.setCellValueFactory(new PropertyValueFactory<>("timesBorrowed"));

        bookTableView.setEditable(true);
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        section.setCellFactory(TextFieldTableCell.forTableColumn());
        listener = (observableValue, book, t1) -> {
            bookTableView.getSelectionModel().selectedItemProperty().removeListener(listener);
            bookTableView.getSelectionModel().select(null);
            bookTableView.getSelectionModel().selectedItemProperty().addListener(listener);

        };
        try{

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/spulibdb","root","");

            TOTAL_ROWS = connection.prepareStatement("SELECT COUNT(*) FROM BOOKS");

            SAVE_CHANGES = connection.prepareStatement("UPDATE BOOKS SET NAME = ? ,AUTHOR =?,BOOK_DATE = ?" +
                    ",PUBLISHER = ?,IMAGE_SOURCE = ?,QUANTITY = ? WHERE ISBN =?");
            SEARCH = connection.prepareStatement("SELECT * FROM BOOKS WHERE NAME LIKE ? AND SECTION = ?");
            DELETE = connection.prepareStatement("DELETE FROM BOOKS WHERE ISBN = ? AND SECTION = ?");
            INSERT_BOOK = connection.prepareStatement("INSERT INTO BOOKS(NAME, AVAILABLE, PRICE, QUANTITY, SECTION, ISBN," +
                    " IMAGE_SOURCE, BOOK_DATE, PUBLISHER, AUTHOR) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?)");
            EDIT_SECTION = connection.prepareStatement("UPDATE BOOKS SET SECTION = ? WHERE ISBN = ?");
            EDIT_NAME = connection.prepareStatement("UPDATE BOOKS SET NAME = ? WHERE ISBN = ?");

            if (nameOfButton.equals("All Books")) {
                SELECT_QUERY = "SELECT * FROM  books";
                deleteButton.setVisible(false);
                insertButton.setVisible(false);
                insertButtonApi.setVisible(false);

            }else {
                SELECT_QUERY = "SELECT * FROM BOOKS WHERE SECTION = " + "'" + nameOfButton + "'";
            }

            SELECT_USING_ISBN = connection.prepareStatement("SELECT * FROM BOOKS WHERE ISBN = ?");
            ResultSet resultSet = connection.createStatement().executeQuery(SELECT_QUERY);
            updateTable(resultSet);

        } catch (SQLException | ClassNotFoundException exception) {
            displayAlert(Alert.AlertType.ERROR , "DATABASE ERROR" ,exception.getMessage());
        }

    }

    private void copyIsbn(long isbn) {
        String myString = String.valueOf(isbn);
        StringSelection stringSelection = new StringSelection(myString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public void searchButtonPressed(ActionEvent actionEvent){
        try {
            String name = "%"+searchByName.getText().trim()+"%";
            SEARCH.setString(1,name);
            SEARCH.setString(2,nameOfButton);
            ResultSet resultSet = SEARCH.executeQuery();
            updateTable(resultSet);
        }catch (SQLException e){
            displayAlert(Alert.AlertType.ERROR , "DATABASE ERROR" ,e.getMessage());
        }

    }

    public void insertButtonPressed(ActionEvent actionEvent) {

        String employeeLog = "logs\\" + LoginController.employeeFileName;

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.initOwner(tableBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/LibraryProgram/fxmlFiles/InsertDialog.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            displayAlert(Alert.AlertType.ERROR , "DIALOG ERROR" ,e.getMessage());
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        dialog.setTitle("Creating a new " + nameOfButton + " row");

        Optional<ButtonType> result = dialog.showAndWait();

        if(result.isPresent() && result.get() == ButtonType.OK) {
            InsertDialogController dialogController = fxmlLoader.getController();
            if(dialogController.nameTextField.getText().isEmpty() ||
                    dialogController.isbnTextField.getText().isEmpty() ||
                    dialogController.quantityTextField.getText().isEmpty() ||
                    dialogController.priceTextField.getText().isEmpty()) {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Enter All Entries");
                alert.setHeaderText("You must fill all the fields to add a " + nameOfButton + " field");
                alert.showAndWait();
            }else {
                if (dialogController.getQuantityTextField() < 0 || dialogController.getPriceTextField() < 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Negative Value");
                    alert.setHeaderText("The Price or Quantity fields must not be negative");
                    alert.showAndWait();
                }else {
                    try {

                        INSERT_BOOK.setString(1,dialogController.getNameTextField());
                        boolean available = dialogController.getQuantityTextField() > 0;
                        INSERT_BOOK.setBoolean(2,available);
                        INSERT_BOOK.setDouble(3,dialogController.getPriceTextField());
                        INSERT_BOOK.setInt(4,dialogController.getQuantityTextField());
                        INSERT_BOOK.setString(5,nameOfButton);
                        INSERT_BOOK.setLong(6,dialogController.getIsbnTextField());
                        INSERT_BOOK.setString(7,dialogController.getImageSource());
                        INSERT_BOOK.setString(8,dialogController.getDateTextField());
                        INSERT_BOOK.setString(9,dialogController.getPublisherTextField());
                        INSERT_BOOK.setString(10,dialogController.getAuthorTextField());

                        Calendar cal = Calendar.getInstance();

                        try {
                            FileWriter fileWriter = new FileWriter(employeeLog,true);
                            fileWriter.append("Inserted ").append("\"").append(dialogController.getNameTextField()).append("\"").append(" In ").append(nameOfButton)
                                    .append(" Section " + " At ").append(String.valueOf(cal.get(Calendar.HOUR))).append(":").append(String.valueOf(cal.get(Calendar.MINUTE)))
                                    .append(":").append(String.valueOf(cal.get(Calendar.SECOND))).append("\n");

                            fileWriter.close();
                        } catch (IOException e) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("PATH ERROR");
                            alert.setContentText(e.getMessage());
                            alert.showAndWait();
                        }

                        INSERT_BOOK.executeUpdate();
                        ResultSet resultSet = connection.createStatement().executeQuery(SELECT_QUERY);
                        updateTable(resultSet);

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Insert Information");
                        alert.setContentText("A new Book has been inserted");
                        alert.showAndWait();

                    } catch (SQLException e ) {
                        displayAlert(Alert.AlertType.ERROR , "DATABASE ERROR" ,e.getMessage());
                    }catch ( IllegalArgumentException exception){
                        displayAlert(Alert.AlertType.ERROR , "FORMAT ERROR" ,exception.getMessage());
                    }
                }
            }
        }
    }

    public void deleteButtonPressed(ActionEvent actionEvent) {

        String employeeLog = LoginController.employeeFileName;
        Book selectedBook = bookTableView.getSelectionModel().getSelectedItem();
        String memberType = LoginController.typeOfMember.toLowerCase();

        if (memberType.equals("admin")) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete ");
            alert.setHeaderText("Are you sure that you want to delete this Book?\nPress Ok to confirm" ) ;
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (bookTableView.getSelectionModel().getSelectedItem() == null) {
                    Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                    alert1.setTitle("Select an item");
                    alert1.setHeaderText("You need to select an item first by \nclicking on it then pressing delete");
                    alert1.showAndWait();
                }else {
                    try {
                        long name = bookTableView.getSelectionModel().getSelectedItem().getIsbn();
                        DELETE.setLong(1, name);
                        DELETE.setString(2, nameOfButton);
                        DELETE.execute();
                        bookTableView.getItems().remove(bookTableView.getSelectionModel().getSelectedItem());
                        bookTableView.getSelectionModel().select(null);

                        Calendar cal = Calendar.getInstance();

                        try {
                            FileWriter fileWriter = new FileWriter(employeeLog,true);
                            fileWriter.append("Deleted ").append("\"").append(selectedBook.getName()).append("\"").append(" From ").append(nameOfButton)
                                    .append(" Section " + " At ").append(String.valueOf(cal.get(Calendar.HOUR))).append(":").append(String.valueOf(cal.get(Calendar.MINUTE)))
                                    .append(":").append(String.valueOf(cal.get(Calendar.SECOND))).append("\n");

                            fileWriter.close();
                        } catch (IOException e) {
                            Alert alert2 = new Alert(Alert.AlertType.ERROR);
                            alert2.setTitle("PATH ERROR");
                            alert2.setContentText(e.getMessage());
                            alert2.showAndWait();
                        }

                    } catch (SQLException e) {
                        displayAlert(Alert.AlertType.ERROR , "DATABASE ERROR" ,e.getMessage());
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

    public void updateTable(ResultSet resultSet){
        try {
            data.clear();

            ResultSet totalRowsResultSet = TOTAL_ROWS.executeQuery();
            totalRowsResultSet.next();
            int totalRows = totalRowsResultSet.getInt(1);
            totalRowsResultSet.close();

            Book[] books = new Book[totalRows];
            int counter = 0;

            while (resultSet.next()) {
                books[counter] = new Book(resultSet.getString("NAME"),
                        resultSet.getBoolean("AVAILABLE"),
                        resultSet.getDouble("PRICE"),
                        resultSet.getInt("QUANTITY"),
                        resultSet.getString("SECTION"),
                        resultSet.getLong("ISBN"),
                        resultSet.getInt("TIMESBORROWED"));

                data.add(books[counter]);
                counter++;
            }
            bookTableView.setItems(data);
        } catch (SQLException e) {
            displayAlert(Alert.AlertType.ERROR , "DATABASE ERROR" ,e.getMessage());
        }
    }

    public void editCell(TableColumn.CellEditEvent<Book, String> bookStringCellEditEvent) {
        try {
            Book bookItem = bookTableView.getSelectionModel().getSelectedItem();
            bookItem.setName(bookStringCellEditEvent.getNewValue());
            EDIT_NAME.setString(1,bookItem.getName());
            EDIT_NAME.setLong(2,bookItem.getIsbn());
            EDIT_NAME.executeUpdate();
            ResultSet resultSet = connection.createStatement().executeQuery(SELECT_QUERY);
            updateTable(resultSet);
        } catch (SQLException e) {
            displayAlert(Alert.AlertType.ERROR , "DATABASE ERROR" ,e.getMessage());
        }

    }

    public void editCell2(TableColumn.CellEditEvent<Book, String> bookStringCellEditEvent)  {
        try {
            Book book = bookTableView.getSelectionModel().getSelectedItem();
            book.setSection(bookStringCellEditEvent.getNewValue());
            EDIT_SECTION.setString(1,book.getSection());
            EDIT_SECTION.setLong(2,book.getIsbn());
            EDIT_SECTION.executeUpdate();
            ResultSet resultSet = connection.createStatement().executeQuery(SELECT_QUERY);
            updateTable(resultSet);
        } catch (SQLException e) {
            displayAlert(Alert.AlertType.ERROR , "DATABASE ERROR" ,e.getMessage());
        }

    }

    private void displayAlert(Alert.AlertType alertType ,String title , String  message){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void navigation(ActionEvent actionEvent) {
        FxmlLoader generalFxmlLoader = new FxmlLoader();
        Pane pane = generalFxmlLoader.getView("HomePage");
        Scene scene = new Scene(pane);
        LibLauncher.applicationStage.setScene(scene);
    }
    public void navigationback(ActionEvent actionEvent) {
        FxmlLoader generalFxmlLoader = new FxmlLoader();
        Pane pane = generalFxmlLoader.getView("BooksPage");
        Scene scene = new Scene(pane);
        LibLauncher.applicationStage.setScene(scene);
    }



    public void insertButtonApiPressed(ActionEvent actionEvent) {

        String employeeLog = "logs\\" + LoginController.employeeFileName;

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.initOwner(tableBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/LibraryProgram/fxmlFiles/InsertBookApiDialog.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            displayAlert(Alert.AlertType.ERROR , "DIALOG ERROR" ,e.getMessage());
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        dialog.setTitle("Creating a new " + nameOfButton + " row");

        Optional<ButtonType> result = dialog.showAndWait();

        if(result.isPresent() && result.get() == ButtonType.OK) {
            InsertBookApiDialogController dialogController = fxmlLoader.getController();
            if(dialogController.isbnTextFieldApi.getText().isEmpty() ||
                    dialogController.quantityTextFieldApi.getText().isEmpty() ||
                    dialogController.priceTextFieldApi.getText().isEmpty()) {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Enter All Entries");
                alert.setHeaderText("You must fill all the fields to add a " + nameOfButton + " field");
                alert.showAndWait();
            }else {
                if (dialogController.getQuantityTextField() < 0 || dialogController.getPriceTextField() < 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Negative Value");
                    alert.setHeaderText("The Price or Quantity fields must not be negative");
                    alert.showAndWait();
                }else {
                    INSERT_PROCESS:
                    try {

                        // creates the book maker api then uses the isbn in the operation

                        String url = "https://api2.isbndb.com/book/" + dialogController.getIsbnTextField();

                        try {

                            URL bookUrl = new URL(url);
                            con = (HttpURLConnection) bookUrl.openConnection();
                            con.setRequestProperty("Content-Type", "application/json");
                            con.setRequestProperty("Authorization", "45834_2b5d4cda67417d5f0ed3681d61f67cb5");
                            con.setRequestMethod("GET");

                            InputStream inputStream = con.getInputStream();
                            BookMakerAPI testingBook = new BookMakerAPI(inputStream);

                            // create the image into the bookImagesFile
                            try {
                                URL url1 = new URL(testingBook.getImage());
                                InputStream in = new BufferedInputStream(url1.openStream());
                                ByteArrayOutputStream out = new ByteArrayOutputStream();
                                byte[] buf = new byte[1024];
                                int n;
                                while (-1!=(n=in.read(buf)))
                                {
                                    out.write(buf, 0, n);
                                }
                                out.close();
                                in.close();
                                byte[] response = out.toByteArray();

                                // name of the image so it gets stored in the database and gets uploaded everytime we use it
                                String imagePath = "resources/bookImages/" + testingBook.getIsbn() + ".jpg";
                                FileOutputStream fos = new FileOutputStream(imagePath);
                                fos.write(response);
                                fos.close();

                                INSERT_BOOK.setString(1,testingBook.getBookName());
                                boolean available = dialogController.getQuantityTextField() > 0;
                                INSERT_BOOK.setBoolean(2,available);
                                INSERT_BOOK.setDouble(3,dialogController.getPriceTextField());
                                INSERT_BOOK.setInt(4,dialogController.getQuantityTextField());
                                INSERT_BOOK.setString(5,nameOfButton);
                                INSERT_BOOK.setLong(6,Long.parseLong(testingBook.getIsbn()));
                                INSERT_BOOK.setString(7,imagePath);
                                INSERT_BOOK.setString(8,testingBook.getDatePublished());
                                INSERT_BOOK.setString(9,testingBook.getPublisher());
                                INSERT_BOOK.setString(10,testingBook.getAuthor());

                            }catch (MalformedURLException | FileNotFoundException e) {
                                displayAlert(Alert.AlertType.ERROR,"Connection Or Book Not Found In the Database","Either the Book is not found in the Database\nor the connections is lost\nplease check the connection to the Internet");
                                break INSERT_PROCESS;
                            }

                            Calendar cal = Calendar.getInstance();

                            try {
                                FileWriter fileWriter = new FileWriter(employeeLog,true);
                                fileWriter.append("Inserted ").append("\"").append(testingBook.getBookName()).append("\"").append(" In ").append(nameOfButton)
                                        .append(" Section " + " At ").append(String.valueOf(cal.get(Calendar.HOUR))).append(":").append(String.valueOf(cal.get(Calendar.MINUTE)))
                                        .append(":").append(String.valueOf(cal.get(Calendar.SECOND))).append("\n");

                                fileWriter.close();
                            } catch (IOException e) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("PATH ERROR");
                                alert.setContentText(e.getMessage());
                                alert.showAndWait();
                            }

                        } catch (IOException e) {
                            displayAlert(Alert.AlertType.ERROR,"Connection Or Book Not Found In the Database","Either the Book is not found in the Database\nor the connections is lost\nplease check the connection to the Internet");
                            break INSERT_PROCESS;
                        } finally {
                            con.disconnect();
                        }

                        INSERT_BOOK.executeUpdate();
                        ResultSet resultSet = connection.createStatement().executeQuery(SELECT_QUERY);
                        updateTable(resultSet);

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Insert Information");
                        alert.setContentText("A new Book has been inserted");
                        alert.showAndWait();

                    } catch (SQLException e ) {
                        displayAlert(Alert.AlertType.ERROR , "DATABASE ERROR" ,e.getMessage());
                    }
                    catch ( IllegalArgumentException e){
                        displayAlert(Alert.AlertType.ERROR , "FORMAT ERROR" ,e.getMessage());
                    }
                }
            }
        }
    }

    public void detailsButtonPressed(ActionEvent actionEvent) {

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.initOwner(tableBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/LibraryProgram/fxmlFiles/BookInfoDialog.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            displayAlert(Alert.AlertType.ERROR , "DIALOG ERROR" ,e.getMessage());
        }

        ButtonType buttonType = new ButtonType("Save Changes", ButtonBar.ButtonData.APPLY);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(buttonType);

        dialog.setTitle("Creating a new " + nameOfButton + " row");

            BookInfoDialogController dialogController = fxmlLoader.getController();

            try {
                Book selectedBook = bookTableView.getSelectionModel().getSelectedItem();

                if (selectedBook == null) {
                    displayAlert(Alert.AlertType.INFORMATION,"Item Selection","Please Select a Book to display it's information");
                }else {
                    SELECT_USING_ISBN.setLong(1,selectedBook.getIsbn());
                    ResultSet resultSet = SELECT_USING_ISBN.executeQuery();
                    resultSet.next();
                    dialogController.setBookAuthor(resultSet.getString("AUTHOR"));
                    dialogController.setBookDate(resultSet.getString("BOOK_DATE"));
                    dialogController.setBookTitle(resultSet.getString("NAME"));
                    dialogController.setBookIsbn(String.valueOf(resultSet.getLong("ISBN")));
                    dialogController.setImageSource(resultSet.getString("IMAGE_SOURCE"));
                    dialogController.setBookPublisher(resultSet.getString("PUBLISHER"));
                    dialogController.setQuantityTextField(resultSet.getString("QUANTITY"));
                    resultSet.close();

                    Optional<ButtonType> result = dialog.showAndWait();

                    if(result.isPresent() && result.get() == buttonType) {
                        try {
                            saveChanges(dialogController.getBookTitle(),dialogController.getBookAuthor(),dialogController.getBookDate(),
                                    dialogController.getBookPublisher(),dialogController.getBookIsbn(),Integer.parseInt(dialogController.getQuantityTextField()));
                        }catch (NumberFormatException e) {
                            displayAlert(Alert.AlertType.ERROR,"Format Error",e.getMessage());
                        }
                    }
                }
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("DATABASE ERROR");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
    }


    private void saveChanges(String bookTitle, String bookAuthor, String bookDate
            , String bookPublisher, String bookIsbn,int quantity) {
        try {
            SAVE_CHANGES.setString(1,bookTitle);
            SAVE_CHANGES.setString(2,bookAuthor);
            SAVE_CHANGES.setString(3,bookDate);
            SAVE_CHANGES.setString(4,bookPublisher);
            SAVE_CHANGES.setString(5,BookInfoDialogController.updatedImageSource);
            SAVE_CHANGES.setInt(6,quantity);
            SAVE_CHANGES.setString(7,bookIsbn);
            SAVE_CHANGES.executeUpdate();
            ResultSet resultSet = connection.createStatement().executeQuery(SELECT_QUERY);
            updateTable(resultSet);
            resultSet.close();

            displayAlert(Alert.AlertType.INFORMATION,"Changes Saved","Information Updated Successfully");
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("DATABASE ERROR");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }


}
