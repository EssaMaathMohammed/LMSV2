package LibraryProgram;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class InsertBookingController {
    public TextField memberIdTextField;
    public TextField bookingDateDay;
    public TextField bookingDateMonth;
    public TextField bookingDateYear;
    public TextField dueDateDay;
    public TextField dueDateMonth;
    public TextField dueDateYear;
    public TextField isbnTextField;

    public void setBookingDateDay(String bookingDateDay) {
        this.bookingDateDay.setText(bookingDateDay);
    }

    public void setBookingDateMonth(String bookingDateMonth) {
        this.bookingDateMonth.setText(bookingDateMonth);
    }

    public void setBookingDateYear(String bookingDateYear) {
        this.bookingDateYear.setText(bookingDateYear);
    }

    public void setDueDateDay(String dueDateDay) {
        this.dueDateDay.setText(dueDateDay);
    }

    public void setDueDateMonth(String dueDateMonth) {
        this.dueDateMonth.setText(dueDateMonth);
    }

    public void setDueDateYear(String dueDateYear) {
        this.dueDateYear.setText(dueDateYear);
    }

    public String getMemberIdTextField() {
        return memberIdTextField.getText().trim();
    }

    public String getBookingDateDay() {
        return bookingDateDay.getText().trim();
    }

    public String getBookingDateMonth() {
        return bookingDateMonth.getText().trim();
    }

    public String getBookingDateYear() {
        return bookingDateYear.getText().trim();
    }

    public String getDueDateDay() {
        return dueDateDay.getText().trim();
    }

    public String getDueDateMonth() {
        return dueDateMonth.getText().trim();
    }

    public String getDueDateYear() {
        return dueDateYear.getText().trim();
    }

    public String getIsbnTextField() {
        return isbnTextField.getText().trim();
    }

    public void autoTodayDate(ActionEvent actionEvent) {
        long mills = System.currentTimeMillis();
        Date currentDate = new Date(mills);
        String[] dates = currentDate.toString().split("-");
        setBookingDateYear(dates[0]);
        setBookingDateMonth(dates[1]);
        setBookingDateDay(dates[2]);
    }

    public void autoDueDate(ActionEvent actionEvent) {
        try {
            String borrowString =getBookingDateYear() + "-"
                    +getBookingDateMonth()+ "-"
                    +getBookingDateDay();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateFormat.parse(borrowString));
            calendar.add(Calendar.DATE,7);
            String due = dateFormat.format(calendar.getTime());
            String[] dates = due.split("-");
            setDueDateYear(dates[0]);
            setDueDateMonth(dates[1]);
            setDueDateDay(dates[2]);
        } catch (ParseException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Borrow Date Error");
            alert.setContentText("Please Fill The Borrow Date First");
            alert.showAndWait();
        }
    }
}
