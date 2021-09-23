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

/**
 * This enumeration represents the different statuses of a coverage link between
 * two items.
 */
public enum LinkStatus
{
    /**
     * Outgoing coverage link status: item 1 covers item 2.
     */
    COVERS(" ", "covers"),
    /**
     * Outgoing coverage link status: item 1 covers a newer revision of item 2.
     */
    PREDATED(">", "predated"),
    /**
     * Outgoing coverage link status: item 1 covers an older revision of item 2.
     */
    OUTDATED("<", "outdated"),
    /**
     * Outgoing coverage link status: two items with the same id are covered by
     * another item.
     */
    AMBIGUOUS("?", "ambiguous"),
    /**
     * Outgoing coverage link status: an item covers another item that does not
     * require coverage.
     */
    UNWANTED("+", "unwanted"),
    /** Outgoing coverage link status: an item covers a non-existing item. */
    ORPHANED("/", "orphaned"),

    /**
     * Incoming coverage link status: an item is directly covered by another
     * item.
     */
    COVERED_SHALLOW(" ", "covered shallow"),
    /**
     * Incoming coverage link status: an item is covered by another item
     * although it does not require coverage.
     */
    COVERED_UNWANTED("+", "unwanted coverage"),
    /**
     * Incoming coverage link status: an item is covered by another item that
     * specifies a newer revision.
     */
    COVERED_PREDATED(">", "predated coverage"),
    /**
     * Incoming coverage link status: an item is covered by another item that
     * specifies an older revision.
     */
    COVERED_OUTDATED("<", "outdated coverage"),

    /** Duplicate link status: two items have the same ID. */
    DUPLICATE("?", "duplicate");

    private final String shortTag;
    private final String text;

    private LinkStatus(final String shortTag, final String text)
    {
        this.shortTag = shortTag;
        this.text = text;
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

    /**
     * Get a text representing the link status
     * 
     * @return link status in human readable form
     */
    @Override
    public String toString()
    {
        return this.text;
    }
}