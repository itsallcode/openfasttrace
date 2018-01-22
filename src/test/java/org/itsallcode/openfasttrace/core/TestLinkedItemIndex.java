package org.itsallcode.openfasttrace.core;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2017 itsallcode.org
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

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.itsallcode.openfasttrace.core.LinkedItemIndex.SpecificationItemIdWithoutVersion;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import nl.jqno.equalsverifier.EqualsVerifier;

public class TestLinkedItemIndex
{
    private final static SpecificationItemId DUPLICATE_ID_1 = SpecificationItemId.createId("type",
            "name", 42);
    private final static SpecificationItemId DUPLICATE_ID_2 = SpecificationItemId.createId("type",
            "name", 42);
    private final static SpecificationItemId DUPLICATE_ID_INGORING_VERSION = SpecificationItemId
            .createId("type", "name", 42 + 1);
    private final static SpecificationItemId UNIQUE_ID = SpecificationItemId.createId("type2",
            "name2", 42 + 1);

    @Mock
    private SpecificationItem duplicateIdItem1Mock, duplicateIdItem2Mock, uniqueIdItemMock,
            duplicateIdIgnoringVersionItemMock;

    @Before
    public void prepareTest()
    {
        MockitoAnnotations.initMocks(this);
        when(this.duplicateIdItem1Mock.getId()).thenReturn(DUPLICATE_ID_1);
        when(this.duplicateIdItem2Mock.getId()).thenReturn(DUPLICATE_ID_2);
        when(this.uniqueIdItemMock.getId()).thenReturn(UNIQUE_ID);
        when(this.duplicateIdIgnoringVersionItemMock.getId())
                .thenReturn(DUPLICATE_ID_INGORING_VERSION);
    }

    @Test
    public void equalsSpecificationItemIdWithoutVersionContract()
    {
        EqualsVerifier.forClass(SpecificationItemIdWithoutVersion.class).verify();
    }

    @Test
    public void testEmptyIndex()
    {
        final LinkedItemIndex index = createIndex();
        assertThat(index.size(), equalTo(0));
        assertThat(index.getById(DUPLICATE_ID_1), nullValue());
    }

    private LinkedItemIndex createIndex(final SpecificationItem... items)
    {
        return LinkedItemIndex.create(asList(items));
    }

    @Test
    public void testNonEmptyIndex()
    {
        final LinkedItemIndex index = createIndex(this.duplicateIdItem1Mock, this.uniqueIdItemMock);
        assertThat(index.size(), equalTo(2));
        assertThat(index.getById(DUPLICATE_ID_1).getItem(),
                sameInstance(this.duplicateIdItem1Mock));
    }

    // [utest->dsn~tracing.tracing.duplicate-items~1]
    @Test
    public void testDuplicateId()
    {
        final LinkedItemIndex index = createIndex(this.duplicateIdItem1Mock,
                this.duplicateIdItem2Mock);
        final LinkedSpecificationItem duplicateItem1 = index.getById(DUPLICATE_ID_1);
        assertThat(index.size(), equalTo(1));
        assertThat(duplicateItem1.getLinksByStatus(LinkStatus.DUPLICATE), hasSize(1));
        final LinkedSpecificationItem duplicateItem2 = duplicateItem1
                .getLinksByStatus(LinkStatus.DUPLICATE).get(0);

        assertThat(duplicateItem2.getLinksByStatus(LinkStatus.DUPLICATE),
                contains(sameInstance(duplicateItem1)));
        assertThat(duplicateItem1.getLinksByStatus(LinkStatus.DUPLICATE),
                contains(sameInstance(duplicateItem2)));
    }

    @Test
    public void testDuplicateVersionId()
    {
        final LinkedItemIndex index = createIndex(this.duplicateIdItem1Mock,
                this.duplicateIdIgnoringVersionItemMock, this.uniqueIdItemMock);
        assertThat(index.size(), equalTo(3));
        assertThat(index.sizeIgnoringVersion(), equalTo(2));
        assertThat(index.getByIdIgnoringVersion(DUPLICATE_ID_1),
                containsInAnyOrder(asList(
                        LinkedItemInstanceMatcher.sameItemInstance(this.duplicateIdItem1Mock),
                        LinkedItemInstanceMatcher
                                .sameItemInstance(this.duplicateIdIgnoringVersionItemMock))));
    }
}
