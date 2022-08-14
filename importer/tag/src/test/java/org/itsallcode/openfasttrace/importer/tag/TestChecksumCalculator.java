package org.itsallcode.openfasttrace.importer.tag;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

class TestChecksumCalculator
{
    @Test
    void testCalculateCrc32OfEmptyString()
    {
        assertThat(ChecksumCalculator.calculateCrc32(""), equalTo(0L));
    }

    @Test
    void testCalculateCrc32OfSimpleString()
    {
        assertThat(ChecksumCalculator.calculateCrc32("abcd"), equalTo(3984772369L));
    }

    @Test
    void testCalculateCrc32OfUtf8String()
    {
        assertThat(ChecksumCalculator.calculateCrc32("äöüÖÄÜß"), equalTo(2866547662L));
    }
}
