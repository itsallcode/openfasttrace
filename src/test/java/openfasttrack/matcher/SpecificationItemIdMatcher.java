package openfasttrack.matcher;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsEmptyIterable;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;

import openfasttrack.core.SpecificationItemId;

public class SpecificationItemIdMatcher extends BaseTypeSafeDiagnosingMatcher<SpecificationItemId>
{
    private final Matcher<String> artifactType;
    private final Matcher<String> name;
    private final Matcher<Integer> revision;

    public SpecificationItemIdMatcher(final SpecificationItemId expected)
    {
        super(expected);
        this.artifactType = Matchers.equalTo(expected.getArtifactType());
        this.name = Matchers.equalTo(expected.getName());
        this.revision = Matchers.equalTo(expected.getRevision());
    }

    @Override
    protected void describeTo(final DescriptionBuilder description)
    {
        description //
                .append("artifactType", this.artifactType) //
                .append("name", this.name) //
                .append("revision", this.revision);
    }

    @Override
    protected void reportMismatches(final SpecificationItemId actual,
            final MismatchReporter mismatchReporter)
    {
        mismatchReporter //
                .checkMismatch("artifactType", this.artifactType, actual.getArtifactType()) //
                .checkMismatch("name", this.name, actual.getName()) //
                .checkMismatch("revision", this.revision, actual.getRevision());
    }

    @Factory
    public static BaseMatcher<SpecificationItemId> equalTo(final SpecificationItemId id)
    {
        return new SpecificationItemIdMatcher(id);
    }

    /**
     * Factory method for a matcher that matches an {@link Iterable} of
     * {@link SpecificationItemId}s in any order.
     *
     * @param expected
     *            the expected ids.
     * @return a id matcher.
     */
    @Factory
    public static Matcher<Iterable<? extends SpecificationItemId>> equalIds(
            final Collection<SpecificationItemId> expected)
    {
        if (expected.isEmpty())
        {
            return IsEmptyIterable.<SpecificationItemId> emptyIterable();
        }
        final List<Matcher<? super SpecificationItemId>> matchers = expected.stream()
                .map(SpecificationItemIdMatcher::equalTo) //
                .collect(Collectors.toList());
        return IsIterableContainingInAnyOrder.containsInAnyOrder(matchers);
    }
}
