package org.itsallcode.openfasttrace.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.itsallcode.openfasttrace.api.core.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestTracer
{
    private static final SpecificationItemId ID_A = SpecificationItemId.parseId("req~a~1");
    private static final SpecificationItemId ID_B = SpecificationItemId.parseId("dsn~b~2");
    private static final SpecificationItemId ID_C = SpecificationItemId.parseId("impl~c~3");

    @Mock
    LinkedSpecificationItem aMock, bMock, cMock;

    @BeforeEach
    public void prepareTest()
    {
        lenient().when(this.aMock.getId()).thenReturn(ID_A);
        lenient().when(this.bMock.getId()).thenReturn(ID_B);
        lenient().when(this.cMock.getId()).thenReturn(ID_C);
    }

    @Test
    void testNoneDefect_Ok()
    {
        when(this.aMock.isDefect()).thenReturn(false);
        when(this.bMock.isDefect()).thenReturn(false);
        final Trace trace = traceItems(this.aMock, this.bMock);
        assertAll(() -> assertThat(trace.hasNoDefects(), equalTo(true)),
                () -> assertThat(trace.getDefectItems(), empty()),
                () -> assertThat(trace.getItems(), containsInAnyOrder(this.aMock, this.bMock)),
                () -> assertThat(trace.countDefects(), equalTo(0)),
                () -> assertThat(trace.count(), equalTo(2)),
                () -> assertThat(trace.getDefectIds(), empty()));
    }

    private Trace traceItems(final LinkedSpecificationItem... items)
    {
        final Tracer tracer = new Tracer();
        return tracer.trace(Arrays.asList(items));
    }

    @Test
    void testNoneDefect_NotOk()
    {
        when(this.aMock.isDefect()).thenReturn(false);
        when(this.bMock.isDefect()).thenReturn(true);
        final Trace trace = traceItems(this.aMock, this.bMock);
        assertAll(() -> assertThat(trace.hasNoDefects(), equalTo(false)),
                () -> assertThat(trace.getDefectItems(), containsInAnyOrder(this.bMock)),
                () -> assertThat(trace.getItems(), containsInAnyOrder(this.aMock, this.bMock)),
                () -> assertThat(trace.countDefects(), equalTo(1)),
                () -> assertThat(trace.count(), equalTo(2)),
                () -> assertThat(trace.getDefectIds(), containsInAnyOrder(ID_B)));
    }
}