package LibraryProgram;

import javafx.scene.control.TextField;

public class MemberInsertDialogController {
    public TextField firstName;
    public TextField MiddleName;
    public TextField lastName;
    public TextField college;
    public TextField department;
    public TextField type;
    public TextField day;
    public TextField month;
    public TextField year;
    public TextField m_id;

    public String getFirstName() {
        return firstName.getText().trim();
    }

    public String getMiddleName() {
        return MiddleName.getText().trim();
    }

    public String  getLastName() {
        return lastName.getText().trim();
    }

    public String getCollege() {
        return college.getText().trim();
    }

    public String getDepartment() {
        return department.getText().trim();
    }

    public String getType() {
        return type.getText().trim();
    }

    public String getDay() {
        return day.getText().trim();
    }

    public String getMonth() {
        return month.getText().trim();
    }

    public String getYear() {
        return year.getText().trim();
    }

    public String getM_id() {
        return m_id.getText().trim();
    }
}
