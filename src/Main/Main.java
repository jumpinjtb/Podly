package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        Parent root1 = FXMLLoader.load(getClass().getResource("../Podcast/Podcast.fxml"));
        Parent root2 = FXMLLoader.load(getClass().getResource("../Search/Search.fxml"));
        primaryStage.setTitle("Podly");
        primaryStage.setScene(new Scene(root1, 1280, 720));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
