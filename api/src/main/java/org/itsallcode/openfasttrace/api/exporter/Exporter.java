package org.itsallcode.openfasttrace.api.exporter;

/**
 * Interface for exporters.
 */
@FunctionalInterface
public interface Exporter
{
    /**
     * Start the export process.
     */
    void runExport();
}
