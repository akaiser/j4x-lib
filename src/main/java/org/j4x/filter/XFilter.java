package org.j4x.filter;

import com.google.gson.annotations.SerializedName;
import org.j4x.constants.XTableContstants.TableFilterType;

/**
 * Helfer-Klasse fuer Elemente der Obeflaeche
 *
 * @author akaiser
 * @version 0.2 (01.03.11)
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

    public void setFilterValue(String value) {
        this.filterValue = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final XFilter other = (XFilter) obj;
        if ((this.elementId == null) ? (other.elementId != null) : !this.elementId.equals(other.elementId)) {
            return false;
        }
        if ((this.filterPath == null) ? (other.filterPath != null) : !this.filterPath.equals(other.filterPath)) {
            return false;
        }
        if (this.filterType != other.filterType) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }
}
