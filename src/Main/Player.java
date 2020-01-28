package Main;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import javax.print.attribute.standard.MediaTray;

public class Player {
    Media m;
    MediaPlayer player;
    MediaView v;


    public Player(String file)
    {
        m = new Media(file);
        player = new MediaPlayer(m);
        v = new MediaView(player);


    }




}
