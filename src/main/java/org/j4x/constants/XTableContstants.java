package org.j4x.constants;

public class XTableContstants {

    public static enum TableRequestType {

        INIT, SORTER, PAGINATOR, FILTER
    }

    public static enum TablePaginatorEvent {

        FIRST, PREVIOUS, NEXT, LAST, RELOAD
    }

    public static enum TableFilterType {

        INPUT, SUGGEST, SELECTONE, SELECTMULTIPLE
    }
}
