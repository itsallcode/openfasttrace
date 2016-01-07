package openfasttrack.core;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

public class TestSpecificationItem
{
    final static SpecificationItemId ID = SpecificationItemId.parseId("req.foobar~1");
    final static String DESCRIPTION = "This is a description\nwith multiple lines";
    final static String RATIONALE = "A rationale\nwith multiple lines";
    final static String COMMENT = "A comment\nwith multiple lines";
    final static List<SpecificationItemId> COVERED_IDS = parseIds("feat.foo~1", "feat.bar~2",
            "constr.baz~3");
    final static List<String> NEEDED_ARTIFACT_TYPES = Arrays.asList(new String[] { "dsn", "uman" });
    final static List<SpecificationItemId> DEPEND_ON_IDS = parseIds("req.blah~4", "req.zoo~5");

    private static List<SpecificationItemId> parseIds(final String... ids)
    {
        return Arrays.asList(ids).stream().map(SpecificationItemId::parseId)
                .collect(Collectors.toList());
    }

    // [utest.requirement_format~1]
    @Test
    public void testBuildSimpleSpecificationItem()
    {
        final SpecificationItem.Builder builder = createSimpleItem();
        final SpecificationItem item = builder.build();
        assertSimpleItemComplete(item);
    }

    private SpecificationItem.Builder createSimpleItem()
    {
        final SpecificationItem.Builder builder = new SpecificationItem.Builder().id(ID);
        builder.description(DESCRIPTION).rationale(RATIONALE).comment(COMMENT);
        return builder;
    }

    private void assertSimpleItemComplete(final SpecificationItem item)
    {
        assertThat(item.getId(), equalTo(ID));
        assertThat(item.getDescription(), equalTo(DESCRIPTION));
        assertThat(item.getRationale(), equalTo(RATIONALE));
        assertThat(item.getComment(), equalTo(COMMENT));
    }

    @Test
    public void testBuildComplexSpecificationItem()
    {
        final SpecificationItem.Builder builder = createSimpleItem();
        for (final SpecificationItemId coveredId : COVERED_IDS)
        {
            builder.addCoveredId(coveredId);
        }
        for (final SpecificationItemId dependOnId : DEPEND_ON_IDS)
        {
            builder.addDependOnId(dependOnId);
        }
        for (final String neededArtifactType : NEEDED_ARTIFACT_TYPES)
        {
            builder.addNeededArtifactType(neededArtifactType);
        }
        final SpecificationItem item = builder.build();
        assertSimpleItemComplete(item);
        assertReationsComplete(item);
    }

    private void assertReationsComplete(final SpecificationItem item)
    {
        assertThat(item.getCoveredIds(), equalTo(COVERED_IDS));
        assertThat(item.getDependOnIds(), equalTo(DEPEND_ON_IDS));
        assertThat(item.getNeededArtifactTypes(), equalTo(NEEDED_ARTIFACT_TYPES));
    }
}