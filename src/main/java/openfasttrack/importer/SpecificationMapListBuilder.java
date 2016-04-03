package openfasttrack.importer;

import java.util.LinkedList;
import java.util.List;

import openfasttrack.core.SpecificationItem;
import openfasttrack.core.SpecificationItemId;

/**
 * The {@link SpecificationMapListBuilder} consumes import events and generates
 * a map of specification items from them. The key to the map is the
 * specification item ID.
 */
public class SpecificationMapListBuilder implements ImportEventListener
{
    private final List<SpecificationItem> items = new LinkedList<>();
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
        this.itemBuilder.addNeedsArtifactType(artifactType);
    }

    /**
     * Build the list of specification items
     *
     * @return the list of specification items collected up to this point
     */
    public List<SpecificationItem> build()
    {
        this.endSpecificationItem();
        return this.items;
    }

    public int getItemCount()
    {
        return this.items.size();
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
        final SpecificationItem item = this.itemBuilder.build();
        this.items.add(item);
        resetState();
    }
}
