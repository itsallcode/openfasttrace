package org.itsallcode.openfasttrace.api.importer;

import org.itsallcode.openfasttrace.api.core.*;

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
     * {@link #setId(LocatedSpecificationItemId)}.
     */
    void beginSpecificationItem();

    /**
     * The importer found the ID of the specification item
     *
     * @param id
     *            the ID of the new item
     * @deprecated Use {@link #setId(LocatedSpecificationItemId)} instead.
     */
    @Deprecated(since = "4.9.0", forRemoval = true)
    void setId(final SpecificationItemId id);

    /**
     * The importer found the declared ID together with its source occurrence.
     *
     * @param id
     *            located ID occurrence
     */
    default void setId(final LocatedSpecificationItemId id)
    {
        setId(id.getId());
    }

    /**
     * The importer found the title of a specification item
     *
     * @param title
     *            the title
     */
    void setTitle(String title);

    /**
     * The importer found the status of the specification item
     *
     * @param status
     *            the status
     */
    void setStatus(ItemStatus status);

    /**
     * Append a text block to an item description
     *
     * @param fragment
     *            the text to be appended to the description
     */
    void appendDescription(final String fragment);

    /**
     * Append a text block to the rationale
     *
     * @param fragment
     *            the text to be appended to the rationale
     */
    void appendRationale(final String fragment);

    /**
     * Append a text block to the comment
     *
     * @param fragment
     *            the text to be appended to the comment
     */
    void appendComment(final String fragment);

    /**
     * The importer found a reference that indicates coverage of another
     * specification item
     *
     * @param id
     *            the ID of the item that is covered
     * @deprecated Use {@link #addCoveredId(LocatedSpecificationItemId)} instead.
     */
    @Deprecated(since = "4.9.0", forRemoval = true)
    void addCoveredId(final SpecificationItemId id);

    /**
     * Add a covered ID together with its source occurrence.
     *
     * @param id
     *            located covered ID occurrence
     */
    default void addCoveredId(final LocatedSpecificationItemId id)
    {
        addCoveredId(id.getId());
    }

    /**
     * Add the ID of a specification item that this item depends on
     *
     * @param id
     *            the ID of the item depends on
     * @deprecated Use {@link #addDependsOnId(LocatedSpecificationItemId)} instead.
     */
    @Deprecated(since = "4.9.0", forRemoval = true)
    void addDependsOnId(final SpecificationItemId id);

    /**
     * Add a dependency ID together with its source occurrence.
     *
     * @param id
     *            located dependency ID occurrence
     */
    default void addDependsOnId(final LocatedSpecificationItemId id)
    {
        addDependsOnId(id.getId());
    }

    /**
     * The importer detected that the current specification object needs to be
     * covered by another specification item
     *
     * @param artifactType
     *            the type of specification item that is needed to provide
     *            coverage
     */
    void addNeededArtifactType(final String artifactType);

    /**
     * Add a tag
     *
     * @param tag
     *            the tag
     */
    void addTag(String tag);

    /**
     * Set the location of the specification item in the imported file
     *
     * @param path
     *            the path of the imported file
     * @param line
     *            the current line number
     */
    void setLocation(final String path, int line);

    /**
     * The importer detected the end of a specification item
     */
    void endSpecificationItem();

    /**
     * Set the location of the specification item in the imported file
     *
     * @param location
     *            the location
     */
    void setLocation(Location location);

    /**
     * Set to {@code true} if the specification item forwards needed coverage
     *
     * @param forwards
     *            {@code true} if the specification item forwards needed
     *            coverage
     */
    void setForwards(boolean forwards);

    /**
     * Add a complete specification item to the list of items being built. Use
     * this method if the specification item is already complete and does not
     * need to be built from events, e.g. when the item is specified in a single
     * line and does not span multiple lines in the input file.
     *
     * @param item
     *            the complete specification item
     */
    void addSpecificationItem(final SpecificationItem item);
}
