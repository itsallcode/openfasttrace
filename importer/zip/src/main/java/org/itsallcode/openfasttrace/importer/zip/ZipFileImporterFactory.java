package org.itsallcode.openfasttrace.importer.zip;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.itsallcode.openfasttrace.api.importer.*;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;

/**
 * Importer factory for importing {@link ZipEntry}s of a {@link ZipFile}
 * using a {@link ZipFileImporter}.
 */
public class ZipFileImporterFactory extends AbstractRegexMatchingImporterFactory
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
