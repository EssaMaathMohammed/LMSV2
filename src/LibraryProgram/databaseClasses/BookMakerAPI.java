package LibraryProgram.databaseClasses;



import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BookMakerAPI {
    private final InputStream bookInputStream;
    private String bookName;
    private String isbn;
    private String image;
    private String author;
    private String datePublished;
    private String publisher;

    public BookMakerAPI(InputStream bookInputStream) {
        this.bookInputStream = bookInputStream;
        readBookInputStream();
    }

    private void readBookInputStream(){
        try {

            StringBuilder content;

            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(bookInputStream))) {

                String line;
                content = new StringBuilder();

                while ((line = in.readLine()) != null) {
                    content.append(line);
                }
            }
            String separatedString = content.toString();

            separatedString = separatedString.replaceAll("[{}]","");
            separatedString = separatedString.replaceAll("\",\"", "\" \"::\" \"");
            separatedString = separatedString.replaceAll("\":\"", "\": \"::\" \"");
            separatedString = separatedString.replace(",\"date_","\": \"::\" \":");
            separatedString = separatedString.replace("\"authors\":[","\"authors\": \"::\" ");
            separatedString = separatedString.replaceAll("\"],\"", "\" \"::\" \"");
            separatedString = separatedString.replaceAll("],\"","\" \"::\" \"");
            String[] separatedArray = separatedString.split("\"::\"");

            String value;

            for (int counter = 0; counter < separatedArray.length; counter++) {
                value = separatedArray[counter];

                switch (value) {
                    case " \"title_long\": " :
                    case " \"title\": " : bookName = lengthChecker(separatedArray[counter+1].trim());
                        break;
                    case " \"image\": " : image = lengthChecker(separatedArray[counter+1].trim());
                        break;
                    case " \"isbn13\": " : isbn = lengthChecker(separatedArray[counter+1].trim());
                        break;
                    case " \"publisher\": " : publisher = lengthChecker(separatedArray[counter+1].trim());
                        break;
                    case " \":published\": " : datePublished = lengthChecker(separatedArray[counter+1].trim());
                        break;
                    case " \"date_published\": " : datePublished = lengthChecker(separatedArray[counter+1].substring(0,14).trim());
                        break;
                    case " \"authors\": " : author = lengthChecker(separatedArray[counter+1].trim());
                        break;
                }
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("INPUT Stream Not Loading ERROR");
            alert.setContentText("The Book Doesn't Exist In the Database");
            alert.showAndWait();
        }
    }

    private String replacer(String input) {
        input = input.replaceFirst("\"","");
        input = input.substring(0,input.length()-1);
        return input;
    }

    private String lengthChecker(String value) {
        if (value.length() >= 5) {
            return replacer(value);
        }else {
            return "null";
        }
    }

    public String getBookName() {
        return bookName;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getImage() {
        return image;
    }

    public String getAuthor() {
        return author;
    }

    public String getDatePublished() {
        return datePublished;
    }

    public String getPublisher() {
        return publisher;
    }
}
