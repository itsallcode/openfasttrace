package org.itsallcode.openfasttrace.importer.xmlparser.event;

import java.util.Objects;

import javax.xml.namespace.QName;

/**
 * Factory for creating {@link QName} objects.
 */
class QNameFactory
{
    private QNameFactory()
    {
    }

    /**
     * Create a new qualified name {@link QName}.
     * 
     * @param namespaceUri
     *            naemspace URI for the qualified name
     * @param localName
     *            local name for the qualified name
     * @param qName
     *            qualified name
     */
    static QName create(final String namespaceUri, final String localName, final String qName)
    {
        if (localName == null || localName.isEmpty())
        {
            return new QName(Objects.requireNonNull(namespaceUri, "namespaceUri"), qName, "");
        }
        return new QName(namespaceUri, localName);
    }
}
