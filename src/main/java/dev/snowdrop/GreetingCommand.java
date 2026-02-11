///usr/bin/env jbang “$0” “$@” ; exit $?
//DEPS io.quarkus.platform:quarkus-bom:3.29.4@pom
//DEPS io.quarkus:quarkus-picocli
//DEPS org.aesh:terminal-tty:3.1
//DEPS org.jboss.logmanager:jboss-logmanager:3.2.1.Final-SNAPSHOT
//SOURCES logging
//SOURCES service
//Q:CONFIG cli.mode=true
//Q:CONFIG cli.logging.colored=true
//Q:CONFIG cli.logging.verbose=false
//Q:CONFIG quarkus.banner.enabled=false
//Q:CONFIG quarkus.log.level=WARN

package dev.snowdrop;

import dev.snowdrop.logging.LoggingService;
import dev.snowdrop.service.MessageService;
import jakarta.inject.Inject;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "greeting", mixinStandardHelpOptions = true)
public class GreetingCommand implements Runnable {

    @Parameters(paramLabel = "<name>", defaultValue = "picocli", description = "Your name.")
    String name;

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @Inject
    MessageService msgService;

    @Inject
    LoggingService LOG;

    @Override
    public void run() {
        // Query the terminal to detect if it is DARK, LIGHt, etc
        LOG.colorDetector();

        // Pass the Picocli Command Spec to the LOG service
        LOG.setSpec(spec);

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
