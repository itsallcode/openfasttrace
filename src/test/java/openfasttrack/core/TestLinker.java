package openfasttrack.core;

import static openfasttrack.core.SpecificationItemBuilders.prepare;

import java.util.Arrays;
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
        final Linker linker = new Linker(Arrays.asList(A, B, C, D));
        final List<LinkedSpecificationItem> linkedItems = linker.link();

    }

}
