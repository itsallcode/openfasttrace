package org.itsallcode.openfasttrace.core.cli;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
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
    private static final Logger LOG = Logger.getLogger(CommandLineInterpreter.class.getName());

    private static final String UNNAMED_ARGUMENTS_SUFFIX = "unnamedvalues";
    private static final String SINGLE_CHAR_ARG_PREFIX = "-";
    private static final String MULTIPLE_CHAR_ARG_PREFIX = "--";
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
        final Map<String, Method> setterMethods = methods
                .filter(method -> method.getName().startsWith(SETTER_PREFIX))
                .collect(toMap(CommandLineInterpreter::getSetterName, Function.identity()));
        LOG.finest(() -> "Found setter methods " + setterMethods.keySet()
                + " for argument receiver " + argumentsReceiver.getClass());
        return setterMethods;
    }

    private static String getSetterName(final Method method)
    {
        return method.getName()
                .substring(SETTER_PREFIX.length())
                .toLowerCase();
    }

    /**
     * Parse the command line and inject the values into the receiver object
     * 
     * @throws CliException
     *             in case the command line could not be parsed
     */
    public void parse() throws CliException
    {
        final List<String> unnamedArguments = new ArrayList<>();
        final ListIterator<String> iterator = asList(this.arguments).listIterator();
        while (iterator.hasNext())
        {
            final String argument = iterator.next();
            if (argument.startsWith(MULTIPLE_CHAR_ARG_PREFIX))
            {
                handleNamedArgument(iterator, argument);
            }
            else if (argument.startsWith(SINGLE_CHAR_ARG_PREFIX))
            {
                handleChainedSingleCharacterArguments(iterator, argument);
            }
            else
            {
                handleUnnamedArgument(unnamedArguments, argument);
            }
        }
        if (!unnamedArguments.isEmpty())
        {
            assignUnnamedArgument(unnamedArguments);
        }
    }

    private void handleChainedSingleCharacterArguments(final ListIterator<String> iterator,
            final String argument) throws CliException
    {
        final String characters = argument.replaceFirst(SINGLE_CHAR_ARG_PREFIX, "").toLowerCase();
        final int lastPosition = characters.length() - 1;

        for (int position = 0; position <= lastPosition; ++position)
        {
            final String character = characters.substring(position, position + 1);
            if (this.setters.containsKey(character))
            {
                if (position == lastPosition)
                {
                    handleExpectedNamedArgument(iterator, character);
                }
                else
                {
                    handleExpectedNamedArgument(null, character);
                }
            }
            else
            {
                reportUnexpectedNamedArgument(character);
            }
        }
    }

    private void handleNamedArgument(final ListIterator<String> iterator, final String argument)
            throws CliException
    {
        final String argumentName = argument.replace("-", "").toLowerCase();
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

    private void reportUnexpectedNamedArgument(final String argument) throws CliException
    {
        throw new CliException("Unexpected parameter '" + argument + "' is not allowed");
    }

    private void handleExpectedNamedArgument(final ListIterator<String> iterator,
            final String argumentName) throws CliException
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
            if ((iterator != null) && iterator.hasNext())
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

    private <T> T convertArgument(final String stringValue, final Class<T> type) throws CliException
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
    private <T> T convertEnum(final String stringValue, final Class<T> type) throws CliException
    {
        @SuppressWarnings("rawtypes")
        final Class enumType = type;
        try
        {
            @SuppressWarnings("rawtypes")
            final Enum enumValue = Enum.valueOf(enumType, stringValue.toUpperCase());
            return type.cast(enumValue);
        }
        catch (final IllegalArgumentException e)
        {
            final List<T> availableValues = asList(type.getEnumConstants());
            throw new CliException("Cannot convert value '" + stringValue + "' to enum " + type.getName()
                    + ". Allowed values: " + availableValues, e);
        }
    }

    private void reportUnsupportedSetterArgumentCount(final Method setter) throws CliException
    {
        throw new CliException("Unsupported argument count for setter '" + setter
                + "'. Only one argument is allowed.");
    }

    private void reportMissingParamterValue(final String argumentName) throws CliException
    {
        throw new CliException("No value for argument '" + argumentName + "'");
    }

    private boolean isParamterName(final String text)
    {
        return text.startsWith(SINGLE_CHAR_ARG_PREFIX);
    }

    private void assignUnnamedArgument(final List<String> unnamedArguments) throws CliException
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

    private void assignValue(final Method setter, final Object value) throws CliException
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
