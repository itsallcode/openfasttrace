package org.itsallcode.openfasttrace.testutil.log;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class NoOpLoggingHandlerTest
{
    @Test
    void testConstructor()
    {
        assertThat(new NoOpLoggingHandler(), notNullValue());
    }

    @Test
    void testPublishDoesNotFail()
    {
        assertDoesNotThrow(() -> testee().publish(null));
    }

    @Test
    void testFlushDoesNotFail()
    {
        assertDoesNotThrow(testee()::flush);
    }

    @Test
    void testCloseDoesNotFail()
    {
        assertDoesNotThrow(testee()::close);
    }

    private NoOpLoggingHandler testee()
    {
        return new NoOpLoggingHandler();
    }
}
