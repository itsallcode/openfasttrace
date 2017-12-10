package openfasttrack.core.xml.callback;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

import openfasttrack.core.xml.ContentHandlerAdapterController;
import openfasttrack.core.xml.EndElementEvent;
import openfasttrack.core.xml.EventContentHandler;
import openfasttrack.core.xml.StartElementEvent;

public class CallbackBasedContentHandler implements EventContentHandler
{
    private static final Logger LOG = Logger.getLogger(CallbackBasedContentHandler.class.getName());

    private ContentHandlerAdapterController contentHandlerAdapter;

    private final Map<String, Consumer<StartElementEvent>> startElementListeners = new HashMap<>();
    private final Consumer<StartElementEvent> defaultStartElementHandler;
    private final Map<String, Consumer<EndElementEvent>> endElementListeners = new HashMap<>();
    private final Consumer<EndElementEvent> defaultEndElementHandler;
    private StringBuilder characterData;

    public CallbackBasedContentHandler()
    {
        this(startElement -> LOG.warning("Unexpected start element " + startElement),
                endElement -> LOG.warning("Unexpected end element " + endElement));
    }

    private CallbackBasedContentHandler(
            final Consumer<StartElementEvent> defaultStartElementHandler,
            final Consumer<EndElementEvent> defaultEndElementHandler)
    {
        this.defaultStartElementHandler = defaultStartElementHandler;
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
            consumer.accept(this.characterData.toString());
            this.characterData = null;
        });
    }

    public void delegateTo(final EventContentHandler subDelegate)
    {
        this.contentHandlerAdapter.delegateToSubHandler(subDelegate);
    }

    @Override
    public void startElement(final StartElementEvent event)
    {
        LOG.finest(() -> "Start element: " + event);
        this.startElementListeners
                .getOrDefault(event.getName().getLocalPart(), this.defaultStartElementHandler)
                .accept(event);
    }

    @Override
    public void endElement(final EndElementEvent event)
    {
        LOG.finest(() -> "End element: " + event);
        this.endElementListeners
                .getOrDefault(event.getName().getLocalPart(), this.defaultEndElementHandler)
                .accept(event);
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
            LOG.fine(() -> "Ignoring character data '"
                    + characters.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t")
                    + "'");
        }
    }

    @Override
    public void init(final ContentHandlerAdapterController contentHandlerAdapter)
    {
        this.contentHandlerAdapter = contentHandlerAdapter;
    }
}
