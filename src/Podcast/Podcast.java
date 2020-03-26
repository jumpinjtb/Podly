package Podcast;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Podcast implements Initializable {
    @FXML
    private Button mainButton, SearchButton, PlayerButton;
    @FXML
    private SplitPane podcastPane;
    @FXML
    private MediaView mediaView = new MediaView();
    private MediaPlayer mediaPlayer;
    private Media media;
    @FXML
    Slider volumeSlider;


    public void MainButtonClicked(ActionEvent actionEvent) throws IOException {
        Parent mainPane = FXMLLoader.load(getClass().getResource("Main.fxml"));
        podcastPane.getItems().setAll(mainPane);
        
    }

    public void SearchButtonClicked(ActionEvent actionEvent) throws IOException {
        Parent searchPane = FXMLLoader.load(getClass().getResource("../Search/Search.fxml"));
        podcastPane.getItems().setAll(searchPane);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String path = new File("src/Main/MusicFile.mp3").getAbsolutePath();
        media = new Media(new File(path).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.setAutoPlay(true);
        volumeSlider.setValue(mediaPlayer.getVolume() * 100);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                mediaPlayer.setVolume(volumeSlider.getValue() / 100);
            }
        });
    }

    public void play(ActionEvent event){
        mediaPlayer.play();
    }
    public void pause(ActionEvent event){
        mediaPlayer.pause();
    }
}
