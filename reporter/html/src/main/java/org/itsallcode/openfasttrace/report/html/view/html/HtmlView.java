package org.itsallcode.openfasttrace.report.html.view.html;

import java.io.*;
import java.net.URL;

import org.itsallcode.openfasttrace.api.report.ReportException;
import org.itsallcode.openfasttrace.report.html.view.AbstractStreamableViewContainer;

/**
 * Single HTML page view
 */
class HtmlView extends AbstractStreamableViewContainer
{
    private final URL cssURL;

    /**
     * Create a new instance of type {@link HtmlView}.
     * 
     * @param stream
     *            the stream to write to
     * @param id
     *            view ID
     * @param title
     *            view title
     * @param cssURL
     *            URL of the CSS stylesheet to be used in the HTML view
     */
    HtmlView(final PrintStream stream, final String id, final String title, final URL cssURL)
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
