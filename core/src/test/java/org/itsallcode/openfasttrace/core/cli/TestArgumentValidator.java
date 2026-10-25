package org.itsallcode.openfasttrace.core.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.itsallcode.openfasttrace.api.cli.DirectoryService;
import org.itsallcode.openfasttrace.api.report.ReportVerbosity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
        assertValidatorResult("Missing command", "Add one of 'help','convert','trace','chains'");
    }

    @Test
    void testTraceCommandGivenIsValid()
    {
        cliArgs.setUnnamedValues(List.of("trace"));
        assertValidatorResult("", "");
    }

    @Test
    void testTraceCommandQuietAndNoOutputFileGivenIsValid()
    {
        cliArgs.setUnnamedValues(List.of("trace"));
        cliArgs.setV(ReportVerbosity.QUIET);
        assertValidatorResult("", "");
    }

    @Test
    void testTraceCommandQuietAndOutputFileGivenIsNotValid()
    {
        cliArgs.setUnnamedValues(List.of("trace"));
        cliArgs.setV(ReportVerbosity.QUIET);
        cliArgs.setOutputFile("outputFile");
        assertValidatorResult(
                "combining stream verbosity 'quiet' and output to file is not supported.",
                "remove output file parameter.");
    }

    @Test
    void testConvertCommandGivenIsValid()
    {
        cliArgs.setUnnamedValues(List.of("convert"));
        cliArgs.setOutputFormat("unsupportedFormat");
        assertValidatorResult("export format 'unsupportedFormat' is not supported.", "");
    }

    @Test
    void testUnknownCommandGivenIsNotValid()
    {
        cliArgs.setUnnamedValues(List.of("unknownCommand"));
        assertValidatorResult("'unknownCommand' is not an OFT command.",
                "Choose one of 'help','convert','trace','chains'.");
    }

    @Test
    void testChainsCommandValidWithDefaults()
    {
        cliArgs.setUnnamedValues(List.of("chains"));
        assertValidatorResult("", "");
    }

    // [utest->dsn~cli.chains.output-format~1]
    @Test
    void testChainsCommandValidWithCustomFormat()
    {
        cliArgs.setUnnamedValues(List.of("chains"));
        cliArgs.setOutputFormat("csv");
        assertValidatorResult("", "");
    }

    // [utest->dsn~cli.chains.output-format~1]
    @Test
    void testChainsCommandUnsupportedFormat()
    {
        cliArgs.setUnnamedValues(List.of("chains"));
        cliArgs.setOutputFormat("unsupported");
        assertValidatorResult("chains format 'unsupported' is not supported.",
                "Choose one of 'chains', 'csv', 'mermaid'.");
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
