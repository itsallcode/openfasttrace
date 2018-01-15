package openfasttrack.importer;

/*-
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 - 2018 hamstercommunity
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

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public abstract class RegexMatchingImporterFactory extends ImporterFactory
{
    private static final Logger LOG = Logger.getLogger(ImporterFactory.class.getName());

    private final Set<Pattern> supportedFilenamePatterns;

    protected RegexMatchingImporterFactory(final String... supportedFilenamePatterns)
    {
        this(asList(supportedFilenamePatterns));
    }

    protected RegexMatchingImporterFactory(final Collection<String> supportedFileExtensions)
    {
        this.supportedFilenamePatterns = supportedFileExtensions.stream() //
                .map(Pattern::compile) //
                .collect(toSet());
    }

    /**
     * Returns <code>true</code> if this {@link ImporterFactory} supports importing
     * the given file based on its file extension.
     *
     * @param file
     *            the file to check.
     * @return <code>true</code> if the given file is supported for importing.
     */
    @Override
    public boolean supportsFile(final Path file)
    {
        final String fileName = file.getFileName().toString();
        for (final Pattern pattern : this.supportedFilenamePatterns)
        {
            if (pattern.matcher(fileName).matches())
            {
                LOG.finest(() -> "Filename '" + fileName + "' matches '" + pattern
                        + "': supported  by " + this.getClass().getName());
                return true;
            }
        }
        LOG.finest(() -> "Filename '" + fileName + "' does not match any regexp of "
                + this.supportedFilenamePatterns + ": not supported by "
                + this.getClass().getName());
        return false;
    }
}
