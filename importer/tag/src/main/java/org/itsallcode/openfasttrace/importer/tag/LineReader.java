package org.itsallcode.openfasttrace.importer.tag;

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
        void readLine(int lineNumber, String line);
    }
}
