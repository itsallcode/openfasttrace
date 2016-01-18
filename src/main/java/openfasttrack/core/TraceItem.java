package openfasttrack.core;

import java.util.ArrayList;
import java.util.List;

public class TraceItem
{

    private final List<ForwardLink> forwardLinks;
    private final List<BackwardLink> backwardLinks;
    private final List<SpecificationItem> duplicates;

    public TraceItem(final List<ForwardLink> forwardLinks, final List<BackwardLink> backwardLinks,
            final List<SpecificationItem> duplicates)
    {
        this.forwardLinks = forwardLinks;
        this.backwardLinks = backwardLinks;
        this.duplicates = duplicates;
    }

    public List<ForwardLink> getForwardLinks()
    {
        return this.forwardLinks;
    }

    public List<BackwardLink> getBackwardLinks()
    {
        return this.backwardLinks;
    }

    public List<SpecificationItem> getDuplicates()
    {
        return this.duplicates;
    }

    public static class Builder
    {
        List<ForwardLink> forwardLinks = new ArrayList<>();
        List<BackwardLink> backwardLinks = new ArrayList<>();
        List<SpecificationItem> duplicates = new ArrayList<>();

        private final SpecificationItem item;

        public Builder(final SpecificationItem item)
        {
            this.item = item;
        }

        public Builder addForwardLink(final String toArtifactType, final ForwardLinkStatus status)
        {
            this.forwardLinks.add(new ForwardLink(this.item, toArtifactType, status));
            return this;
        }

        public Builder addBackwardLink(final SpecificationItemId toId,
                final BackwardLinkStatus status)
        {
            this.backwardLinks.add(new BackwardLink(this.item, toId, status));
            return this;
        }

        public Builder addDuplicate(final SpecificationItem duplicate)
        {
            this.duplicates.add(duplicate);
            return this;
        }

        public TraceItem build()
        {
            return new TraceItem(this.forwardLinks, this.backwardLinks, this.duplicates);
        }
    }
}
