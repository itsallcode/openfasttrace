package org.itsallcode.openfasttrace.api.importer;

import org.itsallcode.openfasttrace.api.core.serviceloader.Initializable;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;

/**
 * Factory for creating {@link Importer}s for supported input files.
 */
public interface ImporterFactory extends Initializable<ImporterContext> {
    /**
     * Returns {@code true} if this importer factory supports importing the
     * given file.
     *
     * @param file file to check.
     * @return {@code true} if the given file is supported for importing.
     */
    boolean supportsFile(InputFile file);

    /**
     * Create an importer that is able to read the given file.
     *
     * @param file     file from which specification items are imported
     * @param listener listener to be informed about detected specification item
     *                 fragments
     * @return an importer instance
     */
    Importer createImporter(InputFile file, ImportEventListener listener);
}
