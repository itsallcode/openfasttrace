package org.itsallcode.openfasttrace.importer.tag;

import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

final class ChecksumCalculator
{
    private ChecksumCalculator()
    {
        // Prevent instantiation.
    }

    static long calculateCrc32(final String value)
    {
        final CRC32 checksum = new CRC32();
        checksum.update(value.getBytes(StandardCharsets.UTF_8));
        return checksum.getValue();
    }
}
