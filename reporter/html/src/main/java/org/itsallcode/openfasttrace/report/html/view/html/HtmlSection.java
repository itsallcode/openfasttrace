package org.itsallcode.openfasttrace.report.html.view.html;

import java.io.PrintStream;

import org.itsallcode.openfasttrace.report.html.view.AbstractStreamableViewContainer;

/**
 * HTML variant of a report section
 */
class HtmlSection extends AbstractStreamableViewContainer
{
    /**
     * Create a new instance of a {@link HtmlSection}
     * 
     * @param stream
     *            stream the section is rendered to
     * @param id
     *            section ID
     * @param title
     *            section title
     */
    HtmlSection(final PrintStream stream, final String id, final String title)
    {
        super(stream, id, title);
    }

    @Override
    protected void renderBeforeChildren(final int level)
    {
        final String header = "h" + (level + 1);
        this.renderIndentation(level);
        this.stream.print("<section id=\"");
        this.stream.print(this.getId());
        this.stream.println("\">");
        this.renderIndentation(level);
        this.stream.print("  <");
        this.stream.print(header);
        this.stream.print(">");
        this.stream.print(this.getTitle());
        this.stream.print("</");
        this.stream.print(header);
        this.stream.println(">");
    }

    @Override
    protected void renderAfterChildren(final int level)
    {
        this.renderIndentation(level);
        this.stream.println("</section>");
    }
}
