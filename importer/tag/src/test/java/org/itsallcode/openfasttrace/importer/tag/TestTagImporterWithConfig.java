package org.itsallcode.openfasttrace.importer.tag;

import static org.junit.jupiter.api.Assertions.assertThrows;import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.ImporterException;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.api.importer.tag.config.PathConfig;
import org.itsallcode.openfasttrace.testutil.importer.input.StreamInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

// [utest->dsn~import.short-coverage-tag~1]
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TestTagImporterWithConfig
{
    private static final String COVERED_ITEM_NAME1 = "covered_name1";
    private static final String COVERED_ITEM_NAME2 = "covered_name2";
    private static final Path FILE = Paths.get("dummy");
    private static final String COVERED_ITEM_TYPE = "covered_type";
    private static final String COVERING_ITEM_TYPE = "covering_type";
    private static final String COVERED_ITEM_NAME_PREFIX = "prefix.";
    private static final String INVALID_REVISION = "invalidRevision";

    @Mock
    private PathConfig configMock;
    @Mock
    private ImportEventListener listenerMock;
    private InOrder inOrderListener;

    @BeforeEach
    void beforeEach()
    {
        this.inOrderListener = inOrder(this.listenerMock);
        when(this.configMock.getCoveredItemArtifactType()).thenReturn(COVERED_ITEM_TYPE);
        when(this.configMock.getCoveredItemNamePrefix()).thenReturn(null);
        when(this.configMock.getTagArtifactType()).thenReturn(COVERING_ITEM_TYPE);
        when(this.configMock.getCoveredItemNamePrefix()).thenReturn(null);
    }

    @Test
    void testEmptyFile()
    {
        runImport("");
        verifyNoInteractions(this.listenerMock);
    }

    @Test
    void testFileWithoutMatchingTag()
    {
        runImport("non matching\nfile\n");
        verifyNoInteractions(this.listenerMock);
    }

    @Test
    void testFileWithNewTagFormatAlsoSupported()
    {
        final String itemName = "coveredtype~coveredname~1"; // do not inline to
                                                             // avoid error in
                                                             // self-trace
        runImport("[type->" + itemName + "]");
        verify(this.listenerMock)
                .setId(SpecificationItemId.createId("type", "coveredname" + "-3264583751", 0));
    }

    @Test
    void testFileWithLegacyTagFormat()
    {
        runImport("[[" + COVERED_ITEM_NAME1 + ":1]]");
        verifyTag(1, SpecificationItemId.createId(COVERED_ITEM_TYPE, COVERED_ITEM_NAME1, 1),
                SpecificationItemId.createId(COVERING_ITEM_TYPE,
                        COVERED_ITEM_NAME1 + "-135790575"));
        this.inOrderListener.verifyNoMoreInteractions();
    }

    @Test
    void testFileWithLegacyTagFormatTwoTagsInTwoLines()
    {
        runImport("[[" + COVERED_ITEM_NAME1 + ":1]]\n" //
                + "[[" + COVERED_ITEM_NAME2 + ":2]]");
        verifyTag(1, SpecificationItemId.createId(COVERED_ITEM_TYPE, COVERED_ITEM_NAME1, 1),
                SpecificationItemId.createId(COVERING_ITEM_TYPE,
                        COVERED_ITEM_NAME1 + "-135790575"));
        verifyTag(2, SpecificationItemId.createId(COVERED_ITEM_TYPE, COVERED_ITEM_NAME2, 2),
                SpecificationItemId.createId(COVERING_ITEM_TYPE,
                        COVERED_ITEM_NAME2 + "-3623433492"));
        this.inOrderListener.verifyNoMoreInteractions();
    }

    @Test
    void testFileWithLegacyTagFormatTwoTagsInSameLine()
    {
        runImport("[[" + COVERED_ITEM_NAME1 + ":1]]" //
                + "[[" + COVERED_ITEM_NAME2 + ":2]]");
        verifyTag(1, SpecificationItemId.createId(COVERED_ITEM_TYPE, COVERED_ITEM_NAME1, 1),
                SpecificationItemId.createId(COVERING_ITEM_TYPE,
                        COVERED_ITEM_NAME1 + "-135790575"));
        verifyTag(1, SpecificationItemId.createId(COVERED_ITEM_TYPE, COVERED_ITEM_NAME2, 2),
                SpecificationItemId.createId(COVERING_ITEM_TYPE,
                        COVERED_ITEM_NAME2 + "-4032809256"));
        this.inOrderListener.verifyNoMoreInteractions();
    }

    @Test
    void testNonIntegerRevisionRejected()
    {
        final String expectedMessage = "Error processing line dummy:1 ([[" + COVERED_ITEM_NAME1
                + ":" + INVALID_REVISION + "]]): Error parsing revision '" + INVALID_REVISION
                + "' for item '" + COVERED_ITEM_NAME1 + "'";
        assertThrows(ImporterException.class,
                () -> runImport("[[" + COVERED_ITEM_NAME1 + ":" + INVALID_REVISION + "]]"),
                expectedMessage);
    }

    @Test
    void testFileWithLegacyTagFormatWithPrefix()
    {
        when(this.configMock.getCoveredItemNamePrefix()).thenReturn(COVERED_ITEM_NAME_PREFIX);
        runImport("[[" + COVERED_ITEM_NAME1 + ":1]]");
        verifyTag(1,
                SpecificationItemId.createId(COVERED_ITEM_TYPE,
                        COVERED_ITEM_NAME_PREFIX + COVERED_ITEM_NAME1, 1),
                SpecificationItemId.createId(COVERING_ITEM_TYPE,
                        COVERED_ITEM_NAME_PREFIX + COVERED_ITEM_NAME1 + "-2831791434"));
        this.inOrderListener.verifyNoMoreInteractions();
    }

    private void verifyTag(final int lineNumber, final SpecificationItemId coveredId,
            final SpecificationItemId tagItemId)
    {
        this.inOrderListener.verify(this.listenerMock).beginSpecificationItem();
        this.inOrderListener.verify(this.listenerMock).setLocation(FILE.toString(), lineNumber);
        this.inOrderListener.verify(this.listenerMock).setId(tagItemId);
        this.inOrderListener.verify(this.listenerMock).addCoveredId(coveredId);
        this.inOrderListener.verify(this.listenerMock).endSpecificationItem();
    }

    private void runImport(final String content)
    {
        final InputFile file = StreamInput.forReader(FILE,
                new BufferedReader(new StringReader(content)));

        TagImporter.create(Optional.of(this.configMock), file, this.listenerMock).runImport();
    }
}
