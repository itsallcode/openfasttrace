package openfasttrack.core;

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
