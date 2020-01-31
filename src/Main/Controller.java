package Main;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;


public class Controller {

    public Button searchButton;
    public TextField searchBar;
    public Label label;

    public void search() throws IOException {
        System.out.println("Searching... ");
        String value = searchBar.getText();

        URL url = new URL("https://itunes.apple.com/search?term=" +
                URLEncoder.encode(value, StandardCharsets.UTF_8) + "&" + "entity=podcast");

        byte[] content = sendGetRequest(url);

        String str = new String(content);
        System.out.println(str);
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
        String image = substring.substring(0, end);

        URL imageURL = new URL(image);
        byte[] content = sendGetRequest(imageURL);
        //System.out.println(content);
    }

    private byte[] sendGetRequest(URL url) throws IOException {
        HttpURLConnection con =  (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.connect();

        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream())
        );

        byte[] buffer = new byte[10000];
        ByteArrayInputStream content = new ByteArrayInputStream(buffer);

        con.disconnect();

        return buffer;
    }
}
