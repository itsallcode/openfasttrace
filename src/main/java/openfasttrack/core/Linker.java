package openfasttrack.core;

import java.util.List;
import java.util.stream.Collectors;

public class Linker
{

    private final List<SpecificationItem> items;

    /**
     * Create a {@link Linker} for specification items.
     *
     * @param items
     *            the specification items to be linked.
     */
    public Linker(final List<SpecificationItem> items)
    {
        this.items = items;
    }

    /**
     * Turn the items into linked items.
     *
     * @return a list of {@link LinkedSpecificationItem}s.
     */
    public List<LinkedSpecificationItem> link()
    {
        final List<LinkedSpecificationItem> linkedItems = this.items.stream().map(item -> {
            return new LinkedSpecificationItem(item);
        }).collect(Collectors.toList());
        return linkedItems;
    }
}
