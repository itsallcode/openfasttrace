package openfasttrack.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Specification item ID
 *
 * Consists of an artifact type (e.g. "test"), a name and a revision number.
 */
public class SpecificationItemId
{
    private final String name;
    private final int revision;
    private final String artifactType;

    private SpecificationItemId(final String name, final String artifactType, final int revision)
    {
        this.name = name;
        this.artifactType = artifactType;
        this.revision = revision;
    }

    /**
     * Get the name part of the ID
     *
     * @return the name part of the ID
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Get the revision number
     *
     * @return the revision number
     */
    public int getRevision()
    {
        return this.revision;
    }

    /**
     * Get the artifact type
     *
     * @return the artifact type
     */
    public String getArtifactType()
    {
        return this.artifactType;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.artifactType == null) ? 0 : this.artifactType.hashCode());
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        result = prime * result + this.revision;
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
        final SpecificationItemId other = (SpecificationItemId) obj;
        if (this.artifactType == null)
        {
            if (other.artifactType != null)
            {
                return false;
            }
        } else if (!this.artifactType.equals(other.artifactType))
        {
            return false;
        }
        if (this.name == null)
        {
            if (other.name != null)
            {
                return false;
            }
        } else if (!this.name.equals(other.name))
        {
            return false;
        }
        if (this.revision != other.revision)
        {
            return false;
        }
        return true;
    }

    /**
     * Create a new {@link Builder}
     */
    public static class Builder
    {
        private final Pattern idPattern = Pattern.compile("(\\w+)\\.(\\w+(?:\\.\\w+)*)~(\\d+)");
        private final String id;
        private String artifactType;
        private String name;
        private int revision;

        public Builder(final String id)
        {
            this.id = id;
        }

        /**
         * Build a specification item ID
         *
         * @return the ID
         */
        public SpecificationItemId build()
        {
            final Matcher matcher = this.idPattern.matcher(this.id);
            if (matcher.matches())
            {
                this.artifactType = matcher.group(1);
                this.name = matcher.group(2);
                this.revision = Integer.parseInt(matcher.group(3));
            } else
            {
                throw new IllegalStateException(
                        "Sting \"" + this.id + "\" cannot be parsed to a specification item ID");
            }

            return new SpecificationItemId(this.name, this.artifactType, this.revision);
        }
    }
}
