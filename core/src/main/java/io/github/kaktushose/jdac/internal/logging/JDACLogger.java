package io.github.kaktushose.jdac.internal.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;

public final class JDACLogger {
    public static Logger getLogger(Class<?> klass) {
        Logger logger = LoggerFactory.getLogger(klass);
        return logger instanceof NOPLogger
                ? new FallbackLogger(klass)
                : logger;
    }
}
