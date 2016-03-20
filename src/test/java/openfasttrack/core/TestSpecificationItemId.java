package openfasttrack.core;

import static openfasttrack.core.SpecificationItemId.createId;
import static openfasttrack.core.SpecificationItemId.parseId;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

import openfasttrack.core.SpecificationItemId.Builder;

public class TestSpecificationItemId
{
    private static final int REVISION = 1;
    private static final String NAME = "foo";
    private static final String ARTIFACT_TYPE_FEATURE = "feat";

    @Test
    public void testCreateId()
    {
        final SpecificationItemId id = createId(ARTIFACT_TYPE_FEATURE, NAME, REVISION);
        assertThat(id, equalTo(new Builder().artifactType(ARTIFACT_TYPE_FEATURE).name(NAME)
                .revision(REVISION).build()));
    }

    @Test
    public void testParseId_singleDigitRevision()
    {
        final SpecificationItemId id = parseId("feat~foo~1");
        assertThat(id.getArtifactType(), equalTo(ARTIFACT_TYPE_FEATURE));
        assertThat(id.getName(), equalTo(NAME));
        assertThat(id.getRevision(), equalTo(1));
    }

    @Test
    public void testParseId_multipleFragmentName()
    {
        final SpecificationItemId id = parseId("feat~foo.bar_zoo.baz~1");
        assertThat(id.getArtifactType(), equalTo(ARTIFACT_TYPE_FEATURE));
        assertThat(id.getName(), equalTo("foo.bar_zoo.baz"));
        assertThat(id.getRevision(), equalTo(1));
    }

    @Test
    public void testParseId_multipleDigitRevision()
    {
        final SpecificationItemId id = parseId("feat~foo~999");
        assertThat(id.getArtifactType(), equalTo(ARTIFACT_TYPE_FEATURE));
        assertThat(id.getName(), equalTo(NAME));
        assertThat(id.getRevision(), equalTo(999));
    }

    @Test(expected = IllegalStateException.class)
    public void testParseIdFailsForWildcardRevision()
    {
        parseId("feat~foo~" + SpecificationItemId.REVISION_WILDCARD);
    }

    @Test
    public void testParseId_mustFailForIllegalIds()
    {
        final String[] negatives = { "feat.foo~1", "foo~1", "req~foo", "req1~foo~1", "req.r~foo~1",
                "req~1foo~1", "req~.foo~1", "req~foo~-1" };

        for (final String sample : negatives)
        {
            assertParsingExceptionOnIllegalSpecificationItemId(sample);
        }
    }

    private void assertParsingExceptionOnIllegalSpecificationItemId(final String sample)
    {
        try
        {
            parseId(sample);
            fail("Expected exception trying to parse \"" + sample
                    + "\" into a specification item ID");
        }
        catch (final IllegalStateException exception)
        {
            // this block intentionally left empty
        }
    }

    @Test
    public void testToRevisionWildcard()
    {
        final SpecificationItemId id = parseId("feat~foo~999");
        assertThat(id.toRevisionWildcard().getRevision(),
                equalTo(SpecificationItemId.REVISION_WILDCARD));
    }

    @Test
    public void testToString()
    {
        final Builder builder = new Builder();
        builder.artifactType("dsn").name("dummy").revision(3);
        assertThat(builder.build().toString(), equalTo("dsn~dummy~3"));
    }

    @Test
    public void testToStringRevisionWildcard()
    {
        final Builder builder = new Builder();
        builder.artifactType("dsn").name("dummy").revisionWildcard();
        assertThat(builder.build().toString(),
                equalTo("dsn~dummy~" + SpecificationItemId.REVISION_WILDCARD));
    }

    public void testCreate_WithRevisionWildcard()
    {
        final SpecificationItemId id = createId(ARTIFACT_TYPE_FEATURE, NAME);
        assertThat(id.getArtifactType(), equalTo(ARTIFACT_TYPE_FEATURE));
        assertThat(id.getName(), equalTo(NAME));
        assertThat(id.getRevision(), equalTo(SpecificationItemId.REVISION_WILDCARD));
    }
}