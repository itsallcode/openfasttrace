package openfasttrack.cli;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Tests for {@link CommandLineInterpreter}
 */
public class TestCommandLineInterpreter
{
    @Test
    public void testGetNamedStringParamter()
    {
        final CommandLineArgumentsStub stub = parseArguments("-a", "value_a");
        assertThat(stub.getA(), equalTo("value_a"));
    }

    @Test
    public void testGetNamedBooleanParamter()
    {
        final CommandLineArgumentsStub stub = parseArguments("-b");
        assertThat(stub.isB(), equalTo(true));
    }

    @Test
    public void testGetUnnamedParamters()
    {
        final String[] args = { "value_1", "value_2" };
        final CommandLineArgumentsStub stub = parseArguments(args);
        assertThat(stub.getUnnamedValues(), equalTo(asList(args)));
    }

    @Test
    public void testCombinedParamters()
    {
        final CommandLineArgumentsStub stub = parseArguments("-a", "value_a", "value_1", "-b",
                "value_2");
        assertThat(stub.getUnnamedValues(), equalTo(asList("value_1", "value_2")));
        assertThat(stub.isB(), equalTo(true));
        assertThat(stub.getA(), equalTo("value_a"));
    }

    private CommandLineArgumentsStub parseArguments(final String... args)
    {
        final CommandLineArgumentsStub stub = new CommandLineArgumentsStub();
        assertThat("Boolean parameter must be false before CLI parsing", stub.isB(),
                equalTo(false));
        final CommandLineInterpreter cli = new CommandLineInterpreter(args, stub);
        cli.parse();
        return stub;
    }
}
