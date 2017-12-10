package openfasttrack.core.xml.callback;

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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

import openfasttrack.core.xml.ContentHandlerAdapterController;
import openfasttrack.core.xml.EndElementEvent;
import openfasttrack.core.xml.EventContentHandler;
import openfasttrack.core.xml.StartElementEvent;
import openfasttrack.importer.ImporterException;

public class CallbackBasedContentHandler implements EventContentHandler
{
    private static final Logger LOG = Logger.getLogger(CallbackBasedContentHandler.class.getName());

    private ContentHandlerAdapterController contentHandlerAdapter;

    private final Map<String, Consumer<StartElementEvent>> startElementListeners = new HashMap<>();
    private Consumer<StartElementEvent> defaultStartElementHandler;
    private final Map<String, Consumer<EndElementEvent>> endElementListeners = new HashMap<>();
    private Consumer<EndElementEvent> defaultEndElementHandler;
    private StringBuilder characterData;

    public void setDefaultStartElementHandler(
            final Consumer<StartElementEvent> defaultStartElementHandler)
    {
        this.defaultStartElementHandler = defaultStartElementHandler;
    }

    public void setDefaultEndElementHandler(
            final Consumer<EndElementEvent> defaultEndElementHandler)
    {
        this.defaultEndElementHandler = defaultEndElementHandler;
    }

    public void addStartElementListener(final String elementName,
            final Consumer<StartElementEvent> consumer)
    {
        if (this.startElementListeners.containsKey(elementName))
        {
            throw new IllegalArgumentException(
                    "Listener already registered for start element " + elementName);
        }
        this.startElementListeners.put(elementName, consumer);
    }

    public void addEndElementListener(final String elementName,
            final Consumer<EndElementEvent> consumer)
    {
        if (this.endElementListeners.containsKey(elementName))
        {
            throw new IllegalArgumentException(
                    "Listener already registered for end element " + elementName);
        }
        this.endElementListeners.put(elementName, consumer);
    }

    public void addCharacterDataListener(final String elementName, final Consumer<String> consumer)
    {
        addStartElementListener(elementName, (elem) -> {
            if (this.characterData != null)
            {
                throw new IllegalArgumentException("Already reading character data");
            }
            this.characterData = new StringBuilder();
        });
        addEndElementListener(elementName, event -> {
            try
            {
                consumer.accept(this.characterData.toString());
            }
            catch (final Exception e)
            {
                throw new ImporterException("Error handling character data '"
                        + replaceWhitespace(this.characterData.toString()) + "' with consumer "
                        + consumer + ": " + e.getMessage(), e);
            }
            this.characterData = null;
        });
    }

    public void stopParsing()
    {
        this.contentHandlerAdapter.parsingFinished();
    }

    @Override
    public void startElement(final StartElementEvent event)
    {
        LOG.finest(() -> "Start element: " + event);
        final Consumer<StartElementEvent> consumer = this.startElementListeners
                .getOrDefault(event.getName().getLocalPart(), this.defaultStartElementHandler);
        if (consumer == null)
        {
            LOG.warning("No default consumer for event " + event);
            return;
        }
        try
        {
            consumer.accept(event);
        }
        catch (final Exception e)
        {
            throw new ImporterException("Error handling " + event + " with consumer " + consumer
                    + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void endElement(final EndElementEvent event)
    {
        LOG.finest(() -> "End element: " + event);
        final Consumer<EndElementEvent> consumer = this.endElementListeners
                .getOrDefault(event.getName().getLocalPart(), this.defaultEndElementHandler);
        if (consumer == null)
        {
            LOG.warning("No default consumer for event " + event);
            return;
        }
        try
        {
            consumer.accept(event);
        }
        catch (final Exception e)
        {
            throw new ImporterException("Error handling " + event + " with consumer " + consumer
                    + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void characters(final String characters)
    {
        if (this.characterData != null)
        {
            this.characterData.append(characters);
        }
        else
        {
            LOG.fine(() -> "Ignoring character data '" + replaceWhitespace(characters) + "'");
        }
    }

    private static String replaceWhitespace(final String characters)
    {
        return characters.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    @Override
    public void init(final ContentHandlerAdapterController contentHandlerAdapter)
    {
        this.contentHandlerAdapter = contentHandlerAdapter;
    }
}
