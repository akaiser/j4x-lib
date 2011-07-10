package org.j4x.extension;

import java.util.Arrays;
import java.util.List;

public class XTablePaging {

    private Integer rowCount = null;
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
     * @param subList of objects
     * @return paginatorValues
     */
    public List getValues(List subList) {

        int subListSize = subList.size();


        if (rowCount != null) {

            // calculate the number of entries
            int itemsFrom = !subList.isEmpty()
                    ? pageNumber * rowCount - rowCount + 1
                    : 0;

            int itemsTo = pageNumber * rowCount > subListSize
                    ? subListSize
                    : pageNumber * rowCount;

            // calculate the number of pages
            int pagesSum = subListSize % rowCount == 0
                    ? subListSize / rowCount
                    : subListSize / rowCount + 1;

            int pagesFrom = pagesSum != 0
                    ? pageNumber
                    : 0;

            // de-/activate the buttons
            boolean previous = pagesFrom > 1,
                    next = pagesSum > pagesFrom;

            return Arrays.asList(
                    itemsFrom
                            + " - " + itemsTo + " / " + subListSize
                            + "|" + pagesFrom + " / " + pagesSum,
                    previous, previous, next, next);

        } else {
            return Arrays.asList(
                    (!subList.isEmpty() ? 1 : 0)
                            + " - " + subListSize
                            + "|" + 1 + " / " + 1);
        }
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
