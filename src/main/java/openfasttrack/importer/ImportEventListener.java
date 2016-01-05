package openfasttrack.importer;

/**
 * Interface for listeners of requirement import events
 *
 * Listeners register themselves at an importer. The importer issues an event
 * whenever it detects a new piece of a specification item.
 */
public interface ImportEventListener
{
    /**
     * The importer found a new specification item ID
     *
     * @param id
     *            the ID of the new item
     */
    public void foundNewSpecificationItem(final String id);

    /**
     * The importer extracted the description of the specification item
     *
     * @param description
     *            the extracted description
     */
    public void setDescription(final String description);

    /**
     * The importer found a reference that indicates coverage of another
     * specification item
     *
     * @param id
     *            the ID of the item that is covered.
     */
    public void addCoverage(final String id);

    /**
     * The importer detected that the current specification object needs to be
     * covered by another specification item
     *
     * @param specificationItemType
     *            the type of specification item that is needed to provide
     *            coverage
     */
    public void addNeeded(final String specificationItemType);

    /**
     * Append a text block to an item description
     *
     * @param string
     *            the text to be appended to the description
     */
    public void appendDescription(String string);
}