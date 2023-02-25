package LibraryProgram;

import javafx.scene.control.TextField;

public class InsertBookApiDialogController {
    public TextField priceTextFieldApi;
    public TextField quantityTextFieldApi;
    public TextField isbnTextFieldApi;

    public double getPriceTextField() {
        return Double.parseDouble(priceTextFieldApi.getText().trim());
    }

    public int getQuantityTextField() {
        return Integer.parseInt(quantityTextFieldApi.getText().trim());
    }

    public long getIsbnTextField() {
        return Long.parseLong(isbnTextFieldApi.getText().trim().replaceAll("-",""));
    }
}
