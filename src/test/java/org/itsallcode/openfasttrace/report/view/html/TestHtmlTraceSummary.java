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

import static org.mockito.Mockito.when;

import org.itsallcode.openfasttrace.core.Trace;
import org.itsallcode.openfasttrace.report.view.Viewable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TestHtmlTraceSummary extends AbstractTestHtmlRenderer
{
    @Mock
    private Trace traceMock;

    @Override
    @BeforeEach
    public void prepareEachTest()
    {
        super.prepareEachTest();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testRenderSummaryOk()
    {
        when(this.traceMock.hasNoDefects()).thenReturn(true);
        when(this.traceMock.count()).thenReturn(200);
        when(this.traceMock.countDefects()).thenReturn(0);
        renderTaceSummaryOnIndentationLevel(1);
        assertOutputLines(
                CharacterConstants.CHECK_MARK + " 200 total " + CharacterConstants.FULL_CIRCLE);
    }

    private void renderTaceSummaryOnIndentationLevel(final int indentationLevel)
    {
        final Viewable view = this.factory.createTraceSummary(this.traceMock);
        view.render(indentationLevel);
    }

    @ParameterizedTest
    @CsvSource({ "0, " + CharacterConstants.EMPTY_CIRCLE, //
            "24, " + CharacterConstants.EMPTY_CIRCLE, //
            "25, " + CharacterConstants.QUATER_CIRCLE, //
            "49, " + CharacterConstants.QUATER_CIRCLE, //
            "50, " + CharacterConstants.HALF_CIRCLE, //
            "74, " + CharacterConstants.HALF_CIRCLE, //
            "75, " + CharacterConstants.THREE_QUARTERS_CIRCLE, //
            "99, " + CharacterConstants.THREE_QUARTERS_CIRCLE })
    void testRenderPercentagesNotOk(final int value, final String completionChar)
    {
        final int maximum = 100;
        final int defects = maximum - value;
        when(this.traceMock.hasNoDefects()).thenReturn(false);
        when(this.traceMock.count()).thenReturn(maximum);
        when(this.traceMock.countDefects()).thenReturn(defects);
        renderTaceSummaryOnIndentationLevel(1);
        assertOutputLines(CharacterConstants.CROSS_MARK + " " + maximum + " total " + completionChar
                + " <span class=\".red\">" + defects + " defects</span>");
    }
}