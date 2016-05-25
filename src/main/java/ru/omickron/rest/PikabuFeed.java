package ru.omickron.rest;

import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.omickron.model.Rss;
import ru.omickron.service.PikabuService;

@RestController
@RequestMapping(value = "/pikabu")
public class PikabuFeed {
    private final PikabuService service;

    @Autowired
    public PikabuFeed( PikabuService service ) {
        this.service = service;
    }

    @RequestMapping(value = "/{type:.*}/{id:.*}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_XML})
    public Rss getChannel( @PathVariable("type") String type, @PathVariable("id") String id ) {
        return service.getRss( type, id );
    }
}
