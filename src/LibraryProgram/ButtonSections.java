package LibraryProgram;

import javafx.scene.control.Button;
import javafx.scene.text.TextAlignment;

public class ButtonSections extends Button {

    public ButtonSections(String s) {
        super(s);
        setPrefHeight(70);
        setPrefWidth(180);
        setMinHeight(70); // we made the min height for the scroll of the grid pane
        setStyle(
                "  -fx-text-fill:  #4F739E;" +
                        "    -fx-font-family: \"Arial Narrow\";" +
                        "    -fx-font-weight: bold;" +
                        "-fx-font-size: 34;" +
                        " -fx-background-color:#2D333F;" +
                        "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");

        setOnMouseExited(mouseEvent -> {
            setStyle(
                "  -fx-text-fill:  #4F739E;" +
                        "    -fx-font-family: \"Arial Narrow\";" +
                        "    -fx-font-weight: bold;" +
                        "-fx-font-size: 34;" +
                        " -fx-background-color: #2D333F;" +
                        "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );"
        );});

        setOnMouseEntered(mouseEvent -> {
            setStyle(" -fx-background-color: #4F739E;" +
                    " -fx-font-family: \"Arial Narrow\";" +
                    "    -fx-text-fill:#2D333F;\n" +
                    "    -fx-font-weight:bold;" +
                    "-fx-font-size: 34");
        });
        textAlignmentProperty().set(TextAlignment.CENTER);
    }
}
