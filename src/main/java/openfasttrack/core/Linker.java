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
            LinkedSpecificationItem coveredLinkedItem;
            if ((coveredLinkedItem = this.index.getById(id)) != null)
            {
                if (coveredLinkedItem.getItem().getNeedsArtifactTypes()
                        .contains(item.getId().getArtifactType()))
                {
                    item.addLinkToItemWithStatus(coveredLinkedItem, LinkStatus.COVERS);
                }
                else
                {
                    item.addLinkToItemWithStatus(coveredLinkedItem, LinkStatus.UNWANTED);
                }
            }
            else
            {
                final List<LinkedSpecificationItem> coveredLinkedItems = this.index
                        .getByIdIgnoringVersion(id);
                for (final LinkedSpecificationItem itemCoveredIgnoringVersion : coveredLinkedItems)
                {
                    if (id.getRevision() < itemCoveredIgnoringVersion.getId().getRevision())
                    {
                        item.addLinkToItemWithStatus(coveredLinkedItem, LinkStatus.OUTDATED);
                    }
                    else
                    {
                        item.addLinkToItemWithStatus(coveredLinkedItem, LinkStatus.PREDATED);
                    }
                }
            }
        }
    }

}
