package dev.snowdrop.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MessageService {

    @Inject
    LoggingFormatingService LOG;

    private String message;

    public MessageService() {}

    public MessageService with(String message) {
        LOG.info(String.format("Here is the message to use to say Hello: %s",message));
        this.message = message;
        return this;
    }

    public String getMessage() {
        return message;
    }
}
