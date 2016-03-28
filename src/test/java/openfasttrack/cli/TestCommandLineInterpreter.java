package openfasttrack.cli;

/*
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 hamstercommunity
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
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Ignore;
import org.junit.Test;

import openfasttrack.cli.CommandLineArgumentsStub.StubEnum;

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

    @Test(expected = CliException.class)
    public void testMissingValueForStringParameter()
    {
        parseArguments("-a");
    }

    @Test(expected = CliException.class)
    public void testUnexpectedArgumentName()
    {
        parseArguments("-unexpected");
    }

    @Test
    public void testGetNamedBooleanParamter()
    {
        final CommandLineArgumentsStub stub = parseArguments("-b");
        assertThat(stub.isB(), equalTo(true));
    }

    @Test
    @Ignore
    public void testGetNamedEnumParamter()
    {
        final CommandLineArgumentsStub stub = parseArguments("-c", "VALUE1");
        assertThat(stub.getC(), equalTo(StubEnum.VALUE1));
    }

    @Test
    public void testGetUnnamedParamters()
    {
        final String[] args = { "value_1", "value_2" };
        final CommandLineArgumentsStub stub = parseArguments(args);
        assertThat(stub.getUnnamedValues(), equalTo(asList(args)));
    }

    @Test(expected = CliException.class)
    public void testNoSetterForUnnamedParameters()
    {
        final String[] args = { "value_1", "value_2" };
        new CommandLineInterpreter(args, new CommandLineArgumentsStubWithoutUnnamedParameters())
                .parse();
    }

    @Test(expected = CliException.class)
    public void testSetterWithoutArgument()
    {
        final String[] args = { "-invalid" };
        new CommandLineInterpreter(args, new CommandLineArgumentsStubWithNoArgSetter()).parse();
    }

    @Test(expected = CliException.class)
    public void testSetterWithTooManyArgument()
    {
        final String[] args = { "-invalid" };
        new CommandLineInterpreter(args, new CommandLineArgumentsStubWithMultiArgSetter()).parse();
    }

    @Test(expected = CliException.class)
    public void testSetterWithUnsupportedArgumentType()
    {
        final String[] args = { "-invalid" };
        new CommandLineInterpreter(args, new CommandLineArgumentsStubWithUnsupportedSetterArg())
                .parse();
    }

    @Test
    public void testCombinedParamters()
    {
        final CommandLineArgumentsStub stub = parseArguments("-a", "value_a", "value_1", "-b",
                "value_2" /* , "-c", "VALUE2" */);
        assertThat(stub.getUnnamedValues(), equalTo(asList("value_1", "value_2")));
        assertThat(stub.isB(), equalTo(true));
        assertThat(stub.getA(), equalTo("value_a"));
        // assertThat(stub.getC(), equalTo(StubEnum.VALUE2));
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

    private static class CommandLineArgumentsStubWithoutUnnamedParameters
    {

    }

    static class CommandLineArgumentsStubWithNoArgSetter
    {
        public void setInvalid()
        {
            // ignore
        }
    }

    static class CommandLineArgumentsStubWithMultiArgSetter
    {
        public void setInvalid(final String arg1, final int arg2)
        {
            // ignore
        }
    }

    static class CommandLineArgumentsStubWithUnsupportedSetterArg
    {
        public void setInvalid(final float unsupportedArg)
        {
            // ignore
        }
    }
}
