package org.itsallcode.openfasttrace.core.cli.commands;

/*-
 * #%L
 * OpenFastTrace Core
 * %%
 * Copyright (C) 2016 - 2022 itsallcode.org
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

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Handler for printing command line usage.
 */
public class HelpCommand implements Performable
{
    /** The command line action for running this command. */
    public static final String COMMAND_NAME = "help";

    @Override
    @SuppressWarnings("java:S106") // Using System.out by intention
    public boolean run()
    {
        final String usage = loadResource("/usage.txt");
        System.out.println(usage);
        return true;
    }

    private String loadResource(String resourceName)
    {
        try (InputStream stream = getResource(resourceName).openStream())
        {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
        catch (IOException exception)
        {
            throw new UncheckedIOException("Error loading resource", exception);
        }
    }

    private URL getResource(String resourceName)
    {
        final URL url = this.getClass().getResource(resourceName);
        if (url == null)
        {
            throw new IllegalStateException("Resource '" + resourceName + "' not found");
        }
        return url;
    }

}
