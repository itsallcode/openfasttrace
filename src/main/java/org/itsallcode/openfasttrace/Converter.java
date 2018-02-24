
package org.itsallcode.openfasttrace;

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

import org.itsallcode.openfasttrace.core.Newline;
import org.itsallcode.openfasttrace.importer.legacytag.LegacyTagImporterConfig;
import org.itsallcode.openfasttrace.importer.legacytag.LegacyTagImporterFactory;

/**
 * Convert between different requirements formats (e.g. from ReqM2 to Markdown)
 */
public interface Converter
{
    /**
     * Select one or more input files
     * 
     * @param inputs
     *            input files
     * @return a <code>Converter</code> instance for fluent programming
     */
    Converter addInputs(final Path... inputs);

    /**
     * Select one or more input files
     * 
     * @param inputs
     *            input files
     * @return a <code>Converter</code> instance for fluent programming
     */
    Converter addInputs(final List<Path> inputs);

    /**
     * Set the list of artifact type to be ignored during import
     * 
     * @param ignoredArtifactTypes
     *            list of ignored artifact types
     */
    Converter ignoreArtifactTypes(List<String> ignoredArtifactTypes);

    /**
     * Set the representation for new line
     * 
     * @param newline
     *            type of newline
     * @return a <code>Converter</code> instance for fluent programming
     */
    Converter setNewline(Newline newline);

    /**
     * Set the {@link LegacyTagImporterConfig} for the
     * {@link LegacyTagImporterFactory}.
     * 
     * @param config
     *            the {@link LegacyTagImporterConfig} to set.
     * @return a <code>Converter</code> instance for fluent programming
     */
    Converter setLegacyTagImporterPathConfig(final LegacyTagImporterConfig config);

    /**
     * Convert the collected requirements into target requirement format
     * 
     * @param output
     *            output file
     * 
     * @param format
     *            target format (this is a name defined in the respective
     *            exporter plug-in)
     */
    void convertToFileInFormat(final Path output, final String format);

    /**
     * Get the default export output format
     * 
     * @return the default output format
     */
    public static String getDefaultOutputFormat()
    {
        return "specobject";
    }
}
