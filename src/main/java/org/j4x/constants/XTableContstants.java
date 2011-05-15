package org.j4x.constants;

public class XTableContstants {

    public static enum TableRequestType {

        INIT, SORT, PAGING, FILTER
    }

    public static enum TablePagingEvent {

        FIRST, PREVIOUS, NEXT, LAST, RELOAD
    }

    public static enum TableFilterType {

        INPUT, SUGGEST, SELECTONE, SELECTMULTIPLE
    }
}
