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
import java.util.logging.*;

import org.junit.rules.ExternalResource;
import org.junit.rules.TestRule;

/**
 * This junit {@link TestRule} sets a temporary log level and a
 * {@link NoOpLoggingHandler} during tests and restores the original state after
 * the test. This allows you to run the tests with all log messages but without
 * the overhead of spamming the log.
 */
public class LogHandlerRule extends ExternalResource
{
    private static Logger LOG = Logger.getLogger(LogHandlerRule.class.getName());
    private final Logger rootLogger;
    private final Handler loggingHandler;
    private final Level logLevel;
    private Level originalLogLevel;

    private LogHandlerRule(final Handler loggingHandler, final Level logLevel)
    {
        this.logLevel = logLevel;
        this.rootLogger = getRootLogger();
        this.loggingHandler = loggingHandler;
    }

    private static Logger getRootLogger()
    {
        return LogManager.getLogManager().getLogger("");
    }

    /**
     * Creates a new {@link LogHandlerRule} that uses log level
     * {@link Level#ALL}.
     */
    public static LogHandlerRule withNoOpHandler()
    {
        final NoOpLoggingHandler handler = new NoOpLoggingHandler();
        return new LogHandlerRule(handler, Level.ALL);
    }

    @Override
    protected void before()
    {
        installHandler();
        configureLogLevel();
    }

    private void installHandler()
    {
        LOG.finest(() -> "Installing logging handler " + this.loggingHandler);
        this.rootLogger.addHandler(this.loggingHandler);
    }

    private void configureLogLevel()
    {
        this.originalLogLevel = this.rootLogger.getLevel();
        LOG.finest(() -> "Configure log level " + this.logLevel + ", original level was "
                + this.originalLogLevel);
        this.rootLogger.setLevel(this.logLevel);
    }

    @Override
    protected void after()
    {
        restoreLogLevel();
        uninstallHandler();
    }

    private void restoreLogLevel()
    {
        LOG.finest(() -> "Restore original log level " + this.originalLogLevel);
        this.rootLogger.setLevel(this.originalLogLevel);
    }

    private void uninstallHandler()
    {
        final Handler[] handlers = this.rootLogger.getHandlers();
        for (final Handler handler : handlers)
        {
            if (handler == this.loggingHandler)
            {
                LOG.finest(() -> "Uninstalling logging handler " + this.loggingHandler);
                this.rootLogger.removeHandler(handler);
            }
        }
    }

}
