package openfasttrack.importer.markdown;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import openfasttrack.core.SpecificationItem;
import openfasttrack.core.SpecificationItemId;
import openfasttrack.importer.ImportEventListener;
import openfasttrack.importer.Importer;

public class MarkdownImporter implements Importer
{

    private final BufferedReader reader;
    private final ImportEventListener listener;
    private final MarkdownImporterStateMachine stateMachine;

    public MarkdownImporter(final Reader reader, final ImportEventListener listener)
    {
        super();
        this.reader = new BufferedReader(reader);
        this.listener = listener;
        this.stateMachine = new MarkdownImporterStateMachine(this.transitions);
    }

    @Override
    public List<SpecificationItem> runImport()
    {

        String line;
        int lineNumber = 0;
        try
        {

            while ((line = this.reader.readLine()) != null)
            {
                ++lineNumber;
                this.stateMachine.step(line);
                System.out.println(lineNumber + " : " + line);
            }

        } catch (final IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    // (ID + Title), Description, Rationale, Comment, Covers, Depends, Needs

    // @formatter:off
    private final Transition[] transitions = {
            _T(State.START      , State.SPEC_ITEM  , MdPattern.ID         , this::emitNewId        ),
            _T(State.START      , State.OUTSIDE    , MdPattern.EVERYTHING , () -> {}               ),

            _T(State.OUTSIDE    , State.SPEC_ITEM  , MdPattern.ID         , this::emitNewId        ),

            _T(State.SPEC_ITEM  , State.SPEC_ITEM  , MdPattern.ID         , this::emitNewId        ),
            _T(State.SPEC_ITEM  , State.RATIONALE  , MdPattern.RATIONALE  , () -> {}               ),
            _T(State.SPEC_ITEM  , State.COMMENT    , MdPattern.COMMENT    , () -> {}               ),
            _T(State.SPEC_ITEM  , State.COVERS     , MdPattern.COVERS     , () -> {}               ),
            _T(State.SPEC_ITEM  , State.DEPENDS    , MdPattern.DEPENDS    , () -> {}               ),
            _T(State.SPEC_ITEM  , State.NEEDS      , MdPattern.NEEDS      , this::addNeeds         ),
            _T(State.SPEC_ITEM  , State.DESCRIPTION, MdPattern.EVERYTHING , this::appendDescription),

            _T(State.DESCRIPTION, State.SPEC_ITEM  , MdPattern.ID         , this::emitNewId        ),
            _T(State.DESCRIPTION, State.RATIONALE  , MdPattern.RATIONALE  , () -> {}               ),
            _T(State.DESCRIPTION, State.COMMENT    , MdPattern.COMMENT    , () -> {}               ),
            _T(State.DESCRIPTION, State.COVERS     , MdPattern.COVERS     , () -> {}               ),
            _T(State.DESCRIPTION, State.DEPENDS    , MdPattern.DEPENDS    , () -> {}               ),
            _T(State.DESCRIPTION, State.NEEDS      , MdPattern.NEEDS      , this::addNeeds         ),
            _T(State.DESCRIPTION, State.DESCRIPTION, MdPattern.EVERYTHING , this::appendDescription),

            _T(State.RATIONALE  , State.SPEC_ITEM  , MdPattern.ID         , this::emitNewId        ),
            _T(State.RATIONALE  , State.COMMENT    , MdPattern.COMMENT    , () -> {}               ),
            _T(State.RATIONALE  , State.COVERS     , MdPattern.COVERS     , () -> {}               ),
            _T(State.RATIONALE  , State.DEPENDS    , MdPattern.DEPENDS    , () -> {}               ),
            _T(State.RATIONALE  , State.NEEDS      , MdPattern.NEEDS      , this::addNeeds         ),
            _T(State.RATIONALE  , State.RATIONALE  , MdPattern.EVERYTHING , this::appendRationale  ),

            _T(State.COMMENT    , State.SPEC_ITEM  , MdPattern.ID         , this::emitNewId        ),
            _T(State.COMMENT    , State.COVERS     , MdPattern.COVERS     , () -> {}               ),
            _T(State.COMMENT    , State.DEPENDS    , MdPattern.DEPENDS    , () -> {}               ),
            _T(State.COMMENT    , State.NEEDS      , MdPattern.NEEDS      , this::addNeeds         ),
            _T(State.COMMENT    , State.RATIONALE  , MdPattern.EVERYTHING , () -> {}               ),
            _T(State.COMMENT    , State.COMMENT    , MdPattern.COMMENT    , this::appendComment    ),

            _T(State.COVERS     , State.SPEC_ITEM  , MdPattern.ID         , this::emitNewId        ),
            _T(State.COVERS     , State.COVERS     , MdPattern.COVERS_REF , this::addCoverage      ),
            _T(State.COVERS     , State.RATIONALE  , MdPattern.RATIONALE  , () -> {}               ),
            _T(State.COVERS     , State.COMMENT    , MdPattern.COMMENT    , () -> {}               ),
            _T(State.COVERS     , State.DEPENDS    , MdPattern.DEPENDS    , () -> {}               ),
            _T(State.COVERS     , State.NEEDS      , MdPattern.NEEDS      , this::addNeeds         ),
            _T(State.COVERS     , State.COVERS     , MdPattern.EMPTY      , () -> {}               ),

            _T(State.DEPENDS    , State.SPEC_ITEM  , MdPattern.ID         , this::emitNewId        ),
            _T(State.DEPENDS    , State.DEPENDS    , MdPattern.DEPENDS_REF, this::addDependency    ),
            _T(State.DEPENDS    , State.RATIONALE  , MdPattern.RATIONALE  , () -> {}               ),
            _T(State.DEPENDS    , State.COMMENT    , MdPattern.COMMENT    , () -> {}               ),
            _T(State.DEPENDS    , State.DEPENDS    , MdPattern.DEPENDS    , () -> {}               ),
            _T(State.DEPENDS    , State.NEEDS      , MdPattern.NEEDS      , this::addNeeds         ),
            _T(State.DEPENDS    , State.DEPENDS    , MdPattern.EMPTY      , () -> {}               ),

            _T(State.NEEDS      , State.SPEC_ITEM  , MdPattern.ID         , this::emitNewId        ),
            _T(State.NEEDS      , State.RATIONALE  , MdPattern.RATIONALE  , () -> {}               ),
            _T(State.NEEDS      , State.COMMENT    , MdPattern.COMMENT    , () -> {}               ),
            _T(State.NEEDS      , State.DEPENDS    , MdPattern.DEPENDS    , () -> {}               ),
            _T(State.NEEDS      , State.NEEDS      , MdPattern.NEEDS      , this::addNeeds         ),
            _T(State.NEEDS      , State.NEEDS      , MdPattern.EMPTY      , () -> {}               )
    };
    // @formatter:on

    private final Transition _T(final State from, final State to, final MdPattern pattern,
            final TransitionAction action)
    {
        return new Transition(from, to, pattern, action);
    }

    private void appendComment()
    {
        // TODO Auto-generated method stub
        return;
    }

    private void addDependency()
    {
        // TODO Auto-generated method stub
        return;
    }

    private void appendDescription()
    {
        // TODO Auto-generated method stub
        return;
    }

    private void addNeeds()
    {
        // TODO Auto-generated method stub
        return;
    }

    private void appendRationale()
    {
        // this.listener.appendRationale(this.stateMachine.getLastToken());
    }

    private void emitNewId()
    {
        final String idText = this.stateMachine.getLastToken();
        final SpecificationItemId id = new SpecificationItemId.Builder(idText).build();
        this.listener.startSpecificationItem();
        this.listener.setId(id);
    }

    private void addCoverage()
    {
        this.listener.addCoveredId(this.stateMachine.getLastToken());
    }
}
