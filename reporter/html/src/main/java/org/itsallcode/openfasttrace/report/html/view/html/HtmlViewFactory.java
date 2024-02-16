package org.itsallcode.openfasttrace.report.html.view.html;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.itsallcode.openfasttrace.api.DetailsSectionDisplay;
import org.itsallcode.openfasttrace.api.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.api.core.Trace;
import org.itsallcode.openfasttrace.api.exporter.ExporterException;
import org.itsallcode.openfasttrace.report.html.view.*;

/**
 * Factory that creates an HTML OFT view (e.g. for reports) and provides an
 * output stream.
 */
public class HtmlViewFactory extends AbstractViewFactory
{
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final URL cssUrl;
    private final DetailsSectionDisplay foldingStatus;

    private HtmlViewFactory(final PrintStream stream, final URL cssUrl, final DetailsSectionDisplay foldingStatus)
    {
        super(stream);
        this.cssUrl = cssUrl;
        this.foldingStatus = foldingStatus;
    }

    /**
     * Create a new instance.
     * 
     * @param stream
     *            the output stream.
     * @param cssURL
     *            the URL of the CSS file to include in the HTML report.
     * @param foldingStatus
     *            the folding status of the {@code &lt;details&gt;} element.
     * @return a new {@link HtmlViewFactory}.
     */
    public static HtmlViewFactory create(final OutputStream stream, final URL cssURL,
            final DetailsSectionDisplay foldingStatus)
    {
        return new HtmlViewFactory(createPrintStream(stream), cssURL, foldingStatus);
    }

    private static PrintStream createPrintStream(final OutputStream stream)
    {
        if (stream instanceof PrintStream)
        {
            return (PrintStream) stream;
        }
        else
        {
            return createPrintStream(stream, DEFAULT_CHARSET);
        }
    }

    private static PrintStream createPrintStream(final OutputStream stream, final Charset charset)
    {
        try
        {
            return new PrintStream(stream, false, charset.name());
        }
        catch (final UnsupportedEncodingException e)
        {
            throw new ExporterException("Error creating print stream for charset " + charset, e);
        }
    }

    @Override
    public ViewableContainer createView(final String id, final String title)
    {
        return new HtmlView(this.outputStream, id, title, this.cssUrl);
    }

    @Override
    public ViewableContainer createReportDetails()
    {
        return new HtmlReportDetails(this.outputStream);
    }

    @Override
    public ViewableContainer createSection(final String id, final String title)
    {
        return new HtmlSection(this.outputStream, id, title);
    }

    @Override
    public Viewable createSpecificationItem(final LinkedSpecificationItem item)
    {
        return new HtmlSpecificationItem(this.outputStream, item, foldingStatus);
    }

    @Override
    public Viewable createTraceSummary(final Trace trace)
    {
        return new HtmlTraceSummary(this.outputStream, trace);
    }

    @Override
    public ViewableContainer createReportSummary()
    {
        return new HtmlReportSummary(this.outputStream);
    }

    @Override
    public Viewable createTableOfContents(final ViewableContainer from)
    {
        return new HtmlTableOfContents(this.outputStream, from);
    }
}
