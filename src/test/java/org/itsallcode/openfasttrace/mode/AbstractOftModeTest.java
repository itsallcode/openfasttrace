package org.itsallcode.openfasttrace.mode;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2017 itsallcode.org
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


import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public abstract class AbstractOftModeTest
{

    protected static final String NEWLINE = "\n";
    protected static final String CARRIAGE_RETURN = "\r";

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    protected Path docDir;
    protected Path outputFile;
    protected final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    protected final ByteArrayOutputStream error = new ByteArrayOutputStream();

    protected void perpareOutput() throws UnsupportedEncodingException
    {
        this.docDir = Paths.get("src", "test", "resources", "markdown").toAbsolutePath();
        this.outputFile = this.tempFolder.getRoot().toPath().resolve("report.txt");
        System.setOut(new PrintStream(this.outputStream, true, "UTF-8"));
        System.setErr(new PrintStream(this.error, true, "UTF-8"));
    }

    protected void assertOutputFileContentStartsWith(final String content) throws IOException
    {
        assertThat(getOutputFileContent(), startsWith(content));
    }

    protected void assertOutputFileExists(final boolean fileExists)
    {
        assertThat("Output file exists", Files.exists(this.outputFile), equalTo(fileExists));
    }

    protected String getOutputFileContent() throws IOException
    {
        final Path file = this.outputFile;
        return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
    }

    protected void assertStdOutStartsWith(final String content)
    {
        assertThat(this.outputStream.toString(), startsWith(content));
    }

    protected void assertStdOutEmpty()
    {
        assertThat("STDOUT stream is empty", this.outputStream.size(), equalTo(0));
    }
}
