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
import java.util.*;
import java.util.function.Predicate;

/**
 * Specification items with links that can be followed.
 */
// [impl->dsn~linked-specification-item~1]
public class LinkedSpecificationItem
{
    private final SpecificationItem item;
    private final Map<LinkStatus, List<LinkedSpecificationItem>> links = new EnumMap<>(
            LinkStatus.class);
    private final Set<String> coveredArtifactTypes = new HashSet<>();
    private final Set<String> overCoveredArtifactTypes = new HashSet<>();

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
     * Get the title of the specification item.
     *
     * @return title of the specification item
     */
    public String getTitle()
    {
        return this.item.getTitle();
    }

    /**
     * Get the maturity status of the specification item.
     *
     * @return maturity status of the specification item
     */
    public ItemStatus getStatus()
    {
        return this.item.getStatus();
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
     * Get the source location of the item.
     * 
     * @return the location
     */
    public Location getLocation()
    {
        return this.item.getLocation();
    }

    /**
     * Get the tags associated with this item.
     * 
     * @return list of tags
     */
    public List<String> getTags()
    {
        return this.item.getTags();
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
        this.links.computeIfAbsent(status, key -> new ArrayList<>());
        this.links.get(status).add(item);
    }

    /**
     * Get all links to the item
     *
     * @return linked item
     */
    public Map<LinkStatus, List<LinkedSpecificationItem>> getLinks()
    {
        return this.links;
    }

    /**
     * Get all links to the item by item status.
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

    public void addOverCoveredArtifactType(final String artifactType)
    {
        this.overCoveredArtifactTypes.add(artifactType);
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
     * Get a list of all artifact types that have unwanted coverage.
     * 
     * @return list of over-covered artifact types.
     */
    public Set<String> getOverCoveredArtifactTypes()
    {

        return this.overCoveredArtifactTypes;
    }

    /**
     * Get a list of all artifact types for which required coverage is missing.
     * 
     * @return list of uncovered artifact types.
     */
    public List<String> getUncoveredArtifactTypes()
    {
        final List<String> uncovered = new ArrayList<>(getNeedsArtifactTypes());
        uncovered.removeAll(getCoveredArtifactTypes());
        return uncovered;
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
     * @return covered, uncovered or ring.
     */
    // [impl->dsn~tracing.deep-coverage~1]
    public DeepCoverageStatus getDeepCoverageStatus()
    {
        return getDeepCoverageStatusEndRecursionStartingAt(this.getId());
    }

    // [impl->dsn~tracing.link-cycle~1]
    private DeepCoverageStatus getDeepCoverageStatusEndRecursionStartingAt(
            final SpecificationItemId startId)
    {
        final boolean covered = isCoveredShallow();
        for (final LinkedSpecificationItem coveringItem : getLinksByStatus(LinkStatus.COVERS))
        {
            if (coveringItem.getId().equals(startId))
            {
                return DeepCoverageStatus.CYCLE;
            }
            else
            {
                final DeepCoverageStatus otherStatus = coveringItem
                        .getDeepCoverageStatusEndRecursionStartingAt(startId);
                if (otherStatus != DeepCoverageStatus.COVERED)
                {
                    return otherStatus;
                }
            }
        }
        return covered ? DeepCoverageStatus.COVERED : DeepCoverageStatus.UNCOVERED;
    }

    /**
     * Check if the item is defect.
     * 
     * An item counts a defect if the following applies:
     * 
     * <pre>
     * has duplicates
     * or (not rejected
     *     and (any outgoing coverage link has a different status than "Covers"
     *          or not covered deeply
     *         )
     *    )
     * </pre>
     *
     * @return <code>true</code> if the item is defect.
     */
    // [impl->dsn~tracing.defect-items~2]
    public boolean isDefect()
    {
        return hasDuplicates() //
                || (this.getStatus() != ItemStatus.REJECTED) //
                        && (hasBadLinks()
                                || (getDeepCoverageStatus() != DeepCoverageStatus.COVERED));
    }

    private boolean hasBadLinks()
    {
        for (final LinkStatus status : this.links.keySet())
        {
            if (status.isBad())
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Count all outgoing links.
     * 
     * @return the total number of outgoing links.
     */
    public int countOutgoingLinks()
    {
        return countLinksWithPredicate(entry -> entry.getKey().isOutgoing());
    }

    private int countLinksWithPredicate(
            final Predicate<Map.Entry<LinkStatus, List<LinkedSpecificationItem>>> predicate)
    {
        return this.links.entrySet().stream().filter(predicate)
                .mapToInt(entry -> entry.getValue().size()).sum();
    }

    /**
     * Count all bad outgoing links.
     * 
     * @return the number of outgoing links that are bad.
     */
    public int countOutgoingBadLinks()
    {
        return countLinksWithPredicate(entry -> entry.getKey().isBadOutgoing());
    }

    /**
     * Count all incoming links.
     * 
     * @return the total number of incoming links.
     */
    public int countIncomingLinks()
    {
        return countLinksWithPredicate(entry -> entry.getKey().isIncoming());
    }

    /**
     * Count all bad incoming links.
     * 
     * @return the number of incoming links that are bad.
     */
    public int countIncomingBadLinks()
    {
        return countLinksWithPredicate(entry -> entry.getKey().isBadIncoming());
    }

    /**
     * Count the duplicate links.
     * 
     * @return the number of links that are duplicates.
     */
    public int countDuplicateLinks()
    {
        return countLinksWithPredicate(entry -> entry.getKey().isDuplicate());
    }

    public boolean hasDuplicates()
    {
        return countDuplicateLinks() != 0;
    }
}
