package openfasttrack.importer.specobject;

import java.util.logging.Logger;

import openfasttrack.core.SpecificationItemId;
import openfasttrack.core.SpecificationItemId.Builder;
import openfasttrack.core.xml.Attribute;
import openfasttrack.core.xml.EventContentHandler;
import openfasttrack.core.xml.callback.CallbackBasedContentHandler;
import openfasttrack.importer.ImportEventListener;
import openfasttrack.importer.ImporterException;

public class SpecObjectHandlerConfigBuilder
{
    private static final Logger LOG = Logger
            .getLogger(SpecObjectHandlerConfigBuilder.class.getName());

    private static final String DOCTYPE_ATTRIBUTE_NAME = "doctype";
    private final CallbackBasedContentHandler handler;
    private String defaultDoctype;
    private final String fileName;
    private final ImportEventListener listener;

    private boolean validRootElementFound = false;

    private boolean needsCoverageSection = false;
    private boolean providesCoverageSection = false;
    private boolean dependenciesSection = false;
    private Builder providesCoverageIdBuilder = null;
    private Builder idBuilder = null;

    public SpecObjectHandlerConfigBuilder(final String fileName, final ImportEventListener listener)
    {
        this.fileName = fileName;
        this.listener = listener;

        this.handler = new CallbackBasedContentHandler();
    }

    public EventContentHandler build()
    {
        this.handler.setDefaultStartElementHandler(startElement -> {
            if (this.validRootElementFound)
            {
                LOG.warning("Found unknown element " + startElement);
                return;
            }
            LOG.info("Found unknown root element " + startElement + ": skip file");
            this.handler.stopParsing();
        });

        this.handler.addStartElementListener("specdocument",
                elem -> this.validRootElementFound = true);
        this.handler.addStartElementListener("specobjects", elem -> {
            final Attribute doctypeAttribute = elem.getAttributeValueByName(DOCTYPE_ATTRIBUTE_NAME);
            if (doctypeAttribute == null)
            {
                throw new ImporterException("Element " + elem + " does not have an attribute '"
                        + DOCTYPE_ATTRIBUTE_NAME + "' at " + elem.getLocation());
            }
            this.defaultDoctype = doctypeAttribute.getValue();
        });
        this.handler.addStartElementListener("specobject", elem -> {
            if (this.defaultDoctype == null)
            {
                throw new ImporterException(
                        "No specobject default doctype found in file '" + this.fileName + "'");
            }
            this.listener.beginSpecificationItem();
            this.listener.setLocation(this.fileName, elem.getLocation().getLine());
            this.idBuilder = new SpecificationItemId.Builder() //
                    .artifactType(this.defaultDoctype);
        });
        this.handler.addEndElementListener("specobject", elem -> {
            this.listener.setId(this.idBuilder.build());
            this.idBuilder = null;
            this.listener.endSpecificationItem();
        });
        this.handler.addCharacterDataListener("id", data -> {
            this.idBuilder.name(data);
        });
        this.handler.addCharacterDataListener("version",
                data -> this.idBuilder.revision(Integer.parseInt(data)));

        this.handler.addCharacterDataListener("description",
                data -> this.listener.appendDescription(data));
        this.handler.addCharacterDataListener("rationale",
                data -> this.listener.appendRationale(data));
        this.handler.addCharacterDataListener("comment", data -> this.listener.appendComment(data));

        this.handler.addStartElementListener("needscoverage",
                elem -> this.needsCoverageSection = true);
        this.handler.addEndElementListener("needscoverage",
                elem -> this.needsCoverageSection = false);
        this.handler.addCharacterDataListener("needsobj", data -> {
            if (this.needsCoverageSection)
            {
                this.listener.addNeededArtifactType(data);
            }
        });

        this.handler.addStartElementListener("providescoverage",
                elem -> this.providesCoverageSection = true);
        this.handler.addEndElementListener("providescoverage",
                elem -> this.providesCoverageSection = false);

        this.handler.addStartElementListener("provcov", elem -> {
            if (this.providesCoverageSection)
            {
                this.providesCoverageIdBuilder = new Builder();
            }
        });
        this.handler.addEndElementListener("provcov", elem -> {
            if (this.providesCoverageSection)
            {
                this.listener.addCoveredId(this.providesCoverageIdBuilder.build());
                this.providesCoverageIdBuilder = null;
            }
        });

        this.handler.addCharacterDataListener("linksto", (data) -> {
            if (this.providesCoverageIdBuilder != null)
            {
                this.providesCoverageIdBuilder.name(data);
            }
        });
        this.handler.addCharacterDataListener("dstversion", (data) -> {
            if (this.providesCoverageIdBuilder != null)
            {
                this.providesCoverageIdBuilder.revision(Integer.parseInt(data));
            }
        });

        this.handler.addStartElementListener("dependencies",
                elem -> this.dependenciesSection = true);
        this.handler.addEndElementListener("dependencies",
                elem -> this.dependenciesSection = false);
        this.handler.addCharacterDataListener("dependson", (data) -> {
            if (this.dependenciesSection)
            {
                this.listener.addDependsOnId(SpecificationItemId.parseId(data));
            }
        });

        return this.handler;
    }
}
