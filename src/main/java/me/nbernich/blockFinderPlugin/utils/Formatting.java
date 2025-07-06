package me.nbernich.blockFinderPlugin.utils;

/**
 * Utility class for formatting strings (such as Material names).
 */
public class Formatting {

    /**
     * Convert a string to Title Case. Input may contain underscores, dashes, or spaces.
     * @param s The input string to convert.
     * @return The string in Title Case.
     */
    public static String toTitleCase(String s) {
        StringBuilder res = new StringBuilder();
        boolean prevSpace = true;
        for (char c : s.toCharArray()) {
            if (c == '_' || c == '-' || c == ' ') {
                res.append(" ");
                prevSpace = true;
            } else if (prevSpace) {
                res.append(Character.toUpperCase(c));
                prevSpace = false;
            } else {
                res.append(Character.toLowerCase(c));
            }
        }
        return res.toString();
    }

}