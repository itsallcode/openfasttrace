package openfasttrack.core;

import java.util.List;
import java.util.stream.Collectors;

public class Tracer
{
    public Trace trace(final List<LinkedSpecificationItem> items)
    {
        final Trace.Builder builder = new Trace.Builder();
        builder.items(items);
        builder.uncleanItems(items.stream() //
                .filter(item -> !item.isCoveredDeeply()) //
                .collect(Collectors.toList()));
        return builder.build();
    }
}
