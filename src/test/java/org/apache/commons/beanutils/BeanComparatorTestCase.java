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

package org.apache.commons.beanutils;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * <p>
 *  Test Case for the BeanComparator class.
 *
 * @version $Id$
 */

public class BeanComparatorTestCase extends TestCase {

    // ---------------------------------------------------- Instance Variables

    /**
     * The test beans for each test.
     */
    protected TestBean bean = null;
    protected AlphaBean alphaBean1 = null;
    protected AlphaBean alphaBean2 = null;


    // ---------------------------------------------------------- Constructors

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public BeanComparatorTestCase(final String name) {
        super(name);
    }


    // -------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    @Override
    public void setUp() {
        bean = new TestBean();
        alphaBean1 = new AlphaBean("alphaBean1");
        alphaBean2 = new AlphaBean("alphaBean2");


    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {
        return (new TestSuite(BeanComparatorTestCase.class));
    }

    /**
     * Tear down instance variables required by this test case.
     */
    @Override
    public void tearDown() {
        bean = null;
        alphaBean1 = null;
        alphaBean2 = null;
    }


    // ------------------------------------------------ Individual Test Methods


    /**
     *  tests comparing two beans via their name using the default Comparator
     */
    public void testSimpleCompare() {
        final BeanComparator<AlphaBean> beanComparator = new BeanComparator<AlphaBean>(
                "name");
        final int result = beanComparator.compare(alphaBean1, alphaBean2);
        assertTrue("Comparator did not sort properly.  Result:" + result,
                result == -1);
    }

    /**
     *  tests comparing two beans via their name using the default Comparator, but the inverse
     */
    public void testSimpleCompareInverse() {
        final BeanComparator<AlphaBean> beanComparator = new BeanComparator<AlphaBean>(
                "name");
        final int result = beanComparator.compare(alphaBean2, alphaBean1);
        assertTrue("Comparator did not sort properly.  Result:" + result,
                result == 1);
    }

    /**
     *  tests comparing two beans via their name using the default Comparator where they have the same value.
     */
    public void testCompareIdentical() {
        alphaBean1 = new AlphaBean("alphabean");
        alphaBean2 = new AlphaBean("alphabean");
        final BeanComparator<AlphaBean> beanComparator = new BeanComparator<AlphaBean>(
                "name");
        final int result = beanComparator.compare(alphaBean1, alphaBean2);
        assertTrue("Comparator did not sort properly.  Result:" + result,
                result == 0);
    }

    /**
     *  tests comparing one bean against itself.
     */
    public void testCompareBeanAgainstSelf() {
        final BeanComparator<AlphaBean> beanComparator = new BeanComparator<AlphaBean>(
                "name");
        final int result = beanComparator.compare(alphaBean1, alphaBean1);
        assertTrue("Comparator did not sort properly.  Result:" + result,
                result == 0);
    }

    /**
     *  tests comparing two beans via their name using the default Comparator, but with one of the beans
     *  being null.
     */
    public void testCompareWithNulls() {
        try {
          final BeanComparator<AlphaBean> beanComparator = new BeanComparator<AlphaBean>("name");
          beanComparator.compare(alphaBean2, null);

          fail("Should not be able to compare a null value.");
        }
        catch (final Exception e) {
            // expected result
        }
    }

    /**
     *  tests comparing two beans who don't have a property
     */
    public void testCompareOnMissingProperty() {
        try {
          final BeanComparator<AlphaBean> beanComparator = new BeanComparator<AlphaBean>("bogusName");
          beanComparator.compare(alphaBean2, alphaBean1);
          fail("should not be able to compare");


        }
        catch (final Exception e) {
          assertTrue("Wrong exception was thrown: " + e, e.toString().indexOf("Unknown property") > -1);
        }
    }

    /**
     *  tests comparing two beans on a boolean property, which is not possible.
     */
    public void testCompareOnBooleanProperty() {
        try {
          final TestBean testBeanA = new TestBean();
          final TestBean testBeanB = new TestBean();

          testBeanA.setBooleanProperty(true);
          testBeanB.setBooleanProperty(false);

          final BeanComparator<TestBean> beanComparator = new BeanComparator<TestBean>("booleanProperty");
          beanComparator.compare(testBeanA, testBeanB);

          // **** java.lang.Boolean implements Comparable from JDK 1.5 onwards
          //      so this test no longer fails
          // fail("BeanComparator should throw an exception when comparing two booleans.");

        }
        catch (final ClassCastException cce){
          // Expected result
        }
    }

    /**
     *  tests comparing two beans on a boolean property, then changing the property and testing
     */
    public void testSetProperty() {
        final TestBean testBeanA = new TestBean();
        final TestBean testBeanB = new TestBean();

        testBeanA.setDoubleProperty(5.5);
        testBeanB.setDoubleProperty(1.0);

        final BeanComparator<TestBean> beanComparator = new BeanComparator<TestBean>(
                "doubleProperty");
        int result = beanComparator.compare(testBeanA, testBeanB);

        assertTrue("Comparator did not sort properly.  Result:" + result,
                result == 1);

        testBeanA.setStringProperty("string 1");
        testBeanB.setStringProperty("string 2");

        beanComparator.setProperty("stringProperty");

        result = beanComparator.compare(testBeanA, testBeanB);

        assertTrue("Comparator did not sort properly.  Result:" + result,
                result == -1);
    }
}
