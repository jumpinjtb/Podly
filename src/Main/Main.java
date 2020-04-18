package Main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
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


    public static void main(String[] args) {
        launch(args);
    }
}
