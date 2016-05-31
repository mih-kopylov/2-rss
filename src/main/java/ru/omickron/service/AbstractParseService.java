package ru.omickron.service;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.omickron.model.RssChannel;
import ru.omickron.model.RssItem;

public abstract class AbstractParseService extends AbstractLoadingService {
    @Override
    protected final RssChannel getChannel( String id ) {
        String page = loadPageHtml( id );
        Document document = Jsoup.parse( page );

        RssChannel result = parseChannel( document );
        result.getItems().addAll( parseItems( document ) );
        return result;
    }

    protected abstract String loadPageHtml( String id );

    protected abstract RssChannel parseChannel( Document document );

    protected abstract List<RssItem> parseItems( Document document );

}
