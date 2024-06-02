package org.itsallcode.openfasttrace.importer.markdown;

import static org.itsallcode.openfasttrace.importer.lightweightmarkup.statemachine.LineParserState.*;

import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.importer.lightweightmarkup.LightWeightMarkupImporter;
import org.itsallcode.openfasttrace.importer.lightweightmarkup.statemachine.*;

/**
 * Importer for OFT augmented Markdown.
 * <p>
 * The purpose of this importer is to find specification items that follow a
 * certain structure inside Markdown documents. It is not the goal to ingest the
 * complete markdown document though, only the specification items. Also, the
 * hierarchical structure of the document itself has no impact on the
 * specification items. OFT just extracts a flat list. Linking the items is
 * explicitly not the purpose of the importer.
 * </p>
 */
class MarkdownImporter extends LightWeightMarkupImporter
{
    private static final LinePattern SECTION_TITLE = new MdSectionTitlePattern();

    /**
     * Creates a {@link MarkdownImporter} object with the given parameters.
     *
     * @param fileName
     *            the input file to be imported
     * @param listener
     *            the listener to handle import events
     */
    MarkdownImporter(final InputFile fileName, final ImportEventListener listener)
    {
        super(fileName, listener);
    }

    protected Transition[] configureTransitions()
    {
        // @formatter:off
        return new Transition[]{
                transition(START      , SPEC_ITEM  , MdPattern.ID         , this::beginItem                    ),
                transition(START      , TITLE      , SECTION_TITLE        , this::rememberTitle                ),
                transition(START      , START      , MdPattern.FORWARD    , this::forward                      ),
                transition(START      , START      , MdPattern.EVERYTHING , () -> {}                           ),

                transition(TITLE      , SPEC_ITEM  , MdPattern.ID         , this::beginItem                    ),
                transition(TITLE      , TITLE      , SECTION_TITLE        , this::rememberTitle                ),
                transition(TITLE      , TITLE      , MdPattern.UNDERLINE  , () -> {}                           ),
                transition(TITLE      , TITLE      , MdPattern.EMPTY      , () -> {}                           ),
                transition(TITLE      , START      , MdPattern.FORWARD    , () -> {forward(); resetTitle();}   ),
                transition(TITLE      , START      , MdPattern.EVERYTHING , this::resetTitle                   ),

                transition(SPEC_ITEM  , SPEC_ITEM  , MdPattern.ID         , this::beginItem                    ),
                transition(SPEC_ITEM  , SPEC_ITEM  , MdPattern.STATUS     , this::setStatus                    ),
                transition(SPEC_ITEM  , TITLE      , SECTION_TITLE        , () -> {endItem(); rememberTitle();}),
                transition(SPEC_ITEM  , RATIONALE  , MdPattern.RATIONALE  , this::beginRationale               ),
                transition(SPEC_ITEM  , COMMENT    , MdPattern.COMMENT    , this::beginComment                 ),
                transition(SPEC_ITEM  , COVERS     , MdPattern.COVERS     , () -> {}                           ),
                transition(SPEC_ITEM  , DEPENDS    , MdPattern.DEPENDS    , () -> {}                           ),
                transition(SPEC_ITEM  , NEEDS      , MdPattern.NEEDS_INT  , this::addNeeds                     ),
                transition(SPEC_ITEM  , NEEDS      , MdPattern.NEEDS      , () -> {}                           ),
                transition(SPEC_ITEM  , TAGS       , MdPattern.TAGS_INT   , this::addTag                       ),
                transition(SPEC_ITEM  , TAGS       , MdPattern.TAGS       , () -> {}                           ),
                transition(SPEC_ITEM  , DESCRIPTION, MdPattern.DESCRIPTION, this::beginDescription             ),
                transition(SPEC_ITEM  , DESCRIPTION, MdPattern.NOT_EMPTY  , this::beginDescription             ),

                transition(DESCRIPTION, SPEC_ITEM  , MdPattern.ID         , this::beginItem                    ),
                transition(DESCRIPTION, TITLE      , SECTION_TITLE        , () -> {endItem(); rememberTitle();}),
                transition(DESCRIPTION, RATIONALE  , MdPattern.RATIONALE  , this::beginRationale               ),
                transition(DESCRIPTION, COMMENT    , MdPattern.COMMENT    , this::beginComment                 ),
                transition(DESCRIPTION, COVERS     , MdPattern.COVERS     , () -> {}                           ),
                transition(DESCRIPTION, DEPENDS    , MdPattern.DEPENDS    , () -> {}                           ),
                transition(DESCRIPTION, NEEDS      , MdPattern.NEEDS_INT  , this::addNeeds                     ),
                transition(DESCRIPTION, NEEDS      , MdPattern.NEEDS      , () -> {}                           ),
                transition(DESCRIPTION, TAGS       , MdPattern.TAGS_INT   , this::addTag                       ),
                transition(DESCRIPTION, TAGS       , MdPattern.TAGS       , () -> {}                           ),
                transition(DESCRIPTION, DESCRIPTION, MdPattern.EVERYTHING , this::appendDescription            ),

                transition(RATIONALE  , SPEC_ITEM  , MdPattern.ID         , this::beginItem                    ),
                transition(RATIONALE  , TITLE      , SECTION_TITLE        , () -> {endItem(); rememberTitle();}),
                transition(RATIONALE  , COMMENT    , MdPattern.COMMENT    , this::beginComment                 ),
                transition(RATIONALE  , COVERS     , MdPattern.COVERS     , () -> {}                           ),
                transition(RATIONALE  , DEPENDS    , MdPattern.DEPENDS    , () -> {}                           ),
                transition(RATIONALE  , NEEDS      , MdPattern.NEEDS_INT  , this::addNeeds                     ),
                transition(RATIONALE  , NEEDS      , MdPattern.NEEDS      , () -> {}                           ),
                transition(RATIONALE  , TAGS       , MdPattern.TAGS_INT   , this::addTag                       ),
                transition(RATIONALE  , TAGS       , MdPattern.TAGS       , () -> {}                           ),
                transition(RATIONALE  , RATIONALE  , MdPattern.EVERYTHING , this::appendRationale              ),

                transition(COMMENT    , SPEC_ITEM  , MdPattern.ID         , this::beginItem                    ),
                transition(COMMENT    , TITLE      , SECTION_TITLE        , () -> {endItem(); rememberTitle();}),
                transition(COMMENT    , COVERS     , MdPattern.COVERS     , () -> {}                           ),
                transition(COMMENT    , DEPENDS    , MdPattern.DEPENDS    , () -> {}                           ),
                transition(COMMENT    , NEEDS      , MdPattern.NEEDS_INT  , this::addNeeds                     ),
                transition(COMMENT    , NEEDS      , MdPattern.NEEDS      , () -> {}                           ),
                transition(COMMENT    , RATIONALE  , MdPattern.RATIONALE  , this::beginRationale               ),
                transition(COMMENT    , TAGS       , MdPattern.TAGS_INT   , this::addTag                       ),
                transition(COMMENT    , TAGS       , MdPattern.TAGS       , () -> {}                           ),
                transition(COMMENT    , COMMENT    , MdPattern.EVERYTHING , this::appendComment                ),


                // [impl->dsn~md.covers-list~1]
                transition(COVERS     , SPEC_ITEM  , MdPattern.ID         , this::beginItem                    ),
                transition(COVERS     , TITLE      , SECTION_TITLE        , () -> {endItem(); rememberTitle();}),
                transition(COVERS     , COVERS     , MdPattern.COVERS_REF , this::addCoverage                  ),
                transition(COVERS     , RATIONALE  , MdPattern.RATIONALE  , this::beginRationale               ),
                transition(COVERS     , COMMENT    , MdPattern.COMMENT    , this::beginComment                 ),
                transition(COVERS     , DEPENDS    , MdPattern.DEPENDS    , () -> {}                           ),
                transition(COVERS     , NEEDS      , MdPattern.NEEDS_INT  , this::addNeeds                     ),
                transition(COVERS     , NEEDS      , MdPattern.NEEDS      , () -> {}                           ),
                transition(COVERS     , COVERS     , MdPattern.EMPTY      , () -> {}                           ),
                transition(COVERS     , TAGS       , MdPattern.TAGS_INT   , this::addTag                       ),
                transition(COVERS     , TAGS       , MdPattern.TAGS       , () -> {}                           ),
                transition(COVERS     , START      , MdPattern.FORWARD    , () -> {endItem(); forward();}      ),

                // [impl->dsn~md.depends-list~1]
                transition(DEPENDS    , SPEC_ITEM  , MdPattern.ID         , this::beginItem                    ),
                transition(DEPENDS    , TITLE      , SECTION_TITLE        , () -> {endItem(); rememberTitle();}),
                transition(DEPENDS    , DEPENDS    , MdPattern.DEPENDS_REF, this::addDependency                ),
                transition(DEPENDS    , RATIONALE  , MdPattern.RATIONALE  , this::beginRationale               ),
                transition(DEPENDS    , COMMENT    , MdPattern.COMMENT    , this::beginComment                 ),
                transition(DEPENDS    , DEPENDS    , MdPattern.DEPENDS    , () -> {}                           ),
                transition(DEPENDS    , NEEDS      , MdPattern.NEEDS_INT  , this::addNeeds                     ),
                transition(DEPENDS    , NEEDS      , MdPattern.NEEDS      , () -> {}                           ),
                transition(DEPENDS    , DEPENDS    , MdPattern.EMPTY      , () -> {}                           ),
                transition(DEPENDS    , COVERS     , MdPattern.COVERS     , () -> {}                           ),
                transition(DEPENDS    , TAGS       , MdPattern.TAGS_INT   , this::addTag                       ),
                transition(DEPENDS    , TAGS       , MdPattern.TAGS       , () -> {}                           ),
                transition(DEPENDS    , START      , MdPattern.FORWARD    , () -> {endItem(); forward();}      ),

                // [impl->dsn~md.needs-coverage-list-single-line~2]
                // [impl->dsn~md.needs-coverage-list~1]
                transition(NEEDS      , SPEC_ITEM  , MdPattern.ID         , this::beginItem                    ),
                transition(NEEDS      , TITLE      , SECTION_TITLE        , () -> {endItem(); rememberTitle();}),
                transition(NEEDS      , RATIONALE  , MdPattern.RATIONALE  , this::beginRationale               ),
                transition(NEEDS      , COMMENT    , MdPattern.COMMENT    , this::beginComment                 ),
                transition(NEEDS      , DEPENDS    , MdPattern.DEPENDS    , () -> {}                           ),
                transition(NEEDS      , NEEDS      , MdPattern.NEEDS_INT  , this::addNeeds                     ),
                transition(NEEDS      , NEEDS      , MdPattern.NEEDS_REF  , this::addNeeds                     ),
                transition(NEEDS      , NEEDS      , MdPattern.EMPTY      , () -> {}                           ),
                transition(NEEDS      , COVERS     , MdPattern.COVERS     , () -> {}                           ),
                transition(NEEDS      , TAGS       , MdPattern.TAGS_INT   , this::addTag                       ),
                transition(NEEDS      , TAGS       , MdPattern.TAGS       , () -> {}                           ),
                transition(NEEDS      , START      , MdPattern.FORWARD    , () -> {endItem(); forward();}      ),

                transition(TAGS       , TAGS       , MdPattern.TAG_ENTRY  , this::addTag                       ),
                transition(TAGS       , SPEC_ITEM  , MdPattern.ID         , this::beginItem                    ),
                transition(TAGS       , TITLE      , SECTION_TITLE        , () -> {endItem(); rememberTitle();}),
                transition(TAGS       , RATIONALE  , MdPattern.RATIONALE  , this::beginRationale               ),
                transition(TAGS       , COMMENT    , MdPattern.COMMENT    , this::beginComment                 ),
                transition(TAGS       , DEPENDS    , MdPattern.DEPENDS    , () -> {}                           ),
                transition(TAGS       , NEEDS      , MdPattern.NEEDS_INT  , this::addNeeds                     ),
                transition(TAGS       , NEEDS      , MdPattern.NEEDS      , () -> {}                           ),
                transition(TAGS       , NEEDS      , MdPattern.EMPTY      , () -> {}                           ),
                transition(TAGS       , COVERS     , MdPattern.COVERS     , () -> {}                           ),
                transition(TAGS       , TAGS       , MdPattern.TAGS       , () -> {}                           ),
                transition(TAGS       , TAGS       , MdPattern.TAGS_INT   , this::addTag                       ),
                transition(TAGS       , START      , MdPattern.FORWARD    , () -> {endItem(); forward();}      )
        };
        // @formatter:on
    }

    private static Transition transition(final LineParserState from, final LineParserState to,
            final MdPattern pattern, final TransitionAction action)
    {
        return new Transition(from, to, pattern.getPattern(), action);
    }
}
