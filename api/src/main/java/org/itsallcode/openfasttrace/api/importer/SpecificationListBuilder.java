package org.itsallcode.openfasttrace.api.importer;

import java.util.Collections;

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

import java.util.LinkedList;
import java.util.List;

import org.itsallcode.openfasttrace.api.FilterSettings;
import org.itsallcode.openfasttrace.api.core.*;

/**
 * The {@link SpecificationListBuilder} consumes import events and generates a
 * map of specification items from them. The key to the map is the specification
 * item ID.
 */
public class SpecificationListBuilder implements ImportEventListener
{
    private final FilterSettings filterSettings;
    private final List<SpecificationItem> items = new LinkedList<>();
    private SpecificationItem.Builder itemBuilder = null;
    private SpecificationItemId id = null;
    private StringBuilder description = new StringBuilder();
    private StringBuilder rationale = new StringBuilder();
    private StringBuilder comment = new StringBuilder();
    private Location location;

    private SpecificationListBuilder(final FilterSettings filterSettings)
    {
        this.filterSettings = filterSettings;
    }

    /**
     * Creates a new {@link SpecificationListBuilder}.
     * 
     * @return a new {@link SpecificationListBuilder}.
     */
    public static SpecificationListBuilder create()
    {
        return new SpecificationListBuilder(new FilterSettings.Builder().build());
    }

    /**
     * Creates a new {@link SpecificationListBuilder} with the given
     * {@link FilterSettings}.
     * 
     * @param filterSettings
     *            the filter settings for the new builder.
     * @return a new {@link SpecificationListBuilder}.
     */
    public static SpecificationListBuilder createWithFilter(final FilterSettings filterSettings)
    {
        return new SpecificationListBuilder(filterSettings);
    }

    @Override
    public void beginSpecificationItem()
    {
        this.itemBuilder = SpecificationItem.builder();
    }

    private void resetState()
    {
        this.itemBuilder = null;
        this.description = new StringBuilder();
        this.rationale = new StringBuilder();
        this.comment = new StringBuilder();
        this.location = null;
        this.id = null;
    }

    @Override
    public void setId(final SpecificationItemId id)
    {
        this.id = id;
    }

    @Override
    public void setStatus(final ItemStatus status)
    {
        this.itemBuilder.status(status);
    }

    @Override
    public void addCoveredId(final SpecificationItemId id)
    {
        // [impl->dsn~filtering-by-artifact-types-during-import~1]
        if (isAcceptedArtifactType(id.getArtifactType()))
        {
            this.itemBuilder.addCoveredId(id);
        }
    }

    @Override
    public void appendDescription(final String fragment)
    {
        this.description.append(fragment);
    }

    @Override
    public void appendRationale(final String fragment)
    {
        this.rationale.append(fragment);
    }

    @Override
    public void appendComment(final String fragment)
    {
        this.comment.append(fragment);
    }

    @Override
    public void addDependsOnId(final SpecificationItemId id)
    {
        // [impl->dsn~filtering-by-artifact-types-during-import~1]
        if (isAcceptedArtifactType(id.getArtifactType()))
        {
            this.itemBuilder.addDependOnId(id);
        }
    }

    @Override
    public void addNeededArtifactType(final String artifactType)
    {
        // [impl->dsn~filtering-by-artifact-types-during-import~1]
        if (isAcceptedArtifactType(artifactType))
        {
            this.itemBuilder.addNeedsArtifactType(artifactType);
        }
    }

    @Override
    public void addTag(final String tag)
    {
        this.itemBuilder.addTag(tag);
    }

    /**
     * Build the list of specification items
     *
     * @return the list of specification items collected up to this point
     */
    public List<SpecificationItem> build()
    {
        this.endSpecificationItem();
        return this.items;
    }

    /**
     * @return the total number of items.
     */
    public int getItemCount()
    {
        return this.items.size();
    }

    @Override
    public void setTitle(final String title)
    {
        this.itemBuilder.title(title);
    }

    @Override
    public void setLocation(final String path, final int line)
    {
        this.setLocation(Location.create(path, line));
    }

    @Override
    public void setLocation(final Location location)
    {
        this.location = location;
    }

    @Override
    public void endSpecificationItem()
    {
        if (this.itemBuilder != null)
        {
            final SpecificationItem item = createNewSpecificationItem();
            // [impl->dsn~filtering-by-artifact-types-during-import~1]
            if (isAccepted(item))
            {
                addNewItemToList(item);
            }
        }
        resetState();
    }

    private SpecificationItem createNewSpecificationItem()
    {
        return this.itemBuilder //
                .id(this.id) //
                .description(this.description.toString()) //
                .rationale(this.rationale.toString()) //
                .comment(this.comment.toString()) //
                .location(this.location) //
                .build();
    }

    private boolean isAccepted(final SpecificationItem item)
    {
        return isAcceptedArtifactType(item.getArtifactType())
                && matchesTagsCriteria(item.getTags());
    }

    // [impl->dsn~filtering-by-tags-during-import~1]
    // [impl->dsn~filtering-by-tags-or-no-tags-during-import~1]
    private boolean matchesTagsCriteria(final List<String> tags)
    {
        return !this.filterSettings.isTagCriteriaSet()
                || (this.filterSettings.withoutTags() && tags.isEmpty())
                || !Collections.disjoint(this.filterSettings.getTags(), tags);
    }

    private boolean isAcceptedArtifactType(final String artifactType)
    {
        return !this.filterSettings.isArtifactTypeCriteriaSet()
                || this.filterSettings.getArtifactTypes().contains(artifactType);
    }

    private void addNewItemToList(final SpecificationItem item)
    {
        this.items.add(item);
    }

    @Override
    public void setForwards(final boolean forwards)
    {
        this.itemBuilder.forwards(forwards);
    }
}