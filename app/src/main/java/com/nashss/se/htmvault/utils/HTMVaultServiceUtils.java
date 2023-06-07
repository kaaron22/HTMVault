package com.nashss.se.htmvault.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.Random;

public class HTMVaultServiceUtils {

    public static final String ALPHA_NUMERIC_SPACE_OR_DASH = "^[A-Za-z0-9- ]+$";
    public static final String CONTROL_NUMBER_PREFIX = "";
    public static final int CONTROL_NUMBER_LENGTH = 9;
    public static final String WORK_ORDER_PREFIX = "WR";
    public static final int WORK_ORDER_ID_LENGTH = 8;

    private HTMVaultServiceUtils() { }

    /**
     * Checks a string value to see if it is null, empty, or blank, or if it does not contain only characters that are
     * acceptable (i.e. only alphanumeric characters, or only alphanumeric characters, dashes, and spaces).
     * @param stringToCheck the value of the attribute that was provided
     * @param validCharacters a list of allowed Characters against which to check each character of the string, in
     *                        regular expression form
     * @return boolean true if string is valid, false otherwise
     */
    public static boolean isValidString(final String stringToCheck, final String validCharacters) {

        // if stringToCheck is null, empty, or blank, then it's invalid
        if (StringUtils.isBlank(stringToCheck)) {
            return false;
        // otherwise, verify the string only contains valid characters
        } else {
            return stringToCheck.matches(validCharacters);
        }
    }

    /**
     * Formats a LocalDateTime object to a String containing a LocalDate and LocalTime,
     * separated by a space
     * @param localDateTime the LocalDateTime to format as a String
     * @return the formatted String containing the date and time or an empty string if the localDateTime is null
     */
    public static String formatLocalDateTime(LocalDateTime localDateTime) {
        return null == localDateTime ? "" : localDateTime.toLocalDate().toString() + " " +
                localDateTime.toLocalTime().toString();
    }

    public static String generateId(String prefix, int length) {
        return prefix + RandomStringUtils.randomNumeric(length);
    }

    public static int generateRandomIntWithLimit(int exclusiveLimit) {
        Random random = new Random();
        return random.nextInt(exclusiveLimit);
    }
}
