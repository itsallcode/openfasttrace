package openfasttrack.core;

public class ForwardLink
{

    private final SpecificationItem from;
    private final String toArtifactType;
    private final ForwardLinkStatus status;

    public ForwardLink(final SpecificationItem item, final String toArtifactType,
            final ForwardLinkStatus status)
    {
        this.from = item;
        this.toArtifactType = toArtifactType;
        this.status = status;
    }

    public SpecificationItem getFrom()
    {
        return this.from;
    }

    public String getToArtifactType()
    {
        return this.toArtifactType;
    }

    public ForwardLinkStatus getStatus()
    {
        return this.status;
    }
}
