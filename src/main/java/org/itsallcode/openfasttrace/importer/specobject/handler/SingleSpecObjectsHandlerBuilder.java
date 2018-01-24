package org.itsallcode.openfasttrace.importer.specobject.handler;

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

import org.itsallcode.openfasttrace.core.SpecificationItemId.Builder;
import org.itsallcode.openfasttrace.core.xml.tree.CallbackContentHandler;
import org.itsallcode.openfasttrace.importer.ImportEventListener;

public class SingleSpecObjectsHandlerBuilder
{
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
        this.handler.addCharacterDataListener("id", this.idBuilder::name);
        this.handler.addIntDataListener("version", this.idBuilder::revision);
        this.handler.addCharacterDataListener("description", this.listener::appendDescription);
        this.handler.addCharacterDataListener("rationale", this.listener::appendRationale);
        this.handler.addCharacterDataListener("comment", this.listener::appendComment);

        this.handler.addSubTreeHandler("needscoverage",
                new NeedsCoverageHandlerBuilder(this.listener)::build);
        this.handler.addSubTreeHandler("providescoverage",
                new ProvidesCoverageHandlerBuilder(this.listener)::build);
        this.handler.addSubTreeHandler("dependencies",
                new DependenciesHandlerBuilder(this.listener)::build);
        return this.handler;
    }
}
