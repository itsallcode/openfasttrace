package openfasttrack.core;

public class Link
{

    private final SpecificationItemId from;
    private final SpecificationItemId to;
    private final LinkStatus status;

    public Link(final SpecificationItemId from, final SpecificationItemId to,
            final LinkStatus status)
    {
        this.from = from;
        this.to = to;
        this.status = status;
    }

    public SpecificationItemId getFrom()
    {
        return this.from;
    }

    public SpecificationItemId getTo()
    {
        return this.to;
    }

    public LinkStatus getStatus()
    {
        return this.status;
    }
}
