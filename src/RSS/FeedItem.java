package RSS;

public class FeedItem {
    public String title;
    public String description;
    public String author;

    public FeedItem(String title, String description, String author) {
        this.title = title;
        this.description = description;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getAuthor() {
        return author;
    }
}
