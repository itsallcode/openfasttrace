package org.itsallcode.openfasttrace.api.importer;

import java.util.List;

import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;

/**
 * This service provides convenient methods for importing
 * {@link SpecificationItem}s that automatically use the correct
 * {@link Importer} based on the filename.
 */
public interface ImporterService
{
    /**
     * Import a file's contents
     * 
     * @param file
     *            file to be imported
     * @return list of recognized specification items
     */
    List<SpecificationItem> importFile(InputFile file);

    /**
     * Create a new {@link MultiFileImporter} using the given
     * {@link ImportEventListener}.
     * 
     * @param builder
     *            the builder used by the new importer.
     * @return a new importer.
     */
    MultiFileImporter createImporter(ImportEventListener builder);

    /**
     * Create a new {@link MultiFileImporter}.
     * 
     * @return a new importer.
     */
    MultiFileImporter createImporter();
}
