package LibraryProgram.databaseClasses;

import javafx.beans.property.SimpleStringProperty;

public class User {
    private SimpleStringProperty userName;
    private SimpleStringProperty password;
    private SimpleStringProperty type;

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User(String userName, String password , int id, String type) {
        this.userName =new SimpleStringProperty(userName) ;
        this.password =new SimpleStringProperty(password) ;
        this.id = id;
        this.type = new SimpleStringProperty(type);
    }
    public String getUserName() {
        return userName.get();
    }

    public SimpleStringProperty userNameProperty() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName.set(userName);
    }

    public String getPassword() {
        return password.get();
    }

    public SimpleStringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
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
}
