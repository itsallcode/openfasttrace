package openfasttrack.matcher;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import openfasttrack.core.SpecificationItem;
import openfasttrack.matcher.config.ConfigurableMatcher;
import openfasttrack.matcher.config.MatcherConfig;

/**
 * {@link Matcher} for {@link SpecificationItem}
 */
public class SpecificationItemMatcher extends ConfigurableMatcher<SpecificationItem>
{
    private SpecificationItemMatcher(final SpecificationItem expected)
    {
        super(MatcherConfig.builder(expected) //
                .addProperty("id", SpecificationItem::getId, SpecificationItemIdMatcher::equalTo) //
                .addEqualsProperty("comment", SpecificationItem::getComment) //
                .addEqualsProperty("description", SpecificationItem::getDescription) //
                .addEqualsProperty("rationale", SpecificationItem::getRationale) //
                .addIterableProperty("coveredIds", SpecificationItem::getCoveredIds,
                        SpecificationItemIdMatcher::equalTo) //
                .addIterableProperty("dependsOnIds", SpecificationItem::getDependOnIds,
                        SpecificationItemIdMatcher::equalTo) //
                .addIterableProperty("neededArtifactTypes",
                        SpecificationItem::getNeedsArtifactTypes, Matchers::equalTo) //
                .build());
    }

    @Factory
    public static Matcher<SpecificationItem> equalTo(final SpecificationItem item)
    {
        return new SpecificationItemMatcher(item);
    }
}
