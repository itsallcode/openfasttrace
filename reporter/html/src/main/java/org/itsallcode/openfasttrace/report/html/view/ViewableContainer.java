package org.itsallcode.openfasttrace.report.html.view;

import java.util.List;

/**
 * A container for viewable elements
 */
public interface ViewableContainer extends Viewable
{
    /**
     * Get the ID of this view element
     * 
     * @return unique ID through which the element can be referenced
     */
    String getId();

    /**
     * Get the title of this view element
     * 
     * @return title of the view element
     */
    String getTitle();

    /**
     * Check if the view element can be referenced
     * 
     * @return <code>true</code> if the view element can be referenced
     */
    boolean isReferenceable();

    /**
     * Add a viewable element
     * 
     * @param child
     *            contained viewable element
     */
    void add(Viewable child);

    /**
     * Get the list of children of the viewable container
     * 
     * @return list of children
     */
    List<Viewable> getChildren();
}