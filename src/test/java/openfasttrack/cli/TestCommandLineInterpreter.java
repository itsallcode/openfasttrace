package openfasttrack.cli;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;

public class TestCommandLineInterpreter
{
    @Test
    public void testGetNamedStringParamter()
    {
        final String[] args = { "-a", "value_a" };
        final CommandLineArgumentsStub stub = new CommandLineArgumentsStub();
        final CommandLineInterpreter cli = new CommandLineInterpreter(args, stub);
        cli.parse();
        assertThat(stub.getA(), equalTo("value_a"));
    }

    @Test
    public void testGetNamedBooleanParamter()
    {
        final String[] args = { "-b" };
        final CommandLineArgumentsStub stub = new CommandLineArgumentsStub();
        assertThat("Boolean parameter must be false before CLI parsing", stub.isB(),
                equalTo(false));
        final CommandLineInterpreter cli = new CommandLineInterpreter(args, stub);
        cli.parse();
        assertThat(stub.isB(), equalTo(true));
    }

    @Test
    public void testGetUnnamedParamters()
    {
        final String[] args = { "value_1", "value_2" };
        final CommandLineArgumentsStub stub = new CommandLineArgumentsStub();
        final CommandLineInterpreter cli = new CommandLineInterpreter(args, stub);
        cli.parse();
        assertThat(stub.getUnnamedValues(), equalTo(Arrays.asList(args)));
    }

    @Test
    public void testCombinedParamters()
    {
        final String[] args = { "-a", "value_a", "value_1", "-b", "value_2" };
        final CommandLineArgumentsStub stub = new CommandLineArgumentsStub();
        assertThat("Boolean parameter must be false before CLI parsing", stub.isB(),
                equalTo(false));
        final CommandLineInterpreter cli = new CommandLineInterpreter(args, stub);
        cli.parse();
        assertThat(stub.getUnnamedValues(), equalTo(Arrays.asList("value_1", "value_2")));
        assertThat(stub.isB(), equalTo(true));
        assertThat(stub.getA(), equalTo("value_a"));
    }
}
