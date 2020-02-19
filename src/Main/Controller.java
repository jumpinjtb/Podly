package Main;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Controller {

    public TextField searchBar;
    public Button searchButton;
    public AnchorPane anchor;

    private int imageWidth = 200;
    private int imageHeight = 200;
    private int resultDistance = 220;


    public void search() throws IOException {
        System.out.println("Searching... ");
        String value = searchBar.getText();

        URL url = new URL("https://itunes.apple.com/search?term=" +
                URLEncoder.encode(value, StandardCharsets.UTF_8) + "&" + "entity=podcast");

        byte[] content = sendGetRequest(url);

        String str = new String(content);
        //System.out.println(str);
        parseJson(str);
    }

    private void parseJson(String json) throws IOException {
        anchor.getChildren().clear();
        anchor.getChildren().addAll(searchBar, searchButton);

        //Create regex patterns
        //Gets number of results from iTunes
        Matcher numMatch = Pattern.compile("\\d+").matcher(json);
        numMatch.find();
        int result = Integer.parseInt(numMatch.group());
        //Gets podcast name
        Matcher nameMatch = Pattern.compile("(?<=\"collectionName\":\")([^\"].*?)(?=\")").matcher(json);
        //Gets podcast art url
        Matcher artMatch = Pattern.compile("(?<=\"artworkUrl600\":\")([^\"].*?)(?=\")").matcher(json);

        for(int i = 0; i < result; i++) {

            //Set properties for image view
            ImageView view = new ImageView();
            view.setFitHeight(imageHeight);
            view.setFitWidth(imageWidth);
            view.setLayoutX(0);
            view.setLayoutY(resultDistance*(i));

            //Set properties for label
            Label label = new Label();
            label.setLayoutX(imageWidth + 20);
            label.setLayoutY(resultDistance*(i));

            //Find next occurrence of podcast name and set label
            nameMatch.find();
            String name = nameMatch.group();
            label.setText(name);
            System.out.println(name);

            //Find next occurence of artwork url and set the image view
            artMatch.find();
            URL artworkURL = new URL(artMatch.group());

            byte[] artworkData = sendGetRequest(artworkURL);
            String filepath = "res/images/" + name + ".jpg";
            FileOutputStream fos = new FileOutputStream(filepath);
            fos.write(artworkData);
            fos.close();
            displayImage(view, filepath);

            anchor.getChildren().addAll(view, label);
        }
    }

    private byte[] sendGetRequest(URL url) throws IOException {
        HttpURLConnection con =  (HttpURLConnection) url.openConnection();
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
