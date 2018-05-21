package org.itsallcode.openfasttrace.importer.zip;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
import org.itsallcode.openfasttrace.importer.*;
import org.itsallcode.openfasttrace.importer.input.InputFile;

public class ZipFileImporter implements Importer
{
    private final InputFile file;
    private final MultiFileImporter delegateImporter;

    public ZipFileImporter(final ImporterService importerService, final InputFile file,
            final ImportEventListener listener)
    {
        this(file, importerService.createImporter(listener));
    }

    public ZipFileImporter(final InputFile file, final MultiFileImporter delegateImporter)
    {
        this.file = file;
        this.delegateImporter = delegateImporter;
    }

    @Override
    public void runImport()
    {
        if (!this.file.isRealFile())
        {
            throw new UnsupportedOperationException(
                    "Importing a zip file from a strem is not supported");
        }
        try (ZipFile zip = new ZipFile(this.file.toPath().toFile(), StandardCharsets.UTF_8))
        {
            zip.stream() //
                    .filter(entry -> !entry.isDirectory()) //
                    .map(entry -> createInput(zip, entry)) //
                    .forEach(this.delegateImporter::importFile);
        }
        catch (final IOException e)
        {
            throw new ImporterException("Error reading file " + this.file, e);
        }
    }

    private InputFile createInput(final ZipFile zip, final ZipEntry entry)
    {
        return InputFile.forZipEntry(zip, entry);
    }
}
