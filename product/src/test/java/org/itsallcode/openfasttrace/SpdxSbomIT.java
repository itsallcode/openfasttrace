package org.itsallcode.openfasttrace;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class SpdxSbomIT
{
    private static final Path SPDX_OUTPUT_DIRECTORY = Path.of("target", "site");
    private static final String SPDX_3_CONTEXT_PATTERN =
            "(?s).*\"@context\"\\s*:\\s*\"https://spdx.org/rdf/3\\.[^\"]+/spdx-context.jsonld\".*";

    @Test
    void generatedSbomDescribesOpenFastTraceProduct() throws IOException
    {
        final List<Path> generatedSboms = findGeneratedSboms();
        assertThat("generated product SBOMs", generatedSboms, hasSize(1));

        final String sbom = Files.readString(generatedSboms.get(0));

        assertAll(
                () -> assertThat("SPDX 3 JSON-LD context", sbom,
                        matchesPattern(SPDX_3_CONTEXT_PATTERN)),
                () -> assertThat("SPDX document", sbom, containsString("\"type\" : \"SpdxDocument\"")),
                () -> assertThat("software SBOM", sbom, containsString("\"type\" : \"software_Sbom\"")),
                () -> assertThat("product identity", sbom, containsString("\"name\" : \"OpenFastTrace Product\"")),
                () -> assertThat("API module", sbom, containsString("\"name\" : \"OpenFastTrace API\"")),
                () -> assertThat("core module", sbom, containsString("\"name\" : \"OpenFastTrace Core\"")),
                () -> assertThat("absence of JUnit test dependencies", sbom, not(containsString("JUnit"))),
                () -> assertThat("absence of Maven build plugins", sbom,
                        not(containsString("maven-compiler-plugin"))));
    }

    private List<Path> findGeneratedSboms() throws IOException
    {
        try (Stream<Path> files = Files.list(SPDX_OUTPUT_DIRECTORY))
        {
            return files.filter(file -> file.getFileName().toString().endsWith(".spdx3.json")).toList();
        }
    }
}
