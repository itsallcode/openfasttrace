package org.itsallcode.openfasttrace.importer.lightweightmarkup.statemachine;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LineParserStateMachineTest
{
    LineParserStateMachine stateMachine;
    @Mock
    TransitionAction actionMock;

    @Test
    void testNoTransitions()
    {
        setupTransitions();
        assertTransition("line", LineParserState.START, "");
        verifyNoMoreInteractions(actionMock);
    }

    @Test
    void testMatchedTransitionWithMock(@Mock final LinePattern patternMock)
    {
        when(patternMock.getMatches("line1", "line2")).thenReturn(Optional.of(List.of("result", "ignored")));
        setupTransitions(transition(LineParserState.START, LineParserState.COMMENT, patternMock));
        assertTransition("line1", "line2", LineParserState.COMMENT, "result");
        verify(actionMock).transit();
        verifyNoMoreInteractions(actionMock);
    }

    @Test
    void testNotMatchedTransitionWithMock(@Mock final LinePattern patternMock)
    {
        when(patternMock.getMatches("line1", "line2")).thenReturn(Optional.empty());
        setupTransitions(transition(LineParserState.START, LineParserState.COMMENT, patternMock));
        assertTransition("line1", "line2", LineParserState.START, "");
        verifyNoMoreInteractions(actionMock);
    }

    @Test
    void testMatchedTransitionInNextLine()
    {
        setupTransitions(transition(LineParserState.START, LineParserState.COMMENT, pattern("(line)")));
        assertTransition("line", LineParserState.COMMENT, "line");
        verify(actionMock).transit();
        verifyNoMoreInteractions(actionMock);
    }

    @Test
    void testMatchedTransitionNoToken()
    {
        setupTransitions(transition(LineParserState.START, LineParserState.COMMENT, pattern("line")));
        assertTransition("line", LineParserState.COMMENT, "");
        verify(actionMock).transit();
        verifyNoMoreInteractions(actionMock);
    }

    @Test
    void testWrongState()
    {
        setupTransitions(transition(LineParserState.COMMENT, LineParserState.COMMENT, pattern("line")));
        assertTransition("line", LineParserState.START, "");
        verifyNoMoreInteractions(actionMock);
    }

    @Test
    void testNoMatch()
    {
        setupTransitions(transition(LineParserState.START, LineParserState.COMMENT, pattern("notmatching")));
        assertTransition("line", LineParserState.START, "");
        verifyNoMoreInteractions(actionMock);
    }

    private LinePattern pattern(final String pattern)
    {
        return SimpleLinePattern.of(pattern);
    }

    private void setupTransitions(final Transition... transitions)
    {
        this.stateMachine = new LineParserStateMachine(transitions);
    }

    private Transition transition(final LineParserState from, final LineParserState to, final LinePattern pattern)
    {
        return new Transition(from, to, pattern, actionMock);
    }

    private void assertTransition(final String inputLine, final LineParserState expectedState,
            final String expectedToken)
    {
        assertTransition(inputLine, null, expectedState, expectedToken);
    }

    private void assertTransition(final String inputLine, final String nextInputLine,
            final LineParserState expectedState, final String expectedToken)
    {
        this.stateMachine.step(inputLine, nextInputLine);
        assertAll(() -> assertThat("next state", this.stateMachine.getState(), equalTo(expectedState)),
                () -> assertThat("token", this.stateMachine.getLastToken(), equalTo(expectedToken)));
    }
}
