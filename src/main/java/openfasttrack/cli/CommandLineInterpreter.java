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
import static java.util.stream.Collectors.toMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * This class implements an interpreter for command line arguments
 *
 * Users of this class must create a POJO that contains a setter method for each
 * command line argument that they want to use.
 *
 * Additionally they can add a setter called <code>setUnnamedValues</code> that
 * will receive all argument values that are unnamed.
 */
public class CommandLineInterpreter
{
    private static final String UNNAMED_ARGUMENTS_SUFFIX = "unnamedvalues";
    private static final String SINGLE_CHAR_ARG_PREFIX = "-";
    private static final String SETTER_PREFIX = "set";
    private final Object argumentsReceiver;
    private final String[] arguments;
    private final Map<String, Method> setters;

    /**
     * Create a new instance of type {@link CommandLineInterpreter}
     *
     * @param arguments
     *            the raw command line arguments
     * @param argumentsReceiver
     *            the class that will receive the parsed command line arguments
     */
    public CommandLineInterpreter(final String[] arguments, final Object argumentsReceiver)
    {
        this.arguments = arguments;
        this.argumentsReceiver = argumentsReceiver;
        this.setters = findAllSettersInArgumentsReceiver(argumentsReceiver);
    }

    private static Map<String, Method> findAllSettersInArgumentsReceiver(
            final Object argumentsReceiver)
    {
        final Class<?> receiverClass = argumentsReceiver.getClass();
        final Stream<Method> methods = Arrays.stream(receiverClass.getMethods());
        return methods.filter(method -> method.getName().startsWith(SETTER_PREFIX)) //
                .collect(toMap(CommandLineInterpreter::getSetterName, Function.identity()));
    }

    private static String getSetterName(final Method method)
    {
        return method.getName() //
                .substring(SETTER_PREFIX.length()) //
                .toLowerCase();
    }

    /**
     * Parse the command line and inject the values into the receiver object
     */
    public void parse()
    {
        final List<String> unnamedArguments = new ArrayList<>();
        final ListIterator<String> iterator = asList(this.arguments).listIterator();
        while (iterator.hasNext())
        {
            final String argument = iterator.next();
            if (argument.startsWith(SINGLE_CHAR_ARG_PREFIX))
            {
                handleNamedArgument(iterator, argument);
            }
            else
            {
                handleUnnamedArgument(unnamedArguments, argument);
            }
        }
        if (!unnamedArguments.isEmpty())
        {
            assignUnnameArgument(unnamedArguments);
        }
    }

    private void handleNamedArgument(final ListIterator<String> iterator, final String argument)
    {
        final String argumentName = argument.substring(SINGLE_CHAR_ARG_PREFIX.length());
        if (this.setters.containsKey(argumentName))
        {
            handleExpectedNamedArgument(iterator, argumentName);
        }
        else
        {
            reportUnexpectedNamedArgument(argumentName);
        }
    }

    private void handleUnnamedArgument(final List<String> unnamedArguments, final String argument)
    {
        unnamedArguments.add(argument);
    }

    private void reportUnexpectedNamedArgument(final String argument)
    {
        throw new CliException("Unexpected parameter '" + argument + "' is not allowed");
    }

    private void handleExpectedNamedArgument(final ListIterator<String> iterator,
            final String argumentName)
    {
        final Method setter = this.setters.get(argumentName);
        if (setter.getParameterTypes().length != 1)
        {
            reportUnsupportedSetterArgumentCount(setter);
        }
        final Class<?> setterParameterType = setter.getParameterTypes()[0];
        if (setterParameterType.equals(boolean.class) || setterParameterType.equals(Boolean.class))
        {
            assignValue(setter, true);
        }
        else
        {
            if (iterator.hasNext())
            {
                final String successor = iterator.next();
                if (isParamterName(successor))
                {
                    iterator.previous();
                    reportMissingParamterValue(argumentName);
                }
                else
                {
                    final Object convertedValue = convertArgument(successor, setterParameterType);
                    assignValue(setter, convertedValue);
                }
            }
            else
            {
                reportMissingParamterValue(argumentName);
            }
        }
    }

    private <T> T convertArgument(final String stringValue, final Class<T> type)
    {
        if (type.equals(String.class))
        {
            return type.cast(stringValue);
        }
        if (type.isEnum())
        {
            return convertEnum(stringValue, type);
        }
        throw new CliException(
                "Type '" + type + "' not supported for converting argument '" + stringValue + "'");
    }

    @SuppressWarnings("unchecked")
    private <T> T convertEnum(final String stringValue, final Class<T> type)
    {
        @SuppressWarnings("rawtypes")
        final Class enumType = type;
        try
        {
            @SuppressWarnings("rawtypes")
            final Enum enumValue = Enum.valueOf(enumType, stringValue);
            return type.cast(enumValue);
        }
        catch (final IllegalArgumentException e)
        {
            throw new CliException(
                    "Cannot convert value '" + stringValue + "' to enum " + type.getName(), e);
        }
    }

    private void reportUnsupportedSetterArgumentCount(final Method setter)
    {
        throw new CliException("Unsupported argument count for setter '" + setter
                + "'. Only one argument is allowed.");
    }

    private void reportMissingParamterValue(final String argumentName)
    {
        throw new CliException("No value for argument '" + argumentName + "'");
    }

    private boolean isParamterName(final String text)
    {
        return text.startsWith(SINGLE_CHAR_ARG_PREFIX);
    }

    private void assignUnnameArgument(final List<String> unnamedArguments)
    {
        final Method unnamedArgumentSetter = this.setters.get(UNNAMED_ARGUMENTS_SUFFIX);
        if (unnamedArgumentSetter != null)
        {
            assignValue(unnamedArgumentSetter, unnamedArguments);
        }
        else
        {
            throw new CliException("Unnamed arguments '" + unnamedArguments + "' are not allowed");
        }
    }

    private void assignValue(final Method setter, final Object value)
    {
        try
        {
            setter.invoke(this.argumentsReceiver, value);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
            throw new CliException(
                    "Error calling setter " + setter + " with argument '" + value + "'", e);
        }
    }
}
