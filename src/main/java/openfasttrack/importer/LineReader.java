package openfasttrack.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class LineReader implements AutoCloseable
{
    private final LineNumberReader reader;
    private final Path file;

    LineReader(final Path file, final LineNumberReader reader)
    {
        this.file = file;
        this.reader = reader;
    }

    public static LineReader create(final Path file, final Charset charset)
    {
        final BufferedReader bufferedReader = createReader(file, charset);
        return create(file, bufferedReader);
    }

    public static LineReader create(final Path file, final Reader reader)
    {
        return new LineReader(file, new LineNumberReader(reader));
    }

    private static BufferedReader createReader(final Path file, final Charset charset)
    {
        try
        {
            return Files.newBufferedReader(file, charset);
        }
        catch (final IOException e)
        {
            throw new ImporterException("Error reading file '" + file + "': " + e.getMessage(), e);
        }
    }

    public void readLines(final LineConsumer consumer)
    {
        try
        {
            String line;
            while ((line = this.reader.readLine()) != null)
            {
                consumer.readLine(getOneBasedLineNumber(), line);
            }
        }
        catch (final IOException e)
        {
            throw new ImporterException(
                    "Error reading file " + this.file + ":" + getOneBasedLineNumber(), e);
        }
    }

    private int getOneBasedLineNumber()
    {
        return this.reader.getLineNumber();
    }

    @FunctionalInterface
    public interface LineConsumer
    {
        void readLine(int lineNumber, String line);
    }

    @Override
    public void close() throws IOException
    {
        this.reader.close();
    }
}
