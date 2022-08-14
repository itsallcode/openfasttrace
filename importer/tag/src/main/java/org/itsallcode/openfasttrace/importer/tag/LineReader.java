package org.itsallcode.openfasttrace.importer.tag;

import java.io.IOException;
import java.io.LineNumberReader;

import org.itsallcode.openfasttrace.api.importer.ImporterException;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;

class LineReader
{
    private final InputFile file;

    LineReader(final InputFile file)
    {
        this.file = file;
    }

    public static LineReader create(final InputFile file)
    {
        return new LineReader(file);
    }

    public void readLines(final LineConsumer consumer)
    {
        int currentLineNumber = 0;
        try (final LineNumberReader reader = new LineNumberReader(this.file.createReader()))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                currentLineNumber = reader.getLineNumber();
                processLine(consumer, currentLineNumber, line);
            }
        }
        catch (final IOException exception)
        {
            throw new ImporterException(
                    "Error reading \"" + this.file + "\" at line " + currentLineNumber, exception);
        }
    }

    private void processLine(final LineConsumer consumer, final int currentLineNumber,
            final String line)
    {
        try
        {
            consumer.readLine(currentLineNumber, line);
        }
        catch (final Exception e)
        {
            throw new ImporterException("Error processing line " + this.file.getPath() + ":"
                    + currentLineNumber + " (" + line + "): " + e.getMessage(), e);
        }
    }

    @FunctionalInterface
    public interface LineConsumer
    {
        /**
         * Process a single line from the input.
         * 
         * @param line
         *            current line.
         * @param lineNumber
         *            number of the current line, starting with {@code 1}
         *            for the first line.
         */
        void readLine(int lineNumber, String line);
    }
}
