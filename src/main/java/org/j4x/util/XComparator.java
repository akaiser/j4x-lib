package org.j4x.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * XComparator
 * 
 * @author akaiser
 * @version 2.0 (20.07.10)
 * 
 */
public class XComparator extends Object implements Comparator<Object> {

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
     * @param asc true = ascending, false = descending
     */
    public XComparator(String methodPath, boolean asc) {
        this.methodPath = new String[]{methodPath};
        this.asc = asc;
    }

    /**
     * XComparator fuer die Sortierung
     *
     * @param methodPath dient der Navigation in Unterobjekte mittels
     * Reflection (beliebig tief verschachtelt)
     * @param asc true = ascending, false = descending
     */
    public XComparator(String[] methodPath, boolean asc) {
        this.methodPath = methodPath;
        this.asc = asc;
    }

    @Override
    public int compare(Object o1, Object o2) {
        Object comp1 = o1;
        Object comp2 = o2;

        try {
            Method o1_Method = null;
            Method o2_Method = null;

            for (String method : methodPath) {
                o1_Method = comp1.getClass().getMethod(method, new Class[]{});
                o2_Method = comp2.getClass().getMethod(method, new Class[]{});

                comp1 = o1_Method.invoke(comp1, new Object[]{});
                comp2 = o2_Method.invoke(comp2, new Object[]{});
            }

            // NULL Behandlung
            if (comp1 == null) {
                return (asc ? -1 : 1);
            }

            if (comp2 == null) {
                return (asc ? 1 : -1);
            }

        } catch (NoSuchMethodException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }

        Comparable c1 = (Comparable) comp1;
        Comparable c2 = (Comparable) comp2;

        return c1.compareTo(c2) * (asc ? 1 : -1);
    }
}
