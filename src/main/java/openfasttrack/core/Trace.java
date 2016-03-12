package openfasttrack.core;

import java.util.Collection;
import java.util.stream.Collectors;

public class Trace
{
    private final Collection<TraceEntry> traceItems;

    public Trace(final Collection<TraceEntry> collection)
    {
        this.traceItems = collection;
    }

    public int countAllLinks()
    {
        return this.traceItems.parallelStream()
                .collect(Collectors.summingInt(traceItem -> traceItem.getBackwardLinks().size()));
    }

    public long countBackwardLinksWithStatus(final BackwardLinkStatus status)
    {
        return this.traceItems.parallelStream() //
                .mapToLong(traceItem -> traceItem //
                        .getBackwardLinks() //
                        .stream() //
                        .filter(link -> (link.getStatus() == status)) //
                        .count()) //
                .sum();
    }

    public long countNeedsCoverageLinksWithStatus(final NeedsCoverageLinkStatus status)
    {
        return this.traceItems.parallelStream() //
                .mapToLong(traceItem -> traceItem //
                        .getNeedsCoverageLinks() //
                        .stream() //
                        .filter(link -> (link.getStatus() == status)) //
                        .count()) //
                .sum();
    }
}