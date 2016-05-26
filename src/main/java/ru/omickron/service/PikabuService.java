package ru.omickron.service;

import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import ru.omickron.model.Rss;
import ru.omickron.model.RssChannel;
import ru.omickron.model.RssItem;

import static org.apache.commons.lang.Validate.isTrue;

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
        RssChannel channel;
        Elements profileInfo = document.select( ".profile_wrap" );
        if (!profileInfo.isEmpty()) {
            channel = getChannelFromProfile( profileInfo );
        } else {
            Elements searchInfo = document.select( ".story-search" );
            if (!searchInfo.isEmpty()) {
                channel = getChannelFromSearch( searchInfo );
            } else {
                log.error( page );
                throw new RuntimeException( "Can't parse channel info" );
            }
        }
        channel.getItems().addAll( getItems( document.select( ".stories" ) ) );
        return channel;
    }

    private List<RssItem> getItems( Elements stories ) {
        return stories.select( ".story" ).stream().map( o -> {
            Elements titleElements = o.select( ".story__header-title a" );
            String title = titleElements.html();
            String link = titleElements.attr( "href" );
            String pubDateString = o.select( ".story__date" ).attr( "title" );
            DateTime pubDate = DateTime.parse( pubDateString, DateTimeFormat.forPattern( "dd MMMM YYYY в HH:mm" ) );
            String description = o.select( ".b-story__content" ).html();
            return new RssItem( title, link, description, pubDate.getMillis() );
        } ).collect( Collectors.toList() );
    }

    private RssChannel getChannelFromSearch( Elements searchInfo ) {
        Elements tagElements = searchInfo.select( ".search-tags-container .tb-tag-complete" );
        isTrue( !tagElements.isEmpty() );
        String tagsPart = tagElements.size() > 1 ? "тегами" : "тегом";
        String tagsString = tagElements.stream().map( o -> o.attr( "data-tag" ) ).collect( Collectors.joining( ", " ) );
        String channelName = String.format( "Записи с %s %s", tagsPart, tagsString );
        return new RssChannel( channelName, String.format( "http://pikabu.ru/tag/%s", tagsString ), channelName );
    }

    private RssChannel getChannelFromProfile( Elements profileInfo ) {
        Elements nameElements = profileInfo.select( "td:nth-child(2) > div > a" );
        isTrue( 1 == nameElements.size() );
        String name = nameElements.get( 0 ).html();
        String link = nameElements.get( 0 ).attr( "href" );
        return new RssChannel( String.format( "Пользователь %s", name ), link,
                String.format( "Все посты пользователя %s", name ) );
    }

    private String getPageHtml( String type, String id ) {
        return client.resource( URL + type + "/" + id ).get( String.class );
    }
}
