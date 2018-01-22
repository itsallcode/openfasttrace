package org.itsallcode.openfasttrace.importer;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 hamstercommunity
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
