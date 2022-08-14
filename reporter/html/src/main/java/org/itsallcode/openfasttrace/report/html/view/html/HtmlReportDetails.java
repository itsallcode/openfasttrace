package org.itsallcode.openfasttrace.report.html.view.html;

import java.io.PrintStream;

import org.itsallcode.openfasttrace.report.html.view.AbstractStreamableViewContainer;

class HtmlReportDetails extends AbstractStreamableViewContainer
{
    HtmlReportDetails(final PrintStream stream)
    {
        super(stream);
    }

    @Override
    protected void renderBeforeChildren(final int level)
    {
        renderIndentation(level);
        this.stream.println("<main>");
    }

    @Override
    protected void renderAfterChildren(final int level)
    {
        renderIndentation(level);
        this.stream.println("</main>");
    }
}