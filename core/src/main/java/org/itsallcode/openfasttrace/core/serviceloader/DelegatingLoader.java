package org.itsallcode.openfasttrace.core.serviceloader;

import java.util.List;
import java.util.stream.Stream;

class DelegatingLoader<T> implements Loader<T>
{
    private final List<Loader<T>> delegates;

    DelegatingLoader(final List<Loader<T>> delegates)
    {
        this.delegates = delegates;
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
