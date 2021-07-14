package org.itsallcode.openfasttrace.report.aspec;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
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

import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.api.exporter.ExporterException;
import org.itsallcode.openfasttrace.api.report.Reportable;
import org.itsallcode.openfasttrace.api.report.ReporterContext;
import org.itsallcode.openfasttrace.exporter.common.IndentingXMLStreamWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

class ASpecReport implements Reportable
{
    private static final Logger LOG = Logger.getLogger(ASpecReport.class.getName());

    private final Trace trace;
    private final XMLOutputFactory xmlOutputFactory;
    private final Newline newline;

    /**
     * Create a new instance of an {@link ASpecReport}
     *
     * @param trace
     *            trace to be reported on
     * @param context
     *            configuration options
     */
    ASpecReport(final Trace trace, final ReporterContext context)
    {
        this.trace = trace;
        this.newline = context.getSettings().getNewline();
        this.xmlOutputFactory = XMLOutputFactory.newFactory();
    }

    @Override
    public void renderToStream(final OutputStream outputStream)
    {
        final XMLStreamWriter xmlWriter = createXmlWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        final IndentingXMLStreamWriter indentingXmlWriter = new IndentingXMLStreamWriter(xmlWriter);

        LOG.fine("aspec starting");
        try (indentingXmlWriter)
        {
            writeOutput(indentingXmlWriter);
        }
        catch (final XMLStreamException exception)
        {
            throw new ExporterException("Generating document", exception);
        }
    }

    private XMLStreamWriter createXmlWriter(final Writer writer)
    {
        try
        {
            return this.xmlOutputFactory.createXMLStreamWriter(writer);
        }
        catch (final XMLStreamException exception)
        {
            throw new ExporterException("Error creating XML stream writer for writer " + writer, exception);
        }
    }

    private void writeOutput(final XMLStreamWriter writer)
            throws XMLStreamException
    {
        writer.writeStartDocument("UTF-8", "1.0");
        writeSpecDocument(writer);
        writer.writeEndDocument();
    }

    private void writeSpecDocument(XMLStreamWriter writer)
            throws XMLStreamException
    {
        writer.writeStartElement("specdocument");

        for (final Map.Entry<String, List<LinkedSpecificationItem>> entry : groupItemsByAttributeType(
                this.trace.getItems()).entrySet())
        {
            final String doctype = entry.getKey();
            final List<LinkedSpecificationItem> specItems = entry.getValue();
            writeItems(writer, doctype, specItems);
        }

        writer.writeEndElement();
    }

    private Map<String, List<LinkedSpecificationItem>> groupItemsByAttributeType(
            final List<LinkedSpecificationItem> items)
    {
        return items.stream().collect(
                groupingBy(LinkedSpecificationItem::getArtifactType, LinkedHashMap::new, toList()));
    }

    private void writeItems(final XMLStreamWriter writer, final String doctype,
            final List<LinkedSpecificationItem> specItems)
            throws XMLStreamException
    {
        LOG.finest(() -> "Writing " + specItems.size() + " items with doctype " + doctype);
        writer.writeStartElement("specobjects");
        writer.writeAttribute("doctype", doctype);
        for (final LinkedSpecificationItem item : specItems)
        {
            writeItem(writer, item);
        }
        writer.writeEndElement();
    }

    private void writeItem(final XMLStreamWriter writer, final LinkedSpecificationItem item) throws XMLStreamException
    {

        writer.writeStartElement("specobject");

        writeItemValues(writer, item);
        writeItemCoverage(writer, item);

        writeCoveredIds(writer, item.getCoveredIds());
        writeDependsOnIds(writer, item.getItem().getDependOnIds());

        writer.writeEndElement();
    }

    private void writeItemValues(XMLStreamWriter writer, LinkedSpecificationItem item) throws XMLStreamException
    {
        final String description = processMultilineText(item.getDescription());
        final String rationale = processMultilineText(item.getItem().getRationale());
        final String comment = processMultilineText(item.getItem().getComment());

        writeElement(writer, "id", item.getName());
        writeElement(writer, "version", item.getRevision());
        writeElementIfPresent(writer, "shortdesc", item.getTitle());
        writeElement(writer, "status", item.getStatus().toString());
        writeLocation(writer, item.getLocation());
        writeElementIfPresent(writer, "description", description);
        writeElementIfPresent(writer, "rationale", rationale);
        writeElementIfPresent(writer, "comment", comment);
        writeTags(writer, item.getTags());
    }

    private String processMultilineText(final String text)
    {
        return unifyNewlines(text);
    }

    private String unifyNewlines(final String text)
    {
        final Matcher matcher = Newline.anyNewlinePattern().matcher(text);
        return matcher.replaceAll(this.newline.toString());
    }

    private void writeTags(final XMLStreamWriter writer, final List<String> tags) throws XMLStreamException
    {
        if (tags.isEmpty())
        {
            return;
        }
        writer.writeStartElement("tags");
        for (final String tag : tags)
        {
            writeElement(writer, "tag", tag);
        }
        writer.writeEndElement();
    }

    private void writeItemCoverage(XMLStreamWriter writer, LinkedSpecificationItem item) throws XMLStreamException
    {
        writer.writeStartElement("coverage");
        writeNeedsArtifactTypes(writer, item.getNeedsArtifactTypes());
        writeElement(writer, "shallowCoverageStatus",
                item.isCoveredShallowWithApprovedItems() ? "COVERED" : "UNCOVERED");
        writeElement(writer, "deepCoverageStatus", item.getDeepCoverageStatusOnlyAcceptApprovedItems().name());

        writeCoveringSpecObjects(writer, item);

        writeCoveredTypes(writer, item.getCoveredApprovedArtifactTypes());
        writeUncoveredTypes(writer, item.getUncoveredApprovedArtifactTypes());
        writer.writeEndElement();
    }

    private void writeCoveringSpecObjects(XMLStreamWriter writer, LinkedSpecificationItem item)
            throws XMLStreamException
    {
        writer.writeStartElement("coveringSpecObjects");
        for (Map.Entry<LinkStatus, List<LinkedSpecificationItem>> entry : item.getLinks().entrySet().stream()
                .filter(entry -> entry.getKey().isIncoming())
                .collect(Collectors.toCollection(LinkedList::new)))
        {
            for (LinkedSpecificationItem coveringItem : entry.getValue())
            {
                writeCoveringSpecObject(writer, entry.getKey(), coveringItem);
            }
        }
        writer.writeEndElement();
    }

    private void writeCoveringSpecObject(final XMLStreamWriter writer, final LinkStatus linkStatus,
            final LinkedSpecificationItem item) throws XMLStreamException
    {
        writer.writeStartElement("coveringSpecObject");

        writeElement(writer, "id", item.getName());
        writeElement(writer, "version", item.getRevision());
        writeElement(writer, "doctype", item.getArtifactType());
        writeElement(writer, "status", item.getStatus().toString());
        writeElement(writer, "ownCoverageStatus", item.isCoveredShallowWithApprovedItems() ? "COVERED" : "UNCOVERED");
        final DeepCoverageStatus deepCoverageStatus = item.getDeepCoverageStatusOnlyAcceptApprovedItems();
        writeElement(writer, "deepCoverageStatus",
                deepCoverageStatus == DeepCoverageStatus.COVERED ? "COVERED" : deepCoverageStatus.name());

        writeCoveringStatus(writer, linkStatus, deepCoverageStatus);

        writer.writeEndElement();
    }

    private void writeCoveringStatus(XMLStreamWriter writer, LinkStatus linkStatus,
            DeepCoverageStatus deepCoverageStatus)
            throws XMLStreamException
    {
        if (linkStatus == LinkStatus.COVERED_SHALLOW && deepCoverageStatus == DeepCoverageStatus.COVERED)
        {
            writeElement(writer, "coveringStatus", CoveringStatus.COVERING.getLabel());
        }
        else if (linkStatus == LinkStatus.COVERED_SHALLOW)
        {
            writeElement(writer, "coveringStatus", CoveringStatus.UNCOVERED.getLabel());
        }
        else if (linkStatus == LinkStatus.COVERED_PREDATED || linkStatus == LinkStatus.COVERED_OUTDATED)
        {
            writeElement(writer, "coveringStatus", CoveringStatus.OUTDATED.getLabel());
        }
        else if (linkStatus == LinkStatus.AMBIGUOUS || linkStatus == LinkStatus.COVERED_UNWANTED)
        {
            writeElement(writer, "coveringStatus", CoveringStatus.UNEXPECTED.getLabel());
        }
    }

    private void writeDependsOnIds(final XMLStreamWriter writer, final List<SpecificationItemId> dependOnIds)
            throws XMLStreamException
    {
        if (dependOnIds.isEmpty())
        {
            return;
        }
        writer.writeStartElement("dependencies");
        for (final SpecificationItemId dependsOnId : dependOnIds)
        {
            writer.writeStartElement("dependsOnSpecObject");
            writeElement(writer, "id", dependsOnId.getName());
            writeElement(writer, "version", dependsOnId.getRevision());
            writeElement(writer, "doctype", dependsOnId.getArtifactType());
            writer.writeEndElement();
        }
        writer.writeEndElement();
    }

    private void writeCoveredIds(final XMLStreamWriter writer, final List<SpecificationItemId> coveredIds)
            throws XMLStreamException
    {
        if (coveredIds.isEmpty())
        {
            return;
        }
        writer.writeStartElement("covering");
        for (final SpecificationItemId coveredId : coveredIds)
        {
            writer.writeStartElement("coveredType");
            writeElement(writer, "id", coveredId.getName());
            writeElement(writer, "version", coveredId.getRevision());
            writeElement(writer, "doctype", coveredId.getArtifactType());
            writer.writeEndElement();
        }
        writer.writeEndElement();
    }

    private void writeNeedsArtifactTypes(final XMLStreamWriter writer, final List<String> needsArtifactTypes)
            throws XMLStreamException
    {
        if (needsArtifactTypes.isEmpty())
        {
            return;
        }
        writer.writeStartElement("needscoverage");
        for (final String neededArtifactType : needsArtifactTypes)
        {
            writeElement(writer, "needsobj", neededArtifactType);
        }
        writer.writeEndElement();
    }

    private void writeCoveredTypes(final XMLStreamWriter writer, final Set<String> types) throws XMLStreamException
    {
        if (types.isEmpty())
            return;
        writer.writeStartElement("coveredTypes");
        for (final String type : types)
        {
            writeElement(writer, "coveredType", type);
        }
        writer.writeEndElement();
    }

    private void writeUncoveredTypes(final XMLStreamWriter writer, final List<String> types) throws XMLStreamException
    {
        if (types.isEmpty())
            return;
        writer.writeStartElement("uncoveredTypes");
        for (final String type : types)
        {
            writeElement(writer, "uncoveredType", type);
        }
        writer.writeEndElement();
    }

    private void writeElement(final XMLStreamWriter writer, final String elementName, final int content)
            throws XMLStreamException
    {
        writeElement(writer, elementName, String.valueOf(content));
    }

    private void writeElementIfPresent(final XMLStreamWriter writer, final String elementName, final String content)
            throws XMLStreamException
    {
        if (content != null && !content.isEmpty())
        {
            writeElement(writer, elementName, content);
        }
    }

    private void writeElement(final XMLStreamWriter writer, final String elementName, final String content)
            throws XMLStreamException
    {
        writer.writeStartElement(elementName);
        writer.writeCharacters(content);
        writer.writeEndElement();
    }

    private void writeLocation(final XMLStreamWriter writer, final Location location) throws XMLStreamException
    {
        if (location != null && location.getPath() != null && !location.getPath().isEmpty())
        {
            writeElement(writer, "sourcefile", location.getPath());
            writeElement(writer, "sourceline", location.getLine());
        }
    }

    public enum CoveringStatus
    {
        COVERING("COVERING"), UNCOVERED("UNCOVERED"), OUTDATED("COVERING_WRONG_VERSION"), UNEXPECTED("UNEXPECTED");

        private final String label;

        CoveringStatus(final String label)
        {
            this.label = label;
        }

        public String getLabel()
        {
            return label;
        }
    }

}