package org.itsallcode.openfasttrace.core;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.itsallcode.openfasttrace.api.core.ItemStatus;
import org.junit.jupiter.api.Test;

class TestItemStatus
{

    @Test
    void testParseString()
    {
        assertThat(ItemStatus.parseString("approved"), equalTo(ItemStatus.APPROVED));
        assertThat(ItemStatus.parseString("Approved"), equalTo(ItemStatus.APPROVED));
        assertThat(ItemStatus.parseString("dRaft"), equalTo(ItemStatus.DRAFT));
        assertThat(ItemStatus.parseString("PROPOSED"), equalTo(ItemStatus.PROPOSED));
        assertThat(ItemStatus.parseString("ReJeCtEd"), equalTo(ItemStatus.REJECTED));
    }

    @Test
    void testParseUnknownStringThrowsException()
    {
        assertThrows(IllegalArgumentException.class, () -> ItemStatus.parseString("Unkown"));
    }
}