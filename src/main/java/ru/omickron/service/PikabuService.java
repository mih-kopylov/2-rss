package ru.omickron.service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;
import ru.omickron.model.Rss;
import ru.omickron.model.RssChannel;
import ru.omickron.model.RssItem;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.Validate.isTrue;

@Component
@Slf4j
public class PikabuService extends AbstractLoadingService {
    @Cacheable(value = "rss")
    public Rss getRss( String id ) {
        log.debug( "Generating channel {}", id );
        return new Rss( getChannel( id ) );
    }

    private RssChannel getChannel( String id ) {
        String page = getPageHtml( id );
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
        return stories.select( ".story" ).stream()
                .filter( o -> isNotBlank( o.select( ".story__date" ).attr( "title" ) ) ).map( o -> {
                    Elements titleElements = o.select( ".story__header-title a" );
                    String title = titleElements.html();
                    String link = titleElements.attr( "href" );
                    String pubDateString = o.select( ".story__date" ).attr( "title" );
                    String description = o.select( ".b-story__content" ).html();
                    return new RssItem( title, link, description, Long.parseLong( pubDateString ) );
                } ).collect( Collectors.toList() );
    }

    private RssChannel getChannelFromSearch( Elements searchInfo ) {
        Elements tagElements = searchInfo.select( ".search-tags-container .search-startup-tag :first-child" );
        isTrue( !tagElements.isEmpty() );
        String tagsPart = tagElements.size() > 1 ? "тегами" : "тегом";
        String tagsString = tagElements.stream().map( o -> o.html() ).collect( Collectors.joining( ", " ) );
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

    private String getPageHtml( String id ) {
        try {
            return client.resource( "http://pikabu.ru" + UriUtils.encodePath( id, Charsets.UTF_8.name() ) )
                    .get( String.class );
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException( e );
        }
    }
}
