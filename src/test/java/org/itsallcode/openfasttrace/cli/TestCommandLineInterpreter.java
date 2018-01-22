package org.itsallcode.openfasttrace.cli;

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


import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.itsallcode.openfasttrace.cli.CliException;
import org.itsallcode.openfasttrace.cli.CommandLineInterpreter;
import org.itsallcode.openfasttrace.cli.CommandLineArgumentsStub.StubEnum;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link CommandLineInterpreter}
 */
public class TestCommandLineInterpreter
{
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testGetNamedStringParamter()
    {
        final CommandLineArgumentsStub stub = parseArguments("-a", "value_a");
        assertThat(stub.getA(), equalTo("value_a"));
    }

    @Test
    public void testGetLongNamedStringParamter()
    {
        final CommandLineArgumentsStub stub = parseArguments("--the-long-parameter", "value_a");
        assertThat(stub.getTheLongParameter(), equalTo("value_a"));
    }

    @Test
    public void testGetNamedStringParamterCaseIndependent()
    {
        final CommandLineArgumentsStub stub = parseArguments("-A", "value_a");
        assertThat(stub.getA(), equalTo("value_a"));
    }

    @Test
    public void testMissingValueForStringParameter()
    {
        expectParseException(new CommandLineArgumentsStub(), asList("-a"),
                "No value for argument 'a'");
    }

    @Test
    public void testUnexpectedArgumentName()
    {
        expectParseException(new CommandLineArgumentsStub(), asList("--unexpected"),
                "Unexpected parameter 'unexpected' is not allowed");
    }

    @Test
    public void testUnexpectedSingleCharacterArgumentName()
    {
        expectParseException(new CommandLineArgumentsStub(), asList("-u"),
                "Unexpected parameter 'u' is not allowed");
    }

    @Test
    public void testGetNamedBooleanParamter()
    {
        final CommandLineArgumentsStub stub = parseArguments("-b");
        assertThat(stub.isB(), equalTo(true));
    }

    @Test
    public void testGetNamedBooleanBoxedParamter()
    {
        final CommandLineArgumentsStub stub = parseArguments("-d");
        assertThat(stub.isD(), equalTo(true));
    }

    @Test
    public void testGetNamedEnumParamter()
    {
        final CommandLineArgumentsStub stub = parseArguments("-c", "VALUE1");
        assertThat(stub.getC(), equalTo(StubEnum.VALUE1));
    }

    @Test
    public void testInvalidEnumParamter()
    {
        expectParseException(new CommandLineArgumentsStub(), asList("-c", "INVALID_VALUE"),
                "Cannot convert value 'INVALID_VALUE' to enum org.itsallcode.openfasttrace.cli.CommandLineArgumentsStub$StubEnum");
    }

    @Test
    public void testGetUnnamedParamters()
    {
        final String[] args = { "value_1", "value_2" };
        final CommandLineArgumentsStub stub = parseArguments(args);
        assertThat(stub.getUnnamedValues(), equalTo(asList(args)));
    }

    @Test
    public void testNoSetterForUnnamedParameters()
    {
        expectParseException(new CliArgsWithoutUnnamedParameters(), asList("value_1", "value_2"),
                "Unnamed arguments '[value_1, value_2]' are not allowed");
    }

    @Test
    public void testSetterWithoutArgument()
    {
        expectParseException(new CliArgsWithNoArgSetter(), asList("--invalid"),
                "Unsupported argument count for setter 'public void org.itsallcode.openfasttrace.cli.TestCommandLineInterpreter$CliArgsWithNoArgSetter.setInvalid()'."
                        + " Only one argument is allowed.");
    }

    @Test
    public void testSetterWithTooManyArguments()
    {
        expectParseException(new CliArgsMultiArgSetter(), asList("--invalid"),
                "Unsupported argument count for setter 'public void org.itsallcode.openfasttrace.cli.TestCommandLineInterpreter$CliArgsMultiArgSetter.setInvalid(java.lang.String,int)'."
                        + " Only one argument is allowed.");
    }

    @Test
    public void testSetterWithUnsupportedArgumentType()
    {
        expectParseException(new CliArgsUnsupportedSetterArg(), asList("--invalid", "3.14"),
                "Type 'float' not supported for converting argument '3.14'");
    }

    @Test
    public void testArgumentFollowedByArgument()
    {
        expectParseException(new CommandLineArgumentsStub(), asList("-a", "--unexpected"),
                "No value for argument 'a'");
    }

    @Test
    public void testCombinedParameters()
    {
        final CommandLineArgumentsStub stub = parseArguments("-a", "value_a", "value_1", "-b",
                "value_2", "-c", "VALUE2");
        assertThat(stub.getUnnamedValues(), equalTo(asList("value_1", "value_2")));
        assertThat(stub.isB(), equalTo(true));
        assertThat(stub.getA(), equalTo("value_a"));
        assertThat(stub.getC(), equalTo(StubEnum.VALUE2));
    }

    @Test
    public void testCombinedParametersWithDifferentOrder()
    {
        final CommandLineArgumentsStub stub = parseArguments("-a", "value_a", "value_1", "-b",
                "value_2", "value_3", "-c", "VALUE2");
        assertThat(stub.getUnnamedValues(), equalTo(asList("value_1", "value_2", "value_3")));
        assertThat(stub.isB(), equalTo(true));
        assertThat(stub.getA(), equalTo("value_a"));
        assertThat(stub.getC(), equalTo(StubEnum.VALUE2));
    }

    @Test
    public void testChainedSingleCharacterParameters()
    {
        final CommandLineArgumentsStub stub = parseArguments("-bda", "value_a", "value_1",
                "value_2");
        assertThat(stub.getUnnamedValues(), containsInAnyOrder("value_1", "value_2"));
        assertThat(stub.isB(), equalTo(true));
        assertThat(stub.isD(), equalTo(true));
        assertThat(stub.getA(), equalTo("value_a"));
    }

    @Test
    public void testChainedSingleCharacterParametersMustFailIfNonBooleanMisplaced()
    {
        final CommandLineArgumentsStub stub = new CommandLineArgumentsStub();
        expectParseException(stub, asList("-bad", "value_a"), "No value for argument 'a'");
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

    private void expectParseException(final Object argumentsReceiver, final List<String> arguments,
            final String expectedExceptionMessage)
    {
        this.thrown.expect(CliException.class);
        this.thrown.expectMessage(expectedExceptionMessage);
        new CommandLineInterpreter(arguments.toArray(new String[0]), argumentsReceiver).parse();
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
