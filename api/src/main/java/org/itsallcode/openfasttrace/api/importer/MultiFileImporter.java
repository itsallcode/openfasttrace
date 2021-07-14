package org.itsallcode.openfasttrace.api.importer;

/*-
 * #%L
 * OpenFastTrace API
 * %%
 * Copyright (C) 2016 - 2019 itsallcode.org
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

import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.List;

import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;

/**
 * Common interface for all importer implementations that support importing
 * multiple files.
 */
public interface MultiFileImporter
{

    /**
     * Import the given file using the matching {@link Importer} using character
     * set {@link StandardCharsets#UTF_8 UTF-8} for reading.
     *
     * @param file
     *            the file to import.
     * @return <code>this</code> for fluent programming style.
     */
    MultiFileImporter importFile(InputFile file);

    /**
     * Import from the path, independently of whether it is represents a
     * directory or a file.
     * 
     * @param paths
     *            lists of paths to files or directories
     * @return <code>this</code> for fluent programming style.
     */
    MultiFileImporter importAny(List<Path> paths);

    /**
     * Import all files from the given directory that match the given
     * {@link FileSystem#getPathMatcher(String) glob expression} and character
     * set {@link StandardCharsets#UTF_8 UTF-8} for reading.
     *
     * @param dir
     *            the dir to search for files to import.
     * @param glob
     *            the {@link FileSystem#getPathMatcher(String) glob expression}
     * @return <code>this</code> for fluent programming style.
     */
    // [impl->dsn~input-directory-recursive-traversal~1]
    MultiFileImporter importRecursiveDir(Path dir, String glob);

    /**
     * Get all imported {@link SpecificationItem}s.
     * 
     * @return all imported {@link SpecificationItem}s.
     */
    List<SpecificationItem> getImportedItems();
}
