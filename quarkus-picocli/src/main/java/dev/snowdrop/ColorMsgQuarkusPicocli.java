/// usr/bin/env jbang “$0” “$@” ; exit $?
//SOURCES logging/PicocliColorHandler.java
//DEPS org.aesh:terminal-tty:3.2
//DEPS info.picocli:picocli:4.7.7
//DEPS org.jboss.logmanager:jboss-logmanager:3.2.1.Final
//RUNTIME_OPTIONS -Djava.util.logging.manager=org.jboss.logmanager.LogManager
package dev.snowdrop;

import dev.snowdrop.logging.LoggerUtils;
import dev.snowdrop.logging.LoggingConfiguration;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jboss.logmanager.Level;
import org.jboss.logmanager.LogManager;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import jakarta.enterprise.context.Dependent;

import java.io.IOException;

@Command(name = "greeting",
        mixinStandardHelpOptions = true,
        version = "1.0",
        description = "Quarkus Picocli and JBoss LogManager")
public class ColorMsgQuarkusPicocli implements Runnable {
    static LogManager logManager = (LogManager) LogManager.getLogManager();
    static Logger logger = Logger.getLogger(ColorMsgQuarkusPicocli.class.getName());

    @Spec
    CommandSpec spec;

    @Option(names = {"-c", "--color"}, negatable = true, defaultValue = "false",
            description = "Enable or disable colored output (default: ${DEFAULT-VALUE})")
    boolean color;

    @CommandLine.Option(
            names = {"-v"},
            description = "Enable more tracing output: WARN, DEBUG, TRACE using -v, -vv or -vvv respectively")
    boolean[] verbosity = new boolean[0];

    @CommandLine.Option(names = {"-n", "--name"}, description = "Who will we greet?", defaultValue = "World")
    String name;

    private final GreetingService greetingService;
    @Inject
    LoggingConfiguration loggingConfig;

    public ColorMsgQuarkusPicocli(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @Override
    public void run() {
        if (color) {
            LoggerUtils loggerUtils = new LoggerUtils();
            loggerUtils.setupLogManagerAndHandler(loggingConfig, verbosity.length,spec);
            logger.infof("Theme of the terminal is: %s", loggerUtils.isTerminalDark() == 0 ? "Dark" : "Light");
        } else {
            org.jboss.logmanager.Logger rootLogger = logManager.getLogger(ColorMsgQuarkusPicocli.class.getName());
            rootLogger.setLevel(Level.ALL);
            rootLogger.setUseParentHandlers(true);
        }

        // Calling the service packaged in an external module
        HelloService helloService = new HelloService();
        helloService.sendMessage(name);

        logger.infof("Verbosity selected: ",verbosity.length);

        // Messages logged with the following levels
        logger.fatalf("Hello %s, this is a FATAL message.");
        logger.errorf("Hello %s, this is an ERROR message.");
        logger.warnf("Hello %s, this is a WARN message.");
        logger.infof("Hello %s, this is an INFO message.");
        logger.debugf("Hello %s, this is a DEBUG message.");
        logger.tracef("Hello %s, this is a TRACE message.");

        greetingService.sayHello(name);
    }
}

@Dependent
class GreetingService {
    void sayHello(String name) {
        System.out.println("Hello " + name + "!");
    }
}