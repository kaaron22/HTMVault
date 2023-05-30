package com.nashss.se.htmvault.utils;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CollectionUtilsTest {

    @Test
    public void copyToList_listStrings_returnsCopyOfList() {
        // GIVEN
        List<String> originalList = new ArrayList<>(Arrays.asList("a", "b", "123"));

        // WHEN
        List<String> copiedList = CollectionUtils.copyToList(originalList);

        // THEN
        assertEquals(originalList, copiedList);
    }

    @Test
    public void copyToList_nullList_returnsNullList() {
        // GIVEN
        List<String> originalList = null;

        // WHEN
        List<String> copiedList = CollectionUtils.copyToList(originalList);

        // THEN
        assertNull(copiedList);
    }
}