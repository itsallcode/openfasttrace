package org.itsallcode.openfasttrace.report.ux.generator;

import org.itsallcode.openfasttrace.api.core.Location;
import org.itsallcode.openfasttrace.report.ux.model.UxModel;
import org.itsallcode.openfasttrace.report.ux.model.UxSpecItem;
import org.itsallcode.openfasttrace.report.ux.model.WrongLinkType;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class JsGenerator implements IGenerator {

    public static final String TYPE = "js";

    /**
     * Indentation spaces
     */
    private static final int INDENT = 2;
    private static final int LINE_LENGTH = 120;

    private int indent = 0;
    private PrintStream out = null;

    public JsGenerator() {
    }

    @Override public String type() {
        return TYPE;
    }

    @Override public void generate(final OutputStream outputStream, final UxModel model) {
        out = new PrintStream(outputStream, false, StandardCharsets.UTF_8);
        generateHeader(model);
        generateMetaData(model);
        generateSpecItemsOpen(model);
        model.getItems().forEach(this::generateSpecItem);
        generateSpecItemsClose(model);
        generateFooter(model);
    }

    private void generateHeader(final UxModel model) {
        printOpen("(function (window,undefined) {");
        printOpen("window.specitem = {");
    }

    private void generateMetaData(final UxModel model) {
        printOpen("project: {");
        println("projectName", model.getProjectName());
        println("types", model.getArtifactTypes());
        println("tags", model.getTags());
        println("status",model.getStatusNames());
        println("wronglinkNames", model.getWrongLinkTypes().stream().map(WrongLinkType::toString).toList());
        println("item_count", model.getItems().size());
        println("item_covered", model.getItems().size() - model.getUncoveredSpecItems());
        println("item_uncovered", model.getUncoveredSpecItems());
        println("type_count",model.getTypeCount());
        println("uncovered_count", model.getUncoveredCount());
        println("status_count",model.getStatusCount());
        println("tag_count", model.getTagCount());
        println("wronglink_count", model.getWrongLinkCount());
        printClose("},");
    }

    private void generateSpecItemsOpen(final UxModel model) {
        printOpen("specitems: [");
    }

    private void generateSpecItem(final UxSpecItem item) {
        printOpen("{");
        println("index", item.getIndex());
        println("type", item.getTypeIndex());
        println("title", item.getTitle());
        println("name", item.getName());
        println("id", item.getId());
        println("tags", item.getTagIndex());
        println("version", item.getItem().getRevision());
        println("content", item.getItem().getItem().getDescription());
        println("provides", item.getProvidesIndex());
        println("needs", item.getNeededTypeIndex());
        println("covered", item.getCoveredIndex());
        println("uncovered", item.getUncoveredIndex());
        println("covering", item.getCoveringIndex());
        println("coveredBy", item.getCoveredByIndex());
        println("depends", item.getDependsIndex());
        println("status", item.getStatusId());
        println("path", item.getPath());
        final Location location = item.getItem().getItem().getLocation();
        println("sourceFile", location != null ? location.getPath() : "");
        println("sourceLine", location != null ? location.getLine() : 0);
        println("comments", item.getItem().getItem().getComment());
        println("wrongLinkTypes", item.getWrongLinkTypes());
        println("wrongLinkTargets",
                item.getWrongLinkTargets().entrySet().stream().map(entry ->
                        String.format("%s[%s]", entry.getKey(), entry.getValue())).toList()
        );
        printClose("},");
    }

    private void generateSpecItemsClose(final UxModel model) {
        printClose("]");
    }

    private void generateFooter(final UxModel model) {
        printClose("}");
        printClose("})(window);");
    }

    private void printf(final String format, Object... args) {
        out.print(" ".repeat(indent));
        out.printf(format, args);
        out.println();
    }

    private void println(final String name, final int value) {
        printf("%s: %d,", name, value);
    }

    private void println(final String name, final String value) {
        printf("%s:%s", name, wrap(value, name.length()));
    }

    private <T> void println(final String name, final List<T> values) {
        printf("%s: [%s],",
                name,
                values.stream().map(value ->
                        ( value instanceof String ) ? "\"" + value + "\"" : value.toString()
                ).collect(Collectors.joining(", ")));
    }

    private void printOpen(String text) {
        out.println(" ".repeat(indent) + text);
        indentBegin();
    }

    private void printClose(String text) {
        indentEnd();
        out.println(" ".repeat(indent) + text);
    }

    private void indentBegin() {
        indent += INDENT;
    }

    private void indentEnd() {
        indent -= INDENT;
    }

    private String wrap(final String text, final int offset) {
        final String value = quote(text);
        if( value.length() < ( LINE_LENGTH - offset - INDENT - 2 ) ) return " '" + value + "',";

        final StringBuilder b = new StringBuilder();
        b.append(System.lineSeparator());

        indentBegin();
        final int fragmentLength = LINE_LENGTH - offset - INDENT - 3;
        for( int i = 0; i < value.length(); i += fragmentLength ) {
            b.append(" ".repeat(indent));
            b.append(i == 0 ? "'" : "+ '");
            b.append(value, i, Math.min(i + fragmentLength, value.length()));
            b.append(( i + fragmentLength ) < value.length() ? "'" + System.lineSeparator() : "',");
        }
        indentEnd();

        return b.toString();
    }

    private String quote(final String text) {
        return text.replace("'", "\\'")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replaceAll("\n\r?|\r", "<br>");
    }

} // JsGenerator