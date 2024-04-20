package org.itsallcode.openfasttrace.testutil.importer;

import org.hamcrest.Matcher;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.importer.Importer;
import org.itsallcode.openfasttrace.api.importer.ImporterFactory;
import org.itsallcode.openfasttrace.api.importer.SpecificationListBuilder;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.testutil.importer.input.StreamInput;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

public final class ImportAssertions
{
    private ImportAssertions()
    {
        // Prevent instantiation.
    }

    /**
     * Assert that the imported input matches the given matcher and filename.
     *
     * @param filename
     *            expected filename
     * @param input
     *            content to be imported
     * @param matcher
     *            matcher that defines expectation for imported data
     */
    public static void assertImportWithFactory(final String filename, final String input,
            final Matcher<Iterable<? extends SpecificationItem>> matcher,
            final ImporterFactory importerFactory)
    {
            assertThat(runImporterOnText(filename, input, importerFactory), matcher);
    }

    public static List<SpecificationItem> runImporterOnText(final String filename, final String text,
            final ImporterFactory importerFactory)
    {
        final BufferedReader reader = new BufferedReader(new StringReader(text));
        final InputFile file = StreamInput.forReader(Paths.get(filename), reader);
        final SpecificationListBuilder specItemBuilder = SpecificationListBuilder.create();
        final Importer importer = importerFactory.createImporter(file, specItemBuilder);
        importer.runImport();
        return specItemBuilder.build();
    }
}
