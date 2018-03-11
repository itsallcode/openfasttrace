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

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class TestTracedLink
{
    @Test
    public void testEqualsAndHashContract()
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
                new SpecificationItem.Builder().id(artifactType, name, revision).build());
    }

    @Test
    public void testTracedLink()
    {
        final LinkedSpecificationItem other = mock(LinkedSpecificationItem.class);
        final LinkStatus status = LinkStatus.AMBIGUOUS;
        final TracedLink link = new TracedLink(other, status);
        assertThat(link.getOtherLinkEnd(), equalTo(other));
        assertThat(link.getStatus(), equalTo(status));
    }
}
