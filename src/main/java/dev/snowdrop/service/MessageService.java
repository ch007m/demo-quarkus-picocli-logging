package dev.snowdrop.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class MessageService {
    private static final Logger logger = Logger.getLogger(MessageService.class);

    private String message;

    public MessageService() {}

    public MessageService with(String message) {
        this.message = message;
        return this;
    }

    public String getMessage() {
        logger.info("getMessage() called");
        return message;
    }
}
