package openfasttrack.importer.specobject;

/*-
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 - 2017 hamstercommunity
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

import openfasttrack.core.xml.tree.CallbackContentHandler;
import openfasttrack.core.xml.tree.TreeContentHandler;
import openfasttrack.importer.ImportEventListener;

public class NeedsCoverageHandlerBuilder
{
    private final ImportEventListener listener;
    private final CallbackContentHandler handler;

    public NeedsCoverageHandlerBuilder(final ImportEventListener listener)
    {
        this.listener = listener;
        this.handler = new CallbackContentHandler();
    }

    public TreeContentHandler build()
    {
        this.handler.addCharacterDataListener("needsobj", data -> {
            this.listener.addNeededArtifactType(data);
        });

        return this.handler;
    }
}
