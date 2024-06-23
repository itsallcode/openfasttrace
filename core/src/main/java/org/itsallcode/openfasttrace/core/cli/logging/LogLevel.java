package org.itsallcode.openfasttrace.core.cli.logging;

import java.util.logging.Level;

public enum LogLevel
{
    OFF(Level.OFF), SEVERE(Level.SEVERE), WARNING(Level.WARNING), INFO(Level.INFO), CONFIG(Level.CONFIG), FINE(
            Level.FINE), FINER(Level.FINER), FINEST(Level.FINEST), ALL(Level.ALL);

    private final Level julLevel;

    private LogLevel(final Level julLevel)
    {
        this.julLevel = julLevel;
    }

    Level getJulLogLevel()
    {
        return julLevel;
    }
}
