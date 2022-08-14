package org.itsallcode.openfasttrace.api.cli;

/**
 * This interface provides access to the current directory. This allows stubbing
 * the service for test purposes.
 */
public interface DirectoryService
{
    /**
     * Get the current directory or the override directory if set
     * 
     * @return current directory
     */
    String getCurrent();
}