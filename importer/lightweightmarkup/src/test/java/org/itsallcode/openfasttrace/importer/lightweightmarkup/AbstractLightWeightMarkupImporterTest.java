package org.itsallcode.openfasttrace.importer.lightweightmarkup;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.itsallcode.matcher.auto.AutoMatcher.contains;
import static org.itsallcode.openfasttrace.testutil.core.ItemBuilderFactory.item;
import static org.itsallcode.openfasttrace.testutil.importer.ImportAssertions.assertImportWithFactory;
import static org.itsallcode.openfasttrace.testutil.importer.ImportAssertions.runImporterOnText;

import java.util.List;
import java.util.stream.Stream;

import org.hamcrest.Matcher;
import org.itsallcode.openfasttrace.api.core.ItemStatus;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.api.importer.ImporterFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

public abstract class AbstractLightWeightMarkupImporterTest
{
    private static final String NL = System.lineSeparator();

    public AbstractLightWeightMarkupImporterTest()
    {
        // Intentionally empty to satisfy compile checks.
    }

    // [utest -> dsn~md.specification-item-id-format~3]
    @CsvSource({
            "at~name~67, at, name, 67",
            "longartifacttype~name.with.dots~123456789, longartifacttype, name.with.dots, 123456789",
            "a~b1.c1.d~0, a, b1.c1.d, 0"
    })
    @ParameterizedTest
    void testRequirementIdDetected(final String markdownId, final String expectedArtifactType,
            final String expectedName, final int expectedRevision)
    {
        assertImport("/doc/spec.md/", markdownId, contains(item()
                .id(expectedArtifactType, expectedName, expectedRevision)
                .location("/doc/spec.md", 1)
                .build()));
    }

    private void assertImport(final String filename, final String input,
            final Matcher<Iterable<? extends SpecificationItem>> matcher)
    {
        assertImportWithFactory(filename, input, matcher, getImporterFactory());
    }

    protected abstract ImporterFactory getImporterFactory();

    // [utest -> dsn~md.requirement-references~1]
    @Test
    void testSpecificationItemReferenceDetected()
    {
        assertImport("/a/b/c.markdown", """
                `req~item-a~2`

                Covers:
                * `feat~item-b~1`
                """,
                contains(item()
                        .id("req", "item-a", 2)
                        .addCoveredId("feat", "item-b", 1)
                        .location("/a/b/c.markdown", 1)
                        .build()));
    }

    // [utest -> dsn.covers-list~1]
    @Test
    void testSpecificationItemCoversList()
    {
        assertImport("/a/b/c.markdown", """
                req~covers-list~4
                Covers:
                * `feat~item-a~1`
                * `feat~item-b~2`
                * `feat~item-c~3`
                """,
                contains(item()
                        .id("req", "covers-list", 4)
                        .addCoveredId("feat", "item-a", 1)
                        .addCoveredId("feat", "item-b", 2)
                        .addCoveredId("feat", "item-c", 3)
                        .location("/a/b/c.markdown", 1)
                        .build()));
    }

    // [utest -> dsn~md.depends-list~1]
    @Test
    void testSpecificationItemDependsList()
    {
        assertImport("/a/b/c.markdown", """
                req~depends-list~4
                Depends:
                * `feat~item-a~1`
                * `feat~item-b~2`
                * `feat~item-c~3`
                """,
                contains(item()
                        .id("req", "depends-list", 4)
                        .addDependOnId("feat", "item-a", 1)
                        .addDependOnId("feat", "item-b", 2)
                        .addDependOnId("feat", "item-c", 3)
                        .location("/a/b/c.markdown", 1)
                        .build()));
    }

    // [utest -> dsn~md.needs-coverage-list-single-line~2]
    @Test
    void testSpecificationItemNeedsCoverageListCompact()
    {
        assertImport("~/git/foo/bar.md", """
                req~needs-coverage-list-single-line~4
                Needs: dsn, uman
                """,
                contains(item()
                        .id("req", "needs-coverage-list-single-line", 4)
                        .addNeedsArtifactType("dsn")
                        .addNeedsArtifactType("uman")
                        .location("~/git/foo/bar.md", 1)
                        .build()));
    }

    static Stream<Arguments> tags()
    {
        return Stream.of(
                Arguments.of("Tags: req , dsn ", List.of("req", "dsn")),
                Arguments.of("Tags: req ", List.of("req")),
                Arguments.of("Tags: req,dsn ", List.of("req", "dsn")),
                Arguments.of("Tags: req ,dsn", List.of("req", "dsn")),
                Arguments.of("Tags: req,dsn", List.of("req", "dsn")),
                Arguments.of("Tags: req,\tdsn\n", List.of("req", "dsn")),
                Arguments.of("Tags:req,dsn", List.of("req", "dsn")),
                Arguments.of("Tags:\n* req\n* dsn", List.of("req", "dsn")),
                Arguments.of("Tags:\n * req\n * dsn\n", List.of("req", "dsn")),
                Arguments.of("Tags:\n* req \n\t* dsn ", List.of("req", "dsn")),
                Arguments.of("Tags:\n* req\n* dsn", List.of("req", "dsn")));
    }

    @ParameterizedTest
    @MethodSource("tags")
    void testTags(final String mdContent, final List<String> expected)
    {
        final List<SpecificationItem> items = runImporterOnText("irrelevant-filename", "`a~b~1`\n" + mdContent,
                getImporterFactory());
        assertThat(items.get(0).getTags(), equalTo(expected));
    }

    // [utest -> dsn~md.needs-coverage-list~1]
    @Test
    void testSpecificationItemNeedsCoverageList()
    {
        assertImport("needs-list.md", """
                req~needs-coverage-list~4
                Needs:
                * dsn
                * uman
                """,
                contains(item()
                        .id("req", "needs-coverage-list", 4)
                        .addNeedsArtifactType("dsn")
                        .addNeedsArtifactType("uman")
                        .location("needs-list.md", 1)
                        .build()));
    }

    // [utest -> dsn~md.artifact-forwarding-notation~1]
    @CsvSource({
            "dsn-->impl:req~foobar~1, dsn, impl, req, foobar, 1",
            " rsn-->sdk:req~foobar~2, rsn, sdk, req, foobar, 2",
            "dsn-->impl : test~foobar~3 , dsn, impl, test, foobar, 3",
            " rsn-->sdk : test~foobar~4 , rsn, sdk, test, foobar, 4",
            "dsn-->impl\t: req~foobar~1, dsn, impl, req, foobar, 1",
            "rsn-->sdk\t: \treq~foobar~2, rsn, sdk, req, foobar, 2",
            "dsn -->impl : test~foobar~3\t , dsn, impl, test, foobar, 3",
            "rsn -->sdk : test~foobar~4\t\t , rsn, sdk, test, foobar, 4"
    })
    @ParameterizedTest
    void testArtifactForwardingNotation(final String input, final String forwardedArtifactType,
            final String targetArtifactType, final String originalArtifactType,
            final String orginalName, final int originalRevision)
    {
        assertImport("xyz", input,
                contains(item()
                        .id(forwardedArtifactType, orginalName, originalRevision)
                        .addCoveredId(originalArtifactType, orginalName, originalRevision)
                        .addNeedsArtifactType(targetArtifactType)
                        .forwards(true)
                        .location("xyz", 1)
                        .build()));
    }

    // [utest -> dsn~md.artifact-forwarding-notation~1]
    @Test
    void testForwardingAfterDepends()
    {
        assertImport("1.2.md", """
                dsn~foo~1
                Depends:
                * req~foo~1
                dsn-->impl:req~bar~2
                """,
                contains(
                        item()
                                .id("dsn", "foo", 1)
                                .location("1.2.md", 1)
                                .addDependOnId("req", "foo", 1)
                                .build(),
                        item()
                                .id("dsn", "bar", 2)
                                .addCoveredId("req", "bar", 2)
                                .addNeedsArtifactType("impl")
                                .location("1.2.md", 4)
                                .forwards(true)
                                .build()));
    }

    // [utest -> dsn~md.artifact-forwarding-notation~1]
    @Test
    void testForwardingAfterTags()
    {
        assertImport("1.2.md", """
                dsn~foo~1
                Tags: vanilla, strawberry, mint
                dsn-->impl:req~bar~2
                """,
                contains(
                        item()
                                .id("dsn", "foo", 1)
                                .location("1.2.md", 1)
                                .addTag("vanilla")
                                .addTag("strawberry")
                                .addTag("mint")
                                .build(),
                        item()
                                .id("dsn", "bar", 2)
                                .addCoveredId("req", "bar", 2)
                                .addNeedsArtifactType("impl")
                                .location("1.2.md", 3)
                                .forwards(true)
                                .build()));
    }

    // [utest -> dsn~md.artifact-forwarding-notation~1]
    @Test
    void testMultipleForwardsInARow()
    {
        assertImport("fwd.md", """
                # A Collection of Different Forwards
                * `arch --> dsn : req~foo~1`
                * arch  -->dsn  : req~bar~2   with a comment
                dsn-->impl      :    req~zoo~3
                """,
                contains(
                        item()
                                .id("arch", "foo", 1).addCoveredId("req", "foo", 1).addNeedsArtifactType("dsn")
                                .forwards(true).location("fwd.md", 2)
                                .build(),
                        item()
                                .id("arch", "bar", 2).addCoveredId("req", "bar", 2).addNeedsArtifactType("dsn")
                                .forwards(true).location("fwd.md", 3)
                                .build(),
                        item()
                                .id("dsn", "zoo", 3).addCoveredId("req", "zoo", 3).addNeedsArtifactType("impl")
                                .forwards(true).location("fwd.md", 4)
                                .build()));
    }

    // [utest -> dsn~md.artifact-forwarding-notation~1]
    @Test
    void testArtifactForwardingAfterARegularSpecificationItem()
    {
        assertImport("üöä", """

                art~name~9876

                # Forwards
                a-->b:c~d~5
                """,
                contains(
                        item()
                                .id("art", "name", 9876)
                                .location("üöä", 2)
                                .build(),
                        item()
                                .id("a", "d", 5)
                                .addCoveredId("c", "d", 5)
                                .addNeedsArtifactType("b")
                                .location("üöä", 5)
                                .forwards(true)
                                .build()));
    }

    // [utest -> dsn~md.artifact-forwarding-notation~1]
    @Test
    void testForwardingAfterCovers()
    {
        assertImport("1.2.md", """
                dsn~foo~1
                Covers:
                * req~foo~1
                dsn-->impl:req~bar~2
                """,
                contains(
                        item()
                                .id("dsn", "foo", 1)
                                .location("1.2.md", 1)
                                .addCoveredId("req", "foo", 1)
                                .build(),
                        item()
                                .id("dsn", "bar", 2)
                                .addCoveredId("req", "bar", 2)
                                .addNeedsArtifactType("impl")
                                .location("1.2.md", 4)
                                .forwards(true)
                                .build()));
    }

    // [utest -> dsn~md.artifact-forwarding-notation~1]
    @Test
    void testForwardingAfterNeeds()
    {
        assertImport("1.2.md", """
                dsn~foo~1
                Needs: impl
                dsn-->impl:req~bar~2
                """,
                contains(
                        item()
                                .id("dsn", "foo", 1)
                                .location("1.2.md", 1)
                                .addNeedsArtifactType("impl")
                                .build(),
                        item()
                                .id("dsn", "bar", 2)
                                .addCoveredId("req", "bar", 2)
                                .addNeedsArtifactType("impl")
                                .location("1.2.md", 3)
                                .forwards(true)
                                .build()));
    }

    @Test
    void testComplexRequirement()
    {
        assertImport("file name", """
                # Requirement Title
                `type~id~1` <a id="type~id~1"></a>
                Description

                More description

                Rationale:
                Rationale
                More rationale

                Covers:

                  * impl~foo1~1
                 + [Link to baz2](#impl~baz2~2)

                Depends:

                  + configuration~blubb.blah.blah~4711
                  - db~blah.blubb~42

                Comment:

                Comment
                More comment

                Needs: artA , artB
                """,
                contains(item().id(SpecificationItemId.parseId("type~id~1")).title("Requirement Title")
                        .comment("Comment" + NL + "More comment")
                        .description("Description" + NL + NL + "More description")
                        .rationale("Rationale" + NL + "More rationale")
                        .addNeedsArtifactType("artA").addNeedsArtifactType("artB")
                        .addCoveredId(SpecificationItemId.parseId("impl~foo1~1"))
                        .addCoveredId(SpecificationItemId.parseId("impl~baz2~2"))
                        .addDependOnId(SpecificationItemId.parseId("configuration~blubb.blah.blah~4711"))
                        .addDependOnId(SpecificationItemId.parseId("db~blah.blubb~42"))
                        .location("file name", 2)
                        .build()));
    }

    @Test
    void testTwoConsecutiveSpecificationItems()
    {
        assertImport("file1.md", """
                dsn~foo~1
                First description

                Comment:

                First comment

                dsn~bar~2
                Second description

                Rationale:
                Second rationale
                """,
                contains(
                        item()
                                .id("dsn", "foo", 1)
                                .description("First description")
                                .comment("First comment")
                                .location("file1.md", 1)
                                .build(),
                        item()
                                .id("dsn", "bar", 2)
                                .description("Second description")
                                .rationale("Second rationale")
                                .location("file1.md", 8)
                                .build()));
    }

    static Stream<Arguments> needsCoverage()
    {
        return Stream.of(
                Arguments.of("Needs: req , dsn ", List.of("req", "dsn")),
                Arguments.of("Needs: req ", List.of("req")),
                Arguments.of("Needs: req,dsn ", List.of("req", "dsn")),
                Arguments.of("Needs: req ,dsn", List.of("req", "dsn")),
                Arguments.of("Needs: req,dsn", List.of("req", "dsn")),
                Arguments.of("Needs: req,\tdsn\n", List.of("req", "dsn")),
                Arguments.of("Needs:req,dsn", List.of("req", "dsn")),
                Arguments.of("Needs:\n* req\n* dsn", List.of("req", "dsn")),
                Arguments.of("Needs:\n * req\n * dsn", List.of("req", "dsn")),
                Arguments.of("Needs:\n* req \n\t* dsn ", List.of("req", "dsn")),
                Arguments.of("Needs:\n* req\n* dsn", List.of("req", "dsn")));
    }

    @ParameterizedTest
    @MethodSource("needsCoverage")
    void testNeedsCoverage(final String mdContent, final List<String> expected)
    {
        final List<SpecificationItem> items = runImporterOnText("irrelevant-filename", "`a~b~1`\n" + mdContent,
                getImporterFactory());
        assertThat(items.get(0).getNeedsArtifactTypes(), equalTo(expected));
    }

    @Test
    void testItemStatsRecognized()
    {
        assertImport("status.MD", """
                `arch~status-enum~1`
                Status: draft
                """,
                contains(item()
                        .id("arch", "status-enum", 1)
                        .status(ItemStatus.DRAFT)
                        .location("status.MD", 1)
                        .build()));
    }

    // [impl -> dsn~md.specification-item-id-format~3] with UTF-8
    @Test
    void testItemIdSupportsUTF8Characaters()
    {
        assertImport("umlauts", """
                ### Die Implementierung muss den Zustand einzelner Zellen ändern
                `req~zellzustandsänderung~1
                Ermöglicht die Aktualisierung des Zustands von lebenden und toten Zellen in jeder Generation.
                Needs: arch
                """,
                contains(item()
                        .id(SpecificationItemId.createId("req", "zellzustandsänderung", 1))
                        .title("Die Implementierung muss den Zustand einzelner Zellen ändern")
                        .description("Ermöglicht die Aktualisierung des Zustands von lebenden und toten Zellen"
                                + " in jeder Generation.")
                        .location("umlauts", 2).addNeedsArtifactType("arch")
                        .build()));
    }
}
