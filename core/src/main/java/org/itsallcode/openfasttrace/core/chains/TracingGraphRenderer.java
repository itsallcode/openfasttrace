package org.itsallcode.openfasttrace.core.chains;

import java.util.*;
import java.util.logging.Logger;

import org.itsallcode.openfasttrace.api.core.Newline;
import org.itsallcode.openfasttrace.core.chains.TracingGraph.Transition;

/**
 * Renders a {@link TracingGraph} into one of the supported {@link ChainsFormat}s.
 */
// [impl->dsn~cli.chains.output-format~1]
public class TracingGraphRenderer
{
    private static final Logger LOG = Logger.getLogger(TracingGraphRenderer.class.getName());

    private final TracingGraph graph;
    private final ChainsFormat format;
    private final Newline newline;

    /**
     * Create a new renderer.
     * 
     * @param graph
     *            tracing graph
     * @param format
     *            output format
     * @param newline
     *            newline style
     */
    public TracingGraphRenderer(final TracingGraph graph, final ChainsFormat format, final Newline newline)
    {
        this.graph = graph;
        this.format = format;
        this.newline = newline;
    }

    /**
     * Render the tracing graph into a formatted string.
     * 
     * @return rendered representation
     */
    public String render()
    {
        return switch (this.format)
        {
        case CHAINS -> renderChains();
        case CSV -> renderCsv();
        case MERMAID -> renderMermaid();
        };
    }

    private String renderChains()
    {
        if (this.graph.getRoots().isEmpty() && !this.graph.getTransitions().isEmpty())
        {
            LOG.warning("No root artifact types found; specifications contain circular dependencies.");
        }

        final Map<String, Set<String>> adjacency = new TreeMap<>();
        for (final Transition transition : this.graph.getTransitions())
        {
            adjacency.computeIfAbsent(transition.from(), k -> new TreeSet<>()).add(transition.to());
        }

        final List<List<String>> allPaths = new ArrayList<>();
        for (final String root : this.graph.getRoots())
        {
            final Set<String> children = adjacency.get(root);
            if (children == null || children.isEmpty())
            {
                allPaths.add(List.of(root));
            }
            else
            {
                final List<String> currentPath = new ArrayList<>();
                currentPath.add(root);
                final Set<String> visitedInPath = new HashSet<>();
                visitedInPath.add(root);
                findPaths(root, currentPath, visitedInPath, adjacency, allPaths);
            }
        }

        final List<String> formatted = groupPaths(allPaths);
        if (formatted.isEmpty())
        {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        for (final String chain : formatted)
        {
            builder.append(chain).append(this.newline);
        }
        return builder.toString();
    }

    private void findPaths(final String current, final List<String> currentPath,
            final Set<String> visitedInPath, final Map<String, Set<String>> adjacency,
            final List<List<String>> result)
    {
        final Set<String> children = adjacency.get(current);
        if (children == null || children.isEmpty())
        {
            result.add(new ArrayList<>(currentPath));
            return;
        }

        boolean hasUnvisitedChild = false;
        for (final String child : children)
        {
            if (visitedInPath.contains(child))
            {
                LOG.warning(() -> "Cycle detected in tracing chain: "
                        + String.join(" -> ", currentPath) + " -> " + child);
            }
            else
            {
                hasUnvisitedChild = true;
                visitedInPath.add(child);
                currentPath.add(child);
                findPaths(child, currentPath, visitedInPath, adjacency, result);
                currentPath.remove(currentPath.size() - 1);
                visitedInPath.remove(child);
            }
        }
        if (!hasUnvisitedChild)
        {
            result.add(new ArrayList<>(currentPath));
        }
    }

    private List<String> groupPaths(final List<List<String>> paths)
    {
        final Map<List<String>, Set<String>> grouped = new LinkedHashMap<>();
        for (final List<String> path : paths)
        {
            if (path.size() > 1)
            {
                final List<String> prefix = path.subList(0, path.size() - 1);
                final String leaf = path.get(path.size() - 1);
                grouped.computeIfAbsent(new ArrayList<>(prefix), k -> new TreeSet<>()).add(leaf);
            }
            else if (path.size() == 1)
            {
                grouped.computeIfAbsent(new ArrayList<>(path), k -> new TreeSet<>());
            }
        }

        final List<String> formattedChains = new ArrayList<>();
        for (final Map.Entry<List<String>, Set<String>> entry : grouped.entrySet())
        {
            final List<String> prefix = entry.getKey();
            final Set<String> leaves = entry.getValue();
            if (leaves.isEmpty())
            {
                formattedChains.add(prefix.get(0));
            }
            else
            {
                formattedChains.add(String.join(" -> ", prefix) + " -> " + String.join(" | ", leaves));
            }
        }
        return formattedChains;
    }

    private String renderCsv()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("from,to").append(this.newline);
        for (final Transition transition : this.graph.getTransitions())
        {
            builder.append(transition.from()).append(",").append(transition.to()).append(this.newline);
        }
        return builder.toString();
    }

    private String renderMermaid()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("graph LR").append(this.newline);
        for (final Transition transition : this.graph.getTransitions())
        {
            builder.append("    ")
                    .append(transition.from())
                    .append(" --> ")
                    .append(transition.to())
                    .append(this.newline);
        }
        return builder.toString();
    }
}
