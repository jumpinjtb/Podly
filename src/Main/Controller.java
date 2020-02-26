package Main;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private MediaView mediaView;
    private MediaPlayer mediaPlayer;
    private Media media;
    private Button mainButton, SearchButton, PlayerButton;
    private Pane mainPane;


    public void PlayerButtonClicked(ActionEvent actionEvent) throws IOException {
    // calling the main pane to reload with the podcast fxml.
        // https://www.youtube.com/watch?v=RJOza3XQk34
        SplitPane podPane = FXMLLoader.load(getClass().getResource("../Podcast/Podcast.fxml"));
        mainPane.getChildren().setAll(podPane);
    }

    public void SearchButtonClicked(ActionEvent actionEvent) throws IOException {
        Pane searchPane = FXMLLoader.load(getClass().getResource("../Search/Search.fxml"));
        mainPane.getChildren().setAll(searchPane);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String path = new File("MusicFile.mp3").getAbsolutePath();
        media = new Media(new File(path).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.setAutoPlay(true);

    }

}
