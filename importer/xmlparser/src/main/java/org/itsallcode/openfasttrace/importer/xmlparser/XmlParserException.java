package org.itsallcode.openfasttrace.importer.xmlparser;

/**
 * The XML parser throws this exception when there is an error while
 * parsing XML.
 */
public class XmlParserException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new {@link XmlParserException}.
     * 
     * @param message the message for the exception.
     * @param cause   the cause for the exception.
     */
    public XmlParserException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new {@link XmlParserException}.
     * 
     * @param message the message for the exception.
     */
    public XmlParserException(final String message) {
        super(message);
    }
}
