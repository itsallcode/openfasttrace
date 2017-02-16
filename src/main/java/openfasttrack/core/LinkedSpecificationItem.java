package openfasttrack.core;

/*
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 hamstercommunity
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

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
     * Get the source location of the item.
     * 
     * @return the location
     */
    public Location getLocation()
    {
        return this.item.getLocation();
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
     * Get a list of all artifact types that have unwanted coverage.
     * 
     * @return list of over-covered artifact types.
     */
    public List<String> getOverCoveredArtifactTypes()
    {
        final List<String> overCovered = new ArrayList<>(getCoveredArtifactTypes());
        overCovered.removeAll(getNeedsArtifactTypes());
        return overCovered;
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
     * @return true if the item is covered recursively.
     */
    public boolean isCoveredDeeply()
    {
        boolean covered = isCoveredShallow();
        for (final LinkedSpecificationItem coveringItem : getLinksByStatus(LinkStatus.COVERS))
        {
            covered = covered && coveringItem.isCoveredDeeply();
        }
        return covered;
    }

    /**
     * Check if the item is defect.
     * 
     * An item counts a defect if one of the following applies:
     * <ul>
     * <li>The item has offending links (broken coverage, duplicates)</li>
     * <li>The item is not covered deeply
     * <li>
     * </ul>
     *
     * @return <code>true</code> if the item is defect.
     */
    public boolean isDefect()
    {
        for (final LinkStatus status : this.links.keySet())
        {
            if (status.isBad())
            {
                return true;
            }
        }
        return !isCoveredDeeply();
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
}