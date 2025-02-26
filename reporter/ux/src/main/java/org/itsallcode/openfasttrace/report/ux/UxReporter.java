package org.itsallcode.openfasttrace.report.ux;

import org.itsallcode.openfasttrace.api.core.Trace;
import org.itsallcode.openfasttrace.api.report.Reportable;
import org.itsallcode.openfasttrace.api.report.ReporterContext;
import org.itsallcode.openfasttrace.report.ux.generator.IGenerator;
import org.itsallcode.openfasttrace.report.ux.generator.JsGenerator;

import java.io.OutputStream;
import java.util.logging.Logger;

/**
 *
 */
public class UxReporter implements Reportable
{

    private static final Logger LOG = Logger.getLogger(UxReporter.class.getName());

    private final Trace trace;
    private final ReporterContext context;

    /**
     *
     * @param trace the traced data
     * @param context settings
     */
    public UxReporter(final Trace trace, final ReporterContext context)
    {
        LOG.info(String.format("constructor(context=%s",context.toString()));
        this.trace = trace;
        this.context = context;
    }

    /**
     * Generate output
     * @param outputStream The file to write output to
     */
    @Override public void renderToStream(OutputStream outputStream)
    {
        LOG.info("renderToStream");
        final Collector collector = new Collector().collect(trace.getItems());
        final IGenerator generator = new JsGenerator();
        generator.generate(outputStream, collector.getUxModel());
    }

} // UxReporter
