package LibraryProgram;

import javafx.scene.control.Button;
import javafx.scene.text.TextAlignment;

public class ButtonSections extends Button {

    public ButtonSections(String s) {
        super(s);
        setPrefHeight(70);
        setPrefWidth(200);
        setMinHeight(70); // we made the min height for the scroll of the grid pane
        setStyle(
                "  -fx-text-fill:  #ABEEFE;" +
                        "    -fx-font-family: \"Times New Roman\";" +
                        "    -fx-font-weight: bold;" +
                        "-fx-font-size: 25;" +
                        " -fx-background-color:#354F91;" +
                        "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");

        setOnMouseExited(mouseEvent -> {
            setStyle(
                "  -fx-text-fill:  #ABEEFE;" +
                        "    -fx-font-family: \"Times New Roman\";" +
                        "    -fx-font-weight: bold;" +
                        "-fx-font-size: 25;" +
                        " -fx-background-color: #354F91;" +
                        "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );"
        );});

        setOnMouseEntered(mouseEvent -> {
            setStyle(" -fx-background-color: #ABEEFE;" +
                    " -fx-font-family: \"Times New Roman\";" +
                    "    -fx-text-fill:#354F91;\n" +
                    "    -fx-font-weight:bold;" +
                    "-fx-font-size: 25");
        });
        textAlignmentProperty().set(TextAlignment.CENTER);
    }
}
