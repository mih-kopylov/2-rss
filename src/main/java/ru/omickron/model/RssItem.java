package ru.omickron.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RssItem {
    private static final int MILLIS = 1000;
    private final String title;
    private final String link;
    private final String description;
    private final long pubDate;

    public RssItem( String title, String link, String description, long pubDate ) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.pubDate = pubDate;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getPubDate() {
        return new SimpleDateFormat( Const.DATE_FORMAT, Locale.ENGLISH ).format( new Date( pubDate * MILLIS ) );
    }
}
