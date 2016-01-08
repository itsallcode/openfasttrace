package openfasttrack.importer;

import java.util.ArrayList;
import java.util.List;

import openfasttrack.core.SpecificationItem;
import openfasttrack.core.SpecificationItemId;

/**
 * The {@link SpecificationItemListBuilder} consumes import events and generates
 * a list of specification items from them.
 */
public class SpecificationItemListBuilder implements ImportEventListener
{
    private final List<SpecificationItem> items = new ArrayList<>();
    private SpecificationItem.Builder itemBuilder = null;
    private StringBuilder description = new StringBuilder();
    private StringBuilder rationale = new StringBuilder();
    private StringBuilder comment = new StringBuilder();

    @Override
    public void beginSpecificationItem()
    {
        this.itemBuilder = new SpecificationItem.Builder();
    }

    private void resetState()
    {
        this.itemBuilder = null;
        this.description = new StringBuilder();
        this.rationale = new StringBuilder();
        this.comment = new StringBuilder();
    }

    @Override
    public void setId(final SpecificationItemId id)
    {
        this.itemBuilder.id(id);
    }

    @Override
    public void addCoveredId(final SpecificationItemId id)
    {
        this.itemBuilder.addCoveredId(id);
    }

    @Override
    public void appendDescription(final String descriptionFragment)
    {
        this.description.append(descriptionFragment);
    }

    @Override
    public void appendRationale(final String rationaleFragment)
    {
        this.rationale.append(rationaleFragment);
    }

    @Override
    public void appendComment(final String commentFragment)
    {
        this.comment.append(commentFragment);
    }

    @Override
    public void addDependsOnId(final SpecificationItemId id)
    {
        this.itemBuilder.addDependOnId(id);
    }

    @Override
    public void addNeededArtifactType(final String artifactType)
    {
        this.itemBuilder.addNeededArtifactType(artifactType);
    }

    /**
     * Build the list of specification items
     *
     * @return the list of specification items collected up to this point
     */
    public List<SpecificationItem> build()
    {
        this.createNewSpecificationItem();
        return this.items;
    }

    @Override
    public void setTitle(final String title)
    {
        this.itemBuilder.title(title);
    }

    @Override
    public void endSpecificationItem()
    {
        if (this.itemBuilder != null)
        {
            createNewSpecificationItem();
        }
    }

    private void createNewSpecificationItem()
    {
        this.itemBuilder //
                .description(this.description.toString()) //
                .rationale(this.rationale.toString()) //
                .comment(this.comment.toString());
        this.items.add(this.itemBuilder.build());
        resetState();
    }
}
