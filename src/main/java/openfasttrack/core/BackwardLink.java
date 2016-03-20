package openfasttrack.core;

public class BackwardLink
{

    private final SpecificationItem from;
    private final SpecificationItemId toId;
    private final BackwardLinkStatus status;

    public BackwardLink(final SpecificationItem from, final SpecificationItemId toId,
            final BackwardLinkStatus status)
    {
        this.from = from;
        this.toId = toId;
        this.status = status;
    }

    public SpecificationItem getFrom()
    {
        return this.from;
    }

    public SpecificationItemId getToId()
    {
        return this.toId;
    }

    public BackwardLinkStatus getStatus()
    {
        return this.status;
    }

    public boolean isClean()
    {
        return this.status == BackwardLinkStatus.OK;
    }
}