package openfasttrack.core.xml;

public interface ContentHandlerAdapterController
{
    void delegateToSubHandler(final EventContentHandler subDelegate);

    void parsingFinished();
}
