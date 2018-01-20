package openfasttrack.core;

import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

public class ChecksumCalculator
{
    private ChecksumCalculator()
    {

    }

    public static long calculateCrc32(final String value)
    {
        final CRC32 checksum = new CRC32();
        checksum.update(value.getBytes(StandardCharsets.UTF_8));
        return checksum.getValue();
    }
}
