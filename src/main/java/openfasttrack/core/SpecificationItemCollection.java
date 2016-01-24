package openfasttrack.core;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class SpecificationItemCollection implements Iterable<SpecificationItem>
{
    private static final int FIRST_INDEX = 0;
    private final List<SpecificationItem> items;
    private Map<SpecificationItemId, List<SpecificationItem>> idIndex = new TreeMap<>();
    private Map<SpecificationItemId, List<SpecificationItem>> idVersionLessIndex = new TreeMap<>();

    public SpecificationItemCollection(final List<SpecificationItem> items)
    {
        this.items = items;
        this.idIndex = this.items.parallelStream()
                .collect(Collectors.groupingBy(SpecificationItem::getId));
        this.idVersionLessIndex = this.items.parallelStream()
                .collect(Collectors.groupingBy(item -> item.getId().toRevisionWildcard()));
    }

    public List<SpecificationItem> getAll(final SpecificationItemId id)
    {
        return this.idIndex.get(id);
    }

    public List<SpecificationItem> getAll(final String artifactType, final String name)
    {
        final SpecificationItemId versionLessId = new SpecificationItemId.Builder()
                .artifactType(artifactType).name(name).revisionWildcard().build();
        return this.idVersionLessIndex.get(versionLessId);
    }

    public SpecificationItem getFirst(final SpecificationItemId id)
    {
        return this.getAll(id).get(FIRST_INDEX);
    }

    public SpecificationItem getFirst(final String artifactType, final String name)
    {
        return this.getAll(artifactType, name).get(FIRST_INDEX);
    }

    public int size()
    {
        return this.items.size();
    }

    @Override
    public Iterator<SpecificationItem> iterator()
    {
        return this.items.iterator();
    }

    public boolean containsKey(final SpecificationItemId coveredId)
    {
        return this.idIndex.containsKey(coveredId);
    }

    public boolean containsKey(final String artifactType, final String name)
    {
        return this.idVersionLessIndex
                .containsKey(SpecificationItemId.createId(artifactType, name));
    }
}
