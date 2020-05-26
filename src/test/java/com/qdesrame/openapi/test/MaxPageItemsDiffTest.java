package com.qdesrame.openapi.test;

import org.junit.jupiter.api.Test;

import static com.qdesrame.openapi.test.TestUtils.*;

public class MaxPageItemsDiffTest {

    private final String OPENAPI_DOC1 = "max_page_items_diff_1.yaml";
    private final String OPENAPI_DOC2 = "max_page_items_diff_2.yaml";
    private final String OPENAPI_DOC3 = "max_page_items_diff_3.yaml";

    @Test
    void testEqual() {
        assertOpenApiAreEquals(OPENAPI_DOC1, OPENAPI_DOC1);
    }

    @Test
    void testIncreasedMax() {
        assertOpenApiBackwardCompatible(OPENAPI_DOC1, OPENAPI_DOC2, true);
    }

    @Test
    void testDecreasedMax() {
        assertOpenApiBackwardIncompatible(OPENAPI_DOC2, OPENAPI_DOC1);
    }

    @Test
    void testRemovedMax() {
        assertOpenApiBackwardCompatible(OPENAPI_DOC2, OPENAPI_DOC3, true);
    }

    @Test
    void testAddedMax() {
        assertOpenApiBackwardIncompatible(OPENAPI_DOC3, OPENAPI_DOC2);
    }

}
