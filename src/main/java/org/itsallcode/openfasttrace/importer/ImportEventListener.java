package org.itsallcode.openfasttrace.importer;

import org.itsallcode.openfasttrace.core.ItemStatus;
import org.itsallcode.openfasttrace.core.Location;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
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

import org.itsallcode.openfasttrace.core.SpecificationItemId;

/**
 * Interface for listeners of requirement import events
 *
 * Listeners register themselves at an importer. The importer issues an event
 * whenever it detects a new piece of a specification item.
 */
public interface ImportEventListener
{
    /**
     * The importer found a new specification item. The
     * {@link SpecificationItemId} must be defined using
     * {@link #setId(SpecificationItemId)}.
     */
    public void beginSpecificationItem();

    /**
     * The importer found the ID of the specification item
     *
     * @param id
     *            the ID of the new item
     */
    public void setId(final SpecificationItemId id);

    /**
     * The importer found the title of a specification item
     *
     * @param title
     *            the title
     */
    public void setTitle(String title);

    /**
     * The importer found the status of the specification item
     * 
     * @param status
     *            the status
     */
    public void setStatus(ItemStatus status);

    /**
     * Append a text block to an item description
     *
     * @param fragment
     *            the text to be appended to the description
     */
    public void appendDescription(final String fragment);

    /**
     * Append a text block to the rationale
     *
     * @param fragment
     *            the text to be appended to the rationale
     */
    public void appendRationale(final String fragment);

    /**
     * Append a text block to the comment
     *
     * @param fragment
     *            the text to be appended to the comment
     */
    public void appendComment(final String fragment);

    /**
     * The importer found a reference that indicates coverage of another
     * specification item
     *
     * @param id
     *            the ID of the item that is covered
     */
    public void addCoveredId(final SpecificationItemId id);

    /**
     * Add the ID of a specification item that this item depends on
     *
     * @param id
     *            the ID of the item depends on
     */
    public void addDependsOnId(final SpecificationItemId id);

    /**
     * The importer detected that the current specification object needs to be
     * covered by another specification item
     *
     * @param artifactType
     *            the type of specification item that is needed to provide
     *            coverage
     */
    public void addNeededArtifactType(final String artifactType);

    /**
     * Add a tag
     * 
     * @param tag
     *            the tag
     */
    public void addTag(String tag);

    /**
     * Set the location of the specification item in the imported file
     * 
     * @param path
     *            the path of the imported file
     * @param line
     *            the current line number
     */
    public void setLocation(final String path, int line);

    /**
     * The importer detected the end of a specification item
     */
    public void endSpecificationItem();

    /**
     * Set the location of the specification item in the imported file
     * 
     * @param location
     *            the location
     */
    public void setLocation(Location location);
}
