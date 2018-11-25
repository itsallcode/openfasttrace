package org.itsallcode.openfasttrace.report.view.html;

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

import org.itsallcode.openfasttrace.core.Trace;
import org.itsallcode.openfasttrace.report.view.Viewable;

public class HtmlTraceSummary implements Viewable
{

    private final PrintStream stream;
    private final Trace trace;

    public HtmlTraceSummary(final PrintStream stream, final Trace trace)
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
        if (this.trace.hasNoDefects() || this.trace.count() == 0)
        {
            this.stream.print(CharacterConstants.FULL_CIRCLE);
        }
        else
        {
            final int completion = 100 * (this.trace.count() - this.trace.countDefects())
                    / this.trace.count();
            if (completion >= 75)
            {
                this.stream.print(CharacterConstants.THREE_QUARTERS_CIRCLE);
            }
            else if (completion < 75 && completion >= 50)
            {
                this.stream.print(CharacterConstants.HALF_CIRCLE);
            }
            else if (completion < 50 && completion >= 25)
            {
                this.stream.print(CharacterConstants.QUATER_CIRCLE);
            }
            else
            {
                this.stream.print(CharacterConstants.EMPTY_CIRCLE);
            }
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
    }
}