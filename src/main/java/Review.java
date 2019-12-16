import java.util.ArrayList;

public class Review implements java.io.Serializable {
    private String id;
    private String link;
    private String title;
    private String text;
    private long rating;
    private String author;
    private String date;
    private int sentiment;
    private boolean isSarcasm;
    private ArrayList<String> entities;

    public Review(String id, String link, String title, String text, long rating, String author, String date) {
        this.id = id;
        this.link = link;
        this.title = title;
        this.text = text;
        this.rating = rating;
        this.author = author;
        this.date = date;
        this.sentiment = -1;
        this.isSarcasm = false;
        this.entities = new ArrayList<String>();
    }


    public Review(){
        this.id = null;
        this.link = null;
        this.title = null;
        this.text = null;
        this.rating = -1;
        this.author = null;
        this.date = null;
        this.sentiment = -1;
        this.isSarcasm = false;
        this.entities = new ArrayList<String>();
    }


    public ArrayList<String> getEntities() {
        return entities;
    }

    public void addeEntitie(String entitie) {
        this.entities.add(entitie);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSentiment() {
        return sentiment;
    }

    public void setSentiment(int sentiment) {
        this.sentiment = sentiment;
    }

    public boolean isSarcasm() {
        return isSarcasm;
    }

    public void setEntities(ArrayList<String> entities) {
        this.entities = entities;
    }

    public void setSarcasm(boolean sarcasm) {
        isSarcasm = sarcasm;
    }

    public void analyzeSarcasm() {
        if (sentiment >= 0)
            isSarcasm = sentiment + 1 != rating;
    }

    @Override
    public String toString() {
        String s = "Review id=" + id  +
                ", Link:" + link +
                ", Title: " + title +
                ", Text: " + text +
                ", Rating:" + rating +
                ", Author:" + author +
                ", Date: " + date +
                ", IsSarcasm: "  + isSarcasm +
                ", Entities: " + entities.toString();
        return s;
    }

    public String getColoredSentimentHTML(String hSize) {
        String str = "<span "  + "style=\"color:rgb";
        if (sentiment != -1){
            if (sentiment == 0)
                str = str.concat("(139,0,0);\"> Sentiment: 0 - very negative");
            else if (sentiment == 1)
                str = str.concat("(255,0,0);\"> Sentiment: 1 - negative");
            else if (sentiment == 2)
                str = str.concat("(0,0,0);\"> Sentiment: 2 - neutral");
            else if (sentiment == 3)
                str = str.concat("(48, 230, 151);\"> Sentiment: 3 - positive");
            else if (sentiment == 4)
                str = str.concat("(13, 145, 88);\"> Sentiment: 4 - very positive");


            return str.concat("</" + "span" + ">");
        }
        return "";
    }
}
