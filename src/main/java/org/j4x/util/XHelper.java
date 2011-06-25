package org.j4x.util;

/**
 * Helfer-Klasse fuer DWRQuery
 *
 * @author akaiser
 * @version 0.2 (20.07.10)
 */
public class XHelper {

    /**
     * Wandelt das Attribut in eine Getter-Paths um
     *
     * @param path to object
     * @return getter-paths
     */
    public static String[] getMethodPath(String path) {


        String[] methodPath = path.split("\\.");

        // Anpassungen fuer den Comparator und Filter durchfuehren
        for (int i = 0; i < methodPath.length; i++) {
            methodPath[i] = "get"
                    + methodPath[i].substring(0, 1).toUpperCase()
                    + methodPath[i].substring(1);
        }

        return methodPath;
    }
}
