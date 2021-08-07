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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.processing.Generated;

/**
 * A specification item that requires coverage from other items and provides
 * coverage for other items.
 */
// [impl->dsn~specification-item~3]
public class SpecificationItem
{
    private final SpecificationItemId id;
    private final String title;
    private final String description;
    private final String rationale;
    private final String comment;
    private final Location location;
    private final ItemStatus status;
    private final List<SpecificationItemId> coveredIds;
    private final List<SpecificationItemId> dependOnIds;
    private final List<String> needsArtifactTypes;
    private final List<String> tags;
    private final boolean forwards;

    private SpecificationItem(final Builder builder)
    {
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.rationale = builder.rationale;
        this.comment = builder.comment;
        this.location = builder.location;
        this.status = builder.status;
        this.coveredIds = builder.coveredIds;
        this.dependOnIds = builder.dependOnIds;
        this.needsArtifactTypes = builder.neededArtifactTypes;
        this.tags = builder.tags;
        this.forwards = builder.forwards;
    }

    /**
     * Get the ID of the specification item
     *
     * @return ID
     */
    public SpecificationItemId getId()
    {
        return this.id;
    }

    /**
     * Get the artifact type of the specification item
     * 
     * @return artifact type
     */
    public String getArtifactType()
    {
        return this.id.getArtifactType();
    }

    /**
     * Get the name part of the specification item ID
     * 
     * <p>
     * Not to be mixed up with the
     * {@link org.itsallcode.openfasttrace.api.core.SpecificationItem#getTitle()}
     * of the specification item
     * </p>
     * 
     * @return name part of the specification item ID
     */
    public String getName()
    {
        return this.id.getName();
    }

    /**
     * Get the revision of the specification item
     * 
     * @return revision
     */
    public int getRevision()
    {
        return this.id.getRevision();
    }

    /**
     * Get the title of the specification item
     *
     * @return title
     */
    public String getTitle()
    {
        return this.title;
    }

    /**
     * Get the description of the specification item
     *
     * @return description
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * Get the rationale of the specification item
     *
     * @return rationale
     */
    public String getRationale()
    {
        return this.rationale;
    }

    /**
     * Get the comment of the specification item
     *
     * @return comment
     */
    public String getComment()
    {
        return this.comment;
    }

    /**
     * Get the list of covered {@link SpecificationItemId}s
     *
     * @return list of covered IDs
     */
    public List<SpecificationItemId> getCoveredIds()
    {
        return this.coveredIds;
    }

    /**
     * Get the list of {@link SpecificationItemId}s this item depends on
     *
     * @return list of IDs this item depends on
     */
    public List<SpecificationItemId> getDependOnIds()
    {
        return this.dependOnIds;
    }

    /**
     * Get the list of artifact types this specification item need to be covered
     * in
     *
     * @return list of artifact types
     */
    public List<String> getNeedsArtifactTypes()
    {
        return this.needsArtifactTypes;
    }

    /**
     * Check if this specification item needs to be covered by the given
     * artifact type.
     *
     * @param artifactType
     *            the artifact type for which needed coverage is evaluated.
     * @return <code>true</code> if this item needs to be covered by the given
     *         artifact type.
     */
    public boolean needsCoverageByArtifactType(final String artifactType)
    {
        return this.needsArtifactTypes.contains(artifactType);
    }

    /**
     * Check if the item needs any coverage
     *
     * @return <code>true</code> if the item needs coverage
     */
    public boolean needsCoverage()
    {
        return !this.needsArtifactTypes.isEmpty();
    }

    /**
     * Get the location where this specification item was defined.
     * 
     * @return location of this item.
     */
    public Location getLocation()
    {
        return this.location;
    }

    /**
     * Get the maturity status of the specification item
     * 
     * @return maturity status
     */
    public ItemStatus getStatus()
    {
        return this.status;
    }

    /**
     * Get the list of associated tags
     * 
     * @return tags
     */
    public List<String> getTags()
    {
        return this.tags;
    }

    /**
     * @return <code>true</code> if this specification item forwards needed
     *         coverage
     */
    public boolean isForwarding()
    {
        return this.forwards;
    }

    @Generated(value = "org.eclipse.Eclipse")
    @Override
    public final int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.comment == null) ? 0 : this.comment.hashCode());
        result = prime * result + ((this.coveredIds == null) ? 0 : this.coveredIds.hashCode());
        result = prime * result + ((this.dependOnIds == null) ? 0 : this.dependOnIds.hashCode());
        result = prime * result + ((this.description == null) ? 0 : this.description.hashCode());
        result = prime * result + (this.forwards ? 1231 : 1237);
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.location == null) ? 0 : this.location.hashCode());
        result = prime * result
                + ((this.needsArtifactTypes == null) ? 0 : this.needsArtifactTypes.hashCode());
        result = prime * result + ((this.rationale == null) ? 0 : this.rationale.hashCode());
        result = prime * result + ((this.status == null) ? 0 : this.status.hashCode());
        result = prime * result + ((this.tags == null) ? 0 : this.tags.hashCode());
        result = prime * result + ((this.title == null) ? 0 : this.title.hashCode());
        return result;
    }

    @Generated(value = "org.eclipse.Eclipse")
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
        if (!(obj instanceof SpecificationItem))
        {
            return false;
        }
        final SpecificationItem other = (SpecificationItem) obj;
        if (this.comment == null)
        {
            if (other.comment != null)
            {
                return false;
            }
        }
        else if (!this.comment.equals(other.comment))
        {
            return false;
        }
        if (this.coveredIds == null)
        {
            if (other.coveredIds != null)
            {
                return false;
            }
        }
        else if (!this.coveredIds.equals(other.coveredIds))
        {
            return false;
        }
        if (this.dependOnIds == null)
        {
            if (other.dependOnIds != null)
            {
                return false;
            }
        }
        else if (!this.dependOnIds.equals(other.dependOnIds))
        {
            return false;
        }
        if (this.description == null)
        {
            if (other.description != null)
            {
                return false;
            }
        }
        else if (!this.description.equals(other.description))
        {
            return false;
        }
        if (this.forwards != other.forwards)
        {
            return false;
        }
        if (this.id == null)
        {
            if (other.id != null)
            {
                return false;
            }
        }
        else if (!this.id.equals(other.id))
        {
            return false;
        }
        if (this.location == null)
        {
            if (other.location != null)
            {
                return false;
            }
        }
        else if (!this.location.equals(other.location))
        {
            return false;
        }
        if (this.needsArtifactTypes == null)
        {
            if (other.needsArtifactTypes != null)
            {
                return false;
            }
        }
        else if (!this.needsArtifactTypes.equals(other.needsArtifactTypes))
        {
            return false;
        }
        if (this.rationale == null)
        {
            if (other.rationale != null)
            {
                return false;
            }
        }
        else if (!this.rationale.equals(other.rationale))
        {
            return false;
        }
        if (this.status != other.status)
        {
            return false;
        }
        if (this.tags == null)
        {
            if (other.tags != null)
            {
                return false;
            }
        }
        else if (!this.tags.equals(other.tags))
        {
            return false;
        }
        if (this.title == null)
        {
            if (other.title != null)
            {
                return false;
            }
        }
        else if (!this.title.equals(other.title))
        {
            return false;
        }
        return true;
    }

    /**
     * Create a builder for specification items
     * 
     * @return new {@link Builder} instance
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder for objects of type {@link SpecificationItem}
     */
    public static class Builder
    {
        private SpecificationItemId id;
        private String title;
        private String description;
        private String rationale;
        private String comment;
        private ItemStatus status;
        private Location location;
        private final List<SpecificationItemId> coveredIds;
        private final List<SpecificationItemId> dependOnIds;
        private final List<String> neededArtifactTypes;
        private final List<String> tags;
        private boolean forwards;

        /**
         * Create a new instance of type {@link SpecificationItem.Builder}
         */
        private Builder()
        {
            this.id = null;
            this.title = "";
            this.description = "";
            this.rationale = "";
            this.comment = "";
            this.location = null;
            this.status = ItemStatus.APPROVED;
            this.coveredIds = new ArrayList<>();
            this.dependOnIds = new ArrayList<>();
            this.neededArtifactTypes = new ArrayList<>();
            this.tags = new ArrayList<>();
        }

        /**
         * Set the specification item ID
         *
         * @param id
         *            the ID
         * @return this builder instance
         */
        public Builder id(final SpecificationItemId id)
        {
            this.id = id;
            return this;
        }

        /**
         * Set the specification item ID by its parts
         *
         * @param artifactType
         *            the artifact type
         * @param name
         *            the artifact name
         * @param revision
         *            the revision number
         * @return this builder instance
         */
        public Builder id(final String artifactType, final String name, final int revision)
        {
            this.id = new SpecificationItemId.Builder() //
                    .artifactType(artifactType).name(name).revision(revision) //
                    .build();
            return this;
        }

        /**
         * Set the title
         *
         * @param title
         *            the title
         * @return this builder instance
         */
        public Builder title(final String title)
        {
            this.title = title;
            return this;
        }

        /**
         * Set the description
         *
         * @param description
         *            the description
         * @return this builder instance
         */
        public Builder description(final String description)
        {
            this.description = description;
            return this;
        }

        /**
         * Set the rationale
         *
         * @param rationale
         *            the rationale
         * @return this builder instance
         */
        public Builder rationale(final String rationale)
        {
            this.rationale = rationale;
            return this;
        }

        /**
         * Set the comment
         *
         * @param comment
         *            the comment
         * @return this builder instance
         */
        public Builder comment(final String comment)
        {
            this.comment = comment;
            return this;
        }

        /**
         * Set the status
         * 
         * @param status
         *            the status
         * @return this builder instance
         */
        public Builder status(final ItemStatus status)
        {
            this.status = status;
            return this;
        }

        /**
         * Add the ID of a specification item covered by the item to build
         *
         * @param coveredId
         *            the covered ID
         * @return this builder instance
         */
        public Builder addCoveredId(final SpecificationItemId coveredId)
        {
            this.coveredIds.add(coveredId);
            return this;
        }

        /**
         * Add the ID of a specification item covered by the item to build
         *
         * @param artifactType
         *            the artifact type of the covered item
         * @param name
         *            the artifact name of the covered item
         * @param revision
         *            the revision number of the covered item
         * @return this builder instance
         */
        public Builder addCoveredId(final String artifactType, final String name,
                final int revision)
        {
            this.coveredIds.add(new SpecificationItemId.Builder() //
                    .artifactType(artifactType).name(name).revision(revision) //
                    .build());
            return this;
        }

        /**
         * Add the ID of a specification item the item to be build depends on
         *
         * @param dependOnId
         *            the ID the item to be build depends on
         * @return this builder instance
         */
        public Builder addDependOnId(final SpecificationItemId dependOnId)
        {
            this.dependOnIds.add(dependOnId);
            return this;
        }

        /**
         * Add the ID of a specification item the item to be build depends on
         *
         * @param artifactType
         *            the artifact type of item to be build depends on
         * @param name
         *            the artifact name of item to be build depends on
         * @param revision
         *            the revision number of item to be build depends on
         * @return this builder instance
         */
        public Builder addDependOnId(final String artifactType, final String name,
                final int revision)
        {
            this.dependOnIds.add(new SpecificationItemId.Builder() //
                    .artifactType(artifactType).name(name).revision(revision) //
                    .build());
            return this;
        }

        /**
         * Add an artifact type where the specification item to be build
         * requires to be covered
         *
         * @param neededArtifactType
         *            the artifact type
         * @return this builder instance
         */
        public Builder addNeedsArtifactType(final String neededArtifactType)
        {
            this.neededArtifactTypes.add(neededArtifactType);
            return this;
        }

        /**
         * Add a tag
         *
         * @param tag
         *            the tag
         * @return this builder instance
         */
        public Builder addTag(final String tag)
        {
            this.tags.add(tag);
            return this;
        }

        /**
         * Set the location
         *
         * @param location
         *            the location
         * @return this builder instance
         */
        public Builder location(final Location location)
        {
            this.location = location;
            return this;
        }

        /**
         * Set the location
         *
         * @param path
         *            the path of the location
         * @param line
         *            the line of the location
         * @return this builder instance
         */
        public Builder location(final String path, final Integer line)
        {
            Objects.requireNonNull(path, "path");
            Objects.requireNonNull(line, "line");
            return this.location(Location.create(path, line));
        }

        /**
         * Build a new instance of type {@link SpecificationItem}
         *
         * @return the specification item
         */
        public SpecificationItem build()
        {
            if (this.id == null)
            {
                throw new IllegalStateException("No id given");
            }
            return new SpecificationItem(this);
        }

        /**
         * Set to <code>true</code> if this specification item forwards needed
         * coverage
         *
         * @param forwards
         *            <code>true</code> if the specification item forwards
         *            needed coverage
         * @return this builder instance
         */
        public Builder forwards(final boolean forwards)
        {
            this.forwards = forwards;
            return this;
        }
    }
}