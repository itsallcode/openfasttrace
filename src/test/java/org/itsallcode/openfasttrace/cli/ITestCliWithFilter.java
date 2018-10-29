package org.itsallcode.openfasttrace.cli;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import static org.hamcrest.core.StringContains.containsString;
import static org.itsallcode.junit.sysextensions.AssertExit.assertExitWithStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.itsallcode.io.Capturable;
import org.itsallcode.junit.sysextensions.ExitGuard;
import org.itsallcode.junit.sysextensions.SystemOutGuard;
import org.itsallcode.junit.sysextensions.SystemOutGuard.SysOut;
import org.itsallcode.openfasttrace.testutil.AbstractFileBasedTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.junitpioneer.jupiter.TempDirectory.TempDir;

@ExtendWith(TempDirectory.class)
@ExtendWith(ExitGuard.class)
@ExtendWith(SystemOutGuard.class)
public class ITestCliWithFilter extends AbstractFileBasedTest
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
    void beforeEach(@TempDir final Path tempDir, @SysOut final Capturable out) throws IOException
    {
        this.specFile = tempDir.resolve("spec.md").toFile();
        writeTextFile(this.specFile, SPECIFICATION);
        out.capture();
    }

    // [itest->dsn~filtering-by-tags-during-import~1]
    @Test
    void testWithoutFilter(@SysOut final Capturable out)
    {
        assertExitWithStatus(0, () -> runWithArguments("convert", this.specFile.toString()));
        final String stdOut = out.getCapturedData();
        assertThat(stdOut, containsString("<id>a<"));
        assertThat(stdOut, containsString("<id>b<"));
        assertThat(stdOut, containsString("<id>c<"));
    }

    // [itest->dsn~filtering-by-tags-during-import~1]
    @Test
    void testFilterWithAtLeastOneMatchingTag(@SysOut final Capturable out)
    {
        assertExitWithStatus(0,
                () -> runWithArguments("convert", "-t", "tag1", this.specFile.toString()));
        final String stdOut = out.getCapturedData();
        assertThat(stdOut, not(containsString("<id>a<")));
        assertThat(stdOut, containsString("<id>b<"));
        assertThat(stdOut, not(containsString("<id>c<")));
    }

    // [itest->dsn~filtering-by-tags-during-import~1]
    @Test
    void testFilterWithEmptyTagListFiltersOutEverything(final Capturable stream)
    {
        assertExitWithStatus(0,
                () -> runWithArguments("convert", "-t", "", this.specFile.toString()));
        final String stdOut = stream.getCapturedData();
        assertThat(stdOut, not(containsString("<id>")));
    }

    // [itest->dsn~filtering-by-tags-or-no-tags-during-import~1]
    @Test
    void testFilterWithAtLeastOneMatchingTagOrNoTags(final Capturable stream)
    {
        assertExitWithStatus(0,
                () -> runWithArguments("convert", "-t", "_,tag1", this.specFile.toString()));
        final String stdOut = stream.getCapturedData();
        assertThat(stdOut, containsString("<id>a<"));
        assertThat(stdOut, containsString("<id>b<"));
        assertThat(stdOut, not(containsString("<id>c<")));
    }
}