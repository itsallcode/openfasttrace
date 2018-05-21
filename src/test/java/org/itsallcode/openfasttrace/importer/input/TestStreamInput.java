package org.itsallcode.openfasttrace.importer.input;

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

import static java.util.stream.Collectors.joining;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class TestStreamInput
{
    private static final String CONTENT = "file content 1\nabcöäüßÖÄÜ!\"§$%&/()=?`´'#+*~-_,.;:<>|^°";

    @Test
    public void testRelativeFileGetPath() throws IOException
    {
        final Path path = Paths.get("blah");
        final InputFile inputFile = InputFile.forReader(path, null);
        assertThat(inputFile.getPath(), equalTo("blah"));
        assertThat(inputFile.toString(), equalTo("blah"));
    }

    @Test
    public void testAbsoluteFileGetPath() throws IOException
    {
        final Path path = Paths.get("blah").toAbsolutePath();
        final InputFile inputFile = InputFile.forReader(path, null);
        assertThat(inputFile.getPath(), equalTo(path.toString()));
        assertThat(inputFile.toString(), equalTo(path.toString()));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testToPathUnsupported() throws IOException
    {
        final Path path = Paths.get("blah");
        InputFile.forReader(path, null).toPath();
    }

    @Test
    public void testIsRealFileFalse() throws IOException
    {
        final InputFile inputFile = InputFile.forReader(Paths.get("blah"), null);
        assertThat(inputFile.isRealFile(), equalTo(false));
    }

    @Test
    public void testReadContent() throws IOException
    {
        final InputFile inputFile = InputFile.forReader(null,
                new BufferedReader(new StringReader(CONTENT)));
        assertThat(readContent(inputFile), equalTo(CONTENT));
    }

    private String readContent(final InputFile inputFile) throws IOException
    {
        return inputFile.createReader().lines().collect(joining("\n"));
    }
}
