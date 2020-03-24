package RSS;

import java.util.ArrayList;
import java.util.List;

public class Feed {
    public String title;
    public String link;
    public String description;

    public List<FeedItem> episodes;

    public Feed(String title, String link, String description, List<FeedItem> episodes) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.episodes = episodes;
    }

    public List<FeedItem> getEpisodes() {
        return episodes;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return title + ": " + description;
    }
}
