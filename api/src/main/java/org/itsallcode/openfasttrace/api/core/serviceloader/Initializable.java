package org.itsallcode.openfasttrace.api.core.serviceloader;

/**
 * Interface for services that are initialized with a context after
 * instantiation.
 *
 * @param <C>
 *            the context injected via {@link #init(Object)}.
 */
public interface Initializable<C>
{
    /**
     * Initializes the service with the given context.
     * 
     * @param context
     *            the context object with which to initialize the service
     *            object.
     */
    void init(C context);
}
