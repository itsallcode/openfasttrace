package org.itsallcode.openfasttrace.core;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.core.LinkedItemIndex.SpecificationItemIdWithoutVersion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import nl.jqno.equalsverifier.EqualsVerifier;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TestLinkedItemIndex
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

    @BeforeEach
    public void prepareTest()
    {
        when(this.duplicateIdItem1Mock.getId()).thenReturn(DUPLICATE_ID_1);
        when(this.duplicateIdItem2Mock.getId()).thenReturn(DUPLICATE_ID_2);
        when(this.uniqueIdItemMock.getId()).thenReturn(UNIQUE_ID);
        when(this.duplicateIdIgnoringVersionItemMock.getId())
                .thenReturn(DUPLICATE_ID_INGORING_VERSION);
    }

    @Test
    void equalsSpecificationItemIdWithoutVersionContract()
    {
        EqualsVerifier.forClass(SpecificationItemIdWithoutVersion.class).verify();
    }

    @Test
    void testEmptyIndex()
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
    void testNonEmptyIndex()
    {
        final LinkedItemIndex index = createIndex(this.duplicateIdItem1Mock, this.uniqueIdItemMock);
        assertThat(index.size(), equalTo(2));
        assertThat(index.getById(DUPLICATE_ID_1).getItem(),
                sameInstance(this.duplicateIdItem1Mock));
    }

    // [utest->dsn~tracing.tracing.duplicate-items~1]
    @Test
    void testDuplicateId()
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
    void testDuplicateVersionId()
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
