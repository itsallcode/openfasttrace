package org.itsallcode.openfasttrace.core.serviceloader;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * A service loader that delegates to a list of other loaders.
 */
class DelegatingLoader<T> implements Loader<T>
{
    private final List<Loader<T>> delegates;

    DelegatingLoader(final List<Loader<T>> delegates)
    {
        this.delegates = new ArrayList<>(delegates);
    }

    @Override
    public Stream<T> load()
    {
        return delegates.stream().flatMap(Loader::load);
    }

    @Override
    public void close()
    {
        delegates.forEach(Loader::close);
    }
}
