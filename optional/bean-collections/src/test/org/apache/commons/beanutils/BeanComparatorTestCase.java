/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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


import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * <p>
 *  Test Case for the BeanComparator class.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Revision$
 */

public class BeanComparatorTestCase extends TestCase {

    // ---------------------------------------------------- Instance Variables

    /**
     * The test beans for each test.
     */
    protected TestBean bean = null;
    protected AlphaBean alphaBean1 = null;
    protected AlphaBean alphaBean2 = null;

    // The test BeanComparator
    protected BeanComparator beanComparator = null;





    // ---------------------------------------------------------- Constructors

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public BeanComparatorTestCase(String name) {
        super(name);
    }


    // -------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
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
    public void tearDown() {
        bean = null;
        alphaBean1 = null;
        alphaBean2 = null;
        beanComparator = null;
    }


    // ------------------------------------------------ Individual Test Methods


    /**
     *  tests comparing two beans via their name using the default Comparator
     */
    public void testSimpleCompare() {
        try {
          beanComparator = new BeanComparator("name");
          int result = beanComparator.compare(alphaBean1, alphaBean2);
          assertTrue("Comparator did not sort properly.  Result:" + result,result==-1);

        }
        catch (Exception e) {
            fail("Exception");
        }
    }

    /**
     *  tests comparing two beans via their name using the default Comparator, but the inverse
     */
    public void testSimpleCompareInverse() {
        try {
          beanComparator = new BeanComparator("name");
          int result = beanComparator.compare(alphaBean2, alphaBean1);
          assertTrue("Comparator did not sort properly.  Result:" + result,result==1);

        }
        catch (Exception e) {
            fail("Exception" + e);
        }
    }

    /**
     *  tests comparing two beans via their name using the default Comparator where they have the same value.
     */
    public void testCompareIdentical() {
        try {
          alphaBean1 = new AlphaBean("alphabean");
          alphaBean2 = new AlphaBean("alphabean");
          beanComparator = new BeanComparator("name");
          int result = beanComparator.compare(alphaBean1, alphaBean2);
          assertTrue("Comparator did not sort properly.  Result:" + result,result==0);

        }
        catch (Exception e) {
            fail("Exception");
        }
    }

    /**
     *  tests comparing one bean against itself.
     */
    public void testCompareBeanAgainstSelf() {
        try {
          beanComparator = new BeanComparator("name");
          int result = beanComparator.compare(alphaBean1, alphaBean1);
          assertTrue("Comparator did not sort properly.  Result:" + result,result==0);

        }
        catch (Exception e) {
            fail("Exception");
        }
    }

    /**
     *  tests comparing two beans via their name using the default Comparator, but with one of the beans
     *  being null.
     */
    public void testCompareWithNulls() {
        try {
          beanComparator = new BeanComparator("name");
          beanComparator.compare(alphaBean2, null);

          // DEP not sure if this is the best way to test an exception?
          fail("Should not be able to compare a null value.");

        }
        catch (Exception e) {

        }
    }

    /**
     *  tests comparing two beans who don't have a property
     */
    public void testCompareOnMissingProperty() {
        try {
          beanComparator = new BeanComparator("bogusName");
          beanComparator.compare(alphaBean2, alphaBean1);
          fail("should not be able to compare");


        }
        catch (ClassCastException cce){
          assertTrue("Wrong exception was thrown.",cce.toString().indexOf("Unknown property") > -1);
        }
        catch (Exception e) {
            fail("Exception" + e);
        }
    }

    /**
     *  tests comparing two beans on a boolean property, which is not possible.
     */
    public void testCompareOnBooleanProperty() {
        try {
          TestBean testBeanA = new TestBean();
          TestBean testBeanB = new TestBean();

          testBeanA.setBooleanProperty(true);
          testBeanB.setBooleanProperty(false);

          beanComparator = new BeanComparator("booleanProperty");
          beanComparator.compare(testBeanA, testBeanB);

          fail("BeanComparator should throw an exception when comparing two booleans.");

        }
        catch (ClassCastException cce){
          ; // Expected result
        }
        catch (Exception e) {
            fail("Exception" + e);
        }
    }

    /**
     *  tests comparing two beans on a boolean property, then changing the property and testing
     */
    public void testSetProperty() {
        try {
          TestBean testBeanA = new TestBean();
          TestBean testBeanB = new TestBean();

          testBeanA.setDoubleProperty(5.5);
          testBeanB.setDoubleProperty(1.0);

          beanComparator = new BeanComparator("doubleProperty");
          int result = beanComparator.compare(testBeanA, testBeanB);

          assertTrue("Comparator did not sort properly.  Result:" + result,result==1);

          testBeanA.setStringProperty("string 1");
          testBeanB.setStringProperty("string 2");

          beanComparator.setProperty("stringProperty");

          result = beanComparator.compare(testBeanA, testBeanB);

          assertTrue("Comparator did not sort properly.  Result:" + result,result==-1);

        }
        catch (ClassCastException cce){
          fail("ClassCaseException " + cce.toString());
        }
        catch (Exception e) {
          fail("Exception" + e);
        }
    }
}


