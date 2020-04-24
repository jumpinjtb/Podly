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
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;


public class Controller implements Initializable {

    @FXML
    private Button opmlButton;
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
        // https://www.youtube.com/watch?v=RJOza3XQk34
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
            File file = new File("res/rss/" + UUID.randomUUID().toString() + ".rss");

            FileOutputStream fos = new FileOutputStream(file.getAbsoluteFile());
            fos.write(feed);
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

                        container.getChildren().addAll(view, name, open);
                        resultPane.setContent(container);
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
}

