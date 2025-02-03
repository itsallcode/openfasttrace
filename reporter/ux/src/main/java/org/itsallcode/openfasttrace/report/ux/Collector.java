package org.itsallcode.openfasttrace.report.ux;

import org.itsallcode.openfasttrace.api.core.LinkStatus;
import org.itsallcode.openfasttrace.api.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.report.ux.model.Coverage;
import org.itsallcode.openfasttrace.report.ux.model.UxSpecItem;

import java.util.*;
import java.util.stream.Collectors;

public class Collector {

    private final List<LinkedSpecificationItem> items = new ArrayList<>();
    private final List<UxSpecItem> uxItems = new ArrayList<>();

    private final List<String> allTypes = new ArrayList<>();
    private final List<String> orderedTypes = new ArrayList<>();

    private final Map<LinkedSpecificationItem, Map<String, Coverage>> itemCoverages = new HashMap<>();

    public Collector() {
    }

    public Collector collect(List<LinkedSpecificationItem> specItems) {
        this.items.clear();
        this.items.addAll(specItems);

        initializeIndexes();
        collectItemCoverages();

        return this;
    }

    public List<String> getAllTypes() {
        return allTypes;
    }

    public List<String> getOrderedTypes() {
        return orderedTypes;
    }

    public Map<LinkedSpecificationItem, Map<String, Coverage>> getItemCoverages() {
        return itemCoverages;
    }

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
        orderedTypes.addAll(createOrderedTypes(items, allTypes));
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
     * @param allTypes
     *         all existing types
     * @return order types
     */
    static List<String> createOrderedTypes(final List<LinkedSpecificationItem> items,
                                           final List<String> allTypes) {
        final List<String> orderedTypes = new ArrayList<>();
        final Map<String, TypeDependencies> dependenciesByType = collectDependentTypes(items);

        // Kahn's BFS algorithm
        while( !dependenciesByType.isEmpty() && orderedTypes.size() < allTypes.size() ) {
            for( final Map.Entry<String, TypeDependencies> neededTypeEntry : dependenciesByType.entrySet().stream()
                    .toList() ) {
                final String type = neededTypeEntry.getKey();
                final TypeDependencies dependencies = neededTypeEntry.getValue();
                if( dependencies.needs.isEmpty() ) {
                    orderedTypes.add(0, type);
                    dependencies.provides.forEach(
                            (providerType) -> dependenciesByType.get(providerType).needs.remove(type));
                    dependenciesByType.remove(type);
                }
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

    void collectItemCoverages() {
        itemCoverages.clear();
        final Map<String, Coverage> coverages = initializedCoverages(orderedTypes);
        for( final LinkedSpecificationItem item : items ) {
            collectItemCoverage(item, coverages);
        }
    }

    void collectItemCoverage(final LinkedSpecificationItem item, final Map<String, Coverage> coverages) {
        // Item Coverage already collected
        if( mergeCoverages(itemCoverages.get(item), coverages) ) return;

        // End of the tree
        if( item.getNeedsArtifactTypes().isEmpty() ) {
            updateItemCoverage(item, true, coverages);
            return;
        }

        // Traverse down
        for( final LinkedSpecificationItem coveringItem : item.getLinksByStatus(LinkStatus.COVERED_SHALLOW) ) {
            collectItemCoverage(coveringItem, coverages);
        }

        // Refresh this coverage
        updateItemCoverage(item, item.isCoveredShallowWithApprovedItems(), coverages);
    }

    void updateItemCoverage(final LinkedSpecificationItem item,
                            final boolean covered,
                            final Map<String, Coverage> coverages) {
        final Map<String, Coverage> targetCoverages = itemCoverages.getOrDefault(item, new HashMap<>());
        mergeCoverages(coverages, targetCoverages);
        targetCoverages.put(item.getArtifactType(), covered ? Coverage.COVERED : Coverage.UNCOVERED);
        itemCoverages.put(item, targetCoverages);
    }

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

    static Coverage mergeCoverType(Coverage type1, Coverage type2) {
        return type1 == Coverage.UNCOVERED || type2 == Coverage.UNCOVERED ? Coverage.UNCOVERED
                : type1 == Coverage.COVERED || type2 == Coverage.COVERED ? Coverage.COVERED
                : Coverage.NONE;
    }

    static Map<String, Coverage> initializedCoverages(final List<String> allTypes) {
        return allTypes.stream().collect(Collectors.toMap(type -> type, (any) -> Coverage.NONE));
    }

} // Collector
