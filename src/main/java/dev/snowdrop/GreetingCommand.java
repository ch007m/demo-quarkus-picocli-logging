///usr/bin/env jbang “$0” “$@” ; exit $?
//DEPS io.quarkus.platform:quarkus-bom:3.29.4@pom
//DEPS io.quarkus:quarkus-picocli
//SOURCES service/LoggingFormatingService.java
//SOURCES service/MessageService.java

package dev.snowdrop;

import dev.snowdrop.service.LoggingFormatingService;
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
    LoggingFormatingService LOG;

    @Override
    public void run() {
        // Pass the Picocli Command Spec to the LOG service
        LOG.setSpec(spec);

        // set the Hello message
        msgService.with("picocli");

        // Messages to log
        LOG.info(String.format("Hello %s, go go commando!", name));
        LOG.warn("Hello is not very happy today !");
        LOG.error("Hello raises an error :-(");
    }

}
