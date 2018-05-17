package org.itsallcode.openfasttrace.importer;

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

import java.nio.charset.Charset;
import java.nio.file.Path;

import org.itsallcode.openfasttrace.importer.input.InputFile;

/**
 * Super class for factories producing {@link Importer}s.
 */
public abstract class ImporterFactory
{
    /**
     * Returns <code>true</code> if this {@link ImporterFactory} supports
     * importing the given file based on its file extension.
     *
     * @param file
     *            the file to check.
     * @return <code>true</code> if the given file is supported for importing.
     */
    public abstract boolean supportsFile(final InputFile file);

    /**
     * @deprecated use {@link #supportsFile(InputFile)}
     */
    @Deprecated
    public boolean supportsFile(final Path file)
    {
        return supportsFile(InputFile.createForPath(file));
    }

    /**
     * Create an importer that is able to read the given file.
     *
     * @param file
     *            the file from which specification items are imported
     * @param listener
     *            the listener to be informed about detected specification item
     *            fragments
     * @return an importer instance
     */
    public abstract Importer createImporter(final InputFile file,
            final ImportEventListener listener);

    /**
     * @deprecated use {@link #createImporter(InputFile, ImportEventListener)}
     */
    @Deprecated
    public Importer createImporter(final Path file, final Charset charset,
            final ImportEventListener listener)
    {
        return createImporter(InputFile.createForPath(file, charset), listener);
    }
}
