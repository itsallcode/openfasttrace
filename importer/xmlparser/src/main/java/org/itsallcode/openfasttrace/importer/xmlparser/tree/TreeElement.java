package org.itsallcode.openfasttrace.importer.xmlparser.tree;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.itsallcode.openfasttrace.api.core.Location;
import org.itsallcode.openfasttrace.importer.xmlparser.event.Attribute;
import org.itsallcode.openfasttrace.importer.xmlparser.event.StartElementEvent;

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
     * Get the {@link StartElementEvent}.
     * 
     * @return the {@link StartElementEvent}.
     */
    public StartElementEvent getElement()
    {
        return this.element;
    }

    /**
     * Get the character data content of the element.
     * 
     * @return the character data content of the element.
     */
    public String getCharacterData()
    {
        return this.characterData.toString();
    }

    /**
     * Check if this is the root element.
     * 
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
     * Get the {@link Location} of the start element.
     * 
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

    @Override
    public String toString()
    {
        return "TreeElement [element=" + this.element + ", characterData=" + this.characterData
                + ", endElementListeners=" + this.endElementListeners + ", parent=" + this.parent + "]";
    }
}
