package ru.omickron.controller;

import javax.ws.rs.FormParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.omickron.model.RssChannel;
import ru.omickron.service.VkService;

@Controller
@RequestMapping("/")
public class HelloController {
    @Qualifier("apiVkService")
    @Autowired
    private VkService vkService;
    @Autowired
    private VkService testService;

    @RequestMapping(method = RequestMethod.GET)
    public String printWelcome() {
        return "hello";
    }

    @RequestMapping(value = "/vk/{idClub:.*}", method = RequestMethod.GET)
    public String getVkFeed( ModelMap model, @PathVariable("idClub") String idClub ) {
        RssChannel channel = vkService.getChannel( idClub );
        model.addAttribute( "channel", channel );
        return "rss";
    }

    @RequestMapping(value = "test", method = RequestMethod.GET)
    public String getTestFeed( ModelMap modelMap ) {
        RssChannel channel = testService.getChannel( "" );
        modelMap.addAttribute( "channel", channel );
        return "rss";
    }

    @RequestMapping(value = "/feed", method = RequestMethod.POST)
    public String newVkFeed( @FormParam("link") String link ) {
        if (link.startsWith( VkService.LINK_VK )) {
            String id = link.substring( VkService.LINK_VK.length() );
            if (id.matches( "[\\w,\\.]+" )) {
                return "redirect:/vk/" + id;
            }
        }
        return "hello";
    }
}