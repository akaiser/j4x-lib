package org.j4x.extension;

import java.util.List;

public class XTablePaging {

    /**
     * Die Anzahl sichtbarer Eintraege/Seite
     */
    private Integer rowCount = null;
    /**
     * Die aktuell dargestellte Seite
     */
    private Integer pageNumber = 1;

    public void turnPage(boolean up) {
        if (up) {
            pageNumber++;
        } else {
            pageNumber--;
        }
    }

    public void setLastPage(List subList) {
        pageNumber = (subList.size() % rowCount == 0)
                ? (subList.size() / rowCount)
                : (subList.size() / rowCount + 1);
    }

    public int getEntryPosition(int i) {
        return (pageNumber * rowCount) - rowCount + i;
    }

    /**
     * Liefert ein Array fuer den vereinfachten Transport von
     * Paginatorattributen an die Oberflaeche.
     *
     * @return paginatorValues
     */
    public Object[] getValues(List subList) {

        // Berechnung der Anzahl von Eintraegen
        if (rowCount != null) {
            int itemsFrom = !subList.isEmpty()
                    ? pageNumber * rowCount - rowCount + 1
                    : 0;

            int itemsTo = pageNumber * rowCount > subList.size()
                    ? subList.size()
                    : pageNumber * rowCount;

            // Berechnung der Anzahl von Seiten
            int pagesSum = (subList.size() % rowCount == 0)
                    ? (subList.size() / rowCount)
                    : (subList.size() / rowCount + 1);

            int pagesFrom = pagesSum != 0
                    ? pageNumber
                    : 0;

            // Setzen von de-/aktiviert Eigenschaft der Buttons
            boolean previous, next;
            previous = pagesFrom <= 1 ? false : true;
            next = pagesSum <= pagesFrom ? false : true;

            // Bauen des Arrays
            return new Object[]{
                        "entry: "
                        + itemsFrom
                        + " - " + itemsTo
                        + " / " + subList.size()
                        + " | page: "
                        + pagesFrom
                        + " / "
                        + pagesSum,
                        previous,
                        previous,
                        next,
                        next
                    };

        } else {

            int itemsFrom = !subList.isEmpty() ? 1 : 0;

            return new Object[]{
                        "entry: "
                        + itemsFrom
                        + " - "
                        + subList.size()
                    };
        }
    }

    /*
     * Getters/Setters
     */
    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getRowCount() {
        return rowCount;
    }

    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }
}
