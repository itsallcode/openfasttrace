package openfasttrack.core;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

// [impl~requirement_format~1]
public class SpecificationItem
{
    private final SpecificationItemId id;
    private final String title;
    private final String description;
    private final String rationale;
    private final String comment;
    private final List<SpecificationItemId> coveredIds;
    private final List<SpecificationItemId> dependOnIds;
    private final List<String> needsArtifactTypes;

    private SpecificationItem(final SpecificationItemId id, final String title,
            final String description, final String rationale, final String comment,
            final List<SpecificationItemId> coveredIds, final List<SpecificationItemId> dependOnIds,
            final List<String> neededArtifactTypes)
    {
        this.id = id;
        this.title = title;
        this.description = description;
        this.rationale = rationale;
        this.comment = comment;
        this.coveredIds = coveredIds;
        this.dependOnIds = dependOnIds;
        this.needsArtifactTypes = neededArtifactTypes;
    }

    /**
     * Get the ID of the specification item
     *
     * @return the ID
     */
    public SpecificationItemId getId()
    {
        return this.id;
    }

    /**
     * Get the title of the specification item
     *
     * @return the title
     */
    public String getTitle()
    {
        return this.title;
    }

    /**
     * Get the description of the specification item
     *
     * @return the description
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * Get the rationale of the specification item
     *
     * @return the rationale
     */
    public String getRationale()
    {
        return this.rationale;
    }

    /**
     * Get the comment of the specification item
     *
     * @return the comment
     */
    public String getComment()
    {
        return this.comment;
    }

    /**
     * Get the list of covered {@link SpecificationItemId}s
     *
     * @return the list of covered IDs
     */
    public List<SpecificationItemId> getCoveredIds()
    {
        return this.coveredIds;
    }

    /**
     * Get the list of {@link SpecificationItemId}s this item depends on
     *
     * @return the list of IDs this item depends on
     */
    public List<SpecificationItemId> getDependOnIds()
    {
        return this.dependOnIds;
    }

    /**
     * Get the list of artifact types this specification item need to be covered
     * in
     *
     * @return the list of artifact types
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
        return this.needsArtifactTypes.size() > 0;
    }

    @Generated(value = "Eclipse")
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.comment == null) ? 0 : this.comment.hashCode());
        result = prime * result + ((this.coveredIds == null) ? 0 : this.coveredIds.hashCode());
        result = prime * result + ((this.dependOnIds == null) ? 0 : this.dependOnIds.hashCode());
        result = prime * result + ((this.description == null) ? 0 : this.description.hashCode());
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result
                + ((this.needsArtifactTypes == null) ? 0 : this.needsArtifactTypes.hashCode());
        result = prime * result + ((this.rationale == null) ? 0 : this.rationale.hashCode());
        result = prime * result + ((this.title == null) ? 0 : this.title.hashCode());
        return result;
    }

    @Generated(value = "Eclipse")
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
     * Builder for objects of type {@link SpecificationItem}
     */
    public static class Builder
    {
        private SpecificationItemId id;
        private String title;
        private String description;
        private String rationale;
        private String comment;
        private final List<SpecificationItemId> coveredIds;
        private final List<SpecificationItemId> dependOnIds;
        private final List<String> neededArtifactTypes;

        /**
         * Create a new instance of type {@link SpecificationItem.Builder}
         */
        public Builder()
        {
            this.id = null;
            this.title = "";
            this.description = "";
            this.rationale = "";
            this.comment = "";
            this.coveredIds = new ArrayList<>();
            this.dependOnIds = new ArrayList<>();
            this.neededArtifactTypes = new ArrayList<>();
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
            return new SpecificationItem(this.id, this.title, this.description, this.rationale,
                    this.comment, this.coveredIds, this.dependOnIds, this.neededArtifactTypes);
        }
    }
}