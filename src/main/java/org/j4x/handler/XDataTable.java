package org.j4x.handler;

import com.google.gson.Gson;
import org.j4x.constants.XTableContstants.TableRequestType;
import org.j4x.extension.XTablePaging;
import org.j4x.extension.XTableSort;
import org.j4x.filter.XFilter;
import org.j4x.util.XComparator;
import org.j4x.filter.XFilterContainer;
import org.j4x.request.XRequestInit;
import org.j4x.request.XRequestPaging;
import org.j4x.request.XRequestSort;
import org.j4x.util.XHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 *
 * Provides filters, sort, and caching functionality for
 * simplified communication with the client
 *
 * @author akaiser
 * @version 0.2 (20.07.10)
 */
public abstract class XDataTable {

    // Sort
    private XTableSort sort = new XTableSort();
    // Paging
    private XTablePaging paging = new XTablePaging();
    // Container for filters elements
    private XFilterContainer filterContainer = new XFilterContainer();
    // Gson
    private Gson gson = new Gson();
    // filtered and sorted object list
    private List<?> subList = null;
    // main object list
    protected List<?> mainList = null;

    /**
     * Methode zum Erstellen der Mainliste
     * mit allen verfuegbaeren Objekten
     */
    protected abstract List<?> buildMainList();

    /**
     * Methode zum Erstellen/Holen von Atrributen als Array aus einem
     * gebebenen Objekt
     */
    protected abstract Object[] getObjectValues(Object object);

    /**
     * Schnittstelle zur Oberflaeche. Liefert eine Liste fuer den vereinfachten
     * Transport von Objektattributen der gesamten Tabelle
     *
     * @param requestType types like [INIT, SORT, PAGING, FILTER]
     * @param requestParams as JSON-String
     * @return Array mit Daten und Informationen zum Sort und Paging
     */
    public HashMap getContent(String requestType, String requestParams) {

        TableRequestType rt = TableRequestType.valueOf(requestType);

        switch (rt) {

            // Initialer Aufruf der Tabelle
            case INIT: {

                XRequestInit req = gson.fromJson(requestParams, XRequestInit.class);

                // Wenn keine Voreinstellungen fuer Sort vorhanden
                if (sort.getSortPath() == null) {

                    // und Parameter fuer den Sort existieren
                    if (req.getSortpath() != null) {
                        sort.setSortPath(req.getSortpath());
                    }
                }

                // Wenn keine Voreinstellungen fuer Paging vorhanden
                if (paging.getRowCount() == null) {

                    // und Parameter fuer den Paging existieren
                    if (req.getRowcount() != null) {
                        paging.setRowCount(req.getRowcount());
                    }
                }

                // Initialisierung der Filter
                if (filterContainer.isEmpty()
                        && req.getFilters() != null) {
                    filterContainer.add(req.getFilters());
                }

                // initiales Erstellen der Listen
                if (mainList == null) {
                    createMainList();
                }

                break;
            }
            // Ein XFilter-Event
            case FILTER: {

                // Falls noch kein XFilter vorhanden
                filterContainer.update(gson.fromJson(requestParams, XFilter.class));

                filterSubList();

                break;
            }

            // Ein Sort-Event
            case SORT: {

                XRequestSort req = gson.fromJson(requestParams, XRequestSort.class);

                // Wenn Liste sortierbar ist
                if (subList.size() > 1) {

                    // Falls Anfrage aktuell
                    if (sort.getSortPath() != null && sort.getSortPath().equals(req.getSortPath())) {

                        // Dann Sortierreihenfolge drehen
                        sort.setSortAsc(sort.isSortAsc() ? false : true);
                    } // Sonst die Sortierung aendern
                    else {

                        // Identifizieren/Setzen des refl Paths
                        sort.setSortPath(req.getSortPath());

                        // Die Abfolge neutralisieren
                        sort.setSortAsc(true);
                    }

                    // Sortierung durchfuehren
                    sortSubList();
                }
                break;
            }

            // Ein Paging-Event
            case PAGING: {

                switch (gson.fromJson(requestParams, XRequestPaging.class).getEvent()) {

                    case FIRST:
                        paging.setPageNumber(1);
                        break;

                    case PREVIOUS:
                        paging.turnPage(false);
                        break;

                    case NEXT:
                        paging.turnPage(true);
                        break;

                    case LAST:
                        paging.setLastPage(subList);
                        break;

                    // reload whole list
                    case RELOAD:

                        // reset all filters
                        filterContainer.resetFilterValues();

                        // generate main list
                        createMainList();
                        break;
                }
                break;
            }
        }

        return new HashMap<String, Object>() {

            {
                put("body", getValues());
                put("sort", sort.getValues(subList));
                put("paging", paging.getValues(subList));
                put("filter", filterContainer.getValues(subList));
            }
        };

        /*
        // create responce
        return new Object[]{
        getValues(),
        sort.getValues(subList),
        paging.getValues(subList),
        filterContainer.getValues(subList)
        };*/
    }

    /**
     * Liefert eine Liste fuer den vereinfachten Transport von
     * Tabellenzeilen an die Oberflaeche.
     *
     * @return tableValues
     */
    private List<Object> getValues() {

        // Liste der Eintraege zum Transport an die Oberflaeche
        List<Object> tableValues = new ArrayList<Object>();

        // Ist die Tabelle in ihrer Darstellung begrenzt, dann filtern
        if (paging.getRowCount() != null) {

            for (int i = 0; i
                    < paging.getRowCount(); i++) {

                // Aktueller index der Eintraege in der gesamten Subliste
                int index = paging.getEntryPosition(i);

                // Falls kein Eintrag mehr verfuegbar
                if (index >= subList.size()) {
                    break;
                }

                if (subList.get(index) != null) {

                    // Holen der ObjectValues
                    tableValues.add(getObjectValues(subList.get(index)));
                }
            }
        } // Sonst alle Eintraege der Subliste senden
        else {
            for (Object o : subList) {

                // Holen der ObjectValues
                tableValues.add(getObjectValues(o));
            }
        }

        return tableValues;
    }

    /**
     * Erstellen der Mainliste
     */
    private void createMainList() {

        // Erzeugen der Hauptliste
        mainList = buildMainList();

        // Filterung anstossen
        filterSubList();
    }

    /**
     *  Filterung der Subliste durchfuehren
     */
    private void filterSubList() {

        subList = filterContainer.filter(mainList);

        // Sortierung anstossen
        sortSubList();
    }

    /**
     *  Sortierung der Subliste durchfuehren
     */
    private void sortSubList() {
        if (sort.getSortPath() != null) {

            // Sortierung durchfuehren
            Collections.sort(subList, new XComparator(XHelper.getMethodPath(sort.getSortPath()), sort.isSortAsc()));
        }

        // Seitenindex neutralisieren
        paging.setPageNumber(1);
    }
}
