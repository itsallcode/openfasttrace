package org.itsallcode.openfasttrace.report.ux;

import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.report.ux.model.Coverage;
import org.itsallcode.openfasttrace.report.ux.model.UxModel;
import org.itsallcode.openfasttrace.report.ux.model.UxSpecItem;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Collector traverses a {@link LinkedSpecificationItem} tree and provides a {@link UxSpecItem} and
 * a {@link UxModel} based on the parsed items.
 */
public class Collector {

    private final List<LinkedSpecificationItem> items = new ArrayList<>();
    private final List<SpecificationItemId> ids = new ArrayList<>();
    private final List<UxSpecItem> uxItems = new ArrayList<>();

    private final List<String> allTypes = new ArrayList<>();
    private final List<String> orderedTypes = new ArrayList<>();

    final List<Map<String, Coverage>> itemCoverages = new ArrayList<>();

    public Collector() {
    }

    /**
     * Fill in the caches of the Collector based on the given items.
     *
     * @param specItems {@link LinkedSpecificationItem} model.
     */
    public Collector collect(List<LinkedSpecificationItem> specItems) {
        this.items.clear();
        this.items.addAll(specItems);

        initializeIndexes();
        collectItemCoverages();

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
     * ItemCoverages provide a shallow coverages for each type of {@link SpecificationItem} type based on the linkage
     * of a SpecItem.
     * The linkage tree is flattended, means the shallows coverages of all types merged. Merged means that the type is
     * not part of the tree {@link Coverage#NONE} is returned, {@link Coverage#UNCOVERED} is returned when at least one
     * item of the type is uncovered. {@link Coverage#COVERED} is returned when all items of a type are covered.
     *
     * @return list of coverages indexes by {@link LinkedSpecificationItem} handed in to {@link #collect(List)}.
     */
    public List<Map<String, Coverage>> getItemCoverages() {return itemCoverages;}

    public List<UxSpecItem> getUxItems() {
        return uxItems;
    }

    //
    // private members

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
    }

    /**
     * @return Get all types of all specItems.
     */
    static Set<String> collectAllTypes(List<LinkedSpecificationItem> items) {
        return items.stream().map(LinkedSpecificationItem::getArtifactType).collect(Collectors.toSet());
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
            collectItemCoverage(i);
        }
    }

    /**
     * Calculate the coverages for a given {@link LinkedSpecificationItem}.
     * The method traverses the tree recursively merging the coverage of all items with the same type
     * with {@link #mergeCoverages(Map, Map)}
     * 
     * @param index The index within the {@link LinkedSpecificationItem} list.
     * @return coverages of the item
     */
    Map<String, Coverage> collectItemCoverage(final int index) {
        // Coverage already collected
        final Map<String, Coverage> targetCoverage = itemCoverages.get(index);
        if( targetCoverage != null ) {
            System.out.println("<<< already covered index " + index);
            return targetCoverage;
        }

        final Map<String, Coverage> coverages = initializedCoverages(orderedTypes);

        // End of the tree
        final LinkedSpecificationItem item = items.get(index);
        if( item.getNeedsArtifactTypes().isEmpty() ) {
            System.out.println("<<< final " + item.getId());
            return updateItemCoverage(index,
                    item.getArtifactType(),
                    item.getStatus() == ItemStatus.APPROVED,
                    coverages);
        }

        // Traverse down
        for( final LinkedSpecificationItem coveringItem : item.getLinksByStatus(LinkStatus.COVERED_SHALLOW) ) {
            int coveringIndex = ids.indexOf(coveringItem.getId());
            System.out.println(">>> coveringItem (" +coveringIndex + ")" + coveringItem.getId());
            final Map<String, Coverage> collectedCoverages = collectItemCoverage(coveringIndex);
            mergeCoverages(collectedCoverages, coverages);
        }

        // Refresh this coverage
        updateItemCoverage(index, item.getArtifactType(), item.isCoveredShallowWithApprovedItems(), coverages);

        System.out.println("<<< intermediate " + item.getId());
        return coverages;
    }

    /**
     * Updates the given coverages by setting the coverage of the goven type and updates the {@link #itemCoverages}.
     *
     * @param index The index of the item
     * @param artifactType The type of the coverage
     * @param covered true if the type is covered
     * @param coverages the coverages to update
     * @return the coverages
     */
    Map<String, Coverage> updateItemCoverage(final int index,
                                             final String artifactType,
                                             final boolean covered,
                                             final Map<String, Coverage> coverages) {
        coverages.put(artifactType, covered ? Coverage.COVERED : Coverage.UNCOVERED);
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
     *
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
        return type1 == org.itsallcode.openfasttrace.report.ux.model.Coverage.UNCOVERED || type2 == org.itsallcode.openfasttrace.report.ux.model.Coverage.UNCOVERED ?
                org.itsallcode.openfasttrace.report.ux.model.Coverage.UNCOVERED
                :
                type1 == org.itsallcode.openfasttrace.report.ux.model.Coverage.COVERED || type2 == org.itsallcode.openfasttrace.report.ux.model.Coverage.COVERED ?
                        org.itsallcode.openfasttrace.report.ux.model.Coverage.COVERED
                        :
                        org.itsallcode.openfasttrace.report.ux.model.Coverage.NONE;
    }

    /**
     * @param allTypes
     *         all known SpecItem types
     * @return Map with all SpecItemTypes as name and Coverage.NONE
     */
    static Map<String, Coverage> initializedCoverages(final List<String> allTypes) {
        return allTypes.stream().collect(
                Collectors.toMap(type -> type, (any) -> org.itsallcode.openfasttrace.report.ux.model.Coverage.NONE));
    }

} // Collector
