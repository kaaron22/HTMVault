package com.nashss.se.htmvault.utils;

import com.nashss.se.htmvault.exceptions.InvalidAttributeException;

import java.util.Map;

public class HTMVaultServiceUtils {

    private HTMVaultServiceUtils() { }

    /**
     * Checks a map of attribute name & value pairs associated with a request that are required to be provided. If any
     * of the attributes required were empty or blank, throws an InvalidAttributeException with a message specifying
     * the attribute that was empty or blank.
     * @param requiredRequestParameterValues the map of attribute name & value pairs
     *
     */
    public static void ifEmptyOrBlank(Map<String, String> requiredRequestParameterValues) {
        for (Map.Entry<String, String> entry : requiredRequestParameterValues.entrySet()) {
            if (entry.getValue().isEmpty() || entry.getValue().isBlank()) {
                throw new InvalidAttributeException(String.format("The %s must not be empty or blank", entry.getKey()));
            }
        }
    }
}
