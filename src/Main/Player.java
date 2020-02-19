package Main;

import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import javax.print.attribute.standard.MediaTray;
import javax.swing.*;

public class Player {
    Button playbutton = new Button("Play");
    Button back10 = new Button("Rewind 10");
    Button forward10 = new Button("FastForward 10");
    ProgressBar progress = new ProgressBar();
    Media m = new Media(new JFileChooser().toString());
   //MediaTray tray = new MediaTray();

    public Player()
    {

    }

}
