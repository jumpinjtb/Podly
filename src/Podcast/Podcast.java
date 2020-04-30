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
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.TimeUnit;
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
    private ScrollPane resultPane;
    @FXML
    private MediaView mediaView = new MediaView();
    private CustomMediaPlayer player;
    @FXML
    private ImageView imageView;
    @FXML
    private Slider volumeSlider;
    @FXML
    private AnchorPane container;
    @FXML
    private Slider seekBar;

    private static String podID = null;
    private static int timePlayed;
    private int resultDistance = 220;


    public void MainButtonClicked(ActionEvent actionEvent) throws IOException {
        Parent mainPane = FXMLLoader.load(getClass().getResource("/Main/Main.fxml"));
        podcastPane.getItems().setAll(mainPane);

    }

    public void SearchButtonClicked(ActionEvent actionEvent) throws IOException {
        Parent searchPane = FXMLLoader.load(getClass().getResource("/Search/Search.fxml"));
        podcastPane.getItems().setAll(searchPane);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Receives the cover art from the images folder and sets it to the ImageView.
        Image coverArt = new Image(new File("res/images/" + podID + ".jpg").toURI().toString());
        imageView.setImage(coverArt);

        //Receives the audio from the audio folder and plays it upon request.
        /* String path = new File("res/audio/SampleMedia.mp3").getAbsolutePath();
        media = new Media(new File(path).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.setAutoPlay(false); */

        //Updates the current time of the currently playing media.


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
                    Media media = new Media(audio.toURI().toString());
                    player = new CustomMediaPlayer(media, seekBar, volumeSlider);
                    player.play(click1);

                    // Listening Stats
                    Thread t1 = new Thread(() -> {
                        String filePath = "res/TimePlayed.txt";
                        File file = new File(filePath);
                        try {
                            if(!file.exists()) {
                                Files.createFile(Paths.get(filePath));
                            }

                            BufferedReader reader = Files.newBufferedReader(Paths.get(filePath));

                            File rssFolder = new File("res/rss/");
                            File[] files = rssFolder.listFiles();

                            List<String> lines = new ArrayList<>(Files.readAllLines(Paths.get(filePath)));

                            int lineIndex = -1;
                            for(int j = 0; j < lines.size(); j++) {
                                String[] values = lines.get(j).split(":");
                                if (values[0].equals(podID)) {
                                    lineIndex = j;
                                    timePlayed = Integer.parseInt(values[1].substring(1));
                                    break;
                                }
                            }
                            reader.close();

                            TimeUnit.SECONDS.sleep(1);
                            boolean playing = player.isPlaying();
                            while(playing) {
                                TimeUnit.SECONDS.sleep(5);
                                playing = player.isPlaying();
                                timePlayed += 5;
                            }
                            for(int j = 0; j < lines.size(); j++) {
                                if(lines.get(j).equals(lines.get(lineIndex))) {
                                    lines.set(j, podID + ": " + timePlayed);
                                }
                            }

                            Files.write(Paths.get(filePath), lines);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    t1.start();
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

                container.getChildren().addAll(title, play, download, delete);
                resultPane.setContent(container);
            }
        }
    }

    public void play(ActionEvent event){
        player.play(event);
    }

    public void pause(ActionEvent event){
        player.pause(event);
    }


    public static void setPodcast(String id) {
        podID = id;
        timePlayed = 0;
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

