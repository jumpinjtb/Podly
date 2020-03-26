package Main;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;

import java.io.IOException;



public class Controller{
    @FXML
    private Button mainButton, SearchButton, PlayerButton;
    @FXML
    private Pane mainPane;


    public void PlayerButtonClicked(ActionEvent actionEvent) throws IOException {
    // calling the main pane to reload with the podcast fxml.
        // https://www.youtube.com/watch?v=RJOza3XQk34
        SplitPane podPane = FXMLLoader.load(getClass().getResource("../Podcast/Podcast.fxml"));
        mainPane.getChildren().setAll(podPane);
    }

    public void SearchButtonClicked(ActionEvent actionEvent) throws IOException {
        Pane searchPane = FXMLLoader.load(getClass().getResource("../Search/Search.fxml"));
        mainPane.getChildren().setAll(searchPane);
    }
}
