package openfasttrack.core;

import static openfasttrack.core.SpecificationItemId.createId;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import openfasttrack.core.SpecificationItem.Builder;

public class TestTracer
{
    private static final SpecificationItem A;
    private static final SpecificationItem B;
    private static final SpecificationItem C;
    private static final SpecificationItem D;
    private static final SpecificationItem E;
    private static final SpecificationItem F;
    private static final SpecificationItem G;
    private static final SpecificationItem H;
    private static final SpecificationItem I;
    private static final SpecificationItem J;
    private static final SpecificationItem K1;
    private static final SpecificationItem K2;
    private static final SpecificationItem L;
    private static final SpecificationItem M1;
    private static final SpecificationItem M2;
    private static final SpecificationItem N;
    private static final SpecificationItemId NON_EXISTENT_ID = createId("bogus", "dummy", 1);
    private static final SpecificationItemId OUTDATED_G_ID = createId("req", "G", 6);
    private static final SpecificationItemId PREDATED_G_ID = createId("req", "G", 8);
    private static final SpecificationItemId AMBIGUOUS_M_ID = createId("req", "M", 5);

    private Trace trace;

    static
    {
        // @formatter:off                                                                                      | OK | UNW | OUT | PRE | BRO | AMB || DUP
        A  = prepare("feat" , "A", 1).addNeededArtifactType("req").build();                                 // |    |     |     |     |     |     ||
        B  = prepare("req"  , "B", 2).addCoveredId(A.getId()).build();                                      // |  1 |     |     |     |     |     ||
        C  = prepare("impl" , "C", 3).addCoveredId(A.getId()).build();                                      // |    |   1 |     |     |     |     ||
        D  = prepare("req"  , "D", 4).addNeededArtifactType("impl").build();                                // |    |     |     |     |     |     ||
        E  = prepare("uman" , "E", 5).addCoveredId(A.getId()).addCoveredId(B.getId()).build();              // |    |   2 |     |     |     |     ||
        F  = prepare("req"  , "F", 6).addCoveredId(NON_EXISTENT_ID).build();                                // |    |     |     |     |   1 |     ||
        G  = prepare("req"  , "G", 7).addNeededArtifactType("impl").addNeededArtifactType("utest").build(); // |    |     |     |     |     |     ||
        H  = prepare("impl" , "H", 8).addCoveredId(OUTDATED_G_ID).build();                                  // |    |     |  1  |     |     |     ||
        I  = prepare("utest", "I", 9).addCoveredId(PREDATED_G_ID).build();                                  // |    |     |     |  1  |     |     ||
        J  = prepare("utest", "J", 1).addCoveredId(G.getId()).addCoveredId(NON_EXISTENT_ID).build();        // |  1 |     |     |     |   1 |     ||
        K1 = prepare("feat" , "K", 2).addNeededArtifactType("req").build();                                 // |    |     |     |     |     |     ||   1
        K2 = prepare("feat" , "K", 2).addNeededArtifactType("req").build();                                 // |    |     |     |     |     |     ||   1
        L  = prepare("req"  , "L", 3).addCoveredId(K1.getId()).build();                                     // |    |     |     |     |     |   1 ||
        M1 = prepare("req"  , "M", 4).addNeededArtifactType("itest").build();                               // |    |     |     |     |     |     ||   1
        M2 = prepare("req"  , "M", 6).addNeededArtifactType("itest").build();                               // |    |     |     |     |     |     ||   1
        N  = prepare("itest", "N", 7).addCoveredId(AMBIGUOUS_M_ID).build();                                 // |    |     |     |     |     |   1 ||
        // @formatter:on
    }

    private static Builder prepare(final String artifactType, final String name, final int revision)
    {
        return new SpecificationItem.Builder().id(artifactType, name, revision);
    }

    @Before
    public void prepareTest()
    {
        final List<SpecificationItem> items = Arrays.asList(A, B, C, D, E, F, G, H, I, J, K1, K2, L,
                M1, M2, N);
        final Tracer tracer = new Tracer(new SpecificationItemCollection(items));
        this.trace = tracer.trace();
    }

    @Test
    public void testCountAllLinks()
    {
        assertThat(this.trace.countAllLinks(), equalTo(11));
    }

    @Test
    public void testCountLinksWithStatus_OK()
    {
        assertLinksWithStatus(LinkStatus.OK, 2L);
    }

    private void assertLinksWithStatus(final LinkStatus status, final long expectedCount)
    {
        assertThat(this.trace.countLinksWithStatus(status), equalTo(expectedCount));
    }

    @Test
    public void testCountLinksWithStatus_UNWANTED()
    {
        assertLinksWithStatus(LinkStatus.UNWANTED, 3L);
    }

    @Test
    public void testCountLinksWithStatus_OUTDATED()
    {
        assertLinksWithStatus(LinkStatus.OUTDATED, 1L);
    }

    @Test
    public void testCountLinksWithStatus_PREDATED()
    {
        assertLinksWithStatus(LinkStatus.PREDATED, 1L);
    }

    @Test
    public void testCountLinsWithStatus_Ambiguous()
    {
        assertLinksWithStatus(LinkStatus.AMBIGUOUS, 2L);
    }

    @Test
    public void testCountLinksWithStatus_BROKEN()
    {
        assertLinksWithStatus(LinkStatus.BROKEN, 2L);
    }
}
