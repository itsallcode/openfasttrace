package org.itsallcode.openfasttrace.core.cli.logging;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.itsallcode.openfasttrace.core.cli.CliArguments;

public class LoggingConfigurator
{
    private static final String CONFIG_TEMPLATE = """
            handlers=java.util.logging.ConsoleHandler
            .level=$LOG_LEVEL
            java.util.logging.ConsoleHandler.level=$LOG_LEVEL
            java.util.logging.ConsoleHandler.formatter=java.util.logging.SimpleFormatter
            java.util.logging.SimpleFormatter.format=%1$tF %1$tT.%1$tL [%4$-7s] %5$s %n
            org.itsallcode.openfasttrace.level=$LOG_LEVEL
            """;
    private final LogLevel logLevel;

    private LoggingConfigurator(final LogLevel logLevel)
    {
        this.logLevel = logLevel;
    }

    public static LoggingConfigurator create(final CliArguments arguments)
    {
        return new LoggingConfigurator(arguments.getLogLevel().orElse(LogLevel.INFO));
    }

    public void configureLogging()
    {
        final LogManager logManager = LogManager.getLogManager();
        configureLogManager(logManager, getConfigContent());
        final Logger rootLogger = logManager.getLogger("");
        rootLogger.info(() -> "Logging configured with level " + this.logLevel + ".");
    }

    private String getConfigContent()
    {
        return CONFIG_TEMPLATE.replace("$LOG_LEVEL", this.logLevel.getJulLogLevel().getName());
    }

    private void configureLogManager(final LogManager logManager, final String configContent)
    {
        try (InputStream config = new ByteArrayInputStream(configContent.getBytes(StandardCharsets.UTF_8)))
        {
            logManager.readConfiguration(config);
        }
        catch (SecurityException | IOException exception)
        {
            throw new IllegalStateException("Failed to configure logging", exception);
        }
    }
}
