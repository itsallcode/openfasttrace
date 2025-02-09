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

    private final Collector collector;

    public UxReporter() {
        collector = new Collector();
    }

    /**
     *
     * @param trace the traced data
     * @param context settings
     */
    public UxReporter(final Trace trace, final ReporterContext context)
    {
        LOG.info(String.format("constructor(context=%s",context.toString()));
        collector = new Collector().collect(trace.getItems());
    }

    /**
     * Generate output
     * @param outputStream The file to write output to
     */
    @Override public void renderToStream(OutputStream outputStream)
    {
        LOG.info("renderToStream");
        final IGenerator generator = new JsGenerator();
        generator.generate(outputStream, collector.getUxModel());
    }

} // UxReporter
