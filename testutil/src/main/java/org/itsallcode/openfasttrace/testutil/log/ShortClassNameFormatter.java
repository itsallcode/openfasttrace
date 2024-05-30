package org.itsallcode.openfasttrace.testutil.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public final class ShortClassNameFormatter extends Formatter
{
    private static final String FORMAT = "%1$tF %1$tT [%4$s] %2$s - %5$s %6$s%n";

    public ShortClassNameFormatter()
    {
        // Suppress warning "class ... declares no explicit constructors,
        // thereby exposing
        // a default constructor"
    }

    @Override
    public String format(final LogRecord logRecord)
    {
        return String.format(FORMAT,
                getTimestamp(logRecord),
                getSource(logRecord),
                logRecord.getLoggerName(),
                logRecord.getLevel().toString(),
                formatMessage(logRecord),
                formatThrowable(logRecord));
    }

    private static String getSource(final LogRecord logRecord)
    {
        if (logRecord.getSourceClassName() != null)
        {
            String source = shortenClassName(logRecord.getSourceClassName());
            if (logRecord.getSourceMethodName() != null)
            {
                source += " " + logRecord.getSourceMethodName();
            }
            return source;
        }
        else
        {
            return logRecord.getLoggerName();
        }
    }

    private static ZonedDateTime getTimestamp(final LogRecord logRecord)
    {
        return ZonedDateTime.ofInstant(logRecord.getInstant(), ZoneId.systemDefault());
    }

    private static String formatThrowable(final LogRecord logRecord)
    {
        if (logRecord.getThrown() == null)
        {
            return "";
        }
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        pw.println();
        logRecord.getThrown().printStackTrace(pw);
        pw.close();
        return sw.toString();
    }

    private static String shortenClassName(final String className)
    {
        final String[] parts = className.split("\\.");
        return parts[parts.length - 1];
    }
}
