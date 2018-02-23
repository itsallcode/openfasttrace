package org.itsallcode.openfasttrace.core;

import javax.annotation.Generated;

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

public final class TracedLink
{
    private final LinkedSpecificationItem otherLinkEnd;
    private final LinkStatus status;

    public TracedLink(final LinkedSpecificationItem otherLinkEnd, final LinkStatus status)
    {
        this.otherLinkEnd = otherLinkEnd;
        this.status = status;
    }

    public LinkedSpecificationItem getOtherLinkEnd()
    {
        return this.otherLinkEnd;
    }

    public LinkStatus getStatus()
    {
        return this.status;
    }

    @Generated(value = "org.eclipse.Eclipse")
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.otherLinkEnd == null) ? 0 : this.otherLinkEnd.hashCode());
        result = prime * result + ((this.status == null) ? 0 : this.status.hashCode());
        return result;
    }

    @Generated(value = "org.eclipse.Eclipse")
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
        if (!(obj instanceof TracedLink))
        {
            return false;
        }
        final TracedLink other = (TracedLink) obj;
        if (this.otherLinkEnd == null)
        {
            if (other.otherLinkEnd != null)
            {
                return false;
            }
        }
        else if (!this.otherLinkEnd.equals(other.otherLinkEnd))
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