package org.itsallcode.openfasttrace.importer.tag.common;

import java.io.IOException;
import java.io.LineNumberReader;

import org.itsallcode.openfasttrace.api.importer.ImporterException;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;

/**
 * Reads an input file line by line and forwards every line to a consumer.
 */
public final class LineReader {
    private final InputFile file;

    private LineReader(final InputFile file) {
        this.file = file;
    }

    /**
     * Create a reader for an input file.
     *
     * @param file
     *             input file to read
     * @return line reader for the input file
     */
    public static LineReader create(final InputFile file) {
        return new LineReader(file);
    }

    /**
     * Read the input file and forward each line to the consumer.
     *
     * @param consumer
     *                 consumer receiving line numbers and line content
     * @throws ImporterException
     *                           if the input cannot be read or a line cannot be
     *                           processed
     */
    public void readLines(final LineConsumer consumer) {
        int currentLineNumber = 0;
        try (final LineNumberReader reader = new LineNumberReader(this.file.createReader())) {
            String line;
            while ((line = reader.readLine()) != null) {
                currentLineNumber = reader.getLineNumber();
                processLine(consumer, currentLineNumber, line);
            }
        } catch (final IOException exception) {
            throw new ImporterException("Error reading \"" + this.file + "\" at line " + currentLineNumber, exception);
        }
    }

    private void processLine(final LineConsumer consumer, final int currentLineNumber,
            final String line) {
        try {
            consumer.readLine(currentLineNumber, line);
        } catch (final RuntimeException exception) {
            throw new ImporterException("Error processing line " + this.file.getPath() + ":" + currentLineNumber + " '"
                    + line + "': " + exception, exception);
        }
    }

    /**
     * Receives a line read from an input file.
     */
    @FunctionalInterface
    public interface LineConsumer {
        /**
         * Process a single input line.
         *
         * @param lineNumber
         *                   line number, starting at {@code 1}
         * @param line
         *                   line content without its line separator
         */
        void readLine(int lineNumber, String line);
    }
}
