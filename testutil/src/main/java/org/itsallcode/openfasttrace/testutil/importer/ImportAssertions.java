package org.itsallcode.openfasttrace.testutil.importer;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import org.hamcrest.Matcher;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.importer.*;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.testutil.importer.input.StreamInput;

/**
 * Provides assertion methods for testing importers.
 */
public final class ImportAssertions
{
    private static final Logger LOGGER = Logger.getLogger(ImportAssertions.class.getName());

    private ImportAssertions()
    {
        // Prevent instantiation.
    }

    /**
     * Assert that the imported input matches the given matcher and filename.
     *
     * @param path
     *            expected filename
     * @param input
     *            content to be imported
     * @param matcher
     *            matcher that defines expectation for imported data
     * @param importerFactory
     *            factory that generates the importer
     */
    public static void assertImportWithFactory(final Path path, final String input,
            final Matcher<Iterable<? extends SpecificationItem>> matcher,
            final ImporterFactory importerFactory)
    {
        assertThat(runImporterOnText(path, input, importerFactory), matcher);
    }

    /**
     * Run an importer on the given text and return the imported specification
     * items.
     * 
     * @param path
     *            path to use for the input file
     * @param text
     *            text to import
     * @param importerFactory
     *            factory to create the importer
     * @return list of imported specification items
     */
    public static List<SpecificationItem> runImporterOnText(final Path path, final String text,
            final ImporterFactory importerFactory)
    {
        LOGGER.finest("Importing text: ***\n" + text + "\n***");
        final BufferedReader reader = new BufferedReader(new StringReader(text));
        final InputFile file = StreamInput.forReader(path, reader);
        final SpecificationListBuilder specItemBuilder = SpecificationListBuilder.create();
        final Importer importer = importerFactory.createImporter(file, specItemBuilder);
        importer.runImport();
        return specItemBuilder.build();
    }
}
