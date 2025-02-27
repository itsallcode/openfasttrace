package org.itsallcode.openfasttrace.report.ux;

import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.report.ux.model.Coverage;
import org.itsallcode.openfasttrace.report.ux.model.UxModel;
import org.itsallcode.openfasttrace.report.ux.model.UxSpecItem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Collector traverses a {@link LinkedSpecificationItem} tree and provides a {@link UxSpecItem} and
 * a {@link UxModel} based on the parsed items.
 */
public class Collector {

    private final List<LinkedSpecificationItem> items = new ArrayList<>();
    private final List<SpecificationItemId> ids = new ArrayList<>();

    private final List<String> allTypes = new ArrayList<>();
    private final List<String> orderedTypes = new ArrayList<>();

    private final List<String> tags = new ArrayList<>();
    private final Set<String> uniqueTags = new HashSet<>();

    final List<Map<String, Coverage>> itemCoverages = new ArrayList<>();

    private final List<Boolean> isCovered = new ArrayList<>();

    private final List<UxSpecItem> uxItems = new ArrayList<>();
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
     * @return the meta model of the collected items.
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
                .withUncoveredSpecItems(items.size() - (int)isCovered.stream().filter(covered -> covered).count())
                .withTags(tags)
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
                .withNeededTypeIndex(typeToIndex(item.getNeedsArtifactTypes()))
                .withCoveredIndex(toCoveragesIds(index))
                .withCoveringIndex(toItemIndex(item.getLinksByStatus(LinkStatus.COVERS)))
                .withCoveredByIndex(toItemIndex(item.getLinksByStatus(LinkStatus.COVERED_SHALLOW)))
                .withDependsIndex(toIdIndex(item.getItem().getDependOnIds()))
                .withStatusId(item.getItem().getStatus().ordinal())
                //.withPath()
                .withItem(item)
                .build();
    }

    private List<Integer> typeToIndex(final List<String> types) {
        return types.stream().map(orderedTypes::indexOf).toList();
    }

    private String toTitle(final LinkedSpecificationItem item ) {
        final String title = item.getTitle();
        return title != null && !title.isEmpty() ? title :  item.getId().getName();
    }

    private String toName( final LinkedSpecificationItem item ) {
        return item.getId().getName();
    }

    private String toId(final LinkedSpecificationItem item) {
        final String type = item.getId().getArtifactType();
        final String name = item.getId().getName();
        final int version = item.getId().getRevision();
        return version > 1 ? type + ":" + name + ":" + version : type + ":" + name;
    }

    private List<Integer> toCoveragesIds(final int index) {
        final Map<String, Coverage> coverages = itemCoverages.get(index);
        return orderedTypes.stream().map(type -> {
            final Coverage coverage = coverages.get(type);
            return coverage != null ? coverage.getId() : Coverage.NONE.ordinal();
        }).toList();
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

        ids.clear();
        ids.addAll(items.stream().map(LinkedSpecificationItem::getId).toList());

        tags.clear();
        uniqueTags.clear();
        tags.addAll(collectTags(items, uniqueTags));
    }

    /**
     * @return Get all types of all specItems.
     */
    static Set<String> collectAllTypes(final List<LinkedSpecificationItem> items) {
        return items.stream().map(LinkedSpecificationItem::getArtifactType).collect(Collectors.toSet());
    }

    static List<String> collectTags(final List<LinkedSpecificationItem> items, Set<String> uniqueTags) {
        final List<String> tags = new ArrayList<>();
        for( final LinkedSpecificationItem item : items ) {
            item.getTags().stream().filter(tag -> !uniqueTags.contains(tag)).forEach(tags::add);
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

            for( final Map.Entry<String, TypeDependencies> neededTypeEntry : previousDependenciesByType.entrySet() ) {
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

    // Covered Status

    /**
     * Fille in the ItemCoverages.
     */
    void collectItemCoverages() {
        // Initialize coverages
        itemCoverages.clear();
        for( int i = 0; i < items.size(); i++ ) {
            itemCoverages.add(null);
        }

        // Fill coverages
        for( int i = 0; i < items.size(); i++ ) {
            final Map<String, Coverage> itemCoverage = collectItemCoverage(i);
            isCovered.add(collectIsCovered(itemCoverage));
        }
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
                item.isCoveredShallowWithApprovedItems() ? Coverage.COVERED :Coverage.UNCOVERED,
                coverages);

        // Refresh needed uncovered types
        for( final String uncoveredType : item.getUncoveredApprovedArtifactTypes() ) {
            updateItemCoverage(index,uncoveredType,Coverage.MISSING,coverages);
        }

        //System.out.println("<<< intermediate " + item.getId());
        return coverages;
    }

    /**
     * Updates the given coverages by setting the coverage of the goven type and updates the {@link #itemCoverages}.
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
        if( fromCoverages == null ) return false;
        for( final Map.Entry<String, Coverage> fromCoverage : fromCoverages.entrySet() ) {
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
