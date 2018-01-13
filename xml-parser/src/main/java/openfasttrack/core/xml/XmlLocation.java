package openfasttrack.core.xml;

/*-
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

public final class XmlLocation
{
    private final String path;
    private final int line;
    private final int column;

    private XmlLocation(final String path, final int line, final int column)
    {
        this.path = path;
        this.line = line;
        this.column = column;
    }

    public static XmlLocation create(final String path, final int line, final int column)
    {
        if (line <= 0)
        {
            throw new IllegalArgumentException("Illegal value for line: " + line);
        }
        if (column <= 0)
        {
            throw new IllegalArgumentException("Illegal value for line: " + line);
        }
        return new XmlLocation(path, line, column);
    }

    public String getPath()
    {
        return this.path;
    }

    public int getLine()
    {
        return this.line;
    }

    public int getColumn()
    {
        return this.column;
    }

    @Generated("Eclipse")
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.column;
        result = prime * result + this.line;
        result = prime * result + ((this.path == null) ? 0 : this.path.hashCode());
        return result;
    }

    @Generated("Eclipse")
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
        final XmlLocation other = (XmlLocation) obj;
        if (this.column != other.column)
        {
            return false;
        }
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
        return this.path + ":" + this.line + (this.column != -1 ? ":" + this.column : "");
    }
}
