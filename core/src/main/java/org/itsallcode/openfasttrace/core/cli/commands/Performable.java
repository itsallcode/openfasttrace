package org.itsallcode.openfasttrace.core.cli.commands;

/**
 * Interface for OpenFastTrace commands.
 */
public interface Performable
{
    /**
     * Run an OpenFastTrace command
     * 
     * @return <code>true</code> if the operation performed was successful.
     */
    boolean run();
}
