package org.itsallcode.openfasttrace.testutil.log;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Logging {@link Handler} that ignores all log messages. This is useful for
 * testing with the highest log level but without spamming the console with log
 * messages.
 */
public class NoOpLoggingHandler extends Handler
{
    /**
     * Creates a new instance of the handler.
     */
    public NoOpLoggingHandler()
    {
        // Default constructor to fix compiler warning "missing-explicit-ctor"
    }

    /**
     * Ignores the log record.
     * 
     * @param logRecord
     *            log record to ignore
     */
    @Override
    public void publish(final LogRecord logRecord)
    {
        // empty on purpose
    }

    /**
     * Does nothing since no logs are stored.
     */
    @Override
    public void flush()
    {
        // empty on purpose
    }

    /**
     * Does nothing since no resources are used.
     */
    @Override
    public void close()
    {
        // empty on purpose
    }
}
