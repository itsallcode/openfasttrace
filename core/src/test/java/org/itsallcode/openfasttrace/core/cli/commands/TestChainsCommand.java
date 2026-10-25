package org.itsallcode.openfasttrace.core.cli.commands;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.itsallcode.openfasttrace.api.cli.DirectoryService;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.core.cli.CliArguments;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// [utest->dsn~cli.chains.output-format~1]
// [utest->dsn~cli.chains.default-output-format~1]
@ExtendWith(MockitoExtension.class)
class TestChainsCommand
{
    @Mock
    private DirectoryService directoryServiceMock;

    private CliArguments args;
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;

    @BeforeEach
    void setUp()
    {
        this.args = new CliArguments(this.directoryServiceMock);
        this.originalOut = System.out;
        this.outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(this.outContent, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void tearDown()
    {
        System.setOut(this.originalOut);
    }

    @Test
    void testConstructor()
    {
        this.args.setUnnamedValues(List.of(ChainsCommand.COMMAND_NAME));
        final ChainsCommand command = new ChainsCommand(this.args);
        assertThat(command, notNullValue());
    }

    @Test
    void testRunPrintsToStdout()
    {
        final SpecificationItem req = createItem("req", "r", List.of("impl"));
        this.args.setUnnamedValues(List.of(ChainsCommand.COMMAND_NAME, "input"));
        final ChainsCommand command = new StubChainsCommand(this.args, List.of(req));

        final boolean success = command.run();

        assertThat(success, equalTo(true));
        assertThat(this.outContent.toString(StandardCharsets.UTF_8),
                equalTo("req -> impl" + this.args.getNewline()));
    }

    private SpecificationItem createItem(final String type, final String name, final List<String> needs)
    {
        final SpecificationItem.Builder builder = SpecificationItem.builder()
                .id(type, name, 1);
        needs.forEach(builder::addNeedsArtifactType);
        return builder.build();
    }

    private static class StubChainsCommand extends ChainsCommand
    {
        private final List<SpecificationItem> items;

        StubChainsCommand(final CliArguments arguments, final List<SpecificationItem> items)
        {
            super(arguments);
            this.items = items;
        }

        @Override
        protected List<SpecificationItem> importItems()
        {
            return this.items;
        }
    }
}
