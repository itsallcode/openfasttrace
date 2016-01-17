package openfasttrack.core;

import java.util.LinkedList;
import java.util.List;

public class Tracer
{
    List<Link> links = new LinkedList<Link>();
    private final SpecificationItemCollection items;

    public Tracer(final SpecificationItemCollection items)
    {
        this.items = items;
    }

    public Trace trace()
    {
        for (final SpecificationItem item : this.items)
        {
            checkCoveredLinks(item);
        }
        return new Trace(this.links);
    }

    private void checkCoveredLinks(final SpecificationItem investigatedItem)
    {
        for (final SpecificationItemId coveredId : investigatedItem.getCoveredIds())
        {
            final LinkStatus status = evaluateLinkStatus(investigatedItem, coveredId);
            this.links.add(new Link(investigatedItem.getId(), coveredId, status));
        }
    }

    private LinkStatus evaluateLinkStatus(final SpecificationItem investigatedItem,
            final SpecificationItemId coveredId)
    {
        LinkStatus status = LinkStatus.BROKEN;
        if (existsItemWithId(coveredId))
        {
            status = evaluateLinkStatusOfDirectMatch(investigatedItem, coveredId);
        }
        else
        {
            final String coveredArtifactType = coveredId.getArtifactType();
            final String coveredName = coveredId.getName();
            final int coveredRevision = coveredId.getRevision();
            if (existsItemWithArtifactTypeAndName(coveredArtifactType, coveredName))
            {
                status = evaluateLinkStatusOfMatchWithoutRevision(investigatedItem,
                        coveredArtifactType, coveredName, coveredRevision);
            }
            else
            {
                status = LinkStatus.BROKEN;
            }
        }
        return status;
    }

    private boolean existsItemWithId(final SpecificationItemId coveredId)
    {
        return this.items.containsKey(coveredId);
    }

    private LinkStatus evaluateLinkStatusOfDirectMatch(final SpecificationItem investigatedItem,
            final SpecificationItemId coveredId)
    {
        LinkStatus status;
        final List<SpecificationItem> machtes = this.items.getAll(coveredId);
        if (machtes.size() == 1)
        {
            if (this.items.getFirst(coveredId)
                    .needsCoverageByArtifactType(investigatedItem.getId().getArtifactType()))
            {
                status = LinkStatus.OK;
            }
            else
            {
                status = LinkStatus.UNWANTED;
            }
        }
        else
        {
            status = LinkStatus.AMBIGUOUS;
        }
        return status;
    }

    private boolean existsItemWithArtifactTypeAndName(final String artifactType, final String name)
    {
        return this.items.containsKey(artifactType, name);
    }

    private LinkStatus evaluateLinkStatusOfMatchWithoutRevision(
            final SpecificationItem investigatedItem, final String coveredArtifactType,
            final String coveredName, final int coveredRevision)
    {
        LinkStatus status;
        final List<SpecificationItem> fuzzyMatches = this.items.getAll(coveredArtifactType,
                coveredName);
        if (fuzzyMatches.size() == 1)
        {
            if (this.items.getFirst(coveredArtifactType, coveredName).getId()
                    .getRevision() < coveredRevision)
            {
                status = LinkStatus.PREDATED;
            }
            else
            {
                status = LinkStatus.OUTDATED;
            }
        }
        else
        {
            status = LinkStatus.AMBIGUOUS;
        }
        return status;
    }
}
