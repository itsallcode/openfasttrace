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

import javax.annotation.processing.Generated;

/**
 * The location of a coverage item.
 */
public final class Location
{
    /** Value indicating an unknown line. */
    public static final int NO_LINE = -1;
    /** Value indicating an unknown column. */
    public static final int NO_COLUMN = -1;
    private final String path;
    private final int line;
    private final int column;

    private Location(final String path, final int line, final int column)
    {
        this.path = path;
        this.line = line;
        this.column = column;
    }

    private Location(final Builder builder)
    {
        this.path = builder.path;
        this.line = builder.line;
        this.column = builder.column;
    }

    /**
     * Create a new {@link Location} with the given file path and line.
     * 
     * @param path
     *            path of the new {@link Location}.
     * @param line
     *            line number of the new {@link Location}, must be
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

    /**
     * Create a new {@link Location} with the given file path and line.
     * 
     * @param path
     *            path of the new {@link Location}.
     * @param line
     *            the line number the new {@link Location}, must be
     *            <code>&gt;=0</code>.
     * @param column
     *            column number the new {@link Location}, must be
     *            <code>&gt;=0</code>.
     * @return the new {@link Location}.
     */
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

    /**
     * Get the path component of the location
     * 
     * @return path
     */
    public String getPath()
    {
        return this.path;
    }

    /**
     * Get the line component of the location
     * 
     * @return line
     */
    public int getLine()
    {
        return this.line;
    }

    /**
     * Get the column component of the location
     * 
     * @return column
     */
    public int getColumn()
    {
        return this.column;
    }

    @Generated(value = "org.eclipse.Eclipse")
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
        return this.path //
                + (this.line != NO_LINE ? ":" + this.line : "") //
                + (this.column != NO_COLUMN ? ":" + this.column : "");
    }

    /**
     * Create a new builder for {@link Location} objects.
     * 
     * @return a new {@link Builder}.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * A builder for {@link Location}. Use {@link Location#builder()} to create
     * a new builder and call {@link #build()} to build a {@link Location}.
     */
    public static class Builder
    {
        private String path;
        private int line = NO_LINE;
        private int column = NO_COLUMN;

        private Builder()
        {
            // prevent external instantiation
        }

        /**
         * Set the path
         * 
         * @param path
         *            the path
         * @return {@link Builder} instance for fluent programming
         */
        public Builder path(final String path)
        {
            this.path = path;
            return this;
        }

        /**
         * Set the line
         * 
         * @param line
         *            the line
         * @return {@link Builder} instance for fluent programming
         */
        public Builder line(final int line)
        {
            this.line = line;
            return this;
        }

        /**
         * Set the column
         * 
         * @param column
         *            the column
         * @return {@link Builder} instance for fluent programming
         */
        public Builder column(final int column)
        {
            this.column = column;
            return this;
        }

        /**
         * Build a {@link Location} instance
         * 
         * @return the location
         */
        public Location build()
        {
            return new Location(this);
        }

        /**
         * Is the location complete enough to be useful?
         * 
         * @return <code>true</code> if the location is complete enough
         */
        public boolean isCompleteEnough()
        {
            return this.path != null && !this.path.isEmpty();
        }
    }
}