package io.github.kaktushose.jdac.internal.logging;

import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.LegacyAbstractLogger;
import org.slf4j.helpers.MessageFormatter;

import java.util.Optional;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

@ApiStatus.Internal
class FallbackLogger extends LegacyAbstractLogger {

    private static final Formatter FORMATTER = new CustomFormatter();
    private static final ConsoleHandler HANDLER;

    static {
        HANDLER = new ConsoleHandler();
        HANDLER.setFormatter(FORMATTER);
    }

    private final StackWalker WALKER = StackWalker.getInstance();
    private final Class<?> klass;
    private final java.util.logging.Logger logger;

    FallbackLogger(Class<?> klass) {
        this.klass = klass;
        this.logger = java.util.logging.Logger.getLogger(klass.getName());

        logger.setUseParentHandlers(false);
        logger.addHandler(HANDLER);
    }

    private static java.util.logging.Level translateLevel(Level level) {
        return switch (level) {
            case ERROR -> java.util.logging.Level.SEVERE;
            case WARN -> java.util.logging.Level.WARNING;
            case INFO -> java.util.logging.Level.INFO;
            case DEBUG -> java.util.logging.Level.FINE;
            case TRACE -> java.util.logging.Level.FINEST;
        };
    }

    @Override
    protected String getFullyQualifiedCallerName() {
        return logger.getName();
    }

    @Override
    protected void handleNormalizedLoggingCall(
            Level level,
            Marker marker,
            String s,
            Object[] objects,
            Throwable throwable
    ) {
        String formatted = MessageFormatter.basicArrayFormat(s, objects);
        LogRecord record = new LogRecord(translateLevel(level), formatted);

        record.setThrown(throwable);

        findCaller().ifPresent(location -> {
            record.setSourceClassName(location.klass);
            record.setSourceMethodName(location.method);
        });

        logger.log(record);
    }

    private Optional<Location> findCaller() {
        return WALKER.walk(stream -> stream
                .dropWhile(frame -> !frame.getClassName().equals(klass.getName()))
                .findFirst()
                .map(frame -> {
                    String[] nameSplit = frame.getClassName().split("[.]");
                    return new Location(nameSplit[nameSplit.length - 1], frame.getMethodName()); // only use class name
                })
        );
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isLoggable(translateLevel(Level.TRACE));
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isLoggable(translateLevel(Level.DEBUG));
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isLoggable(translateLevel(Level.INFO));
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isLoggable(translateLevel(Level.WARN));
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isLoggable(translateLevel(Level.ERROR));
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return isTraceEnabled();
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return isWarnEnabled();
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return isErrorEnabled();
    }

    private record Location(String klass, String method) { }
}
