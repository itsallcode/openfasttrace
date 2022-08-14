package org.itsallcode.openfasttrace.mode;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

abstract class AbstractOftTest
{
    protected static final String NEWLINE = "\n";
    protected static final String CARRIAGE_RETURN = "\r";

    protected Path docDir;
    protected Path outputFile;
    protected final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    protected final ByteArrayOutputStream error = new ByteArrayOutputStream();

    protected void perpareOutput(final Path outputDir) throws UnsupportedEncodingException
    {
        this.docDir = Paths.get("../core/src/test/resources/markdown").toAbsolutePath();
        this.outputFile = outputDir.resolve("stream.txt");
        System.setOut(new PrintStream(this.outputStream, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(this.error, true, StandardCharsets.UTF_8));
    }

    protected void assertOutputFileContentStartsWith(final String content) throws IOException
    {
        assertThat(getOutputFileContent(), startsWith(content));
    }

    protected void assertOutputFileExists(final boolean fileExists)
    {
        assertThat("Output file exists", Files.exists(this.outputFile), equalTo(fileExists));
    }

    protected String getOutputFileContent() throws IOException
    {
        final Path file = this.outputFile;
        return Files.readString(file);
    }

    protected void assertStdOutStartsWith(final String content)
    {
        assertThat(this.outputStream.toString(), startsWith(content));
    }

    protected void assertStdOutEmpty()
    {
        assertThat("STDOUT stream is empty", this.outputStream.size(), equalTo(0));
    }
}