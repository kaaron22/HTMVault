package com.nashss.se.htmvault.utils;

import com.nashss.se.htmvault.exceptions.InvalidAttributeException;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

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

    /**
     * Checks a string value to see if it contains only characters that meet at least one of the desired conditions
     * (i.e. only alphanumeric characters, or only alphanumeric characters and dashes). If not, an
     * InvalidAttributeException is thrown, specifying the attribute and the value provided.
     * @param attributeName the name of the attribute being checked
     * @param stringToCheck the value of the attribute that was provided
     * @param conditions a list of Character conditions against which to check each character of the string
     */
    public static void ifNotValidString(String attributeName, String stringToCheck,
                                        List<Predicate<Character>> conditions) {

        // check each character of the string being
        for (int i = 0; i < stringToCheck.length(); i++) {
            // this is an invalid character unless proven otherwise
            boolean validCharacter = false;
            // check this character against the list of predicate conditions provided
            for (Predicate<Character> condition : conditions) {
                if (condition.test(stringToCheck.charAt((i)))) {
                    // if this character meets one of the conditions, it's a valid character
                    // and we can stop checking it
                    validCharacter = true;
                    break;
                }
            }

            // if the character was not proven to be valid, we throw an exception
            if (!validCharacter) {
                throw new InvalidAttributeException(String.format("The %s provided (%s) contained invalid characters",
                        attributeName, stringToCheck));
            }
        }
    }

    public static void allowedMaintenanceFrequencyRange(int min, int max, int actual) {
        if (actual < min || actual > max) {
            throw new InvalidAttributeException(String.format("The maintenance frequency provided (%s) is outside of " +
                    "the acceptable range of %s-%s", actual, min, max));
        }
    }
}
