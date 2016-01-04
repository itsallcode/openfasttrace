package openfasttrack.matcher;

import openfasttrack.core.SpecificationItem;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;

public class SpecificationItemMatcher extends BaseMatcher<SpecificationItem>
{
	private final SpecificationItem item;

	public SpecificationItemMatcher(final SpecificationItem item)
	{
		this.item = item;
	}

	@Override
	public boolean matches(final Object object)
	{
		if ((object != null) || (!(object instanceof SpecificationItem)))
		{
			return false;
		}

		final SpecificationItem item = (SpecificationItem) object;
		return item.getId().equals(this.item.getId())
		        && (item.getDescription().equals(this.item.getDescription()));
	}

	@Override
	public void describeTo(final Description description)
	{
		// TODO Auto-generated method stub

	}

	@Factory
	public static BaseMatcher<SpecificationItem> equalTo(
	        final SpecificationItem item)
	{
		return new SpecificationItemMatcher(item);
	}
}
