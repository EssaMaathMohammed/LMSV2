package LibraryProgram;

import javafx.scene.control.TextField;

public class InsertNewUserContoller {
    public TextField idTextField;
    public TextField userNameTextField;
    public TextField typeTextField;
    public TextField passwordTextField;

    public Integer getIdTextField() {
        return Integer.parseInt(idTextField.getText().trim());
    }

    public String getUserNameTextField() {
        return userNameTextField.getText().trim();
    }

    public String getTypeTextField() {
        return typeTextField.getText().trim();
    }

    public String getPasswordTextField() {
        return passwordTextField.getText().trim();
    }
}
