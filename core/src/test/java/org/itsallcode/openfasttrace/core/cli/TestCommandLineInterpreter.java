package org.itsallcode.openfasttrace.core.cli;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.itsallcode.openfasttrace.core.cli.CommandLineArgumentsStub.StubEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for {@link CommandLineInterpreter}
 */
class TestCommandLineInterpreter
{
    @Test
    void testGetNamedStringParamter() throws CliException
    {
        final CommandLineArgumentsStub stub = parseArguments("-a", "value_a");
        assertThat(stub.getA(), equalTo("value_a"));
    }

    @Test
    void testGetLongNamedStringParamter() throws CliException
    {
        final CommandLineArgumentsStub stub = parseArguments("--the-long-parameter", "value_a");
        assertThat(stub.getTheLongParameter(), equalTo("value_a"));
    }

    @Test
    void testGetNamedStringParamterCaseIndependent() throws CliException
    {
        final CommandLineArgumentsStub stub = parseArguments("-A", "value_a");
        assertThat(stub.getA(), equalTo("value_a"));
    }

    @Test
    void testMissingValueForStringParameter()
    {
        expectParseException(new CommandLineArgumentsStub(), asList("-a"),
                "No value for argument 'a'");
    }

    @ParameterizedTest
    @ValueSource(strings =
    { "--unexpected", "--Unexpected", "-u", "--u", "--long-unexpected-parameter", "-unexpectedParameter",
            "--unexpectedParameter" })
    void testUnexpectedArgumentName(final String parameter)
    {
        expectParseException(new CommandLineArgumentsStub(), asList(parameter),
                "Unexpected parameter '" + parameter + "' is not allowed");
    }

    @Test
    void testGetNamedBooleanParamter() throws CliException
    {
        final CommandLineArgumentsStub stub = parseArguments("-b");
        assertThat(stub.isB(), equalTo(true));
    }

    @Test
    void testGetNamedBooleanBoxedParamter() throws CliException
    {
        final CommandLineArgumentsStub stub = parseArguments("-d");
        assertThat(stub.isD(), equalTo(true));
    }

    @Test
    void testGetNamedEnumParamter() throws CliException
    {
        final CommandLineArgumentsStub stub = parseArguments("-c", "VALUE1");
        assertThat(stub.getC(), equalTo(StubEnum.VALUE1));
    }

    @Test
    void testGetNamedEnumParamterLowercase() throws CliException
    {
        final CommandLineArgumentsStub stub = parseArguments("-c", "value1");
        assertThat(stub.getC(), equalTo(StubEnum.VALUE1));
    }

    @Test
    void testInvalidEnumParamter()
    {
        expectParseException(new CommandLineArgumentsStub(), asList("-c", "INVALID_VALUE"),
                "Cannot convert value 'INVALID_VALUE' to enum org.itsallcode.openfasttrace.core.cli.CommandLineArgumentsStub$StubEnum. Allowed values: [VALUE1, VALUE2]");
    }

    @Test
    void testGetUnnamedParamters() throws CliException
    {
        final String[] args = { "value_1", "value_2" };
        final CommandLineArgumentsStub stub = parseArguments(args);
        assertThat(stub.getUnnamedValues(), equalTo(asList(args)));
    }

    @Test
    void testNoSetterForUnnamedParameters()
    {
        expectParseException(new CliArgsWithoutUnnamedParameters(), asList("value_1", "value_2"),
                "Unnamed arguments '[value_1, value_2]' are not allowed");
    }

    @Test
    void testSetterWithoutArgument()
    {
        expectParseException(new CliArgsWithNoArgSetter(), asList("--invalid"),
                "Unsupported argument count for setter 'public void org.itsallcode.openfasttrace.core.cli.TestCommandLineInterpreter$CliArgsWithNoArgSetter.setInvalid()'."
                        + " Only one argument is allowed.");
    }

    @Test
    void testSetterWithTooManyArguments()
    {
        expectParseException(new CliArgsMultiArgSetter(), asList("--invalid"),
                "Unsupported argument count for setter 'public void org.itsallcode.openfasttrace.core.cli.TestCommandLineInterpreter$CliArgsMultiArgSetter.setInvalid(java.lang.String,int)'."
                        + " Only one argument is allowed.");
    }

    @Test
    void testSetterWithUnsupportedArgumentType()
    {
        expectParseException(new CliArgsUnsupportedSetterArg(), asList("--invalid", "3.14"),
                "Type 'float' not supported for converting argument '3.14'");
    }

    @Test
    void testArgumentFollowedByArgument()
    {
        expectParseException(new CommandLineArgumentsStub(), asList("-a", "--unexpected"),
                "No value for argument 'a'");
    }

    @Test
    void testCombinedParameters() throws CliException
    {
        final CommandLineArgumentsStub stub = parseArguments("-a", "value_a", "value_1", "-b",
                "value_2", "-c", "VALUE2");
        assertThat(stub.getUnnamedValues(), equalTo(asList("value_1", "value_2")));
        assertThat(stub.isB(), equalTo(true));
        assertThat(stub.getA(), equalTo("value_a"));
        assertThat(stub.getC(), equalTo(StubEnum.VALUE2));
    }

    @Test
    void testCombinedParametersWithDifferentOrder() throws CliException
    {
        final CommandLineArgumentsStub stub = parseArguments("-a", "value_a", "value_1", "-b",
                "value_2", "value_3", "-c", "VALUE2");
        assertThat(stub.getUnnamedValues(), equalTo(asList("value_1", "value_2", "value_3")));
        assertThat(stub.isB(), equalTo(true));
        assertThat(stub.getA(), equalTo("value_a"));
        assertThat(stub.getC(), equalTo(StubEnum.VALUE2));
    }

    @Test
    void testChainedSingleCharacterParameters() throws CliException
    {
        final CommandLineArgumentsStub stub = parseArguments("-bda", "value_a", "value_1",
                "value_2");
        assertThat(stub.getUnnamedValues(), containsInAnyOrder("value_1", "value_2"));
        assertThat(stub.isB(), equalTo(true));
        assertThat(stub.isD(), equalTo(true));
        assertThat(stub.getA(), equalTo("value_a"));
    }

    @Test
    void testChainedSingleCharacterParametersMustFailIfNonBooleanMisplaced()
    {
        final CommandLineArgumentsStub stub = new CommandLineArgumentsStub();
        expectParseException(stub, asList("-bad", "value_a"), "No value for argument 'a'");
    }

    private CommandLineArgumentsStub parseArguments(final String... args) throws CliException
    {
        final CommandLineArgumentsStub stub = new CommandLineArgumentsStub();
        assertThat("Boolean parameter must be false before CLI parsing", stub.isB(),
                equalTo(false));
        final CommandLineInterpreter cli = new CommandLineInterpreter(args, stub);
        cli.parse();
        return stub;
    }

    private void expectParseException(final Object argumentsReceiver, final List<String> arguments,
            final String expectedExceptionMessage)
    {
        final CliException exception = assertThrows(CliException.class,
                () -> new CommandLineInterpreter(arguments.toArray(new String[0]),
                        argumentsReceiver).parse());
        assertThat(exception.getMessage(), equalTo(expectedExceptionMessage));
    }

    private static class CliArgsWithoutUnnamedParameters
    {

    }

    private static class CliArgsWithNoArgSetter
    {
        @SuppressWarnings("unused")
        public void setInvalid()
        {
            // ignore
        }
    }

    private static class CliArgsMultiArgSetter
    {
        @SuppressWarnings("unused")
        public void setInvalid(final String arg1, final int arg2)
        {
            // ignore
        }
    }

    private static class CliArgsUnsupportedSetterArg
    {
        @SuppressWarnings("unused")
        public void setInvalid(final float unsupportedArg)
        {
            // ignore
        }
    }
}
