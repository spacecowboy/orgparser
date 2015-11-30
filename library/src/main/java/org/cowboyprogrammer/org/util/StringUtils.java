package org.cowboyprogrammer.org.util;


public class StringUtils {

    /**
     * If null, returns null.
     *
     * @param text like "text"
     * @return reversed text like "txet"
     */
    public static String reverse(String text) {
        if (text == null) {
            return null;
        }
        return new StringBuilder(text).reverse().toString();
    }
}
