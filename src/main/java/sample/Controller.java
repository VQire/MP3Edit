package sample;

import com.mpatric.mp3agic.*;
import javafx.event.ActionEvent;
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    public GridPane gridPane;

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
            System.out.println("textfield"+labels[i]);
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
                    getMP3Tags();
                }
            }
        });
        gridPane.add(selectFile,0,counter+1);


        Button confirmChanges = new Button("Zatwierdź zmiany");
        confirmChanges.setId("confirmChangesButton");
        confirmChanges.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setMP3Tags();
            }
        });
        gridPane.add(confirmChanges,1,counter+1);

        Image image = new Image("nope.png");
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setId("albumCover");
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

    private void getMP3Tags(){
        try {
            Mp3File mp3File = new Mp3File(chosenFile);
            if (mp3File != null){
                if(mp3File.hasId3v1Tag()){
                    System.out.println("has ID3v1");
                    ID3v1 id3v1Tags = mp3File.getId3v1Tag();
                    TextField textFieldTitle = (TextField) gridPane.lookup("#textfieldTytul");
                    System.out.println(id3v1Tags.getTitle());
                    textFieldTitle.setText(id3v1Tags.getTitle());
                    TextField textFieldArtist = (TextField) gridPane.lookup("#textfieldArtysta");
                    System.out.println(id3v1Tags.getArtist());
                    textFieldArtist.setText(id3v1Tags.getArtist());
                    TextField textFieldAlbum = (TextField) gridPane.lookup("#textfieldAlbum");
                    System.out.println(id3v1Tags.getAlbum());
                    textFieldAlbum.setText(id3v1Tags.getAlbum());
                    TextField textFieldYear = (TextField) gridPane.lookup("#textfieldRok_wydania");
                    System.out.println(id3v1Tags.getYear());
                    textFieldYear.setText(id3v1Tags.getYear());
                    TextField textFieldNumber = (TextField) gridPane.lookup("#textfieldNumer_utworu");
                    System.out.println(id3v1Tags.getTrack());
                    textFieldNumber.setText(id3v1Tags.getTrack());
                    TextField textFieldGenre = (TextField) gridPane.lookup("#textfieldGatunek");
                    System.out.println(id3v1Tags.getGenre());
                    textFieldGenre.setText(String.valueOf(id3v1Tags.getGenre())+ " " + id3v1Tags.getGenreDescription() +")");
                }
                else if(mp3File.hasId3v2Tag())
                {
                    System.out.println("has ID3v2");
                    ID3v2 id3v2Tags = mp3File.getId3v2Tag();
                    TextField textFieldTitle = (TextField) gridPane.lookup("#textfieldTytul");
                    System.out.println(id3v2Tags.getTitle());
                    textFieldTitle.setText(id3v2Tags.getTitle());
                    TextField textFieldArtist = (TextField) gridPane.lookup("#textfieldArtysta");
                    System.out.println(id3v2Tags.getArtist());
                    textFieldArtist.setText(id3v2Tags.getArtist());
                    TextField textFieldAlbum = (TextField) gridPane.lookup("#textfieldAlbum");
                    System.out.println(id3v2Tags.getAlbum());
                    textFieldAlbum.setText(id3v2Tags.getAlbum());
                    TextField textFieldYear = (TextField) gridPane.lookup("#textfieldRok_wydania");
                    System.out.println(id3v2Tags.getYear());
                    textFieldYear.setText(id3v2Tags.getYear());
                    TextField textFieldNumber = (TextField) gridPane.lookup("#textfieldNumer_utworu");
                    System.out.println(id3v2Tags.getTrack());
                    textFieldNumber.setText(id3v2Tags.getTrack());
                    TextField textFieldGenre = (TextField) gridPane.lookup("#textfieldGatunek");
                    System.out.println(id3v2Tags.getGenre());
                    textFieldGenre.setText(String.valueOf(id3v2Tags.getGenre())+ " " + id3v2Tags.getGenreDescription());
                    byte[] cover = id3v2Tags.getAlbumImage();
                    if(cover != null){
                        System.out.println(id3v2Tags.getAlbumImageMimeType());
                        Image coverImage = new Image(new ByteArrayInputStream(cover));
                        ImageView tmp = (ImageView) gridPane.lookup("#albumCover");
                        tmp.setImage(coverImage);
                    }
                }
                else
                {
                    System.out.println("has no tags");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedTagException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        }
    }

    private void setMP3Tags(){
        try {
            Mp3File mp3File = new Mp3File(chosenFile);
            if (mp3File != null){
                if(mp3File.hasId3v1Tag()){
                    ID3v1 id3v1Tags = mp3File.getId3v1Tag();
                    TextField textFieldTitle = (TextField) gridPane.lookup("#textfieldTytul");
                    id3v1Tags.setTitle(textFieldTitle.getText());
                    TextField textFieldArtist = (TextField) gridPane.lookup("#textfieldArtysta");
                    id3v1Tags.setArtist(textFieldArtist.getText());
                    TextField textFieldAlbum = (TextField) gridPane.lookup("#textfieldAlbum");
                    id3v1Tags.setAlbum(textFieldAlbum.getText());
                    TextField textFieldYear = (TextField) gridPane.lookup("#textfieldRok_wydania");
                    id3v1Tags.setYear(textFieldYear.getText());
                    TextField textFieldNumber = (TextField) gridPane.lookup("#textfieldNumer_utworu");
                    id3v1Tags.setTrack(textFieldNumber.getText());
                    TextField textFieldGenre = (TextField) gridPane.lookup("#textfieldGatunek");
                    id3v1Tags.setGenre(Integer.parseInt(textFieldGenre.getText().split(" ")[0]));
                    //System.out.print(Integer.parseInt(textFieldGenre.getText()));
                    //System.out.print(chosenFile.getParent());
                    //File tmp = new File(chosenFile.getParent()+"\\"+id3v1Tags.getArtist()+" "+id3v1Tags.getTitle()+".mp3");
                    String parentOfFile = chosenFile.getParent();
//                    if (tmp.exists()){
//                        System.out.print("jest");
//                        chosenFile = new File("tmp.mp3");
//                        tmp.delete();
//                    }
                    mp3File.save(parentOfFile+"\\"+id3v1Tags.getArtist()+" "+id3v1Tags.getTitle()+".mp3");
                }
                else if(mp3File.hasId3v2Tag())
                {
                    ID3v2 id3v2Tags = mp3File.getId3v2Tag();
                    TextField textFieldTitle = (TextField) gridPane.lookup("#textfieldTytul");
                    id3v2Tags.setTitle(textFieldTitle.getText());
                    TextField textFieldArtist = (TextField) gridPane.lookup("#textfieldArtysta");
                    id3v2Tags.setArtist(textFieldArtist.getText());
                    TextField textFieldAlbum = (TextField) gridPane.lookup("#textfieldAlbum");
                    id3v2Tags.setAlbum(textFieldAlbum.getText());
                    TextField textFieldYear = (TextField) gridPane.lookup("#textfieldRok_wydania");
                    id3v2Tags.setYear(textFieldYear.getText());
                    TextField textFieldNumber = (TextField) gridPane.lookup("#textfieldNumer_utworu");
                    id3v2Tags.setTrack(textFieldNumber.getText());
                    TextField textFieldGenre = (TextField) gridPane.lookup("#textfieldGatunek");
                    id3v2Tags.setGenre(Integer.parseInt(textFieldGenre.getText().split(" ")[0]));
                    //System.out.print(Integer.parseInt(textFieldGenre.getText().split(" ")[0]));
                    //System.out.print(chosenFile.getParent());
                    //File tmp = new File(chosenFile.getParent()+"\\"+id3v1Tags.getArtist()+" "+id3v1Tags.getTitle()+".mp3");
                    String parentOfFile = chosenFile.getParent();
//                    if (tmp.exists()){
//                        System.out.print("jest");
//                        chosenFile = new File("tmp.mp3");
//                        tmp.delete();
//                    }
                    mp3File.save(parentOfFile+"\\"+id3v2Tags.getArtist()+" "+id3v2Tags.getTitle()+".mp3");
                }
                else
                {
                    System.out.println("has no tags");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedTagException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        } catch (NotSupportedException e) {
            e.printStackTrace();
        }
    }

}
