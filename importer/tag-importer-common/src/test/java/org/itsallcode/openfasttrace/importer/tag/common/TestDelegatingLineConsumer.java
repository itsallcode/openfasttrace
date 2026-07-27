package org.itsallcode.openfasttrace.importer.tag.common;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.itsallcode.openfasttrace.importer.tag.common.LineReader.LineConsumer;
import org.junit.jupiter.api.Test;

class TestDelegatingLineConsumer
{
    @Test
    void testReadLineCallsAllDelegates()
    {
        final LineConsumer firstDelegate = mock(LineConsumer.class);
        final LineConsumer secondDelegate = mock(LineConsumer.class);
        final DelegatingLineConsumer consumer = new DelegatingLineConsumer(List.of(firstDelegate, secondDelegate));
        consumer.readLine(2, "line");
        verify(firstDelegate).readLine(2, "line");
        verify(secondDelegate).readLine(2, "line");
    }

    @Test
    void testFinishesAllDelegates()
    {
        final LineConsumer firstDelegate = mock(LineConsumer.class);
        final LineConsumer secondDelegate = mock(LineConsumer.class);
        final DelegatingLineConsumer consumer = new DelegatingLineConsumer(List.of(firstDelegate, secondDelegate));
        consumer.finish();
        verify(firstDelegate).finish();
        verify(secondDelegate).finish();
    }
}
