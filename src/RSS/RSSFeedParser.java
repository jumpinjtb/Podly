package RSS;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.jdom2.*;
import org.jdom2.input.SAXBuilder;

public class RSSFeedParser {
    private File feed;
    private Element root;

    public RSSFeedParser(String feed) {
        this.feed = new File(feed);
    }

    public Feed readFeed() throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(feed);
        this.root = document.getRootElement();

        String title = root.getChildText("title");
        String description = root.getChildText("description");
        String link = root.getChildText("itunes:new-feed-url");

        List<FeedItem> episodes = readEpisodes(this.feed);

        return new Feed(title, link, description, episodes);
    }

    private List<FeedItem> readEpisodes(File feed) throws MalformedURLException {
        List<FeedItem> episodes = new LinkedList<>();

        List<Element> items = root.getChildren("item");
        for(Element episode : items) {
            String title = episode.getChildText("title");
            String description = episode.getChildText("itunes:summary");
            URL audio =  new URL(episode.getChildText("url"));
            episodes.add(new FeedItem(title, description, audio));
        }

        return episodes;
    }
}
