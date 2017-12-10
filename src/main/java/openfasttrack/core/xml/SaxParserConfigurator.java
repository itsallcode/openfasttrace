package openfasttrack.core.xml;

import javax.xml.parsers.SAXParserFactory;

public class SaxParserConfigurator
{
    private SaxParserConfigurator()
    {
        // not instantiable
    }

    public static SAXParserFactory createSaxParserFactory()
    {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory;
    }
}
