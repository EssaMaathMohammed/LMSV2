package LibraryProgram.databaseClasses;

import javafx.beans.property.SimpleStringProperty;

import java.util.Date;

public class Book {
    private long isbn;
    private SimpleStringProperty name;
    private boolean available;
    private double price;
    private int quantity;
    private SimpleStringProperty section;
    private int timesBorrowed;

    //the new variables (in the database)

    private SimpleStringProperty imageUrl;
    private SimpleStringProperty publisher;
    private SimpleStringProperty author;
    private SimpleStringProperty bookDate;

    /*
    .
    .
    .
    .
    .
    .
    .
     */
    // the books information dialog constructor
    public Book(long isbn, String name, String imageUrl, String publisher, String author
            ,boolean available,double price,String bookDate,int quantity,String section,int timesBorrowed) {
        this.isbn = isbn;
        this.name = new SimpleStringProperty(name);
        this.imageUrl = new SimpleStringProperty(imageUrl);
        this.publisher = new SimpleStringProperty(publisher);
        this.author = new SimpleStringProperty(author);
        this.bookDate = new SimpleStringProperty(bookDate);
        this.available = available;
        this.price = price;
        this.quantity = quantity;
        this.section = new SimpleStringProperty(section);
        this.timesBorrowed = timesBorrowed;
    }

    public Book(String name, boolean available
            , double price, int quantity, String section, long isbn, int timesBorrowed) {
        this.isbn = isbn;
        this.name = new SimpleStringProperty(name);
        this.available = available;
        this.price = price;
        this.quantity = quantity;
        this.section = new SimpleStringProperty(section);
        this.timesBorrowed = timesBorrowed;
    }

    public Book(String name, int timesBorrowed) {
        this.name = new SimpleStringProperty(name);
        this.timesBorrowed = timesBorrowed;
    }

    public int getTimesBorrowed() {
        return timesBorrowed;
    }

    public void setTimesBorrowed(int timesBorrowed) {
        this.timesBorrowed = timesBorrowed;
    }

    public long getIsbn() {
        return isbn;
    }

    public void setIsbn(long isbn) {
        this.isbn = isbn;
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSection() {
        return section.get();
    }

    public SimpleStringProperty sectionProperty() {
        return section;
    }

    public void setSection(String section) {
        this.section.set(section);
    }
}
