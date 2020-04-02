package Search;

import RSS.Feed;
import RSS.FeedItem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import RSS.RSSFeedParser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import org.jdom2.JDOMException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.IOException;

public class Search {
    @FXML
    private Button mainButton, searchButton, playerButton, search;
    @FXML
    private Pane searchPane;
    @FXML
    public TextField searchBar;
    @FXML
    public ToolBar toolBar;

    private int imageWidth = 200;
    private int imageHeight = 200;
    private int resultDistance = 220;


    public void MainButtonClicked(ActionEvent actionEvent) throws IOException {
        Parent mainPane = FXMLLoader.load(getClass().getResource("../Main/Main.fxml"));
        searchPane.getChildren().setAll(mainPane);
    }

    public void PlayerButtonClicked(ActionEvent actionEvent) throws IOException {
        Parent podPane = FXMLLoader.load(getClass().getResource("../Podcast/Podcast.fxml"));
        searchPane.getChildren().setAll(podPane);
    }

    public void search() throws IOException {
        String value = searchBar.getText();

        URL url = new URL("https://itunes.apple.com/search?term=" +
                URLEncoder.encode(value, StandardCharsets.UTF_8) + "&" + "entity=podcast");

        byte[] content = sendGetRequest(url);

        String str = new String(content);
        parseJson(str);

    }

    private void parseJson(String json) throws IOException {
        searchPane.getChildren().clear();
        searchPane.getChildren().addAll(toolBar);
        searchPane.getChildren().addAll(searchBar, search);

        //Create regex patterns
        //Gets number of results from iTunes
        Matcher numMatch = Pattern.compile("\\d+").matcher(json);
        numMatch.find();
        int result = Integer.parseInt(numMatch.group());
        //Gets podcast name
        Matcher nameMatch = Pattern.compile("(?<=\"collectionName\":\")([^\"].*?)(?=\")").matcher(json);
        //Gets podcast art url
        Matcher artMatch = Pattern.compile("(?<=\"artworkUrl600\":\")([^\"].*?)(?=\")").matcher(json);
        //Get podcast ID
        Matcher idMatch = Pattern.compile("(?<=\"collectionId\":)([0-9]*?)(?=,)").matcher(json);
        //Get rss URL
        Matcher feedMatch = Pattern.compile("(?<=\"feedUrl\":\")([^\"].*?)(?=\")").matcher(json);

        for (int i = 0; i < result; i++) {

            //Set properties for image view
            ImageView view = new ImageView();
            view.setFitHeight(imageHeight);
            view.setFitWidth(imageWidth);
            view.setLayoutX(0);
            view.setLayoutY(resultDistance * (i));

            //Set properties for label
            Label label = new Label();
            label.setLayoutX(imageWidth + 20);
            label.setLayoutY(resultDistance * (i));

            //Find next occurrence of podcast name and set label
            nameMatch.find();
            String name = nameMatch.group();
            label.setText(name);

            //Find next occurrence of artwork url and set the image view
            artMatch.find();
            URL artworkURL = new URL(artMatch.group());

            //Find next occurrence of podcast ID
            idMatch.find();
            String id = idMatch.group();

            byte[] artworkData = sendGetRequest(artworkURL);
            String imgFilepath = "res/images/" + id + ".jpg";
            File imgFile = new File(imgFilepath);
            imgFile.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(imgFilepath);
            fos.write(artworkData);
            fos.close();
            displayImage(view, imgFilepath);

            feedMatch.find();
            String feedURL = feedMatch.group();
            String rssFilePath = "res/rss/" + id + ".rss";
            File rssFile = new File(rssFilePath);
            rssFile.getParentFile().mkdirs();
            fos = new FileOutputStream(rssFilePath);
            fos.write(sendGetRequest(new URL(feedURL)));
            fos.close();

            Button open = new Button();
            open.setOnAction(click -> {
                try {
                    openPodcast(rssFilePath, id);
                } catch (JDOMException | IOException e) {
                    e.printStackTrace();
                }
            });
            open.setLayoutX(imageWidth + 20);
            open.setLayoutY((resultDistance * i) + 20);
            open.setText("View");

            searchPane.getChildren().addAll(view, label, open);
        }
    }

    private byte[] sendGetRequest(URL url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.connect();

        int status = con.getResponseCode();
        InputStream is = con.getInputStream();
        byte[] value = is.readAllBytes();
        con.disconnect();
        return value;
    }

    private void downloadAudio(URL url, File file) throws IOException {

        OutputStream output = null;
        InputStream input = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            output = new FileOutputStream(file);
            input = connection.getInputStream();

            byte[] fileChunk = new byte[8*1024];
            int bytesRead;
            while((bytesRead = input.read(fileChunk)) != -1) {
                output.write(fileChunk, 0, bytesRead);
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

    private void displayImage(ImageView view, String filepath) throws IOException {
        FileInputStream is = new FileInputStream(filepath);
        view.setImage(new Image(is));
    }

    private void openPodcast(String filePath, String id) throws JDOMException, IOException {
        searchPane.getChildren().clear();
        searchPane.getChildren().addAll(toolBar);
        searchPane.getChildren().addAll(searchBar, search);

        RSSFeedParser parser = new RSSFeedParser(filePath);
        Feed feed = parser.readFeed(id);

        ProgressIndicator pb = new ProgressIndicator();

        int index = 0;
        for(FeedItem item: feed.episodes) {
            Label title = new Label(item.title);
            Button download = new Button();

            download.setOnAction(click -> {
                pb.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
                pb.setLayoutY(25);
                searchPane.getChildren().add(pb);
                Thread t1 = new Thread(() -> {
                    String audioFilePath = "res/audio/" + id + "/" + item.title;
                    System.out.println(item.title);
                    File file = new File(audioFilePath);
                    try {
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                        downloadAudio(item.audio, new File(audioFilePath));
                        System.out.println(item.audio);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                t1.start();
            });

            title.setLayoutY(resultDistance * index);
            download.setLayoutY(resultDistance * index + 15);
            download.setText("Download");
            searchPane.getChildren().addAll(title, download);
            index++;
        }
    }
}
