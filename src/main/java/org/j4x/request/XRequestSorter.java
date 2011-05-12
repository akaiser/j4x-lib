package org.j4x.request;

import com.google.gson.annotations.SerializedName;

public class XRequestSorter {

    @SerializedName("sortpath")
    private String sortPath;

    public String getSortPath() {
        return sortPath;
    }

    public void setSortPath(String sortPath) {
        this.sortPath = sortPath;
    }
}
