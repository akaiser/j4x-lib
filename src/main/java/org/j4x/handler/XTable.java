package org.j4x.handler;

import com.google.gson.Gson;
import org.j4x.constants.XTableContstants.TableRequestType;
import org.j4x.extension.XTablePaginator;
import org.j4x.extension.XTableSorter;
import org.j4x.filter.XFilter;
import org.j4x.util.XComparator;
import org.j4x.filter.XFilterContainer;
import org.j4x.request.XRequestInit;
import org.j4x.request.XRequestPaginator;
import org.j4x.request.XRequestSorter;
import org.j4x.util.XHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 *
 * Provides filter, sort, and caching functionality for
 * simplified communication with the client
 *
 * @author akaiser
 * @version 0.2 (20.07.10)
 */
public abstract class XTable {

    // Sorter
    private XTableSorter sorter = new XTableSorter();
    // Paginator
    private XTablePaginator paginator = new XTablePaginator();
    // Registriert die Filter-Instanzen
    private XFilterContainer filterContainer = new XFilterContainer();
    // Gson instanz
    private Gson gson = new Gson();
    // Die Liste mit gefilterten/sortierten Objekten
    private List<?> subList = null;
    // Die Liste mit allen Objekten
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
     * @param requestType   type like [INIT, SORTER, PAGINATOR, FILTER]
     * @param requestParams as JSON-String
     * @return Array mit Daten und Informationen zum Sorter und Paginator
     */
    public HashMap getTableContent(String requestType, String requestParams) {

        TableRequestType rt = TableRequestType.valueOf(requestType);

        switch (rt) {

            // Initialer Aufruf der Tabelle
            case INIT: {

                XRequestInit req = gson.fromJson(requestParams, XRequestInit.class);

                // Wenn keine Voreinstellungen fuer Sortervorhanden
                if (sorter.getSortPath() == null) {

                    // und Parameter fuer den Sorter existieren
                    if (req.getSortpath() != null) {
                        sorter.setSortPath(req.getSortpath());
                    }
                }

                // Wenn keine Voreinstellungen fuer Paginator vorhanden
                if (paginator.getRowCount() == null) {

                    // und Parameter fuer den Paginator existieren
                    if (req.getRowcount() != null) {
                        paginator.setRowCount(req.getRowcount());
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
            case SORTER: {

                XRequestSorter req = gson.fromJson(requestParams, XRequestSorter.class);

                // Wenn Liste sortierbar ist
                if (subList.size() > 1) {

                    // Falls Anfrage aktuell
                    if (sorter.getSortPath() != null && sorter.getSortPath().equals(req.getSortPath())) {

                        // Dann Sortierreihenfolge drehen
                        sorter.setSortAsc(sorter.isSortAsc() ? false : true);
                    } // Sonst die Sortierung aendern
                    else {

                        // Identifizieren/Setzen des refl Paths
                        sorter.setSortPath(req.getSortPath());

                        // Die Abfolge neutralisieren
                        sorter.setSortAsc(true);
                    }

                    // Sortierung durchfuehren
                    sortSubList();
                }
                break;
            }

            // Ein Paginator-Event
            case PAGINATOR: {

                switch (gson.fromJson(requestParams, XRequestPaginator.class).getEvent()) {

                    case FIRST:
                        paginator.setPageNumber(1);
                        break;

                    case PREVIOUS:
                        paginator.turnPage(false);
                        break;

                    case NEXT:
                        paginator.turnPage(true);
                        break;

                    case LAST:
                        paginator.setLastPage(subList);
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
                put("sorter", sorter.getValues(subList));
                put("paginator", paginator.getValues(subList));
                put("filter", filterContainer.getValues(subList));
            }
        };
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
        if (paginator.getRowCount() != null) {

            for (int i = 0; i
                    < paginator.getRowCount(); i++) {

                // Aktueller index der Eintraege in der gesamten Subliste
                int index = paginator.getEntryPosition(i);

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
        if (sorter.getSortPath() != null) {

            // Sortierung durchfuehren
            Collections.sort(subList, new XComparator(XHelper.getMethodPath(sorter.getSortPath()), sorter.isSortAsc()));
        }

        // Seitenindex neutralisieren
        paginator.setPageNumber(1);
    }
}
