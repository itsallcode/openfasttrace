package org.itsallcode.openfasttrace.testutil;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import org.itsallcode.openfasttrace.core.cli.CliStarter;

/**
 * This class is the base class for integration tests that require input files.
 */
// This is a base class for tests and has no tests by intention.
@SuppressWarnings("squid:S2187")
public class AbstractFileBasedTest
{
    @SuppressWarnings("javadoc")
    protected void writeTextFile(final File file, final String content) throws IOException
    {
        final PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8.toString());
        writer.print(content);
        writer.close();
    }

    @SuppressWarnings("javadoc")
    protected void runWithArguments(final String... args)
    {
        CliStarter.main(args);
    }
}