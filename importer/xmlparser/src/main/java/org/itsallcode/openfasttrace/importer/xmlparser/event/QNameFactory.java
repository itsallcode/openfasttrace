package org.itsallcode.openfasttrace.importer.xmlparser.event;

import java.util.Objects;

import javax.xml.namespace.QName;

class QNameFactory
{
    private QNameFactory()
    {
    }

    static QName create(final String namespaceUri, final String localName, final String qName)
    {
        if (localName == null || localName.isEmpty())
        {
            return new QName(Objects.requireNonNull(namespaceUri, "namespaceUri"), qName, "");
        }
        return new QName(namespaceUri, localName);
    }
}
