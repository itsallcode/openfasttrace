package org.itsallcode.openfasttrace.core.chains;

import java.util.*;

import org.itsallcode.openfasttrace.api.core.SpecificationItem;

/**
 * Tracing graph representation containing roots and directed transitions.
 */
// [impl->dsn~chains.graph~1]
public class TracingGraph
{
    /**
     * A directed transition from one artifact type to another.
     * 
     * @param from
     *            source artifact type
     * @param to
     *            target artifact type
     */
    public record Transition(String from, String to) implements Comparable<Transition>
    {
        @Override
        public int compareTo(final Transition other)
        {
            final int cmp = this.from.compareTo(other.from);
            if (cmp != 0)
            {
                return cmp;
            }
            return this.to.compareTo(other.to);
        }
    }

    private final Set<String> roots;
    private final Set<Transition> transitions;

    /**
     * Create a tracing graph.
     * 
     * @param roots
     *            set of root artifact types
     * @param transitions
     *            set of directed transitions
     */
    public TracingGraph(final Set<String> roots, final Set<Transition> transitions)
    {
        this.roots = Collections.unmodifiableSet(roots);
        this.transitions = Collections.unmodifiableSet(transitions);
    }

    /**
     * Build a tracing graph from imported specification items.
     * 
     * @param items
     *            specification items
     * @return tracing graph containing roots and transitions
     */
    public static TracingGraph fromItems(final List<SpecificationItem> items)
    {
        final Set<String> types = new TreeSet<>();
        final Set<Transition> transitions = new TreeSet<>();
        final Set<String> targets = new HashSet<>();

        for (final SpecificationItem item : items)
        {
            final String from = item.getArtifactType();
            types.add(from);
            for (final String to : item.getNeedsArtifactTypes())
            {
                transitions.add(new Transition(from, to));
                targets.add(to);
            }
        }

        final Set<String> roots = new TreeSet<>(types);
        roots.removeAll(targets);

        return new TracingGraph(roots, transitions);
    }

    /**
     * Get the root artifact types.
     * 
     * @return root artifact types
     */
    public Set<String> getRoots()
    {
        return this.roots;
    }

    /**
     * Get the directed transitions.
     * 
     * @return transitions
     */
    public Set<Transition> getTransitions()
    {
        return this.transitions;
    }
}
