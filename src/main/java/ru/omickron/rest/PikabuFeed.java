package ru.omickron.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.omickron.model.Rss;
import ru.omickron.service.PikabuService;

@RestController
@RequestMapping(value = PikabuFeed.URL)
public class PikabuFeed {
    public static final String URL = "/pikabu";
    private final PikabuService service;

    @Autowired
    public PikabuFeed( PikabuService service ) {
        this.service = service;
    }

    @RequestMapping(value = "/**", method = RequestMethod.GET, produces = {MediaType.APPLICATION_XML})
    public Rss getChannel( HttpServletRequest request ) {
        String servletPath = request.getServletPath();
        return service.getRss( servletPath.substring( URL.length() ) );    }
}
