package org.itsallcode.openfasttrace.api.importer;

/**
 * Common interface for all importer implementations
 */
@FunctionalInterface
public interface Importer
{
    /**
     * Run the import.
     */
    void runImport();
}
