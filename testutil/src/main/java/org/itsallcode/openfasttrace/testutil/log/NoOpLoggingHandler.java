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
    @Override
    public void publish(final LogRecord logRecord)
    {
        // empty on purpose
    }

    @Override
    public void flush()
    {
        // empty on purpose
    }

    @Override
    public void close()
    {
        // empty on purpose
    }
}
