package org.itsallcode.openfasttrace.importer.zip;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.itsallcode.openfasttrace.api.importer.*;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.importer.zip.input.ZipEntryInput;

/**
 * This {@link Importer} supports reading {@link ZipFile} and delegates import
 * of {@link ZipEntry}s to a {@link MultiFileImporter}.
 */
public class ZipFileImporter implements Importer
{
    private final InputFile file;
    private final MultiFileImporter delegateImporter;

    ZipFileImporter(final ImporterService importerService, final InputFile file,
            final ImportEventListener listener)
    {
        this(file, importerService.createImporter(listener));
    }

    ZipFileImporter(final InputFile file, final MultiFileImporter delegateImporter)
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
                    "Importing a zip file from a stream is not supported");
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
            throw new ImporterException("Error reading \"" + this.file + "\"", e);
        }
    }

    private InputFile createInput(final ZipFile zip, final ZipEntry entry)
    {
        return ZipEntryInput.forZipEntry(zip, entry);
    }
}
