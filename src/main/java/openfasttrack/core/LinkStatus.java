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

public enum LinkStatus
{
    // Outgoing coverage link status
    COVERS, PREDATED, OUTDATED, AMBIGUOUS, UNWANTED, ORPHANED, //
    // Incoming coverage link status
    COVERED_SHALLOW, COVERED_UNWANTED, COVERED_PREDATED, COVERED_OUTDATED, //
    // Duplicate link status
    DUPLICATE;

    /**
     * Check if this is a bad link status.
     * 
     * @return <code>true</code> if the link status is bad.
     */
    public boolean isBad()
    {
        return (this != COVERS) && (this != COVERED_SHALLOW);
    }
}
