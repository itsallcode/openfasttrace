package openfasttrack.core;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@link TraceEntry} is an entry in a specification coverage trace.
 *
 * Each trace entry relates to a single specification item. All evaluated links
 * and duplicates are calculated from the perspective of this item.
 */
public class TraceEntry
{
    private final SpecificationItem item;
    private final List<NeedsCoverageLink> needsCoverageLinks;
    private final List<BackwardLink> backwardLinks;
    private final List<SpecificationItem> duplicates;

    private TraceEntry(final SpecificationItem item, final List<NeedsCoverageLink> needsCoverageLinks,
            final List<BackwardLink> backwardLinks, final List<SpecificationItem> duplicates)
    {
        this.item = item;
        this.needsCoverageLinks = needsCoverageLinks;
        this.backwardLinks = backwardLinks;
        this.duplicates = duplicates;
    }

    /**
     * Get the {@link SpecificationItem} this trace entry belongs to.
     *
     * @return the corresponding specification item
     */
    public SpecificationItem getItem()
    {
        return this.item;
    }

    /**
     * Get the list of needed coverage links originating at the corresponding
     * specification item.
     *
     * @return the list of links for required coverage
     */
    public List<NeedsCoverageLink> getNeedsCoverageLinks()
    {
        return this.needsCoverageLinks;
    }

    /**
     * Get the list of links that cover other specification items.
     *
     * @return the list of backward coverage links
     */
    public List<BackwardLink> getBackwardLinks()
    {
        return this.backwardLinks;
    }

    /**
     * Get the list of duplicates of the corresponding specification item.
     *
     * @return the list of duplicates
     */
    public List<SpecificationItem> getDuplicates()
    {
        return this.duplicates;
    }

    /**
     * Builder for {@link TraceEntry} objects.
     */
    public static class Builder
    {
        private final SpecificationItem item;
        private final List<NeedsCoverageLink> needsCoverageLinks = new ArrayList<>();
        private final List<BackwardLink> backwardLinks = new ArrayList<>();
        private final List<SpecificationItem> duplicates = new ArrayList<>();

        /**
         * Create a new builder for a {@link TraceEntry}.
         *
         * @param item
         *            the specification item the built entry relates to.
         */
        public Builder(final SpecificationItem item)
        {
            this.item = item;
        }

        /**
         * Add a link that indicates coverage needed by the specification item.
         *
         * @param toArtifactType
         *            the artifact type in which coverage is needed
         *
         * @param status
         *            the link status
         * @return this builder instance
         */
        public Builder addNeedsCoverageLink(final String toArtifactType,
                final NeedsCoverageLinkStatus status)
        {
            this.needsCoverageLinks.add(new NeedsCoverageLink(this.item, toArtifactType, status));
            return this;
        }

        /**
         * Add a backward coverage link.
         *
         * @param toId
         *            the ID the link claims to cover.
         * @param status
         *            the link status
         * @return this builder instance
         */
        public Builder addBackwardLink(final SpecificationItemId toId,
                final BackwardLinkStatus status)
        {
            this.backwardLinks.add(new BackwardLink(this.item, toId, status));
            return this;
        }

        /**
         * Add the reference to a duplicate of this specification item
         *
         * @param duplicate
         *            the reference to the duplicate
         * @return this builder instance
         */
        public Builder addDuplicate(final SpecificationItem duplicate)
        {
            this.duplicates.add(duplicate);
            return this;
        }

        /**
         * Build a new instance of a {@link TraceEntry}
         * 
         * @return the new trace entry.
         */
        public TraceEntry build()
        {
            return new TraceEntry(this.item, this.needsCoverageLinks, this.backwardLinks,
                    this.duplicates);
        }
    }
}