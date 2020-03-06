package RSS;


import java.io.File;
import java.net.URL;

public class FeedItem {
    public String title;
    public String description;
    public URL audio;

    public FeedItem(String title, String description, URL audio) {
        this.title = title;
        this.description = description;
        this.audio = audio;
    }

    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
}
