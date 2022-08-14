package org.itsallcode.openfasttrace.core;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.itsallcode.openfasttrace.api.core.Newline;
import org.junit.jupiter.api.Test;

class TestNewline
{
    @Test
    void testUnix()
    {
        assertThat(Newline.fromRepresentation("\n"), equalTo(Newline.UNIX));
    }

    @Test
    void testWindows()
    {
        assertThat(Newline.fromRepresentation("\r\n"), equalTo(Newline.WINDOWS));
    }

    @Test
    void testOldMac()
    {
        assertThat(Newline.fromRepresentation("\r"), equalTo(Newline.OLDMAC));
    }

    @Test
    void testUnknownThrowsException()
    {
        assertThrows(IllegalArgumentException.class, () -> Newline.fromRepresentation("unknown"));
    }
}