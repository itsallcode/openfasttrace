package org.itsallcode.openfasttrace.core.cli.commands;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.itsallcode.openfasttrace.api.cli.DirectoryService;
import org.itsallcode.openfasttrace.api.exporter.ExporterException;
import org.itsallcode.openfasttrace.core.cli.CliArguments;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestConvertCommand
{
    @Mock
    private DirectoryService directoryServiceMock;

    @Test
    void testRun()
    {
        final CliArguments args = new CliArguments(directoryServiceMock);
        args.setUnnamedValues(asList("convert", "input"));
        final ExporterException exception = assertThrows(ExporterException.class,
                () -> new ConvertCommand(args).run());
        assertThat(exception.getMessage(),
                equalTo("Found no matching exporter for output format 'specobject'"));
    }
}
