package openfasttrack.core;

/*-
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 - 2017 hamstercommunity
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

import java.util.List;
import java.util.stream.Collectors;

public class Linker
{
    private final List<LinkedSpecificationItem> linkedItems;
    private final LinkedItemIndex index;

    /**
     * Create a {@link Linker} for specification items.
     *
     * @param items
     *            the specification items to be linked.
     */
    public Linker(final List<SpecificationItem> items)
    {
        this.linkedItems = wrapItems(items);
        this.index = LinkedItemIndex.createFromWrappedItems(this.linkedItems);
    }

    private List<LinkedSpecificationItem> wrapItems(final List<SpecificationItem> items)
    {
        return items.stream() //
                .map(LinkedSpecificationItem::new) //
                .collect(Collectors.toList());
    }

    /**
     * Turn the items into linked items.
     *
     * @return a list of {@link LinkedSpecificationItem}s.
     */
    // [impl->dsn~tracing.needed-coverage-status~1]
    public List<LinkedSpecificationItem> link()
    {

        for (final LinkedSpecificationItem linkedItem : this.linkedItems)
        {
            linkItem(linkedItem);
        }
        return this.linkedItems;
    }

    private void linkItem(final LinkedSpecificationItem item)
    {
        for (final SpecificationItemId id : item.getCoveredIds())
        {
            linkItemToItemWithId(item, id);
        }
    }

    // [impl->dsn~tracing.outgoing-coverage-link-status~1]
    // [impl->dsn~tracing.incoming-coverage-link-status~1]
    private void linkItemToItemWithId(final LinkedSpecificationItem item,
            final SpecificationItemId id)
    {
        LinkedSpecificationItem coveredLinkedItem;
        if ((coveredLinkedItem = this.index.getById(id)) != null)
        {
            linkMatchingRevision(item, coveredLinkedItem);
        }
        else
        {
            linkIgnoringRevision(item, id);
        }
    }

    private void linkMatchingRevision(final LinkedSpecificationItem covering,
            final LinkedSpecificationItem covered)
    {
        final String coveringArtifactType = covering.getId().getArtifactType();
        if (covered.getItem().getNeedsArtifactTypes().contains(coveringArtifactType))
        {
            covering.addLinkToItemWithStatus(covered, LinkStatus.COVERS);
            covered.addLinkToItemWithStatus(covering, LinkStatus.COVERED_SHALLOW);
            covered.addCoveredArtifactType(coveringArtifactType);
        }
        else
        {
            covering.addLinkToItemWithStatus(covered, LinkStatus.UNWANTED);
            covered.addLinkToItemWithStatus(covering, LinkStatus.COVERED_UNWANTED);
        }
    }

    private void linkIgnoringRevision(final LinkedSpecificationItem item,
            final SpecificationItemId id)
    {
        final List<LinkedSpecificationItem> coveredLinkedItems = this.index
                .getByIdIgnoringVersion(id);
        if (coveredLinkedItems.isEmpty())
        {
            // FIXME: Collect list of covered specification items that do not
            // exist
            // https://github.com/hamstercommunity/openfasttrack/issues/86
            System.err.println("Error: " + item.getId() + " links to non-existent " + id);
        }
        else
        {
            for (final LinkedSpecificationItem itemCoveredIgnoringVersion : coveredLinkedItems)
            {
                if (id.getRevision() < itemCoveredIgnoringVersion.getId().getRevision())
                {
                    item.addLinkToItemWithStatus(itemCoveredIgnoringVersion, LinkStatus.OUTDATED);
                    itemCoveredIgnoringVersion.addLinkToItemWithStatus(item,
                            LinkStatus.COVERED_OUTDATED);
                }
                else
                {
                    item.addLinkToItemWithStatus(itemCoveredIgnoringVersion, LinkStatus.PREDATED);
                    itemCoveredIgnoringVersion.addLinkToItemWithStatus(item,
                            LinkStatus.COVERED_PREDATED);
                }
            }
        }
    }

}
