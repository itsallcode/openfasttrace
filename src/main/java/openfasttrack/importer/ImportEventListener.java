package openfasttrack.importer;

import openfasttrack.core.SpecificationItemId;

/**
 * Interface for listeners of requirement import events
 *
 * Listeners register themselves at an importer. The importer issues an event
 * whenever it detects a new piece of a specification item.
 */
public interface ImportEventListener
{
    /**
     * The importer found a new specification item. The
     * {@link SpecificationItemId} must be defined using
     * {@link #setId(SpecificationItemId)}.
     */
    public void startSpecificationItem();

    /**
     * The importer found the ID of the specification item
     *
     * @param id
     *            the ID of the new item
     */
    public void setId(final SpecificationItemId id);

    /**
     * Append a text block to an item description
     *
     * @param descriptionFragment
     *            the text to be appended to the description
     */
    public void appendDescription(final String descriptionFragment);

    /**
     * Append a text block to the rationale
     *
     * @param rationaleFragment
     *            the text to be appended to the rationale
     */
    public void appendRationale(final String rationaleFragment);

    /**
     * Append a text block to the comment
     *
     * @param commentFragment
     *            the text to be appended to the comment
     */
    public void appendComment(final String commentFragment);

    /**
     * The importer found a reference that indicates coverage of another
     * specification item
     *
     * @param id
     *            the ID of the item that is covered
     */
    public void addCoveredId(final String id);

    /**
     * Add the ID of a specification item that this item depends on
     *
     * @param id
     *            the ID of the item depends on
     */
    public void addDependsOnId(final SpecificationItemId id);

    /**
     * The importer detected that the current specification object needs to be
     * covered by another specification item
     *
     * @param artifactType
     *            the type of specification item that is needed to provide
     *            coverage
     */
    public void addNeededArtifactType(final String artifactType);
}