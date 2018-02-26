package org.itsallcode.openfasttrace.importer;

import static org.hamcrest.Matchers.containsInAnyOrder;

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

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.itsallcode.openfasttrace.core.ItemStatus;
import org.itsallcode.openfasttrace.core.SpecificationItem;
import org.itsallcode.openfasttrace.core.SpecificationItemId;
import org.junit.Test;;

public class TestSpecificationListBuilder
{

    private final static SpecificationItemId ID = SpecificationItemId.parseId("feat~id~1");

    @Test
    public void testBuildBasicItem()
    {
        final String expectedTitle = "title";
        final String expectedDescription = "description";
        final SpecificationListBuilder itemsBuilder = createBasicListBuilder();
        itemsBuilder.appendDescription(expectedDescription);
        itemsBuilder.setTitle(expectedTitle);
        final List<SpecificationItem> items = itemsBuilder.build();
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getId(), equalTo(ID));
        assertThat(items.get(0).getDescription(), equalTo("description"));
        assertThat(items.get(0).getTitle(), equalTo("title"));
    }

    private SpecificationListBuilder createBasicListBuilder()
    {
        final SpecificationListBuilder itemsBuilder = SpecificationListBuilder.create();
        itemsBuilder.beginSpecificationItem();
        itemsBuilder.setId(ID);
        return itemsBuilder;
    }

    @Test
    public void testBuildWithStatus()
    {
        final SpecificationListBuilder itemsBuilder = createBasicListBuilder();
        itemsBuilder.setStatus(ItemStatus.DRAFT);
        final List<SpecificationItem> items = itemsBuilder.build();
        assertThat(items.get(0).getStatus(), equalTo(ItemStatus.DRAFT));
    }

    @Test
    public void testBuildWithTags()
    {
        final SpecificationListBuilder itemsBuilder = createBasicListBuilder();
        itemsBuilder.addTag("foo");
        itemsBuilder.addTag("bar");
        final List<SpecificationItem> items = itemsBuilder.build();
        assertThat(items.get(0).getTags(), containsInAnyOrder("foo", "bar"));
    }

    // [utest->dsn~ignoring-artifact-types-during-import~2]
    @Test
    public void testIgnoreArtifactOfType()
    {
        final SpecificationListBuilder ignoringBuilder = createListBuilderIgnoringImpl();
        ignoringBuilder.beginSpecificationItem();
        ignoringBuilder.setId(SpecificationItemId.createId("impl", "ignore", 1));
        ignoringBuilder.endSpecificationItem();
        ignoringBuilder.beginSpecificationItem();
        final SpecificationItemId importedId = SpecificationItemId.createId("dsn", "import", 1);
        ignoringBuilder.setId(importedId);
        ignoringBuilder.endSpecificationItem();
        final List<SpecificationItem> items = ignoringBuilder.build();
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getId(), equalTo(importedId));
    }

    private SpecificationListBuilder createListBuilderIgnoringImpl()
    {
        final SpecificationListBuilder ignoringBuilder = SpecificationListBuilder
                .createIgnoringArtifactTypes(Arrays.asList("impl"));
        return ignoringBuilder;
    }

    // [utest->dsn~ignoring-artifact-types-during-import~2]
    @Test
    public void testIgnoreNeededArtifactType()
    {
        final SpecificationListBuilder ignoringBuilder = createListBuilderIgnoringImpl();
        ignoringBuilder.beginSpecificationItem();
        final SpecificationItemId id = SpecificationItemId.createId("dsn", "import", 1);
        ignoringBuilder.setId(id);
        ignoringBuilder.addNeededArtifactType("impl");
        ignoringBuilder.addNeededArtifactType("utest");
        ignoringBuilder.addNeededArtifactType("itest");
        ignoringBuilder.endSpecificationItem();
        final List<SpecificationItem> items = ignoringBuilder.build();
        assertThat(items.get(0).getNeedsArtifactTypes(), containsInAnyOrder("utest", "itest"));
    }

    // [utest->dsn~ignoring-artifact-types-during-import~2]
    @Test
    public void testIgnoreCoversLinkWithArtifactType()
    {
        final SpecificationItemId acceptedId = SpecificationItemId.createId("utest", "accept", 2);
        final SpecificationItemId rejectedId = SpecificationItemId.createId("impl", "reject", 3);
        final SpecificationListBuilder ignoringBuilder = SpecificationListBuilder
                .createIgnoringArtifactTypes(Arrays.asList("impl"));
        ignoringBuilder.beginSpecificationItem();
        final SpecificationItemId importedId = SpecificationItemId.createId("dsn", "import", 1);
        ignoringBuilder.setId(importedId);
        ignoringBuilder.addCoveredId(acceptedId);
        ignoringBuilder.addCoveredId(rejectedId);
        ignoringBuilder.endSpecificationItem();
        final List<SpecificationItem> items = ignoringBuilder.build();
        assertThat(items.get(0).getCoveredIds(), containsInAnyOrder(acceptedId));
    }

    // [utest->dsn~ignoring-artifact-types-during-import~2]
    @Test
    public void testIgnoreDependsLinkWithArtifactType()
    {
        final SpecificationItemId acceptedId = SpecificationItemId.createId("utest", "accept", 2);
        final SpecificationItemId rejectedId = SpecificationItemId.createId("impl", "reject", 3);
        final SpecificationListBuilder ignoringBuilder = SpecificationListBuilder
                .createIgnoringArtifactTypes(Arrays.asList("impl"));
        ignoringBuilder.beginSpecificationItem();
        final SpecificationItemId importedId = SpecificationItemId.createId("dsn", "import", 1);
        ignoringBuilder.setId(importedId);
        ignoringBuilder.addDependsOnId(acceptedId);
        ignoringBuilder.addDependsOnId(rejectedId);
        ignoringBuilder.endSpecificationItem();
        final List<SpecificationItem> items = ignoringBuilder.build();
        assertThat(items.get(0).getDependOnIds(), containsInAnyOrder(acceptedId));
    }

    @Test
    public void testDuplicateIdNotIgnored()
    {
        final SpecificationListBuilder itemsBuilder = SpecificationListBuilder.create();
        itemsBuilder.beginSpecificationItem();
        itemsBuilder.setId(ID);
        itemsBuilder.endSpecificationItem();
        itemsBuilder.beginSpecificationItem();
        itemsBuilder.setId(ID);
        itemsBuilder.endSpecificationItem();
        assertThat(itemsBuilder.getItemCount(), equalTo(2));
    }
}
