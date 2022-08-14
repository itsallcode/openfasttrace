package org.itsallcode.openfasttrace.report.html.view.html;

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
            .compile("^(?:file://|ftp://|mailto:|https?://).*");

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
        return path.replace("\\\\", "/");
    }
}
