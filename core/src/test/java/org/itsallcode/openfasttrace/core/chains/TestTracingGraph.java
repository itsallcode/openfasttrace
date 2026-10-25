package org.itsallcode.openfasttrace.core.chains;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;

import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.core.chains.TracingGraph.Transition;
import org.junit.jupiter.api.Test;

// [utest->dsn~chains.graph~1]
class TestTracingGraph
{
    @Test
    void testEmptyItemsProduceEmptyGraph()
    {
        final TracingGraph graph = TracingGraph.fromItems(List.of());
        assertThat(graph.getRoots(), empty());
        assertThat(graph.getTransitions(), empty());
    }

    @Test
    void testItemWithoutNeedsProducesRootAndNoTransitions()
    {
        final SpecificationItem item = createItem("req", "foo", List.of());
        final TracingGraph graph = TracingGraph.fromItems(List.of(item));
        assertThat(graph.getRoots(), contains("req"));
        assertThat(graph.getTransitions(), empty());
    }

    @Test
    void testSingleItemWithMultipleNeedsExtractsTransitions()
    {
        final SpecificationItem item = createItem("req", "foo", List.of("utest", "impl"));
        final TracingGraph graph = TracingGraph.fromItems(List.of(item));

        assertThat(graph.getRoots(), contains("req"));
        assertThat(graph.getTransitions(), contains(
                new Transition("req", "impl"),
                new Transition("req", "utest")));
    }

    @Test
    void testMultiLevelRequirementsIdentifyCorrectRoots()
    {
        final SpecificationItem feat = createItem("feat", "f", List.of("req"));
        final SpecificationItem req = createItem("req", "r", List.of("dsn"));
        final SpecificationItem dsn = createItem("dsn", "d", List.of("impl", "utest", "itest"));

        final TracingGraph graph = TracingGraph.fromItems(List.of(feat, req, dsn));

        assertThat(graph.getRoots(), contains("feat"));
        assertThat(graph.getTransitions(), contains(
                new Transition("dsn", "impl"),
                new Transition("dsn", "itest"),
                new Transition("dsn", "utest"),
                new Transition("feat", "req"),
                new Transition("req", "dsn")));
    }

    @Test
    void testForwardingItemParticipatesNormally()
    {
        final SpecificationItem arch = createForwardingItem("arch", "forwarder", List.of("dsn"));
        final SpecificationItem dsn = createItem("dsn", "worker", List.of("impl"));

        final TracingGraph graph = TracingGraph.fromItems(List.of(arch, dsn));

        assertThat(graph.getRoots(), contains("arch"));
        assertThat(graph.getTransitions(), contains(
                new Transition("arch", "dsn"),
                new Transition("dsn", "impl")));
    }

    private SpecificationItem createItem(final String type, final String name, final List<String> needs)
    {
        final SpecificationItem.Builder builder = SpecificationItem.builder()
                .id(type, name, 1);
        needs.forEach(builder::addNeedsArtifactType);
        return builder.build();
    }

    private SpecificationItem createForwardingItem(final String type, final String name,
            final List<String> needs)
    {
        final SpecificationItem.Builder builder = SpecificationItem.builder()
                .id(type, name, 1)
                .forwards(true);
        needs.forEach(builder::addNeedsArtifactType);
        return builder.build();
    }
}
