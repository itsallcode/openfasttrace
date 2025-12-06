package org.itsallcode.openfasttrace.cli;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.StringContains.containsString;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.itsallcode.junit.sysextensions.SystemOutGuard;
import org.itsallcode.openfasttrace.testutil.AbstractFileBasedTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

@ExtendWith(SystemOutGuard.class)
class ITestCliWithFilter extends AbstractFileBasedTest
{
    public static final String SPECIFICATION = String.join(System.lineSeparator(), //
            "`feat~a~1`", //
            "I am a feature", //
            "`req~b~2`", //
            "A user requirement", //
            "Tags: tag1", //
            "`dsn~c~3`", //
            "Design", //
            "Tags: tag2", //
            "`impl~d~4`");

    private File specFile;

    @BeforeEach
    void beforeEach(@TempDir final Path tempDir) throws IOException
    {
        this.specFile = tempDir.resolve("spec.md").toFile();
        writeTextFile(this.specFile, SPECIFICATION);
    }

    // [itest->dsn~filtering-by-tags-during-import~1]
    @Test
    void testWithoutFilter()
    {
        assertStdOut(List.of("convert", this.specFile.toString()),
                allOf(containsString("<id>a<"),
                        containsString("<id>b<"),
                        containsString("<id>c<")));
    }

    // [itest->dsn~filtering-by-tags-during-import~1]
    @Test
    void testFilterWithAtLeastOneMatchingTag()
    {
        assertStdOut(List.of("convert", "-t", "tag1", this.specFile.toString()),
                allOf(not(containsString("<id>a<")), containsString("<id>b<"), not(containsString("<id>c<"))));
    }

    // [itest->dsn~filtering-by-tags-during-import~1]
    @Test
    void testFilterWithEmptyTagListFiltersOutEverything()
    {
        assertStdOut(List.of("convert", "-t", "", this.specFile.toString()), not(containsString("<id>")));
    }

    // [itest->dsn~filtering-by-tags-or-no-tags-during-import~1]
    @Test
    void testFilterWithAtLeastOneMatchingTagOrNoTags()
    {
        assertStdOut(List.of(
                "convert", "-t", "_,tag1", this.specFile.toString()),
                allOf(containsString("<id>a<"), containsString("<id>b<"), not(containsString("<id>c<"))));
    }
}
