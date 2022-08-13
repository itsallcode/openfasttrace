package org.itsallcode.openfasttrace.report.html.view.html;

import java.io.PrintStream;

import org.itsallcode.openfasttrace.report.html.view.Viewable;
import org.itsallcode.openfasttrace.report.html.view.ViewableContainer;

class HtmlTableOfContents implements Viewable
{

    private final PrintStream stream;
    private final ViewableContainer from;

    HtmlTableOfContents(final PrintStream outputStream, final ViewableContainer from)
    {
        this.stream = outputStream;
        this.from = from;
    }

    @Override
    public void render(final int level)
    {
        boolean first = true;
        for (final Viewable view : this.from.getChildren())
        {
            renderTableOfContentsItem(view, first);
            first = false;
        }
    }

    protected void renderTableOfContentsItem(final Viewable view, final boolean first)
    {
        if (view instanceof ViewableContainer)
        {
            renderContainerItem(view, first);
        }
    }

    protected void renderContainerItem(final Viewable view, final boolean first)
    {
        final ViewableContainer container = (ViewableContainer) view;
        if (container.isReferenceable())
        {
            renderSeparator(first);
            renderLinkWithText(container);
        }
    }

    protected void renderSeparator(final boolean first)
    {
        this.stream.print(first ? " | " : " &middot; ");
    }

    protected void renderLinkWithText(final ViewableContainer container)
    {
        this.stream.print("<a href=\"#");
        this.stream.print(container.getId());
        this.stream.print("\">");
        this.stream.print(container.getTitle());
        this.stream.print("</a>");
    }
}