package dev.snowdrop.service;

import dev.snowdrop.logging.LoggingService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MessageService {

    @Inject
    LoggingService LOG;

    private String message;

    public MessageService() {
    }

    public MessageService with(String message) {
        LOG.info(String.format("Here is the message to use to say Hello: %s", message));
        this.message = message;
        return this;
    }

    public String getMessage() {
        return message;
    }
}
