package org.itsallcode.openfasttrace.report.view.html;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
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

import static org.itsallcode.openfasttrace.report.view.html.MarkdownLineState.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class MarkdownLineStateMachine
{
    private static final int INCLUDE_EMPTY_STRINGS = -1;
    private static final String P_ANY = ".*";
    private static final String P_OL_LI = "^ {0,3}[0-9]+\\..*";
    private static final String P_UL_LI = "^ {0,3}[-+*].*";
    private static final String P_PRE = "^    .*";
    private static final String P_LIST_CONT = ".+";
    private static final String P_TERM = "^$";
    private final List<MarkdownLineTransition> transitions = new ArrayList<>();

    public MarkdownLineStateMachine()
    {
        initializeTransitions();
    }

    // Duplicate strings help making this easier to understand.
    @SuppressWarnings("squid:S1192")
    protected void initializeTransitions()
    {
        // @formatter:off
        t(START         , PREFORMATTED  , P_PRE      , ""          , "<pre>"   , trimPre());
        t(START         , UNORDERED_LIST, P_UL_LI    , ""          , "<ul><li>", trimBullet());
        t(START         , ORDERED_LIST  , P_OL_LI    , ""          , "<ol><li>", trimEnum());
        t(START         , PARAGRAPH     , P_ANY      , ""          , "<p>"     , String::trim);
        t(START         , TERMINATOR    , P_TERM     , ""          , ""        , empty());
        t(UNORDERED_LIST, UNORDERED_LIST, P_UL_LI    , "</li>"     , "<li>"    , trimBullet());
        t(UNORDERED_LIST, ORDERED_LIST  , P_UL_LI    , "</li></ul>", "<ol><li>", trimEnum());
        t(UNORDERED_LIST, UNORDERED_LIST, P_LIST_CONT, ""          , " "       , String::trim);
        t(UNORDERED_LIST, PREFORMATTED  , P_PRE      , "</li></ul>", "<pre>"   , trimPre());
        t(UNORDERED_LIST, TERMINATOR    , P_TERM     , "</li></ul>" , ""       , empty());
        t(UNORDERED_LIST, PARAGRAPH     , P_ANY      , "</li></ul>", "<p>"     , String::trim);
        t(ORDERED_LIST  , UNORDERED_LIST, P_UL_LI    , "</li></ol>", "<ul><li>", trimBullet());
        t(ORDERED_LIST  , ORDERED_LIST  , P_OL_LI    , "</li>"     , "<li>"    , trimEnum());
        t(ORDERED_LIST  , UNORDERED_LIST, P_LIST_CONT, ""          , " "       , String::trim);
        t(ORDERED_LIST  , PREFORMATTED  , P_PRE      , "</li></ol>", "<pre>"   , trimPre());
        t(ORDERED_LIST  , TERMINATOR    , P_TERM     , "</li></ol>", ""        , empty());
        t(ORDERED_LIST  , PARAGRAPH     , P_ANY      , "</li></ol>", "<p>"     , String::trim);
        t(PREFORMATTED  , PREFORMATTED  , P_PRE      , ""          , "\n"      , trimPre());
        t(PREFORMATTED  , UNORDERED_LIST, P_UL_LI    , "</pre>"    , "<ul><li>", trimBullet());
        t(PREFORMATTED  , ORDERED_LIST  , P_OL_LI    , "</pre>"    , "<ol><li>", trimEnum());
        t(PREFORMATTED  , TERMINATOR    , P_TERM     , "</pre>"    , ""        , empty());
        t(PREFORMATTED  , PARAGRAPH     , P_ANY      , "</pre>"    , "<p>"     , String::trim);
        t(TERMINATOR    , TERMINATOR    , P_TERM     , ""          , ""        , empty());
        t(TERMINATOR    , UNORDERED_LIST, P_UL_LI    , ""          , "<ul><li>", trimBullet());
        t(TERMINATOR    , ORDERED_LIST  , P_OL_LI    , ""          , "<ol><li>", trimEnum());
        t(TERMINATOR    , PREFORMATTED  , P_PRE      , ""          , "<pre>"   , trimPre());
        t(TERMINATOR    , PARAGRAPH     , P_ANY      , ""          , "<p>"     , String::trim);
        t(PARAGRAPH     , UNORDERED_LIST, P_UL_LI    , "</p>"      , "<ul><li>", trimBullet());
        t(PARAGRAPH     , ORDERED_LIST  , P_OL_LI    , "</p>"      , "<ol><li>", trimEnum());
        t(PARAGRAPH     , PREFORMATTED  , P_PRE      , "</p>"      , "<pre>"   , String::trim);
        t(PARAGRAPH     , TERMINATOR    , P_TERM     , "</p>"      , ""        , empty());
        t(PARAGRAPH     , PARAGRAPH     , P_ANY      , ""          , " "     , String::trim);
        // @formatter:on
    }

    private void t(final MarkdownLineState from, final MarkdownLineState to, final String pattern,
            final String postfix, final String prefix, final Function<String, String> conversion)
    {
        this.transitions
                .add(new MarkdownLineTransition(from, to, pattern, prefix, postfix, conversion));
    }

    public String run(final String input)
    {
        final StringBuilder builder = new StringBuilder();
        MarkdownLineState state = START;
        for (final String line : input.split("(?:\n\r?|\r)", INCLUDE_EMPTY_STRINGS))
        {
            for (final MarkdownLineTransition transition : this.transitions)
            {
                if (transition.getFrom() == state
                        && transition.getPattern().matcher(line).matches())
                {
                    builder.append(transition.getPostfix());
                    builder.append(transition.getPrefix());
                    builder.append(MarkdownSpanConverter
                            .convertLineContent(transition.getConversion().apply(line)));
                    state = transition.getTo();
                    break;
                }
            }
        }
        closeLastLineState(builder, state);
        return builder.toString();
    }

    protected void closeLastLineState(final StringBuilder builder, final MarkdownLineState state)
    {
        switch (state)
        {
        case PREFORMATTED:
            builder.append("</pre>");
            break;
        case UNORDERED_LIST:
            builder.append("</li></ul>");
            break;
        case ORDERED_LIST:
            builder.append("</li></ol>");
            break;
        case PARAGRAPH:
            builder.append("</p>");
            break;
        case TERMINATOR:
            break;
        default:
            throw new IllegalStateException(
                    "MarkdownLineStateMachine terminates with unknown end state");
        }
    }

    protected Function<String, String> empty()
    {
        return s -> "";
    }

    protected Function<String, String> trimEnum()
    {
        return s -> s.substring(s.indexOf('.') + 1).trim();
    }

    protected Function<String, String> trimPre()
    {
        return s -> s.substring(4);
    }

    protected Function<String, String> trimBullet()
    {
        return s -> s.replaceFirst("^ {0,3}[-+*]", "").trim();
    }
}