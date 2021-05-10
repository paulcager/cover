package org.paulcager.cover;

import org.junit.Test;
import org.paulcager.cover.tests.TestClass;

public class CoverageTest {

    @Test
    public void testGetGetters() {
        new Coverage(new TestClass()).cover();
    }
}