package Main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.beans.EventHandler;
import java.io.IOException;


public class Controller {
    @FXML
    private Button mainButton, SearchButton, PlayerButton;



    public void MainButtonClicked(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        Stage mainStage = new Stage();
        mainStage.setScene(new Scene(root,1280, 720));
        mainStage.show();
    }

    public void PlayerButtonClicked(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../Podcast/Podcast.fxml"));
        Stage podcastStage = new Stage();
        podcastStage.setScene(new Scene(root,1280, 720));
        podcastStage.show();
    }

    public void SearchButtonClicked(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../Search/Search.fxml"));
        Stage searchStage = new Stage();
        searchStage.setScene(new Scene(root,1280, 720));
        searchStage.show();
    }
}
