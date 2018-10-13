package org.itsallcode.openfasttrace.testutil.log;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Logging {@link Handler} that ignores all log messages. This is useful for
 * testing with the highest log level but without spamming the console with log
 * messages.
 */
public class NoOpLoggingHandler extends Handler
{
    public NoOpLoggingHandler()
    {
    }

    @Override
    public void publish(final LogRecord record)
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
