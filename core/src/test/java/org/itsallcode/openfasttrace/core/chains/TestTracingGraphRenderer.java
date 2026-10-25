package org.itsallcode.openfasttrace.core.chains;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;

import org.itsallcode.openfasttrace.api.core.Newline;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.junit.jupiter.api.Test;

// [utest->dsn~cli.chains.output-format~1]
class TestTracingGraphRenderer
{
    @Test
    void testRenderChainsFormat()
    {
        final SpecificationItem feat = createItem("feat", "f", List.of("req"));
        final SpecificationItem req = createItem("req", "r", List.of("dsn"));
        final SpecificationItem dsn = createItem("dsn", "d", List.of("impl", "utest"));

        final TracingGraph graph = TracingGraph.fromItems(List.of(feat, req, dsn));
        final TracingGraphRenderer renderer = new TracingGraphRenderer(graph, ChainsFormat.CHAINS, Newline.UNIX);

        final String expected = "feat -> req -> dsn -> impl | utest\n";
        assertThat(renderer.render(), equalTo(expected));
    }

    @Test
    void testRenderChainsEmpty()
    {
        final TracingGraph graph = TracingGraph.fromItems(List.of());
        final TracingGraphRenderer renderer = new TracingGraphRenderer(graph, ChainsFormat.CHAINS, Newline.UNIX);

        assertThat(renderer.render(), equalTo(""));
    }

    @Test
    void testRenderChainsItemWithoutNeeds()
    {
        final SpecificationItem req = createItem("req", "r", List.of());
        final TracingGraph graph = TracingGraph.fromItems(List.of(req));
        final TracingGraphRenderer renderer = new TracingGraphRenderer(graph, ChainsFormat.CHAINS, Newline.UNIX);

        assertThat(renderer.render(), equalTo("req\n"));
    }

    @Test
    void testRenderChainsBranchingTreeMaintainsDistinctPaths()
    {
        final SpecificationItem feat = createItem("feat", "f", List.of("req"));
        final SpecificationItem req = createItem("req", "r", List.of("dsn", "scn"));
        final SpecificationItem scn = createItem("scn", "s", List.of("dsn"));
        final SpecificationItem dsn = createItem("dsn", "d", List.of("impl", "itest", "utest"));

        final TracingGraph graph = TracingGraph.fromItems(List.of(feat, req, scn, dsn));
        final TracingGraphRenderer renderer = new TracingGraphRenderer(graph, ChainsFormat.CHAINS, Newline.UNIX);

        final String expected = "feat -> req -> dsn -> impl | itest | utest\n"
                + "feat -> req -> scn -> dsn -> impl | itest | utest\n";
        assertThat(renderer.render(), equalTo(expected));
    }

    @Test
    void testRenderCsvFormat()
    {
        final SpecificationItem req = createItem("req", "r", List.of("impl", "utest"));
        final TracingGraph graph = TracingGraph.fromItems(List.of(req));
        final TracingGraphRenderer renderer = new TracingGraphRenderer(graph, ChainsFormat.CSV, Newline.UNIX);

        final String expected = "from,to\n"
                + "req,impl\n"
                + "req,utest\n";
        assertThat(renderer.render(), equalTo(expected));
    }

    @Test
    void testRenderCsvEmpty()
    {
        final TracingGraph graph = TracingGraph.fromItems(List.of());
        final TracingGraphRenderer renderer = new TracingGraphRenderer(graph, ChainsFormat.CSV, Newline.UNIX);

        assertThat(renderer.render(), equalTo("from,to\n"));
    }

    @Test
    void testRenderMermaidFormat()
    {
        final SpecificationItem req = createItem("req", "r", List.of("impl", "utest"));
        final TracingGraph graph = TracingGraph.fromItems(List.of(req));
        final TracingGraphRenderer renderer = new TracingGraphRenderer(graph, ChainsFormat.MERMAID, Newline.UNIX);

        final String expected = "graph LR\n"
                + "    req --> impl\n"
                + "    req --> utest\n";
        assertThat(renderer.render(), equalTo(expected));
    }

    @Test
    void testRenderMermaidEmpty()
    {
        final TracingGraph graph = TracingGraph.fromItems(List.of());
        final TracingGraphRenderer renderer = new TracingGraphRenderer(graph, ChainsFormat.MERMAID, Newline.UNIX);

        assertThat(renderer.render(), equalTo("graph LR\n"));
    }

    @Test
    void testRenderWithWindowsNewlines()
    {
        final SpecificationItem req = createItem("req", "r", List.of("impl"));
        final TracingGraph graph = TracingGraph.fromItems(List.of(req));
        final TracingGraphRenderer renderer = new TracingGraphRenderer(graph, ChainsFormat.CHAINS, Newline.WINDOWS);

        assertThat(renderer.render(), equalTo("req -> impl\r\n"));
    }

    private SpecificationItem createItem(final String type, final String name, final List<String> needs)
    {
        final SpecificationItem.Builder builder = SpecificationItem.builder()
                .id(type, name, 1);
        needs.forEach(builder::addNeedsArtifactType);
        return builder.build();
    }
}
