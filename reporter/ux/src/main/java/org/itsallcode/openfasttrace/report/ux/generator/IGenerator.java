package org.itsallcode.openfasttrace.report.ux.generator;

import org.itsallcode.openfasttrace.report.ux.model.UxModel;

import java.io.OutputStream;

public interface IGenerator {

    String type();

    public void generate(final OutputStream out, final UxModel model);

} // IGenerator
