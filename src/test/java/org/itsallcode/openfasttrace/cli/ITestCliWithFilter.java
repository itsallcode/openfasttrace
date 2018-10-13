package org.itsallcode.openfasttrace.cli;

import static org.hamcrest.core.IsNot.not;

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
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.itsallcode.openfasttrace.testutil.AbstractFileBasedTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TemporaryFolder;

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

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    private File specFile;

    @Before
    public void before() throws IOException
    {
        this.specFile = this.tempFolder.newFile("spec.md");
        writeTextFile(this.specFile, SPECIFICATION);
        this.exit.expectSystemExitWithStatus(0);
    }

    // [itest->dsn~filtering-by-tags-during-import~1]
    @Test
    public void testWithoutFilter()
    {
        this.exit.checkAssertionAfterwards(() -> {
            final String stdOut = this.systemOutRule.getLog();
            assertThat(stdOut, containsString("<id>a<"));
            assertThat(stdOut, containsString("<id>b<"));
            assertThat(stdOut, containsString("<id>c<"));
        });
        runWithArguments("convert", this.specFile.toString());
    }

    // [itest->dsn~filtering-by-tags-during-import~1]
    @Test
    public void testFilterWithAtLeastOneMatchingTag()
    {
        this.exit.checkAssertionAfterwards(() -> {
            final String stdOut = this.systemOutRule.getLog();
            assertThat(stdOut, not(containsString("<id>a<")));
            assertThat(stdOut, containsString("<id>b<"));
            assertThat(stdOut, not(containsString("<id>c<")));
        });
        runWithArguments("convert", "-t", "tag1", this.specFile.toString());
    }

    // [itest->dsn~filtering-by-tags-during-import~1]
    @Test
    public void testFilterWithEmptyTagListFiltersOutEverything()
    {
        this.exit.checkAssertionAfterwards(() -> {
            final String stdOut = this.systemOutRule.getLog();
            assertThat(stdOut, not(containsString("<id>")));
        });
        runWithArguments("convert", "-t", "", this.specFile.toString());
    }

    // [itest->dsn~filtering-by-tags-or-no-tags-during-import~1]
    @Test
    public void testFilterWithAtLeastOneMatchingTagOrNoTags()
    {
        this.exit.checkAssertionAfterwards(() -> {
            final String stdOut = this.systemOutRule.getLog();
            assertThat(stdOut, containsString("<id>a<"));
            assertThat(stdOut, containsString("<id>b<"));
            assertThat(stdOut, not(containsString("<id>c<")));
        });
        runWithArguments("convert", "-t", "_,tag1", this.specFile.toString());
    }
}