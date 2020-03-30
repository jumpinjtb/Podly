package RSS;

import java.util.List;

public class Feed {
    public String title;
    public String id;
    public String link;
    public String description;

    public List<FeedItem> episodes;

    public Feed(String title, String id, String link, String description, List<FeedItem> episodes) {
        this.title = title;
        this.id = id;
        this.link = link;
        this.description = description;
        this.episodes = episodes;
    }

    public List<FeedItem> getEpisodes() {
        return episodes;
    }

    public String getId() {
        return id;
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
