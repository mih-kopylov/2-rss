package ru.omickron.service;

import com.sun.jersey.api.client.Client;

public class AbstractLoadingService {
    protected final Client client = Client.create();
}
