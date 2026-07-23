package org.itsallcode.openfasttrace.importer.tag.common;

import java.util.Collections;
import java.util.List;

import org.itsallcode.openfasttrace.importer.tag.common.LineReader.LineConsumer;

class DelegatingLineConsumer implements LineConsumer
{
    private final List<LineConsumer> delegates;

    DelegatingLineConsumer(final List<LineConsumer> delegates)
    {
        this.delegates = Collections.unmodifiableList(delegates);
    }

    @Override
    public void readLine(final int lineNumber, final String line)
    {
        this.delegates.forEach(delegate -> delegate.readLine(lineNumber, line));
    }
}
