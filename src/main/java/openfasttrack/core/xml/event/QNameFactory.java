package openfasttrack.core.xml.event;

import javax.xml.namespace.QName;

class QNameFactory
{
    private QNameFactory()
    {

    }

    static QName create(final String uri, final String localName, final String qName)
    {
        if (localName == null || localName.isEmpty())
        {
            return new QName(uri, qName, "");
        }
        return new QName(uri, localName);
    }
}
