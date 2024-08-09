package org.itsallcode.openfasttrace.core.serviceloader;

import java.io.Closeable;
import java.util.stream.Stream;

/**
 * A loader for services, similar to Java's {@link java.util.ServiceLoader}.
 * 
 * @param <T>
 *            service type
 */
public interface Loader<T> extends Closeable
{
    /**
     * Load services.
     * 
     * @return a stream of services
     */
    Stream<T> load();

    /**
     * Close the loader and potentially the underlying ClassLoader.
     */
    void close();
}
