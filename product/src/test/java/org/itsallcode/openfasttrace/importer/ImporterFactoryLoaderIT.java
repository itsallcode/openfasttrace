package org.itsallcode.openfasttrace.importer;

import org.itsallcode.openfasttrace.api.FilterSettings;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.importer.ImportSettings;

import org.itsallcode.openfasttrace.core.Oft;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ImporterFactoryLoaderIT {
    @Test
    void testFallBackToTagImporterWhenXmlIsNotSpecObjectFile(@TempDir Path tempDir) throws IOException {
        final Oft oft = Oft.create();
        final String specObjectContent = """
                <specdocument>
                    <specobjects doctype=\"dsn\">
                        <specobject>
                            <id>foobar</id>
                                 <version>1</version>
                            </specobject>
                    </specobjects>
                </specdocument>""";
        Files.write(tempDir.resolve("specobject.xml"), specObjectContent.getBytes());
        final String xmlContent = "<!-- [" + "impl -> dns~foobar~1" + "] -->";
        Files.write(tempDir.resolve("non_specobject.xml"), xmlContent.getBytes());
        final ImportSettings settings = ImportSettings.builder()
                .addInputs(tempDir)
                .filter(FilterSettings.builder().build())
                .build();
        final List<SpecificationItem> items = oft.importItems(settings);
        assertThat(items, containsInAnyOrder(
                hasProperty("id", hasToString("dsn~foobar~1")),
                hasProperty("id", hasToString(startsWith("impl~foobar")))
        ));
    }
}
