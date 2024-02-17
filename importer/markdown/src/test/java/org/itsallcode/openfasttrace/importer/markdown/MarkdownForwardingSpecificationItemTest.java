package org.itsallcode.openfasttrace.importer.markdown;

import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

class MarkdownForwardingSpecificationItemTest
{
    @Test
    void parseForwardInstrcution()
    {
        final MarkdownForwardingSpecificationItem item = new MarkdownForwardingSpecificationItem(
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