package openfasttrack.core;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Generated;

/**
 * Specification item ID
 *
 * Consists of an artifact type (e.g. "test"), a name and a revision number.
 */
// [impl->dsn~specification-item-id~1]
public class SpecificationItemId implements Comparable<SpecificationItemId>
{
    public static final String ARTIFACT_TYPE_SEPARATOR = "~";
    public static final String REVISION_SEPARATOR = "~";
    public static final int REVISION_WILDCARD = Integer.MIN_VALUE;
    // [impl->dsn~md.specification-item-id-format~2]
    private static final String ID = "(\\p{Alpha}+)" //
            + ARTIFACT_TYPE_SEPARATOR //
            + "(\\p{Alpha}[\\w-]*(?:\\.\\p{Alpha}[\\w-]*)*)" //
            + REVISION_SEPARATOR //
            + "(\\d+)";
    private static final String LEGACY_ID = "(\\p{Alpha}+)" //
            + "\\." //
            + "(\\p{Alpha}[\\w-]*(?:\\.\\p{Alpha}[\\w-]*)*)" //
            + ", *v" //
            + "(\\d+)";

    public static final Pattern ID_PATTERN = Pattern.compile(ID);
    public static final Pattern LEGACY_ID_PATTERN = Pattern.compile(LEGACY_ID);

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
    public final int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.artifactType == null) ? 0 : this.artifactType.hashCode());
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        result = prime * result + this.revision;
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
        if (!(obj instanceof SpecificationItemId))
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
        }
        else if (!this.artifactType.equals(other.artifactType))
        {
            return false;
        }
        if (this.name == null)
        {
            if (other.name != null)
            {
                return false;
            }
        }
        else if (!this.name.equals(other.name))
        {
            return false;
        }
        if ((other.revision != REVISION_WILDCARD) && (this.revision != other.revision))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder() //
                .append(this.artifactType) //
                .append(ARTIFACT_TYPE_SEPARATOR) //
                .append(this.name) //
                .append(REVISION_SEPARATOR) //
                .append(this.revision);
        return builder.toString();
    }

    public SpecificationItemId toRevisionWildcard()
    {
        return new Builder().artifactType(this.artifactType).name(this.name).revisionWildcard()
                .build();
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

    /**
     * Create a specification item ID
     *
     * @param artifactType
     *            the artifact type
     * @param name
     *            the specification item name
     * @param revision
     *            the revision
     * @return the specification item ID
     */
    public static SpecificationItemId createId(final String artifactType, final String name,
            final int revision)
    {
        return new SpecificationItemId.Builder().artifactType(artifactType).name(name)
                .revision(revision).build();
    }

    /**
     * Create a specification item ID with a revision wildcard
     *
     * @param artifactType
     *            the artifact type
     * @param name
     *            the specification item name
     * @return the specification item ID
     */
    public static SpecificationItemId createId(final String artifactType, final String name)
    {
        return new SpecificationItemId.Builder().artifactType(artifactType).name(name)
                .revisionWildcard().build();
    }

    /**
     * Create a new {@link Builder}
     */
    public static class Builder
    {
        // [impl->dsn~md.specification_item_id_format~2]
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
         * Turn the ID into an ID with a revision wildcard.
         *
         * @return this builder
         */
        public Builder revisionWildcard()
        {
            this.revision = SpecificationItemId.REVISION_WILDCARD;
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
            }
            else
            {
                parseId();
            }

            return new SpecificationItemId(this.name, this.artifactType, this.revision);
        }

        private void validateFields()
        {
            if (this.name == null)
            {
                throw new IllegalStateException("Name is missing");
            }
        }

        private void parseId()
        {
            final Matcher matcher = ID_PATTERN.matcher(this.id);
            if (matcher.matches())
            {
                extractIdPartsFromMatcher(matcher);
            }
            else
            {
                final Matcher legacyMatcher = LEGACY_ID_PATTERN.matcher(this.id);
                if (legacyMatcher.matches())
                {
                    extractIdPartsFromMatcher(legacyMatcher);
                }
                else
                {
                    throw new IllegalStateException("String \"" + this.id
                            + "\" cannot be parsed to a specification item ID");
                }
            }
        }

        private void extractIdPartsFromMatcher(final Matcher matcher)
        {
            this.artifactType = matcher.group(1);
            this.name = matcher.group(2);
            try
            {
                this.revision = Integer.parseInt(matcher.group(3));
            }
            catch (final NumberFormatException exception)
            {
                throw new IllegalArgumentException(
                        "Error parsing version number from specification item ID: \"" + this.id
                                + "\"",
                        exception);
            }
        }
    }

    @Override
    public int compareTo(final SpecificationItemId other)
    {
        int compared = this.getArtifactType().compareTo(other.getArtifactType());
        if (compared == 0)
        {
            compared = this.getName().compareTo(other.getName());
            if (compared == 0)
            {
                if (this.getRevision() > other.getRevision())
                {
                    compared = 1;
                }
                else if (this.getRevision() < other.getRevision())
                {
                    compared = -1;
                }
            }
        }
        return compared;
    }
}
