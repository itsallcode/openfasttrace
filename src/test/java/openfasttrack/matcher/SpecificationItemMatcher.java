package openfasttrack.matcher;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import openfasttrack.core.SpecificationItem;
import openfasttrack.core.SpecificationItemId;

public class SpecificationItemMatcher extends BaseTypeSafeDiagnosingMatcher<SpecificationItem>
{
    private final BaseMatcher<SpecificationItemId> id;
    private final Matcher<String> comment;
    private final Matcher<String> description;
    private final Matcher<String> rationale;
    private final Matcher<Iterable<? extends SpecificationItemId>> coveredIds;
    private final Matcher<Iterable<? extends SpecificationItemId>> dependsOnIds;
    private final Matcher<Iterable<? extends String>> neededArtifactTypes;

    public SpecificationItemMatcher(final SpecificationItem expected)
    {
        super(expected);
        this.id = SpecificationItemIdMatcher.equalTo(expected.getId());
        this.comment = Matchers.equalTo(expected.getComment());
        this.description = Matchers.equalTo(expected.getDescription());
        this.rationale = Matchers.equalTo(expected.getRationale());
        this.coveredIds = SpecificationItemIdMatcher.equalIds(expected.getCoveredIds());
        this.dependsOnIds = SpecificationItemIdMatcher.equalIds(expected.getDependOnIds());
        this.neededArtifactTypes = Matchers
                .containsInAnyOrder(expected.getNeededArtifactTypes().toArray(new String[0]));
    }

    @Override
    protected void describeTo(final DescriptionBuilder description)
    {
        description //
                .append("id", this.id) //
                .append("comment", this.comment) //
                .append("description", this.description) //
                .append("rationale", this.rationale) //
                .append("coveredIds", this.coveredIds) //
                .append("dependsOnIds", this.dependsOnIds) //
                .append("neededArtifactTypes", this.neededArtifactTypes);
    }

    @Override
    protected void reportMismatches(final SpecificationItem actual,
            final MismatchReporter mismatchReporter)
    {
        mismatchReporter //
                .checkMismatch("id", this.id, actual.getId()) //
                .checkMismatch("comment", this.comment, actual.getComment()) //
                .checkMismatch("description", this.description, actual.getDescription()) //
                .checkMismatch("rationale", this.rationale, actual.getRationale()) //
                .checkMismatch("coveredIds", this.coveredIds, actual.getCoveredIds()) //
                .checkMismatch("dependsOnIds", this.dependsOnIds, actual.getDependOnIds()) //
                .checkMismatch("neededArtifactTypes", this.neededArtifactTypes,
                        actual.getNeededArtifactTypes());
    }

    @Factory
    public static BaseMatcher<SpecificationItem> equalTo(final SpecificationItem item)
    {
        return new SpecificationItemMatcher(item);
    }
}
