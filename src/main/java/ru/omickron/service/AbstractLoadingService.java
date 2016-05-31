package ru.omickron.service;

import com.sun.jersey.api.client.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import ru.omickron.model.Rss;
import ru.omickron.model.RssChannel;

@Slf4j
public abstract class AbstractLoadingService {
    protected final Client client = Client.create();

    @Cacheable(value = "rss")
    public final Rss getRss( String id ) {
        log.debug( "Generating channel {}", id );
        return new Rss( getChannel( id ) );
    }

    protected abstract RssChannel getChannel( String id );
}
