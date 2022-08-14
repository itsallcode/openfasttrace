package org.itsallcode.openfasttrace.report.html.view.html;

import static org.mockito.Mockito.when;

import org.itsallcode.openfasttrace.api.core.Trace;
import org.itsallcode.openfasttrace.report.html.view.Viewable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestHtmlTraceSummary extends AbstractTestHtmlRenderer
{
    @Mock
    private Trace traceMock;

    @Override
    @BeforeEach
    public void prepareEachTest()
    {
        super.prepareEachTest();
    }

    @Test
    void testRenderSummaryOk()
    {
        when(this.traceMock.hasNoDefects()).thenReturn(true);
        when(this.traceMock.count()).thenReturn(200);
        when(this.traceMock.countDefects()).thenReturn(0);
        renderTaceSummaryOnIndentationLevel(1);
        assertOutputLines("  " + CharacterConstants.CHECK_MARK
                + " 200 total <meter value=\"200\" max=\"200\">100%</meter>");
    }

    private void renderTaceSummaryOnIndentationLevel(final int indentationLevel)
    {
        final Viewable view = this.factory.createTraceSummary(this.traceMock);
        view.render(indentationLevel);
    }

    @ParameterizedTest
    @ValueSource(ints =
    { 0, 1, 50, 99 })
    void testRenderPercentagesNotOk(final int value)
    {
        final int maximum = 100;
        final int defects = maximum - value;
        when(this.traceMock.hasNoDefects()).thenReturn(false);
        when(this.traceMock.count()).thenReturn(maximum);
        when(this.traceMock.countDefects()).thenReturn(defects);
        renderTaceSummaryOnIndentationLevel(1);
        assertOutputLines("  " + CharacterConstants.CROSS_MARK + " " + maximum
                + " total <meter value=\"" + value + "\" low=\"99\" max=\"100\">" + value
                + "%</meter>" + " <span class=\".red\">" + defects + " defects</span>");
    }
}