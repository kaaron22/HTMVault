package com.nashss.se.htmvault.models;

public class SortOrder {

    public static final String DEFAULT = "DESCENDING";
    public static final String ASCENDING = "ASCENDING";

    private SortOrder() {
    }

    /**
     * Returns an array of "sort order" values for comparison, functioning similar to an enum.
     *
     * @return the array of values
     */
    public static String[] values() {
        return new String[]{DEFAULT, ASCENDING};
    }
}
