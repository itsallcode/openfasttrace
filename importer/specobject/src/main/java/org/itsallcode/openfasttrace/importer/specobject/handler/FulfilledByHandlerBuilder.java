package org.itsallcode.openfasttrace.importer.specobject.handler;

/*-
 * #%L
 * OpenFastTrace Specobject Importer
 * %%
 * Copyright (C) 2016 - 2019 itsallcode.org
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

import org.itsallcode.openfasttrace.importer.specobject.xml.tree.CallbackContentHandler;
import org.itsallcode.openfasttrace.importer.specobject.xml.tree.TreeContentHandler;

class FulfilledByHandlerBuilder
{
    private final CallbackContentHandler handler;

    FulfilledByHandlerBuilder()
    {
        this.handler = new CallbackContentHandler();
    }

    TreeContentHandler build()
    {
        this.handler.addSubTreeHandler("ffbObj", this::createFulfillByObjectHandler);
        return this.handler;
    }

    private CallbackContentHandler createFulfillByObjectHandler()
    {
        return new CallbackContentHandler()
                .addCharacterDataListener("ffbId", data -> {})
                .addCharacterDataListener("ffbType", data -> {})
                .addIntDataListener("ffbVersion", data -> {});
    }
}
