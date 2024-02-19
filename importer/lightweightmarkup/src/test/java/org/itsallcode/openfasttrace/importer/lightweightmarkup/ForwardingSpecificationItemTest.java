package org.itsallcode.openfasttrace.importer.lightweightmarkup;

import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.importer.lightweightmarkup.ForwardingSpecificationItem;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

class ForwardingSpecificationItemTest
{
    @Test
    void parseForwardInstrcution()
    {
        final ForwardingSpecificationItem item = new ForwardingSpecificationItem(
                "arch --> dsn : req~web-ui-uses-corporate-design~1");
        assertAll(
                () -> assertThat(item.getSkippedArtifactType(), equalTo("arch")),
                () -> assertThat(item.getSkippedId(),
                        equalTo(SpecificationItemId.parseId("arch~web-ui-uses-corporate-design~1"))),
                () -> assertThat(item.getTargetArtifactTypes(), containsInAnyOrder("dsn")),
                () -> assertThat(item.getOriginalId(),
                        equalTo(SpecificationItemId.parseId("req~web-ui-uses-corporate-design~1"))));
    }
}