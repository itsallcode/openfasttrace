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

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class ASpecReport implements Reportable {
    private static final Logger LOG = Logger.getLogger(ASpecReport.class.getName());

    private final Trace trace;
    private final XMLOutputFactory xmlOutputFactory;
    private final Newline newline;

    /**
     * Create a new instance of an {@link ASpecReport}
     *
     * @param trace trace to be reported on
     */
    public ASpecReport(final Trace trace, final ReporterContext context) {
        this.trace = trace;
        this.newline = context.getSettings().getNewline();
        this.xmlOutputFactory = XMLOutputFactory.newFactory();
    }


    @Override
    public void renderToStream(final OutputStream outputStream) {
        final XMLStreamWriter xmlWriter = createXmlWriter(new OutputStreamWriter(outputStream));
        final IndentingXMLStreamWriter indentingXmlWriter = new IndentingXMLStreamWriter(xmlWriter);
        final Map<String, List<LinkedSpecificationItem>> items = groupByDoctype(this.trace.getItems().stream());

        LOG.info( "aspec starting");
        try (indentingXmlWriter) {
            writeOutput(indentingXmlWriter, items);
        } catch (final XMLStreamException e) {
            throw new ExporterException("Generating document", e);
        }
    }

    private XMLStreamWriter createXmlWriter(final Writer writer) {
        try {
            return this.xmlOutputFactory.createXMLStreamWriter(writer);
        } catch (final XMLStreamException e) {
            throw new ExporterException("Error creating xml stream writer for writer " + writer, e);
        }
    }

    private Map<String, List<LinkedSpecificationItem>> groupByDoctype(
            final Stream<LinkedSpecificationItem> itemStream) {
        return itemStream.collect(
                groupingBy(LinkedSpecificationItem::getArtifactType, LinkedHashMap::new, toList()));
    }

    private void writeOutput(final XMLStreamWriter writer, Map<String, List<LinkedSpecificationItem>> items) throws XMLStreamException {
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeStartElement("specdocument");

        for (final Map.Entry<String, List<LinkedSpecificationItem>> entry : items.entrySet()) {
            final String doctype = entry.getKey();
            final List<LinkedSpecificationItem> specItems = entry.getValue();
            writeItems(writer, doctype, specItems);
        }

        writeStatistics(writer);
        writer.writeEndElement();
        writer.writeEndDocument();
    }

    private static final Comparator<LinkedSpecificationItem> LINKED_ITEM_BY_ID = Comparator
            .comparing(LinkedSpecificationItem::getId);

    private void writeStatistics(final XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("summary");
        writeElement(writer, "count", trace.count());
        writeElement(writer, "countDefects", trace.countDefects());
        writer.writeStartElement("defects");
        for ( final LinkedSpecificationItem item : trace.getDefectItems().stream()
                .sorted(LINKED_ITEM_BY_ID).collect(Collectors.toCollection(LinkedList::new)) ) {
            printDefectItem(writer,item);
        }
        writer.writeEndElement();
        writer.writeEndElement();
    }

    private void printDefectItem(final XMLStreamWriter writer, final LinkedSpecificationItem item) throws XMLStreamException {
        writer.writeStartElement("defectobject");
        writeElement(writer, "id", item.getId().toString());
        writer.writeEndElement();
    }

    private void writeItems(final XMLStreamWriter writer, final String doctype, final List<LinkedSpecificationItem> specItems)
            throws XMLStreamException {
        LOG.finest(() -> "Writing " + specItems.size() + " items with doctype " + doctype);
        writer.writeStartElement("specobjects");
        writer.writeAttribute("doctype", doctype);
        for (final LinkedSpecificationItem item : specItems) {
            writeItem(writer, item);
        }
        writer.writeEndElement();
    }

    private void writeItem(final XMLStreamWriter writer, final LinkedSpecificationItem item) throws XMLStreamException {
        final String description = processMultilineText(item.getDescription());
        final String rationale = processMultilineText(item.getItem().getRationale());
        final String comment = processMultilineText(item.getItem().getComment());

        writer.writeStartElement("specobject");

        writeElement(writer, "id", item.getName());
        writeElement(writer, "version", item.getRevision());
        writeElementIfPresent(writer, "shortdesc", item.getTitle());
        writeElement(writer, "status", item.getStatus().toString());
        writeLocation(writer, item.getLocation());
        writeElementIfPresent(writer, "description", description);
        writeElementIfPresent(writer, "rationale", rationale);
        writeElementIfPresent(writer, "comment", comment);
        writeTags(writer, item.getTags());

        writer.writeStartElement("coverage");
        writeNeedsArtifactTypes(writer, item.getNeedsArtifactTypes());
        writeElement(writer,"shallowCoverageStatus", item.isCoveredShallowWithApprovedItems() ? "COVERD" : "UNCOVERED");
        writeElement(writer, "transitiveCoverageStatus", item.getDeepCoverageStatusOnlyAcceptApprovedItems().name());
        writer.writeStartElement( "coveringSpecObjects");
        for(Map.Entry<LinkStatus,List<LinkedSpecificationItem>> entry : item.getLinks().entrySet().stream()
                .filter(entry -> entry.getKey().isIncoming())
                .collect(Collectors.toCollection(LinkedList::new))){
            for( LinkedSpecificationItem coveringItem : entry.getValue() ) {
                writeCoveringItem( writer, entry.getKey(), coveringItem );
            }
        }
        writer.writeEndElement();

        writeCoveredTypes(writer,item.getCoveredArtifactTypes());
        writeUncoveredTypes(writer,item.getUncoveredArtifactTypes());
        writer.writeEndElement();

        writeCoveredIds(writer, item.getCoveredIds());
        writeDependsOnIds(writer, item.getItem().getDependOnIds());

        writer.writeEndElement();
    }

    private String processMultilineText(final String text) {
        return unifyNewlines(text);
    }

    private String unifyNewlines(final String text) {
        final Matcher matcher = Newline.anyNewlinePattern().matcher(text);
        return matcher.replaceAll(this.newline.toString());
    }

    private void writeTags(final XMLStreamWriter writer, final List<String> tags) throws XMLStreamException {
        if (tags.isEmpty()) {
            return;
        }
        writer.writeStartElement("tags");
        for (final String tag : tags) {
            writeElement(writer, "tag", tag);
        }
        writer.writeEndElement();
    }

    private void writeCoveringItem( final XMLStreamWriter writer, final LinkStatus linkStatus, final LinkedSpecificationItem item ) throws XMLStreamException {
        writer.writeStartElement("coveringSpecObject");

        writeElement(writer, "id", item.getName());
        writeElement(writer, "version", item.getRevision());
        writeElement(writer, "status", item.getStatus().toString());
        if( linkStatus == LinkStatus.COVERED_SHALLOW ) {
            writeElement(writer, "ownCoverageStatus", item.getStatus() == ItemStatus.APPROVED ? "COVERS" : "UNCOVERS" );
            final DeepCoverageStatus deepCoverageStatus = item.getDeepCoverageStatusOnlyAcceptApprovedItems();
            writeElement(writer, "transitiveCoverageStatus", deepCoverageStatus == DeepCoverageStatus.COVERED ?
                    "COVERS" :
                    deepCoverageStatus.name() );
        } else {
            writeElement(writer, "ownCoverageStatus", linkStatus.name() );
            writeElement(writer, "transitiveCoverageStatus", linkStatus.name() );
        }

        writer.writeEndElement();
    }


    private void writeDependsOnIds(final XMLStreamWriter writer, final List<SpecificationItemId> dependOnIds)
            throws XMLStreamException {
        if (dependOnIds.isEmpty()) {
            return;
        }
        writer.writeStartElement("dependencies");
        for (final SpecificationItemId dependsOnId : dependOnIds) {
            writeElement(writer, "dependson", dependsOnId.toString());
        }
        writer.writeEndElement();
    }

    private void writeCoveredIds(final XMLStreamWriter writer, final List<SpecificationItemId> coveredIds)
            throws XMLStreamException {
        if (coveredIds.isEmpty()) {
            return;
        }
        writer.writeStartElement("providescoverage");
        for (final SpecificationItemId coveredId : coveredIds) {
            writer.writeStartElement("provcov");
            writeElement(writer, "linksto", coveredId.getArtifactType() + ":" + coveredId.getName());
            writeElement(writer, "dstversion", coveredId.getRevision());
            writer.writeEndElement();
        }
        writer.writeEndElement();
    }

    private void writeNeedsArtifactTypes(final XMLStreamWriter writer, final List<String> needsArtifactTypes)
            throws XMLStreamException {
        if (needsArtifactTypes.isEmpty()) {
            return;
        }
        writer.writeStartElement("needscoverage");
        for (final String neededArtifactType : needsArtifactTypes) {
            writeElement(writer, "needsobj", neededArtifactType);
        }
        writer.writeEndElement();
    }

    private void writeCoveredTypes(final XMLStreamWriter writer,final Set<String> types ) throws XMLStreamException {
        if(types.isEmpty()) return;
        writer.writeStartElement("coveredTypes");
        for( final String type : types ) {
            writeElement(writer,"coveredType", type );
        }
        writer.writeEndElement();
    }

    private void writeUncoveredTypes(final XMLStreamWriter writer,final List<String> types ) throws XMLStreamException {
        if(types.isEmpty()) return;
        writer.writeStartElement("uncoveredTypes");
        for( final String type : types ) {
            writeElement(writer,"uncoveredType", type );
        }
        writer.writeEndElement();
    }

    private void writeElement(final XMLStreamWriter writer, final String elementName, final int content) throws XMLStreamException {
        writeElement(writer, elementName, String.valueOf(content));
    }

    private void writeElementIfPresent(final XMLStreamWriter writer, final String elementName, final String content)
            throws XMLStreamException {
        if (content != null && !content.isEmpty()) {
            writeElement(writer, elementName, content);
        }
    }

    private void writeElement(final XMLStreamWriter writer, final String elementName, final String content)
            throws XMLStreamException {
        writer.writeStartElement(elementName);
        writer.writeCharacters(content);
        writer.writeEndElement();
    }

    private void writeLocation(final XMLStreamWriter writer, final Location location) throws XMLStreamException {
        if (location != null && location.getPath() != null && !location.getPath().isEmpty()) {
            writeElement(writer, "sourcefile", location.getPath());
            writeElement(writer, "sourceline", location.getLine());
        }
    }


}