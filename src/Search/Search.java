package Search;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;


import java.io.IOException;

public class Search {
    @FXML
    private Button mainButton, SearchButton, PlayerButton;
    @FXML
    private Pane searchPane;


    public void MainButtonClicked(ActionEvent actionEvent) throws IOException {
        Parent mainPane = FXMLLoader.load(getClass().getResource("Main.fxml"));
        searchPane.getChildren().setAll(mainPane);
    }

    public void PlayerButtonClicked(ActionEvent actionEvent) throws IOException {
        Parent podPane = FXMLLoader.load(getClass().getResource("../Podcast/Podcast.fxml"));
        searchPane.getChildren().setAll(podPane);
    }

}
