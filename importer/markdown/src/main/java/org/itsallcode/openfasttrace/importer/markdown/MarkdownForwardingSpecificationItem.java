package org.itsallcode.openfasttrace.importer.markdown;

import java.util.Arrays;
import java.util.List;

import org.itsallcode.openfasttrace.api.core.SpecificationItemId;

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

/**
 * The {@link MarkdownForwardingSpecificationItem} splits the textual
 * representation of a specification item that forwards needed artifact coverage
 * into is components.
 */
public class MarkdownForwardingSpecificationItem
{
    static final String ORIGINAL_MARKER = ":";
    static final String FORWARD_MARKER = "-->";
    private final String skippedArtifactType;
    private final SpecificationItemId originalId;
    private final SpecificationItemId skippedId;
    private final List<String> targetArtifactTypes;

    /**
     * Create an instance of {@link MarkdownForwardingSpecificationItem}
     * 
     * @param forward
     *            the textual representation
     */
    public MarkdownForwardingSpecificationItem(final String forward)
    {
        final int posForwardMarker = forward.indexOf(FORWARD_MARKER);
        final int posOriginalMarker = forward.indexOf(ORIGINAL_MARKER);
        this.skippedArtifactType = forward.substring(0, posForwardMarker).trim();
        this.targetArtifactTypes = Arrays.asList(forward //
                .substring(posForwardMarker + FORWARD_MARKER.length(), posOriginalMarker) //
                .trim() //
                .split(",\\s*"));
        this.originalId = SpecificationItemId.parseId(forward //
                .substring(posOriginalMarker + ORIGINAL_MARKER.length()) //
                .trim());
        this.skippedId = SpecificationItemId.createId(this.skippedArtifactType,
                this.originalId.getName(), this.originalId.getRevision());
    }

    /**
     * The artifact type which forwards the needed coverage (in effect the one
     * that is "skipped" during authoring)
     * 
     * @return the "skipped" artifact type
     */
    public String getSkippedArtifactType()
    {
        return this.skippedArtifactType;
    }

    /**
     * The ID of the specification item that originally required coverage
     * 
     * @return the specification item ID
     */
    public SpecificationItemId getOriginalId()
    {
        return this.originalId;
    }

    /**
     * The constructed ID of the specification item that gets "skipped" by
     * forwarding the needed coverage
     * 
     * @return ID of the "skipped" item
     */
    public SpecificationItemId getSkippedId()
    {
        return this.skippedId;
    }

    /**
     * The list of artifact types the needed coverage is forwarded to
     * 
     * @return list of artifact types
     */
    public List<String> getTargetArtifactTypes()
    {
        return this.targetArtifactTypes;
    }
}