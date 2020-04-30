package Search;

import RSS.Feed;
import RSS.FeedItem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import RSS.RSSFeedParser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jdom2.JDOMException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.IOException;

public class Search {
    @FXML
    private Button search, mainButton, playerButton, searchButton;
    @FXML
    private Pane searchPane;
    @FXML
    private TextField searchBar;
    @FXML
    private ScrollPane resultPane;
    @FXML
    private AnchorPane container;

    @FXML
    public ScrollBar scrollBar;

    @FXML
    private ToolBar topBar,bottomBar;



    private int imageWidth = 200;
    private int imageHeight = 200;
    private int resultDistance = 220;
    private int episodeResult = 60;

    public void MainButtonClicked(ActionEvent actionEvent) throws IOException {
        Parent mainPane = FXMLLoader.load(getClass().getResource("/Main/Main.fxml"));
        searchPane.getChildren().setAll(mainPane);
    }

    public void PlayerButtonClicked(ActionEvent actionEvent) throws IOException {
        Parent podPane = FXMLLoader.load(getClass().getResource("/Podcast/Podcast.fxml"));
        searchPane.getChildren().setAll(podPane);
    }


    public void search() throws IOException, JDOMException {
        String value = searchBar.getText();

        if(isValudURL(value)) {
            byte[] result = sendGetRequest(new URL(value));
            String id = UUID.randomUUID().toString();
            String filePath = "res/rss/" + id + ".rss";
            FileOutputStream out = new FileOutputStream(filePath);
            out.write(result);
            openPodcast(filePath, id);

            RSSFeedParser parser = new RSSFeedParser(filePath);
            Feed feed = parser.readFeed();
            String imageURL = feed.imageURL;

            byte[] image = sendGetRequest(new URL(imageURL));
            out = new FileOutputStream("res/images/" + id + ".jpg");
            out.write(image);
        }
        else {
            URL url = new URL("https://itunes.apple.com/search?term=" +
                    URLEncoder.encode(value, StandardCharsets.UTF_8) + "&" + "entity=podcast");

            byte[] content = sendGetRequest(url);

            String str = new String(content);
            parseJson(str);
        }

    }

    private void parseJson(String json) throws IOException {

        searchPane.getChildren().clear();
        container.getChildren().clear();
        searchPane.getChildren().addAll(bottomBar, topBar, resultPane);
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
            view.setLayoutY(resultDistance * (i) + 45);

            //Set properties for label
            Label label = new Label();
            label.setLayoutX(imageWidth + 20);
            label.setLayoutY(resultDistance * (i) +45);

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
            String imgFilepath = "res/temp/" + id + ".jpg";
            File imgFile = new File(imgFilepath);
            imgFile.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(imgFilepath);
            fos.write(artworkData);
            fos.close();
            displayImage(view, imgFilepath);

            feedMatch.find();
            String feedURL = feedMatch.group();
            String rssFilePath = "res/temp/" + id + ".rss";
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
            open.setLayoutY((resultDistance * i) + 60);
            open.setText("View");


            Button subscribe = new Button();
            subscribe.setOnAction(click -> {
                try {
                    subscribe(id);
                    subscribe.setDisable(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            subscribe.setLayoutX(imageWidth + 20);
            subscribe.setLayoutY((resultDistance * i) + 90);
            subscribe.setText("Subscribe");

            container.getChildren().addAll(view, open, label,subscribe);
            resultPane.setContent(container);

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

    private void displayImage(ImageView view, String filepath) throws IOException {
        FileInputStream is = new FileInputStream(filepath);
        view.setImage(new Image(is));
    }

    private void openPodcast(String filePath, String id) throws JDOMException, IOException {
        container.getChildren().clear();


        RSSFeedParser parser = new RSSFeedParser(filePath);
        Feed feed = parser.readFeed();

        Button subscribe = new Button();
        subscribe.setOnAction(click -> {
            try {
                subscribe(id);
                subscribe.setDisable(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        subscribe.setLayoutX(container.getWidth()-80);
        subscribe.setLayoutY(50);
        subscribe.setText("Subscribe");
        container.getChildren().add(subscribe);

        int index = 0;
        for(FeedItem item: feed.episodes) {
            Label title = new Label(item.title);
            Button download = new Button();

            download.setOnAction(click -> {
                Thread t1 = new Thread(() -> {
                    String audioFilePath = "res/audio/" + id + "/" + item.title.replace(":", "") + ".mp3";
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

            title.setLayoutY(episodeResult * index);
            download.setLayoutY(episodeResult * index + 15);
            download.setText("Download");
            container.getChildren().addAll(title, download);
            resultPane.setContent(container);
            index++;
        }
    }

    private void subscribe(String id) throws IOException {
        if(!new File("res/images/").exists()) {
            new File("res/images").mkdirs();
        }

        String imgFilePath = "res/temp/" + id + ".jpg";
        OutputStream newImg = new FileOutputStream("res/images/" + id + ".jpg");
        Files.copy(Paths.get(imgFilePath), newImg);
        File imgFile = new File(imgFilePath);

        if(!new File("res/rss/").exists()) {
            new File("res/rss/").mkdirs();
        }

        String rssFilePath = "res/temp/" + id + ".rss";
        OutputStream newRss = new FileOutputStream("res/rss/" + id + ".rss");
        Files.copy(Paths.get("res/temp/" + id + ".rss"), newRss);
        File rssFile = new File(rssFilePath);

        File timePlayed = new File("res/TimePlayed.txt");
        if(!timePlayed.exists()) {
            timePlayed.getParentFile().mkdirs();
            timePlayed.createNewFile();
        }

        Files.write(Paths.get("res/TimePlayed.txt"), (id + ": 0 \n").getBytes(), StandardOpenOption.APPEND);
    }

    public boolean isValudURL(String url) {
        try {
            new URL(url);
            return true;
        }
        catch(Exception e) {
            return false;
        }
    }

}
