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
        step("line");
        assertTransition(LineParserState.START, "");
        verifyNoMoreInteractions(actionMock);
    }

    private void setupTransitions(final Transition... transitions)
    {
        this.stateMachine = new LineParserStateMachine(transitions);
    }

    private void assertTransition(final LineParserState expectedState, final String expectedToken)
    {
        assertAll(() -> assertThat("next state", this.stateMachine.getState(), equalTo(expectedState)),
                () -> assertThat("token", this.stateMachine.getLastToken(), equalTo(expectedToken)));
    }

    private void step(final String inputLine)
    {
        this.step(inputLine, null);
    }

    private void step(final String inputLine, final String nextInputLine)
    {
        this.stateMachine.step(inputLine, nextInputLine);
    }

    @Test
    void testMatchedSingleTransitionWithMock(@Mock final LinePattern patternMock)
    {
        when(patternMock.getMatches("line1", "line2")).thenReturn(Optional.of(List.of("result", "ignored")));
        setupTransitions(transition(LineParserState.START, LineParserState.COMMENT, patternMock));
        step("line1", "line2");
        assertTransition(LineParserState.COMMENT, "result");
        verify(actionMock).transit();
        verifyNoMoreInteractions(actionMock);
    }

    @Test
    void testNotMatchedTransitionWithMock(@Mock final LinePattern patternMock)
    {
        when(patternMock.getMatches("line1", "line2")).thenReturn(Optional.empty());
        setupTransitions(transition(LineParserState.START, LineParserState.COMMENT, patternMock));
        step("line1", "line2");
        assertTransition(LineParserState.START, "");
        verifyNoMoreInteractions(actionMock);
    }

    @Test
    void testMatchedTransitionInNextLine()
    {
        setupTransitions(transition(LineParserState.START, LineParserState.COMMENT, pattern("(line)")));
        step("line");
        assertTransition(LineParserState.COMMENT, "line");
        verify(actionMock).transit();
        verifyNoMoreInteractions(actionMock);
    }

    @Test
    void testMatchedTransitionNoToken()
    {
        setupTransitions(transition(LineParserState.START, LineParserState.COMMENT, pattern("line")));
        step("line");
        assertTransition(LineParserState.COMMENT, "");
        verify(actionMock).transit();
        verifyNoMoreInteractions(actionMock);
    }

    @Test
    void testWrongState()
    {
        setupTransitions(transition(LineParserState.COMMENT, LineParserState.COMMENT, pattern("line")));
        step("line");
        assertTransition(LineParserState.START, "");
        verifyNoMoreInteractions(actionMock);
    }

    @Test
    void testNoMatch()
    {
        setupTransitions(transition(LineParserState.START, LineParserState.COMMENT, pattern("notmatching")));
        step("line");
        assertTransition(LineParserState.START, "");
        verifyNoMoreInteractions(actionMock);
    }

    private LinePattern pattern(final String pattern)
    {
        return SimpleLinePattern.of(pattern);
    }

    private Transition transition(final LineParserState from, final LineParserState to, final LinePattern pattern)
    {
        return new Transition(from, to, pattern, actionMock);
    }
}
