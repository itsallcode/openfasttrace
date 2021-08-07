package org.itsallcode.openfasttrace.report.html.view.html;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
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