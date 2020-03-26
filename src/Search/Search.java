package Search;

import RSS.Feed;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import RSS.RSSFeedParser;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.jdom2.JDOMException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import java.io.IOException;
import java.net.URL;

public class Search {
    @FXML
    private Button mainButton, search, playerButton;
    @FXML
    private Pane searchPane;
    @FXML
    public TextField searchBar;

    private int imageWidth = 200;
    private int imageHeight = 200;
    private int resultDistance = 220;


    public void MainButtonClicked(ActionEvent actionEvent) throws IOException {
        Parent mainPane = FXMLLoader.load(getClass().getResource("Main.fxml"));
        searchPane.getChildren().setAll(mainPane);
    }

    public void PlayerButtonClicked(ActionEvent actionEvent) throws IOException {
        Parent podPane = FXMLLoader.load(getClass().getResource("../Podcast/Podcast.fxml"));
        searchPane.getChildren().setAll(podPane);
    }


    public void search() throws IOException, JDOMException {
        String value = searchBar.getText();

        URL url = new URL("https://itunes.apple.com/search?term=" +
                URLEncoder.encode(value, StandardCharsets.UTF_8) + "&" + "entity=podcast");

        byte[] content = sendGetRequest(url);

        String str = new String(content);
        parseJson(str);

    }

    private void parseJson(String json) throws IOException, JDOMException {
        searchPane.getChildren().clear();
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
            FileOutputStream fos = new FileOutputStream(imgFilepath);
            fos.write(artworkData);
            fos.close();
            displayImage(view, imgFilepath);

            feedMatch.find();
            String feedURL = feedMatch.group();
            String rssFilePath = "res/rss/" + id + ".rss";
            fos = new FileOutputStream(rssFilePath);
            fos.write(sendGetRequest(new URL(feedURL)));
            fos.close();

            searchPane.getChildren().addAll(view, label);

            RSSFeedParser parser = new RSSFeedParser(rssFilePath);
            Feed feed = parser.readFeed();
        }
    }

    private byte[] sendGetRequest(URL url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.connect();

        int status = con.getResponseCode();
        byte[] value = con.getInputStream().readAllBytes();
        con.disconnect();
        return value;
    }

    private void displayImage(ImageView view, String filepath) throws IOException {
        FileInputStream is = new FileInputStream(filepath);
        view.setImage(new Image(is));
    }
}
