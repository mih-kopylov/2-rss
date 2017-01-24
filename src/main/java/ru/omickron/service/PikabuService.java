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

@Component
@Slf4j
public class PikabuService extends AbstractParseService {
    public static final String HTTP_PIKABU_RU = "http://pikabu.ru";

    @Override
    protected String loadPageHtml( String id ) {
        try {
            return client.resource( HTTP_PIKABU_RU + UriUtils.encodePath( id, Charsets.UTF_8.name() ) )
                    .get( String.class );
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException( e );
        }
    }

    @Override
    protected RssChannel parseChannel( Document document ) {
        PikabuMode mode = PikabuMode.select( document );
        return mode.parseChannel( document );
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
}
