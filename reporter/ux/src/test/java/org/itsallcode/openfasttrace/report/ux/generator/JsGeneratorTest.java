package org.itsallcode.openfasttrace.report.ux.generator;

import org.itsallcode.openfasttrace.report.ux.Collector;
import org.itsallcode.openfasttrace.report.ux.SampleData;
import org.itsallcode.openfasttrace.report.ux.model.UxModel;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class JsGeneratorTest {

    @Test
    void type() {
        final IGenerator generator = new JsGenerator();
        assertThat( generator.type(), equalTo(JsGenerator.TYPE ));

    }

    @Test
    void generate() {
        final UxModel model = new Collector().collect(SampleData.LINKED_SAMPLE_ITEMS).getUxModel();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        new JsGenerator().generate(out,model);
        System.out.println(out);
    }

    @Test
    void regexp() {
        String text = "'Users can extend OFT's features with plugins from third parties.'";
        String o = text.replace("'","\\\'").replaceAll("\n\r?|\r", "<br>");
        System.out.println(o);
    }

} // JsGeneratorTest