package org.itsallcode.openfasttrace.testutil.matcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;

class MatcherTestBaseTest
{
    @Test
    void testConstructor()
    {
        assertThat(new DummyMatcher(), notNullValue());
    }

    private static class DummyMatcher extends MatcherTestBase<String>
    {
        @Override
        protected Matcher<String> createMatcher(final String object)
        {
            throw new UnsupportedOperationException("Unimplemented method 'createMatcher'");
        }
    }
}
