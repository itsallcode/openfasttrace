package org.itsallcode.openfasttrace.report.html.view;

/**
 * Interface for all view elements which know how to render themselves
 */
public interface Viewable
{
    /**
     * Render the viewable element.
     */
    default void render()
    {
        render(0);
    }

    /**
     * Render the viewable element on given indentation level.
     * 
     * @param level
     *            indentation level
     */
    void render(int level);
}
