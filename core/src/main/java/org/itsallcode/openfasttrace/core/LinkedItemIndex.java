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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.itsallcode.openfasttrace.api.core.*;

/**
 * An idex for {@link LinkedSpecificationItem} that allows retrieving items by
 * {@link SpecificationItemId}, optionally ignoring the revision.
 */
public class LinkedItemIndex
{
    private final Map<SpecificationItemId, LinkedSpecificationItem> idIndex;
    private final Map<SpecificationItemIdWithoutVersion, List<LinkedSpecificationItem>> idIndexIgnoringVersion;

    private LinkedItemIndex(final Map<SpecificationItemId, LinkedSpecificationItem> idIndex,
            final Map<SpecificationItemIdWithoutVersion, List<LinkedSpecificationItem>> idIndexIgnoringVersion)
    {
        this.idIndex = idIndex;
        this.idIndexIgnoringVersion = idIndexIgnoringVersion;
    }

    /**
     * Create a new index containing the given items.
     * 
     * @param items
     *            the items to add to the new index.
     * @return a new index.
     */
    public static LinkedItemIndex create(final List<SpecificationItem> items)
    {
        return createFromWrappedItems(wrapItems(items));
    }

    private static List<LinkedSpecificationItem> wrapItems(final List<SpecificationItem> items)
    {
        return items.stream().map(LinkedSpecificationItem::new).collect(Collectors.toList());
    }

    /**
     * Create a new index containing the given wrapped items.
     * 
     * @param wrappedItems
     *            the items to add to the new index.
     * @return a new index.
     */
    public static LinkedItemIndex createFromWrappedItems(
            final List<LinkedSpecificationItem> wrappedItems)
    {
        return new LinkedItemIndex( //
                createIdIndex(wrappedItems), //
                createIdIndexIgnoringVersion(wrappedItems));

    }

    private static Map<SpecificationItemIdWithoutVersion, List<LinkedSpecificationItem>> createIdIndexIgnoringVersion(
            final List<LinkedSpecificationItem> wrappedItems)
    {
        return wrappedItems.stream()
                .collect(Collectors.groupingBy(SpecificationItemIdWithoutVersion::new));
    }

    private static Map<SpecificationItemId, LinkedSpecificationItem> createIdIndex(
            final List<LinkedSpecificationItem> wrappedItems)
    {
        return wrappedItems.stream().collect(Collectors.toMap(LinkedSpecificationItem::getId, //
                item -> item, //
                LinkedItemIndex::handleDuplicates));
    }

    // [impl->dsn~tracing.tracing.duplicate-items~1]
    private static LinkedSpecificationItem handleDuplicates(final LinkedSpecificationItem item1,
            final LinkedSpecificationItem item2)
    {
        item1.addLinkToItemWithStatus(item2, LinkStatus.DUPLICATE);
        item2.addLinkToItemWithStatus(item1, LinkStatus.DUPLICATE);
        return item1;
    }

    /**
     * @return the total number of items in this index.
     */
    public int size()
    {
        return this.idIndex.size();
    }

    /**
     * Get an item by id.
     * 
     * @param id
     *            the item id.
     * @return the item with the given id or {@code null} if no item exists.
     */
    public LinkedSpecificationItem getById(final SpecificationItemId id)
    {
        return this.idIndex.get(id);
    }

    /**
     * @return the number of IDs ignoring the version.
     */
    public int sizeIgnoringVersion()
    {
        return this.idIndexIgnoringVersion.size();
    }

    /**
     * Get all items for the given ID, ignoring the version.
     * 
     * @param id
     *            the item id.
     * @return the item with the given id or {@code null} if no item exists.
     */
    public List<LinkedSpecificationItem> getByIdIgnoringVersion(final SpecificationItemId id)
    {
        final List<LinkedSpecificationItem> items = this.idIndexIgnoringVersion
                .get(new SpecificationItemIdWithoutVersion(id));
        return items == null ? Collections.emptyList() : items;
    }

    static final class SpecificationItemIdWithoutVersion
    {
        private final String name;
        private final String artifcatType;

        public SpecificationItemIdWithoutVersion(final SpecificationItemId id)
        {
            this.name = id.getName();
            this.artifcatType = id.getArtifactType();
        }

        public SpecificationItemIdWithoutVersion(final LinkedSpecificationItem linkedItem)
        {
            this(linkedItem.getId());
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((this.artifcatType == null) ? 0 : this.artifcatType.hashCode());
            result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (obj == null)
            {
                return false;
            }
            if (getClass() != obj.getClass())
            {
                return false;
            }
            final SpecificationItemIdWithoutVersion other = (SpecificationItemIdWithoutVersion) obj;
            if (this.artifcatType == null)
            {
                if (other.artifcatType != null)
                {
                    return false;
                }
            }
            else if (!this.artifcatType.equals(other.artifcatType))
            {
                return false;
            }
            if (this.name == null)
            {
                if (other.name != null)
                {
                    return false;
                }
            }
            else if (!this.name.equals(other.name))
            {
                return false;
            }
            return true;
        }
    }
}
