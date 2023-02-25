package LibraryProgram.databaseClasses;

import javafx.beans.property.SimpleStringProperty;

import java.sql.Date;

public class Borrow {
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

    private SimpleStringProperty firstName;
    private SimpleStringProperty middleName;
    private SimpleStringProperty m_id;
    private Date borrowDate;
    private Date dueDate;
    private Date returnDate;
    private int quantity;
    private long isbn;
    private SimpleStringProperty note;
    private SimpleStringProperty bookName;



    public Borrow(String firstName , String middleName, String bookName , String m_id, Date borrowDate, Date dueDate, Date returnDate, int quantity, long isbn) {
        this.firstName = new SimpleStringProperty(firstName);
        this.middleName= new SimpleStringProperty(middleName);
        this.bookName = new SimpleStringProperty(bookName);
        this.m_id = new SimpleStringProperty(m_id);
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.quantity = quantity;
        this.isbn = isbn;
    }

    public Borrow(String firstName , String middleName , String bookName ,String m_id, Date borrowDate, Date dueDate, int quantity, long isbn, String note) {

        this.firstName = new SimpleStringProperty(firstName);
        this.middleName= new SimpleStringProperty(middleName);
        this.bookName = new SimpleStringProperty(bookName);
        this.m_id =new SimpleStringProperty(m_id);
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.quantity = quantity;
        this.isbn = isbn;
        this.note =  new SimpleStringProperty(note);

    }

    public String getM_id() {
        return m_id.get();
    }

    public SimpleStringProperty m_idProperty() {
        return m_id;
    }

    public void setM_id(String m_id) {
        this.m_id.set(m_id);
    }

    public String getNote() {
        return note.get();
    }

    public SimpleStringProperty noteProperty() {
        return note;
    }

    public void setNote(String note) {
        this.note.set(note);
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getIsbn() {
        return isbn;
    }

    public void setIsbn(long isbn) {
        this.isbn = isbn;
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

    @Override
    public String toString() {
        return "Borrow{" +
                "firstName=" + firstName +
                ", middleName=" + middleName +
                ", m_id=" + m_id +
                ", borrowDate=" + borrowDate +
                ", dueDate=" + dueDate +
                ", quantity=" + quantity +
                ", isbn=" + isbn +
                ", note=" + note +
                ", bookName=" + bookName +
                '}';
    }
}
