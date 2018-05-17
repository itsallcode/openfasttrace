package org.itsallcode.openfasttrace.importer.markdown;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2017 itsallcode.org
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

import static org.itsallcode.openfasttrace.importer.markdown.State.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;

import org.itsallcode.openfasttrace.core.ItemStatus;
import org.itsallcode.openfasttrace.core.SpecificationItemId;
import org.itsallcode.openfasttrace.importer.ImportEventListener;
import org.itsallcode.openfasttrace.importer.Importer;
import org.itsallcode.openfasttrace.importer.ImporterException;
import org.itsallcode.openfasttrace.importer.input.InputFile;

class MarkdownImporter implements Importer
{
    private static final Logger LOG = Logger.getLogger(MarkdownImporter.class.getName());

    // @formatter:off
    private final  Transition[] transitions = {
        transition(START      , SPEC_ITEM  , MdPattern.ID         , this::beginItem                            ),
        transition(START      , TITLE      , MdPattern.TITLE      , this::rememberTitle                        ),
        transition(START      , OUTSIDE    , MdPattern.EVERYTHING , () -> {}                                   ),
    
        transition(TITLE      , SPEC_ITEM  , MdPattern.ID         , this::beginItem                            ),
        transition(TITLE      , TITLE      , MdPattern.EMPTY      , () -> {}                                   ),
        transition(TITLE      , OUTSIDE    , MdPattern.EVERYTHING , this::resetTitle                           ),
    
        transition(OUTSIDE    , SPEC_ITEM  , MdPattern.ID         , this::beginItem                            ),
    
        transition(SPEC_ITEM  , SPEC_ITEM  , MdPattern.ID         , this::beginItem                            ),
        transition(SPEC_ITEM  , SPEC_ITEM  , MdPattern.STATUS     , this::setStatus                            ),
        transition(SPEC_ITEM  , TITLE      , MdPattern.TITLE      , this::endItem                              ),
        transition(SPEC_ITEM  , RATIONALE  , MdPattern.RATIONALE  , this::beginRationale                       ),
        transition(SPEC_ITEM  , COMMENT    , MdPattern.COMMENT    , this::beginComment                         ),
        transition(SPEC_ITEM  , COVERS     , MdPattern.COVERS     , () -> {}                                   ),
        transition(SPEC_ITEM  , DEPENDS    , MdPattern.DEPENDS    , () -> {}                                   ),
        transition(SPEC_ITEM  , NEEDS      , MdPattern.NEEDS_INT  , this::addNeeds                             ),
        transition(SPEC_ITEM  , NEEDS      , MdPattern.NEEDS      , () -> {}                                   ),
        transition(SPEC_ITEM  , DESCRIPTION, MdPattern.DESCRIPTION, this::beginDescription                     ),
        transition(SPEC_ITEM  , DESCRIPTION, MdPattern.NOT_EMPTY  , this::beginDescription                     ),
        transition(SPEC_ITEM  , TAGS       , MdPattern.TAGS_INT   , this::addTag                               ),
        transition(SPEC_ITEM  , TAGS       , MdPattern.TAGS       , () -> {}                                   ),
    
        transition(DESCRIPTION, SPEC_ITEM  , MdPattern.ID         , () -> {endDescription(); beginItem();}     ),
        transition(DESCRIPTION, TITLE      , MdPattern.TITLE      , () -> {endDescription(); endItem();}       ),
        transition(DESCRIPTION, RATIONALE  , MdPattern.RATIONALE  , () -> {endDescription(); beginRationale();}),
        transition(DESCRIPTION, COMMENT    , MdPattern.COMMENT    , () -> {endDescription(); beginComment();}  ),
        transition(DESCRIPTION, COVERS     , MdPattern.COVERS     , this::endDescription                       ),
        transition(DESCRIPTION, DEPENDS    , MdPattern.DEPENDS    , this::endDescription                       ),
        transition(DESCRIPTION, NEEDS      , MdPattern.NEEDS_INT  , () -> {endDescription(); addNeeds();}      ),
        transition(DESCRIPTION, NEEDS      , MdPattern.NEEDS      , this::endDescription                       ),
        transition(DESCRIPTION, DESCRIPTION, MdPattern.EVERYTHING , this::appendDescription                    ),
        transition(DESCRIPTION, TAGS       , MdPattern.TAGS_INT   , this::addTag                               ),
        transition(DESCRIPTION, TAGS       , MdPattern.TAGS       , () -> {}                                   ),

    
        transition(RATIONALE  , SPEC_ITEM  , MdPattern.ID         , () -> {endRationale(); beginItem();}       ),
        transition(RATIONALE  , TITLE      , MdPattern.TITLE      , () -> {endRationale(); endItem();}         ),
        transition(RATIONALE  , COMMENT    , MdPattern.COMMENT    , () -> {endRationale(); beginComment();}    ),
        transition(RATIONALE  , COVERS     , MdPattern.COVERS     , this::endRationale                         ),
        transition(RATIONALE  , DEPENDS    , MdPattern.DEPENDS    , this::endRationale                         ),
        transition(RATIONALE  , NEEDS      , MdPattern.NEEDS_INT  , () -> {endRationale(); addNeeds();}        ),
        transition(RATIONALE  , NEEDS      , MdPattern.NEEDS      , this::endRationale                         ),
        transition(RATIONALE  , RATIONALE  , MdPattern.EVERYTHING , this::appendRationale                      ),
        transition(RATIONALE  , TAGS       , MdPattern.TAGS_INT   , this::addTag                               ),
        transition(RATIONALE  , TAGS       , MdPattern.TAGS       , () -> {}                                   ),
    
        transition(COMMENT    , SPEC_ITEM  , MdPattern.ID         , () -> {endComment(); beginItem();}         ),
        transition(COMMENT    , TITLE      , MdPattern.TITLE      , () -> {endComment(); endItem();}           ),
        transition(COMMENT    , COVERS     , MdPattern.COVERS     , this::endComment                           ),
        transition(COMMENT    , DEPENDS    , MdPattern.DEPENDS    , this::endComment                           ),
        transition(COMMENT    , NEEDS      , MdPattern.NEEDS_INT  , () -> {endComment(); addNeeds();}          ),
        transition(COMMENT    , NEEDS      , MdPattern.NEEDS      , this::endComment                           ),
        transition(COMMENT    , RATIONALE  , MdPattern.RATIONALE  , () -> {endComment(); beginRationale();}    ),
        transition(COMMENT    , COMMENT    , MdPattern.EVERYTHING , this::appendComment                        ),
        transition(COMMENT    , TAGS       , MdPattern.TAGS_INT   , this::addTag                               ),
        transition(COMMENT    , TAGS       , MdPattern.TAGS       , () -> {}                                   ),

        
        // [impl->dsn~md.covers-list~1]
        transition(COVERS     , SPEC_ITEM  , MdPattern.ID         , this::beginItem                            ),
        transition(COVERS     , TITLE      , MdPattern.TITLE      , this::endItem                              ),
        transition(COVERS     , COVERS     , MdPattern.COVERS_REF , this::addCoverage                          ),
        transition(COVERS     , RATIONALE  , MdPattern.RATIONALE  , this::beginRationale                       ),
        transition(COVERS     , COMMENT    , MdPattern.COMMENT    , this::beginComment                         ),
        transition(COVERS     , DEPENDS    , MdPattern.DEPENDS    , () -> {}                                   ),
        transition(COVERS     , NEEDS      , MdPattern.NEEDS_INT  , this::addNeeds                             ),
        transition(COVERS     , NEEDS      , MdPattern.NEEDS      , () -> {}                                   ),
        transition(COVERS     , COVERS     , MdPattern.EMPTY      , () -> {}                                   ),
        transition(COVERS     , TAGS       , MdPattern.TAGS_INT   , this::addTag                               ),
        transition(COVERS     , TAGS       , MdPattern.TAGS       , () -> {}                                   ),

        // [impl->dsn~md.depends-list~1]
        transition(DEPENDS    , SPEC_ITEM  , MdPattern.ID         , this::beginItem                            ),
        transition(DEPENDS    , TITLE      , MdPattern.TITLE      , this::endItem                              ),
        transition(DEPENDS    , DEPENDS    , MdPattern.DEPENDS_REF, this::addDependency                        ),
        transition(DEPENDS    , RATIONALE  , MdPattern.RATIONALE  , this::beginRationale                       ),
        transition(DEPENDS    , COMMENT    , MdPattern.COMMENT    , this::beginComment                         ),
        transition(DEPENDS    , DEPENDS    , MdPattern.DEPENDS    , () -> {}                                   ),
        transition(DEPENDS    , NEEDS      , MdPattern.NEEDS_INT  , this::addNeeds                             ),
        transition(DEPENDS    , NEEDS      , MdPattern.NEEDS      , () -> {}                                   ),
        transition(DEPENDS    , DEPENDS    , MdPattern.EMPTY      , () -> {}                                   ),
        transition(DEPENDS    , COVERS     , MdPattern.COVERS     , () -> {}                                   ),
        transition(DEPENDS    , TAGS       , MdPattern.TAGS_INT   , this::addTag                               ),
        transition(DEPENDS    , TAGS       , MdPattern.TAGS       , () -> {}                                   ),

        // [impl->dsn~md.needs-coverage-list~2]
        // [impl->dsn~md.needs-coverage-list-compact~1]
        transition(NEEDS      , SPEC_ITEM  , MdPattern.ID         , this::beginItem                            ),
        transition(NEEDS      , TITLE      , MdPattern.TITLE      , this::endItem                              ),
        transition(NEEDS      , RATIONALE  , MdPattern.RATIONALE  , this::beginRationale                       ),
        transition(NEEDS      , COMMENT    , MdPattern.COMMENT    , this::beginComment                         ),
        transition(NEEDS      , DEPENDS    , MdPattern.DEPENDS    , () -> {}                                   ),
        transition(NEEDS      , NEEDS      , MdPattern.NEEDS_INT  , this::addNeeds                             ),
        transition(NEEDS      , NEEDS      , MdPattern.NEEDS_REF  , this::addNeeds                             ),
        transition(NEEDS      , NEEDS      , MdPattern.EMPTY      , () -> {}                                   ),
        transition(NEEDS      , COVERS     , MdPattern.COVERS     , () -> {}                                   ),
        transition(NEEDS      , TAGS       , MdPattern.TAGS_INT   , this::addTag                               ),
        transition(NEEDS      , TAGS       , MdPattern.TAGS       , () -> {}                                   ),
        
        transition(TAGS       , TAGS       , MdPattern.TAG_ENTRY  , this::addTag                               ),
        transition(TAGS       , SPEC_ITEM  , MdPattern.ID         , this::beginItem                            ),
        transition(TAGS       , TITLE      , MdPattern.TITLE      , this::endItem                              ),
        transition(TAGS       , RATIONALE  , MdPattern.RATIONALE  , this::beginRationale                       ),
        transition(TAGS       , COMMENT    , MdPattern.COMMENT    , this::beginComment                         ),
        transition(TAGS       , DEPENDS    , MdPattern.DEPENDS    , () -> {}                                   ),
        transition(TAGS       , NEEDS      , MdPattern.NEEDS_INT  , this::addNeeds                             ),
        transition(TAGS       , NEEDS      , MdPattern.NEEDS      , () -> {}                                   ),
        transition(TAGS       , NEEDS      , MdPattern.EMPTY      , () -> {}                                   ),
        transition(TAGS       , COVERS     , MdPattern.COVERS     , () -> {}                                   ),
        transition(TAGS       , TAGS       , MdPattern.TAGS       , () -> {}                                   ),
        transition(TAGS       , TAGS       , MdPattern.TAGS_INT   , this::addTag                               ),
    };

    private final InputFile file;
    private final ImportEventListener listener;
    private final MarkdownImporterStateMachine stateMachine;
    private String lastTitle = null;
    private boolean inSpecificationItem;
    private StringBuilder lastDescription;
    private StringBuilder lastRationale;
    private StringBuilder lastComment;
    private int lineNumber = 0;

    MarkdownImporter(final InputFile fileName, final ImportEventListener listener)
    {
        this.file = fileName;
        this.listener = listener;
        this.stateMachine = new MarkdownImporterStateMachine(this.transitions);
    }

    @Override
    public void runImport()
    {
        LOG.fine(() -> "Starting import of file " + this.file);
        String line;
        this.lineNumber = 0;
        try(BufferedReader reader = this.file.createReader())
        {
            while ((line = reader.readLine()) != null)
            {
                ++this.lineNumber;
                this.stateMachine.step(line);
            }
        }
        catch (final IOException exception)
        {
            throw new ImporterException("Error reading file " + this.file, exception);
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
        this.listener.setLocation(this.file.getPath().toString(), this.lineNumber);
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
    
    private void setStatus()
    {
        this.listener.setStatus(ItemStatus.parseString(this.stateMachine.getLastToken()));
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
    
    private void addTag()
    {
        final String tags = this.stateMachine.getLastToken();
        for(final String tag : tags.split(",\\s*"))
        {
            this.listener.addTag(tag);
        }
    }
}
