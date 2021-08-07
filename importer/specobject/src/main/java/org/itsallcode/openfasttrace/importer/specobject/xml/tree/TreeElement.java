package org.itsallcode.openfasttrace.importer.specobject.xml.tree;

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

import org.itsallcode.openfasttrace.api.core.Location;
import org.itsallcode.openfasttrace.importer.specobject.xml.event.Attribute;
import org.itsallcode.openfasttrace.importer.specobject.xml.event.StartElementEvent;

/**
 * An element node in a parsed XML tree.
 */
public class TreeElement
{
    private final StartElementEvent element;
    private final StringBuilder characterData = new StringBuilder();
    private final List<Consumer<TreeElement>> endElementListeners = new LinkedList<>();
    private final TreeElement parent;

    TreeElement(final StartElementEvent element, final TreeElement parent)
    {
        this.element = element;
        this.parent = parent;
    }

    /**
     * @return the {@link StartElementEvent}.
     */
    public StartElementEvent getElement()
    {
        return this.element;
    }

    /**
     * @return the character data content of the element.
     */
    public String getCharacterData()
    {
        return this.characterData.toString();
    }

    /**
     * @return {@code true} if this is the root element.
     */
    public boolean isRootElement()
    {
        return this.parent == null;
    }

    void addCharacterData(final String characters)
    {
        this.characterData.append(characters);
    }

    void addEndElementListener(final Consumer<TreeElement> newEndElementListener)
    {
        this.endElementListeners.add(newEndElementListener);
    }

    @Override
    public String toString()
    {
        return "TreeElement [element=" + this.element + ", characterData=" + this.characterData
                + ", endElementListeners=" + this.endElementListeners + "]";
    }

    /**
     * Get an {@link Attribute} by it's name.
     * 
     * @param name
     *            the attribute name.
     * @return the {@link Attribute} or {@code null} if no attribute exists.
     */
    public Attribute getAttributeValueByName(final String name)
    {
        return this.element.getAttributeValueByName(name);
    }

    /**
     * @return the {@link Location} of the start element.
     */
    public Location getLocation()
    {
        return this.element.getLocation();
    }

    /**
     * Invokes the registered end element listeners.
     */
    public void invokeEndElementListeners()
    {
        this.endElementListeners.forEach(listener -> listener.accept(this));
    }
}
