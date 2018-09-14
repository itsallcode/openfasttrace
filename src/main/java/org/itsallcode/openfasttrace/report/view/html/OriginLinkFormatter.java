package org.itsallcode.openfasttrace.report.view.html;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import org.itsallcode.openfasttrace.core.Location;

public final class OriginLinkFormatter
{
    private static final Pattern PROTOCOL_PREFIX_PATTERN = Pattern
            .compile("^file://|ftp://|mailto:|https?://.*");

    private OriginLinkFormatter()
    {
        // prevent instantiation.
    }

    public static String format(final Location location)
    {
        if (location == null)
        {
            return "";
        }
        final String path = location.getPath();
        final StringBuilder builder = new StringBuilder();
        URI uri = null;
        boolean validUri = false;
        if (PROTOCOL_PREFIX_PATTERN.matcher(path).matches())
        {
            try
            {
                uri = URI.create(path);
                validUri = true;
            }
            catch (final IllegalArgumentException e)
            {
                validUri = false;
            }
        }
        else
        {
            final Path realPath = Paths.get(unWindowsify(path));
            if (realPath.isAbsolute())
            {
                uri = realPath.toUri();
                validUri = true;
            }
            else
            {
                validUri = false;
            }
        }
        if (validUri)
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
        builder.append(location.getLine());
        return builder.toString();
    }

    private static String unWindowsify(final String path)
    {
        return path.replaceAll("([A-Z]:)", "/$1").replaceAll("\\\\", "/");
    }
}
