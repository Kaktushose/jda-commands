package io.github.kaktushose.jdac.internal.logging;

import org.jetbrains.annotations.ApiStatus;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

@ApiStatus.Internal
public class CustomFormatter extends SimpleFormatter {

    // borrowed from JDA :D
    private static final String format = "%1$tF %1$tT [%2$s] [%3$s] %4$s%n%5$s";

    @Override
    public String format(LogRecord record) {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(record.getInstant(), ZoneId.systemDefault());

        String source = record.getSourceClassName() != null ? record.getSourceClassName() : record.getLoggerName();

        String throwable = "";
        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            record.getThrown().printStackTrace(pw);
            pw.close();
            throwable = sw.toString();
        }

        return format.formatted(zdt, source, record.getLevel().getName(), record.getMessage(), throwable);
    }
}
