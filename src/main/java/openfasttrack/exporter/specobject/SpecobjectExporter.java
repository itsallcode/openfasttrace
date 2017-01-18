package openfasttrack.exporter.specobject;

/*
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 hamstercommunity
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import openfasttrack.core.LinkedSpecificationItem;
import openfasttrack.core.SpecificationItem;
import openfasttrack.core.SpecificationItemId;
import openfasttrack.exporter.Exporter;
import openfasttrack.exporter.ExporterException;

public class SpecobjectExporter implements Exporter
{
    private static final Logger LOG = Logger.getLogger(SpecobjectExporter.class.getName());

    private final XMLStreamWriter writer;
    private final Map<String, List<LinkedSpecificationItem>> items;

    public SpecobjectExporter(final Collection<LinkedSpecificationItem> items,
            final XMLStreamWriter xmlWriter)
    {
        this.items = groupByDoctype(items);
        this.writer = xmlWriter;
    }

    private Map<String, List<LinkedSpecificationItem>> groupByDoctype(
            final Collection<LinkedSpecificationItem> items)
    {
        return items.stream().collect(
                groupingBy(item -> item.getId().getArtifactType(), LinkedHashMap::new, toList()));
    }

    @Override
    public void runExport()
    {
        try
        {
            writeOutput();
        }
        catch (final XMLStreamException e)
        {
            throw new ExporterException("Error exporting to specobject format", e);
        }
        finally
        {
            closeXmlWriter();
        }
    }

    private void closeXmlWriter()
    {
        try
        {
            LOG.finest(() -> "Closing xml writer");
            this.writer.close();
        }
        catch (final XMLStreamException e)
        {
            throw new ExporterException("Error closing xml writer", e);
        }
    }

    private void writeOutput() throws XMLStreamException
    {
        this.writer.writeStartDocument("UTF-8", "1.0");
        this.writer.writeStartElement("specdocument");

        for (final Entry<String, List<LinkedSpecificationItem>> entry : this.items.entrySet())
        {
            final String doctype = entry.getKey();
            final List<LinkedSpecificationItem> specItems = entry.getValue();
            writeItems(doctype, specItems);
        }

        this.writer.writeEndElement();
        this.writer.writeEndDocument();
    }

    private void writeItems(final String doctype, final List<LinkedSpecificationItem> specItems)
            throws XMLStreamException
    {
        LOG.finest(() -> "Writing " + specItems.size() + " items with doctype " + doctype);
        this.writer.writeStartElement("specobjects");
        this.writer.writeAttribute("doctype", doctype);
        for (final LinkedSpecificationItem item : specItems)
        {
            writeItem(item.getItem());
        }
        this.writer.writeEndElement();
    }

    private void writeItem(final SpecificationItem item) throws XMLStreamException
    {
        this.writer.writeStartElement("specobject");
        writeElement("id", item.getId().getName());
        writeElement("version", item.getId().getRevision());
        writeElement("description", item.getDescription());
        writeElement("rationale", item.getRationale());
        writeElement("comment", item.getComment());

        writeNeedsArtifactTypes(item.getNeedsArtifactTypes());
        writeCoveredIds(item.getCoveredIds());
        writeDependsOnIds(item.getDependOnIds());

        this.writer.writeEndElement();
    }

    private void writeDependsOnIds(final List<SpecificationItemId> dependOnIds)
            throws XMLStreamException
    {
        if (dependOnIds.isEmpty())
        {
            return;
        }
        this.writer.writeStartElement("dependencies");
        for (final SpecificationItemId dependsOnId : dependOnIds)
        {
            writeElement("dependson", dependsOnId.toString());
        }
        this.writer.writeEndElement();
    }

    private void writeCoveredIds(final List<SpecificationItemId> coveredIds)
            throws XMLStreamException
    {
        if (coveredIds.isEmpty())
        {
            return;
        }
        this.writer.writeStartElement("providescoverage");
        for (final SpecificationItemId coveredId : coveredIds)
        {
            this.writer.writeStartElement("provcov");
            writeElement("linksto", coveredId.getName());
            writeElement("dstversion", coveredId.getRevision());
            this.writer.writeEndElement();
        }
        this.writer.writeEndElement();
    }

    private void writeNeedsArtifactTypes(final List<String> needsArtifactTypes)
            throws XMLStreamException
    {
        if (needsArtifactTypes.isEmpty())
        {
            return;
        }
        this.writer.writeStartElement("needscoverage");
        for (final String neededArtifactType : needsArtifactTypes)
        {
            writeElement("needsobj", neededArtifactType);
        }
        this.writer.writeEndElement();
    }

    private void writeElement(final String elementName, final int content) throws XMLStreamException
    {
        writeElement(elementName, String.valueOf(content));
    }

    private void writeElement(final String elementName, final String content)
            throws XMLStreamException
    {
        this.writer.writeStartElement(elementName);
        this.writer.writeCharacters(content);
        this.writer.writeEndElement();
    }
}
