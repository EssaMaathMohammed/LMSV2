package LibraryProgram;


import javafx.scene.control.TextField;

public class SearchDialogController {


    public TextField dayFromDate;
    public TextField monthFromDate;
    public TextField yearFromDate;
    public TextField dayToDate;
    public TextField monthToDate;
    public TextField yearToDate;

    public String getDayFromDate() {
        return dayFromDate.getText().trim();
    }

    public String getMonthFromDate() {
        return monthFromDate.getText().trim();
    }

    public String getYearFromDate() {
        return yearFromDate.getText().trim();
    }

    public String getDayToDate() {
        return dayToDate.getText().trim();
    }

    public String getMonthToDate() {
        return monthToDate.getText().trim();
    }

    public String getYearToDate() {
        return yearToDate.getText().trim();
    }
}
