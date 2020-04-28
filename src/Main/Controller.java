package Main;


import Podcast.Podcast;
import RSS.Feed;
import RSS.RSSFeedParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;


public class Controller implements Initializable {

    @FXML
    public AnchorPane settingsContainer;
    @FXML
    public ScrollPane settingsPane;
    @FXML
    private Button opmlBtn;
    @FXML
    private Label labelFile;
    @FXML
    public AnchorPane container;
    public ScrollPane resultPane;
    @FXML
    private Button mainButton, searchButton, playerButton;
    @FXML
    private Pane mainPane;

    private int imageWidth = 200;
    private int imageHeight = 200;
    private int resultDistance = 220;


    public void PlayerButtonClicked(ActionEvent actionEvent) throws IOException {
        // calling the main pane to reload with the podcast fxml.
        SplitPane podPane = FXMLLoader.load(getClass().getResource("../Podcast/Podcast.fxml"));
        mainPane.getChildren().setAll(podPane);
    }

    public void SearchButtonClicked(ActionEvent actionEvent) throws IOException {
        Pane searchPane = FXMLLoader.load(getClass().getResource("../Search/Search.fxml"));
        mainPane.getChildren().setAll(searchPane);
    }

    @FXML
    public void opmlFileChooser(ActionEvent event) throws JDOMException, IOException {
        FileChooser fc = new FileChooser();
        File f = fc.showOpenDialog(null);

        if (f != null){
            labelFile.setText(f.getAbsolutePath());
        }

        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(f);
        Element root = doc.getRootElement();
        List<Element> items = root.getChild("body").getChild("outline").getChildren();
        for(Element item: items) {
            String rss = item.getAttributeValue("xmlUrl");
            URL url = new URL(rss);
            byte[] feed = sendGetRequest(url);
            String id = UUID.randomUUID().toString();
            String filePath = "res/rss/" + id + ".rss";
            File file = new File(filePath);

            FileOutputStream fos = new FileOutputStream(file.getAbsoluteFile());
            fos.write(feed);
            RSSFeedParser parser = new RSSFeedParser(filePath);
            Feed rssFeed = parser.readFeed();
            URL imageURL = new URL(rssFeed.imageURL);
            byte[] image = sendGetRequest(imageURL);
            fos = new FileOutputStream("res/images/" + id + ".jpg");
            fos.write(image);
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File rssFolder = new File("res/rss/");
        if (rssFolder.exists()) {
            String[] files = rssFolder.list();
            if (files != null) {
                int i = 0;
                for (String fileName : files) {
                    String id = "";
                    int periodIndex = fileName.indexOf('.');
                    if(periodIndex != -1) {
                        id = fileName.substring(0, periodIndex);
                    }
                    try {
                        fileName = "res/rss/" + id + ".rss";
                        String imageName = "res/images/" + id + ".jpg";
                        RSSFeedParser parser = new RSSFeedParser(fileName);
                        Feed feed = parser.readFeed();
                        File image = new File(imageName);

                        ImageView view = new ImageView();
                        view.setFitWidth(imageWidth);
                        view.setFitHeight(imageHeight);
                        view.setLayoutX(0);
                        view.setLayoutY(resultDistance * i + 45);
                        view.setImage(new Image(new FileInputStream(image.getAbsoluteFile())));

                        String podName = feed.title;
                        Label name = new Label();
                        name.setLayoutX(imageWidth + 20);
                        name.setLayoutY(resultDistance * i + 45);
                        name.setText(podName);

                        Button open = new Button();
                        String finalId = id;
                        open.setOnAction(click -> {
                            Podcast.setPodcast(finalId);
                            Parent pane = null;
                            try {
                                pane = FXMLLoader.load(getClass().getResource("../Podcast/Podcast.fxml"));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            mainPane.getChildren().setAll(pane);
                        });

                        open.setLayoutX(imageWidth + 20);
                        open.setLayoutY(resultDistance * i + 60);
                        open.setText("Open");

                        Button unsub = new Button();
                        unsub.setOnAction(click -> {
                            container.getChildren().removeAll(view, name, open, unsub);
                            System.out.println("Hello");
                            try {
                                Files.delete(Paths.get("res/rss/" + finalId + ".rss"));
                                Files.delete(Paths.get("res/images/" + finalId + ".jpg"));
                                if(new File("res/audio/" + finalId).exists()) {
                                    File file = new File("res/audio/" + finalId);
                                    File[] fileList = file.listFiles();
                                    for (File f : fileList) {
                                        Files.delete(Paths.get(f.getPath()));
                                    }
                                    Files.delete(Paths.get("res/audio/" + finalId));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                        unsub.setLayoutX(imageWidth + 70);
                        unsub.setLayoutY(resultDistance * i + 60);
                        unsub.setText("Unsubscribe");

                        container.getChildren().addAll(view, name, open, unsub);
                        resultPane.setContent(container);

                        ImageView view1 = new ImageView();
                        view1.setImage(new Image(new FileInputStream(image.getAbsoluteFile())));
                        view1.setFitWidth(imageWidth);
                        view1.setFitHeight(imageHeight);
                        view1.setLayoutY(resultDistance * i + 45);

                        Label label1 = new Label();
                        label1.setLayoutX(imageWidth + 20);
                        label1.setLayoutY(resultDistance * i + 45);
                        label1.setText("Time Listened");

                        Label timePlayed = new Label();
                        timePlayed.setText(timeListened(id));
                        timePlayed.setLayoutX(imageWidth + 20);
                        timePlayed.setLayoutY(resultDistance * i + 60);

                        opmlBtn.setLayoutX(800);
                        settingsContainer.getChildren().addAll(label1, view1, timePlayed);
                        settingsPane.setContent(settingsContainer);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    i++;
                }
            }
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

    private String timeListened(String id) throws IOException {
        String val = "";
        List<String> lines = new ArrayList<>(Files.readAllLines(Paths.get("res/TimePlayed.txt")));
        String[] values;
        for(String line: lines) {
            values = line.split(":");
            if(values[0].equals(id)) {
                int time = Integer.parseInt(values[1].replace(" ", ""));
                int hours = time / 60;
                int minutes = time % 60;
                val = hours + " Hours " + minutes + " Minutes";
            }
        }
        return val;
    }
}

