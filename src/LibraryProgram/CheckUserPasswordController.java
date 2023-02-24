package LibraryProgram;

import javafx.scene.control.PasswordField;

public class CheckUserPasswordController {
    public PasswordField passwordTextField;

    public String getPasswordTextField() {
        return passwordTextField.getText().trim();
    }
}
