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

public enum LinkStatus
{
    // Outgoing coverage link status
    COVERS(" "), PREDATED(">"), OUTDATED("<"), AMBIGUOUS("?"), UNWANTED("+"), ORPHANED("/"), //
    // Incoming coverage link status
    COVERED_SHALLOW(" "), COVERED_UNWANTED("+"), COVERED_PREDATED(">"), COVERED_OUTDATED("<"), //
    // Duplicate link status
    DUPLICATE("?");

    private final String shortTag;

    private LinkStatus(final String shortTag)
    {
        this.shortTag = shortTag;
    }

    /**
     * Check if this is a bad link status.
     * 
     * @return <code>true</code> if the link status is bad.
     */
    public boolean isBad()
    {
        return (this != COVERS) && (this != COVERED_SHALLOW);
    }

    /**
     * Check if the link is an outgoing link.
     * 
     * @return <code>true</code> if the link is outgoing.
     */
    public boolean isOutgoing()
    {
        return (this == LinkStatus.COVERS) || isBadOutgoing();
    }

    /**
     * Check if the link is a bad outgoing link.
     * 
     * @return <code>true</code> if the link is outgoing and has an unclean
     *         status.
     */
    public boolean isBadOutgoing()
    {
        return (this == PREDATED) || (this == OUTDATED) || (this == AMBIGUOUS) || (this == UNWANTED)
                || (this == ORPHANED);
    }

    /**
     * Check if the link is an incoming link.
     * 
     * @return <code>true</code> if the link is incoming.
     */
    public boolean isIncoming()
    {
        return (this == COVERED_SHALLOW) || isBadIncoming();
    }

    /**
     * Check if the link is a bad incoming link.
     * 
     * @return <code>true</code> if the link is incoming and has an unclean
     *         status.
     */
    public boolean isBadIncoming()
    {
        return (this == COVERED_UNWANTED) || (this == COVERED_PREDATED)
                || (this == COVERED_OUTDATED);
    }

    /**
     * Check if the link status indicates a duplicate.
     * 
     * @return <code>true</code> if the link points to duplicate.
     */
    public boolean isDuplicate()
    {
        return this == DUPLICATE;
    }

    /**
     * Get the short tag representing the link status
     * 
     * @return short tag
     */
    public String getShortTag()
    {
        return this.shortTag;
    }
}
