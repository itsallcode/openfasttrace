package org.itsallcode.openfasttrace.importer.specobject.xml;

import org.itsallcode.openfasttrace.importer.specobject.xml.event.EndElementEvent;
import org.itsallcode.openfasttrace.importer.specobject.xml.event.StartElementEvent;

/**
 * An event handler for XML parsing events.
 */
public interface EventContentHandler
{
    /**
     * Called before parsing begins.
     * 
     * @param contentHandlerAdapter
     *            the controller.
     */
    void init(ContentHandlerAdapterController contentHandlerAdapter);

    /**
     * Called when an XML element starts.
     * 
     * @param event
     *            the start event.
     */
    void startElement(StartElementEvent event);

    /**
     * Called when an XML element ends.
     * 
     * @param event
     *            the end event.
     */
    void endElement(EndElementEvent event);

    /**
     * Called when character data content is found.
     * 
     * @param characters
     *            the character data.
     */
    void characters(String characters);
}
