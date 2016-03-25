package openfasttrack.core;

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
        final List<LinkedSpecificationItem> linkedItems = items.stream() //
                .map(LinkedSpecificationItem::new) //
                .collect(Collectors.toList());
        return linkedItems;
    }

    /**
     * Turn the items into linked items.
     *
     * @return a list of {@link LinkedSpecificationItem}s.
     */
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
