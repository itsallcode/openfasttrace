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

import javax.annotation.Generated;

public final class Location
{
    private static final int NO_COLUMN = -1;
    private final String path;
    private final int line;
    private final int column;

    private Location(final String path, final int line, final int column)
    {
        this.path = path;
        this.line = line;
        this.column = column;
    }

    /**
     * Create a new {@link Location} with the given file path and line.
     * 
     * @param path
     *            the file path of the new {@link Location}.
     * @param line
     *            the line number the new {@link Location}, must be
     *            <code>&gt;=0</code>.
     * @return the new {@link Location}.
     */
    public static Location create(final String path, final int line)
    {
        validateLine(line);
        return new Location(path, line, NO_COLUMN);
    }

    private static void validateLine(final int line)
    {
        if (line <= 0)
        {
            throw new IllegalArgumentException("Illegal value for line: " + line);
        }
    }

    public static Location create(final String path, final int line, final int column)
    {
        validateLine(line);
        validateColumn(column);
        return new Location(path, line, column);
    }

    private static void validateColumn(final int column)
    {
        if (column <= 0)
        {
            throw new IllegalArgumentException("Illegal value for column: " + column);
        }
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
        final Location other = (Location) obj;
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
        return this.path + ":" + this.line + (this.column != NO_COLUMN ? ":" + this.column : "");
    }
}
