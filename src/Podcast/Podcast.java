package Podcast;

import RSS.Feed;
import RSS.FeedItem;
import RSS.RSSFeedParser;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import javax.xml.transform.Result;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.stream.Stream;

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

    private static String podID = null;
    private int resultDistance = 220;


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

        if(podID != null) {
            String filename = "res/rss/" + podID + ".rss";
            RSSFeedParser parser = new RSSFeedParser(filename);
            Feed feed = null;
            try {
                feed = parser.readFeed();
            } catch (Exception e) {
                e.printStackTrace();
            }

            assert feed != null;
            List<FeedItem> episodes = feed.getEpisodes();
            int index = 0;
            for(FeedItem episode: episodes) {
                File audio = new File("res/audio/" + podID +  "/" +
                        episode.title.replace(":", "") + ".mp3").getAbsoluteFile();
                Label title = new Label(episode.title);
                Button play = new Button();
                Button download = new Button();
                Button delete = new Button();

                play.setOnAction(click1 -> {
                    media = new Media(audio.toURI().toString());
                    mediaPlayer = new MediaPlayer(media);
                    mediaView.setMediaPlayer(mediaPlayer);
                    mediaPlayer.play();

                    // Listening Stats
                    /* Thread t1 = new Thread(() -> {
                        File file = new File("res/TimePlayed.txt");
                        file.mkdirs();
                        BufferedReader reader = null;
                        Object[] arr = null;
                        try {
                            reader = new BufferedReader(new FileReader(file));
                            Stream<String> stream = reader.lines();
                            arr = stream.toArray();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        boolean playing = mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING);
                        while(playing) {
                            playing = mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING);

                        }
                    });
                    t1.start(); */
                });

                title.setLayoutY(resultDistance * index + 40);
                play.setLayoutY(resultDistance * index + 55);
                play.setText("Play");

                download.setOnAction(click -> {
                    Thread t1 = new Thread(() -> {
                        String audioFilePath = "res/audio/" + podID + "/" + episode.title.replace(":", "") + ".mp3";
                        System.out.println(episode.title);
                        File file = new File(audioFilePath);
                        try {
                            file.getParentFile().mkdirs();
                            file.createNewFile();
                            downloadAudio(episode.audio, new File(audioFilePath));
                            System.out.println(episode.audio);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    t1.start();
                    download.setDisable(true);
                    play.setDisable(false);
                    delete.setDisable(false);
                });

                download.setLayoutX(40);
                download.setLayoutY(resultDistance * index + 55);
                download.setText("Download");

                delete.setOnAction(click -> {
                    try {
                        Files.delete(Paths.get("res/audio/" + podID + "/" + episode.title.replace(":", "") + ".mp3"));
                        delete.setDisable(true);
                        download.setDisable(false);
                        play.setDisable(true);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                delete.setLayoutX(112);
                delete.setLayoutY(resultDistance * index + 55);
                delete.setText("Delete");

                if(!audio.exists()) {
                    play.setDisable(true);
                    download.setDisable(false);
                    delete.setDisable(true);
                }
                else {
                    play.setDisable(false);
                    download.setDisable(true);
                    delete.setDisable(false);
                }

                index++;

                displayPane.getChildren().addAll(title, play, download, delete);
            }
        }
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
        mediaPlayer.seek(mediaPlayer.getMedia().getDuration().multiply(seekBar.getValue() / 100));
        mediaPlayer.play();
    }

    public void pause(ActionEvent event){
        mediaPlayer.pause();
    }

    public static void setPodcast(String id) {
        podID = id;
    }

    private void downloadAudio(URL url, File file) throws IOException {

        OutputStream output = null;
        InputStream input = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            int value = connection.getContentLength();
            System.out.println(value);

            output = new FileOutputStream(file);
            input = connection.getInputStream();

            byte[] fileChunk = new byte[8*1024];
            int bytesRead;
            int totalBytes = 0;
            while((bytesRead = input.read(fileChunk)) != -1) {
                totalBytes += bytesRead;
                output.write(fileChunk, 0, bytesRead);
                System.out.println(((float)totalBytes/value) * 100 + "%");
            }
            System.out.println("Done!");
        }
        catch (IOException e) {
            System.out.println("Receiving file at " + url.toString() + " failed");
        }
        finally {
            if(input != null) {
                input.close();
            }
            if(output != null) {
                output.close();
            }
        }
    }

}

