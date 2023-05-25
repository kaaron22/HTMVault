package com.nashss.se.htmvault.utils;

import com.nashss.se.htmvault.exceptions.InvalidAttributeException;

import java.util.Map;

public class HTMVaultServiceUtils {

    private HTMVaultServiceUtils() { }

    public static <T> void ifEmptyOrBlank(Map<String, String> requiredRequestParameterValues) {
        for (Map.Entry<String, String> entry : requiredRequestParameterValues.entrySet()) {
            if (entry.getValue().isEmpty() || entry.getValue().isBlank()) {
                throw new InvalidAttributeException(String.format("The %s must not be empty or blank", entry.getKey()));
            }
        }
    }
}
