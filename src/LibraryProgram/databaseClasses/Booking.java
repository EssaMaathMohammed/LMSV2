package LibraryProgram.databaseClasses;

import javafx.beans.property.SimpleStringProperty;

import java.util.Date;

public class Booking {

    private long orderId;
    private Date bookingDate;
    private Date dueBookingDate;
    private long isbn;
    private SimpleStringProperty bookName;
    private SimpleStringProperty memberId;
    private SimpleStringProperty firstName;
    private SimpleStringProperty middleName;

    public Booking(String firstName, String middleName,long orderId,String bookName, Date bookingDate, Date dueBookingDate, long isbn, String memberId) {
        this.orderId = orderId;
        this.bookingDate = bookingDate;
        this.dueBookingDate = dueBookingDate;
        this.isbn = isbn;
        this.bookName = new SimpleStringProperty(bookName);
        this.memberId = new SimpleStringProperty(memberId);
        this.firstName = new SimpleStringProperty(firstName);
        this.middleName = new SimpleStringProperty(middleName);
    }

    public long getOrderId() {
        return orderId;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public Date getDueBookingDate() {
        return dueBookingDate;
    }

    public long getIsbn() {
        return isbn;
    }

    public String getMemberId() {
        return memberId.get();
    }

    public SimpleStringProperty memberIdProperty() {
        return memberId;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public void setDueBookingDate(Date dueBookingDate) {
        this.dueBookingDate = dueBookingDate;
    }

    public void setIsbn(long isbn) {
        this.isbn = isbn;
    }

    public void setMemberId(String memberId) {
        this.memberId.set(memberId);
    }

    public String getFirstName() {
        return firstName.get();
    }

    public SimpleStringProperty firstNameProperty() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public String getMiddleName() {
        return middleName.get();
    }

    public SimpleStringProperty middleNameProperty() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName.set(middleName);
    }

    public String getBookName() {
        return bookName.get();
    }

    public SimpleStringProperty bookNameProperty() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName.set(bookName);
    }
}
