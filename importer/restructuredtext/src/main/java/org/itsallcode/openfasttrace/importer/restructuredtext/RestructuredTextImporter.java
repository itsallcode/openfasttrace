package org.itsallcode.openfasttrace.importer.restructuredtext;

import static org.itsallcode.openfasttrace.importer.lightweightmarkup.statemachine.LineParserState.*;

import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.importer.lightweightmarkup.LightWeightMarkupImporter;
import org.itsallcode.openfasttrace.importer.lightweightmarkup.statemachine.*;

/**
 * Importer for OFT augmented reStructuredText.
 * <p>
 * The purpose of this importer is to find specification items that follow a
 * certain structure inside reStructuredText documents. It is not the goal to
 * ingest the complete reStructuredText document though, only the specification
 * items. Also, the hierarchical structure of the document itself has no impact
 * on the specification items. OFT just extracts a flat list. Linking the items
 * is explicitly not the purpose of the importer.
 * </p>
 */
public class RestructuredTextImporter extends LightWeightMarkupImporter
{
    private static final LinePattern SECTION_TITLE = new RstSectionTitlePattern();

    /**
     * Creates a {@link RestructuredTextImporter} object with the given
     * parameters.
     *
     * @param fileName
     *            the input file to be imported
     * @param listener
     *            the listener to handle import events
     */
    RestructuredTextImporter(final InputFile fileName, final ImportEventListener listener)
    {
        super(fileName, listener);
    }

    @Override
    protected Transition[] configureTransitions()
    {
        // @formatter:off
        return new Transition[]{
            transition(START      , SPEC_ITEM  , RstPattern.ID         , this::beginItem                    ),
            transition(START      , START      , RstPattern.FORWARD    , this::forward                      ),
            transition(START      , TITLE      , SECTION_TITLE         , this::rememberTitle                ),
            transition(START      , START      , RstPattern.EVERYTHING , () -> {}                           ),

            transition(TITLE      , TITLE      , RstPattern.UNDERLINE  , () -> {}                           ),
            transition(TITLE      , TITLE      , SECTION_TITLE         , this::rememberTitle                ),
            transition(TITLE      , SPEC_ITEM  , RstPattern.ID         , this::beginItem                    ),
            transition(TITLE      , TITLE      , RstPattern.EMPTY      , () -> {}                           ),
            transition(TITLE      , START      , RstPattern.FORWARD    , () -> {endItem(); forward();}      ),
            transition(TITLE      , START      , RstPattern.EVERYTHING , this::resetTitle                   ),

            transition(SPEC_ITEM  , SPEC_ITEM  , RstPattern.ID         , this::beginItem                    ),
            transition(SPEC_ITEM  , SPEC_ITEM  , RstPattern.STATUS     , this::setStatus                    ),
            transition(SPEC_ITEM  , RATIONALE  , RstPattern.RATIONALE  , this::beginRationale               ),
            transition(SPEC_ITEM  , COMMENT    , RstPattern.COMMENT    , this::beginComment                 ),
            transition(SPEC_ITEM  , TITLE      , SECTION_TITLE         , () -> {endItem(); rememberTitle();}),
            transition(SPEC_ITEM  , COVERS     , RstPattern.COVERS     , () -> {}                           ),
            transition(SPEC_ITEM  , DEPENDS    , RstPattern.DEPENDS    , () -> {}                           ),
            transition(SPEC_ITEM  , NEEDS      , RstPattern.NEEDS_INT  , this::addNeeds                     ),
            transition(SPEC_ITEM  , NEEDS      , RstPattern.NEEDS      , () -> {}                           ),
            transition(SPEC_ITEM  , TAGS       , RstPattern.TAGS_INT   , this::addTag                       ),
            transition(SPEC_ITEM  , TAGS       , RstPattern.TAGS       , () -> {}                           ),
            transition(SPEC_ITEM  , DESCRIPTION, RstPattern.DESCRIPTION, this::beginDescription             ),
            transition(SPEC_ITEM  , DESCRIPTION, RstPattern.NOT_EMPTY  , this::beginDescription             ),

            transition(DESCRIPTION, SPEC_ITEM  , RstPattern.ID         , this::beginItem                    ),
            transition(DESCRIPTION, TITLE      , SECTION_TITLE         , () -> {endItem(); rememberTitle();}),
            transition(DESCRIPTION, RATIONALE  , RstPattern.RATIONALE  , this::beginRationale               ),
            transition(DESCRIPTION, COMMENT    , RstPattern.COMMENT    , this::beginComment                 ),
            transition(DESCRIPTION, COVERS     , RstPattern.COVERS     , () -> {}                           ),
            transition(DESCRIPTION, DEPENDS    , RstPattern.DEPENDS    , () -> {}                           ),
            transition(DESCRIPTION, NEEDS      , RstPattern.NEEDS_INT  , this::addNeeds                     ),
            transition(DESCRIPTION, NEEDS      , RstPattern.NEEDS      , () -> {}                           ),
            transition(DESCRIPTION, TAGS       , RstPattern.TAGS_INT   , this::addTag                       ),
            transition(DESCRIPTION, TAGS       , RstPattern.TAGS       , () -> {}                           ),
            transition(DESCRIPTION, START      , RstPattern.FORWARD    , () -> {endItem(); forward();}      ),
            transition(DESCRIPTION, TITLE      , SECTION_TITLE         , this::rememberTitle                ),
            transition(DESCRIPTION, DESCRIPTION, RstPattern.EVERYTHING , this::appendDescription            ),

            transition(RATIONALE  , SPEC_ITEM  , RstPattern.ID         , this::beginItem                    ),
            transition(RATIONALE  , TITLE      , SECTION_TITLE         , () -> {endItem(); rememberTitle();}),
            transition(RATIONALE  , COMMENT    , RstPattern.COMMENT    , this::beginComment                 ),
            transition(RATIONALE  , COVERS     , RstPattern.COVERS     , () -> {}                           ),
            transition(RATIONALE  , DEPENDS    , RstPattern.DEPENDS    , () -> {}                           ),
            transition(RATIONALE  , NEEDS      , RstPattern.NEEDS_INT  , this::addNeeds                     ),
            transition(RATIONALE  , NEEDS      , RstPattern.NEEDS      , () -> {}                           ),
            transition(RATIONALE  , TAGS       , RstPattern.TAGS_INT   , this::addTag                       ),
            transition(RATIONALE  , TAGS       , RstPattern.TAGS       , () -> {}                           ),
            transition(RATIONALE  , RATIONALE  , RstPattern.EVERYTHING , this::appendRationale              ),

            transition(COMMENT    , SPEC_ITEM  , RstPattern.ID         , this::beginItem                    ),
            transition(COMMENT    , TITLE      , SECTION_TITLE         , () -> {endItem(); rememberTitle();}),
            transition(COMMENT    , COVERS     , RstPattern.COVERS     , () -> {}                           ),
            transition(COMMENT    , DEPENDS    , RstPattern.DEPENDS    , () -> {}                           ),
            transition(COMMENT    , NEEDS      , RstPattern.NEEDS_INT  , this::addNeeds                     ),
            transition(COMMENT    , NEEDS      , RstPattern.NEEDS      , () -> {}                           ),
            transition(COMMENT    , RATIONALE  , RstPattern.RATIONALE  , this::beginRationale               ),
            transition(COMMENT    , TAGS       , RstPattern.TAGS_INT   , this::addTag                       ),
            transition(COMMENT    , TAGS       , RstPattern.TAGS       , () -> {}                           ),
            transition(COMMENT    , COMMENT    , RstPattern.EVERYTHING , this::appendComment                ),

            // [impl->dsn~md.covers-list~1]
            transition(COVERS     , SPEC_ITEM  , RstPattern.ID         , this::beginItem                    ),
            transition(COVERS     , TITLE      , SECTION_TITLE         , () -> {endItem(); rememberTitle();}),
            transition(COVERS     , COVERS     , RstPattern.COVERS_REF , this::addCoverage                  ),
            transition(COVERS     , RATIONALE  , RstPattern.RATIONALE  , this::beginRationale               ),
            transition(COVERS     , COMMENT    , RstPattern.COMMENT    , this::beginComment                 ),
            transition(COVERS     , DEPENDS    , RstPattern.DEPENDS    , () -> {}                           ),
            transition(COVERS     , NEEDS      , RstPattern.NEEDS_INT  , this::addNeeds                     ),
            transition(COVERS     , NEEDS      , RstPattern.NEEDS      , () -> {}                           ),
            transition(COVERS     , COVERS     , RstPattern.EMPTY      , () -> {}                           ),
            transition(COVERS     , TAGS       , RstPattern.TAGS_INT   , this::addTag                       ),
            transition(COVERS     , TAGS       , RstPattern.TAGS       , () -> {}                           ),
            transition(COVERS     , START      , RstPattern.FORWARD    , () -> {endItem(); forward();}      ),

            // [impl->dsn~md.depends-list~1]
            transition(DEPENDS    , SPEC_ITEM  , RstPattern.ID         , this::beginItem                    ),
            transition(DEPENDS    , TITLE      , SECTION_TITLE         , () -> {endItem(); rememberTitle();}),
            transition(DEPENDS    , DEPENDS    , RstPattern.DEPENDS_REF, this::addDependency                ),
            transition(DEPENDS    , RATIONALE  , RstPattern.RATIONALE  , this::beginRationale               ),
            transition(DEPENDS    , COMMENT    , RstPattern.COMMENT    , this::beginComment                 ),
            transition(DEPENDS    , DEPENDS    , RstPattern.DEPENDS    , () -> {}                           ),
            transition(DEPENDS    , NEEDS      , RstPattern.NEEDS_INT  , this::addNeeds                     ),
            transition(DEPENDS    , NEEDS      , RstPattern.NEEDS      , () -> {}                           ),
            transition(DEPENDS    , DEPENDS    , RstPattern.EMPTY      , () -> {}                           ),
            transition(DEPENDS    , COVERS     , RstPattern.COVERS     , () -> {}                           ),
            transition(DEPENDS    , TAGS       , RstPattern.TAGS_INT   , this::addTag                       ),
            transition(DEPENDS    , TAGS       , RstPattern.TAGS       , () -> {}                           ),
            transition(DEPENDS    , START      , RstPattern.FORWARD    , () -> {endItem(); forward();}      ),

            // [impl->dsn~md.needs-coverage-list-single-line~2]
            // [impl->dsn~md.needs-coverage-list~1]
            transition(NEEDS      , SPEC_ITEM  , RstPattern.ID         , this::beginItem                    ),
            transition(NEEDS      , TITLE      , SECTION_TITLE         , () -> {endItem(); rememberTitle();}),
            transition(NEEDS      , RATIONALE  , RstPattern.RATIONALE  , this::beginRationale               ),
            transition(NEEDS      , COMMENT    , RstPattern.COMMENT    , this::beginComment                 ),
            transition(NEEDS      , DEPENDS    , RstPattern.DEPENDS    , () -> {}                           ),
            transition(NEEDS      , NEEDS      , RstPattern.NEEDS_INT  , this::addNeeds                     ),
            transition(NEEDS      , NEEDS      , RstPattern.NEEDS_REF  , this::addNeeds                     ),
            transition(NEEDS      , NEEDS      , RstPattern.EMPTY      , () -> {}                           ),
            transition(NEEDS      , COVERS     , RstPattern.COVERS     , () -> {}                           ),
            transition(NEEDS      , TAGS       , RstPattern.TAGS_INT   , this::addTag                       ),
            transition(NEEDS      , TAGS       , RstPattern.TAGS       , () -> {}                           ),
            transition(NEEDS      , START      , RstPattern.FORWARD    , () -> {endItem(); forward();}      ),

            transition(TAGS       , TAGS       , RstPattern.TAG_ENTRY  , this::addTag                       ),
            transition(TAGS       , TITLE      , SECTION_TITLE         , () -> {endItem(); rememberTitle();}),
            transition(TAGS       , SPEC_ITEM  , RstPattern.ID         , this::beginItem                    ),
            transition(TAGS       , RATIONALE  , RstPattern.RATIONALE  , this::beginRationale               ),
            transition(TAGS       , COMMENT    , RstPattern.COMMENT    , this::beginComment                 ),
            transition(TAGS       , DEPENDS    , RstPattern.DEPENDS    , () -> {}                           ),
            transition(TAGS       , NEEDS      , RstPattern.NEEDS_INT  , this::addNeeds                     ),
            transition(TAGS       , NEEDS      , RstPattern.NEEDS      , () -> {}                           ),
            transition(TAGS       , NEEDS      , RstPattern.EMPTY      , () -> {}                           ),
            transition(TAGS       , COVERS     , RstPattern.COVERS     , () -> {}                           ),
            transition(TAGS       , TAGS       , RstPattern.TAGS       , () -> {}                           ),
            transition(TAGS       , TAGS       , RstPattern.TAGS_INT   , this::addTag                       ),
            transition(TAGS       , START      , RstPattern.FORWARD    , () -> {endItem(); forward();}      )
        };
        // @formatter:on
    }

    private static Transition transition(final LineParserState from, final LineParserState to,
            final RstPattern pattern, final TransitionAction action)
    {
        return new Transition(from, to, pattern.getPattern(), action);
    }
}
