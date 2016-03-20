package openfasttrack.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Specification items with links that can be followed.
 */
public class LinkedSpecificationItem
{

    private final SpecificationItem item;
    private final List<LinkedSpecificationItem> coveredLinks = new ArrayList<>();
    private final List<LinkedSpecificationItem> itemsDuplicateId = new ArrayList<>();

    /**
     * Create a new instance of class {@link LinkedSpecificationItem}.
     *
     * @param item
     *            the actual specification item that is at the center of the
     *            links
     */
    public LinkedSpecificationItem(final SpecificationItem item)
    {
        this.item = item;
    }

    public SpecificationItemId getId()
    {
        return this.item.getId();
    }

    /**
     * Get the specification item.
     *
     * @return the specification item
     */
    public SpecificationItem getItem()
    {
        return this.item;
    }

    /**
     * Add a link to a covered item.
     *
     * @param item
     *            the covered item
     */
    public void addCovered(final LinkedSpecificationItem item)
    {
        this.coveredLinks.add(item);
    }

    /**
     * Get all links to covered items.
     *
     * @return the covered items
     */
    public List<LinkedSpecificationItem> getCoveredLinks()
    {
        return this.coveredLinks;
    }

    public void addDuplicateIdItem(final LinkedSpecificationItem item)
    {
        this.itemsDuplicateId.add(item);
    }

    public List<LinkedSpecificationItem> getItemsDuplicateId()
    {
        return this.itemsDuplicateId;
    }
}
