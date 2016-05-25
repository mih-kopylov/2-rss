package ru.omickron.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import ru.omickron.model.Rss;
import ru.omickron.model.RssChannel;

@Component
@Slf4j
public class PikabuService extends AbstractLoadingService {
    private static final String URL = "http://pikabu.ru/";

    @Cacheable(value = "rss")
    public Rss getRss( String type, String id ) {
        log.debug( "Generating channel {} {}", type, id );
        return new Rss( getChannel( type, id ) );
    }

    private RssChannel getChannel( String type, String id ) {
        String page = getPageHtml( type, id );
        Document document = Jsoup.parse( page );
        return new RssChannel( "title", "link", "desc" );
    }

    private String getPageHtml( String type, String id ) {
        return client.resource( URL + type + "/" + id ).get( String.class );
    }
}
