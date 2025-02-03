package org.itsallcode.openfasttrace.report.ux.model;

public enum Coverage
{
    COVERED(0),
    UNCOVERED(1),
    NONE(2);

    Coverage(int index) {
        this.index = index;
    }

    private final int index;

    public int getIndex()
    {
        return index;
    }
}
