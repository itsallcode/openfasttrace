package openfasttrack.core;

public class NeedsCoverageLink
{

    private final SpecificationItem from;
    private final String toArtifactType;
    private final NeedsCoverageLinkStatus status;

    public NeedsCoverageLink(final SpecificationItem item, final String toArtifactType,
            final NeedsCoverageLinkStatus status)
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

    public NeedsCoverageLinkStatus getStatus()
    {
        return this.status;
    }

    public boolean isClean()
    {
        return this.status == NeedsCoverageLinkStatus.OK;
    }
}