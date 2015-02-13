package ru.omickron.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import ru.omickron.model.RssChannel;
import ru.omickron.model.RssItem;

@Component
public class PageVkService implements VkService {
    private static final String VK_CLUB_LINK = "http://m.vk.com/";

    @Override
    public RssChannel getChannel( String id ) {
        final String NO_DATA = "No data";
        if (id == null) {
            return new RssChannel( NO_DATA, NO_DATA, NO_DATA );
        }
        Document page = getPage( id );

        if (page != null) {
            String channelTitle = getChannelTitle( page );
            String channelLink = getChannelLink( id );
            RssChannel channel = new RssChannel( channelTitle, channelLink, channelTitle );
            channel.getItems().addAll( getChannelItems( page ) );
            return channel;
        } else {
            return new RssChannel( null, null, null );
        }
    }

    private List<RssItem> getChannelItems( Document document ) {
        List<RssItem> result = new ArrayList<RssItem>();
        Elements elements = document.select( ".post_item" );
        for (Element element : elements) {
            String link = element.select( ".pi_body .pi_info a.pi_date" ).attr( "href" );
            String text = StringEscapeUtils.escapeXml( element.select( ".pi_body" ).html() );
            String title = element.select( ".pi_head .pi_author" ).text();

            result.add( new RssItem( title, link, text, new Date().getTime() / 1000 ) );
        }
        return result;
    }

    private String getChannelLink( String idClub ) {
        return VK_CLUB_LINK + idClub;
    }

    private String getChannelTitle( Document pageString ) {
        return "Моя лента " + pageString.getElementsByTag( "title" ).text();
    }

    private Document getPage( String idClub ) {
        try {
            return Jsoup.connect( getChannelLink( idClub ) ).get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
