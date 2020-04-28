package Main;

import RSS.RSSFeedParser;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {

    private boolean isRunning = true;
    private long timeStart = System.nanoTime();
    private double interval = 6E12;

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Time Tracking
       /*  Runnable r = () -> {
            while(isRunning) {
                try {
                    TimeUnit.SECONDS.sleep(60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if((timeStart + interval) < System.nanoTime()) {
                    System.out.println("Hello");
                    System.out.println(timeStart + interval);
                    timeStart = System.nanoTime();
                    File folder = new File("res/rss");
                    for(File file: Objects.requireNonNull(folder.listFiles())) {
                        RSSFeedParser parser = new RSSFeedParser("res/rss/" + file.getName());
                        try {
                            System.out.println(file.getName().substring(0, file.getName().length() - 4));
                            URL url = new URL("https://itunes.apple.com/lookup?id=" +
                                    file.getName().substring(0, file.getName().length() - 4));
                            byte[] value = sendGetRequest(url);

                            String json = new String(value);

                            Matcher feedMatch = Pattern.compile("(?<=\"feedUrl\":\")([^\"].*?)(?=\")").matcher(json);
                            feedMatch.find();
                            String feedURL = feedMatch.group();
                            FileOutputStream fos = new FileOutputStream(file);
                            fos.write(sendGetRequest(new URL(feedURL)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        new Thread(r).start(); */
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                isRunning = false;
                File folder = new File("res/temp/");
                if(folder.exists()) {
                    String[] files = folder.list();
                    if (files != null) {
                        for (String fileName : files) {
                            fileName = "res/temp/" + fileName;
                            try {
                                File file = new File(fileName);
                                Files.delete(Paths.get(file.getAbsolutePath()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                        try {
                            Files.delete(Paths.get("res/temp"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Platform.exit();
                System.exit(0);
            }
        });
        primaryStage.setTitle("Podly");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
        Image icon = new Image("Main/PlayButton.png");
        primaryStage.getIcons().add(icon);
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


    public static void main(String[] args) {
        launch(args);
    }
}
