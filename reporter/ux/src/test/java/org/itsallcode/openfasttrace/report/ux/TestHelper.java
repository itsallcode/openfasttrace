package org.itsallcode.openfasttrace.report.ux;

import org.hamcrest.Matcher;
import org.itsallcode.openfasttrace.api.ReportSettings;
import org.itsallcode.openfasttrace.api.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.api.core.Trace;
import org.itsallcode.openfasttrace.api.report.Reportable;
import org.itsallcode.openfasttrace.api.report.ReporterContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;

public class TestHelper
{
    /**
     * Creates a {@link UxReporter} with a default Context.
     *
     * @param items The items to process
     * @return a UxReporter
     */
    public static Reportable createReporter(final List<LinkedSpecificationItem> items) {
        final UxReporterFactory factory = new UxReporterFactory();
        factory.init(new ReporterContext(ReportSettings.createDefault()));
        final Trace trace = createTrace(items);
        return factory.createImporter(trace);
    }

    /**
     * Create a {@link Trace} from a List of {@link LinkedSpecificationItem}.
     *
     * @param items The items to trace
     * @return The Trace
     */
    public static Trace createTrace(final List<LinkedSpecificationItem> items) {
        return Trace.builder()
                .items(items)
                .defectItems(new ArrayList<>())
                .build();
    }

    /**
     * Matcher that equals against a test resource file.
     *
     * @param fileName The file beneath test/resources
     * @return A matcher
     * @throws IOException file does not exist
     */
    public static Matcher<Object> equalsToResource( final String fileName ) throws IOException
    {
        return equalTo(new String(Files.readAllBytes(Paths.get("src/test/resources", fileName ))));
    }

    /**
     * Removes the generated project Name from the generated js file as it includes a timestamp.
     *
     * @param generatedText The generated js
     * @return The generated js without the project name
     */
    public static String removeProjectNameFromJs( final String generatedText ) {
        return generatedText.replaceFirst("(?m)projectName: '[^']*',","projectName: '',");
    }

} // TestHelper
