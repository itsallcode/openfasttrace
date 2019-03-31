package org.itsallcode.openfasttrace.cli;

/*-
 * #%L
 * OpenFastTrace Core
 * %%
 * Copyright (C) 2016 - 2019 itsallcode.org
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

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.itsallcode.openfasttrace.report.ReportVerbosity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TestArgumentValidator
{

    @Mock
    private DirectoryService directoryServiceMock;
    private CliArguments cliArgs;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.initMocks(this);
        cliArgs = new CliArguments(directoryServiceMock);
    }

    @Test
    void testNoCommandGivenIsInvalid()
    {
        assertValidatorResult("Missing command", "Add one of 'convert','trace'");
    }

    @Test
    void testTraceCommandGivenIsValid()
    {
        cliArgs.setUnnamedValues(asList("trace"));
        assertValidatorResult("", "");
    }

    @Test
    void testTraceCommandQuietAndNoOutputFileGivenIsValid()
    {
        cliArgs.setUnnamedValues(asList("trace"));
        cliArgs.setV(ReportVerbosity.QUIET);
        assertValidatorResult("", "");
    }

    @Test
    void testTraceCommandQuietAndOutputFileGivenIsNotValid()
    {
        cliArgs.setUnnamedValues(asList("trace"));
        cliArgs.setV(ReportVerbosity.QUIET);
        cliArgs.setOutputFile("outputFile");
        assertValidatorResult(
                "combining stream verbosity 'quiet' and ouput to file is not supported.",
                "remove output file parameter.");
    }

    @Test
    void testConvertCommandGivenIsValid()
    {
        cliArgs.setUnnamedValues(asList("convert"));
        cliArgs.setOutputFormat("unsupportedFormat");
        assertValidatorResult("export format 'unsupportedFormat' is not supported.", "");
    }

    @Test
    void testUnknownCommandGivenIsNotValid()
    {
        cliArgs.setUnnamedValues(asList("unknownCommand"));
        assertValidatorResult("'unknownCommand' is not an OFT command.",
                "Choose one of 'convert','trace'.");
    }

    private void assertValidatorResult(String expectedError, String expectedSuggestion)
    {
        final ArgumentValidator validator = validator();

        assertThat(validator.getError(), equalTo(expectedError));
        assertThat(validator.getSuggestion(), equalTo(expectedSuggestion));
        assertThat(validator.isValid(), equalTo(expectedError.isEmpty()));
    }

    private ArgumentValidator validator()
    {
        return new ArgumentValidator(cliArgs);
    }
}
