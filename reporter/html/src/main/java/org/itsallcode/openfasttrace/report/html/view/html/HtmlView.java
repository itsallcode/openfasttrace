package org.itsallcode.openfasttrace.report.html.view.html;

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
import java.net.URL;

import org.itsallcode.openfasttrace.api.report.ReportException;
import org.itsallcode.openfasttrace.report.html.view.AbstractStreamableViewContainer;

/**
 * Single HTML page view
 */
public class HtmlView extends AbstractStreamableViewContainer
{
    private final URL cssURL;

    /**
     * Create a new instance of type {@link HtmlView}.
     * 
     * @param stream
     *            the stream to write to
     * 
     * @param id
     *            view ID
     * @param title
     *            view title
     * @param cssURL
     *            URL of the CSS stylesheet to be used in the HTML view
     */
    public HtmlView(final PrintStream stream, final String id, final String title, final URL cssURL)
    {
        super(stream, id, title);
        this.cssURL = cssURL;
    }

    @Override
    public void renderBeforeChildren(final int level)
    {
        this.stream.println("<!DOCTYPE html>");
        this.stream.println("<html>");
        this.stream.println("  <head>");
        this.stream.println("    <meta charset=\"UTF-8\">");
        this.stream.println("    <style>");
        inlineCSS();
        this.stream.println("    </style>");
        this.stream.print("    <title>");
        this.stream.print(this.getTitle());
        this.stream.println("</title>");
        this.stream.println("  </head>");
        this.stream.println("  <body>");
    }

    // [impl->dsn~reporting.html.inline_css~1]
    private void inlineCSS()
    {
        try (final InputStream css = this.cssURL.openStream())
        {
            final byte[] buffer = new byte[4096];
            int n;
            while ((n = css.read(buffer)) > 0)
            {
                this.stream.write(buffer, 0, n);
            }
        }
        catch (final IOException e)
        {
            throw new ReportException("Unable to copy CSS content \"" + this.cssURL.toString()
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