package dev.snowdrop;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.logging.Logger;

@ApplicationScoped
public class MessageService {
    Logger LOG = Logger.getLogger(MessageService.class.getName());
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
