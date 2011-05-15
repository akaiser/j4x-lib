package org.j4x.extension;

import java.util.List;

public class XTableSort {

    // Die aktuelle Identifikation des refl. Path
    private String sortPath = null;
    // Die Abfolge der Sortierung
    private boolean sortAsc = true;

    /**
     * Liefert ein Array fuer den vereinfachten Transport von Sortierattributen
     *
     * @return Groesse der Liste und die aktuelle Sortierspalte
     */
    public Object[] getValues(List subList) {
        return new Object[]{
                    subList.size(),
                    sortPath,
                    sortAsc
                };
    }

    /*
     * Getters/Setters
     */
    public boolean isSortAsc() {
        return sortAsc;
    }

    public void setSortAsc(boolean sortAsc) {
        this.sortAsc = sortAsc;
    }

    public String getSortPath() {
        return sortPath;
    }

    public void setSortPath(String sortPath) {
        this.sortPath = sortPath;
    }
}
