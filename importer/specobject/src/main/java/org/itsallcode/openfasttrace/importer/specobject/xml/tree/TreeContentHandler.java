package org.itsallcode.openfasttrace.importer.specobject.xml.tree;

/**
 * A callback interface for handling XML parsing events.
 */
public interface TreeContentHandler
{
    /**
     * Called before the parsing is started.
     * 
     * @param parsingController
     *            the controller.
     */
    void init(TreeParsingController parsingController);

    /**
     * Called when a new XML element starts.
     * 
     * @param treeElement
     *            the starting element.
     */
    void startElement(TreeElement treeElement);

    /**
     * Called when a XML element is closed.
     * 
     * @param closedElement
     *            the closed element.
     */
    void endElement(TreeElement closedElement);

}
