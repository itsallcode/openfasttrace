package org.itsallcode.openfasttrace.importer.xmlparser;

import org.itsallcode.openfasttrace.importer.xmlparser.event.EndElementEvent;
import org.itsallcode.openfasttrace.importer.xmlparser.event.StartElementEvent;

/**
 * An event handler for XML parsing events.
 */
public interface EventContentHandler
{
    /**
     * Called before parsing begins.
     * 
     * @param contentHandlerAdapter
     *            content handler adapter that allows stopping the parsing in
     *            case of errors.
     */
    void init(ContentHandlerAdapterController contentHandlerAdapter);

    /**
     * Called when an XML element starts.
     * 
     * @param event
     *            start event.
     */
    void startElement(StartElementEvent event);

    /**
     * Called when an XML element ends.
     * 
     * @param event
     *            end event.
     */
    void endElement(EndElementEvent event);

    /**
     * Called when character data content is found.
     * 
     * @param characters
     *            character data.
     */
    void characters(String characters);
}
