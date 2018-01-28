package org.itsallcode.openfasttrace.core;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TestItemStatus
{

    @Test
    public void testParseString()
    {
        assertThat(ItemStatus.parseString("approved"), equalTo(ItemStatus.APPROVED));
        assertThat(ItemStatus.parseString("Approved"), equalTo(ItemStatus.APPROVED));
        assertThat(ItemStatus.parseString("dRaft"), equalTo(ItemStatus.DRAFT));
        assertThat(ItemStatus.parseString("PROPOSED"), equalTo(ItemStatus.PROPOSED));
        assertThat(ItemStatus.parseString("ReJeCtEd"), equalTo(ItemStatus.REJECTED));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseUnknownStringThrowsException()
    {
        ItemStatus.parseString("Unkown");
    }
}
