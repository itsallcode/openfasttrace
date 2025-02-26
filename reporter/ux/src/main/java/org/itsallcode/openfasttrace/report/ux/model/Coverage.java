package org.itsallcode.openfasttrace.report.ux.model;

public enum Coverage
{
    COVERED(2),
    UNCOVERED(1),
    NONE(0);

    Coverage(int id) {
        this.id = id;
    }

    private final int id;

    public int getId()
    {
        return id;
    }
}
