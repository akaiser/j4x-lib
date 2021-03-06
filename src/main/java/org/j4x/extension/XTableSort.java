package org.j4x.extension;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

public class XTableSort {

    @SerializedName("sortpath")
    private String sortPath = null;
    @SerializedName("sortasc")
    private Boolean sortAsc = true;

    /**
     * @return current sort values
     */
    public List getValues() {
        return Arrays.asList(sortPath, sortAsc);
    }

    public Boolean isSortAsc() {
        return sortAsc;
    }

    public void setSortAsc(Boolean sortAsc) {
        this.sortAsc = sortAsc;
    }

    public String getSortPath() {
        return sortPath;
    }

    public void setSortPath(String sortPath) {
        this.sortPath = sortPath;
    }
}
