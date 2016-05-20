package ru.omickron.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.NonNull;

@JacksonXmlRootElement(localName = "rss")
public class Rss {
    @JacksonXmlProperty(isAttribute = true)
    private final String version = "2.0";
    @JacksonXmlProperty(localName = "channel")
    private final RssChannel channel;

    public Rss( @NonNull RssChannel channel ) {
        this.channel = channel;
    }

    public RssChannel getChannel() {
        return channel;
    }
}
