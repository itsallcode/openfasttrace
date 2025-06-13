package org.itsallcode.openfasttrace.testutil.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * A stream writer for XML that adds indentation.
 */
public class IndentingXMLStreamWriter extends StreamWriterDelegate
{
    private static final int WROTE_MARKUP = 1;
    private static final int WROTE_DATA = 2;
    private final String indent;
    private final String newLine;
    /** How deeply nested the current scope is. The root element is depth 1. */
    private int depth = 0; // document scope
    /** stack[depth] indicates what's been written into the current scope. */
    private int[] stack = new int[] { 0, 0, 0, 0 }; // nothing written yet
    /** Prefix that defines how deeply a line is indented. */
    private char[] linePrefix = null;

    /**
     * Create a new instance of the {@link IndentingXMLStreamWriter}.
     *
     * @param out stream writer to output to
     * @param indent string used as the indentation prefix
     * @param newLine newline character (for platform-specific output)
     */
    public IndentingXMLStreamWriter(final XMLStreamWriter out, final String indent,
            final String newLine)
    {
        super(out);
        this.indent = indent;
        this.newLine = newLine;
    }

    @Override
    public void writeStartDocument() throws XMLStreamException
    {
        beforeMarkup();
        this.out.writeStartDocument();
        afterMarkup();
    }

    @Override
    public void writeStartDocument(final String version) throws XMLStreamException
    {
        beforeMarkup();
        this.out.writeStartDocument(version);
        afterMarkup();
    }

    @Override
    public void writeStartDocument(final String encoding, final String version)
            throws XMLStreamException
    {
        beforeMarkup();
        this.out.writeStartDocument(encoding, version);
        afterMarkup();
    }

    @Override
    public void writeDTD(final String dtd) throws XMLStreamException
    {
        beforeMarkup();
        this.out.writeDTD(dtd);
        afterMarkup();
    }

    @Override
    public void writeProcessingInstruction(final String target) throws XMLStreamException
    {
        beforeMarkup();
        this.out.writeProcessingInstruction(target);
        afterMarkup();
    }

    @Override
    public void writeProcessingInstruction(final String target, final String data)
            throws XMLStreamException
    {
        beforeMarkup();
        this.out.writeProcessingInstruction(target, data);
        afterMarkup();
    }

    @Override
    public void writeComment(final String data) throws XMLStreamException
    {
        beforeMarkup();
        this.out.writeComment(data);
        afterMarkup();
    }

    @Override
    public void writeEmptyElement(final String localName) throws XMLStreamException
    {
        beforeMarkup();
        this.out.writeEmptyElement(localName);
        afterMarkup();
    }

    @Override
    public void writeEmptyElement(final String namespaceURI, final String localName)
            throws XMLStreamException
    {
        beforeMarkup();
        this.out.writeEmptyElement(namespaceURI, localName);
        afterMarkup();
    }

    @Override
    public void writeEmptyElement(final String prefix, final String localName,
            final String namespaceURI) throws XMLStreamException
    {
        beforeMarkup();
        this.out.writeEmptyElement(prefix, localName, namespaceURI);
        afterMarkup();
    }

    @Override
    public void writeStartElement(final String localName) throws XMLStreamException
    {
        beforeStartElement();
        this.out.writeStartElement(localName);
        afterStartElement();
    }

    @Override
    public void writeStartElement(final String namespaceURI, final String localName)
            throws XMLStreamException
    {
        beforeStartElement();
        this.out.writeStartElement(namespaceURI, localName);
        afterStartElement();
    }

    @Override
    public void writeStartElement(final String prefix, final String localName,
            final String namespaceURI) throws XMLStreamException
    {
        beforeStartElement();
        this.out.writeStartElement(prefix, localName, namespaceURI);
        afterStartElement();
    }

    @Override
    public void writeCharacters(final String text) throws XMLStreamException
    {
        this.out.writeCharacters(text);
        afterData();
    }

    @Override
    public void writeCharacters(final char[] text, final int start, final int len)
            throws XMLStreamException
    {
        this.out.writeCharacters(text, start, len);
        afterData();
    }

    @Override
    public void writeCData(final String data) throws XMLStreamException
    {
        this.out.writeCData(data);
        afterData();
    }

    @Override
    public void writeEntityRef(final String name) throws XMLStreamException
    {
        this.out.writeEntityRef(name);
        afterData();
    }

    @Override
    public void writeEndElement() throws XMLStreamException
    {
        beforeEndElement();
        this.out.writeEndElement();
        afterEndElement();
    }

    @Override
    public void writeEndDocument() throws XMLStreamException
    {
        while (this.depth > 0)
        {
            writeEndElement(); // indented
        }
        this.out.writeEndDocument();
        afterEndDocument();
    }

    /**
     * Prepare to write markup by writing a new line and indentation.
     *
     * @throws XMLStreamException
     *             here for interface compatibility only
     */
    protected void beforeMarkup() throws XMLStreamException
    {
        final int soFar = this.stack[this.depth];
        if ((soFar & WROTE_DATA) == 0 // no data in this scope
                && (this.depth > 0 || soFar != 0)) // not the first line
        {
            writeNewLine(this.depth);
            if (this.depth > 0 && !this.indent.isEmpty())
            {
                afterMarkup(); // indentation was written
            }
        }
    }

    /** Note that markup or indentation was written. */
    protected void afterMarkup()
    {
        this.stack[this.depth] |= WROTE_MARKUP;
    }

    /** Note that data were written. */
    protected void afterData()
    {
        this.stack[this.depth] |= WROTE_DATA;
    }

    /**
     * Prepare to start an element by allocating stack space.
     *
     * @throws XMLStreamException
     *             here for interface compatibility only
     */
    protected void beforeStartElement() throws XMLStreamException
    {
        beforeMarkup();
        if (this.stack.length <= this.depth + 1)
        {
            // Allocate more space for the stack:
            final int[] newStack = new int[this.stack.length * 2];
            System.arraycopy(this.stack, 0, newStack, 0, this.stack.length);
            this.stack = newStack;
        }
        this.stack[this.depth + 1] = 0; // nothing written yet
    }

    /** Note that an element was started. */
    protected void afterStartElement()
    {
        afterMarkup();
        ++this.depth;
    }

    /**
     * Prepare to end an element by writing a new line and indentation.
     *
     * @throws XMLStreamException
     *             here for interface compatibility only
     */
    protected void beforeEndElement() throws XMLStreamException
    {
        if (this.depth > 0 && this.stack[this.depth] == WROTE_MARKUP)
        { // but not data
            writeNewLine(this.depth - 1);
        }
    }

    /** Note that an element was ended. */
    protected void afterEndElement()
    {
        if (this.depth > 0)
        {
            --this.depth;
        }
    }

    /**
     * Note that a document was ended.
     *
     * @throws XMLStreamException
     *             here for interface compatibility only
     */
    protected void afterEndDocument() throws XMLStreamException
    {
        this.depth = 0;
        if (this.stack[this.depth] == WROTE_MARKUP)
        { // but not data
            writeNewLine(0);
        }
        this.stack[this.depth] = 0; // start fresh
    }

    /**
     * Write an indented line.
     * 
     * @param indentation
     *            indentation depth
     * @throws XMLStreamException
     *             if the underlying {@link XMLStreamWriter} has a problem
     */
    protected void writeNewLine(final int indentation) throws XMLStreamException
    {
        final int newLineLength = this.newLine.length();
        final int prefixLength = newLineLength + (this.indent.length() * indentation);
        if (prefixLength > 0)
        {
            if (this.linePrefix == null)
            {
                this.linePrefix = (this.newLine + this.indent).toCharArray();
            }
            while (prefixLength > this.linePrefix.length)
            {
                // make linePrefix longer:
                final char[] newPrefix = new char[newLineLength
                        + ((this.linePrefix.length - newLineLength) * 2)];
                System.arraycopy(this.linePrefix, 0, newPrefix, 0, this.linePrefix.length);
                System.arraycopy(this.linePrefix, newLineLength, newPrefix, this.linePrefix.length,
                        this.linePrefix.length - newLineLength);
                this.linePrefix = newPrefix;
            }
            this.out.writeCharacters(this.linePrefix, 0, prefixLength);
        }
    }
}
