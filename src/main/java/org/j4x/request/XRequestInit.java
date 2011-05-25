package org.j4x.request;

import com.google.gson.annotations.SerializedName;
import org.j4x.extension.XTableSort;
import org.j4x.filter.XFilter;

public class XRequestInit {

    @SerializedName("rowcount")
    private Integer rowCount;
    @SerializedName("sort")
    private XTableSort sort;
    @SerializedName("filter")
    XFilter[] filters;

    public XFilter[] getFilters() {
        return filters;
    }

    public void setFilters(XFilter[] filters) {
        this.filters = filters;
    }

    public Integer getRowCount() {
        return rowCount;
    }

    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    public XTableSort getSort() {
        return sort;
    }

    public void setSort(XTableSort sort) {
        this.sort = sort;
    }
}
