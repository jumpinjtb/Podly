package RSS;

import java.net.MalformedURLException;
import java.net.URL;

public class RSSFeedParser {
    private URL url;

    public RSSFeedParser(String feedURL) {
        try {
            this.url = new URL(feedURL);
        } catch (MalformedURLException e) {
            System.out.println("Invalid URL");
            e.printStackTrace();
        }
    }

    public Feed readFeed() {
        return null;
    }
}
