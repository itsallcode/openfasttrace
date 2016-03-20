package openfasttrack.exporter.specobject;

import java.util.Collection;

import javax.xml.stream.XMLStreamWriter;

import openfasttrack.core.LinkedSpecificationItem;
import openfasttrack.exporter.Exporter;

public class SpecobjectExporter implements Exporter
{
    private final XMLStreamWriter xmlWriter;
    private final Collection<LinkedSpecificationItem> items;

    public SpecobjectExporter(final Collection<LinkedSpecificationItem> items,
            final XMLStreamWriter xmlWriter)
    {
        this.items = items;
        this.xmlWriter = xmlWriter;
    }

    @Override
    public void runExport()
    {

    }
}
