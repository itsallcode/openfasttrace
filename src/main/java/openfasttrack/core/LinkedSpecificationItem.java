package openfasttrack.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Specification items with links that can be followed.
 */
public class LinkedSpecificationItem
{

    private final SpecificationItem item;
    private final Map<LinkStatus, List<LinkedSpecificationItem>> links = new EnumMap<>(
            LinkStatus.class);
    private final Set<String> coveredArtifactTypes = new HashSet<>();

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

    /**
     * Get the ID of the specification item.
     *
     * @return ID of the specification item
     */
    public SpecificationItemId getId()
    {
        return this.item.getId();
    }

    /**
     * Get the description of the specification item.
     *
     * @return the description
     */
    public String getDescription()
    {
        return this.item.getDescription();
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
     * Add a link to another item with a status.
     *
     * @param item
     *            the item to be linked to
     * @param status
     *            the link status
     */
    public void addLinkToItemWithStatus(final LinkedSpecificationItem item, final LinkStatus status)
    {
        List<LinkedSpecificationItem> linksWithStatus = this.links.get(status);
        if (linksWithStatus == null)
        {
            linksWithStatus = new ArrayList<>();
            this.links.put(status, linksWithStatus);
        }
        linksWithStatus.add(item);
    }

    /**
     * Get all links to the items by item status.
     *
     * @return the covered items
     */
    public List<LinkedSpecificationItem> getLinksByStatus(final LinkStatus status)
    {
        final List<LinkedSpecificationItem> linksWithStatus = this.links.get(status);
        return (linksWithStatus == null) ? Collections.<LinkedSpecificationItem> emptyList()
                : linksWithStatus;
    }

    /**
     * Get the ID of the items this {@link LinkedSpecificationItem} covers.
     *
     * @return the list of IDs
     */
    public List<SpecificationItemId> getCoveredIds()
    {
        return this.getItem().getCoveredIds();
    }

    /**
     * Get the artifact types in which this item needs to be covered.
     *
     * @return list of artifact types.
     */
    public List<String> getNeedsArtifactTypes()
    {
        return this.getItem().getNeedsArtifactTypes();
    }

    /**
     * Add a covered artifact type.
     *
     * @param artifactType
     *            the covered artifact type.
     */
    public void addCoveredArtifactType(final String artifactType)
    {
        this.coveredArtifactTypes.add(artifactType);
    }

    /**
     * Get the artifact type which are covered.
     *
     * @return the list of covered artifact types.
     */
    public Set<String> getCoveredArtifactTypes()
    {
        return this.coveredArtifactTypes;
    }

    /**
     * Check if the item is covered shallow (i.e. if for all needed artifact
     * types coverage exists without recursive search).
     *
     * @return <code>true</code> if the item is covered
     */
    public boolean isCoveredShallow()
    {
        return this.getCoveredArtifactTypes().containsAll(this.getNeedsArtifactTypes());
    }

    /**
     * Check if this item and all items providing coverage for it are covered.
     *
     * @return true if the item is covered recursively.
     */
    public boolean isCoveredDeeply()
    {
        boolean covered = isCoveredShallow();
        for (final LinkedSpecificationItem coveringItem : getLinksByStatus(LinkStatus.COVERS))
        {
            covered = (covered && coveringItem.isCoveredDeeply());
        }
        return covered;
    }

    /**
     * Check if the item is clean (i.e. covered deeply and not having any broken
     * links)
     *
     * @return <code>true</code> if the item is clean.
     */
    public boolean isOk()
    {
        return isCoveredDeeply();
    }
}