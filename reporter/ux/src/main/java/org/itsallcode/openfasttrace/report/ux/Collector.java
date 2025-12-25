package org.itsallcode.openfasttrace.report.ux;

import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.report.ux.model.Coverage;
import org.itsallcode.openfasttrace.report.ux.model.UxModel;
import org.itsallcode.openfasttrace.report.ux.model.UxSpecItem;
import org.itsallcode.openfasttrace.report.ux.model.WrongLinkType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static java.util.Map.entry;

/**
 * Collector traverses a {@link LinkedSpecificationItem} tree and provides a {@link UxSpecItem} and
 * a {@link UxModel} based on the parsed items.
 */
public class Collector {

    private final List<LinkedSpecificationItem> items = new ArrayList<>();
    private final List<SpecificationItemId> ids = new ArrayList<>();

    private final List<String> allTypes = new ArrayList<>();
    private final List<String> orderedTypes = new ArrayList<>();

    private final List<WrongLinkType> wrongLinkTypes = new ArrayList<>();

    private final List<String> tags = new ArrayList<>();
    private final List<Integer> tagCount = new ArrayList<>();

    final List<Map<String, Coverage>> itemCoverages = new ArrayList<>();

    private final List<Boolean> isDeepCovered = new ArrayList<>();

    private final List<UxSpecItem> uxItems = new ArrayList<>();

    private final List<Integer> typeCount = new ArrayList<>();

    private final List<Integer> uncoveredCounts = new ArrayList<>();

    private final List<Integer> statusCount = new ArrayList<>();

    private final List<Integer> wrongLinkCount = new ArrayList<>();

    private UxModel uxModel = null;

    public Collector() {
    }

    /**
     * Fill in the caches of the Collector based on the given items.
     *
     * @param specItems
     *         {@link LinkedSpecificationItem} model.
     */
    public Collector collect(List<LinkedSpecificationItem> specItems) {
        this.items.clear();
        this.items.addAll(specItems);

        initializeIndexes();
        collectItemCoverages();
        collectUxItems();
        collectUxModel();

        return this;
    }

    /**
     * @return unordered list of {@link SpecificationItem} types.
     */
    public List<String> getAllTypes() {
        return allTypes;
    }

    /**
     * @return {@link SpecificationItem} types ordered base on the downward linkage of items.
     */
    public List<String> getOrderedTypes() {
        return orderedTypes;
    }

    /**
     * @return all tags of all items.
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * ItemCoverages provide a shallow coverages for each type of {@link SpecificationItem} type based on the linkage
     * of a SpecItem.
     * The linkage tree is flattened, means the shallows coverages of all types merged. Merged means that the type is
     * not part of the tree {@link Coverage#NONE} is returned, {@link Coverage#UNCOVERED} is returned when at least one
     * item of the type is uncovered. {@link Coverage#COVERED} is returned when all items of a type are covered.
     *
     * @return list of coverages indexes by {@link LinkedSpecificationItem} handed in to {@link #collect(List)}.
     */
    public List<Map<String, Coverage>> getItemCoverages() {
        return itemCoverages;
    }

    /**
     * @return the metamodel of the collected items.
     */
    public UxModel getUxModel() {
        return uxModel;
    }

    /**
     * @return All {@link UxSpecItem} matching all items given to {@link #collect(List)}
     */
    public List<UxSpecItem> getUxItems() {
        return uxItems;
    }

    //
    // private members

    // UxModel

    private void collectUxModel() {
        uxModel = UxModel.Builder.builder()
                .withProjectName(generateProjectName(""))
                .withArtifactTypes(orderedTypes)
                .withNumberOfSpecItems(items.size())
                .withUncoveredSpecItems(items.size() - (int) isDeepCovered.stream().filter(covered -> covered).count())
                .withTags(tags)
                .withStatusNames(Arrays.stream(ItemStatus.values()).map(ItemStatus::toString).toList())
                .withWrongLinkType(wrongLinkTypes)
                .withTypeCount(typeCount)
                .withUncoveredCount(uncoveredCounts)
                .withStatusCount(statusCount)
                .withTagCount(tagCount)
                .withWrongLinkCount(wrongLinkCount)
                .withItems(uxItems)
                .build();
    }

    private String generateProjectName(final String name) {
        final StringBuilder projectName = new StringBuilder();
        projectName.append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replaceAll(":", "."));
        if( name != null && !name.isEmpty() ) projectName.append(name).append("-");

        return projectName.toString();
    }

    private void collectUxItems() {
        uxItems.clear();
        for( int i = 0; i < items.size(); i++ ) {
            uxItems.add(createUxSpecItem(i));
        }
    }

    UxSpecItem createUxSpecItem(final int index) {
        final LinkedSpecificationItem item = items.get(index);
        return UxSpecItem.Builder.builder()
                .withIndex(index)
                .withTypeIndex(orderedTypes.indexOf(item.getArtifactType()))
                .withTitle(toTitle(item))
                .withName(toName(item))
                .withId(toId(item))
                .withTagIndex(toTagIndex(item))
                .withProvidesIndex(getProvidesTypeIndex(item))
                .withNeededTypeIndex(typeToIndex(item.getNeedsArtifactTypes()))
                .withCoveredIndex(toCoveragesIds(index))
                .withUncoveredIndex(toUncoveredIndexes(index))
                .withCoveringIndex(toItemIndex(item.getLinksByStatus(LinkStatus.COVERS)))
                .withCoveredByIndex(toItemIndex(item.getLinksByStatus(LinkStatus.COVERED_SHALLOW)))
                .withDependsIndex(toIdIndex(item.getItem().getDependOnIds()))
                .withStatusId(item.getItem().getStatus().ordinal())
                .withWrongLinkTypes(getWrongLinkTypeIndexes(item))
                .withWrongLinkTargets(getWrongLinkTypeByTargets(item))
                //.withPath()
                .withItem(item)
                .build();
    }

    private List<Integer> typeToIndex(final List<String> types) {
        return types.stream().map(orderedTypes::indexOf).toList();
    }

    private String toTitle(final LinkedSpecificationItem item) {
        return item.getTitleWithFallback();
    }

    private String toName(final LinkedSpecificationItem item) {
        return item.getId().getName();
    }

    private String toId(final LinkedSpecificationItem item) {
        final String type = item.getId().getArtifactType();
        final String name = item.getId().getName();
        final int version = item.getId().getRevision();
        return version > 1 ? type + ":" + name + ":" + version : type + ":" + name;
    }

    private List<Integer> getProvidesTypeIndex( final LinkedSpecificationItem item ) {
        List<LinkedSpecificationItem> uplinks = item.getLinks().getOrDefault(LinkStatus.COVERS,List.of());
        return typeToIndex(uplinks.stream().map(LinkedSpecificationItem::getArtifactType).toList());
    }

    private List<Integer> toCoveragesIds(final int index) {
        final Map<String, Coverage> coverages = itemCoverages.get(index);
        return orderedTypes.stream().map(type -> {
            final Coverage coverage = coverages.get(type);
            return coverage != null ? coverage.getId() : Coverage.NONE.getId();
        }).toList();
    }

    private List<Integer> toUncoveredIndexes(final int index) {
        final Map<String, Coverage> coverages = itemCoverages.get(index);
        final List<Integer> uncoveredIndexes = new ArrayList<>();
        int i = 0;
        for( final String type : orderedTypes ) {
            final Coverage coverage = coverages.get(type);
            if( ( coverage == Coverage.UNCOVERED || coverage == Coverage.MISSING ) ) {
                uncoveredIndexes.add(i);
            }
            i++;
        }
        return uncoveredIndexes;
    }

    private List<Integer> getWrongLinkTypeIndexes(final LinkedSpecificationItem item)
    {
        return item.getLinks().keySet().stream()
                .map(WrongLinkType::toWrongLinkType)
                .filter(WrongLinkType::isValid).distinct()
                .map(wrongLinkTypes::indexOf)
                .toList();
    }

    private Map<String, String> getWrongLinkTypeByTargets(final LinkedSpecificationItem item)
    {
        final Set<LinkStatus> acceptedStatusTypes = Set.of(LinkStatus.ORPHANED, LinkStatus.AMBIGUOUS,
                LinkStatus.COVERED_UNWANTED, LinkStatus.COVERED_OUTDATED, LinkStatus.COVERED_PREDATED);

        final Map<List<LinkedSpecificationItem>, LinkStatus> statusByLinkTargets = item.getLinks().entrySet().stream()
                .filter(entry -> acceptedStatusTypes.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        return statusByLinkTargets.entrySet().stream()
                .flatMap(entry -> entry.getKey().stream()
                        .map(targetItem -> entry(toId(targetItem), entry.getValue().toString())))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    private List<Integer> toItemIndex(final List<LinkedSpecificationItem> items) {
        return items.stream().map(item -> ids.indexOf(item.getId())).toList();
    }

    private List<Integer> toIdIndex(final List<SpecificationItemId> ids) {
        return ids.stream().map(this.ids::indexOf).filter(id -> id >= 0).toList();
    }

    private List<Integer> toTagIndex(final LinkedSpecificationItem item) {
        return item.getTags().stream().map(tags::indexOf).toList();
    }

    // Types and indexes

    /**
     * Fill alTypes and orderedTypes.
     */
    private void initializeIndexes() {
        allTypes.clear();
        allTypes.addAll(collectAllTypes(items));
        orderedTypes.clear();
        orderedTypes.addAll(createOrderedTypes(items));
        typeCount.clear();
        typeCount.addAll(collectTypeCount(items, orderedTypes));
        wrongLinkTypes.clear();
        wrongLinkTypes.addAll(collectWrongLinkTypes(items));

        ids.clear();
        ids.addAll(items.stream().map(LinkedSpecificationItem::getId).toList());

        tags.clear();
        tagCount.clear();
        final Map<String, Integer> tagMap = collectTagCount(items);
        final List<String> tagList = new ArrayList<>(tagMap.keySet());
        tags.addAll(tagList);
        tagList.forEach(tag -> tagCount.add(tagMap.get(tag)));

        statusCount.clear();
        statusCount.addAll(collectStatusCount(items));

        wrongLinkCount.clear();
        wrongLinkCount.addAll(collectWrongLinkCount(items, wrongLinkTypes));
    }

    /**
     * @return Get all types of all specItems.
     */
    static Set<String> collectAllTypes(final List<LinkedSpecificationItem> items) {
        return items.stream().map(LinkedSpecificationItem::getArtifactType).collect(Collectors.toSet());
    }

    /**
     * Collects all wrongLinkTypes that exist in the model.
     *
     * @param items
     *         All {@link LinkedSpecificationItem}
     * @return A list of used types
     */
    static List<WrongLinkType> collectWrongLinkTypes(final List<LinkedSpecificationItem> items)
    {
        return items.stream()
                .map(item -> item.getLinks().keySet())
                .flatMap(Collection::stream)
                .map(WrongLinkType::toWrongLinkType)
                .filter(WrongLinkType::isValid)
                .distinct()
                .toList();
    }

    /**
     * Provides a list of tags accompanied by the number of items that provides a specific tags.
     *
     * @param items
     *         The items to process
     * @return tag to count mapping
     */
    static Map<String, Integer> collectTagCount(final List<LinkedSpecificationItem> items)
    {
        final Map<String, Integer> tags = new HashMap<>();
        for (final LinkedSpecificationItem item : items)
        {
            for (final String tag : item.getTags())
            {
                tags.put(tag, tags.getOrDefault(tag, 0) + 1);
            }
        }

        return tags;
    }

    /**
     * Provide a list of artifact types sorted by needs dependencies extracted form items.
     *
     * @param items
     *         Items to process
     * @return order types
     */
    static List<String> createOrderedTypes(final List<LinkedSpecificationItem> items) {
        final List<String> orderedTypes = new ArrayList<>();
        final Map<String, TypeDependencies> dependenciesByType = collectDependentTypes(items);

        // Kahn's BFS algorithm
        while( !dependenciesByType.isEmpty() ) {
            final Map<String, TypeDependencies> previousDependenciesByType = new HashMap<>(dependenciesByType);

            for (final Entry<String, TypeDependencies> neededTypeEntry : previousDependenciesByType.entrySet())
            {
                final String type = neededTypeEntry.getKey();
                final TypeDependencies dependencies = neededTypeEntry.getValue();
                if( dependencies.needs.isEmpty() ) {
                    orderedTypes.add(0, type);
                    dependencies.provides.forEach(
                            (providerType) -> dependenciesByType.get(providerType).needs.remove(type));
                    dependenciesByType.remove(type);
                }
            }

            // Break circles
            if( dependenciesByType.size() == previousDependenciesByType.size() ) {
                orderedTypes.addAll(0, dependenciesByType.keySet());
                dependenciesByType.clear();
            }
        }

        return orderedTypes;
    }

    static class TypeDependencies {
        public final Set<String> provides = new HashSet<>();
        public final Set<String> needs = new HashSet<>();

        @Override public String toString() {
            return String.format("{provides{%s}, needs[%s]}", String.join(",", provides), String.join(",", needs));
        }
    } // TypeDependencies

    /**
     * @return superset of all types needed by a type for all types of all items
     */
    static Map<String, TypeDependencies> collectDependentTypes(final List<LinkedSpecificationItem> items) {
        final Map<String, TypeDependencies> dependenciesByType = new HashMap<>();
        for( final LinkedSpecificationItem item : items ) {
            final String itemType = item.getArtifactType();
            final TypeDependencies dependencies = dependenciesByType.getOrDefault(itemType, new TypeDependencies());

            // Add needed to processed item
            dependencies.needs.addAll(item.getNeedsArtifactTypes());
            dependenciesByType.put(itemType, dependencies);

            // Add item type to provides of all needed types
            for( final String need : dependencies.needs ) {
                final TypeDependencies providerDependencies = dependenciesByType.getOrDefault(need,
                        new TypeDependencies());
                providerDependencies.provides.add(itemType);
                dependenciesByType.put(need, providerDependencies);
            }
        }
        return dependenciesByType;
    }

    /**
     * Collects the number of items for all types of the given types.
     *
     * @param items
     *         The items to process
     * @param orderedTypes
     *         The index of the returned list is the index of the type in orderedTypes
     */
    static List<Integer> collectTypeCount(final List<LinkedSpecificationItem> items,
            final List<String> orderedTypes)
    {
        final List<Integer> typeCount = new ArrayList<>(Collections.nCopies(orderedTypes.size(), 0));
        for (final LinkedSpecificationItem item : items)
        {
            final int typeIndex = orderedTypes.indexOf(item.getArtifactType());
            typeCount.set(typeIndex, typeCount.get(typeIndex) + 1);
        }

        return typeCount;
    }

    static List<Integer> collectStatusCount(final List<LinkedSpecificationItem> items)
    {
        final List<Integer> statusCount = new ArrayList<>(Collections.nCopies(ItemStatus.values().length, 0));
        for (final LinkedSpecificationItem item : items)
        {
            final int statusIndex = item.getStatus().ordinal();
            statusCount.set(statusIndex, statusIndex < statusCount.size() ? statusCount.get(statusIndex) + 1 : 1);
        }

        return statusCount;
    }

    /**
     * Collects the number of wrong links for each wrong link type.
     *
     * @param items
     *         The items to process
     * @param wrongLinkTypes
     *         The list of wrong link types to count
     * @return A list of counts where the index corresponds to the wrong link type index
     */
    static List<Integer> collectWrongLinkCount(final List<LinkedSpecificationItem> items,
            final List<WrongLinkType> wrongLinkTypes)
    {
        return wrongLinkTypes.stream()
                .map(wrongLinkType -> items.stream()
                        .flatMap(item -> item.getLinks().entrySet().stream())
                        .filter(entry -> WrongLinkType.toWrongLinkType(entry.getKey()) == wrongLinkType)
                        .mapToInt(entry -> entry.getValue().size())
                        .sum())
                .toList();
    }


    // Covered Status

    /**
     * Fill in the ItemCoverages.
     */
    void collectItemCoverages() {
        // Initialize coverages
        itemCoverages.clear();
        for( int i = 0; i < items.size(); i++ ) {
            itemCoverages.add(null);
        }

        // Initialize uncoveredCounts
        uncoveredCounts.clear();
        for (int i = 0; i < orderedTypes.size(); i++)
        {
            uncoveredCounts.add(0);
        }

        // Fill coverages
        for( int i = 0; i < items.size(); i++ ) {
            final Map<String, Coverage> itemCoverage = collectItemCoverage(i);
            isDeepCovered.add(collectIsCovered(itemCoverage));
            updateUncoveredCount(i,items.get(i).isCoveredShallowWithApprovedItems());
        }
    }

    /**
     * Update {@link #uncoveredCounts} by incrementing the corresponding entry if the item with the given index is
     * uncovered.
     *
     * @param index
     *         The index of the processed item
     * @param isCovered
     *         true of the item is covered
     * @return true of the item is covered
     */
    private boolean updateUncoveredCount(final int index, final boolean isCovered)
    {
        if (!isCovered)
        {
            final int uncoveredIndex = orderedTypes.indexOf(items.get(index).getArtifactType());
            uncoveredCounts.set(uncoveredIndex, uncoveredCounts.get(uncoveredIndex) + 1);
        }

        return isCovered;
    }

    /**
     * @param itemCoverage
     *         collected coverages for an item
     * @return true if item is fully covered
     */
    private boolean collectIsCovered(final Map<String, Coverage> itemCoverage) {
        return itemCoverage.values().stream().noneMatch(coverage -> coverage == Coverage.UNCOVERED);
    }

    /**
     * Calculate the coverages for a given {@link LinkedSpecificationItem}.
     * The method traverses the tree recursively merging the coverage of all items with the same type
     * with {@link #mergeCoverages(Map, Map)}
     *
     * @param index
     *         The index within the {@link LinkedSpecificationItem} list.
     * @return coverages of the item
     */
    Map<String, Coverage> collectItemCoverage(final int index) {
        // Coverage already collected
        final Map<String, Coverage> targetCoverage = itemCoverages.get(index);
        if( targetCoverage != null ) {
            //System.out.println("<<< already covered index " + index);
            return targetCoverage;
        }

        final Map<String, Coverage> coverages = initializedCoverages(orderedTypes);

        // End of the tree
        final LinkedSpecificationItem item = items.get(index);
        if( item.getNeedsArtifactTypes().isEmpty() ) {
            //System.out.println("<<< final " + item.getId());
            return updateItemCoverage(index,
                    item.getArtifactType(),
                    item.getStatus() == ItemStatus.APPROVED ? Coverage.COVERED : Coverage.UNCOVERED,
                    coverages);
        }

        // Traverse down
        for( final LinkedSpecificationItem coveringItem : item.getLinksByStatus(LinkStatus.COVERED_SHALLOW) ) {
            final int coveringIndex = ids.indexOf(coveringItem.getId());
            //System.out.println(">>> coveringItem (" + coveringIndex + ")" + coveringItem.getId());
            final Map<String, Coverage> collectedCoverages = collectItemCoverage(coveringIndex);
            mergeCoverages(collectedCoverages, coverages);
        }

        // Refresh this coverage
        updateItemCoverage(index,
                item.getArtifactType(),
                item.isCoveredShallowWithApprovedItems() ? Coverage.COVERED : Coverage.UNCOVERED,
                coverages);

        // Refresh needed uncovered types
        for( final String uncoveredType : item.getUncoveredApprovedArtifactTypes() ) {
            updateItemCoverage(index, uncoveredType, Coverage.MISSING, coverages);
        }

        //System.out.println("<<< intermediate " + item.getId());
        return coverages;
    }

    /**
     * Updates the given coverages by setting the coverage of the given type and updates the {@link #itemCoverages}.
     *
     * @param index
     *         The index of the item
     * @param artifactType
     *         The type of the coverage
     * @param coverage
     *         true if the type is covered
     * @param coverages
     *         the coverages to update
     * @return the coverages
     */
    Map<String, Coverage> updateItemCoverage(final int index,
                                             final String artifactType,
                                             final Coverage coverage,
                                             final Map<String, Coverage> coverages) {
        coverages.put(artifactType, coverage);
        itemCoverages.set(index, coverages);
        return coverages;
    }

    /**
     * Merges to SpecItemType coverages resulting in a superset with Coverage types merged by mergeCoverType.
     *
     * @param fromCoverages
     *         types to be merged into toCoverage, may be null
     * @param toCoverages
     *         the target types
     * @return true = merged
     */
    static boolean mergeCoverages(final Map<String, Coverage> fromCoverages,
                                  final Map<String, Coverage> toCoverages) {
        if (fromCoverages == null)
            return false;
        for (final Entry<String, Coverage> fromCoverage : fromCoverages.entrySet())
        {
            final Coverage fromCoverageValue = fromCoverage.getValue();
            final Coverage toCoverageVales = toCoverages.get(fromCoverage.getKey());
            toCoverages.put(fromCoverage.getKey(), mergeCoverType(fromCoverageValue, toCoverageVales));
        }
        return true;
    }

    /**
     * Merges two coverage types.
     * At least one coverage type is uncovered, result is uncovered, no type on with returns NONE, both covered
     * returns covered.
     *
     * @param type1
     *         First input coverage
     * @param type2
     *         Second input coverage
     * @return merge input coverage
     */
    static Coverage mergeCoverType(Coverage type1, Coverage type2) {
        return type1 == Coverage.MISSING || type2 == Coverage.MISSING ? Coverage.MISSING
                : type1 == Coverage.UNCOVERED || type2 == Coverage.UNCOVERED ? Coverage.UNCOVERED
                : type1 == Coverage.COVERED || type2 == Coverage.COVERED ? Coverage.COVERED
                : Coverage.NONE;
    }

    /**
     * @param allTypes
     *         all known SpecItem types
     * @return Map with all SpecItemTypes as name and Coverage.NONE
     */
    static Map<String, Coverage> initializedCoverages(final List<String> allTypes) {
        return allTypes.stream().collect(
                Collectors.toMap(type -> type, (any) -> Coverage.NONE));
    }

} // Collector
