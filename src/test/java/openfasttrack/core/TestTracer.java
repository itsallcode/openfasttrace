package openfasttrack.core;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TestTracer
{
    private static final SpecificationItemId ID_A = SpecificationItemId.parseId("req~a~1");
    private static final SpecificationItemId ID_B = SpecificationItemId.parseId("dsn~b~2");
    private static final SpecificationItemId ID_C = SpecificationItemId.parseId("impl~c~3");
    @Mock
    LinkedSpecificationItem aMock, bMock, cMock;

    @Before
    public void prepareTest()
    {
        MockitoAnnotations.initMocks(this);
        when(this.aMock.getId()).thenReturn(ID_A);
        when(this.bMock.getId()).thenReturn(ID_B);
        when(this.cMock.getId()).thenReturn(ID_C);
    }

    @Test
    public void testAllCovered_Ok()
    {
        when(this.aMock.isCoveredDeeply()).thenReturn(true);
        when(this.bMock.isCoveredDeeply()).thenReturn(true);
        final Trace trace = traceItems(this.aMock, this.bMock);
        assertThat(trace.isAllCovered(), equalTo(true));
        assertThat(trace.getUncoveredItems(), empty());
        assertThat(trace.getItems(), containsInAnyOrder(this.aMock, this.bMock));
        assertThat(trace.countUncovered(), equalTo(0));
        assertThat(trace.count(), equalTo(2));
        assertThat(trace.getUncoveredIds(), empty());
    }

    private Trace traceItems(final LinkedSpecificationItem... items)
    {
        final Tracer tracer = new Tracer();
        return tracer.trace(Arrays.asList(items));
    }

    @Test
    public void testAllCovered_NotOk()
    {
        when(this.aMock.isCoveredDeeply()).thenReturn(true);
        when(this.bMock.isCoveredDeeply()).thenReturn(false);
        final Trace trace = traceItems(this.aMock, this.bMock);
        assertThat(trace.isAllCovered(), equalTo(false));
        assertThat(trace.getUncoveredItems(), containsInAnyOrder(this.bMock));
        assertThat(trace.getItems(), containsInAnyOrder(this.aMock, this.bMock));
        assertThat(trace.countUncovered(), equalTo(1));
        assertThat(trace.count(), equalTo(2));
        assertThat(trace.getUncoveredIds(), containsInAnyOrder(ID_B));
    }
}