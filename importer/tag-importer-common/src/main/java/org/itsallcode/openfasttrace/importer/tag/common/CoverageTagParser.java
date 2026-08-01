package org.itsallcode.openfasttrace.importer.tag.common;

import java.util.ArrayList;
import java.util.List;

import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.api.importer.tag.config.PathConfig;
import org.itsallcode.openfasttrace.importer.tag.common.LineReader.LineConsumer;

/**
 * Creates line consumers that import full and configured short coverage tags.
 */
public final class CoverageTagParser {
    private CoverageTagParser() {
        // Prevent instantiation.
    }

    /**
     * Create a line consumer for coverage tags in an input file.
     *
     * @param config
     *                 optional configuration for short coverage tags
     * @param file
     *                 input file that contains the tags
     * @param listener
     *                 listener receiving imported specification items
     * @return line consumer that imports full and configured short coverage tags
     */
    public static LineConsumer create(final PathConfig config, final InputFile file,
            final ImportEventListener listener) {
        final List<LineConsumer> parsers = new ArrayList<>();
        parsers.add(new LongTagImportingLineConsumer(file, listener));
        if (config != null) {
            parsers.add(new ShortTagImportingLineConsumer(config, file, listener));
        }
        return new DelegatingLineConsumer(parsers);
    }
}
