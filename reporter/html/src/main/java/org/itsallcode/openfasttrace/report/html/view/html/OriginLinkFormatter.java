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

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import org.itsallcode.openfasttrace.api.core.Location;

/**
 * This class renders {@link Location} object as HTML spans or blocks.
 */
public final class OriginLinkFormatter
{
    private static final Pattern PROTOCOL_PREFIX_PATTERN = Pattern
            .compile("^file://|ftp://|mailto:|https?://.*");

    private OriginLinkFormatter()
    {
        // prevent instantiation.
    }

    /**
     * Render an origin to an HTML span
     * 
     * @param location
     *            origin location
     * @return a string representing an HTML span containing a link to the
     *         origin
     */
    public static String formatAsSpan(final Location location)
    {
        return format(location, true);
    }

    /**
     * Render an origin to an HTML block
     * 
     * @param location
     *            origin location
     * @return a string representing an HTML block containing a link to the
     *         origin
     */
    public static String formatAsBlock(final Location location)
    {
        return format(location, false);
    }

    private static String format(final Location location, final boolean asSpan)
    {
        if ((location != null) && !location.getPath().isEmpty())
        {
            return formatNonEmptyLocation(location, asSpan);
        }
        else
        {
            return "";
        }
    }

    private static String formatNonEmptyLocation(final Location location, final boolean asSpan)
    {
        final String path = location.getPath();
        final URI uri = checkPathHasProtocol(path) ? convertPathWithProtocolToUri(path)
                : convertPathWithoutProtocolToUri(path);
        final StringBuilder builder = new StringBuilder();
        builder.append(asSpan ? "<span" : "<p");
        builder.append(" class=\"origin\">");
        if (uri != null)
        {
            builder.append("<a href=\"");
            builder.append(uri);
            builder.append("\">");
            builder.append(path);
            builder.append("</a>");
        }
        else
        {
            builder.append(path);
        }
        builder.append(":");
        final int line = location.getLine();
        builder.append(line);
        builder.append(asSpan ? "</span>" : "</p>");
        return builder.toString();
    }

    private static boolean checkPathHasProtocol(final String path)
    {
        return PROTOCOL_PREFIX_PATTERN.matcher(path).matches();
    }

    private static URI convertPathWithProtocolToUri(final String path)
    {
        URI uri;
        try
        {
            uri = URI.create(path);
        }
        catch (final IllegalArgumentException e)
        {
            uri = null;
        }
        return uri;
    }

    private static URI convertPathWithoutProtocolToUri(final String path)
    {
        final Path realPath = Paths.get(unWindowsify(path));
        if (realPath.isAbsolute())
        {
            return realPath.toUri();
        }
        return null;
    }

    private static String unWindowsify(final String path)
    {
        return path.replaceAll("\\\\", "/");
    }
}
