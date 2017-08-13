package openfasttrack.core;

/*
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 hamstercommunity
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
        when(this.aMock.getDeepCoverageStatus()).thenReturn(DeepCoverageStatus.COVERED);
        when(this.bMock.getDeepCoverageStatus()).thenReturn(DeepCoverageStatus.COVERED);
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
        when(this.aMock.getDeepCoverageStatus()).thenReturn(DeepCoverageStatus.COVERED);
        when(this.bMock.getDeepCoverageStatus()).thenReturn(DeepCoverageStatus.UNCOVERED);
        final Trace trace = traceItems(this.aMock, this.bMock);
        assertThat(trace.isAllCovered(), equalTo(false));
        assertThat(trace.getUncoveredItems(), containsInAnyOrder(this.bMock));
        assertThat(trace.getItems(), containsInAnyOrder(this.aMock, this.bMock));
        assertThat(trace.countUncovered(), equalTo(1));
        assertThat(trace.count(), equalTo(2));
        assertThat(trace.getUncoveredIds(), containsInAnyOrder(ID_B));
    }
}