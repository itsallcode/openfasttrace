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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

import org.itsallcode.openfasttrace.api.importer.ImporterException;

/**
 * A convenient {@link TreeContentHandler} that allows registering listeners for
 * specific elements.
 */
public class CallbackContentHandler implements TreeContentHandler
{
    private static final Logger LOG = Logger.getLogger(CallbackContentHandler.class.getName());

    private final Map<String, Consumer<TreeElement>> startElementListeners = new HashMap<>();

    private Consumer<TreeElement> defaultStartElementListener;
    private TreeParsingController treeParsingController;

    /**
     * Sets the default start element listener that is called when no other
     * listener matches.
     * 
     * @param defaultStartElementListener
     *            the default start element listener.
     */
    public void setDefaultStartElementListener(
            final Consumer<TreeElement> defaultStartElementListener)
    {
        this.defaultStartElementListener = defaultStartElementListener;
    }

    /**
     * Adds a {@link TreeContentHandler} that will process the elements in the
     * sub tree.
     * 
     * @param elementName
     *            the element name for which to register the listener.
     * @param supplier
     *            the supplier for the content handler.
     * @return this instance for method chaining.
     */
    public CallbackContentHandler addSubTreeHandler(final String elementName,
            final Supplier<TreeContentHandler> supplier)
    {
        addElementListener(elementName, element -> this.pushDelegate(supplier.get()));
        return this;
    }

    /**
     * Adds a start element listener for elements with a given name. The
     * listener will be called when an element with the given name is found.
     * 
     * @param elementName
     *            the element name for which to register the listener.
     * @param startElementEventListener
     *            the listener.
     * @return this instance for method chaining.
     */
    public CallbackContentHandler addElementListener(final String elementName,
            final Consumer<TreeElement> startElementEventListener)
    {
        this.addElementListener(elementName, startElementEventListener, null);
        return this;
    }

    /**
     * Adds start and end element listener for elements with a given name. The
     * listener will be called when an element with the given name is found.
     * 
     * @param elementName
     *            the element name for which to register the listener.
     * @param startElementListener
     *            the start element listener.
     * @param endElementListener
     *            the end element listener.
     * @return this instance for method chaining.
     */
    public CallbackContentHandler addElementListener(final String elementName,
            final Consumer<TreeElement> startElementListener,
            final Consumer<TreeElement> endElementListener)
    {
        if (this.startElementListeners.containsKey(elementName))
        {
            throw new IllegalArgumentException(
                    "Listener already registered for start element " + elementName);
        }
        this.startElementListeners.put(elementName, startElement -> {
            if (endElementListener != null)
            {
                startElement.addEndElementListener(endElementListener);
            }
            startElementListener.accept(startElement);
        });
        return this;
    }

    @Override
    public void init(final TreeParsingController treeParsingController)
    {
        this.treeParsingController = treeParsingController;
    }

    @Override
    public void startElement(final TreeElement treeElement)
    {
        LOG.finest(() -> "Start element: " + treeElement);
        final Consumer<TreeElement> consumer = this.startElementListeners.getOrDefault(
                treeElement.getElement().getName().getLocalPart(),
                this.defaultStartElementListener);
        if (consumer == null)
        {
            LOG.warning(() -> "No consumer for event " + treeElement);
            return;
        }
        try
        {
            consumer.accept(treeElement);
        }
        catch (final Exception e)
        {
            throw new ImporterException("Error handling " + treeElement + " with consumer "
                    + consumer + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void endElement(final TreeElement closedElement)
    {
        closedElement.invokeEndElementListeners();
    }

    /**
     * Stop parsing, e.g. in case of a parsing error.
     */
    public void stopParsing()
    {
        this.treeParsingController.stopParsing();
    }

    /**
     * Pushes the given {@link TreeContentHandler} as a delegate.
     * 
     * @param delegate
     *            the new delegate.
     */
    public void pushDelegate(final TreeContentHandler delegate)
    {
        this.treeParsingController.setDelegate(delegate);
        this.treeParsingController.getCurrentElement()
                .addEndElementListener(endElement -> this.treeParsingController.setDelegate(this));
    }

    /**
     * Add a listener for elements with integer content.
     * 
     * @param elementName
     *            the element name.
     * @param listener
     *            the listener.
     * @return this instance for method chaining.
     */
    public CallbackContentHandler addIntDataListener(final String elementName,
            final IntConsumer listener)
    {
        addCharacterDataListener(elementName, data -> {
            if (data == null || data.isEmpty())
            {
                throw new IllegalStateException("No string data found for element " + elementName);
            }
            final int parsedInt = Integer.parseInt(data);
            listener.accept(parsedInt);
        });
        return this;
    }

    /**
     * Add a listener for elements with string content.
     * 
     * @param elementName
     *            the element name.
     * @param listener
     *            the listener.
     * @return this instance for method chaining.
     */
    public CallbackContentHandler addCharacterDataListener(final String elementName,
            final Consumer<String> listener)
    {
        addElementListener(elementName, startElement -> {},
                endElement -> listener.accept(endElement.getCharacterData()));
        return this;
    }
}
