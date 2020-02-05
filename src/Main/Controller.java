package Main;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.lang.Object.*;


public class Controller {

    public Button searchButton;
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
        int stringIndex = json.indexOf("collectionName");
        String substring = json.substring(stringIndex + 17);
        int end = substring.indexOf("\"");
        String name = substring.substring(0, end);

        label.setText(name);

        stringIndex = json.indexOf("artworkUrl30");
        substring = json.substring(stringIndex + 15);
        end = substring.indexOf("\"");

        URL imageURL = new URL(substring.substring(0, end));
        byte[] imageArray = sendGetRequest(imageURL);
        FileOutputStream fos = new FileOutputStream("res/images/image.jpg");
        fos.write(imageArray);
        fos.close();
        displayImage(image, "res/images/image.jpg");
    }

    private byte[] sendGetRequest(URL url) throws IOException {
        HttpURLConnection con =  (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.connect();

        int status = con.getResponseCode();

        return con.getInputStream().readAllBytes();
    }

    private void displayImage(ImageView view, String filepath) throws IOException {
        System.out.println(filepath);
        FileInputStream is = new FileInputStream(filepath);
        view.setImage(new Image(is));
    }
}
