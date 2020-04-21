package org.itsallcode.openfasttrace.api.core;

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
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
     * Get the title or a fallback if the optional title is not specified
     * 
     * @return title if exists, otherwise name part of the ID
     */
    public String getTitleWithFallback()
    {
        final String title = this.getTitle();
        if (title != null && !title.isEmpty())
        {
            return title;
        }
        final SpecificationItemId id = this.getId();
        if (id != null)
        {
            final String name = id.getName();
            if (name != null && !name.isEmpty())
            {
                return name;
            }
        }
        return "???";
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
     * @param status
     *            link status
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
     * Get a list of traced links (i.e. links including the tracing status)
     * 
     * @return list of traced links
     */
    public List<TracedLink> getTracedLinks()
    {
        final List<TracedLink> tracedLinks = new ArrayList<>();
        for (final Entry<LinkStatus, List<LinkedSpecificationItem>> entry : this.links.entrySet())
        {
            for (final LinkedSpecificationItem other : entry.getValue())
            {
                tracedLinks.add(new TracedLink(other, entry.getKey()));
            }
        }
        return tracedLinks;
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
     *            covered artifact type.
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
        return getDeepCoverageStatusEndRecursionStartingAt(this.getId(),
                DeepCoverageStatus.COVERED);
    }

    // [impl->dsn~tracing.link-cycle~1]
    private DeepCoverageStatus getDeepCoverageStatusEndRecursionStartingAt(
            final SpecificationItemId startId, final DeepCoverageStatus worstStatusSeen)
    {
        DeepCoverageStatus status = worstStatusSeen;
        for (final LinkedSpecificationItem incomingItem : getIncomingItems())
        {
            if (incomingItem.getId().equals(startId))
            {
                return DeepCoverageStatus.CYCLE;
            }
            else
            {
                final DeepCoverageStatus otherStatus = incomingItem
                        .getDeepCoverageStatusEndRecursionStartingAt(startId, status);
                if (otherStatus == DeepCoverageStatus.CYCLE)
                {
                    return DeepCoverageStatus.CYCLE;
                }
                status = DeepCoverageStatus.getWorst(status, otherStatus);
            }
        }
        if (status == DeepCoverageStatus.COVERED && !isCoveredShallow())
        {
            return DeepCoverageStatus.UNCOVERED;
        }
        else
        {
            return status;
        }
    }

    private List<LinkedSpecificationItem> getIncomingItems()
    {
        return this.links.entrySet() //
                .stream() //
                .filter(entry -> entry.getKey().isIncoming()) //
                .flatMap(entry -> entry.getValue().stream()) //
                .collect(Collectors.toList());
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
                || (getStatus() != ItemStatus.REJECTED) //
                        && (hasBadLinks()
                                || (getDeepCoverageStatus() != DeepCoverageStatus.COVERED));
    }

    /**
     * @return <code>true</code> if the item has one or more links
     */
    public boolean hasLinks()
    {
        return !this.links.isEmpty();
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
     * @return total number of outgoing links.
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
     * @return number of outgoing links that are bad.
     */
    public int countOutgoingBadLinks()
    {
        return countLinksWithPredicate(entry -> entry.getKey().isBadOutgoing());
    }

    /**
     * Count all incoming links.
     * 
     * @return total number of incoming links.
     */
    public int countIncomingLinks()
    {
        return countLinksWithPredicate(entry -> entry.getKey().isIncoming());
    }

    /**
     * Count all bad incoming links.
     * 
     * @return number of incoming links that are bad.
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

    /**
     * @return <code>true</code> if this item has one ore more duplicates.
     */
    public boolean hasDuplicates()
    {
        return countDuplicateLinks() != 0;
    }

    /**
     * Get the artifact type of the linked specification item
     * 
     * @return artifact type
     */
    public String getArtifactType()
    {
        return this.item.getArtifactType();
    }

    /**
     * Get the name part of the linked specification item ID
     * 
     * <p>
     * Not to be mixed up with the
     * {@link org.itsallcode.openfasttrace.api.core.LinkedSpecificationItem#getTitle()}
     * of the linked specification item
     * </p>
     * 
     * @return name part of the specification item ID
     */
    public String getName()
    {

        return this.item.getName();
    }

    /**
     * Get the revision of the specification item
     * 
     * @return revision
     */
    public int getRevision()
    {
        return this.item.getRevision();
    }
}