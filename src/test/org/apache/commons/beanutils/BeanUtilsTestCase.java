/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/test/org/apache/commons/beanutils/BeanUtilsTestCase.java,v 1.15 2002/12/09 22:17:12 rwaldhoff Exp $
 * $Revision: 1.15 $
 * $Date: 2002/12/09 22:17:12 $
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

import junit.framework.Test;
import junit.framework.TestCase;
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
 * @version $Revision: 1.15 $
 */

public class BeanUtilsTestCase extends TestCase {

    // ---------------------------------------------------- Instance Variables

    /**
     * The test bean for each test.
     */
    protected TestBean bean = null;


    /**
     * The set of properties that should be described.
     */
    protected String describes[] =
    { "booleanProperty",
      "booleanSecond",
      "doubleProperty",
      "dupProperty",
      "floatProperty",
      "intArray",
      //      "intIndexed",
      "intProperty",
      "listIndexed",
      "longProperty",
      //      "mappedProperty",
      //      "mappedIntProperty",
      "nested",
      "nullProperty",
      "readOnlyProperty",
      "shortProperty",
      "stringArray",
      //      "stringIndexed",
      "stringProperty"
    };


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
     * Test the copyProperties() method from a DynaBean.
     */
    public void testCopyPropertiesDynaBean() {

        // Set up an origin bean with customized properties
        DynaClass dynaClass = DynaBeanUtilsTestCase.createDynaClass();
        DynaBean orig = null;
        try {
            orig = dynaClass.newInstance();
        } catch (Exception e) {
            fail("newInstance(): " + e);
        }
        orig.set("booleanProperty", Boolean.FALSE);
        orig.set("doubleProperty", new Double(333.33));
        orig.set("dupProperty",
                 new String[] { "New 0", "New 1", "New 2" });
        orig.set("intArray", new int[] { 100, 200, 300 });
        orig.set("intProperty", new Integer(333));
        orig.set("longProperty", new Long(3333));
        orig.set("shortProperty", new Short((short) 33));
        orig.set("stringArray", new String[] { "New 0", "New 1" });
        orig.set("stringProperty", "Custom string");

        // Copy the origin bean to our destination test bean
        try {
            BeanUtils.copyProperties(bean, orig);
        } catch (Exception e) {
            fail("Threw exception: " + e);
        }

        // Validate the results for scalar properties
        assertEquals("Copied boolean property",
                     false,
                     bean.getBooleanProperty());
        assertEquals("Copied double property",
                     333.33,
                     bean.getDoubleProperty(),
                     0.005);
        assertEquals("Copied int property",
                     333,
                     bean.getIntProperty());
        assertEquals("Copied long property",
                     (long) 3333,
                     bean.getLongProperty());
        assertEquals("Copied short property",
                     (short) 33,
                     bean.getShortProperty());
        assertEquals("Copied string property",
                     "Custom string",
                     bean.getStringProperty());

        // Validate the results for array properties
        String dupProperty[] = bean.getDupProperty();
        assertNotNull("dupProperty present", dupProperty);
        assertEquals("dupProperty length", 3, dupProperty.length);
        assertEquals("dupProperty[0]", "New 0", dupProperty[0]);
        assertEquals("dupProperty[1]", "New 1", dupProperty[1]);
        assertEquals("dupProperty[2]", "New 2", dupProperty[2]);
        int intArray[] = bean.getIntArray();
        assertNotNull("intArray present", intArray);
        assertEquals("intArray length", 3, intArray.length);
        assertEquals("intArray[0]", 100, intArray[0]);
        assertEquals("intArray[1]", 200, intArray[1]);
        assertEquals("intArray[2]", 300, intArray[2]);
        String stringArray[] = bean.getStringArray();
        assertNotNull("stringArray present", stringArray);
        assertEquals("stringArray length", 2, stringArray.length);
        assertEquals("stringArray[0]", "New 0", stringArray[0]);
        assertEquals("stringArray[1]", "New 1", stringArray[1]);

    }


    /**
     * Test copyProperties() when the origin is a a <code>Map</code>.
     */
    public void testCopyPropertiesMap() {

        Map map = new HashMap();
        map.put("booleanProperty", "false");
        map.put("doubleProperty", "333.0");
        map.put("dupProperty", new String[] { "New 0", "New 1", "New 2" });
        map.put("floatProperty", "222.0");
        map.put("intArray", new String[] { "0", "100", "200" });
        map.put("intProperty", "111");
        map.put("longProperty", "444");
        map.put("shortProperty", "555");
        map.put("stringProperty", "New String Property");

        try {
            BeanUtils.copyProperties(bean, map);
        } catch (Throwable t) {
            fail("Threw " + t.toString());
        }

        // Scalar properties
        assertEquals("booleanProperty", false,
                     bean.getBooleanProperty());
        assertEquals("doubleProperty", 333.0,
                     bean.getDoubleProperty(), 0.005);
        assertEquals("floatProperty", (float) 222.0,
                     bean.getFloatProperty(), (float) 0.005);
        assertEquals("intProperty", 111,
                     bean.getIntProperty());
        assertEquals("longProperty", (long) 444,
                     bean.getLongProperty());
        assertEquals("shortProperty", (short) 555,
                     bean.getShortProperty());
        assertEquals("stringProperty", "New String Property",
                     bean.getStringProperty());
                     
        // Indexed Properties
        String dupProperty[] = bean.getDupProperty();
        assertNotNull("dupProperty present", dupProperty);
        assertEquals("dupProperty length", 3, dupProperty.length);
        assertEquals("dupProperty[0]", "New 0", dupProperty[0]);
        assertEquals("dupProperty[1]", "New 1", dupProperty[1]);
        assertEquals("dupProperty[2]", "New 2", dupProperty[2]);
        int intArray[] = bean.getIntArray();
        assertNotNull("intArray present", intArray);
        assertEquals("intArray length", 3, intArray.length);
        assertEquals("intArray[0]", 0, intArray[0]);
        assertEquals("intArray[1]", 100, intArray[1]);
        assertEquals("intArray[2]", 200, intArray[2]);

    }


    /**
     * Test the copyProperties() method from a standard JavaBean.
     */
    public void testCopyPropertiesStandard() {

        // Set up an origin bean with customized properties
        TestBean orig = new TestBean();
        orig.setBooleanProperty(false);
        orig.setDoubleProperty(333.33);
        orig.setDupProperty(new String[] { "New 0", "New 1", "New 2" });
        orig.setIntArray(new int[] { 100, 200, 300 });
        orig.setIntProperty(333);
        orig.setLongProperty(3333);
        orig.setShortProperty((short) 33);
        orig.setStringArray(new String[] { "New 0", "New 1" });
        orig.setStringProperty("Custom string");

        // Copy the origin bean to our destination test bean
        try {
            BeanUtils.copyProperties(bean, orig);
        } catch (Exception e) {
            fail("Threw exception: " + e);
        }

        // Validate the results for scalar properties
        assertEquals("Copied boolean property",
                     false,
                     bean.getBooleanProperty());
        assertEquals("Copied double property",
                     333.33,
                     bean.getDoubleProperty(),
                     0.005);
        assertEquals("Copied int property",
                     333,
                     bean.getIntProperty());
        assertEquals("Copied long property",
                     (long) 3333,
                     bean.getLongProperty());
        assertEquals("Copied short property",
                     (short) 33,
                     bean.getShortProperty());
        assertEquals("Copied string property",
                     "Custom string",
                     bean.getStringProperty());

        // Validate the results for array properties
        String dupProperty[] = bean.getDupProperty();
        assertNotNull("dupProperty present", dupProperty);
        assertEquals("dupProperty length", 3, dupProperty.length);
        assertEquals("dupProperty[0]", "New 0", dupProperty[0]);
        assertEquals("dupProperty[1]", "New 1", dupProperty[1]);
        assertEquals("dupProperty[2]", "New 2", dupProperty[2]);
        int intArray[] = bean.getIntArray();
        assertNotNull("intArray present", intArray);
        assertEquals("intArray length", 3, intArray.length);
        assertEquals("intArray[0]", 100, intArray[0]);
        assertEquals("intArray[1]", 200, intArray[1]);
        assertEquals("intArray[2]", 300, intArray[2]);
        String stringArray[] = bean.getStringArray();
        assertNotNull("stringArray present", stringArray);
        assertEquals("stringArray length", 2, stringArray.length);
        assertEquals("stringArray[0]", "New 0", stringArray[0]);
        assertEquals("stringArray[1]", "New 1", stringArray[1]);

    }


    /**
     * Test the describe() method.
     */
    public void testDescribe() {

        Map map = null;
        try {
            map = BeanUtils.describe(bean);
        } catch (Exception e) {
            fail("Threw exception " + e);
        }

        // Verify existence of all the properties that should be present
        for (int i = 0; i < describes.length; i++) {
            assertTrue("Property '" + describes[i] + "' is present",
                       map.containsKey(describes[i]));
        }
        assertTrue("Property 'writeOnlyProperty' is not present",
                   !map.containsKey("writeOnlyProperty"));

        // Verify the values of scalar properties
        assertEquals("Value of 'booleanProperty'",
                     "true",
                     (String) map.get("booleanProperty"));
        assertEquals("Value of 'doubleProperty'",
                     "321.0",
                     (String) map.get("doubleProperty"));
        assertEquals("Value of 'floatProperty'",
                     "123.0",
                     (String) map.get("floatProperty"));
        assertEquals("Value of 'intProperty'",
                     "123",
                     (String) map.get("intProperty"));
        assertEquals("Value of 'longProperty'",
                     "321",
                     (String) map.get("longProperty"));
        assertEquals("Value of 'shortProperty'",
                     "987",
                     (String) map.get("shortProperty"));
        assertEquals("Value of 'stringProperty'",
                     "This is a string",
                     (String) map.get("stringProperty"));

    }


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
     * Test populate() on mapped properties.
     */
    public void testPopulateMapped() {

        try {

            HashMap map = new HashMap();
            map.put("mappedProperty(First Key)", "New First Value");
            map.put("mappedProperty(Third Key)", "New Third Value");

            BeanUtils.populate(bean, map);

            assertEquals("mappedProperty(First Key)",
                         "New First Value",
                         bean.getMappedProperty("First Key"));
            assertEquals("mappedProperty(Second Key)",
                         "Second Value",
                         bean.getMappedProperty("Second Key"));
            assertEquals("mappedProperty(Third Key)",
                         "New Third Value",
                         bean.getMappedProperty("Third Key"));
            assertNull("mappedProperty(Fourth Key",
                       bean.getMappedProperty("Fourth Key"));

        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        }

    }


    /**
     * Test populate() method on nested properties.
     */
    public void testPopulateNested() {

        try {

            HashMap map = new HashMap();
            map.put("nested.booleanProperty", "false");
            // booleanSecond is left at true
            map.put("nested.doubleProperty", "432.0");
            // floatProperty is left at 123.0
            map.put("nested.intProperty", "543");
            // longProperty is left at 321
            map.put("nested.shortProperty", "654");
            // stringProperty is left at "This is a string"
            map.put("nested.writeOnlyProperty", "New writeOnlyProperty value");

            BeanUtils.populate(bean, map);

            assertTrue("booleanProperty is false",
                       !bean.getNested().getBooleanProperty());
            assertTrue("booleanSecond is true",
                       bean.getNested().isBooleanSecond());
            assertEquals("doubleProperty is 432.0",
                         (double) 432.0,
                         bean.getNested().getDoubleProperty(),
                         (double) 0.005);
            assertEquals("floatProperty is 123.0",
                         (float) 123.0,
                         bean.getNested().getFloatProperty(),
                         (float) 0.005);
            assertEquals("intProperty is 543",
                         543, bean.getNested().getIntProperty());
            assertEquals("longProperty is 321",
                         (long) 321, bean.getNested().getLongProperty());
            assertEquals("shortProperty is 654",
                         (short) 654, bean.getNested().getShortProperty());
            assertEquals("stringProperty is \"This is a string\"",
                         "This is a string",
                         bean.getNested().getStringProperty());
            assertEquals("writeOnlyProperty is \"New writeOnlyProperty value\"",
                         "New writeOnlyProperty value",
                         bean.getNested().getWriteOnlyPropertyValue());

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
            map.put("longProperty", "");
            map.put("shortProperty", "654");
            // stringProperty is left at "This is a string"
            map.put("writeOnlyProperty", "New writeOnlyProperty value");
            map.put("readOnlyProperty", "New readOnlyProperty value");

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
            assertEquals("longProperty is 0",
                         (long) 0, bean.getLongProperty());
            assertEquals("shortProperty is 654",
                         (short) 654, bean.getShortProperty());
            assertEquals("stringProperty is \"This is a string\"",
                         "This is a string", bean.getStringProperty());
            assertEquals("writeOnlyProperty is \"New writeOnlyProperty value\"",
                         "New writeOnlyProperty value",
                         bean.getWriteOnlyPropertyValue());
            assertEquals("readOnlyProperty is \"Read Only String Property\"",
                         "Read Only String Property",
                         bean.getReadOnlyProperty());

        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        }

    }


    /** See http://issues.apache.org/bugzilla/show_bug.cgi?id=15170 */
    public void testSetPropertyOnPrimitavieWrappers() throws Exception {
        BeanUtils.setProperty(bean,"intProperty", new Integer(1));
        assertEquals(1,bean.getIntProperty());
    }
}

