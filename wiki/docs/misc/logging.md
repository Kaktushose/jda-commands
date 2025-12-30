# Logging
JDA-Commands uses [SLF4J](https://slf4j.org/) as its logging framework allowing bot developers to use an own logging backend.

If you see following on startup:
```
SLF4J(W): No SLF4J providers were found.
SLF4J(W): Defaulting to no-operation (NOP) logger implementation
SLF4J(W): See https://www.slf4j.org/codes.html#noProviders for further details.
```

and/ or
```

[JDALogger] [WARN] Using fallback logger due to missing SLF4J implementation.
[JDALogger] [WARN] Please setup a logging framework to use JDA.
[JDALogger] [WARN] You can use our logging setup guide https://jda.wiki/setup/logging/
[JDALogger] [WARN] To disable the fallback logger, add the slf4j-nop dependency or use JDALogger.setFallbackLoggerEnabled(false)
```

It means that you have to configure a logging framework. You can consult the [JDA documentation about it](https://jda.wiki/setup/logging/).

Per default, we will provide a very basic fallback logger based on [java.util.logging][[java.util.logging.Logger]].
Please consult its documentation to know how to configure it.

!!! info
    Sometimes  `java.util.logging` is referred to as `JUL`. Don't be confused, it's the same!

## The Fallback Logger
The fallback logger is a simple SLF4J logger built around [java.util.logging.Logger][[java.util.logging.Logger]]
with a log format similar to JDAs fallback logger format.

The log levels are translated following:

- ERROR -> <java.util.logging.Level#SEVERE> (JUL)
- WARN -> <java.util.logging.Level#WARNING> (JUL)
- INFO -> <java.util.logging.Level#INFO> (JUL)
- DEBUG -> <java.util.logging.Level#FINE> (JUL)
- TRACE -> <java.util.logging.Level#FINEST> (JUL)

### Debug Mode
If you want to enable debug logging for the fallback logger you have to somewhere create a `logging.properties`
with following contents:

```properties title="logging.properties"
.level = FINE

handlers = java.util.logging.ConsoleHandler
java.util.logging.ConsoleHandler.level = FINE
```

Then add this to your JVM args:
`-Djava.util.logging.config.file=<your_path_to_logging.properties>`

For example, your Java command could look like this:

```shell
java -Djava.util.logging.config.file=./app/src/main/resources/logging.properties -jar myBot.jar
```

!!! danger
    Using the fallback logger is not recommended. As said before, consult the [JDA documentation](https://jda.wiki/setup/logging/)
    on how to setup proper logging. 
