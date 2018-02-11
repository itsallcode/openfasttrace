package org.itsallcode.openfasttrace.core;

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

public class TracedLink
{
    private final LinkedSpecificationItem other;
    private final LinkStatus status;

    public TracedLink(final LinkedSpecificationItem other, final LinkStatus status)
    {
        this.other = other;
        this.status = status;
    }

    public LinkedSpecificationItem getOther()
    {
        return this.other;
    }

    public LinkStatus getStatus()
    {
        return this.status;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.other == null) ? 0 : this.other.hashCode());
        result = prime * result + ((this.status == null) ? 0 : this.status.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final TracedLink other = (TracedLink) obj;
        if (this.other == null)
        {
            if (other.other != null)
            {
                return false;
            }
        }
        else if (!this.other.equals(other.other))
        {
            return false;
        }
        if (this.status != other.status)
        {
            return false;
        }
        return true;
    }
}
