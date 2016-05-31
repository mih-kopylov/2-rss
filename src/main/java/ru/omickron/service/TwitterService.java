package ru.omickron.service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;
import ru.omickron.model.RssChannel;
import ru.omickron.model.RssItem;

@Component
@Slf4j
public class TwitterService extends AbstractParseService {
    public static final String URL = "https://twitter.com";

    @Override
    protected String loadPageHtml( String id ) {
        try {
            return client.resource( URL + "/" + UriUtils.encodePath( id, Charsets.UTF_8.name() ) ).get( String.class );
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException( e );
        }
    }

    @Override
    protected RssChannel parseChannel( Document document ) {
        String title = document.select( ".ProfileHeaderCard-name a" ).html();
        String link = URL + document.select( ".ProfileHeaderCard-name a" ).attr( "href" );
        String description = document.select( ".ProfileHeaderCard-bio" ).html();
        return new RssChannel( title, link, description );
    }

    @Override
    protected List<RssItem> parseItems( Document document ) {
        return document.select( ".js-stream-tweet" ).stream().map( o -> {
            String title = "Твит";
            StringBuilder descrption = new StringBuilder();
            Element textBlock = o.select( ".js-tweet-text" ).first();
            if (null != textBlock) {
                descrption.append( textBlock.ownText() );
                for (Element linkElement : textBlock.select( "a[data-expanded-url]" )) {
                    descrption.append(
                            String.format( "<br><a href=\"%1$s\">%1$s</a>", linkElement.attr( "data-expanded-url" ) ) );
                }
            }
            for (Element photoElement : o.select( ".js-adaptive-photo[data-image-url]" )) {
                descrption.append( String.format( "<img src=\"%1$s\">", photoElement.attr( "data-image-url" ) ) );
            }

            long pubDate = Long.parseLong( o.select( ".js-short-timestamp" ).attr( "data-time" ) );
            String link = URL + o.select( ".time a" ).attr( "href" );
            return new RssItem( title, link, descrption.toString(), pubDate );
        } ).collect( Collectors.toList() );
    }
}
