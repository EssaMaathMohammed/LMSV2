module LibraryManagementSystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires javafx.swing;
    requires javafx.graphics;
    requires mysql.connector.j;
    requires org.json;
    opens resources;
    opens LibraryProgram;
    opens LibraryProgram.databaseClasses;
    opens LibraryProgram.fxmlFiles;
}