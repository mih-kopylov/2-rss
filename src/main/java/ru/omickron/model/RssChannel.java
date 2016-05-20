package ru.omickron.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.collect.Lists;

@JacksonXmlRootElement(localName = "channel")
public class RssChannel {
    private final String title;
    private final String link;
    private final String description;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "item")
    private final List<RssItem> items = Lists.newArrayList();
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

    public String getPubDate() {
        return getLastBuildDate();
    }
}
