package org.itsallcode.openfasttrace.importer.markdown;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
class TransitionTest
{
    @Test
    void testToString(@Mock final TransitionAction actionMock)
    {
        final Transition transition = new Transition(State.OUTSIDE, State.TITLE, MdPattern.TITLE, actionMock);
        assertThat(transition.toString(), equalTo("Transition [from=OUTSIDE, to=TITLE, markdownPattern=TITLE]"));
    }
}