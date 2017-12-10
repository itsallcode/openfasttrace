package openfasttrack.core.xml;

public interface EventContentHandler
{
    void startElement(StartElementEvent event);

    void endElement(EndElementEvent event);

    void characters(String characters);

    void init(ContentHandlerAdapterController contentHandlerAdapter);
}
