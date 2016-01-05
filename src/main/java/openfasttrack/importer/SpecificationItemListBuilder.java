package openfasttrack.importer;

import java.util.ArrayList;
import java.util.List;

import openfasttrack.core.SpecificationItem;

public class SpecificationItemListBuilder implements ImportEventListener
{
    private final List<SpecificationItem> items = new ArrayList<>();

    @Override
    public void foundNewSpecificationItem(final String id)
    {

    }

    @Override
    public void setDescription(final String description)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void addCoverage(final String id)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void addNeeded(final String specificationItemType)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void appendDescription(final String string)
    {
        // TODO Auto-generated method stub

    }

    public List<SpecificationItem> build()
    {
        return null;
    }
}
