package openfasttrack.importer.markdown;

/*
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 hamstercommunity
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

class Transition
{
    private final State from;
    private final State to;
    private final MdPattern markdownPattern;
    private final TransitionAction transition;

    public Transition(final State from, final State to, final MdPattern markdownPattern,
            final TransitionAction transition)
    {
        super();
        this.from = from;
        this.to = to;
        this.markdownPattern = markdownPattern;
        this.transition = transition;
    }

    public State getFrom()
    {
        return this.from;
    }

    public State getTo()
    {
        return this.to;
    }

    public MdPattern getMarkdownPattern()
    {
        return this.markdownPattern;
    }

    public TransitionAction getTransition()
    {
        return this.transition;
    }
}