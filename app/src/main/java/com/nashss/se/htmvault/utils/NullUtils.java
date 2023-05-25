package com.nashss.se.htmvault.utils;

import com.nashss.se.htmvault.exceptions.InvalidAttributeException;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Various utilities to deal with null.
 */
public class NullUtils {
    private NullUtils() { }

    /**
     * Checks a map of attribute name & value pairs associated with a request that are required to be provided. If any
     * of the attributes required were not provided (i.e. the value is null), throws an InvalidAttributeException, with
     * a message specifying the attribute that was not provided.
     * @param requiredRequestParameterValues the map of attribute name & value pairs
     *
     */
    public static void ifNull(Map<String, String> requiredRequestParameterValues) {
        for (Map.Entry<String, String> entry : requiredRequestParameterValues.entrySet()) {
            if (null == entry.getValue()) {
                throw new InvalidAttributeException(String.format("The %s must be provided", entry.getKey()));
            }
        }
    }

    /**
     * If obj is null, return valIfNull, otherwise return obj.
     * @param obj The object to check for null.
     * @param valIfNull The value to return if obj is null.
     * @param <T> The type of obj and valIfNull.
     * @return obj or valIfNull.
     */
    public static <T> T ifNull(T obj, T valIfNull) {
        return obj != null ? obj : valIfNull;
    }

    /**
     * If obj is null, return value supplied by valIfNullSupplier.
     * @param obj The object to check for null.
     * @param valIfNullSupplier The supplier of the value to return if obj is null.
     * @param <T> The type of obj and the supplier.
     * @return obj or value returned by valIfNullSupplier.
     */
    public static <T> T ifNull(T obj, Supplier<T> valIfNullSupplier) {
        return obj != null ? obj : valIfNullSupplier.get();
    }

    /**
     * If obj is null, return null, otherwise return valIfNotNull.
     * @param obj The object to check for null.
     * @param valIfNotNull The value to return if obj is not null.
     * @param <T> The type of obj and valIfNotNull.
     * @return null or valIfNotNull.
     */
    public static <T> T ifNotNull(T obj, T valIfNotNull) {
        return obj == null ? null : valIfNotNull;
    }

    /**
     * If obj is null, return value supplied by valIfNotNullSupplier.
     * @param obj The object to check for null.
     * @param valIfNotNullSupplier The supplier of the value to return if obj is not null.
     * @param <T> The type of obj.
     * @param <U> The type of returned by the supplier.
     * @return null or value returned by valIfNotNullSupplier.
     */
    public static <T, U> U ifNotNull(T obj, Supplier<U> valIfNotNullSupplier) {
        return obj == null ? null : valIfNotNullSupplier.get();
    }
}
