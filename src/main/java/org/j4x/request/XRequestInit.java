package org.j4x.request;

import com.google.gson.annotations.SerializedName;
import org.j4x.filter.XFilter;

public class XRequestInit {

    @SerializedName("rowcount")
    private Integer rowCount;
    @SerializedName("sortpath")
    private String sortPath;
    @SerializedName("filter")
    XFilter[] filters;

    public XFilter[] getFilters() {
        return filters;
    }

    public void setFilters(XFilter[] filters) {
        this.filters = filters;
    }

    public Integer getRowcount() {
        return rowCount;
    }

    public void setRowcount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    public String getSortpath() {
        return sortPath;
    }

    public void setSortpath(String sortPath) {
        this.sortPath = sortPath;
    }
}
