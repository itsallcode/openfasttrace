package org.itsallcode.openfasttrace.core;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.itsallcode.openfasttrace.api.core.ItemStatus;
import org.junit.jupiter.api.Test;

class TestItemStatus
{

    // [utest->dsn~specification-item~4]
    @Test
    void testParseString()
    {
        assertThat(ItemStatus.parseString("approved"), equalTo(ItemStatus.APPROVED));
        assertThat(ItemStatus.parseString("Approved"), equalTo(ItemStatus.APPROVED));
        assertThat(ItemStatus.parseString("dRaft"), equalTo(ItemStatus.DRAFT));
        assertThat(ItemStatus.parseString("PROPOSED"), equalTo(ItemStatus.PROPOSED));
        assertThat(ItemStatus.parseString("ReJeCtEd"), equalTo(ItemStatus.REJECTED));
    }

    // [utest->dsn~specification-item~4]
    @Test
    void testParseUnknownStringThrowsException()
    {
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ItemStatus.parseString("unknown"));
        assertThat(exception.getMessage(), equalTo("Unrecognized specification item status: \"unknown\""));
    }

    // [utest->dsn~specification-item~4]
    @Test
    void testParseUnknownStringAsUnknown()
    {
        assertThat(ItemStatus.parseOrUnknown("in review"), equalTo(ItemStatus.UNKNOWN));
    }
}
