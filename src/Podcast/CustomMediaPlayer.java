package Podcast;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class CustomMediaPlayer {
    private MediaView mediaView = new MediaView();
    private MediaPlayer mediaPlayer;
    private Media media;
    private Slider seekBar;
    private Slider volumeSlider;

    public CustomMediaPlayer(Media media, Slider seekBar, Slider volumeSlider) {
        this.media = media;
        this.mediaPlayer = new MediaPlayer(media);
        this.seekBar = seekBar;
        this.volumeSlider = volumeSlider;

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

    protected void updatedValues(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                seekBar.setValue(mediaPlayer.getCurrentTime().toMillis() / mediaPlayer.getTotalDuration().toMillis() * 100);
            }
        });
    }

    public void play(ActionEvent event){
        mediaPlayer.seek(mediaPlayer.getMedia().getDuration().multiply(seekBar.getValue() / 100));
        mediaPlayer.play();
    }

    public void pause(ActionEvent event){
        mediaPlayer.pause();
    }

    public boolean isPlaying() {
        return mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING);
    }
}
