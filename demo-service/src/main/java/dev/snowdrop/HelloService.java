package dev.snowdrop;

import org.jboss.logging.Logger;

public class HelloService {
    private final static Logger logger = Logger.getLogger(HelloService.class.getName());

    public HelloService() {}

    public void sendMessage(String user) {
        logger.infof("Hello: %s, using the logger !",user);
    }
}
