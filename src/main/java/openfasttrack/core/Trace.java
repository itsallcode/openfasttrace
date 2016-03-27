package openfasttrack.core;

import java.util.List;
import java.util.stream.Collectors;

public class Trace
{
    private final List<LinkedSpecificationItem> items;
    private final List<LinkedSpecificationItem> uncoveredItems;

    private Trace(final List<LinkedSpecificationItem> items,
            final List<LinkedSpecificationItem> uncleanItems)
    {
        this.items = items;
        this.uncoveredItems = uncleanItems;
    }

    public boolean isAllCovered()
    {
        return this.uncoveredItems.isEmpty();
    }

    public List<LinkedSpecificationItem> getUncoveredItems()
    {
        return this.uncoveredItems;
    }

    public List<LinkedSpecificationItem> getItems()
    {
        return this.items;
    }

    public List<SpecificationItemId> getUncoveredIds()
    {
        return this.uncoveredItems.stream() //
                .map(LinkedSpecificationItem::getId) //
                .collect(Collectors.toList());
    }

    public int countUncovered()
    {
        return this.uncoveredItems.size();
    }

    public int count()
    {
        return this.items.size();
    }

    public static class Builder
    {
        private List<LinkedSpecificationItem> items;
        private List<LinkedSpecificationItem> uncleanItems;

        public Builder items(final List<LinkedSpecificationItem> items)
        {
            this.items = items;
            return this;
        }

        public Builder uncleanItems(final List<LinkedSpecificationItem> uncleanItems)
        {
            this.uncleanItems = uncleanItems;
            return this;
        }

        public Trace build()
        {
            return new Trace(this.items, this.uncleanItems);
        }
    }
}
