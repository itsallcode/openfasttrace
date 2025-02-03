package org.itsallcode.openfasttrace.report.ux;

import org.itsallcode.openfasttrace.api.core.Trace;
import org.itsallcode.openfasttrace.api.report.Reportable;
import org.itsallcode.openfasttrace.api.report.ReporterContext;

import java.io.OutputStream;
import java.util.logging.Logger;

/**
 *
 */
public class UxReporter implements Reportable
{

    private static final Logger LOG = Logger.getLogger(UxReporter.class.getName());

    public UxReporter() {
    }

    /**
     *
     * @param trace the traced data
     * @param context settings
     */
    public UxReporter(final Trace trace, final ReporterContext context)
    {
        LOG.info(String.format("constructor(context=%s",context.toString()));
    }

    /**
     * Generate output
     * @param outputStream The file to write output to
     */
    @Override public void renderToStream(OutputStream outputStream)
    {
        LOG.info("renderToStream");
    }
}
