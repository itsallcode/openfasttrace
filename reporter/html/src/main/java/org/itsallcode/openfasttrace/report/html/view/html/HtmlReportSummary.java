package org.itsallcode.openfasttrace.report.html.view.html;

import java.io.PrintStream;

import org.itsallcode.openfasttrace.report.html.view.AbstractStreamableViewContainer;
import org.itsallcode.openfasttrace.report.html.view.IndentationHelper;

class HtmlReportSummary extends AbstractStreamableViewContainer
{
    HtmlReportSummary(final PrintStream stream)
    {
        super(stream);
    }

    @Override
    protected void renderBeforeChildren(final int level)
    {
        final String indentation = IndentationHelper.createIndentationPrefix(level);
        this.stream.print(indentation);
        this.stream.println("<nav>");
    }

    @Override
    protected void renderAfterChildren(final int level)
    {
        this.stream.println("</nav>");
    }
}