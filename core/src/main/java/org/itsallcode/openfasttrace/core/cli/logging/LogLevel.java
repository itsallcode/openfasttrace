package org.itsallcode.openfasttrace.core.cli.logging;

import java.util.logging.Level;

/**
 * Log levels for console logging.
 * <p>
 * We can't use {@code java.util.logging} (JUL) {@link java.util.logging.Level}
 * directly for configuration because it is not an enum.
 */
public enum LogLevel
{
    /**
     * OFF is a special level that can be used to turn off logging.
     */
    OFF(Level.OFF),
    /**
     * SEVERE is a message level indicating a serious failure.
     */
    SEVERE(Level.SEVERE),
    /**
     * WARNING is a message level indicating a potential problem.
     */
    WARNING(Level.WARNING),
    /**
     * INFO is a message level for informational messages.
     */
    INFO(Level.INFO),
    /**
     * CONFIG is a message level for static configuration messages.
     */
    CONFIG(Level.CONFIG),
    /**
     * FINE is a message level providing tracing information.
     */
    FINE(Level.FINE),
    /**
     * FINER indicates a fairly detailed tracing message.
     */
    FINER(Level.FINER),
    /**
     * FINEST indicates a highly detailed tracing message.
     */
    FINEST(Level.FINEST),
    /**
     * ALL indicates that all messages should be logged.
     */
    ALL(Level.ALL);

    private final Level julLevel;

    LogLevel(final Level julLevel)
    {
        this.julLevel = julLevel;
    }

    String getJavaUtilLoggingLogLevel()
    {
        return julLevel.getName();
    }
}
