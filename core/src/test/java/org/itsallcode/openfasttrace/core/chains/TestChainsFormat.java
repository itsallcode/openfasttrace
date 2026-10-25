package org.itsallcode.openfasttrace.core.chains;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

// [utest->dsn~cli.chains.output-format~1]
class TestChainsFormat
{
    @ParameterizedTest
    @ValueSource(strings = {"chains", "CHAINS", "csv", "CSV", "mermaid", "Mermaid"})
    void testIsSupportedReturnsTrueForValidFormats(final String format)
    {
        assertThat(ChainsFormat.isSupported(format), equalTo(true));
    }

    @ParameterizedTest
    @ValueSource(strings = {"unknown", "json", "tree", ""})
    void testIsSupportedReturnsFalseForInvalidFormats(final String format)
    {
        assertThat(ChainsFormat.isSupported(format), equalTo(false));
    }

    @Test
    void testIsSupportedReturnsFalseForNull()
    {
        assertThat(ChainsFormat.isSupported(null), equalTo(false));
    }

    @Test
    void testFromStringParsesSuccessfully()
    {
        assertThat(ChainsFormat.fromString("chains"), equalTo(ChainsFormat.CHAINS));
        assertThat(ChainsFormat.fromString("CSV"), equalTo(ChainsFormat.CSV));
        assertThat(ChainsFormat.fromString("Mermaid"), equalTo(ChainsFormat.MERMAID));
    }

    @Test
    void testFromStringThrowsOnNull()
    {
        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ChainsFormat.fromString(null));
        assertThat(ex.getMessage(), equalTo("Chains format cannot be null"));
    }

    @Test
    void testFromStringThrowsOnUnsupportedFormat()
    {
        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ChainsFormat.fromString("xml"));
        assertThat(ex.getMessage(), equalTo("Unsupported chains format: 'xml'"));
    }

    @Test
    void testGetName()
    {
        assertThat(ChainsFormat.CHAINS.getName(), equalTo("chains"));
        assertThat(ChainsFormat.CSV.getName(), equalTo("csv"));
        assertThat(ChainsFormat.MERMAID.getName(), equalTo("mermaid"));
    }
}
