package org.itsallcode.openfasttrace.importer.lightweightmarkup;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
class TransitionTest
{
    @Test
    void testToString(@Mock final TransitionAction actionMock, @Mock final LinePattern patternMock)
    {
        Mockito.when(patternMock.toString()).thenReturn("DUMMY_PATTERN");
        final Transition transition = new Transition(LineParserState.OUTSIDE, LineParserState.TITLE, patternMock,
                actionMock);
        assertThat(transition.toString(),
                equalTo("Transition [from=OUTSIDE, to=TITLE, markdownPattern=DUMMY_PATTERN]"));
    }
}