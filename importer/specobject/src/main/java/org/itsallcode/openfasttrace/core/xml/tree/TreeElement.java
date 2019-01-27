package org.itsallcode.openfasttrace.core.xml.tree;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2017 itsallcode.org
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

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.itsallcode.openfasttrace.core.Location;
import org.itsallcode.openfasttrace.core.xml.event.Attribute;
import org.itsallcode.openfasttrace.core.xml.event.StartElementEvent;

public class TreeElement
{
    private final StartElementEvent element;
    private final StringBuilder characterData = new StringBuilder();
    private final List<Consumer<TreeElement>> endElementListeners = new LinkedList<>();
    private final TreeElement parent;

    public TreeElement(final StartElementEvent element, final TreeElement parent)
    {
        this.element = element;
        this.parent = parent;
    }

    public StartElementEvent getElement()
    {
        return this.element;
    }

    public String getCharacterData()
    {
        return this.characterData.toString();
    }

    public boolean isRootElement()
    {
        return this.parent == null;
    }

    void addCharacterData(final String characters)
    {
        this.characterData.append(characters);
    }

    void setEndElementListener(final Consumer<TreeElement> newEndElementListener)
    {
        this.endElementListeners.add(newEndElementListener);
    }

    @Override
    public String toString()
    {
        return "TreeElement [element=" + this.element + ", characterData=" + this.characterData
                + ", endElementListeners=" + this.endElementListeners + "]";
    }

    public Attribute getAttributeValueByName(final String name)
    {
        return this.element.getAttributeValueByName(name);
    }

    public Location getLocation()
    {
        return this.element.getLocation();
    }

    public void invokeEndElementListeners()
    {
        this.endElementListeners.forEach(listener -> listener.accept(this));
    }
}
