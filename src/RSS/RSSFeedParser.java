package RSS;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import org.jdom2.*;
import org.jdom2.input.SAXBuilder;

public class RSSFeedParser {
    private File feed;
    private Element root;

    public RSSFeedParser(String feed) {
        this.feed = new File(feed).getAbsoluteFile();
    }

    public Feed readFeed() throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(feed);
        this.root = document.getRootElement();

        String title = root.getChild("channel").getChildText("title");
        String description = root.getChild("channel").getChildText("itunes:subtitle");
        String link = root.getChild("channel").getChildText("link");

        List<FeedItem> episodes = readEpisodes();

        String id = "";
        int periodIndex = feed.getName().indexOf('.');
        if(periodIndex != -1) {
            id = feed.getName().substring(0, periodIndex);
        }

        return new Feed(title, id, link, description, episodes);
    }

    private List<FeedItem> readEpisodes() throws MalformedURLException {
        List<FeedItem> episodes = new LinkedList<>();

        List<Element> items = root.getChild("channel").getChildren("item");
        for(Element episode : items) {
            String title = episode.getChildText("title");
            String description = episode.getChildText("itunes:summary");
            try {
                URL audio = new URL(episode.getChild("enclosure").getAttributeValue("url"));
                episodes.add(new FeedItem(title, description, audio));
            }
            catch (Exception e) {
                // Edge case for No Dumb Questions
                URL audio = new URL(episode.getChildText("link"));
                episodes.add(new FeedItem(title, description, audio));
            }
        }

        return episodes;
    }
}
