package org.itsallcode.openfasttrace.testutil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.hamcrest.Matcher;
import org.itsallcode.openfasttrace.cli.JarLauncher;

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
    protected void assertStdOut(final List<String> args, final Matcher<String> stdOutMatcher)
    {
        JarLauncher.builder().args(args).currentWorkingDir().expectStdOut(stdOutMatcher).expectedExitCode(0).start();
    }
}
