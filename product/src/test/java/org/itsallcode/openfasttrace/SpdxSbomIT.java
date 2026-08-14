package org.itsallcode.openfasttrace;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

import com.exasol.mavenprojectversiongetter.MavenProjectVersionGetter;
import org.junit.jupiter.api.Test;

class SpdxSbomIT
{
    private static final Path PROJECT_ROOT = Path.of("..").toAbsolutePath();
    private static final String SPDX3_CONTEXT = "https://spdx.org/rdf/3.0.1/spdx-context.jsonld";
    private static final String GPL_3_ONLY = "http://spdx.org/licenses/GPL-3.0-only";
    private static final String VERSION = MavenProjectVersionGetter
            .getProjectRevision(PROJECT_ROOT.resolve("parent/pom.xml"));

    @Test
    // [itest->dsn~build.spdx-sbom-generation~2]
    void testBuildCreatesSpdx3SbomWithDependencyAndLicenseData() throws IOException
    {
        final Path sbomFile = Path.of("target", "openfasttrace-" + VERSION + ".spdx3.json");
        assertThat(Files.exists(sbomFile), is(true));
        try (final Stream<Path> files = Files.list(sbomFile.getParent()))
        {
            assertThat(files.filter(path -> path.getFileName().toString().matches("openfasttrace-.*\\.spdx3\\.json"))
                    .count(), is(1L));
        }
        final String sbom = Files.readString(sbomFile);
        assertAll(
                () -> assertThat(sbom, containsString("\"@context\" : \"" + SPDX3_CONTEXT + "\"")),
                () -> assertThat(sbom, containsString("\"scope\" : \"runtime\"")),
                () -> assertThat(sbom, not(containsString("\"scope\" : \"test\""))),
                () -> assertThat(sbom, containsString("openfasttrace-api")),
                () -> assertThat(sbom, not(containsString("Byte Buddy"))),
                () -> assertThat(sbom, containsString(GPL_3_ONLY)),
                () -> assertThat(sbom, containsString("http://spdx.org/licenses/")));
    }

    @Test
    // [itest->dsn~build.spdx-sbom-release-asset~1]
    void testReleaseScriptIncludesSpdxSbomAsset() throws IOException
    {
        final String releaseScript = Files.readString(PROJECT_ROOT.resolve(".github/workflows/github_release.sh"));
        assertThat(releaseScript, containsString("\"$sbom_path\""));
    }
}
