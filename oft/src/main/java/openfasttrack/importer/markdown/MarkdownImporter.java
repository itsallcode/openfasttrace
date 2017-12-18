package openfasttrack.importer.markdown;

/*-
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 - 2017 hamstercommunity
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;

import openfasttrack.core.SpecificationItemId;
import openfasttrack.importer.ImportEventListener;
import openfasttrack.importer.Importer;
import openfasttrack.importer.ImporterException;

class MarkdownImporter implements Importer
{
    private final static Logger LOG = Logger.getLogger(MarkdownImporter.class.getName());

    // @formatter:off
    private final  Transition[] transitions = {
        transition(State.START      , State.SPEC_ITEM  , MdPattern.ID         , this::beginItem                            ),
        transition(State.START      , State.TITLE      , MdPattern.TITLE      , this::rememberTitle                        ),
        transition(State.START      , State.OUTSIDE    , MdPattern.EVERYTHING , () -> {}                                   ),
    
        transition(State.TITLE      , State.SPEC_ITEM  , MdPattern.ID         , this::beginItem                            ),
        transition(State.TITLE      , State.TITLE      , MdPattern.EMPTY      , () -> {}                                   ),
        transition(State.TITLE      , State.OUTSIDE    , MdPattern.EVERYTHING , this::resetTitle                           ),
    
        transition(State.OUTSIDE    , State.SPEC_ITEM  , MdPattern.ID         , this::beginItem                            ),
    
        transition(State.SPEC_ITEM  , State.SPEC_ITEM  , MdPattern.ID         , this::beginItem                            ),
        transition(State.SPEC_ITEM  , State.TITLE      , MdPattern.TITLE      , this::endItem                              ),
        transition(State.SPEC_ITEM  , State.RATIONALE  , MdPattern.RATIONALE  , this::beginRationale                       ),
        transition(State.SPEC_ITEM  , State.COMMENT    , MdPattern.COMMENT    , this::beginComment                         ),
        transition(State.SPEC_ITEM  , State.COVERS     , MdPattern.COVERS     , () -> {}                                   ),
        transition(State.SPEC_ITEM  , State.DEPENDS    , MdPattern.DEPENDS    , () -> {}                                   ),
        transition(State.SPEC_ITEM  , State.NEEDS      , MdPattern.NEEDS      , this::addNeeds                             ),
        transition(State.SPEC_ITEM  , State.DESCRIPTION, MdPattern.EVERYTHING , this::beginDescription                     ),
    
        transition(State.DESCRIPTION, State.SPEC_ITEM  , MdPattern.ID         , () -> {endDescription(); beginItem();}     ),
        transition(State.DESCRIPTION, State.TITLE      , MdPattern.TITLE      , () -> {endDescription(); endItem();}       ),
        transition(State.DESCRIPTION, State.RATIONALE  , MdPattern.RATIONALE  , () -> {endDescription(); beginRationale();}),
        transition(State.DESCRIPTION, State.COMMENT    , MdPattern.COMMENT    , () -> {endDescription(); beginComment();}  ),
        transition(State.DESCRIPTION, State.COVERS     , MdPattern.COVERS     , this::endDescription                       ),
        transition(State.DESCRIPTION, State.DEPENDS    , MdPattern.DEPENDS    , this::endDescription                       ),
        transition(State.DESCRIPTION, State.NEEDS      , MdPattern.NEEDS      , () -> {endDescription(); addNeeds();}      ),
        transition(State.DESCRIPTION, State.DESCRIPTION, MdPattern.EVERYTHING , this::appendDescription                    ),
    
        transition(State.RATIONALE  , State.SPEC_ITEM  , MdPattern.ID         , () -> {endRationale(); beginItem();}       ),
        transition(State.RATIONALE  , State.TITLE      , MdPattern.TITLE      , () -> {endRationale(); endItem();}         ),
        transition(State.RATIONALE  , State.COMMENT    , MdPattern.COMMENT    , () -> {endRationale(); beginComment();}    ),
        transition(State.RATIONALE  , State.COVERS     , MdPattern.COVERS     , this::endRationale                         ),
        transition(State.RATIONALE  , State.DEPENDS    , MdPattern.DEPENDS    , this::endRationale                         ),
        transition(State.RATIONALE  , State.NEEDS      , MdPattern.NEEDS      , () -> {endRationale(); addNeeds();}        ),
        transition(State.RATIONALE  , State.RATIONALE  , MdPattern.EVERYTHING , this::appendRationale                      ),
    
        transition(State.COMMENT    , State.SPEC_ITEM  , MdPattern.ID         , () -> {endComment(); beginItem();}         ),
        transition(State.COMMENT    , State.TITLE      , MdPattern.TITLE      , () -> {endComment(); endItem();}           ),
        transition(State.COMMENT    , State.COVERS     , MdPattern.COVERS     , this::endComment                           ),
        transition(State.COMMENT    , State.DEPENDS    , MdPattern.DEPENDS    , this::endComment                           ),
        transition(State.COMMENT    , State.NEEDS      , MdPattern.NEEDS      , () -> {endComment(); addNeeds();}          ),
        transition(State.COMMENT    , State.RATIONALE  , MdPattern.RATIONALE  , () -> {endComment(); beginRationale();}    ),
        transition(State.COMMENT    , State.COMMENT    , MdPattern.EVERYTHING , this::appendComment                        ),
        // [impl->dsn~md.covers_list~1]
        transition(State.COVERS     , State.SPEC_ITEM  , MdPattern.ID         , this::beginItem                            ),
        transition(State.COVERS     , State.TITLE      , MdPattern.TITLE      , this::endItem                              ),
        transition(State.COVERS     , State.COVERS     , MdPattern.COVERS_REF , this::addCoverage                          ),
        transition(State.COVERS     , State.RATIONALE  , MdPattern.RATIONALE  , this::beginRationale                       ),
        transition(State.COVERS     , State.COMMENT    , MdPattern.COMMENT    , this::beginComment                         ),
        transition(State.COVERS     , State.DEPENDS    , MdPattern.DEPENDS    , () -> {}                                   ),
        transition(State.COVERS     , State.NEEDS      , MdPattern.NEEDS      , this::addNeeds                             ),
        transition(State.COVERS     , State.COVERS     , MdPattern.EMPTY      , () -> {}                                   ),
        // [impl->dsn~md.depends_list~1]
        transition(State.DEPENDS    , State.SPEC_ITEM  , MdPattern.ID         , this::beginItem                            ),
        transition(State.DEPENDS    , State.TITLE      , MdPattern.TITLE      , this::endItem                              ),
        transition(State.DEPENDS    , State.DEPENDS    , MdPattern.DEPENDS_REF, this::addDependency                        ),
        transition(State.DEPENDS    , State.RATIONALE  , MdPattern.RATIONALE  , this::beginRationale                       ),
        transition(State.DEPENDS    , State.COMMENT    , MdPattern.COMMENT    , this::beginComment                         ),
        transition(State.DEPENDS    , State.DEPENDS    , MdPattern.DEPENDS    , () -> {}                                   ),
        transition(State.DEPENDS    , State.NEEDS      , MdPattern.NEEDS      , this::addNeeds                             ),
        transition(State.DEPENDS    , State.DEPENDS    , MdPattern.EMPTY      , () -> {}                                   ),
    
        transition(State.NEEDS      , State.SPEC_ITEM  , MdPattern.ID         , this::beginItem                            ),
        transition(State.NEEDS      , State.TITLE      , MdPattern.TITLE      , this::endItem                              ),
        transition(State.NEEDS      , State.RATIONALE  , MdPattern.RATIONALE  , this::beginRationale                       ),
        transition(State.NEEDS      , State.COMMENT    , MdPattern.COMMENT    , this::beginComment                         ),
        transition(State.NEEDS      , State.DEPENDS    , MdPattern.DEPENDS    , () -> {}                                   ),
        transition(State.NEEDS      , State.NEEDS      , MdPattern.NEEDS      , this::addNeeds                             ),
        transition(State.NEEDS      , State.NEEDS      , MdPattern.EMPTY      , () -> {}                                   )
    };

    private final String fileName;
    private final BufferedReader reader;
    private final ImportEventListener listener;
    private final MarkdownImporterStateMachine stateMachine;
    private String lastTitle = null;
    private boolean inSpecificationItem;
    private StringBuilder lastDescription;
    private StringBuilder lastRationale;
    private StringBuilder lastComment;
    private int lineNumber = 0;

    MarkdownImporter(final String fileName, final Reader reader, final ImportEventListener listener)
    {
        this.fileName = fileName;
        this.reader = new BufferedReader(reader);
        this.listener = listener;
        this.stateMachine = new MarkdownImporterStateMachine(this.transitions);
    }

    @Override
    public void runImport()
    {
        LOG.fine(() -> "Starting import of file " + this.fileName);
        String line;
        this.lineNumber = 0;
        try
        {
            while ((line = this.reader.readLine()) != null)
            {
                ++this.lineNumber;
                this.stateMachine.step(line);
            }
        }
        catch (final IOException exception)
        {
            throw new ImporterException("IO exception after " + this.fileName + ":" + this.lineNumber,
                    exception);
        }
        finishImport();
    }

    private void finishImport()
    {
        if (this.inSpecificationItem)
        {
            this.listener.endSpecificationItem();
        }
    }

    private static final Transition transition(final State from, final State to, final MdPattern pattern,
            final TransitionAction action)
    {
        return new Transition(from, to, pattern, action);
    }

    private void beginItem()
    {
        cleanUpLastItem();
        this.inSpecificationItem = true;
        informListenerAboutNewItem();
    }

    private void cleanUpLastItem()
    {
        if(this.inSpecificationItem)
        {
            endItem();
        }
    }

    private void informListenerAboutNewItem()
    {
        final String idText = this.stateMachine.getLastToken();
        final SpecificationItemId id = new SpecificationItemId.Builder(idText).build();
        this.listener.beginSpecificationItem();
        this.listener.setId(id);
        this.listener.setLocation(this.fileName, this.lineNumber);
        if(this.lastTitle != null)
        {
            this.listener.setTitle(this.lastTitle);
        }
    }

    private void endItem()
    {
        this.inSpecificationItem = false;
        resetTitle();
        this.listener.endSpecificationItem();
    }
    
    private void beginDescription()
    {
        this.lastDescription = new StringBuilder(this.stateMachine.getLastToken());
    }

    private void appendDescription()
    {
        this.lastDescription.append(System.lineSeparator()).append(this.stateMachine.getLastToken());
    }
    
    private void endDescription()
    {
        this.listener.appendDescription(this.lastDescription.toString().trim());
        this.lastDescription = null;
    }
    
    private void beginRationale()
    {
        this.lastRationale = new StringBuilder();
    }
    
    private void appendRationale()
    {
        if(this.lastRationale.length() > 0)
        {
            this.lastRationale.append(System.lineSeparator());
        }
        this.lastRationale.append(this.stateMachine.getLastToken());
    }
    
    private void endRationale()
    {
        this.listener.appendRationale(this.lastRationale.toString().trim());
        this.lastRationale = null;
    }

    private void beginComment()
    {
        this.lastComment= new StringBuilder();
    }

    private void appendComment()
    {
        if(this.lastComment.length() > 0)
        {
            this.lastComment.append(System.lineSeparator());
        }
        this.lastComment.append(this.stateMachine.getLastToken());
    }
    
    private void endComment()
    {
        this.listener.appendComment(this.lastComment.toString().trim());
        this.lastComment = null;
    }

    private void addDependency()
    {
        final SpecificationItemId.Builder builder = new SpecificationItemId.Builder(this.stateMachine.getLastToken());
        this.listener.addDependsOnId(builder.build());
    }

    private void addNeeds()
    {
        final String artifactTypes = this.stateMachine.getLastToken();
        for(final String artifactType : artifactTypes.split(",\\s*"))
        {
            this.listener.addNeededArtifactType(artifactType);
        }
    }

    private void rememberTitle()
    {
        this.lastTitle = this.stateMachine.getLastToken();
    }

    private void resetTitle()
    {
        this.lastTitle = null;
    }

    private void addCoverage()
    {
        this.listener.addCoveredId(SpecificationItemId.parseId(this.stateMachine.getLastToken()));
    }
}
