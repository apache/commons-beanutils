/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/test/org/apache/commons/beanutils/BeanUtilsTestCase.java,v 1.6 2002/03/11 04:49:53 craigmcc Exp $
 * $Revision: 1.6 $
 * $Date: 2002/03/11 04:49:53 $
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
import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * <p>
 *  Test Case for the BeanUtils class.  The majority of these tests use
 *  instances of the TestBean class, so be sure to update the tests if you
 *  change the characteristics of that class.
 * </p>
 *
 * <p>
 *  Template for this stolen from Craigs PropertyUtilsTestCase
 * </p>
 *
 * <p>
 *   Note that the tests are dependant upon the static aspects
 *   (such as array sizes...) of the TestBean.java class, so ensure
 *   than all changes to TestBean are reflected here.
 * </p>
 *
 * <p>
 *  So far, this test case has tests for the following methods of the
 *  <code>BeanUtils</code> class:
 * </p>
 * <ul>
 *   <li>getArrayProperty(Object bean, String name)</li>
 * </ul>
 *
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @version $Revision: 1.6 $
 */

public class BeanUtilsTestCase extends TestCase {

    // ---------------------------------------------------- Instance Variables

    /**
     * The test bean for each test.
     */
    protected TestBean bean = null;


    // ---------------------------------------------------------- Constructors

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public BeanUtilsTestCase(String name) {
        super(name);
    }


    // -------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() {
        bean = new TestBean();
    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {
        return (new TestSuite(BeanUtilsTestCase.class));
    }

    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {
        bean = null;
    }


    // ------------------------------------------------ Individual Test Methods


    /**
     *  tests the string and int arrays of TestBean
     */
    public void testGetArrayProperty() {
        try {
            String arr[] = BeanUtils.getArrayProperty(bean, "stringArray");
            String comp[] = bean.getStringArray();

            assertTrue("String array length = " + comp.length,
                    (comp.length == arr.length));

            arr = BeanUtils.getArrayProperty(bean, "intArray");
            int iarr[] = bean.getIntArray();

            assertTrue("String array length = " + iarr.length,
                    (iarr.length == arr.length));
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     *  tests getting an indexed property
     */
    public void testGetIndexedProperty1() {
        try {
            String val = BeanUtils.getIndexedProperty(bean, "intIndexed[3]");
            String comp = String.valueOf(bean.getIntIndexed(3));
            assertTrue("intIndexed[3] == " + comp, val.equals(comp));

            val = BeanUtils.getIndexedProperty(bean, "stringIndexed[3]");
            comp = bean.getStringIndexed(3);
            assertTrue("stringIndexed[3] == " + comp, val.equals(comp));
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }
    }

    /**
     *  tests getting an indexed property
     */
    public void testGetIndexedProperty2() {
        try {
            String val = BeanUtils.getIndexedProperty(bean, "intIndexed", 3);
            String comp = String.valueOf(bean.getIntIndexed(3));

            assertTrue("intIndexed,3 == " + comp, val.equals(comp));

            val = BeanUtils.getIndexedProperty(bean, "stringIndexed", 3);
            comp = bean.getStringIndexed(3);

            assertTrue("stringIndexed,3 == " + comp, val.equals(comp));

        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }
    }

    /**
     *  tests getting a nested property
     */
    public void testGetNestedProperty() {
        try {
            String val = BeanUtils.getNestedProperty(bean, "nested.stringProperty");
            String comp = bean.getNested().getStringProperty();
            assertTrue("nested.StringProperty == " + comp,
                    val.equals(comp));
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }
    }

    /**
     *  tests getting a 'whatever' property
     */
    public void testGetGeneralProperty() {
        try {
            String val = BeanUtils.getProperty(bean, "nested.intIndexed[2]");
            String comp = String.valueOf(bean.getIntIndexed(2));

            assertTrue("nested.intIndexed[2] == " + comp,
                    val.equals(comp));
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }
    }

    /**
     *  tests getting a 'whatever' property
     */
    public void testGetSimpleProperty() {
        try {
            String val = BeanUtils.getSimpleProperty(bean, "shortProperty");
            String comp = String.valueOf(bean.getShortProperty());

            assertTrue("shortProperty == " + comp,
                    val.equals(comp));
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }
    }

    /**
     * Test populate() method on individual array elements.
     */
    public void testPopulateArrayElements() {

        try {

            HashMap map = new HashMap();
            map.put("intIndexed[0]", "100");
            map.put("intIndexed[2]", "120");
            map.put("intIndexed[4]", "140");

            BeanUtils.populate(bean, map);

            assertEquals("intIndexed[0] is 100",
                         100, bean.getIntIndexed(0));
            assertEquals("intIndexed[1] is 10",
                         10, bean.getIntIndexed(1));
            assertEquals("intIndexed[2] is 120",
                         120, bean.getIntIndexed(2));
            assertEquals("intIndexed[3] is 30",
                         30, bean.getIntIndexed(3));
            assertEquals("intIndexed[4] is 140",
                         140, bean.getIntIndexed(4));

            map.clear();
            map.put("stringIndexed[1]", "New String 1");
            map.put("stringIndexed[3]", "New String 3");

            BeanUtils.populate(bean, map);

            assertEquals("stringIndexed[0] is \"String 0\"",
                         "String 0", bean.getStringIndexed(0));
            assertEquals("stringIndexed[1] is \"New String 1\"",
                         "New String 1", bean.getStringIndexed(1));
            assertEquals("stringIndexed[2] is \"String 2\"",
                         "String 2", bean.getStringIndexed(2));
            assertEquals("stringIndexed[3] is \"New String 3\"",
                         "New String 3", bean.getStringIndexed(3));
            assertEquals("stringIndexed[4] is \"String 4\"",
                         "String 4", bean.getStringIndexed(4));

        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        }

    }

    /**
     * Test populate() method on array properties as a whole.
     */
    public void testPopulateArrayProperties() {

        try {

            HashMap map = new HashMap();
            int intArray[] = new int[] { 123, 456, 789 };
            map.put("intArray", intArray);
            String stringArray[] = new String[]
                { "New String 0", "New String 1" };
            map.put("stringArray", stringArray);

            BeanUtils.populate(bean, map);

            intArray = bean.getIntArray();
            assertNotNull("intArray is present", intArray);
            assertEquals("intArray length",
                         3, intArray.length);
            assertEquals("intArray[0]", 123, intArray[0]);
            assertEquals("intArray[1]", 456, intArray[1]);
            assertEquals("intArray[2]", 789, intArray[2]);
            stringArray = bean.getStringArray();
            assertNotNull("stringArray is present", stringArray);
            assertEquals("stringArray length", 2, stringArray.length);
            assertEquals("stringArray[0]", "New String 0", stringArray[0]);
            assertEquals("stringArray[1]", "New String 1", stringArray[1]);

        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        }

    }

    /**
     * Test populate() method on scalar properties.
     */
    public void testPopulateScalar() {

        try {

            HashMap map = new HashMap();
            map.put("booleanProperty", "false");
            // booleanSecond is left at true
            map.put("doubleProperty", "432.0");
            // floatProperty is left at 123.0
            map.put("intProperty", "543");
            // longProperty is left at 321
            map.put("shortProperty", "654");
            // stringProperty is left at "This is a string"
            map.put("writeOnlyProperty", "New writeOnlyProperty value");

            BeanUtils.populate(bean, map);

            assertTrue("booleanProperty is false", !bean.getBooleanProperty());
            assertTrue("booleanSecond is true", bean.isBooleanSecond());
            assertEquals("doubleProperty is 432.0",
                         (double) 432.0, bean.getDoubleProperty(),
                         (double) 0.005);
            assertEquals("floatProperty is 123.0",
                         (float) 123.0, bean.getFloatProperty(),
                         (float) 0.005);
            assertEquals("intProperty is 543",
                         543, bean.getIntProperty());
            assertEquals("longProperty is 321",
                         (long) 321, bean.getLongProperty());
            assertEquals("shortProperty is 654",
                         (short) 654, bean.getShortProperty());
            assertEquals("stringProperty is \"This is a string\"",
                         "This is a string", bean.getStringProperty());
            assertEquals("writeOnlyProperty is \"New writeOnlyProperty value\"",
                         "New writeOnlyProperty value",
                         bean.getWriteOnlyPropertyValue());

        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        }

    }


}

