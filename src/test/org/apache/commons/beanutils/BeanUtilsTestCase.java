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
import java.util.Iterator;
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
 * @version $Revision$
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
      "byteProperty",
      "doubleProperty",
      "dupProperty",
      "floatProperty",
      "intArray",
      //      "intIndexed",
      "longProperty",
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
        orig.set("byteProperty", new Byte((byte) 111));
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
        assertEquals("Copied byte property",
                     (byte) 111,
                     bean.getByteProperty());
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
        map.put("byteProperty", "111");
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
        assertEquals("byteProperty", (byte) 111,
                     bean.getByteProperty());
        assertEquals("doubleProperty", 333.0,
                     bean.getDoubleProperty(), 0.005);
        assertEquals("floatProperty", (float) 222.0,
                     bean.getFloatProperty(), (float) 0.005);
        assertEquals("longProperty", 111,
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
        orig.setByteProperty((byte) 111);
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
        assertEquals("Copied byte property",
                     (byte) 111,
                     bean.getByteProperty());
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
        assertEquals("Value of 'byteProperty'",
                     "121",
                     (String) map.get("byteProperty"));
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

            bean.setNullProperty("Non-null value");

            HashMap map = new HashMap();
            map.put("booleanProperty", "false");
            // booleanSecond is left at true
            map.put("byteProperty", "111");
            map.put("doubleProperty", "432.0");
            // floatProperty is left at 123.0
            map.put("intProperty", "543");
            map.put("longProperty", "");
            map.put("nullProperty", null);
            map.put("shortProperty", "654");
            // stringProperty is left at "This is a string"
            map.put("writeOnlyProperty", "New writeOnlyProperty value");
            map.put("readOnlyProperty", "New readOnlyProperty value");

            BeanUtils.populate(bean, map);

            assertTrue("booleanProperty is false", !bean.getBooleanProperty());
            assertTrue("booleanSecond is true", bean.isBooleanSecond());
            assertEquals("byteProperty is 111",
                         (byte) 111, bean.getByteProperty());
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
            assertNull("nullProperty is null",
                       bean.getNullProperty());
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


    /**
     * Test calling setProperty() with null property values.
     */
    public void testSetPropertyNullValues() throws Exception {

        Object oldValue = null;
        Object newValue = null;

        // Scalar value into array
        oldValue = PropertyUtils.getSimpleProperty(bean, "stringArray");
        BeanUtils.setProperty(bean, "stringArray", (String) null);
        newValue = PropertyUtils.getSimpleProperty(bean, "stringArray");
        assertNotNull("stringArray is not null", newValue);
        assertTrue("stringArray of correct type",
                   newValue instanceof String[]);
        assertEquals("stringArray length",
                     1, ((String[]) newValue).length);
        assertTrue("stringArray[0] is null",
                   ((String[]) newValue)[0] == null);
        PropertyUtils.setProperty(bean, "stringArray", oldValue);

        // Indexed value into array
        oldValue = PropertyUtils.getSimpleProperty(bean, "stringArray");
        BeanUtils.setProperty(bean, "stringArray[2]", (String) null);
        newValue = PropertyUtils.getSimpleProperty(bean, "stringArray");
        assertNotNull("stringArray is not null", newValue);
        assertTrue("stringArray of correct type",
                   newValue instanceof String[]);
        assertEquals("stringArray length",
                     5, ((String[]) newValue).length);
        assertTrue("stringArray[2] is null",
                   ((String[]) newValue)[2] == null);
        PropertyUtils.setProperty(bean, "stringArray", oldValue);

        // Value into scalar
        BeanUtils.setProperty(bean, "stringProperty", null);
        assertTrue("stringProperty is now null",
                   BeanUtils.getProperty(bean, "stringProperty") == null);

    }


    /**
     * Test converting to and from primitive wrapper types.
     */
    public void testSetPropertyOnPrimitiveWrappers() throws Exception {

        BeanUtils.setProperty(bean,"intProperty", new Integer(1));
        assertEquals(1,bean.getIntProperty());
        BeanUtils.setProperty(bean,"stringProperty", new Integer(1));
        assertEquals(1, Integer.parseInt(bean.getStringProperty()));

    }


    /**
     * Test narrowing and widening conversions on byte.
     */
    public void testSetPropertyByte() throws Exception {

        BeanUtils.setProperty(bean, "byteProperty", new Byte((byte) 123));
        assertEquals((byte) 123, bean.getByteProperty());
/*
        BeanUtils.setProperty(bean, "byteProperty", new Double((double) 123));
        assertEquals((byte) 123, bean.getByteProperty());
        BeanUtils.setProperty(bean, "byteProperty", new Float((float) 123));
        assertEquals((byte) 123, bean.getByteProperty());
*/
        BeanUtils.setProperty(bean, "byteProperty", new Integer((int) 123));
        assertEquals((byte) 123, bean.getByteProperty());
        BeanUtils.setProperty(bean, "byteProperty", new Long((long) 123));
        assertEquals((byte) 123, bean.getByteProperty());
        BeanUtils.setProperty(bean, "byteProperty", new Short((short) 123));
        assertEquals((byte) 123, bean.getByteProperty());

    }


    /**
     * Test narrowing and widening conversions on double.
     */
    public void testSetPropertyDouble() throws Exception {

        BeanUtils.setProperty(bean, "doubleProperty", new Byte((byte) 123));
        assertEquals((double) 123, bean.getDoubleProperty(), 0.005);
        BeanUtils.setProperty(bean, "doubleProperty", new Double((double) 123));
        assertEquals((double) 123, bean.getDoubleProperty(), 0.005);
        BeanUtils.setProperty(bean, "doubleProperty", new Float((float) 123));
        assertEquals((double) 123, bean.getDoubleProperty(), 0.005);
        BeanUtils.setProperty(bean, "doubleProperty", new Integer((int) 123));
        assertEquals((double) 123, bean.getDoubleProperty(), 0.005);
        BeanUtils.setProperty(bean, "doubleProperty", new Long((long) 123));
        assertEquals((double) 123, bean.getDoubleProperty(), 0.005);
        BeanUtils.setProperty(bean, "doubleProperty", new Short((short) 123));
        assertEquals((double) 123, bean.getDoubleProperty(), 0.005);

    }


    /**
     * Test narrowing and widening conversions on float.
     */
    public void testSetPropertyFloat() throws Exception {

        BeanUtils.setProperty(bean, "floatProperty", new Byte((byte) 123));
        assertEquals((float) 123, bean.getFloatProperty(), 0.005);
        BeanUtils.setProperty(bean, "floatProperty", new Double((double) 123));
        assertEquals((float) 123, bean.getFloatProperty(), 0.005);
        BeanUtils.setProperty(bean, "floatProperty", new Float((float) 123));
        assertEquals((float) 123, bean.getFloatProperty(), 0.005);
        BeanUtils.setProperty(bean, "floatProperty", new Integer((int) 123));
        assertEquals((float) 123, bean.getFloatProperty(), 0.005);
        BeanUtils.setProperty(bean, "floatProperty", new Long((long) 123));
        assertEquals((float) 123, bean.getFloatProperty(), 0.005);
        BeanUtils.setProperty(bean, "floatProperty", new Short((short) 123));
        assertEquals((float) 123, bean.getFloatProperty(), 0.005);

    }


    /**
     * Test narrowing and widening conversions on int.
     */
    public void testSetPropertyInteger() throws Exception {

        BeanUtils.setProperty(bean, "longProperty", new Byte((byte) 123));
        assertEquals((int) 123, bean.getIntProperty());
/*
        BeanUtils.setProperty(bean, "longProperty", new Double((double) 123));
        assertEquals((int) 123, bean.getIntProperty());
        BeanUtils.setProperty(bean, "longProperty", new Float((float) 123));
        assertEquals((int) 123, bean.getIntProperty());
*/
        BeanUtils.setProperty(bean, "longProperty", new Integer((int) 123));
        assertEquals((int) 123, bean.getIntProperty());
        BeanUtils.setProperty(bean, "longProperty", new Long((long) 123));
        assertEquals((int) 123, bean.getIntProperty());
        BeanUtils.setProperty(bean, "longProperty", new Short((short) 123));
        assertEquals((int) 123, bean.getIntProperty());

    }


    /**
     * Test narrowing and widening conversions on long.
     */
    public void testSetPropertyLong() throws Exception {

        BeanUtils.setProperty(bean, "longProperty", new Byte((byte) 123));
        assertEquals((long) 123, bean.getLongProperty());
/*
        BeanUtils.setProperty(bean, "longProperty", new Double((double) 123));
        assertEquals((long) 123, bean.getLongProperty());
        BeanUtils.setProperty(bean, "longProperty", new Float((float) 123));
        assertEquals((long) 123, bean.getLongProperty());
*/
        BeanUtils.setProperty(bean, "longProperty", new Integer((int) 123));
        assertEquals((long) 123, bean.getLongProperty());
        BeanUtils.setProperty(bean, "longProperty", new Long((long) 123));
        assertEquals((long) 123, bean.getLongProperty());
        BeanUtils.setProperty(bean, "longProperty", new Short((short) 123));
        assertEquals((long) 123, bean.getLongProperty());

    }


    /**
     * Test setting a null property value.
     */
    public void testSetPropertyNull() throws Exception {

        bean.setNullProperty("non-null value");
        BeanUtils.setProperty(bean, "nullProperty", null);
        assertNull("nullProperty is null", bean.getNullProperty());

    }


    /**
     * Test narrowing and widening conversions on short.
     */
    public void testSetPropertyShort() throws Exception {

        BeanUtils.setProperty(bean, "shortProperty", new Byte((byte) 123));
        assertEquals((short) 123, bean.getShortProperty());
/*
        BeanUtils.setProperty(bean, "shortProperty", new Double((double) 123));
        assertEquals((short) 123, bean.getShortProperty());
        BeanUtils.setProperty(bean, "shortProperty", new Float((float) 123));
        assertEquals((short) 123, bean.getShortProperty());
*/
        BeanUtils.setProperty(bean, "shortProperty", new Integer((int) 123));
        assertEquals((short) 123, bean.getShortProperty());
        BeanUtils.setProperty(bean, "shortProperty", new Long((long) 123));
        assertEquals((short) 123, bean.getShortProperty());
        BeanUtils.setProperty(bean, "shortProperty", new Short((short) 123));
        assertEquals((short) 123, bean.getShortProperty());

    }


    /**
     * Test narrowing and widening conversions on byte.
     */
    public void testCopyPropertyByte() throws Exception {

        BeanUtils.copyProperty(bean, "byteProperty", new Byte((byte) 123));
        assertEquals((byte) 123, bean.getByteProperty());
        BeanUtils.copyProperty(bean, "byteProperty", new Double((double) 123));
        assertEquals((byte) 123, bean.getByteProperty());
        BeanUtils.copyProperty(bean, "byteProperty", new Float((float) 123));
        assertEquals((byte) 123, bean.getByteProperty());
        BeanUtils.copyProperty(bean, "byteProperty", new Integer((int) 123));
        assertEquals((byte) 123, bean.getByteProperty());
        BeanUtils.copyProperty(bean, "byteProperty", new Long((long) 123));
        assertEquals((byte) 123, bean.getByteProperty());
        BeanUtils.copyProperty(bean, "byteProperty", new Short((short) 123));
        assertEquals((byte) 123, bean.getByteProperty());

    }


    /**
     * Test narrowing and widening conversions on double.
     */
    public void testCopyPropertyDouble() throws Exception {

        BeanUtils.copyProperty(bean, "doubleProperty", new Byte((byte) 123));
        assertEquals((double) 123, bean.getDoubleProperty(), 0.005);
        BeanUtils.copyProperty(bean, "doubleProperty", new Double((double) 123));
        assertEquals((double) 123, bean.getDoubleProperty(), 0.005);
        BeanUtils.copyProperty(bean, "doubleProperty", new Float((float) 123));
        assertEquals((double) 123, bean.getDoubleProperty(), 0.005);
        BeanUtils.copyProperty(bean, "doubleProperty", new Integer((int) 123));
        assertEquals((double) 123, bean.getDoubleProperty(), 0.005);
        BeanUtils.copyProperty(bean, "doubleProperty", new Long((long) 123));
        assertEquals((double) 123, bean.getDoubleProperty(), 0.005);
        BeanUtils.copyProperty(bean, "doubleProperty", new Short((short) 123));
        assertEquals((double) 123, bean.getDoubleProperty(), 0.005);

    }


    /**
     * Test narrowing and widening conversions on float.
     */
    public void testCopyPropertyFloat() throws Exception {

        BeanUtils.copyProperty(bean, "floatProperty", new Byte((byte) 123));
        assertEquals((float) 123, bean.getFloatProperty(), 0.005);
        BeanUtils.copyProperty(bean, "floatProperty", new Double((double) 123));
        assertEquals((float) 123, bean.getFloatProperty(), 0.005);
        BeanUtils.copyProperty(bean, "floatProperty", new Float((float) 123));
        assertEquals((float) 123, bean.getFloatProperty(), 0.005);
        BeanUtils.copyProperty(bean, "floatProperty", new Integer((int) 123));
        assertEquals((float) 123, bean.getFloatProperty(), 0.005);
        BeanUtils.copyProperty(bean, "floatProperty", new Long((long) 123));
        assertEquals((float) 123, bean.getFloatProperty(), 0.005);
        BeanUtils.copyProperty(bean, "floatProperty", new Short((short) 123));
        assertEquals((float) 123, bean.getFloatProperty(), 0.005);

    }


    /**
     * Test narrowing and widening conversions on int.
     */
    public void testCopyPropertyInteger() throws Exception {

        BeanUtils.copyProperty(bean, "longProperty", new Byte((byte) 123));
        assertEquals((int) 123, bean.getIntProperty());
        BeanUtils.copyProperty(bean, "longProperty", new Double((double) 123));
        assertEquals((int) 123, bean.getIntProperty());
        BeanUtils.copyProperty(bean, "longProperty", new Float((float) 123));
        assertEquals((int) 123, bean.getIntProperty());
        BeanUtils.copyProperty(bean, "longProperty", new Integer((int) 123));
        assertEquals((int) 123, bean.getIntProperty());
        BeanUtils.copyProperty(bean, "longProperty", new Long((long) 123));
        assertEquals((int) 123, bean.getIntProperty());
        BeanUtils.copyProperty(bean, "longProperty", new Short((short) 123));
        assertEquals((int) 123, bean.getIntProperty());

    }


    /**
     * Test narrowing and widening conversions on long.
     */
    public void testCopyPropertyLong() throws Exception {

        BeanUtils.copyProperty(bean, "longProperty", new Byte((byte) 123));
        assertEquals((long) 123, bean.getLongProperty());
        BeanUtils.copyProperty(bean, "longProperty", new Double((double) 123));
        assertEquals((long) 123, bean.getLongProperty());
        BeanUtils.copyProperty(bean, "longProperty", new Float((float) 123));
        assertEquals((long) 123, bean.getLongProperty());
        BeanUtils.copyProperty(bean, "longProperty", new Integer((int) 123));
        assertEquals((long) 123, bean.getLongProperty());
        BeanUtils.copyProperty(bean, "longProperty", new Long((long) 123));
        assertEquals((long) 123, bean.getLongProperty());
        BeanUtils.copyProperty(bean, "longProperty", new Short((short) 123));
        assertEquals((long) 123, bean.getLongProperty());

    }


    /**
     * Test narrowing and widening conversions on short.
     */
    public void testCopyPropertyShort() throws Exception {

        BeanUtils.copyProperty(bean, "shortProperty", new Byte((byte) 123));
        assertEquals((short) 123, bean.getShortProperty());
        BeanUtils.copyProperty(bean, "shortProperty", new Double((double) 123));
        assertEquals((short) 123, bean.getShortProperty());
        BeanUtils.copyProperty(bean, "shortProperty", new Float((float) 123));
        assertEquals((short) 123, bean.getShortProperty());
        BeanUtils.copyProperty(bean, "shortProperty", new Integer((int) 123));
        assertEquals((short) 123, bean.getShortProperty());
        BeanUtils.copyProperty(bean, "shortProperty", new Long((long) 123));
        assertEquals((short) 123, bean.getShortProperty());
        BeanUtils.copyProperty(bean, "shortProperty", new Short((short) 123));
        assertEquals((short) 123, bean.getShortProperty());

    }


    /**
     * Test copying a property using a nested indexed array expression,
     * with and without conversions.
     */
    public void testCopyPropertyNestedIndexedArray() throws Exception {

        int origArray[] = { 0, 10, 20, 30, 40 };
        int intArray[] = { 0, 0, 0 };
        bean.getNested().setIntArray(intArray);
        int intChanged[] = { 0, 0, 0 };

        // No conversion required
        BeanUtils.copyProperty(bean, "nested.intArray[1]", new Integer(1));
        checkIntArray(bean.getIntArray(), origArray);
        intChanged[1] = 1;
        checkIntArray(bean.getNested().getIntArray(), intChanged);

        // Widening conversion required
        BeanUtils.copyProperty(bean, "nested.intArray[1]", new Byte((byte) 2));
        checkIntArray(bean.getIntArray(), origArray);
        intChanged[1] = 2;
        checkIntArray(bean.getNested().getIntArray(), intChanged);

        // Narrowing conversion required
        BeanUtils.copyProperty(bean, "nested.intArray[1]", new Long((long) 3));
        checkIntArray(bean.getIntArray(), origArray);
        intChanged[1] = 3;
        checkIntArray(bean.getNested().getIntArray(), intChanged);

        // String conversion required
        BeanUtils.copyProperty(bean, "nested.intArray[1]", "4");
        checkIntArray(bean.getIntArray(), origArray);
        intChanged[1] = 4;
        checkIntArray(bean.getNested().getIntArray(), intChanged);

    }


    /**
     * Test copying a property using a nested mapped map property.
     */
    public void testCopyPropertyNestedMappedMap() throws Exception {

        Map origMap = new HashMap();
        origMap.put("First Key", "First Value");
        origMap.put("Second Key", "Second Value");
        Map changedMap = new HashMap();
        changedMap.put("First Key", "First Value");
        changedMap.put("Second Key", "Second Value");

        // No conversion required
        BeanUtils.copyProperty(bean, "nested.mapProperty(Second Key)",
                               "New Second Value");
        checkMap(bean.getMapProperty(), origMap);
        changedMap.put("Second Key", "New Second Value");
        checkMap(bean.getNested().getMapProperty(), changedMap);

    }


    /**
     * Test copying a property using a nested simple expression, with and
     * without conversions.
     */
    public void testCopyPropertyNestedSimple() throws Exception {

        bean.setIntProperty(0);
        bean.getNested().setIntProperty(0);

        // No conversion required
        BeanUtils.copyProperty(bean, "nested.intProperty", new Integer(1));
        assertNotNull(bean.getNested());
        assertEquals(0, bean.getIntProperty());
        assertEquals(1, bean.getNested().getIntProperty());

        // Widening conversion required
        BeanUtils.copyProperty(bean, "nested.intProperty", new Byte((byte) 2));
        assertNotNull(bean.getNested());
        assertEquals(0, bean.getIntProperty());
        assertEquals(2, bean.getNested().getIntProperty());

        // Narrowing conversion required
        BeanUtils.copyProperty(bean, "nested.intProperty", new Long((long) 3));
        assertNotNull(bean.getNested());
        assertEquals(0, bean.getIntProperty());
        assertEquals(3, bean.getNested().getIntProperty());

        // String conversion required
        BeanUtils.copyProperty(bean, "nested.intProperty", "4");
        assertNotNull(bean.getNested());
        assertEquals(0, bean.getIntProperty());
        assertEquals(4, bean.getNested().getIntProperty());

    }


    /**
     * Test copying a null property value.
     */
    public void testCopyPropertyNull() throws Exception {

        bean.setNullProperty("non-null value");
        BeanUtils.copyProperty(bean, "nullProperty", null);
        assertNull("nullProperty is null", bean.getNullProperty());

    }


    /**
     * Test copying a new value to a write-only property, with and without
     * conversions.
     */
    public void testCopyPropertyWriteOnly() throws Exception {

        bean.setWriteOnlyProperty("Original value");

        // No conversion required
        BeanUtils.copyProperty(bean, "writeOnlyProperty", "New value");
        assertEquals("New value", bean.getWriteOnlyPropertyValue());

        // Integer->String conversion required
        BeanUtils.copyProperty(bean, "writeOnlyProperty", new Integer(123));
        assertEquals("123", bean.getWriteOnlyPropertyValue());

    }


    /**
     * Test setting a new value to a write-only property, with and without
     * conversions.
     */
    public void testSetPropertyWriteOnly() throws Exception {

        bean.setWriteOnlyProperty("Original value");

        // No conversion required
        BeanUtils.setProperty(bean, "writeOnlyProperty", "New value");
        assertEquals("New value", bean.getWriteOnlyPropertyValue());

        // Integer->String conversion required
        BeanUtils.setProperty(bean, "writeOnlyProperty", new Integer(123));
        assertEquals("123", bean.getWriteOnlyPropertyValue());

    }

    /** Tests that separate instances can register separate instances */
    public void testSeparateInstances() throws Exception {
        BeanUtilsBean utilsOne = new BeanUtilsBean(
                                                new ConvertUtilsBean(), 
                                                new PropertyUtilsBean());
        BeanUtilsBean utilsTwo = new BeanUtilsBean(
                                                new ConvertUtilsBean(), 
                                                new PropertyUtilsBean());        
        
        
        TestBean bean = new TestBean();
        
        // Make sure what we're testing works
        bean.setBooleanProperty(false);
        utilsOne.setProperty(bean, "booleanProperty", "true");
        assertEquals("Set property failed (1)", bean.getBooleanProperty(), true);
        
        bean.setBooleanProperty(false);
        utilsTwo.setProperty(bean, "booleanProperty", "true");
        assertEquals("Set property failed (2)", bean.getBooleanProperty(), true);       
        
        // now change the registered conversion
        
        utilsOne.getConvertUtils().register(new ThrowExceptionConverter(), Boolean.TYPE);
        try {
            
            bean.setBooleanProperty(false);
            utilsOne.setProperty(bean, "booleanProperty", "true");
            fail("Registered conversion not used.");
            
        } catch (PassTestException e) { /* Do nothing */ }
        
        // make sure that this conversion has no been registered in the other instance
        try {
        
            bean.setBooleanProperty(false);
            utilsTwo.setProperty(bean, "booleanProperty", "true");
            assertEquals("Set property failed (3)", bean.getBooleanProperty(), true);
            
        } catch (PassTestException e) {
            fail("Registed converter is used by other instances");
        }
    }

    public void testArrayPropertyConversion() throws Exception {
        BeanUtilsBean beanUtils = new BeanUtilsBean(	
                                                    new ConvertUtilsBean(), 
                                                    new PropertyUtilsBean());
        beanUtils.getConvertUtils().register(
            new Converter () {
                public Object convert(Class type, Object value) {
                    return "Spam, spam, spam, spam!";
                }
            },
            String.class);
            
        TestBean bean = new TestBean();
        String [] results = beanUtils.getArrayProperty(bean, "intArray");
                
        int[] values = bean.getIntArray();
        assertEquals(
                    "Converted array size not equal to property array size.",
                    results.length,
                    values.length);
        for (int i=0, size=values.length ;  i<size; i++) {
            assertEquals(
                    "Value " + i + " incorrectly converted ", 
                    "Spam, spam, spam, spam!",
                    results[i]);
        }
    }

    // Ensure that the actual int[] matches the expected int[]
    protected void checkIntArray(int actual[], int expected[]) {
        assertNotNull("actual array not null", actual);
        assertEquals("actual array length", expected.length, actual.length);
        for (int i = 0; i < actual.length; i++) {
            assertEquals("actual array value[" + i + "]",
                         expected[i], actual[i]);
        }
    }


    // Ensure that the actual Map matches the expected Map
    protected void checkMap(Map actual, Map expected) {
        assertNotNull("actual map not null", actual);
        assertEquals("actual map size", expected.size(), actual.size());
        Iterator keys = expected.keySet().iterator();
        while (keys.hasNext()) {
            Object key = keys.next();
            assertEquals("actual map value(" + key + ")",
                         expected.get(key), actual.get(key));
        }
    }

    public void testMappedProperty() throws Exception {
        MappedPropertyTestBean bean = new MappedPropertyTestBean();
        
        BeanUtils.setProperty(bean, "mapproperty(this.that.the-other)", "some.dotty.value");
        
        assertEquals(
                        "Mapped property set correctly", 
                        "some.dotty.value", 
                        bean.getMapproperty("this.that.the-other"));
    }	
}
