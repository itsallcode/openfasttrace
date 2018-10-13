package org.itsallcode.openfasttrace.report.view.html;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.itsallcode.openfasttrace.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.report.html.HtmlReport;
import org.itsallcode.openfasttrace.report.view.ViewFactory;
import org.itsallcode.openfasttrace.report.view.Viewable;
import org.junit.Test;

public class TestHtmlViewFactory
{
    private LinkedSpecificationItem item;

    @Test
    public void testCreateSpecificationItem()
    {
        final OutputStream outputStream = new ByteArrayOutputStream();
        final ViewFactory factory = HtmlViewFactory.create(outputStream, HtmlReport.getCssUrl());
        final Viewable view = factory.createSpecificationItem(this.item);
        assertThat(view, instanceOf(HtmlSpecificationItem.class));
    }
}
