package org.itsallcode.openfasttrace.core.cli;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.itsallcode.openfasttrace.api.cli.DirectoryService;
import org.itsallcode.openfasttrace.api.report.ReportVerbosity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestArgumentValidator
{

    @Mock
    private DirectoryService directoryServiceMock;
    private CliArguments cliArgs;

    @BeforeEach
    void setUp()
    {
        cliArgs = new CliArguments(directoryServiceMock);
    }

    @Test
    void testNoCommandGivenIsInvalid()
    {
        assertValidatorResult("Missing command", "Add one of 'help','convert','trace'");
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
                "combining stream verbosity 'quiet' and output to file is not supported.",
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
                "Choose one of 'help','convert','trace'.");
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
