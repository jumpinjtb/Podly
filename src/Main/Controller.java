package Main;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;

import com.google.gson.*;

public class Controller {
    public Button searchButton;
    public TextField searchBar;
    public Label label;
    public void search() throws IOException {
        System.out.println("Searching... ");
        String value = searchBar.getText();

        URL url = new URL("https://itunes.apple.com/search?term=" +
                URLEncoder.encode(value, StandardCharsets.UTF_8) + "&" + "entity=podcast");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.connect();

        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream())
        );
        String inputLine;
        String content = "";
        while ((inputLine = in.readLine()) != null) {
            content += inputLine;
        }
        in.close();
        con.disconnect();
        System.out.println(content);
        //JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
        parseJson(content);
    }

    private void parseJson(String json) {
        int stringIndex = json.indexOf("collectionName");
        String nameSubstring = json.substring(stringIndex + 17);
        int end = nameSubstring.indexOf("\"");
        String name = nameSubstring.substring(0, end);

        label.setText(name);
    }

}
