package Podcast;

import javafx.application.Platform;
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
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Podcast implements Initializable {
    @FXML
    public Pane displayPane;
    @FXML
    public Pane playerPane;
    @FXML
    private Button mainButton, SearchButton, PlayerButton;
    @FXML
    private SplitPane podcastPane;
    @FXML
    private MediaView mediaView = new MediaView();
    private MediaPlayer mediaPlayer;
    private Media media;
    @FXML
    private ImageView imageView;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Slider seekBar;


    public void MainButtonClicked(ActionEvent actionEvent) throws IOException {
        Parent mainPane = FXMLLoader.load(getClass().getResource("../Main/Main.fxml"));
        podcastPane.getItems().setAll(mainPane);
    }

    public void SearchButtonClicked(ActionEvent actionEvent) throws IOException {
        Parent searchPane = FXMLLoader.load(getClass().getResource("../Search/Search.fxml"));
        podcastPane.getItems().setAll(searchPane);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Receives the cover art from the images folder and sets it to the ImageView.
        Image coverArt = new Image(new File("res/images/SampleImage.jpg").toURI().toString());
        imageView.setImage(coverArt);

        //Receives the audio from the audio folder and plays it upon request.
        String path = new File("res/audio/SampleMedia.mp3").getAbsolutePath();
        media = new Media(new File(path).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.setAutoPlay(false);

        //Updates the current time of the currently playing media.
        mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                updatedValues();
            }
        });

        //Allows the user to seek through the playing media
        seekBar.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (seekBar.isPressed()){
                    mediaPlayer.seek(mediaPlayer.getMedia().getDuration().multiply(seekBar.getValue() / 100));
                }
            }
        });

        volumeSlider.setValue(mediaPlayer.getVolume() * 100);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                mediaPlayer.setVolume(volumeSlider.getValue() / 100);
            }
        });
    }

    //Updates the value of the seekBar to be in sync with the current time of the media.
    protected void updatedValues(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                seekBar.setValue(mediaPlayer.getCurrentTime().toMillis() / mediaPlayer.getTotalDuration().toMillis() * 100);
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
