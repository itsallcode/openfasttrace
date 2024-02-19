package org.itsallcode.openfasttrace.importer.xmlparser.tree;

/**
 * This interface allows {@link TreeContentHandler}s to control the parsing
 * process, e.g. by registering a delegate or stop parsing.
 */
public interface TreeParsingController {
    /**
     * Set a new handler delegate.
     * 
     * @param newDelegate
     *                    the new delegate.
     */
    void setDelegate(TreeContentHandler newDelegate);

    /**
     * Get the currently parsed element node.
     * 
     * @return the currently parsed element node.
     */
    TreeElement getCurrentElement();

    /**
     * Tell the controller to stop parsing, e.g. in case of a parsing error.
     */
    void stopParsing();
}
