package org.j4x.filter;

import org.j4x.util.XHelper;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Helfer-Klasse fuer das Registrieren von XFilter-Instanzen der Obeflaeche
 * und das Filtern der Liste
 *
 * @author akaiser
 * @version 0.2 (20.07.10)
 */
public class XFilterContainer {

    // Liste mit allen Filtern
    private List<XFilter> filterList = new ArrayList<XFilter>();

    // Hinzufuegen einer XFilter-Instanz
    public void add(XFilter[] filter) {
        filterList.addAll(Arrays.asList(filter));
    }

    public void update(XFilter updateFilter) {

        for (XFilter presentFilter : filterList) {

            // update the value
            if (presentFilter.equals(updateFilter)) {
                presentFilter.setFilterValue(updateFilter.getFilterValue());

                // @todo done here? not really
                // onsubmit for all filter updates possible
                return;
            }
        }
    }

    /**
     * Filterung der Liste anhand von gegebenen Filterelementen
     *
     * @param mainList  Die Liste mit allen Elementen
     */
    public List filter(List mainList) {

        // list with filtered objects
        List<Object> temp = new ArrayList<Object>();

        // value to check
        String objValue = null;

        // iterate over all objects in list
        for (Object o : mainList) {

            // value is valid
            boolean isValid = true;

            // Durchlauf aller verfuegbarer XFilter
            for (XFilter filter : filterList) {

                if (filter.getFilterValue() != null
                        && !filter.getFilterValue().isEmpty()) {
                    try {
                        Method om = null;
                        Object oc = o;

                        // Durchlauf aller verschachtelten Methoden
                        for (String method : XHelper.getMethodPath(filter.getFilterPath())) {
                            om = oc.getClass().getMethod(method, new Class[]{});
                            oc = om.invoke(oc, new Object[]{});
                        }

                        // set the value
                        objValue = oc.toString();

                        switch (filter.getFilterType()) {

                            case INPUT: {

                                // does not match
                                if (!objValue.toUpperCase().matches(".*" + filter.getFilterValue().toUpperCase() + ".*")) {
                                    isValid = false;
                                    break;
                                }
                                break;
                            }

                            case SELECTONE: {

                                // is not equal
                                if (!objValue.equals(filter.getFilterValue())) {
                                    isValid = false;
                                    break;
                                }
                                break;
                            }

                            case SELECTMULTIPLE: {
                                // @todo selectmultiple
                                /*
                                isValid = false;
                                iterate over all filter values - esp. for selectmultiple
                                for (Object filterValue : filter.getFilterValue()) {
                                
                                if (oc.toString().toUpperCase().matches(".*" + filterValue.toString().toUpperCase() + ".*")) {
                                isValid = true;
                                break;
                                }
                                }
                                 */
                                break;
                            }
                        }

                    } catch (IllegalAccessException ex) {
                    } catch (IllegalArgumentException ex) {
                    } catch (InvocationTargetException ex) {
                    } catch (NoSuchMethodException ex) {
                    } catch (SecurityException ex) {
                    }
                }
            }

            // object is valid, add to list
            if (isValid) {
                temp.add(o);
            }
        }

        return temp;
    }

    // Rueckgabe der XFilter-Werte
    public List<Object> getValues(List subList) {

        List<Object> temp = new ArrayList<Object>();

        // iterate over all registered filters
        for (XFilter filter : filterList) {

            Object filterData = null;

            // output depends on filtertype
            switch (filter.getFilterType()) {
                case INPUT:
                case SUGGEST:
                    filterData = new Object[]{
                                filter.getElementId(),
                                filter.getFilterType().toString(),
                                filter.getFilterValue()
                            };
                    break;
                case SELECTONE:

                    filterData = new Object[]{
                                filter.getElementId(),
                                filter.getFilterType().toString(),
                                getOptions(filter, subList),
                                filter.getFilterValue()
                            };
                    break;
                case SELECTMULTIPLE:

                    // @todo
                    /*
                    filterData = new Object[]{
                    filter.getElementId(),
                    filter.getFilterType().toString(),
                    getOptions(filter, subList),
                    filter.getFilterValue()
                    };
                     */
                    break;
            }

            temp.add(filterData);
        }

        return temp;
    }

    public void resetFilterValues() {
        for (XFilter filter : filterList) {
            filter.setFilterValue(null);
        }
    }

    /**
     * Suchen, Filtern und Sortiren von Elementen aller Objekte einer Spalte
     *
     * @param filter    the filter
     * @param subList   list with elements (subList)
     * @return gefiltertes Array
     */
    private Object[] getOptions(XFilter filter, List subList) {

        // Liste mit gefilterten Objekten
        Set<Object> temp = new TreeSet<Object>();

        String[] methodPath = XHelper.getMethodPath(filter.getFilterPath());

        // Durchlauf aller Objekte der Liste
        for (Object o : subList) {

            try {
                Method om = null;
                Object oc = o;

                // Durchlauf aller verschachtelten Methoden
                for (String method : methodPath) {
                    om = oc.getClass().getMethod(method, new Class[]{});
                    oc = om.invoke(oc, new Object[]{});
                }

                temp.add(oc);

            } catch (IllegalAccessException ex) {
            } catch (IllegalArgumentException ex) {
            } catch (InvocationTargetException ex) {
            } catch (NoSuchMethodException ex) {
            } catch (SecurityException ex) {
            }
        }
        return temp.toArray();
    }

    public boolean isEmpty() {
        return filterList.isEmpty();
    }
}
