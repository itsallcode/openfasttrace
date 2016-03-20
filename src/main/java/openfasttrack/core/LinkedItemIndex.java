package openfasttrack.core;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LinkedItemIndex
{
    private final Map<SpecificationItemId, LinkedSpecificationItem> idIndex;
    private final Map<SpecificationItemIdWithoutVersion, List<LinkedSpecificationItem>> idIndexIgnoringVersion;

    private LinkedItemIndex(final Map<SpecificationItemId, LinkedSpecificationItem> idIndex,
            final Map<SpecificationItemIdWithoutVersion, List<LinkedSpecificationItem>> idIndexIgnoringVersion)
    {
        this.idIndex = idIndex;
        this.idIndexIgnoringVersion = idIndexIgnoringVersion;
    }

    public static LinkedItemIndex create(final List<SpecificationItem> items)
    {
        return createFromWrappedItems(wrapItems(items));
    }

    private static List<LinkedSpecificationItem> wrapItems(final List<SpecificationItem> items)
    {
        return items.stream().map(LinkedSpecificationItem::new).collect(Collectors.toList());
    }

    public static LinkedItemIndex createFromWrappedItems(
            final List<LinkedSpecificationItem> wrappedItems)
    {
        return new LinkedItemIndex( //
                createIdIndex(wrappedItems), //
                createIdIndexIgnoringVersion(wrappedItems));

    }

    private static Map<SpecificationItemIdWithoutVersion, List<LinkedSpecificationItem>> createIdIndexIgnoringVersion(
            final List<LinkedSpecificationItem> wrappedItems)
    {
        return wrappedItems.stream()
                .collect(Collectors.groupingBy(SpecificationItemIdWithoutVersion::new));
    }

    private static Map<SpecificationItemId, LinkedSpecificationItem> createIdIndex(
            final List<LinkedSpecificationItem> wrappedItems)
    {
        final Map<SpecificationItemId, LinkedSpecificationItem> index = wrappedItems.stream()
                .collect(Collectors.toMap(LinkedSpecificationItem::getId, //
                        item -> item, //
                        LinkedItemIndex::handleDuplicates));
        return index;
    }

    private static LinkedSpecificationItem handleDuplicates(final LinkedSpecificationItem item1,
            final LinkedSpecificationItem item2)
    {
        item1.addLinkToItemWithStatus(item2, LinkStatus.DUPLICATE);
        item2.addLinkToItemWithStatus(item1, LinkStatus.DUPLICATE);
        return item1;
    }

    public int size()
    {
        return this.idIndex.size();
    }

    public LinkedSpecificationItem getById(final SpecificationItemId id)
    {
        return this.idIndex.get(id);
    }

    public int sizeIgnoringVersion()
    {
        return this.idIndexIgnoringVersion.size();
    }

    public List<LinkedSpecificationItem> getByIdIgnoringVersion(final SpecificationItemId id)
    {
        return this.idIndexIgnoringVersion.get(new SpecificationItemIdWithoutVersion(id));
    }

    private static class SpecificationItemIdWithoutVersion
    {
        private final String name;
        private final String artifcatType;

        public SpecificationItemIdWithoutVersion(final SpecificationItemId id)
        {
            this.name = id.getName();
            this.artifcatType = id.getArtifactType();
        }

        public SpecificationItemIdWithoutVersion(final LinkedSpecificationItem linkedItem)
        {
            this(linkedItem.getId());
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((this.artifcatType == null) ? 0 : this.artifcatType.hashCode());
            result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (obj == null)
            {
                return false;
            }
            if (getClass() != obj.getClass())
            {
                return false;
            }
            final SpecificationItemIdWithoutVersion other = (SpecificationItemIdWithoutVersion) obj;
            if (this.artifcatType == null)
            {
                if (other.artifcatType != null)
                {
                    return false;
                }
            }
            else if (!this.artifcatType.equals(other.artifcatType))
            {
                return false;
            }
            if (this.name == null)
            {
                if (other.name != null)
                {
                    return false;
                }
            }
            else if (!this.name.equals(other.name))
            {
                return false;
            }
            return true;
        }
    }
}
