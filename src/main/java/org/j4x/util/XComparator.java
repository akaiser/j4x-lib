package org.j4x.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * XComparator
 *
 * @author akaiser
 * @version 2.0 (20.07.10)
 */
public class XComparator implements Comparator<Object> {

    private String[] methodPath = {"toString"};
    private boolean asc;

    /**
     * XComparator fuer die Sortiegung (ascending, mittels toString)
     */
    public XComparator() {
        this(true);
    }

    /**
     * XComparator fuer die Sortiegung (mittels toString)
     *
     * @param asc true = ascending, false = descending
     */
    public XComparator(boolean asc) {
        this.asc = asc;
    }

    /**
     * XComparator fuer die Sortiegung mittels Reflection
     *
     * @param methodPath dient der Navigation in eine Methode
     * @param asc        true = ascending, false = descending
     */
    public XComparator(String methodPath, boolean asc) {
        this.methodPath = new String[]{methodPath};
        this.asc = asc;
    }

    /**
     * XComparator fuer die Sortierung
     *
     * @param methodPath dient der Navigation in Unterobjekte mittels
     *                   Reflection (beliebig tief verschachtelt)
     * @param asc        true = ascending, false = descending
     */
    public XComparator(String[] methodPath, boolean asc) {
        this.methodPath = methodPath;
        this.asc = asc;
    }

    public int compare(Object o1, Object o2) {

        try {
            Method o1_Method, o2_Method;

            for (String method : methodPath) {
                o1_Method = o1.getClass().getMethod(method, new Class[]{});
                o2_Method = o2.getClass().getMethod(method, new Class[]{});

                o1 = o1_Method.invoke(o1, new Object[]{});
                o2 = o2_Method.invoke(o2, new Object[]{});
            }

            // NULL Behandlung
            if (o1 == null) {
                return (asc ? -1 : 1);
            }

            if (o2 == null) {
                return (asc ? 1 : -1);
            }

        } catch (NoSuchMethodException ignored) {
        } catch (IllegalAccessException ignored) {
        } catch (InvocationTargetException ignored) {
        }

        Comparable c1 = (Comparable) o1;
        Comparable c2 = (Comparable) o2;

        return c1.compareTo(c2) * (asc ? 1 : -1);
    }
}
