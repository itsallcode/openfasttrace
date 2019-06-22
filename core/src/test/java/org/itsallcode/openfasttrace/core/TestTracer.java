package org.itsallcode.openfasttrace.core;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.itsallcode.openfasttrace.api.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.api.core.Trace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
        MockitoAnnotations.initMocks(this);
        when(this.aMock.getId()).thenReturn(ID_A);
        when(this.bMock.getId()).thenReturn(ID_B);
        when(this.cMock.getId()).thenReturn(ID_C);
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