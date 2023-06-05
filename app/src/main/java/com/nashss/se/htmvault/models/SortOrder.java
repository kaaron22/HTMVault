package com.nashss.se.htmvault.models;

public class SortOrder {

    public static final String DEFAULT = "DESCENDING";
    public static final String ASCENDING = "ASCENDING";

    private SortOrder() {
    }

    public static String[] values() {
        return new String[]{DEFAULT, ASCENDING};
    }
}
