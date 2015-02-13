package ru.omickron.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;
import ru.omickron.model.RssChannel;
import ru.omickron.model.RssItem;

@Component
public class TestService implements VkService {
    private static final List<RssItem> ITEMS = new ArrayList<RssItem>();

    @Override
    public RssChannel getChannel( String id ) {
        RssChannel channel = new RssChannel( "Test Channel", "http://localhost", "Test Channel" );
        Date date = new Date();
        String title = new SimpleDateFormat( "dd.MM.yyyy HH:mm:ss" ).format( date );
        RssItem item = new RssItem( title, "", "", date.getTime() / 1000 );
        ITEMS.add(0, item );
        channel.getItems().addAll( ITEMS );
        return channel;
    }
}
