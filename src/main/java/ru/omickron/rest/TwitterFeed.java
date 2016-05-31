package ru.omickron.rest;

import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.omickron.model.Rss;
import ru.omickron.service.TwitterService;

@RestController
@RequestMapping(value = "/twitter")
public class TwitterFeed {
    private final TwitterService service;

    @Autowired
    public TwitterFeed( TwitterService service ) {
        this.service = service;
    }

    @RequestMapping(value = "/{id:.*}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_XML})
    public Rss getChannel( @PathVariable("id") String id ) {
        return service.getRss( id );
    }
}
