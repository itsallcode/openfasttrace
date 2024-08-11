package org.itsallcode.openfasttrace.importer.tag;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.itsallcode.matcher.auto.AutoMatcher;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItem.Builder;
import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.api.importer.ImporterContext;
import org.itsallcode.openfasttrace.api.importer.SpecificationListBuilder;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.testutil.importer.input.StreamInput;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

// [utest->dsn~import.full-coverage-tag~1]
class TestTagImporter
{
    private static final String FILENAME = "testfilename.java";

    private static final String UNIX_NEWLINE = "\n";
    private static final String CARRIAGE_RETURN = "\r";
    private static final String WINDOWS_NEWLINE = CARRIAGE_RETURN + UNIX_NEWLINE;

    static Stream<Arguments> tagImporterTests()
    {
        return Stream.of(
                noItemDetected(""),
                noItemDetected("non empty string"),
                noItemDetected("non empty string\nmultiline"),
                noItemDetected("impl->dsn~missing-brackets~1"),
                noItemDetected("[missing arrow]"),
                noItemDetected("[impl->invalid covered tag]"),
                noItemDetected("[impl->dsn~missing-revision]"),
                noItemDetected("[impl->dsn~invalid-revision~invalid]"),
                noItemDetected("[impl->dsn~invalid-revision-negative~-1]"),
                noItemDetected("[impl->dsn~invalid-revision~dot-notation~1.0]"),
                noItemDetected("[impl->\ndsn~newline-after-arrow~1]"),
                noItemDetected("[impl->dsn~name1~1>>]"),
                noItemDetected("[impl->dsn~name1~1>>tag with space]"),

                parsedItem("[impl->dsn~name1~1" + "]",
                        itemACoveringB("impl~name1-912633853~0", "dsn~name1~1")),
                parsedItem("[ impl -> dsn~nameA~1 " + "]",
                        itemACoveringB("impl~nameA-1653917613~0", "dsn~nameA~1")),
                parsedItem("[\timpl\t->\tdsn~name1~1\t" + "]",
                        itemACoveringB("impl~name1-912633853~0", "dsn~name1~1")),
                parsedItem("[impl->dsn~name1~1" + "]" + UNIX_NEWLINE,
                        itemACoveringB("impl~name1-912633853~0", "dsn~name1~1")),

                parsedItem("prefix[impl->dsn~name1~1" + "]",
                        itemACoveringB("impl~name1-912633853~0", "dsn~name1~1")),
                parsedItem("prefix with spaces [impl->dsn~name1~1" + "]",
                        itemACoveringB("impl~name1-912633853~0", "dsn~name1~1")),

                parsedItem("[impl->dsn~name1~1" + "]postfix",
                        itemACoveringB("impl~name1-912633853~0", "dsn~name1~1")),
                parsedItem("[impl->dsn~name1~1" + "] postfix with space",
                        itemACoveringB("impl~name1-912633853~0", "dsn~name1~1")),

                parsedItem("prefix[impl->dsn~name1~1" + "]postfix",
                        itemACoveringB("impl~name1-912633853~0", "dsn~name1~1")),
                parsedItem("prefix with space [impl->dsn~name1~1" + "] postfix with space",
                        itemACoveringB("impl~name1-912633853~0", "dsn~name1~1")),

                parsedItems("[implA->dsn~name1~2" + "][implB->dsn~name2~3" + "]",
                        itemACoveringB("implA~name1-2943155783~0", "dsn~name1~2"),
                        itemACoveringB("implB~name2-1099447527~0", "dsn~name2~3")),
                parsedItems("[implA->dsn~name1~2" + "] [implB->dsn~name2~3" + "]",
                        itemACoveringB("implA~name1-2943155783~0", "dsn~name1~2"),
                        itemACoveringB("implB~name2-1099447527~0", "dsn~name2~3")),
                parsedItems("[implA->dsn~name1~2" + "]separator[implB->dsn~name2~3" + "]",
                        itemACoveringB("implA~name1-2943155783~0", "dsn~name1~2"),
                        itemACoveringB("implB~name2-1099447527~0", "dsn~name2~3")),
                parsedItems("[implA->dsn~name1~2" + "] separator [implB->dsn~name2~3" + "]",
                        itemACoveringB("implA~name1-2943155783~0", "dsn~name1~2"),
                        itemACoveringB("implB~name2-1099447527~0", "dsn~name2~3")),

                parsedItems("[implA->dsn~name1~2" + "][implB->dsn~name2~3" + "][implC->dsn~name3~4"
                        + "]",
                        itemACoveringB("implA~name1-2943155783~0", "dsn~name1~2"),
                        itemACoveringB("implB~name2-1099447527~0", "dsn~name2~3"),
                        itemACoveringB("implC~name3-2846888323~0", "dsn~name3~4")),

                parsedItems("[implA->dsn~name1~2" + "]" + UNIX_NEWLINE + "[implB->dsn~name2~3" + "]",
                        itemACoveringB("implA~name1-2943155783~0", "dsn~name1~2"),
                        itemACoveringB("implB~name2-1743199302~0", "dsn~name2~3")
                                .location(FILENAME, 2)),
                parsedItems("[implA->dsn~name1~2" + "]" + WINDOWS_NEWLINE + "[implB->dsn~name2~3" + "]",
                        itemACoveringB("implA~name1-2943155783~0", "dsn~name1~2"),
                        itemACoveringB("implB~name2-1743199302~0", "dsn~name2~3")
                                .location(FILENAME, 2)),
                parsedItems("[implA->dsn~name1~2" + "]" + CARRIAGE_RETURN + "[implB->dsn~name2~3" + "]",
                        itemACoveringB("implA~name1-2943155783~0", "dsn~name1~2"),
                        itemACoveringB("implB~name2-1743199302~0", "dsn~name2~3")
                                .location(FILENAME, 2)),

                parsedItems("[impl->dsn~name~1" + "][impl->dsn~name~1" + "]",
                        itemACoveringB("impl~name-4161631350~0", "dsn~name~1"),
                        itemACoveringB("impl~name-964930486~0", "dsn~name~1")),
                parsedItems("[impl->dsn~name~1" + "]" + UNIX_NEWLINE + "[impl->dsn~name~1" + "]",
                        itemACoveringB("impl~name-4161631350~0", "dsn~name~1"),
                        itemACoveringB("impl~name-2408818310~0", "dsn~name~1")
                                .location(FILENAME, 2)),

                // [utest->dsn~import.full-coverage-tag-with-needed-coverage~1]
                // [utest->dsn~import.full-coverage-tag-with-needed-coverage-readable-names~1]
                parsedItem("[dsn->feat~name1~1>>impl" + "]",
                        itemACoveringB("dsn~name1~0", "feat~name1~1")
                                .addNeedsArtifactType("impl")),

                // [utest->dsn~import.full-coverage-tag-with-needed-coverage~1]
                // [utest->dsn~import.full-coverage-tag-with-needed-coverage-readable-names~1]
                parsedItem("[dsn->feat~name1~1>>impl,test" + "]",
                        itemACoveringB("dsn~name1~0", "feat~name1~1")
                                .addNeedsArtifactType("impl")
                                .addNeedsArtifactType("test")),
                parsedItem("[ dsn -> feat~name1~1 >> impl , test " + "]",
                        itemACoveringB("dsn~name1~0", "feat~name1~1")
                                .addNeedsArtifactType("impl")
                                .addNeedsArtifactType("test")),

                // [utest->dsn~import.full-coverage-tag-with-revision~1]
                parsedItem("[impl~~42->req~name~17" + "]",
                        itemACoveringB("impl~name-3433816440~42", "req~name~17")),
                parsedItems("[impl~~42->req~name~17" + "][impl~~42->req~name~17" + "]",
                        itemACoveringB("impl~name-3433816440~42", "req~name~17"),
                        itemACoveringB("impl~name-1460579607~42", "req~name~17")),

                noItemDetected("[impl~~-42->req~name~17]"),
                // [utest->dsn~import.full-coverage-tag-with-revision~1]
                parsedItem("[impl~~42->req~example_name~17>>test" + "]",
                        itemACoveringB("impl~example_name~42", "req~example_name~17")
                                .addNeedsArtifactType("test")),

                // [utest->dsn~import.full-coverage-tag-with-name-and-revision~1]
                parsedItem("[impl->dsn~name~2" + "]",
                        itemACoveringB("impl~name-1627661772~0", "dsn~name~2")),
                parsedItem("[impl~name1~1->dsn~name2~2" + "]",
                        itemACoveringB("impl~name1~1", "dsn~name2~2")),
                parsedItem("[impl~name1~1->dsn~name2~2>>test" + "]",
                        itemACoveringB("impl~name1~1", "dsn~name2~2")
                                .addNeedsArtifactType("test")),
                parsedItem("[impl~name1~1->dsn~name2~2>>test,other" + "]",
                        itemACoveringB("impl~name1~1", "dsn~name2~2")
                                .addNeedsArtifactType("test")
                                .addNeedsArtifactType("other")),
                parsedItem(" [ test~with.spaces~1 -> dsn~spaces-supported~2 >> test , other " + "]",
                        itemACoveringB("test~with.spaces~1", "dsn~spaces-supported~2")
                                .addNeedsArtifactType("test")
                                .addNeedsArtifactType("other")),
                noItemDetected("[impl~missing-revision->dsn~name2~2]"),
                noItemDetected("[impl~missing-revision~->dsn~name2~2]"),
                noItemDetected("[impl~illegal?char~1->dsn~name2~2]"),
                noItemDetected("[impl~negative-revision~-1->dsn~name2~2]"),
                noItemDetected("[impl~missing-forward~1->dsn~name2~2>>]"),
                noItemDetected("[impl~trailing-comma~1->dsn~name2~2>>test,]"),
                noItemDetected("[impl~duplicate-comma~1->dsn~name2~2>>test,,other]"),
                noItemDetected("[impl~name1~1->dsn~name2~2>>test,other,]"));
    }

    private static SpecificationItem.Builder itemACoveringB(final String id, final String coveredId)
    {
        return itemBuilder().id(SpecificationItemId.parseId(id))
                .addCoveredId(SpecificationItemId.parseId(coveredId));
    }

    private static SpecificationItem.Builder itemBuilder()
    {
        return SpecificationItem.builder();
    }

    @ParameterizedTest(name = "Text ''{0}'' converted to spec items {1}")
    @MethodSource("tagImporterTests")
    void testTagImporter(final String content, final List<SpecificationItem> expectedItems)
    {
        final List<SpecificationItem> result = runImporter(content);
        assertThat(result, hasSize(expectedItems.size()));
        if (!expectedItems.isEmpty())
        {
            assertThat(result, AutoMatcher.contains(expectedItems.toArray(new SpecificationItem[0])));
        }
    }

    private List<SpecificationItem> runImporter(final String content)
    {
        final SpecificationListBuilder builder = SpecificationListBuilder.create();
        final InputFile file = StreamInput.forReader(Paths.get(FILENAME),
                new BufferedReader(new StringReader(content)));
        final TagImporterFactory factory = new TagImporterFactory();
        factory.init(new ImporterContext(null));
        factory.createImporter(file, builder).runImport();
        return builder.build();
    }

    /**
     * Create a test case using the content as input for the
     * {@link TagImporter}. Make sure to concatenate the content to avoid
     * breaking self-tracing.
     *
     * @param content
     *            content to parse
     * @param itemBuilder
     *            builder for the expected item
     * @return the test case arguments
     */
    private static Arguments parsedItem(final String content, final SpecificationItem.Builder itemBuilder)
    {
        if (itemBuilder.build().getLocation() == null)
        {
            itemBuilder.location(FILENAME, 1);
        }
        return Arguments.of(content, List.of(itemBuilder.build()));
    }

    private static Arguments parsedItems(final String content, final SpecificationItem.Builder... itemBuilders)
    {
        final List<SpecificationItem> expectedItems = Arrays.stream(itemBuilders)
                .map(TestTagImporter::setLocation)
                .map(SpecificationItem.Builder::build)
                .toList();
        return Arguments.of(content, expectedItems);
    }

    private static Builder setLocation(final Builder builder)
    {
        if (builder.build().getLocation() == null)
        {
            builder.location(FILENAME, 1);
        }
        return builder;
    }

    private static Arguments noItemDetected(final String content)
    {
        return Arguments.of(content, emptyList());
    }
}
