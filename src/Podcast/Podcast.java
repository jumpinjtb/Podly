package Podcast;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;

import java.io.IOException;

public class Podcast {
    @FXML
    private Button mainButton, SearchButton, PlayerButton;
    @FXML
    private SplitPane podcastPane;


    public void MainButtonClicked(ActionEvent actionEvent) throws IOException {
        Parent mainPane = FXMLLoader.load(getClass().getResource("../Main/Main.fxml"));
        podcastPane.getItems().setAll(mainPane);
        
    }

    public void SearchButtonClicked(ActionEvent actionEvent) throws IOException {
        Parent searchPane = FXMLLoader.load(getClass().getResource("../Search/Search.fxml"));
        podcastPane.getItems().setAll(searchPane);
    }
}
