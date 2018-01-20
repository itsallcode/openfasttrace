package openfasttrack.core;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TestChecksumCalculator
{
    @Test
    public void testCalculateCrc32OfEmptyString()
    {
        assertThat(ChecksumCalculator.calculateCrc32(""), equalTo(0L));
    }

    @Test
    public void testCalculateCrc32OfSimpleString()
    {
        assertThat(ChecksumCalculator.calculateCrc32("abcd"), equalTo(3984772369L));
    }

    @Test
    public void testCalculateCrc32OfUtf8String()
    {
        assertThat(ChecksumCalculator.calculateCrc32("äöüÖÄÜß"), equalTo(2866547662L));
    }
}
