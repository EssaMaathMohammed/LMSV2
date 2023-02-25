package LibraryProgram.databaseClasses;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;

import java.sql.Date;

public class Member {

    private SimpleStringProperty m_id;
    private SimpleStringProperty first_name;
    private SimpleStringProperty middle_name;
    private SimpleStringProperty last_name;
    private SimpleStringProperty type;
    private SimpleStringProperty college;
    private SimpleStringProperty department;
    private Date exp_date;
    private int timesBorrowed;

    public Member(String m_id, String first_name, String middle_name, String last_name,
                  String type, String college, String department, Date exp_date,int timesBorrowed) {
        this.m_id = new SimpleStringProperty(m_id);
        this.first_name = new SimpleStringProperty(first_name);
        this.middle_name = new SimpleStringProperty(middle_name);
        this.last_name = new SimpleStringProperty(last_name);
        this.type = new SimpleStringProperty(type);
        this.college = new SimpleStringProperty(college);
        this.department = new SimpleStringProperty(department);
        this.exp_date = exp_date;
        this.timesBorrowed = timesBorrowed;
    }

    public int getTimesBorrowed(){
        return timesBorrowed;
    }

    public void setTimesBorrowed(int timesBorrowed) {
        this.timesBorrowed = timesBorrowed;
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

    public String getFirst_name() {
        return first_name.get();
    }

    public SimpleStringProperty first_nameProperty() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name.set(first_name);
    }

    public String getMiddle_name() {
        return middle_name.get();
    }

    public SimpleStringProperty middle_nameProperty() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name.set(middle_name);
    }

    public String getLast_name() {
        return last_name.get();
    }

    public SimpleStringProperty last_nameProperty() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name.set(last_name);
    }

    public String getType() {
        return type.get();
    }

    public SimpleStringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public String getCollege() {
        return college.get();
    }

    public SimpleStringProperty collegeProperty() {
        return college;
    }

    public void setCollege(String college) {
        this.college.set(college);
    }

    public String getDepartment() {
        return department.get();
    }

    public SimpleStringProperty departmentProperty() {
        return department;
    }

    public void setDepartment(String department) {
        this.department.set(department);
    }

    public Date getExp_date() {
        return exp_date;
    }

    public void setExp_date(Date exp_date) {
        this.exp_date = exp_date;
    }
}
