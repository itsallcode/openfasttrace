package org.itsallcode.openfasttrace.core.serviceloader;

/**
 * Interface for initializable services that can be loaded by
 * {@link InitializingServiceLoader}.
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
    public void init(C context);
}
