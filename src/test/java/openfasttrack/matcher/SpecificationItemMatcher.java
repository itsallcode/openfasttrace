package openfasttrack.matcher;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Factory;
import org.hamcrest.Matchers;

import openfasttrack.core.SpecificationItem;
import openfasttrack.matcher.config.ConfigurableMatcher;
import openfasttrack.matcher.config.MatcherConfig;

public class SpecificationItemMatcher extends ConfigurableMatcher<SpecificationItem>
{
    public SpecificationItemMatcher(final SpecificationItem expected)
    {
        super(MatcherConfig.builder(expected) //
                .addGenericProperty("id", SpecificationItem::getId, SpecificationItemIdMatcher::equalTo) //
                .addStringProperty("comment", SpecificationItem::getComment) //
                .addStringProperty("description", SpecificationItem::getDescription) //
                .addStringProperty("rationale", SpecificationItem::getRationale) //
                .addIterableProperty("coveredIds", SpecificationItem::getCoveredIds,
                        SpecificationItemIdMatcher::equalTo) //
                .addIterableProperty("dependsOnIds", SpecificationItem::getDependOnIds,
                        SpecificationItemIdMatcher::equalTo) //
                .addIterableProperty("neededArtifactTypes",
                        SpecificationItem::getNeededArtifactTypes, Matchers::equalTo) //
                .build());
    }

    @Factory
    public static BaseMatcher<SpecificationItem> equalTo(final SpecificationItem item)
    {
        return new SpecificationItemMatcher(item);
    }
}
