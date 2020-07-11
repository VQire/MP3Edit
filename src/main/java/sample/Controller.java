package sample;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    public GridPane gridPane;
    @FXML
    public javafx.scene.control.Button chooseFileButton;

    public static File chosenFile;
    private int numberOfLabels;
    public static String[] labels;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gridPane.setPadding(new Insets(10,10,10,10));
        gridPane.setGridLinesVisible(true);
        gridPane.setVgap(10);
        gridPane.setHgap(20);
        String[] tmp_labels = {"Tytul","Artysta","Album","Rok_wydania","Numer_utworu","Gatunek"};
        labels = tmp_labels;

        int counter = 0;
        for (String label : labels){
            Text text = new Text(label);
            gridPane.add(text,0,counter);
            counter++;
        }
        this.numberOfLabels = counter;

        for (int i = 0; i < counter; i++){
            TextField textField = new TextField();
            textField.setId("textfield"+labels[i]);
            //System.out.println("textfield"+labels[i]);
            textField.setDisable(true);
            gridPane.add(textField,1,i);
        }

        Button selectFile = new Button("Wybierz plik");
        selectFile.setId("selectFileButton");
        selectFile.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                Window stage = ((Node) event.getSource()).getScene().getWindow();
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 files","*.mp3"));
                File chosen = fileChooser.showOpenDialog(stage);
                if (chosen != null){
                    chosenFile = chosen;
                    unlockTextFields();
                }
                TextField tmp = (TextField) gridPane.lookup("#textfield"+labels[0]);
            }
        });
        gridPane.add(selectFile,0,counter+1);
        Button confirmChanges = new Button("Zatwierdź zmiany");
        confirmChanges.setId("confirmChangesButton");
        gridPane.add(confirmChanges,1,counter+1);

        Image image = new Image("nope.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(210);
        imageView.setFitWidth(210);
        gridPane.add(imageView,2,0,1,7);

    }

    private void unlockTextFields(){
        for (int i = 0;i<this.numberOfLabels;i++){
            TextField tmp = (TextField) gridPane.lookup("#textfield"+labels[i]);
            if(tmp != null){
                tmp.setDisable(false);
            }
        }
    }


}
