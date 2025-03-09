package org.itsallcode.openfasttrace.report.ux;

import org.itsallcode.openfasttrace.api.ReportSettings;
import org.itsallcode.openfasttrace.api.core.Trace;
import org.itsallcode.openfasttrace.api.report.Reportable;
import org.itsallcode.openfasttrace.api.report.ReporterContext;
import org.itsallcode.openfasttrace.report.ux.generator.IGenerator;
import org.itsallcode.openfasttrace.report.ux.generator.JsGenerator;
import org.itsallcode.openfasttrace.report.ux.model.UxModel;

import java.io.OutputStream;
import java.util.logging.Logger;

/**
 *
 */
public class UxReporter implements Reportable
{

    private static final Logger LOG = Logger.getLogger(UxReporter.class.getName());

    private final Trace trace;
    private final ReportSettings settings;

    /**
     *
     * @param trace the traced data
     * @param context settings
     */
    public UxReporter(final Trace trace, final ReporterContext context)
    {
        LOG.info(String.format("constructor(context=%s",context.toString()));
        this.trace = trace;
        this.settings = context.getSettings();
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
        generator.generate(outputStream, extendModel(collector.getUxModel(), settings));
    }

    /**
     * Adjusts
     *
     * @param uxModel The collected model
     * @return uxModel extended via setting
     */
    private static UxModel extendModel( final UxModel uxModel, final ReportSettings settings ) {
        final UxModel.Builder uxModelBuilder = UxModel.builder(uxModel);

        // Add project name prefix if set
        final String projectName = System.getProperty("oftProjectName");
        if( projectName != null )
        {
            uxModelBuilder.withProjectName(projectName + " " + uxModel.getProjectName() );
        }

        return uxModelBuilder.build();
    }

} // UxReporter
