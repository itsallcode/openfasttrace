package org.itsallcode.openfasttrace.importer.lightweightmarkup.statemachine;

/**
 * Action that is executed as a result of a state transition in the line parser.
 */
@FunctionalInterface
public interface TransitionAction
{
    /**
     * Execute the transition action.
     */
    void transit();
}
