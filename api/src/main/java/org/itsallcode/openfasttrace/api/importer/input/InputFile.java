package org.itsallcode.openfasttrace.api.importer.input;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;

/**
 * This represents a file (either physical or virtual as a stream) that can be
 * read by an importer.
 */
public interface InputFile
{

    /**
     * Get a {@link BufferedReader} for reading the file.
     * 
     * @return a {@link BufferedReader} for reading the file.
     * @throws IOException
     *             when there is an error reading the file.
     */
    BufferedReader createReader() throws IOException;

    /**
     * Get a string representation of the path.
     * 
     * @return a string representation of the path.
     */
    String getPath();

    /**
     * Check if this {@link InputFile} is based on a real file on disk.
     * 
     * @return <code>true</code> if this {@link InputFile} is based on a real
     *         file.
     */
    boolean isRealFile();

    /**
     * Get the {@link Path} to the file when {@link #isRealFile()} is
     * <code>true</code>. Else throws an {@link UnsupportedOperationException}.
     * 
     * @return the {@link Path} to the file
     * @throws UnsupportedOperationException
     *             when this is not a real file.
     */
    Path toPath();
}
