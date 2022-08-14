package org.itsallcode.openfasttrace.api.importer;

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
