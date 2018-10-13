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

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.itsallcode.openfasttrace.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.exporter.ExporterException;
import org.itsallcode.openfasttrace.report.view.AbstractViewFactory;
import org.itsallcode.openfasttrace.report.view.Viewable;
import org.itsallcode.openfasttrace.report.view.ViewableContainer;

public class HtmlViewFactory extends AbstractViewFactory
{
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final URL cssUrl;

    HtmlViewFactory(final PrintStream stream, final URL cssUrl)
    {
        super(stream);
        this.cssUrl = cssUrl;
    }

    public static HtmlViewFactory create(final OutputStream stream, final URL cssURL)
    {
        return new HtmlViewFactory(createPrintStream(stream, DEFAULT_CHARSET), cssURL);
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
    public ViewableContainer createSection(final String id, final String title)
    {
        return new HtmlSection(this.outputStream, id, title);
    }

    @Override
    public Viewable createSpecificationItem(final LinkedSpecificationItem item)
    {
        return new HtmlSpecificationItem(this.outputStream, item);
    }
}
