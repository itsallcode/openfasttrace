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

import java.util.logging.Logger;

import openfasttrack.core.SpecificationItemId.Builder;
import openfasttrack.core.xml.tree.CallbackContentHandler;
import openfasttrack.importer.ImportEventListener;

public class SingleSpecObjectsHandlerBuilder
{
    private static final Logger LOG = Logger
            .getLogger(SingleSpecObjectsHandlerBuilder.class.getName());

    private final CallbackContentHandler handler;
    private final ImportEventListener listener;

    private final Builder idBuilder;

    public SingleSpecObjectsHandlerBuilder(final ImportEventListener listener,
            final Builder idBuilder)
    {
        this.listener = listener;
        this.idBuilder = idBuilder;
        this.handler = new CallbackContentHandler();
    }

    public CallbackContentHandler build()
    {
        this.handler.addCharacterDataListener("id", data -> {
            this.idBuilder.name(data);
        });
        this.handler.addCharacterDataListener("version",
                data -> this.idBuilder.revision(Integer.parseInt(data)));

        this.handler.addCharacterDataListener("description",
                data -> this.listener.appendDescription(data));
        this.handler.addCharacterDataListener("rationale",
                data -> this.listener.appendRationale(data));
        this.handler.addCharacterDataListener("comment", data -> this.listener.appendComment(data));

        this.handler.addElementListener("needscoverage", elem -> this.handler
                .pushDelegate(new NeedsCoverageHandlerBuilder(this.listener).build()));

        this.handler.addElementListener("providescoverage", elem -> this.handler
                .pushDelegate(new ProvidesCoverageHandlerBuilder(this.listener).build()));

        this.handler.addElementListener("dependencies", elem -> this.handler
                .pushDelegate(new DependenciesHandlerBuilder(this.listener).build()));

        return this.handler;
    }
}
