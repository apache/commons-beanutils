/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.beanutils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * Test Case for the BeanComparator class.
 *
 */
public class BeanComparatorTestCase {

    /**
     * The test beans for each test.
     */
    protected TestBean bean;
    protected AlphaBean alphaBean1;
    protected AlphaBean alphaBean2;

    /**
     * Set up instance variables required by this test case.
     */
    @BeforeEach
    public void setUp() {
        bean = new TestBean();
        alphaBean1 = new AlphaBean("alphaBean1");
        alphaBean2 = new AlphaBean("alphaBean2");
    }

    /**
     * Tear down instance variables required by this test case.
     */
    @AfterEach
    public void tearDown() {
        bean = null;
        alphaBean1 = null;
        alphaBean2 = null;
    }

    /**
     * tests comparing one bean against itself.
     */
    @Test
    public void testCompareBeanAgainstSelf() {
        final BeanComparator<AlphaBean> beanComparator = new BeanComparator<>("name");
        final int result = beanComparator.compare(alphaBean1, alphaBean1);
        assertTrue(result == 0, "Comparator did not sort properly.  Result:" + result);
    }

    /**
     * tests comparing two beans via their name using the default Comparator where they have the same value.
     */
    @Test
    public void testCompareIdentical() {
        alphaBean1 = new AlphaBean("alphabean");
        alphaBean2 = new AlphaBean("alphabean");
        final BeanComparator<AlphaBean> beanComparator = new BeanComparator<>("name");
        final int result = beanComparator.compare(alphaBean1, alphaBean2);
        assertTrue(result == 0, "Comparator did not sort properly.  Result:" + result);
    }

    /**
     * tests comparing two beans on a boolean property, which is not possible.
     */
    @Test
    public void testCompareOnBooleanProperty() {
        try {
            final TestBean testBeanA = new TestBean();
            final TestBean testBeanB = new TestBean();
            testBeanA.setBooleanProperty(true);
            testBeanB.setBooleanProperty(false);
            final BeanComparator<TestBean> beanComparator = new BeanComparator<>("booleanProperty");
            beanComparator.compare(testBeanA, testBeanB);
            // **** java.lang.Boolean implements Comparable from JDK 1.5 onwards
            // so this test no longer fails
            // fail("BeanComparator should throw an exception when comparing two booleans.");
        } catch (final ClassCastException cce) {
            // Expected result
        }
    }

    /**
     * tests comparing two beans who don't have a property
     */
    @Test
    public void testCompareOnMissingProperty() {
        try {
            final BeanComparator<AlphaBean> beanComparator = new BeanComparator<>("bogusName");
            beanComparator.compare(alphaBean2, alphaBean1);
            fail("should not be able to compare");
        } catch (final Exception e) {
            assertTrue(e.toString().indexOf("Unknown property") > -1, "Wrong exception was thrown: " + e);
        }
    }

    /**
     * tests comparing two beans via their name using the default Comparator, but with one of the beans being null.
     */
    @Test
    public void testCompareWithNulls() {
        try {
            final BeanComparator<AlphaBean> beanComparator = new BeanComparator<>("name");
            beanComparator.compare(alphaBean2, null);
            fail("Should not be able to compare a null value.");
        } catch (final Exception e) {
            // expected result
        }
    }

    /**
     * tests comparing two beans on a boolean property, then changing the property and testing
     */
    @Test
    public void testSetProperty() {
        final TestBean testBeanA = new TestBean();
        final TestBean testBeanB = new TestBean();
        testBeanA.setDoubleProperty(5.5);
        testBeanB.setDoubleProperty(1.0);
        final BeanComparator<TestBean> beanComparator = new BeanComparator<>("doubleProperty");
        int result = beanComparator.compare(testBeanA, testBeanB);
        assertTrue(result == 1, "Comparator did not sort properly.  Result:" + result);
        testBeanA.setStringProperty("string 1");
        testBeanB.setStringProperty("string 2");
        beanComparator.setProperty("stringProperty");
        result = beanComparator.compare(testBeanA, testBeanB);
        assertTrue(result == -1, "Comparator did not sort properly.  Result:" + result);
    }

    /**
     * tests comparing two beans via their name using the default Comparator
     */
    @Test
    public void testSimpleCompare() {
        final BeanComparator<AlphaBean> beanComparator = new BeanComparator<>("name");
        final int result = beanComparator.compare(alphaBean1, alphaBean2);
        assertTrue(result == -1, "Comparator did not sort properly.  Result:" + result);
    }

    /**
     * tests comparing two beans via their name using the default Comparator, but the inverse
     */
    @Test
    public void testSimpleCompareInverse() {
        final BeanComparator<AlphaBean> beanComparator = new BeanComparator<>("name");
        final int result = beanComparator.compare(alphaBean2, alphaBean1);
        assertTrue(result == 1, "Comparator did not sort properly.  Result:" + result);
    }
}
