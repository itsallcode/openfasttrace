package openfasttrack.core;

/*
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 - 2017 hamstercommunity
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

import javax.annotation.Generated;

public class Location
{
    private final String path;
    private final int line;

    private Location(final String path, final int line)
    {
        this.path = path;
        this.line = line;
    }

    public static Location create(final String path, final int line)
    {
        if (line <= 0)
        {
            throw new IllegalArgumentException("Illegal value for line: " + line);
        }
        return new Location(path, line);
    }

    public String getPath()
    {
        return this.path;
    }

    public int getLine()
    {
        return this.line;
    }

    @Generated("Eclipse")
    @Override
    public final int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.line;
        result = prime * result + ((this.path == null) ? 0 : this.path.hashCode());
        return result;
    }

    @Override
    public final boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof Location))
        {
            return false;
        }
        final Location other = (Location) obj;
        if (this.line != other.line)
        {
            return false;
        }
        if (this.path == null)
        {
            if (other.path != null)
            {
                return false;
            }
        }
        else if (!this.path.equals(other.path))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return this.path + ":" + this.line;
    }
}
