package org.itsallcode.openfasttrace.report.view.html;

import java.io.IOException;
import java.io.InputStream;

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

import org.itsallcode.openfasttrace.report.ReportException;
import org.itsallcode.openfasttrace.report.view.AbstractViewContainer;
import org.itsallcode.openfasttrace.report.view.Viewable;

/**
 * Single HTML page view
 */
public class HtmlView extends AbstractViewContainer implements Viewable
{
    private final String title;
    private final PrintStream stream;
    private final boolean inlineCSS;
    private static final String REPORT_CSS_FILE = "/css/report.css";

    /**
     * Create a new instance of type {@link HtmlView}.
     * 
     * @param stream
     *            the stream to write to
     * 
     * @param id
     *            the view ID
     * @param title
     *            the view title
     */
    public HtmlView(final PrintStream stream, final String id, final String title,
            final boolean inlineCSS)
    {
        this.stream = stream;
        this.title = title;
        this.inlineCSS = inlineCSS;
    }

    /**
     * Create a new instance of type {@link HtmlView}.
     * 
     * @param stream
     *            the stream to write to
     * 
     * @param id
     *            the view ID
     * @param title
     *            the view title
     */
    public HtmlView(final PrintStream stream, final String id, final String title)
    {
        this(stream, id, title, true);
    }

    @Override
    public void renderBeforeChildren(final int level)
    {
        this.stream.println("<!DOCTYPE html>");
        this.stream.println("<html>");
        this.stream.println("  <head>");
        this.stream.println("    <meta charset=\"UTF-8\">");
        inlineCSS();
        this.stream.print("    <title>");
        this.stream.print(this.title);
        this.stream.println("</title>");
        this.stream.println("  </head>");
        this.stream.println("  <body>");
    }

    // [impl->dsn~reporting.html.inline_css~1]
    private void inlineCSS()
    {
        if (this.inlineCSS)
        {
            this.stream.println("    <style>");
            final InputStream css = getClass().getResourceAsStream(REPORT_CSS_FILE);
            if (css == null)
            {
                throw new ReportException("Unable open CSS stylesheet \"" + REPORT_CSS_FILE
                        + "\" trying to generate HTML view.");
            }
            copyCSSContent(css);
            this.stream.println("    </style>");
        }
    }

    private void copyCSSContent(final InputStream css) throws ReportException
    {
        final byte[] buffer = new byte[4096];
        int n;
        try
        {
            while ((n = css.read(buffer)) > 0)
            {
                this.stream.write(buffer, 0, n);
            }
        }
        catch (final IOException e)
        {
            throw new ReportException("Unable to copy CSS content \"" + REPORT_CSS_FILE
                    + "\" trying to generate HTML view.", e);
        }
    }

    @Override
    public void renderAfterChildren(final int level)
    {
        this.stream.println("  </body>");
        this.stream.print("</html>");
    }
}
