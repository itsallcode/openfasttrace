package openfasttrack.core;

import static openfasttrack.core.SpecificationItemBuilders.prepare;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class TestLinker
{
    private static final SpecificationItem A;
    private static final SpecificationItem B;
    private static final SpecificationItem C;
    private static final SpecificationItem D;

    static
    {
        A = prepare("req", "A", 1).addNeedsArtifactType("dsn").build();
        B = prepare("dsn", "B", 1).addCoveredId(A.getId()).build();
        C = prepare("impl", "C", 1).addCoveredId(A.getId()).build();
        D = prepare("req", "D", 1).build();
    }

    @Test
    public void testLink()
    {
        final List<SpecificationItem> items = Arrays.asList(A, B, C, D);
        final Linker linker = new Linker(items);
        final List<LinkedSpecificationItem> linkedItems = linker.link();
        assertThat(linkedItems, hasSize(items.size()));
        final Iterator<LinkedSpecificationItem> iterator = linkedItems.iterator();
        final LinkedSpecificationItem linkedA = iterator.next();
        final LinkedSpecificationItem linkedB = iterator.next();
        final LinkedSpecificationItem linkedC = iterator.next();
        final LinkedSpecificationItem linkedD = iterator.next();
        assertThat(linkedA.getCoveredLinks(), hasSize(0));
        assertThat(linkedB.getCoveredLinks(), hasSize(1));
        assertThat(linkedB.getCoveredLinks(), hasSize(1));
        assertThat(linkedC.getCoveredLinks(), hasSize(0));
    }

}
