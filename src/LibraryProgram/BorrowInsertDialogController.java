package LibraryProgram;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;


public class BorrowInsertDialogController {
    public TextField borrowDateDay;
    public TextField memberIdTextField;
    public TextField borrowDateMonth;
    public TextField borrowDateYear;
    public TextField dueDateDay;
    public TextField dueDateMonth;
    public TextField dueDateYear;
    public TextField returnDateDay;
    public TextField returnDateMonth;
    public TextField returnDateYear;
    public TextField quantityTextField;
    public TextField isbnTextField;

    public void setBorrowDateDay(String borrowDateDay) {
        this.borrowDateDay.setText(borrowDateDay);
    }

    public void setBorrowDateMonth(String borrowDateMonth) {
        this.borrowDateMonth.setText(borrowDateMonth);
    }

    public void setBorrowDateYear(String borrowDateYear) {
        this.borrowDateYear.setText(borrowDateYear);
    }

    public String getBorrowDateDay() {
        return borrowDateDay.getText().trim();
    }

    public String getMemberIdString() {
        return memberIdTextField.getText().trim();
    }

    public String getBorrowDateMonth() {
        return borrowDateMonth.getText().trim();
    }

    public String getBorrowDateYear() {
        return borrowDateYear.getText().trim();
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

    public String getReturnDateDay() {
        return returnDateDay.getText().trim();
    }

    public String getReturnDateMonth() {
        return returnDateMonth.getText().trim();
    }

    public String getReturnDateYear() {
        return returnDateYear.getText().trim();
    }

    public String getQuantityString() {
        return quantityTextField.getText().trim();
    }

    public String getIsbnString() {
        return isbnTextField.getText().trim();
    }

    public void setDueDateDay(String dueDateDay) {
        this.dueDateDay.setText(dueDateDay);
    }

    public void setDueDateMonth(String  dueDateMonth) {
        this.dueDateMonth.setText(dueDateMonth);
    }

    public void setDueDateYear(String dueDateYear) {
        this.dueDateYear.setText(dueDateYear);
    }

    public void autoDueDate(ActionEvent actionEvent){
        try {
            String borrowString =getBorrowDateYear() + "-"
                    +getBorrowDateMonth()+ "-"
                    +getBorrowDateDay();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateFormat.parse(borrowString));
            calendar.add(Calendar.DATE,14);
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

    public void autoTodayDate(ActionEvent actionEvent) {
        long mills = System.currentTimeMillis();
        Date currentDate = new Date(mills);
        String[] dates = currentDate.toString().split("-");
        setBorrowDateYear(dates[0]);
        setBorrowDateMonth(dates[1]);
        setBorrowDateDay(dates[2]);
    }
}
