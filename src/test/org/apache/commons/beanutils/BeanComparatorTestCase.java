/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/test/org/apache/commons/beanutils/Attic/BeanComparatorTestCase.java,v 1.1 2002/10/18 17:41:48 rdonkin Exp $
 * $Revision: 1.1 $
 * $Date: 2002/10/18 17:41:48 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
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
 * @version $Revision: 1.1 $
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
          int result = beanComparator.compare(alphaBean2, null);

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
          int result = beanComparator.compare(alphaBean2, alphaBean1);
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
          int result = beanComparator.compare(testBeanA, testBeanB);

          fail("BeanComparator should throw an exception when comparing two booleans.");

        }
        catch (ClassCastException cce){
          assertTrue("Two booleans should not be comparable",cce.toString().indexOf("Both arguments of this method were not Comparables: java.lang.Boolean and java.lang.Boolean") >=0);
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


