package ru.omickron.service;

import ru.omickron.model.RssChannel;

public interface VkService {
    public static final String LINK_VK = "http://vk.com/";

    public RssChannel getChannel( String id );
}
