package ru.omickron.service;

import java.util.function.Function;
import java.util.stream.Collectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.omickron.model.RssChannel;

import static org.apache.commons.lang.Validate.isTrue;

public enum PikabuMode {
    USER_PROFILE( ".b-user-profile", PikabuMode:: parseChannelFromProfile ),
    TAGS( ".story-search", PikabuMode:: parseChannelFromSearch ),
    COMMUNITY( ".b-community-header", PikabuMode:: parseChannelFromCommunity );
    private final String selector;
    private final Function<Elements, RssChannel> channelFunction;

    PikabuMode( String selector, Function<Elements, RssChannel> channelFunction ) {
        this.selector = selector;
        this.channelFunction = channelFunction;
    }

    public static PikabuMode select( Document document ) {
        for (PikabuMode mode : values()) {
            String selector = mode.selector;
            Elements elements = document.select( selector );
            if (!elements.isEmpty()) {
                return mode;
            }
        }
        throw new RuntimeException( "Unknown Pikabu Mode" );
    }

    private static RssChannel parseChannelFromSearch( Elements elements ) {
        Elements tagElements = elements.select( ".search-tags-container .search-startup-tag :first-child" );
        isTrue( !tagElements.isEmpty(), "Can't find channel headers" );
        String tagsPart = tagElements.size() > 1 ? "тегами" : "тегом";
        String tagsString = tagElements.stream().map( Element:: html ).collect( Collectors.joining( ", " ) );
        String channelName = String.format( "Записи с %s %s", tagsPart, tagsString );
        return new RssChannel( channelName, String.format( "http://pikabu.ru/tag/%s", tagsString ), channelName );
    }

    private static RssChannel parseChannelFromProfile( Elements elements ) {
        Elements nameElements = elements.select( "td:nth-child(2) > div > a:nth-child(1)" );
        isTrue( !nameElements.isEmpty(), "Can't find profile headers" );
        String name = nameElements.get( 0 ).html();
        String link = nameElements.get( 0 ).attr( "href" );
        return new RssChannel( String.format( "Пользователь %s", name ), link,
                String.format( "Все посты пользователя %s", name ) );
    }

    private static RssChannel parseChannelFromCommunity( Elements elements ) {
        Elements nameElements = elements.select( "h1" );
        Elements linkElements = elements.get( 0 ).parent().select( ".b-community-h-menu a:nth-child(1)" );
        isTrue( !nameElements.isEmpty(), "Can't find community name" );
        isTrue( !linkElements.isEmpty(), "Can't find community link" );
        String name = nameElements.get( 0 ).html();
        String link = linkElements.get( 0 ).attr( "href" );
        return new RssChannel( String.format( "Сообщество %s", name ), link,
                String.format( "Все посты сообщества %s", name ) );
    }

    public RssChannel parseChannel( Document document ) {
        return channelFunction.apply( document.select( selector ) );
    }

}
