package org.itsallcode.openfasttrace.importer.lightweightmarkup;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

import org.itsallcode.openfasttrace.importer.lightweightmarkup.statemachine.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransitionTest
{
    @Test
    void testToString(@Mock final TransitionAction actionMock, @Mock final LinePattern patternMock)
    {
        when(patternMock.toString()).thenReturn("DUMMY_PATTERN");
        final Transition transition = new Transition(LineParserState.COMMENT, LineParserState.TITLE, patternMock,
                actionMock);
        assertThat(transition.toString(),
                equalTo("Transition [from=COMMENT, to=TITLE, markdownPattern=DUMMY_PATTERN]"));
    }
}
