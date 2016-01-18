package openfasttrack.core;

import java.util.List;
import java.util.stream.Collectors;

public class Trace
{
    private final List<TraceItem> traceItems;

    public Trace(final List<TraceItem> traceItems)
    {
        this.traceItems = traceItems;
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
}