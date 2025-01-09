/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.beanutils2;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test Case for the BeanComparator class.
 */
public class BeanComparatorTest {

    /**
     * The test beans for each test.
     */
    protected TestBean bean;
    protected AlphaBean alphaBean1;

    protected AlphaBean alphaBean2;

    /**
     * Sets up instance variables required by this test case.
     */
    @BeforeEach
    public void setUp() {
        bean = new TestBean();
        alphaBean1 = new AlphaBean("alphaBean1");
        alphaBean2 = new AlphaBean("alphaBean2");
    }

    /**
     * Tears down instance variables required by this test case.
     */
    @AfterEach
    public void tearDown() {
        bean = null;
        alphaBean1 = null;
        alphaBean2 = null;
    }

    /**
     * Tests comparing one bean against itself.
     */
    @Test
    public void testCompareBeanAgainstSelf() {
        final BeanComparator<AlphaBean, String> beanComparator = new BeanComparator<>("name");
        final int result = beanComparator.compare(alphaBean1, alphaBean1);
        assertEquals(0, result, () -> "Comparator did not sort properly.  Result:" + result);
    }

    /**
     * Tests comparing two beans via their name using the default Comparator where they have the same value.
     */
    @Test
    public void testCompareIdentical() {
        alphaBean1 = new AlphaBean("alphabean");
        alphaBean2 = new AlphaBean("alphabean");
        final BeanComparator<AlphaBean, String> beanComparator = new BeanComparator<>("name");
        final int result = beanComparator.compare(alphaBean1, alphaBean2);
        assertEquals(0, result, () -> "Comparator did not sort properly.  Result:" + result);
    }

    /**
     * Tests comparing two beans on a boolean property, which is not possible.
     */
    @Test
    public void testCompareOnBooleanProperty() {
        try {
            final TestBean testBeanA = new TestBean();
            final TestBean testBeanB = new TestBean();

            testBeanA.setBooleanProperty(true);
            testBeanB.setBooleanProperty(false);

            final BeanComparator<TestBean, String> beanComparator = new BeanComparator<>("booleanProperty");
            beanComparator.compare(testBeanA, testBeanB);

            // **** java.lang.Boolean implements Comparable from JDK 1.5 onwards
            // so this test no longer fails
            // fail("BeanComparator should throw an exception when comparing two booleans.");

        } catch (final ClassCastException cce) {
            // Expected result
        }
    }

    /**
     * Tests comparing two beans who don't have a property
     */
    @Test
    public void testCompareOnMissingProperty() {
        final BeanComparator<AlphaBean, String> beanComparator = new BeanComparator<>("bogusName");
        final Exception e = assertThrows(RuntimeException.class, () -> beanComparator.compare(alphaBean2, alphaBean1));
        assertTrue(e.toString().contains("Unknown property"), () -> "Wrong exception was thrown: " + e);
    }

    /**
     * Tests comparing two beans via their name using the default Comparator, but with one of the beans being null.
     */
    @Test
    public void testCompareWithNulls() {
        final BeanComparator<AlphaBean, String> beanComparator = new BeanComparator<>("name");
        assertThrows(NullPointerException.class, () -> beanComparator.compare(alphaBean2, null));
    }

    /**
     * Tests comparing two beans on a boolean property, then changing the property and testing/
     */
    @Test
    public void testSetProperty() {
        final TestBean testBeanA = new TestBean();
        final TestBean testBeanB = new TestBean();

        testBeanA.setDoubleProperty(5.5);
        testBeanB.setDoubleProperty(1.0);

        final BeanComparator<TestBean, String> beanComparator = new BeanComparator<>("doubleProperty");
        final int result1 = beanComparator.compare(testBeanA, testBeanB);

        assertEquals(1, result1, () -> "Comparator did not sort properly.  Result:" + result1);

        testBeanA.setStringProperty("string 1");
        testBeanB.setStringProperty("string 2");

        beanComparator.setProperty("stringProperty");

        final int result2 = beanComparator.compare(testBeanA, testBeanB);

        assertEquals(-1, result2, () -> "Comparator did not sort properly.  Result:" + result2);
    }

    /**
     * Tests comparing two beans via their name using the default Comparator
     */
    @Test
    public void testSimpleCompare() {
        final BeanComparator<AlphaBean, String> beanComparator = new BeanComparator<>("name");
        final int result = beanComparator.compare(alphaBean1, alphaBean2);
        assertEquals(-1, result, () -> "Comparator did not sort properly.  Result:" + result);
    }

    /**
     * Tests comparing two beans via their name using the default Comparator, but the inverse
     */
    @Test
    public void testSimpleCompareInverse() {
        final BeanComparator<AlphaBean, String> beanComparator = new BeanComparator<>("name");
        final int result = beanComparator.compare(alphaBean2, alphaBean1);
        assertEquals(1, result, () -> "Comparator did not sort properly.  Result:" + result);
    }

    /**
     * Tests comparing two beans via their name using the default natural order Comparator
     */
    @Test
    public void testSimpleCompareWithDefaultNaturalComparator() {
        final BeanComparator<AlphaBean, String> beanComparator = new BeanComparator<>("name", null);
        final int result = beanComparator.compare(alphaBean1, alphaBean2);
        assertEquals(-1, result, () -> "Comparator did not sort properly.  Result:" + result);
    }

    /**
     * Tests comparing two beans via their name using the default natural order Comparator, but the inverse
     */
    @Test
    public void testSimpleCompareInverseWithDefaultNaturalComparator() {
        final BeanComparator<AlphaBean, String> beanComparator = new BeanComparator<>("name", null);
        final int result = beanComparator.compare(alphaBean2, alphaBean1);
        assertEquals(1, result, () -> "Comparator did not sort properly.  Result:" + result);
    }

    /**
     * Tests comparing two comparable beans using the default natural order Comparator
     */
    @Test
    public void testNaturalCompare() {
        final BeanComparator<String, ?> beanComparator = new BeanComparator<>();
        final int result = beanComparator.compare("string1", "string2");
        assertEquals(-1, result, () -> "Comparator did not sort properly.  Result:" + result);
    }

    /**
     * Tests comparing two comparable beans using the default natural order Comparator, but the inverse
     */
    @Test
    public void testNaturalCompareInverse() {
        final BeanComparator<String, ?> beanComparator = new BeanComparator<>();
        final int result = beanComparator.compare("string2", "string1");
        assertEquals(1, result, () -> "Comparator did not sort properly.  Result:" + result);
    }

    /**
     * Tests comparing two beans via their name using the default Comparator
     */
    @Test
    public void testWithCustomComparator() {
        final Comparator<AlphaBean> comparator = Comparator.comparing(AlphaBean::getName);

        final BeanComparator<AlphaBean, ?> beanComparator = new BeanComparator<>(null, comparator);
        final int result = beanComparator.compare(alphaBean1, alphaBean2);
        assertEquals(-1, result, () -> "Comparator did not sort properly.  Result:" + result);
    }

    /**
     * Tests comparing two beans via their name using the default Comparator
     */
    @Test
    public void testWithCustomComparatorInverse() {
        final Comparator<AlphaBean> comparator = Comparator.comparing(AlphaBean::getName);

        final BeanComparator<AlphaBean, ?> beanComparator = new BeanComparator<>(null, comparator);
        final int result = beanComparator.compare(alphaBean2, alphaBean1);
        assertEquals(1, result, () -> "Comparator did not sort properly.  Result:" + result);
    }

    @Test
    public void testEquals() {
        final Comparator<AlphaBean> comparator = Comparator.comparing(AlphaBean::getName);
        final BeanComparator<AlphaBean, String> nameComparator = new BeanComparator<>("name");
        final BeanComparator<AlphaBean, String> nameComparator2 = new BeanComparator<>("name");
        final BeanComparator<AlphaBean, String> nullPropertyComparator1 = new BeanComparator<>(null);
        final BeanComparator<AlphaBean, String> nullPropertyComparator2 = new BeanComparator<>(null);
        final BeanComparator<AlphaBean, String> nameComparatorDifferentComparator = new BeanComparator<>("name",
                                                                                             String::compareTo);
        final BeanComparator<AlphaBean, Boolean> booleanComparator = new BeanComparator<>("booleanProperty");
        assertAll(
                () -> assertEquals(nameComparator, nameComparator,
                                   "an instance should be equal to itself"),
                () -> assertNotEquals(nameComparator, comparator,
                                      "an instance should not be equal to a non-BeanComparator"),
                () -> assertNotEquals(nameComparator, nameComparatorDifferentComparator,
                                   "an instance should not be equal to a BeanComparator using different comparator"),
                () -> assertNotEquals(nameComparator, booleanComparator,
                                      "an instance should not be equal to a BeanComparator using different property"),
                () -> assertEquals(nameComparator, nameComparator2,
                                   "an instance should be equal to a BeanComparator with same comparator and property"),
                () -> assertNotEquals(nullPropertyComparator1, booleanComparator,
                                      "an instance with null property comparator should not be equal to a BeanComparator using a property"),
                () -> assertEquals(nullPropertyComparator1, nullPropertyComparator2,
                                   "an instance with null property comparator should be equal to a BeanComparator with same comparator and a null property")
        );
    }
}
