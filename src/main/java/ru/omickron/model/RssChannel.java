package ru.omickron.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RssChannel {
    private final String title;
    private final String link;
    private final String description;
    private final List<RssItem> items = new ArrayList<RssItem>();
    private final Date date;

    public RssChannel( String title, String link, String description ) {
        this.title = title;
        this.link = link;
        this.description = description;
        date = new Date();
    }

    public List<RssItem> getItems() {
        return items;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public String getLastBuildDate() {
        return new SimpleDateFormat( Const.DATE_FORMAT, Locale.ENGLISH ).format( date );
    }
}
