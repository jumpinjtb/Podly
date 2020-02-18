package Main;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Controller {

    public Button searchButton;
    public Button subButton;
    public TextField searchBar;
    public Label label;
    public ImageView image;


    public void search() throws IOException {
        System.out.println("Searching... ");
        String value = searchBar.getText();

        URL url = new URL("https://itunes.apple.com/search?term=" +
                URLEncoder.encode(value, StandardCharsets.UTF_8) + "&" + "entity=podcast");

        byte[] content = sendGetRequest(url);

        String str = new String(content);
        System.out.println(str);
        parseJson(str);
    }

    private void parseJson(String json) throws IOException {

        //Gets number of results from iTunes
        Matcher numMatch = Pattern.compile("\\d+").matcher(json);
        numMatch.find();
        int result = Integer.parseInt(numMatch.group());

        //Gets podcast name
        Matcher nameMatch = Pattern.compile("(?<=\"collectionName\":\")([A-Z]|[a-z]|[0-9]|\\s)*").matcher(json);
        nameMatch.find();
        String name = nameMatch.group();

        label.setText(name);

        //Gets podcast art url
        Matcher artMatch = Pattern.compile("(?<=\"artworkUrl600\":\")([^\"].*?)(?=\")").matcher(json);
        artMatch.find();
        URL artworkURL = new URL(artMatch.group());
        System.out.println(artMatch.group());

        byte[] artworkData = sendGetRequest(artworkURL);
        String filepath = "res/images/" + name + ".jpg";
        FileOutputStream fos = new FileOutputStream(filepath);
        fos.write(artworkData);
        fos.close();
        displayImage(image, filepath);
        

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
