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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

import org.itsallcode.openfasttrace.importer.ImporterException;

public class CallbackContentHandler implements TreeContentHandler
{
    private static final Logger LOG = Logger.getLogger(CallbackContentHandler.class.getName());

    private final Map<String, Consumer<TreeElement>> startElementListeners = new HashMap<>();

    private Consumer<TreeElement> defaultStartElementListener;
    private TreeParsingController treeParsingController;

    public void setDefaultStartElementListener(
            final Consumer<TreeElement> defaultSTartElementListener)
    {
        this.defaultStartElementListener = defaultSTartElementListener;
    }

    public CallbackContentHandler addSubTreeHandler(final String elementName,
            final Supplier<TreeContentHandler> supplier)
    {
        addElementListener(elementName, element -> this.pushDelegate(supplier.get()));
        return this;
    }

    public CallbackContentHandler addElementListener(final String elementName,
            final Consumer<TreeElement> startElementConsumer)
    {
        this.addElementListener(elementName, startElementConsumer, null);
        return this;
    }

    public CallbackContentHandler addElementListener(final String elementName,
            final Consumer<TreeElement> startElementConsumer,
            final Consumer<TreeElement> endElementConsumer)
    {
        if (this.startElementListeners.containsKey(elementName))
        {
            throw new IllegalArgumentException(
                    "Listener already registered for start element " + elementName);
        }
        this.startElementListeners.put(elementName, startElement -> {
            if (endElementConsumer != null)
            {
                startElement.setEndElementListener(endElementConsumer);
            }
            startElementConsumer.accept(startElement);
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

    public void stopParsing()
    {
        this.treeParsingController.stopParsing();
    }

    public void pushDelegate(final TreeContentHandler delegate)
    {
        this.treeParsingController.setDelegate(delegate);
        this.treeParsingController.getCurrentElement()
                .setEndElementListener(endElement -> this.treeParsingController.setDelegate(this));
    }

    public CallbackContentHandler addIntDataListener(final String elementName,
            final Consumer<Integer> listener)
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

    public CallbackContentHandler addCharacterDataListener(final String elementName,
            final Consumer<String> listener)
    {
        addElementListener(elementName, startElement -> {},
                endElement -> listener.accept(endElement.getCharacterData()));
        return this;
    }
}
