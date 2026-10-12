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
        return create(config, file, listener, false);
    }

    /**
     * Create a line consumer for coverage tags in an input file that also
     * imports full coverage tags spanning multiple lines.
     *
     * @param config
     *                 optional configuration for short coverage tags
     * @param file
     *                 input file that contains the tags
     * @param listener
     *                 listener receiving imported specification items
     * @return line consumer that imports single- and multi-line full coverage
     *         tags and configured short coverage tags
     */
    // [impl->dsn~import.full-coverage-tag-multi-line~1]
    public static LineConsumer createWithMultiLineTags(final PathConfig config, final InputFile file,
            final ImportEventListener listener) {
        return create(config, file, listener, true);
    }

    private static LineConsumer create(final PathConfig config, final InputFile file,
            final ImportEventListener listener, final boolean multiLineTags) {
        final List<LineConsumer> parsers = new ArrayList<>();
        parsers.add(new LongTagImportingLineConsumer(file, listener, multiLineTags));
        if (config != null) {
            parsers.add(new ShortTagImportingLineConsumer(config, file, listener));
        }
        return new DelegatingLineConsumer(parsers);
    }
}
