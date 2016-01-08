package openfasttrack.core;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

public class TestSpecificationItemId
{

    @Test
    public void testParseId_singleDigitRevision()
    {
        final SpecificationItemId id = SpecificationItemId.parseId("feat~foo~1");
        assertThat(id.getArtifactType(), equalTo("feat"));
        assertThat(id.getName(), equalTo("foo"));
        assertThat(id.getRevision(), equalTo(1));
    }

    @Test
    public void testParseId_multipleFragmentName()
    {
        final SpecificationItemId id = SpecificationItemId.parseId("feat~foo.bar_zoo.baz~1");
        assertThat(id.getArtifactType(), equalTo("feat"));
        assertThat(id.getName(), equalTo("foo.bar_zoo.baz"));
        assertThat(id.getRevision(), equalTo(1));
    }

    @Test
    public void testParseId_multipleDigitRevision()
    {
        final SpecificationItemId id = SpecificationItemId.parseId("feat~foo~999");
        assertThat(id.getArtifactType(), equalTo("feat"));
        assertThat(id.getName(), equalTo("foo"));
        assertThat(id.getRevision(), equalTo(999));
    }

    @Test
    public void testParseId_mustFailForIllegalIds()
    {
        final String[] negatives = { "feat.foo~1", "foo~1", "req~foo" };

        for (final String sample : negatives)
        {
            assertParsingExceptionOnIllegalSpecificationItemId(sample);
        }
    }

    private void assertParsingExceptionOnIllegalSpecificationItemId(final String sample)
    {
        try
        {
            SpecificationItemId.parseId(sample);
            fail("Expected exception trying to parse \"" + sample
                    + "\" into a specification item ID");
        } catch (final IllegalStateException exception)
        {
            // this block intentionally left empty
        }
    }

    @Test
    public void testToString()
    {
        final SpecificationItemId.Builder builder = new SpecificationItemId.Builder();
        builder.artifactType("dsn").name("dummy").revision(3);
        assertThat(builder.build().toString(), equalTo("dsn~dummy~3"));
    }
}
