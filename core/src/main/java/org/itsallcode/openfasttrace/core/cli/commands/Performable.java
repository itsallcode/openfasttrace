package org.itsallcode.openfasttrace.core.cli.commands;

/**
 * Interface for OpenFastTrace commands.
 */
public interface Performable
{
    /**
     * Run an OpenFastTrace command
     * 
     * @return {@code true} if the operation performed was successful.
     */
    boolean run();
}
