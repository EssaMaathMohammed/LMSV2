package LibraryProgram;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class AddSectionDialogController {
    public TextField nameTextField;

    @FXML
    public ButtonSections newButton() {

        String name = nameTextField.getText().trim();

        if (name.isEmpty()){
            System.out.println("enter all the info");
        }else {
            return new ButtonSections(name);
        }
        return null;
    }
}
