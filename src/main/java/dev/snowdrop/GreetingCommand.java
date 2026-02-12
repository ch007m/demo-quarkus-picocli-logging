///usr/bin/env jbang “$0” “$@” ; exit $?
//DEPS io.quarkus.platform:quarkus-bom:3.29.4@pom
//DEPS io.quarkus:quarkus-picocli
//DEPS org.aesh:terminal-tty:3.1
//DEPS org.jboss.logmanager:jboss-logmanager:3.2.1.Final
//SOURCES logging
//SOURCES service
//Q:CONFIG client.mode=true
//Q:CONFIG client.logging.verbose=false
//Q:CONFIG quarkus.banner.enabled=false
//Q:CONFIG quarkus.log.level=WARN

package dev.snowdrop;

import dev.snowdrop.logging.LogFactory;
import dev.snowdrop.logging.LoggingService;
import dev.snowdrop.service.MessageService;
import jakarta.inject.Inject;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "greeting", mixinStandardHelpOptions = true)
public class GreetingCommand implements Runnable {

    @Inject
    LogFactory logFactory;

    private LoggingService LOG;

    @CommandLine.Spec
    void setSpec(CommandLine.Model.CommandSpec spec) {
        logFactory.setSpec(spec);
        this.LOG = logFactory.getLogger();
    };

    @Parameters(paramLabel = "<name>", defaultValue = "picocli", description = "Your name.")
    String name;

    @Inject
    MessageService msgService;

    @Override
    public void run() {

        // set the Hello message
        msgService.with("picocli");

        // Messages to log
        LOG.trace("This is a logging TRACE message.");
        LOG.debug("This is a logging DEBUG message.");
        LOG.info(String.format("Hello %s, go go commando!", name));
        LOG.warn("This is a logging WARN message.");

        try {
            // Emulate a business logic failure
            throw new IllegalStateException("Database connection timed out after 30s");
        } catch (Exception e) {
            LOG.error(String.format("Failed to execute command: %s",e.getMessage()),e);
        }

        LOG.fatal("This is a logging FATAL message.");
    }

}
