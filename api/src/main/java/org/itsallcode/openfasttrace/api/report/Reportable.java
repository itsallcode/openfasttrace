package org.itsallcode.openfasttrace.api.report;

import java.io.OutputStream;
import java.util.function.Consumer;

/**
 * Interface for coverage reports.
 */
@FunctionalInterface
public interface Reportable extends Consumer<OutputStream>
{
    /**
     * Render the plain text coverage stream.
     *
     * @param outputStream
     *            output stream to which the stream is rendered
     */
    void renderToStream(final OutputStream outputStream);

    @Override
    default void accept(final OutputStream outputStream) {
        renderToStream(outputStream);
    }
}
