package org.itsallcode.openfasttrace.core.serviceloader;

import java.io.Closeable;
import java.util.stream.Stream;

interface Loader<T> extends Closeable
{
    Stream<T> load();

    void close();
}
