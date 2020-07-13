package sample;

import com.mpatric.mp3agic.*;
import com.sun.javafx.collections.MappingChange;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    public GridPane gridPane;

    public static File chosenFile;
    public static File coverImage;
    public static byte[] prevImageBytes;
    public static String prevImageMimeType;
    //stores number of existings labels - modified tags (excluding image)
    private int numberOfLabels;
    public static String[] labels;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gridPane.setPadding(new Insets(10,10,10,10));
        //gridPane.setGridLinesVisible(true);
        gridPane.setVgap(10);
        gridPane.setHgap(20);
        String[] tmp_labels = {"Tytul","Artysta","Album","Rok_wydania","Numer_utworu","Gatunek","Nazwa_pliku"};
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
                    lockOnTextFields(false);
                    getMP3Tags();
                    Label info = (Label)gridPane.lookup("#infoLabel");
                    info.setText("wypelnij pola");
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
        final ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setId("albumCover");
        imageView.setFitHeight(230);
        imageView.setFitWidth(210);
        imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Window stage = ((Node) event.getSource()).getScene().getWindow();
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JPG images", "*.jpg"),new FileChooser.ExtensionFilter("PNG images","*.png"));
                File chosen = fileChooser.showOpenDialog(stage);
                if (chosen != null){
                    coverImage = chosen;
                    Image newImage = new Image(coverImage.toURI().toString());
                    imageView.setImage(newImage);
                }
            }

        });
        imageView.setDisable(true);
        gridPane.add(imageView,2,0,1,7);

        Label infoText = new Label("Wybierz plik");
        infoText.setId("infoLabel");
        gridPane.add(infoText,2,counter+1);

    }

    //changes possibility of editing TextFields with ID3 tags
    //boolean states if field is to remain locked
    private void lockOnTextFields(boolean lock_on){
        for (int i = 0;i<this.numberOfLabels;i++){
            TextField tmp = (TextField) gridPane.lookup("#textfield"+labels[i]);
            if(tmp != null){
                tmp.setDisable(lock_on);
            }
        }
    }

    //gets TextFields to display info about current MP3File
    private void getMP3Tags(){
        try {
            if(chosenFile!= null){
                Mp3File mp3File = new Mp3File(chosenFile);
                if (mp3File != null) {
                    if (mp3File.hasId3v1Tag()) {
                        getID3v1Tags(mp3File);
                    } else if (mp3File.hasId3v2Tag()) {
                        getID3v2Tags(mp3File);
                    } else {
                        //System.out.println("has no tags");
                    }
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

    //sets Tags basing on TextFields (and image)
    private void setMP3Tags(){
        try {
            //opens mp3 file for editing
            Mp3File mp3File = new Mp3File(chosenFile);
            //checks if file was opened correctly
            if (mp3File != null){
                //checks if mp3 file uses ID3v1 Tags
                if(mp3File.hasId3v1Tag()){
                    setID3v1Tags(mp3File);
                }
                //checks if mp3 file uses ID3v2 Tags
                else if(mp3File.hasId3v2Tag())
                {
                    setID3v2Tags(mp3File);
                }
                //if file has no tags creates ID3v2 tags
                else{
                    setID3v2Tags(mp3File);
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

    private void cleanup(){
        TextField textFieldTitle = (TextField) gridPane.lookup("#textfieldTytul");
        textFieldTitle.setText("");
        TextField textFieldArtist = (TextField) gridPane.lookup("#textfieldArtysta");
        textFieldArtist.setText("");
        TextField textFieldAlbum = (TextField) gridPane.lookup("#textfieldAlbum");
        textFieldAlbum.setText("");
        TextField textFieldYear = (TextField) gridPane.lookup("#textfieldRok_wydania");
        textFieldYear.setText("");
        TextField textFieldNumber = (TextField) gridPane.lookup("#textfieldNumer_utworu");
        textFieldNumber.setText("");
        TextField textFieldGenre = (TextField) gridPane.lookup("#textfieldGatunek");
        textFieldGenre.setText("");
        chosenFile = null;
        coverImage = null;
        prevImageMimeType = null;
        prevImageBytes = null;
        Image image = new Image("nope.png");
        ImageView imageView = (ImageView) gridPane.lookup("#albumCover");
        imageView.setImage(image);
        lockOnTextFields(true);
    }

    private void getID3v1Tags(Mp3File mp3File){
        System.out.println("has ID3v1");

        //gets existing tags ID3v1
        ID3v1 id3v1Tags = mp3File.getId3v1Tag();
        //displays Title
        TextField textFieldTitle = (TextField) gridPane.lookup("#textfieldTytul");
        textFieldTitle.setText(id3v1Tags.getTitle());
        //displays Artist name
        TextField textFieldArtist = (TextField) gridPane.lookup("#textfieldArtysta");
        textFieldArtist.setText(id3v1Tags.getArtist());
        //displays Album name
        TextField textFieldAlbum = (TextField) gridPane.lookup("#textfieldAlbum");
        textFieldAlbum.setText(id3v1Tags.getAlbum());
        //displays Year of publication
        TextField textFieldYear = (TextField) gridPane.lookup("#textfieldRok_wydania");
        textFieldYear.setText(id3v1Tags.getYear());
        //displays Number/index of track
        TextField textFieldNumber = (TextField) gridPane.lookup("#textfieldNumer_utworu");
        textFieldNumber.setText(id3v1Tags.getTrack());
        //displays genre
        TextField textFieldGenre = (TextField) gridPane.lookup("#textfieldGatunek");
        textFieldGenre.setText(String.valueOf(id3v1Tags.getGenre())+ " " + id3v1Tags.getGenreDescription() +")");
    }

    private void getID3v2Tags(Mp3File mp3File){
        System.out.println("has ID3v2");

        //gets existing tags ID3v2
        ID3v2 id3v2Tags = mp3File.getId3v2Tag();
        //displays Title
        TextField textFieldTitle = (TextField) gridPane.lookup("#textfieldTytul");
        textFieldTitle.setText(id3v2Tags.getTitle());
        //displays Artist name
        TextField textFieldArtist = (TextField) gridPane.lookup("#textfieldArtysta");
        textFieldArtist.setText(id3v2Tags.getArtist());
        //displays Album name
        TextField textFieldAlbum = (TextField) gridPane.lookup("#textfieldAlbum");
        textFieldAlbum.setText(id3v2Tags.getAlbum());
        //displays Year of publication
        TextField textFieldYear = (TextField) gridPane.lookup("#textfieldRok_wydania");
        textFieldYear.setText(id3v2Tags.getYear());
        //displays Number/index of track
        TextField textFieldNumber = (TextField) gridPane.lookup("#textfieldNumer_utworu");
        textFieldNumber.setText(id3v2Tags.getTrack());
        //displays genre
        TextField textFieldGenre = (TextField) gridPane.lookup("#textfieldGatunek");
        textFieldGenre.setText(String.valueOf(id3v2Tags.getGenre())+ " " + id3v2Tags.getGenreDescription());
        //gets Album cover, if cover exists - displays it and saves for possible reuse

        byte[] cover = id3v2Tags.getAlbumImage();
        if(cover != null){
            Image coverImage = new Image(new ByteArrayInputStream(cover));
            ImageView tmp = (ImageView) gridPane.lookup("#albumCover");
            tmp.setImage(coverImage);
            prevImageBytes = cover;
            prevImageMimeType = id3v2Tags.getAlbumImageMimeType();
        }
        ImageView albumCover = (ImageView) gridPane.lookup("#albumCover");
        //ulnocks filechooser for images
        albumCover.setDisable(false);
    }

    private void setID3v2Tags(Mp3File mp3File) throws IOException, NotSupportedException {
        TextField textFieldFileName = (TextField) gridPane.lookup("#textfieldNazwa_pliku");
        String fileName = textFieldFileName.getText();
        if(!fileName.isEmpty()) {
            //creates tags for mp3 file
            ID3v2 id3v2Tags = new ID3v23Tag();
            //sets Title
            TextField textFieldTitle = (TextField) gridPane.lookup("#textfieldTytul");
            id3v2Tags.setTitle(textFieldTitle.getText());
            //sets Artist name
            TextField textFieldArtist = (TextField) gridPane.lookup("#textfieldArtysta");
            id3v2Tags.setArtist(textFieldArtist.getText());
            //sets Album name
            TextField textFieldAlbum = (TextField) gridPane.lookup("#textfieldAlbum");
            id3v2Tags.setAlbum(textFieldAlbum.getText());
            //sets Year of publication
            TextField textFieldYear = (TextField) gridPane.lookup("#textfieldRok_wydania");
            id3v2Tags.setYear(textFieldYear.getText());
            //sets Index of track
            TextField textFieldNumber = (TextField) gridPane.lookup("#textfieldNumer_utworu");
            id3v2Tags.setTrack(textFieldNumber.getText());
            //sets Genre
            TextField textFieldGenre = (TextField) gridPane.lookup("#textfieldGatunek");
            id3v2Tags.setGenre(Integer.parseInt(textFieldGenre.getText().split(" ")[0]));
            //checks if image for cover was chosen


            if (coverImage != null) {
                //converts chosen file to byte array used in library
                byte[] imageBytes = new byte[(int) coverImage.length()];
                FileInputStream fileInputStream = new FileInputStream(coverImage);
                fileInputStream.read(imageBytes);
                fileInputStream.close();
                //System.out.println(new MimetypesFileTypeMap().getContentType(coverImage));
                //sets new cover image
                id3v2Tags.setAlbumImage(imageBytes, new MimetypesFileTypeMap().getContentType(coverImage));
            } else {
                //sets existing cover (previously saved) as cover in new tags
                id3v2Tags.setAlbumImage(prevImageBytes, prevImageMimeType);
            }
            //sets created tags to mp3 file
            mp3File.setId3v2Tag(id3v2Tags);
            //saves the file
            String parentOfFile = chosenFile.getParent();
            File tmp = new File(parentOfFile + "\\" + fileName + ".mp3");
            if (tmp.exists()) {
                int indexOfFile = 0;
                for (int i = 1; ; i++) {
                    tmp = new File(parentOfFile + "\\" + fileName + " (" + String.valueOf(i) + ")" + ".mp3");
                    if (!tmp.exists()) {
                        mp3File.save(parentOfFile + "\\" + fileName + " (" + String.valueOf(i) + ")" + ".mp3");
                        break;
                    }
                }
            } else {
                mp3File.save(parentOfFile + "\\" + fileName + ".mp3");
            }
            cleanup();
        }
        else{
            Label info = (Label)gridPane.lookup("#infoLabel");
            info.setText("ustaw nazwę pliku wynikowego");
        }
    }

    private void setID3v1Tags(Mp3File mp3File) throws IOException, NotSupportedException {
        //gets Tags from file
        ID3v1 id3v1Tags = mp3File.getId3v1Tag();
        //sets Title
        TextField textFieldTitle = (TextField) gridPane.lookup("#textfieldTytul");
        id3v1Tags.setTitle(textFieldTitle.getText());
        //sets Artist name
        TextField textFieldArtist = (TextField) gridPane.lookup("#textfieldArtysta");
        id3v1Tags.setArtist(textFieldArtist.getText());
        //sets Album name
        TextField textFieldAlbum = (TextField) gridPane.lookup("#textfieldAlbum");
        id3v1Tags.setAlbum(textFieldAlbum.getText());
        //sets Year of publication
        TextField textFieldYear = (TextField) gridPane.lookup("#textfieldRok_wydania");
        id3v1Tags.setYear(textFieldYear.getText());
        //sets Index of track
        TextField textFieldNumber = (TextField) gridPane.lookup("#textfieldNumer_utworu");
        id3v1Tags.setTrack(textFieldNumber.getText());
        //sets Genre
        TextField textFieldGenre = (TextField) gridPane.lookup("#textfieldGatunek");
        id3v1Tags.setGenre(Integer.parseInt(textFieldGenre.getText().split(" ")[0]));
        //saves the file
        String parentOfFile = chosenFile.getParent();
        File tmp = new File(parentOfFile+"\\"+id3v1Tags.getArtist()+" "+id3v1Tags.getTitle()+".mp3");
        if(tmp.exists()){
            int indexOfFile = 0;
            for (int i = 1;;i++){
                tmp = new File(parentOfFile+"\\"+id3v1Tags.getArtist()+" "+id3v1Tags.getTitle()+" ("+String.valueOf(i)+")"+".mp3");
                if(!tmp.exists()){
                    mp3File.save(parentOfFile+"\\"+id3v1Tags.getArtist()+" "+id3v1Tags.getTitle()+" ("+String.valueOf(i)+")"+".mp3");
                    break;
                }
            }
        }
        else {
            mp3File.save(parentOfFile + "\\" + id3v1Tags.getArtist() + " " + id3v1Tags.getTitle() + ".mp3");
        }
        cleanup();
    }
}
