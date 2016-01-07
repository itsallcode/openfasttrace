package openfasttrack.matcher;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.collection.IsEmptyIterable;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;

import openfasttrack.core.SpecificationItemId;
import openfasttrack.matcher.config.BaseConfigurableMatcher;
import openfasttrack.matcher.config.MatcherConfig;

public class SpecificationItemIdMatcher extends BaseConfigurableMatcher<SpecificationItemId>
{

    public SpecificationItemIdMatcher(final SpecificationItemId expected)
    {
        super(MatcherConfig.builder(expected) //
                .addStringProperty("artifactType", SpecificationItemId::getArtifactType) //
                .addStringProperty("name", SpecificationItemId::getName) //
                .addIntProperty("revision", SpecificationItemId::getRevision) //
                .build());
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
