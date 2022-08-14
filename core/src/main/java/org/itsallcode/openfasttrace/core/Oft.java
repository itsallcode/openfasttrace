package org.itsallcode.openfasttrace.core;

import java.nio.file.Path;
import java.util.List;

import org.itsallcode.openfasttrace.api.ReportSettings;
import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.api.importer.ImportSettings;

/**
 * Import requirements (e.g. from ReqM2 to Markdown)
 */
public interface Oft
{
    /**
     * Run an import with default settings
     * 
     * @return list of imported specification items
     */
    List<SpecificationItem> importItems();

    /**
     * Run an import
     * 
     * @param settings
     *            import stage settings
     * 
     * @return list of imported specification items.
     */
    List<SpecificationItem> importItems(ImportSettings settings);

    /**
     * Link specification items
     * 
     * @param items
     *            specification items to be interlinked
     * @return list of linked specification items
     */
    List<LinkedSpecificationItem> link(List<SpecificationItem> items);

    /**
     * Trace a list of linked specification items
     * 
     * @param linkedItems
     *            items to be traced
     * @return trace result
     */
    Trace trace(List<LinkedSpecificationItem> linkedItems);

    /**
     * Export items with default settings
     * 
     * @param items
     *            items to be exported
     * @param path
     *            output path for export
     */
    void exportToPath(List<SpecificationItem> items, final Path path);

    /**
     * Export items
     * 
     * @param items
     *            items to be exported
     * @param path
     *            output path for export
     * @param settings
     *            export settings
     */
    void exportToPath(final List<SpecificationItem> items, final Path path,
            ExportSettings settings);

    /**
     * Generate a report with default settings
     * 
     * @param trace
     *            trace from which the report is generated
     */
    void reportToStdOut(Trace trace);

    /**
     * Generate a report
     * 
     * @param trace
     *            specification item trace to be turned into a report
     * 
     * @param settings
     *            report settings
     */
    void reportToStdOut(Trace trace, ReportSettings settings);

    /**
     * Generate a report
     * 
     * @param trace
     *            specification item trace to be turned into a report
     * 
     * @param outputPath
     *            path the report should be written to (or file in case this is
     *            a single-file report)
     */
    void reportToPath(Trace trace, Path outputPath);

    /**
     * Generate a report
     * 
     * @param trace
     *            specification item trace to be turned into a report
     * 
     * @param outputPath
     *            path the report should be written to (or file in case this is
     *            a single-file report)
     *
     * @param settings
     *            report settings
     */
    void reportToPath(Trace trace, Path outputPath, ReportSettings settings);

    /**
     * Create a new instance of a object implementing the {@link Oft} interface
     * 
     * @return object implementing {@link Oft} interface
     */
    static Oft create()
    {
        return new OftRunner();
    }
}