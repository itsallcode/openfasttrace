package org.itsallcode.openfasttrace.core;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;

import org.itsallcode.openfasttrace.api.core.*;
import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class TestTracedLink
{
    @Test
    void testEqualsAndHashContract()
    {
        EqualsVerifier.forClass(TracedLink.class) //
                .withPrefabValues(LinkedSpecificationItem.class,
                        createPrefabricatedLinkedSpecificationItem("req", "foo", 1),
                        createPrefabricatedLinkedSpecificationItem("impl", "bar", 2))
                .verify();
    }

    private LinkedSpecificationItem createPrefabricatedLinkedSpecificationItem(
            final String artifactType, final String name, final int revision)
    {
        return new LinkedSpecificationItem(
                SpecificationItem.builder().id(artifactType, name, revision).build());
    }

    @Test
    void testTracedLink()
    {
        final LinkedSpecificationItem other = mock(LinkedSpecificationItem.class);
        final LinkStatus status = LinkStatus.AMBIGUOUS;
        final TracedLink link = new TracedLink(other, status);
        assertThat(link.getOtherLinkEnd(), equalTo(other));
        assertThat(link.getStatus(), equalTo(status));
    }
}
