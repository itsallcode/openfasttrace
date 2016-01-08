package openfasttrack.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Generated;

/**
 * Specification item ID
 *
 * Consists of an artifact type (e.g. "test"), a name and a revision number.
 */
public class SpecificationItemId
{
    public static final String ARTIFACT_TYPE_SEPARATOR = "~";
    public static final String REVISION_SEPARATOR = "~";
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
    @Generated(value = "Eclipse")
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
    @Generated(value = "Eclipse")
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

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder(this.artifactType)
                .append(ARTIFACT_TYPE_SEPARATOR).append(this.name).append(REVISION_SEPARATOR)
                .append(this.revision);
        return builder.toString();
    }

    /**
     * Create a new {@link Builder}
     */
    public static class Builder
    {
        private final Pattern idPattern = Pattern
                .compile("(\\p{Alpha}+)~(\\p{Alpha}\\w*(?:\\.\\p{Alpha}\\w*)*)~(\\d+)");
        private final String id;
        private String artifactType;
        private String name;
        private int revision;

        /**
         * Create a new {@link Builder} and initialize it with the string
         * representation of a specification item ID
         *
         * @param id
         *            the string representation of a specification item ID
         */
        public Builder(final String id)
        {
            this.id = id;
        }

        public Builder()
        {
            this(null);
        }

        /**
         * Set the artifact type
         *
         * @param artifactType
         *            the artifact type
         * @return this builder
         */
        public Builder artifactType(final String artifactType)
        {
            this.artifactType = artifactType;
            return this;
        }

        /**
         * Set the ID name part
         *
         * @param name
         *            the name part of the ID
         * @return this builder
         */
        public Builder name(final String name)
        {
            this.name = name;
            return this;
        }

        /**
         * Set the revision number of the ID
         *
         * @param revision
         *            the revision number
         *
         * @return this builder
         */
        public Builder revision(final int revision)
        {
            this.revision = revision;
            return this;
        }

        /**
         * Build a specification item ID
         *
         * @return the ID
         */
        public SpecificationItemId build()
        {
            if (this.id == null)
            {
                validateFields();
            } else
            {
                parseId();
            }

            return new SpecificationItemId(this.name, this.artifactType, this.revision);
        }

        private void validateFields()
        {
            if (this.name == null || this.artifactType == null)
            {
                throw new IllegalStateException("Name or artifactType is missing");
            }
        }

        private void parseId()
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
        }
    }

    /**
     * Parse a string for a specification item ID
     *
     * @param idText
     *            the string to be parsed
     * @return the specification item ID
     */
    public static SpecificationItemId parseId(final String idText)
    {
        return new Builder(idText).build();
    }
}
