package openfasttrack.importer.legacytag;

/*-
 * #%L
 * OpenFastTrack
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
import java.nio.file.Path;

import openfasttrack.importer.ImportEventListener;
import openfasttrack.importer.Importer;
import openfasttrack.importer.ImporterException;

class LegacyTagImporter implements Importer
{
    private final PathConfig pathConfig;
    private final BufferedReader reader;
    private final ImportEventListener listener;
    private final Path file;

    LegacyTagImporter(final PathConfig pathConfig, final Path file, final BufferedReader reader,
            final ImportEventListener listener)
    {
        this.pathConfig = pathConfig;
        this.file = file;
        this.reader = reader;
        this.listener = listener;
    }

    @Override
    public void runImport()
    {
        String line;
        int lineNumber = 0;
        try
        {
            while ((line = this.reader.readLine()) != null)
            {
                ++lineNumber;
                processLine(lineNumber, line);
            }
        }
        catch (final IOException e)
        {
            throw new ImporterException("Error reading file " + this.file + ":" + lineNumber, e);
        }
    }

    private void processLine(final int lineNumber, final String line)
    {

    }
}
