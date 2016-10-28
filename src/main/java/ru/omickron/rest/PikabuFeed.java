package ru.omickron.rest;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.OptionalInt;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;
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
    @SneakyThrows
    public Rss getChannel( HttpServletRequest request ) {
        Charset encoding1 = StandardCharsets.ISO_8859_1;
        Charset encoding2 = StandardCharsets.UTF_8;
        String servletPath1 =
                new String( UriUtils.decode( request.getRequestURI(), encoding1.displayName() ).getBytes( encoding1 ) );
        String servletPath2 = UriUtils.decode( request.getRequestURI(), encoding2.displayName() );
        String servletPath = choosePath( servletPath1, servletPath2 );
        return service.getRss( servletPath.substring( URL.length() ) );
    }

    private String choosePath( String servletPath1, String servletPath2 ) {
        String letters = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя";
        OptionalInt path2 = letters.chars().filter( o -> servletPath2.indexOf( o ) >= 0 ).findAny();
        if (path2.isPresent()) {
            return servletPath2;
        }
        return servletPath1;
    }
}
