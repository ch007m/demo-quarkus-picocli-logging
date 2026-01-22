package dev.snowdrop;

import dev.snowdrop.service.LoggingFormatingService;
import dev.snowdrop.service.MessageService;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "greeting", mixinStandardHelpOptions = true)
public class GreetingCommand implements Runnable {
    private static final Logger logger = Logger.getLogger(GreetingCommand.class);

    @Parameters(paramLabel = "<name>", defaultValue = "picocli",
        description = "Your name.")
    String name;

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @Inject
    MessageService msgService;

    @Inject
    LoggingFormatingService LOG;

    @Override
    public void run() {
        msgService.with("picocli");

        // Pass the Picocli Command Spec to the LOG service
        LOG.setSpec(spec);

        // Messages to log
        LOG.info(String.format("Hello %s, go go commando!", name));
        LOG.warn("Warning msg");
        LOG.error("Error msg");

        /*
         System.out.printf("System out msg: Hello %s, go go commando!%n", msgService.getMessage());
         logger.infof("Log msg: Hello %s, go go commando!", name);

        String warnMsg = "@|bold,yellow WARN: Log Ansi Yellow msg !|@";
        logger.warn(CommandLine.Help.Ansi.AUTO.string(warnMsg));

        String errorMsg = "@|bold,red ERROR: Log Ansi Red msg |@";
        logger.error(CommandLine.Help.Ansi.AUTO.string(errorMsg));

        spec.commandLine().getOut().println(
            CommandLine.Help.Ansi.AUTO.string("@|bold,green Picocli out writer with Ansi Green formated msg |@"));
        spec.commandLine().getErr().println(
            CommandLine.Help.Ansi.AUTO.string("@|bold,red Picocli out writer with Ansi Red formated msg |@"));

         */
    }

}
