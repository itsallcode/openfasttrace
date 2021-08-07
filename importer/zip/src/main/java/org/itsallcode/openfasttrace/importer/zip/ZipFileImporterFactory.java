package org.itsallcode.openfasttrace.importer.zip;

/*-
 * #%L
 * OpenFastTrace Zip Importer
 * %%
 * Copyright (C) 2016 - 2019 itsallcode.org
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

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.itsallcode.openfasttrace.api.importer.*;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;

/**
 * {@link ImporterFactory} for importing {@link ZipEntry}s of a {@link ZipFile}
 * using a {@link ZipFileImporter}.
 */
public class ZipFileImporterFactory extends RegexMatchingImporterFactory
{
    /** Creates a new instance. */
    public ZipFileImporterFactory()
    {
        super("(?i).*\\.(zip)");
    }

    @Override
    public Importer createImporter(final InputFile file, final ImportEventListener listener)
    {
        return new ZipFileImporter(getContext().getImporterService(), file, listener);
    }
}
