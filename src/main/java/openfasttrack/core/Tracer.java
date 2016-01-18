package openfasttrack.core;

import java.util.ArrayList;
import java.util.List;

public class Tracer
{
    List<TraceItem> traceItems = new ArrayList<>();
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
        return new Trace(this.traceItems);
    }

    private void checkCoveredLinks(final SpecificationItem investigatedItem)
    {
        // for (final String artifactType :
        // investigatedItem.getNeededArtifactTypes())
        // {
        // final ForwardLinkStatus status =
        // evalForwardCoverage(investigatedItem, artifactType);
        // this.forwareLinks.add(new ForwardLink(investigatedItem.getId(),
        // artifactType, status));
        // }
        final TraceItem.Builder traceItemBuilder = new TraceItem.Builder(investigatedItem);
        for (final SpecificationItemId coveredId : investigatedItem.getCoveredIds())
        {
            final BackwardLinkStatus status = evalBackwardCoverage(investigatedItem, coveredId);
            traceItemBuilder.addBackwardLink(coveredId, status);
        }
        this.traceItems.add(traceItemBuilder.build());
    }

    // private ForwardLinkStatus evalForwardCoverage(final SpecificationItem
    // investigatedItem,
    // final String artifactType)
    // {
    // // TODO Auto-generated method stub
    // return null;
    // }

    // [impl~backward_coverage_status~1]
    private BackwardLinkStatus evalBackwardCoverage(final SpecificationItem investigatedItem,
            final SpecificationItemId coveredId)
    {
        BackwardLinkStatus status = BackwardLinkStatus.ORPHANED;
        if (existsItemWithId(coveredId))
        {
            status = evalBackwardCoverageOfDirectMatch(investigatedItem, coveredId);
        }
        else
        {
            final String coveredArtifactType = coveredId.getArtifactType();
            final String coveredName = coveredId.getName();
            final int coveredRevision = coveredId.getRevision();
            if (existsItemWithArtifactTypeAndName(coveredArtifactType, coveredName))
            {
                status = evalBackwardCoverageOfMatchWithoutRevision(investigatedItem,
                        coveredArtifactType, coveredName, coveredRevision);
            }
            else
            {
                status = BackwardLinkStatus.ORPHANED;
            }
        }
        return status;
    }

    private boolean existsItemWithId(final SpecificationItemId coveredId)
    {
        return this.items.containsKey(coveredId);
    }

    private BackwardLinkStatus evalBackwardCoverageOfDirectMatch(
            final SpecificationItem investigatedItem, final SpecificationItemId coveredId)
    {
        BackwardLinkStatus status;
        final List<SpecificationItem> machtes = this.items.getAll(coveredId);
        if (machtes.size() == 1)
        {
            if (this.items.getFirst(coveredId)
                    .needsCoverageByArtifactType(investigatedItem.getId().getArtifactType()))
            {
                status = BackwardLinkStatus.OK;
            }
            else
            {
                status = BackwardLinkStatus.UNWANTED;
            }
        }
        else
        {
            status = BackwardLinkStatus.AMBIGUOUS;
        }
        return status;
    }

    private boolean existsItemWithArtifactTypeAndName(final String artifactType, final String name)
    {
        return this.items.containsKey(artifactType, name);
    }

    private BackwardLinkStatus evalBackwardCoverageOfMatchWithoutRevision(
            final SpecificationItem investigatedItem, final String coveredArtifactType,
            final String coveredName, final int coveredRevision)
    {
        BackwardLinkStatus status;
        final List<SpecificationItem> fuzzyMatches = this.items.getAll(coveredArtifactType,
                coveredName);
        if (fuzzyMatches.size() == 1)
        {
            if (this.items.getFirst(coveredArtifactType, coveredName).getId()
                    .getRevision() < coveredRevision)
            {
                status = BackwardLinkStatus.PREDATED;
            }
            else
            {
                status = BackwardLinkStatus.OUTDATED;
            }
        }
        else
        {
            status = BackwardLinkStatus.AMBIGUOUS;
        }
        return status;
    }
}
