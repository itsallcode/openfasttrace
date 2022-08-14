package org.itsallcode.openfasttrace.report.html.view.html;
import java.io.PrintStream;

import org.itsallcode.openfasttrace.api.core.Trace;
import org.itsallcode.openfasttrace.report.html.view.IndentationHelper;
import org.itsallcode.openfasttrace.report.html.view.Viewable;

class HtmlTraceSummary implements Viewable
{
    private final PrintStream stream;
    private final Trace trace;

    HtmlTraceSummary(final PrintStream stream, final Trace trace)
    {
        this.stream = stream;
        this.trace = trace;
    }

    @Override
    public void render(final int level)
    {
        final String indentation = IndentationHelper.createIndentationPrefix(level);
        renderStart(indentation);
        renderStatusIndicator();
        renderTotalCount();
        renderCompletionIndicator();
        renderDefectCount();
        renderEnd();
    }

    protected void renderStart(final String indentation)
    {
        this.stream.print(indentation);
    }

    protected void renderStatusIndicator()
    {
        this.stream.print(this.trace.hasNoDefects() ? CharacterConstants.CHECK_MARK
                : CharacterConstants.CROSS_MARK);
        this.stream.print(" ");
    }

    protected void renderTotalCount()
    {
        this.stream.print(this.trace.count());
        this.stream.print(" total ");
    }

    protected void renderCompletionIndicator()
    {
        final int count = this.trace.count();
        if (count == 0)
        {
            this.stream.print("<meter>100%</meter>");
        }
        else
        {
            final int value = count - this.trace.countDefects();
            final int percent = 100 * value / count;
            this.stream.print("<meter value=\"");
            this.stream.print(value);
            if (value < count)
            {
                this.stream.print("\" low=\"");
                this.stream.print(count - 1);
            }
            this.stream.print("\" max=\"");
            this.stream.print(count);
            this.stream.print("\">");
            this.stream.print(percent);
            this.stream.print("%</meter>");
        }
    }

    private void renderDefectCount()
    {
        if (!this.trace.hasNoDefects())
        {
            this.stream.print(" <span class=\".red\">");
            this.stream.print(this.trace.countDefects());
            this.stream.print(" defects</span>");
        }
    }

    protected void renderEnd()
    {
        // intentionally left empty
    }
}