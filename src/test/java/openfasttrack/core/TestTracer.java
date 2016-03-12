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

    /* @formatter:off
     *    |          Backward Link Status           | Needs Status
     *====#=========================================#=======================
     *    | OK | UNW | OUT | PRE | BRO | AMB || DUP | COV | UNC | OUT | PRE
     * A  |    |     |     |     |     |     ||     |   1 |     |     |
     * B  |  1 |     |     |     |     |     ||     |     |     |     |
     * C  |    |   1 |     |     |     |     ||     |     |     |     |
     * D  |    |     |     |     |     |     ||     |     |   1 |     |
     * E  |    |   2 |     |     |     |     ||     |     |     |     |
     * F  |    |     |     |     |   1 |     ||     |     |     |     |
     * G  |    |     |     |     |     |     ||     |   1 |   1 |     |
     * H  |    |     |  1  |     |     |     ||     |     |     |     |
     * I  |    |     |     |  1  |     |     ||     |     |     |     |
     * J  |  1 |     |     |     |   1 |     ||     |     |     |     |
     * K1 |    |     |     |     |     |     ||   1 |   1 |     |     |
     * K2 |    |     |     |     |     |     ||   1 |   1 |     |     |
     * L  |    |     |     |     |     |   1 ||     |     |     |     |
     * M1 |    |     |     |     |     |     ||   1 |     |     |     |  1
     * M2 |    |     |     |     |     |     ||   1 |     |     |   1 |
     * N  |    |     |     |     |     |   1 ||     |     |     |     |
     * @formatter:on
     */
    static
    {
        //
        //
        // @formatter:off
        A  = prepare("feat" , "A", 1).addNeedsArtifactType("req").build();
        B  = prepare("req"  , "B", 2).addCoveredId(A.getId()).build();
        C  = prepare("impl" , "C", 3).addCoveredId(A.getId()).build();
        D  = prepare("req"  , "D", 4).addNeedsArtifactType("impl").build();
        E  = prepare("uman" , "E", 5).addCoveredId(A.getId()).addCoveredId(B.getId()).build();
        F  = prepare("req"  , "F", 6).addCoveredId(NON_EXISTENT_ID).build();
        G  = prepare("req"  , "G", 7).addNeedsArtifactType("impl").addNeedsArtifactType("utest").build();
        H  = prepare("impl" , "H", 8).addCoveredId(OUTDATED_G_ID).build();
        I  = prepare("utest", "I", 9).addCoveredId(PREDATED_G_ID).build();
        J  = prepare("utest", "J", 1).addCoveredId(G.getId()).addCoveredId(NON_EXISTENT_ID).build();
        K1 = prepare("feat" , "K", 2).addNeedsArtifactType("req").build();
        K2 = prepare("feat" , "K", 2).addNeedsArtifactType("req").build();
        L  = prepare("req"  , "L", 3).addCoveredId(K1.getId()).build();
        M1 = prepare("req"  , "M", 4).addNeedsArtifactType("itest").build();
        M2 = prepare("req"  , "M", 6).addNeedsArtifactType("itest").build();
        N  = prepare("itest", "N", 7).addCoveredId(AMBIGUOUS_M_ID).build();
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

    // [utest~backward_coverage_status~1]
    @Test
    public void testCountBackwardinksWithStatus_OK()
    {
        assertBackwardLinksWithStatus(BackwardLinkStatus.OK, 2L);
    }

    private void assertBackwardLinksWithStatus(final BackwardLinkStatus status,
            final long expectedCount)
    {
        assertThat(this.trace.countBackwardLinksWithStatus(status), equalTo(expectedCount));
    }

    // [utest~backward_coverage_status~1]
    @Test
    public void testCountBackwardLinksWithStatus_UNWANTED()
    {
        assertBackwardLinksWithStatus(BackwardLinkStatus.UNWANTED, 3L);
    }

    // [utest~backward_coverage_status~1]
    @Test
    public void testCountBackwardLinksWithStatus_OUTDATED()
    {
        assertBackwardLinksWithStatus(BackwardLinkStatus.OUTDATED, 1L);
    }

    // [utest~backward_coverage_status~1]
    @Test
    public void testCountBackwardLinksWithStatus_PREDATED()
    {
        assertBackwardLinksWithStatus(BackwardLinkStatus.PREDATED, 1L);
    }

    // [utest~backward_coverage_status~1]
    @Test
    public void testCountBackwardLinksWithStatus_Ambiguous()
    {
        assertBackwardLinksWithStatus(BackwardLinkStatus.AMBIGUOUS, 2L);
    }

    // [utest~backward_coverage_status~1]
    @Test
    public void testCountBackwardLinksWithStatus_BROKEN()
    {
        assertBackwardLinksWithStatus(BackwardLinkStatus.ORPHANED, 2L);
    }

    // [utest~needed_coverage_status~1]
    @Test
    public void testCountNeedsCoverageWithStatus_COVERED()
    {
        assertNeedsCoverageWithStatus(NeedsCoverageLinkStatus.OK, 4L);
    }

    private void assertNeedsCoverageWithStatus(final NeedsCoverageLinkStatus status,
            final long expectedCount)
    {
        assertThat(this.trace.countNeedsCoverageLinksWithStatus(status), equalTo(expectedCount));
    }

    // [utest~needed_coverage_status~1]
    @Test
    public void testCountNeedsCoverageWithStatus_UNCOVERED()
    {
        assertNeedsCoverageWithStatus(NeedsCoverageLinkStatus.UNCOVERED, 2L);
    }

    // [utest~needed_coverage_status~1]
    @Test
    public void testCountNeedsCoverageWithStatus_OUTDATED()
    {
        assertNeedsCoverageWithStatus(NeedsCoverageLinkStatus.OUTDATED, 1L);
    }

    // [utest~needed_coverage_status~1]
    @Test
    public void testCountNeedsCoverageWithStatus_PREDATED()
    {
        assertNeedsCoverageWithStatus(NeedsCoverageLinkStatus.PREDATED, 1L);
    }

}