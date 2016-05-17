package ru.omickron.rest;

import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.omickron.model.RssChannel;
import ru.omickron.service.VkService;

@RestController
@RequestMapping(value = "/vk")
public class VkFeedFacade extends FeedFacade {
    @Qualifier("apiVkService")
    @Autowired
    private VkService vkService;

    @RequestMapping(value = "/{idClub:.*}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_XML})
    public RssChannel getChannel( @PathVariable("idClub") String idClub ) {
        return vkService.getChannel( idClub );
    }
}
