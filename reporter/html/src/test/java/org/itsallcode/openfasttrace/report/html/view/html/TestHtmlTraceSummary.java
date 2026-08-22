package org.itsallcode.openfasttrace.report.html.view.html;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.itsallcode.openfasttrace.api.core.LinkedSpecificationItem;
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

    @Mock
    private LinkedSpecificationItem directDefectMock;

    @Mock
    private LinkedSpecificationItem transitiveDefectMock;

    @Override
    @BeforeEach
    public void prepareEachTest()
    {
        super.prepareEachTest();
    }

    @Test
    // [utest->dsn~reporting.html.summary~2]
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
    // [utest->dsn~reporting.html.summary~2]
    void testRenderPercentagesNotOk(final int value)
    {
        final int maximum = 100;
        final int defectsCount = maximum - value;
        when(this.traceMock.hasNoDefects()).thenReturn(false);
        when(this.traceMock.count()).thenReturn(maximum);
        when(this.traceMock.countDefects()).thenReturn(defectsCount);

        final List<LinkedSpecificationItem> defectItems = new ArrayList<>();
        for (int i = 0; i < defectsCount; i++)
        {
            defectItems.add(this.directDefectMock);
        }
        when(this.traceMock.getDefectItems()).thenReturn(defectItems);
        when(this.directDefectMock.isTransitiveDefect()).thenReturn(false);

        renderTaceSummaryOnIndentationLevel(1);
        assertOutputLines("  " + CharacterConstants.CROSS_MARK + " " + maximum
                + " total <meter value=\"" + value + "\" low=\"99\" max=\"100\">" + value
                + "%</meter>" + " <span class=\"red\">" + defectsCount
                + " direct, 0 transitive defects</span>");
    }

    @Test
    // [utest->dsn~reporting.html.summary~2]
    void testRenderTransitiveDefects()
    {
        when(this.traceMock.hasNoDefects()).thenReturn(false);
        when(this.traceMock.count()).thenReturn(10);
        when(this.traceMock.countDefects()).thenReturn(2);
        when(this.traceMock.getDefectItems())
                .thenReturn(List.of(this.directDefectMock, this.transitiveDefectMock));
        when(this.directDefectMock.isTransitiveDefect()).thenReturn(false);
        when(this.transitiveDefectMock.isTransitiveDefect()).thenReturn(true);

        renderTaceSummaryOnIndentationLevel(0);
        assertOutputLines(CharacterConstants.CROSS_MARK
                + " 10 total <meter value=\"8\" low=\"9\" max=\"10\">80%</meter>"
                + " <span class=\"red\">1 direct, 1 transitive defects</span>");
    }
}