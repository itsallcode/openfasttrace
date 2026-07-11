package org.itsallcode.openfasttrace.api.report;

import java.io.OutputStream;

/**
 * Interface for coverage reports.
 */
@FunctionalInterface
@SuppressWarnings("java:S1711") // Replacing with Consumer would shade API intent.
public interface Reportable
{
    /**
     * Render the plain text coverage stream.
     *
     * @param outputStream
     *            output stream to which the stream is rendered
     */
    void renderToStream(final OutputStream outputStream);
}
