package org.itsallcode.openfasttrace.report.html.view;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all containers of viewable elements
 */
public abstract class AbstractViewContainer implements ViewableContainer
{
    private final List<Viewable> children;
    private final String id;
    private final String title;

    /**
     * Create a new instance of type {@link AbstractViewContainer}.
     */
    protected AbstractViewContainer()
    {
        this(null, null);
    }

    /**
     * Create a new instance of type {@link AbstractViewContainer}.
     * 
     * @param id
     *            unique container ID
     * @param title
     *            container title
     */
    protected AbstractViewContainer(final String id, final String title)
    {
        this.id = id;
        this.title = title;
        this.children = new ArrayList<>();
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public String getTitle()
    {
        return this.title;
    }

    @Override
    public boolean isReferenceable()
    {
        return this.id != null;
    }

    @Override
    public void render(final int level)
    {
        renderBeforeChildren(level);
        renderChildren(level);
        renderAfterChildren(level);
    }

    /**
     * Render a the part of the view that comes before the children.
     * 
     * @param level
     *            indentation level
     */
    protected abstract void renderBeforeChildren(final int level);

    /**
     * Render a the children of this sub(view).
     * 
     * @param level
     *            indentation level
     */
    protected void renderChildren(final int level)
    {
        for (final Viewable child : this.children)
        {
            child.render(level + 1);
        }
    }

    /**
     * Render a the part of the view that comes after the children.
     * 
     * @param level
     *            indentation level
     */
    protected abstract void renderAfterChildren(final int level);

    /*
     * (non-Javadoc)
     * 
     * @see org.itsallcode.openfasttrace.report.view.ViewableContainer#add(org.
     * itsallcode. openfasttrace.view.Viewable)
     */
    @Override
    public void add(final Viewable child)
    {
        this.children.add(child);
    }

    @Override
    public List<Viewable> getChildren()
    {
        return this.children;
    }
}
