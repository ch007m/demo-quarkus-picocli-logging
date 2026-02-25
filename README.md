# How to colorize messages logged

The goal of this project is to demo how we can display colorized messages in a terminal where the theme has been detected: dark or light using as
terminal theme detector - Aesh and JBoss LogManager using a color formater.

## Aesh

Using Aesh and LogManager to colorize the messages to be logged

```bash
set AESH_GAV "$HOME/.m2/repository/dev/snowdrop/aesh/1.0.0-SNAPSHOT/aesh-1.0.0-SNAPSHOT-jar-with-dependencies.jar"
alias colorWithAesh='java -Djava.util.logging.manager=org.jboss.logmanager.LogManager -cp $AESH_GAV dev.snowdrop.ColorMsgAeshApp'
colorWithAesh

or 

java -Djava.util.logging.manager=org.jboss.logmanager.LogManager \
        -cp "$HOME/.m2/repository/dev/snowdrop/aesh/1.0.0-SNAPSHOT/aesh-1.0.0-SNAPSHOT-jar-with-dependencies.jar" \
        dev.snowdrop.ColorMsgAeshApp
```
1. Light Theme

![Light](./image/aesh-light.png)

2. Dark theme

![Dark](./image/aesh-dark.png)

## Standalone application using JBoss LogManager

Using the JBoss LogManager and a ColorHandler for colorize and format the messages
```bash
set LOGMANAGER_GAV "$HOME/.m2/repository/dev/snowdrop/logmanager/1.0.0-SNAPSHOT/logmanager-1.0.0-SNAPSHOT-jar-with-dependencies.jar"
alias colorWithLogManager='java -Djava.util.logging.manager=org.jboss.logmanager.LogManager -cp $LOGMANAGER_GAV dev.snowdrop.ColorMsgLogManagerApp'
colorWithLogManager

or 

java -Djava.util.logging.manager=org.jboss.logmanager.LogManager \
  -cp "$HOME/.m2/repository/dev/snowdrop/logmanager/1.0.0-SNAPSHOT/logmanager-1.0.0-SNAPSHOT-jar-with-dependencies.jar" \
  dev.snowdrop.ColorMsgLogManagerApp
```

1. Light Theme

![Light](./image/logmanager-light.png)

2. Dark theme

![Dark](./image/logmanager-dark.png)

## JBang Application using JBoss LogManager

Using the JBoss LogManager and a ColorHandler for colorize and format the messages
```bash
jbang ./jbang/src/main/java/dev/snowdrop/ColorMsgLogManagerJBangApp.java
```

1. Light Theme

![Light](./image/jbang-light.png)

2. Dark theme

![Dark](./image/jbang-dark.png)


## Picocli

Using Picocli, Aesh and JBoss LogManager to colorize and format the messages
```bash
set PICOCLI_GAV "$HOME/.m2/repository/dev/snowdrop/picocli/1.0.0-SNAPSHOT/picocli-1.0.0-SNAPSHOT-jar-with-dependencies.jar"
alias colorWithPicocli='java -Djava.util.logging.manager=org.jboss.logmanager.LogManager -cp $PICOCLI_GAV dev.snowdrop.ColorMsgPicocliApp'
```

1. Using JBoss Loggger
```bash
colorWithPicocli --name snowdrop
```

2. Using a PicocliColorHandler
```bash
colorWithPicocli --name snowdrop -c
```

![Light](./image/picocli-light.png)

![Dark](./image/picocli-dark.png)

## Quarkus & Picocli

Using Quarkus Picocli, Aesh and JBoss LogManager to colorize and format the messages

1. Default Quarkus Logger disabled
```bash
java -Djava.util.logging.manager=org.jboss.logmanager.LogManager -jar ./target/quarkus-picocli-1.0.0-SNAPSHOT-runner.jar 
```

2. Combining the Handler with Quarkus Picocli
```bash
java -Djava.util.logging.manager=org.jboss.logmanager.LogManager -jar ./target/quarkus-picocli-1.0.0-SNAPSHOT-runner.jar -c
```

3. Changing the format of the messages to be logged
```bash
java -Dcli.log.msg.format="greeting-app: %s%e%n" -Djava.util.logging.manager=org.jboss.logmanager.LogManager -jar ./target/quarkus-picocli-1.0.0-SNAPSHOT-runner.jar -c 
```


![Light](./image/quarkus-picocli-light.png)

![Dark](./image/quarkus-picocli-dark.png)
