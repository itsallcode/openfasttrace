package openfasttrack.core;

import java.util.ArrayList;
import java.util.List;

public class Tracer
{
    private final List<TraceEntry> traceItems = new ArrayList<>();
    private final SpecificationItemCollection items;

    public Tracer(final SpecificationItemCollection items)
    {
        this.items = items;
    }
}
