package org.j4x.extension;

import com.google.gson.annotations.SerializedName;

public class XTableSort {

    @SerializedName("sortpath")
    private String sortPath = null;
    @SerializedName("sortasc")
    private Boolean sortAsc = true;

    /**
     * Liefert ein Array fuer den vereinfachten Transport von Sortierattributen
     *
     * @return current sort values
     */
    public Object[] getValues() {
        return new Object[]{
                    sortPath,
                    sortAsc
                };
    }

    /*
     * Getters/Setters
     */
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
