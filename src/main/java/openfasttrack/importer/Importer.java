package openfasttrack.importer;

import java.util.List;

import openfasttrack.core.SpecificationItem;

/**
 * Common interface for all importer implementations
 */
public interface Importer
{
    /**
     * Run the import.
     */
    public List<SpecificationItem> runImport();
}