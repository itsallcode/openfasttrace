package openfasttrack.cli;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
    private Map<String, Method> setters = new HashMap<>();

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
        findAllSettersInArgumentsReceiver();
    }

    private void findAllSettersInArgumentsReceiver()
    {
        final Class<?> receiverClass = this.argumentsReceiver.getClass();
        final Stream<Method> methods = Arrays.stream(receiverClass.getMethods());
        this.setters = methods.filter(method -> method.getName().startsWith(SETTER_PREFIX)) //
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
        assignUnnameArgument(unnamedArguments);
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
            reportUnexpectedNamedArgument();
        }
    }

    private void handleUnnamedArgument(final List<String> unnamedArguments, final String argument)
    {
        unnamedArguments.add(argument);
    }

    private void reportUnexpectedNamedArgument()
    {
        // TODO Auto-generated method stub
    }

    private void handleExpectedNamedArgument(final ListIterator<String> iterator,
            final String argumentName)
    {
        final Method setter = this.setters.get(argumentName);
        final Class<?> setterParameterType = setter.getParameterTypes()[0];
        if (setterParameterType.equals(boolean.class) || setterParameterType.equals(Boolean.class))
        {
            assignBooleanValue(setter, true);
        }
        else if (setterParameterType.equals(String.class))
        {
            if (iterator.hasNext())
            {
                final String successor = iterator.next();
                if (isParamterName(successor))
                {
                    iterator.previous();
                    reportMissingParamterValue();
                }
                else
                {
                    assignStringValue(setter, successor);
                }
            }
            else
            {
                reportMissingParamterValue();
            }
        }
    }

    private void reportMissingParamterValue()
    {
        // TODO Auto-generated method stub

    }

    private void assignStringValue(final Method setter, final String value)
    {
        try
        {
            setter.invoke(this.argumentsReceiver, value);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private boolean isParamterName(final String text)
    {
        return text.startsWith(SINGLE_CHAR_ARG_PREFIX);
    }

    private void assignBooleanValue(final Method setter, final boolean isSet)
    {
        try
        {
            setter.invoke(this.argumentsReceiver, isSet);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void assignUnnameArgument(final List<String> unnamedArguments)
    {
        if (this.setters.containsKey(UNNAMED_ARGUMENTS_SUFFIX))
        {
            try
            {
                this.setters.get(UNNAMED_ARGUMENTS_SUFFIX).invoke(this.argumentsReceiver,
                        unnamedArguments);
            }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
