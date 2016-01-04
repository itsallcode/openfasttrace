package openfasttrack.core;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TestSpecificationItem
{

    @Test
    public void testBuild()
    {
        final String ID = "req:foobar";
        final String ARTIFACT_TYPE = "dsn";
        final String DESCRIPTION = "This is a description";
        final SpecificationItem.Builder builder = new SpecificationItem.Builder(
                ID);
        builder.artifactType(ARTIFACT_TYPE).description(DESCRIPTION);
        final SpecificationItem item = builder.build();
        assertThat(item.getId(), equalTo(ID));
        assertThat(item.getDescription(), equalTo(DESCRIPTION));
        assertThat(item.getArtifactType(), equalTo(ARTIFACT_TYPE));
    }
}
