package ru.omickron.service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;
import ru.omickron.model.RssChannel;
import ru.omickron.model.RssItem;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.Validate.isTrue;

@Component
@Slf4j
public class PikabuService extends AbstractParseService {
    @Override
    protected String loadPageHtml( String id ) {
        try {
            return client.resource( "http://pikabu.ru" + UriUtils.encodePath( id, Charsets.UTF_8.name() ) )
                    .get( String.class );
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException( e );
        }
    }

    @Override
    protected RssChannel parseChannel( Document document ) {
        RssChannel result;
        Elements profileInfo = document.select( ".profile_wrap" );
        if (!profileInfo.isEmpty()) {
            result = parseChannelFromProfile( profileInfo );
        } else {
            Elements searchInfo = document.select( ".story-search" );
            if (!searchInfo.isEmpty()) {
                result = parseChannelFromSearch( searchInfo );
            } else {
                log.error( document.html() );
                throw new RuntimeException( "Can't parse channel info" );
            }
        }
        return result;
    }

    @Override
    protected List<RssItem> parseItems( Document document ) {
        return document.select( ".stories .story" ).stream().filter( this :: hasTitle ).filter( this :: isNotAd )
                .map( o -> {
                    Elements titleElements = o.select( ".story__header-title a" );
                    String title = titleElements.html();
                    String link = titleElements.attr( "href" );
                    String pubDateString = o.select( ".story__date" ).attr( "title" );
                    String description = o.select( ".b-story__content" ).html();
                    Document descriptionDocument = Jsoup.parse( description );
                    descriptionDocument.select( ".b-video" ).forEach( videoDiv -> {
                        String videoUrl = videoDiv.attr( "data-url" );
                        videoDiv.html( String.format( "<iframe src=\"%s\" width=\"600\" height=\"337\"/>", videoUrl ) );
                    } );
                    description = descriptionDocument.html();
                    description = description.replaceAll( "<p><br></p>", "" );
                    return new RssItem( title, link, description, Long.parseLong( pubDateString ) );
                } ).collect( Collectors.toList() );
    }

    private boolean isNotAd( Element element ) {
        return element.select( ".story__sponsor" ).isEmpty();
    }

    private boolean hasTitle( Element element ) {
        return isNotBlank( element.select( ".story__date" ).attr( "title" ) );
    }

    private RssChannel parseChannelFromSearch( Elements searchInfo ) {
        Elements tagElements = searchInfo.select( ".search-tags-container .search-startup-tag :first-child" );
        isTrue( !tagElements.isEmpty() );
        String tagsPart = tagElements.size() > 1 ? "тегами" : "тегом";
        String tagsString = tagElements.stream().map( Element:: html ).collect( Collectors.joining( ", " ) );
        String channelName = String.format( "Записи с %s %s", tagsPart, tagsString );
        return new RssChannel( channelName, String.format( "http://pikabu.ru/tag/%s", tagsString ), channelName );
    }

    private RssChannel parseChannelFromProfile( Elements profileInfo ) {
        Elements nameElements = profileInfo.select( "td:nth-child(2) > div > a" );
        isTrue( 1 == nameElements.size() );
        String name = nameElements.get( 0 ).html();
        String link = nameElements.get( 0 ).attr( "href" );
        return new RssChannel( String.format( "Пользователь %s", name ), link,
                String.format( "Все посты пользователя %s", name ) );
    }
}
