package org.itsallcode.openfasttrace.core.serviceloader;

import java.util.stream.Stream;

interface Loader<T>
{
    Stream<T> load();
}
