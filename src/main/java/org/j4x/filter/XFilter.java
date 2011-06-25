package org.j4x.filter;

import com.google.gson.annotations.SerializedName;
import com.sun.istack.internal.Nullable;
import org.j4x.constants.XTableContstants.TableFilterType;

/**
 * @author akaiser
 * @version 0.3 (01.03.11)
 */
public class XFilter {

    @SerializedName("id")
    private String elementId;
    @SerializedName("filterpath")
    private String filterPath;
    @SerializedName("filtervalue")
    private String filterValue;
    @SerializedName("filtertype")
    private TableFilterType filterType;


    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public TableFilterType getFilterType() {
        return filterType;
    }

    public void setFilterType(TableFilterType filterType) {
        this.filterType = filterType;
    }

    public String getFilterPath() {
        return filterPath;
    }

    public void setFilterPath(String methodPath) {
        this.filterPath = methodPath;
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(@Nullable String value) {
        this.filterValue = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        XFilter xFilter = (XFilter) o;

        return !(elementId != null ? !elementId.equals(xFilter.elementId) : xFilter.elementId != null) &&
                !(filterPath != null ? !filterPath.equals(xFilter.filterPath) : xFilter.filterPath != null) &&
                filterType == xFilter.filterType;
    }

    @Override
    public int hashCode() {
        int result = elementId != null ? elementId.hashCode() : 0;
        result = 31 * result + (filterPath != null ? filterPath.hashCode() : 0);
        result = 31 * result + (filterType != null ? filterType.hashCode() : 0);
        return result;
    }
}
