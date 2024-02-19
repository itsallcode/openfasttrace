package org.itsallcode.openfasttrace.importer.xmlparser.tree;

import java.util.HashMap;
import java.util.Map;
import java.util.function.*;
import java.util.logging.Logger;

import org.itsallcode.openfasttrace.importer.xmlparser.XmlParserException;

/**
 * A convenient {@link TreeContentHandler} that allows registering listeners for
 * specific elements.
 */
public class CallbackContentHandler implements TreeContentHandler {
    private static final Logger LOG = Logger.getLogger(CallbackContentHandler.class.getName());
    private static final String OPENFASTTRACE_XML_NAMESPACE = "https://github.com/itsallcode/openfasttrace";

    private final Map<String, Consumer<TreeElement>> startElementListeners = new HashMap<>();

    private Consumer<TreeElement> defaultStartElementListener;
    private TreeParsingController treeParsingController;

    /**
     * Create a new {@link CallbackContentHandler}.
     */
    public CallbackContentHandler() {
        // empty by intention
    }

    /**
     * Sets the default start element listener that is called when no other
     * listener matches.
     *
     * @param defaultListener the default start element listener.
     */
    public void setDefaultStartElementListener(
            final Consumer<TreeElement> defaultListener) {
        this.defaultStartElementListener = defaultListener;
    }

    /**
     * Adds a {@link TreeContentHandler} that will process the elements in the
     * sub tree.
     *
     * @param elementName the element name for which to register the listener.
     * @param supplier    the supplier for the content handler.
     * @return this instance for method chaining.
     */
    public CallbackContentHandler addSubTreeHandler(final String elementName,
            final Supplier<TreeContentHandler> supplier) {
        this.addElementListener(elementName, element -> this.pushDelegate(supplier.get()));
        return this;
    }

    /**
     * Adds a start element listener for elements with a given name. The
     * listener will be called when an element with the given name is found.
     *
     * @param elementName the element name for which to register the
     *                    listener.
     * @param listener    the start element listener.
     * @return this instance for method chaining.
     */
    public CallbackContentHandler addElementListener(final String elementName,
            final Consumer<TreeElement> listener) {
        this.addElementListener(elementName, listener, null);
        return this;
    }

    /**
     * Adds start and end element listener for elements with a given name. The
     * listener will be called when an element with the given name is found.
     *
     * @param elementName   the element name for which to register the listeners.
     * @param startListener the start element listener.
     * @param endListener   the end element listener.
     * @return this instance for method chaining.
     */
    public CallbackContentHandler addElementListener(final String elementName,
            final Consumer<TreeElement> startListener,
            final Consumer<TreeElement> endListener) {
        if (this.startElementListeners.containsKey(elementName)) {
            throw new IllegalArgumentException(
                    "Listener already registered for start element " + elementName);
        }
        this.startElementListeners.put(elementName, startElement -> {
            if (endListener != null) {
                startElement.addEndElementListener(endListener);
            }
            startListener.accept(startElement);
        });
        return this;
    }

    @Override
    public void init(final TreeParsingController treeParsingController) {
        this.treeParsingController = treeParsingController;
    }

    @Override
    public void startElement(final TreeElement treeElement) {
        LOG.finest(() -> "Start element: " + treeElement);
        final String namespaceURI = treeElement.getElement().getName().getNamespaceURI();
        if (isCustomXMLNamespace(namespaceURI)) {
            LOG.finest(() -> "custom XML element with namespace " + namespaceURI);
            return;
        }
        final Consumer<TreeElement> consumer = this.startElementListeners.getOrDefault(
                treeElement.getElement().getName().getLocalPart(),
                this.defaultStartElementListener);
        if (consumer == null) {
            LOG.warning(() -> "No consumer for event " + treeElement);
            return;
        }
        try {
            consumer.accept(treeElement);
        } catch (final Exception e) {
            throw new XmlParserException("Error handling " + treeElement + " with consumer "
                    + consumer + ": " + e.getMessage(), e);
        }
    }

    private boolean isCustomXMLNamespace(final String namespaceURI) {
        return !"".equals(namespaceURI) && !OPENFASTTRACE_XML_NAMESPACE.equals(namespaceURI);
    }

    @Override
    public void endElement(final TreeElement closedElement) {
        closedElement.invokeEndElementListeners();
    }

    /**
     * Stop parsing, e.g. in case of a parsing error.
     */
    public void stopParsing() {
        this.treeParsingController.stopParsing();
    }

    /**
     * Pushes the given {@link TreeContentHandler} as a delegate.
     * This will restore the original handler (i.e. {@code this}) when the current
     * element ends.
     * 
     * @param delegate the new delegate.
     */
    public void pushDelegate(final TreeContentHandler delegate) {
        this.treeParsingController.setDelegate(delegate);
        this.treeParsingController.getCurrentElement()
                .addEndElementListener(endElement -> this.treeParsingController.setDelegate(this));
    }

    /**
     * Add a listener for elements with integer content.
     *
     * @param elementName the element name.
     * @param listener    the listener.
     * @return this instance for method chaining.
     */
    public CallbackContentHandler addIntDataListener(final String elementName,
            final IntConsumer listener) {
        addCharacterDataListener(elementName, data -> {
            if (data == null || data.isEmpty()) {
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
     * @param elementName the element name.
     * @param listener    the listener.
     * @return this instance for method chaining.
     */
    public CallbackContentHandler addCharacterDataListener(final String elementName,
            final Consumer<String> listener) {
        addElementListener(elementName, startElement -> {
        }, endElement -> listener.accept(endElement.getCharacterData()));
        return this;
    }
}
