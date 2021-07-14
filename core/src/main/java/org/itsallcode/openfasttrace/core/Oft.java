package org.itsallcode.openfasttrace.core;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2017 itsallcode.org
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