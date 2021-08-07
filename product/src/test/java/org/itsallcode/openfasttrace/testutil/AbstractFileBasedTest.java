package org.itsallcode.openfasttrace.testutil;

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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import org.itsallcode.openfasttrace.core.cli.CliStarter;

/**
 * This class is the base class for integration tests that require input files.
 */
// This is a base class for tests and has no tests by intention.
@SuppressWarnings("squid:S2187")
public class AbstractFileBasedTest
{
    @SuppressWarnings("javadoc")
    protected void writeTextFile(final File file, final String content) throws IOException
    {
        final PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8.toString());
        writer.print(content);
        writer.close();
    }

    @SuppressWarnings("javadoc")
    protected void runWithArguments(final String... args)
    {
        CliStarter.main(args);
    }
}