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


import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.beanutils.converters.ArrayConverter;
import org.apache.commons.beanutils.converters.DateConverter;


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
 * @version $Id$
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

    /** Test Calendar value */
    protected java.util.Calendar testCalendar;

    /** Test java.util.Date value */
    protected java.util.Date testUtilDate;

    /** Test String Date value */
    protected String testStringDate;

    // ---------------------------------------------------------- Constructors

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public BeanUtilsTestCase(final String name) {
        super(name);
    }


    // -------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    @Override
    public void setUp() {
        ConvertUtils.deregister();
        BeanUtilsBean.setInstance(new BeanUtilsBean());
        setUpShared();
    }

    /**
     * Shared Set up.
     */
    protected void setUpShared() {
        bean = new TestBean();

        final DateConverter dateConverter = new DateConverter(null);
        dateConverter.setLocale(Locale.US);
        dateConverter.setPattern("dd.MM.yyyy");
        ConvertUtils.register(dateConverter, java.util.Date.class);

        final ArrayConverter dateArrayConverter =
            new ArrayConverter(java.util.Date[].class, dateConverter, 0);
        ConvertUtils.register(dateArrayConverter, java.util.Date[].class);

        testCalendar = Calendar.getInstance();
        testCalendar.set(1992, 11, 28, 0, 0, 0);
        testCalendar.set(Calendar.MILLISECOND, 0);
        testUtilDate = testCalendar.getTime();
        testStringDate = "28.12.1992";
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
    @Override
    public void tearDown() {
        bean = null;
    }


    // ------------------------------------------------ Individual Test Methods


    /**
     * Test the copyProperties() method from a DynaBean.
     */
    public void testCopyPropertiesDynaBean() {

        // Set up an origin bean with customized properties
        final DynaClass dynaClass = DynaBeanUtilsTestCase.createDynaClass();
        DynaBean orig = null;
        try {
            orig = dynaClass.newInstance();
        } catch (final Exception e) {
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
        } catch (final Exception e) {
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
                     3333,
                     bean.getLongProperty());
        assertEquals("Copied short property",
                     (short) 33,
                     bean.getShortProperty());
        assertEquals("Copied string property",
                     "Custom string",
                     bean.getStringProperty());

        // Validate the results for array properties
        final String dupProperty[] = bean.getDupProperty();
        assertNotNull("dupProperty present", dupProperty);
        assertEquals("dupProperty length", 3, dupProperty.length);
        assertEquals("dupProperty[0]", "New 0", dupProperty[0]);
        assertEquals("dupProperty[1]", "New 1", dupProperty[1]);
        assertEquals("dupProperty[2]", "New 2", dupProperty[2]);
        final int intArray[] = bean.getIntArray();
        assertNotNull("intArray present", intArray);
        assertEquals("intArray length", 3, intArray.length);
        assertEquals("intArray[0]", 100, intArray[0]);
        assertEquals("intArray[1]", 200, intArray[1]);
        assertEquals("intArray[2]", 300, intArray[2]);
        final String stringArray[] = bean.getStringArray();
        assertNotNull("stringArray present", stringArray);
        assertEquals("stringArray length", 2, stringArray.length);
        assertEquals("stringArray[0]", "New 0", stringArray[0]);
        assertEquals("stringArray[1]", "New 1", stringArray[1]);

    }


    /**
     * Test copyProperties() when the origin is a a <code>Map</code>.
     */
    public void testCopyPropertiesMap() {

        final Map<String, Object> map = new HashMap<String, Object>();
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
        } catch (final Throwable t) {
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
        assertEquals("longProperty", 444,
                     bean.getLongProperty());
        assertEquals("shortProperty", (short) 555,
                     bean.getShortProperty());
        assertEquals("stringProperty", "New String Property",
                     bean.getStringProperty());

        // Indexed Properties
        final String dupProperty[] = bean.getDupProperty();
        assertNotNull("dupProperty present", dupProperty);
        assertEquals("dupProperty length", 3, dupProperty.length);
        assertEquals("dupProperty[0]", "New 0", dupProperty[0]);
        assertEquals("dupProperty[1]", "New 1", dupProperty[1]);
        assertEquals("dupProperty[2]", "New 2", dupProperty[2]);
        final int intArray[] = bean.getIntArray();
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
        final TestBean orig = new TestBean();
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
        } catch (final Exception e) {
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
                     3333,
                     bean.getLongProperty());
        assertEquals("Copied short property",
                     (short) 33,
                     bean.getShortProperty());
        assertEquals("Copied string property",
                     "Custom string",
                     bean.getStringProperty());

        // Validate the results for array properties
        final String dupProperty[] = bean.getDupProperty();
        assertNotNull("dupProperty present", dupProperty);
        assertEquals("dupProperty length", 3, dupProperty.length);
        assertEquals("dupProperty[0]", "New 0", dupProperty[0]);
        assertEquals("dupProperty[1]", "New 1", dupProperty[1]);
        assertEquals("dupProperty[2]", "New 2", dupProperty[2]);
        final int intArray[] = bean.getIntArray();
        assertNotNull("intArray present", intArray);
        assertEquals("intArray length", 3, intArray.length);
        assertEquals("intArray[0]", 100, intArray[0]);
        assertEquals("intArray[1]", 200, intArray[1]);
        assertEquals("intArray[2]", 300, intArray[2]);
        final String stringArray[] = bean.getStringArray();
        assertNotNull("stringArray present", stringArray);
        assertEquals("stringArray length", 2, stringArray.length);
        assertEquals("stringArray[0]", "New 0", stringArray[0]);
        assertEquals("stringArray[1]", "New 1", stringArray[1]);

    }


    /**
     * Test the describe() method.
     */
    public void testDescribe() {

        Map<String, String> map = null;
        try {
            map = BeanUtils.describe(bean);
        } catch (final Exception e) {
            fail("Threw exception " + e);
        }

        // Verify existence of all the properties that should be present
        for (String describe : describes) {
            assertTrue("Property '" + describe + "' is present",
                       map.containsKey(describe));
        }
        assertTrue("Property 'writeOnlyProperty' is not present",
                   !map.containsKey("writeOnlyProperty"));

        // Verify the values of scalar properties
        assertEquals("Value of 'booleanProperty'",
                     "true",
                     map.get("booleanProperty"));
        assertEquals("Value of 'byteProperty'",
                     "121",
                     map.get("byteProperty"));
        assertEquals("Value of 'doubleProperty'",
                     "321.0",
                     map.get("doubleProperty"));
        assertEquals("Value of 'floatProperty'",
                     "123.0",
                     map.get("floatProperty"));
        assertEquals("Value of 'intProperty'",
                     "123",
                     map.get("intProperty"));
        assertEquals("Value of 'longProperty'",
                     "321",
                     map.get("longProperty"));
        assertEquals("Value of 'shortProperty'",
                     "987",
                     map.get("shortProperty"));
        assertEquals("Value of 'stringProperty'",
                     "This is a string",
                     map.get("stringProperty"));

    }


    /**
     *  tests the string and int arrays of TestBean
     */
    public void testGetArrayProperty() {
        try {
            String arr[] = BeanUtils.getArrayProperty(bean, "stringArray");
            final String comp[] = bean.getStringArray();

            assertTrue("String array length = " + comp.length,
                    (comp.length == arr.length));

            arr = BeanUtils.getArrayProperty(bean, "intArray");
            final int iarr[] = bean.getIntArray();

            assertTrue("String array length = " + iarr.length,
                    (iarr.length == arr.length));


            // Test property which isn't array or collection
            arr = BeanUtils.getArrayProperty(bean, "shortProperty");
            final String shortAsString = "" + bean.getShortProperty();
            assertEquals("Short List Test lth", 1, arr.length);
            assertEquals("Short Test value", shortAsString, arr[0]);


            // Test comma delimited list
            bean.setStringProperty("ABC");
            arr = BeanUtils.getArrayProperty(bean, "stringProperty");
            assertEquals("Delimited List Test lth", 1, arr.length);
            assertEquals("Delimited List Test value1", "ABC", arr[0]);

        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }

    /**
     * Test <code>getArrayProperty()</code> converting to a String.
     */
    public void testGetArrayPropertyDate() {
        String[] value = null;
        try {
            bean.setDateArrayProperty(new java.util.Date[] {testUtilDate});
            value = BeanUtils.getArrayProperty(bean, "dateArrayProperty");
        } catch (final Throwable t) {
            fail("Threw " + t);
        }
        assertEquals("java.util.Date[] --> String[] length", 1, value.length);
        assertEquals("java.util.Date[] --> String[] value ", testUtilDate.toString(), value[0]);
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
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }
    }

    /**
     * Test <code>getArrayProperty()</code> converting to a String.
     */
    public void testGetIndexedPropertyDate() {
        String value = null;
        try {
            bean.setDateArrayProperty(new java.util.Date[] {testUtilDate});
            value = BeanUtils.getIndexedProperty(bean, "dateArrayProperty[0]");
        } catch (final Throwable t) {
            fail("Threw " + t);
        }
        assertEquals("java.util.Date[0] --> String", testUtilDate.toString(), value);
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

        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }
    }


    /**
     *  tests getting a nested property
     */
    public void testGetNestedProperty() {
        try {
            final String val = BeanUtils.getNestedProperty(bean, "nested.stringProperty");
            final String comp = bean.getNested().getStringProperty();
            assertTrue("nested.StringProperty == " + comp,
                    val.equals(comp));
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }
    }


    /**
     *  tests getting a 'whatever' property
     */
    public void testGetGeneralProperty() {
        try {
            final String val = BeanUtils.getProperty(bean, "nested.intIndexed[2]");
            final String comp = String.valueOf(bean.getIntIndexed(2));

            assertTrue("nested.intIndexed[2] == " + comp,
                    val.equals(comp));
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }
    }


    /**
     *  tests getting a 'whatever' property
     */
    public void testGetSimpleProperty() {
        try {
            final String val = BeanUtils.getSimpleProperty(bean, "shortProperty");
            final String comp = String.valueOf(bean.getShortProperty());

            assertTrue("shortProperty == " + comp,
                    val.equals(comp));
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }
    }

    /**
     * Test <code>getSimpleProperty()</code> converting to a String.
     */
    public void testGetSimplePropertyDate() {
        String value = null;
        try {
            bean.setDateProperty(testUtilDate);
            value = BeanUtils.getSimpleProperty(bean, "dateProperty");
        } catch (final Throwable t) {
            fail("Threw " + t);
        }
        assertEquals("java.util.Date --> String", testUtilDate.toString(), value);
    }

    /**
     * Test populate() method on individual array elements.
     */
    public void testPopulateArrayElements() {

        try {

            final HashMap<String, Object> map = new HashMap<String, Object>();
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

        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        }

    }


    /**
     * Test populate() method on array properties as a whole.
     */
    public void testPopulateArrayProperties() {

        try {

            final HashMap<String, Object> map = new HashMap<String, Object>();
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

        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        }

    }


    /**
     * Test populate() on mapped properties.
     */
    public void testPopulateMapped() {

        try {

            final HashMap<String, Object> map = new HashMap<String, Object>();
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

        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        }

    }


    /**
     * Test populate() method on nested properties.
     */
    public void testPopulateNested() {

        try {

            final HashMap<String, Object> map = new HashMap<String, Object>();
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
                         432.0,
                         bean.getNested().getDoubleProperty(),
                         0.005);
            assertEquals("floatProperty is 123.0",
                         (float) 123.0,
                         bean.getNested().getFloatProperty(),
                         (float) 0.005);
            assertEquals("intProperty is 543",
                         543, bean.getNested().getIntProperty());
            assertEquals("longProperty is 321",
                         321, bean.getNested().getLongProperty());
            assertEquals("shortProperty is 654",
                         (short) 654, bean.getNested().getShortProperty());
            assertEquals("stringProperty is \"This is a string\"",
                         "This is a string",
                         bean.getNested().getStringProperty());
            assertEquals("writeOnlyProperty is \"New writeOnlyProperty value\"",
                         "New writeOnlyProperty value",
                         bean.getNested().getWriteOnlyPropertyValue());

        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        }

    }


    /**
     * Test populate() method on scalar properties.
     */
    public void testPopulateScalar() {

        try {

            bean.setNullProperty("Non-null value");

            final HashMap<String, Object> map = new HashMap<String, Object>();
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
                         432.0, bean.getDoubleProperty(),
                         0.005);
            assertEquals("floatProperty is 123.0",
                         (float) 123.0, bean.getFloatProperty(),
                         (float) 0.005);
            assertEquals("intProperty is 543",
                         543, bean.getIntProperty());
            assertEquals("longProperty is 0",
                         0, bean.getLongProperty());
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

        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final InvocationTargetException e) {
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
        BeanUtils.setProperty(bean, "byteProperty", new Integer(123));
        assertEquals((byte) 123, bean.getByteProperty());
        BeanUtils.setProperty(bean, "byteProperty", new Long(123));
        assertEquals((byte) 123, bean.getByteProperty());
        BeanUtils.setProperty(bean, "byteProperty", new Short((short) 123));
        assertEquals((byte) 123, bean.getByteProperty());

    }

    /**
     * Test <code>setProperty()</code> conversion.
     */
    public void testSetPropertyConvert() {
        try {
            BeanUtils.setProperty(bean, "dateProperty", testCalendar);
        } catch (final Throwable t) {
            fail("Threw " + t);
        }
        assertEquals("Calendar --> java.util.Date", testUtilDate, bean.getDateProperty());
    }

    /**
     * Test <code>setProperty()</code> converting from a String.
     */
    public void testSetPropertyConvertFromString() {
        try {
            BeanUtils.setProperty(bean, "dateProperty", testStringDate);
        } catch (final Throwable t) {
            fail("Threw " + t);
        }
        assertEquals("String --> java.util.Date", testUtilDate, bean.getDateProperty());
    }

    /**
     * Test <code>setProperty()</code> converting to a String.
     */
    public void testSetPropertyConvertToString() {
        try {
            BeanUtils.setProperty(bean, "stringProperty", testUtilDate);
        } catch (final Throwable t) {
            fail("Threw " + t);
        }
        assertEquals("java.util.Date --> String", testUtilDate.toString(), bean.getStringProperty());
    }

    /**
     * Test <code>setProperty()</code> converting to a String array.
     */
    public void testSetPropertyConvertToStringArray() {
        try {
            bean.setStringArray(null);
            BeanUtils.setProperty(bean, "stringArray", new java.util.Date[] {testUtilDate});
        } catch (final Throwable t) {
            fail("Threw " + t);
        }
        assertEquals("java.util.Date[] --> String[] length", 1, bean.getStringArray().length);
        assertEquals("java.util.Date[] --> String[] value ", testUtilDate.toString(), bean.getStringArray()[0]);
    }

    /**
     * Test <code>setProperty()</code> converting to a String on indexed property
     */
    public void testSetPropertyConvertToStringIndexed() {
        try {
            bean.setStringArray(new String[1]);
            BeanUtils.setProperty(bean, "stringArray[0]", testUtilDate);
        } catch (final Throwable t) {
            fail("Threw " + t);
        }
        assertEquals("java.util.Date --> String[]", testUtilDate.toString(), bean.getStringArray()[0]);
    }

    /**
     * Test narrowing and widening conversions on double.
     */
    public void testSetPropertyDouble() throws Exception {

        BeanUtils.setProperty(bean, "doubleProperty", new Byte((byte) 123));
        assertEquals(123, bean.getDoubleProperty(), 0.005);
        BeanUtils.setProperty(bean, "doubleProperty", new Double(123));
        assertEquals(123, bean.getDoubleProperty(), 0.005);
        BeanUtils.setProperty(bean, "doubleProperty", new Float(123));
        assertEquals(123, bean.getDoubleProperty(), 0.005);
        BeanUtils.setProperty(bean, "doubleProperty", new Integer(123));
        assertEquals(123, bean.getDoubleProperty(), 0.005);
        BeanUtils.setProperty(bean, "doubleProperty", new Long(123));
        assertEquals(123, bean.getDoubleProperty(), 0.005);
        BeanUtils.setProperty(bean, "doubleProperty", new Short((short) 123));
        assertEquals(123, bean.getDoubleProperty(), 0.005);

    }


    /**
     * Test narrowing and widening conversions on float.
     */
    public void testSetPropertyFloat() throws Exception {

        BeanUtils.setProperty(bean, "floatProperty", new Byte((byte) 123));
        assertEquals(123, bean.getFloatProperty(), 0.005);
        BeanUtils.setProperty(bean, "floatProperty", new Double(123));
        assertEquals(123, bean.getFloatProperty(), 0.005);
        BeanUtils.setProperty(bean, "floatProperty", new Float(123));
        assertEquals(123, bean.getFloatProperty(), 0.005);
        BeanUtils.setProperty(bean, "floatProperty", new Integer(123));
        assertEquals(123, bean.getFloatProperty(), 0.005);
        BeanUtils.setProperty(bean, "floatProperty", new Long(123));
        assertEquals(123, bean.getFloatProperty(), 0.005);
        BeanUtils.setProperty(bean, "floatProperty", new Short((short) 123));
        assertEquals(123, bean.getFloatProperty(), 0.005);

    }


    /**
     * Test narrowing and widening conversions on int.
     */
    public void testSetPropertyInteger() throws Exception {

        BeanUtils.setProperty(bean, "longProperty", new Byte((byte) 123));
        assertEquals(123, bean.getIntProperty());
/*
        BeanUtils.setProperty(bean, "longProperty", new Double((double) 123));
        assertEquals((int) 123, bean.getIntProperty());
        BeanUtils.setProperty(bean, "longProperty", new Float((float) 123));
        assertEquals((int) 123, bean.getIntProperty());
*/
        BeanUtils.setProperty(bean, "longProperty", new Integer(123));
        assertEquals(123, bean.getIntProperty());
        BeanUtils.setProperty(bean, "longProperty", new Long(123));
        assertEquals(123, bean.getIntProperty());
        BeanUtils.setProperty(bean, "longProperty", new Short((short) 123));
        assertEquals(123, bean.getIntProperty());

    }


    /**
     * Test narrowing and widening conversions on long.
     */
    public void testSetPropertyLong() throws Exception {

        BeanUtils.setProperty(bean, "longProperty", new Byte((byte) 123));
        assertEquals(123, bean.getLongProperty());
/*
        BeanUtils.setProperty(bean, "longProperty", new Double((double) 123));
        assertEquals((long) 123, bean.getLongProperty());
        BeanUtils.setProperty(bean, "longProperty", new Float((float) 123));
        assertEquals((long) 123, bean.getLongProperty());
*/
        BeanUtils.setProperty(bean, "longProperty", new Integer(123));
        assertEquals(123, bean.getLongProperty());
        BeanUtils.setProperty(bean, "longProperty", new Long(123));
        assertEquals(123, bean.getLongProperty());
        BeanUtils.setProperty(bean, "longProperty", new Short((short) 123));
        assertEquals(123, bean.getLongProperty());

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
        BeanUtils.setProperty(bean, "shortProperty", new Integer(123));
        assertEquals((short) 123, bean.getShortProperty());
        BeanUtils.setProperty(bean, "shortProperty", new Long(123));
        assertEquals((short) 123, bean.getShortProperty());
        BeanUtils.setProperty(bean, "shortProperty", new Short((short) 123));
        assertEquals((short) 123, bean.getShortProperty());

    }

    /**
     * Test setting a String value to a String array property
     */
    public void testSetPropertyStringToArray() throws Exception {
        BeanUtils.setProperty(bean, "stringArray", "ABC,DEF,GHI");
        final String[] strArray =  bean.getStringArray();
        assertEquals("length", 3, strArray.length);
        assertEquals("value[0]", "ABC", strArray[0]);
        assertEquals("value[1]", "DEF", strArray[1]);
        assertEquals("value[2]", "GHI", strArray[2]);

        BeanUtils.setProperty(bean, "intArray", "0, 10, 20, 30, 40");
        final int[] intArray =  bean.getIntArray();
        assertEquals("length", 5, intArray.length);
        assertEquals("value[0]", 0, intArray[0]);
        assertEquals("value[1]", 10, intArray[1]);
        assertEquals("value[2]", 20, intArray[2]);
        assertEquals("value[3]", 30, intArray[3]);
        assertEquals("value[4]", 40, intArray[4]);
    }


    /**
     * Test narrowing and widening conversions on byte.
     */
    public void testCopyPropertyByte() throws Exception {

        BeanUtils.copyProperty(bean, "byteProperty", new Byte((byte) 123));
        assertEquals((byte) 123, bean.getByteProperty());
        BeanUtils.copyProperty(bean, "byteProperty", new Double(123));
        assertEquals((byte) 123, bean.getByteProperty());
        BeanUtils.copyProperty(bean, "byteProperty", new Float(123));
        assertEquals((byte) 123, bean.getByteProperty());
        BeanUtils.copyProperty(bean, "byteProperty", new Integer(123));
        assertEquals((byte) 123, bean.getByteProperty());
        BeanUtils.copyProperty(bean, "byteProperty", new Long(123));
        assertEquals((byte) 123, bean.getByteProperty());
        BeanUtils.copyProperty(bean, "byteProperty", new Short((short) 123));
        assertEquals((byte) 123, bean.getByteProperty());

    }

    /**
     * Test <code>copyProperty()</code> conversion.
     */
    public void testCopyPropertyConvert() {
        try {
            BeanUtils.copyProperty(bean, "dateProperty", testCalendar);
        } catch (final Throwable t) {
            fail("Threw " + t);
        }
        assertEquals("Calendar --> java.util.Date", testUtilDate, bean.getDateProperty());
    }

    /**
     * Test <code>copyProperty()</code> converting from a String.
     */
    public void testCopyPropertyConvertFromString() {
        try {
            BeanUtils.copyProperty(bean, "dateProperty", testStringDate);
        } catch (final Throwable t) {
            fail("Threw " + t);
        }
        assertEquals("String --> java.util.Date", testUtilDate, bean.getDateProperty());
    }

    /**
     * Test <code>copyProperty()</code> converting to a String.
     */
    public void testCopyPropertyConvertToString() {
        try {
            BeanUtils.copyProperty(bean, "stringProperty", testUtilDate);
        } catch (final Throwable t) {
            fail("Threw " + t);
        }
        assertEquals("java.util.Date --> String", testUtilDate.toString(), bean.getStringProperty());
    }

    /**
     * Test <code>copyProperty()</code> converting to a String.
     */
    public void testCopyPropertyConvertToStringArray() {
        try {
            bean.setStringArray(null);
            BeanUtils.copyProperty(bean, "stringArray", new java.util.Date[] {testUtilDate});
        } catch (final Throwable t) {
            fail("Threw " + t);
        }
        assertEquals("java.util.Date[] --> String[] length", 1, bean.getStringArray().length);
        assertEquals("java.util.Date[] --> String[] value ", testUtilDate.toString(), bean.getStringArray()[0]);
    }

    /**
     * Test <code>copyProperty()</code> converting to a String on indexed property
     */
    public void testCopyPropertyConvertToStringIndexed() {
        try {
            bean.setStringArray(new String[1]);
            BeanUtils.copyProperty(bean, "stringArray[0]", testUtilDate);
        } catch (final Throwable t) {
            fail("Threw " + t);
        }
        assertEquals("java.util.Date --> String[]", testUtilDate.toString(), bean.getStringArray()[0]);
    }

    /**
     * Test narrowing and widening conversions on double.
     */
    public void testCopyPropertyDouble() throws Exception {

        BeanUtils.copyProperty(bean, "doubleProperty", new Byte((byte) 123));
        assertEquals(123, bean.getDoubleProperty(), 0.005);
        BeanUtils.copyProperty(bean, "doubleProperty", new Double(123));
        assertEquals(123, bean.getDoubleProperty(), 0.005);
        BeanUtils.copyProperty(bean, "doubleProperty", new Float(123));
        assertEquals(123, bean.getDoubleProperty(), 0.005);
        BeanUtils.copyProperty(bean, "doubleProperty", new Integer(123));
        assertEquals(123, bean.getDoubleProperty(), 0.005);
        BeanUtils.copyProperty(bean, "doubleProperty", new Long(123));
        assertEquals(123, bean.getDoubleProperty(), 0.005);
        BeanUtils.copyProperty(bean, "doubleProperty", new Short((short) 123));
        assertEquals(123, bean.getDoubleProperty(), 0.005);

    }


    /**
     * Test narrowing and widening conversions on float.
     */
    public void testCopyPropertyFloat() throws Exception {

        BeanUtils.copyProperty(bean, "floatProperty", new Byte((byte) 123));
        assertEquals(123, bean.getFloatProperty(), 0.005);
        BeanUtils.copyProperty(bean, "floatProperty", new Double(123));
        assertEquals(123, bean.getFloatProperty(), 0.005);
        BeanUtils.copyProperty(bean, "floatProperty", new Float(123));
        assertEquals(123, bean.getFloatProperty(), 0.005);
        BeanUtils.copyProperty(bean, "floatProperty", new Integer(123));
        assertEquals(123, bean.getFloatProperty(), 0.005);
        BeanUtils.copyProperty(bean, "floatProperty", new Long(123));
        assertEquals(123, bean.getFloatProperty(), 0.005);
        BeanUtils.copyProperty(bean, "floatProperty", new Short((short) 123));
        assertEquals(123, bean.getFloatProperty(), 0.005);

    }


    /**
     * Test narrowing and widening conversions on int.
     */
    public void testCopyPropertyInteger() throws Exception {

        BeanUtils.copyProperty(bean, "longProperty", new Byte((byte) 123));
        assertEquals(123, bean.getIntProperty());
        BeanUtils.copyProperty(bean, "longProperty", new Double(123));
        assertEquals(123, bean.getIntProperty());
        BeanUtils.copyProperty(bean, "longProperty", new Float(123));
        assertEquals(123, bean.getIntProperty());
        BeanUtils.copyProperty(bean, "longProperty", new Integer(123));
        assertEquals(123, bean.getIntProperty());
        BeanUtils.copyProperty(bean, "longProperty", new Long(123));
        assertEquals(123, bean.getIntProperty());
        BeanUtils.copyProperty(bean, "longProperty", new Short((short) 123));
        assertEquals(123, bean.getIntProperty());

    }


    /**
     * Test narrowing and widening conversions on long.
     */
    public void testCopyPropertyLong() throws Exception {

        BeanUtils.copyProperty(bean, "longProperty", new Byte((byte) 123));
        assertEquals(123, bean.getLongProperty());
        BeanUtils.copyProperty(bean, "longProperty", new Double(123));
        assertEquals(123, bean.getLongProperty());
        BeanUtils.copyProperty(bean, "longProperty", new Float(123));
        assertEquals(123, bean.getLongProperty());
        BeanUtils.copyProperty(bean, "longProperty", new Integer(123));
        assertEquals(123, bean.getLongProperty());
        BeanUtils.copyProperty(bean, "longProperty", new Long(123));
        assertEquals(123, bean.getLongProperty());
        BeanUtils.copyProperty(bean, "longProperty", new Short((short) 123));
        assertEquals(123, bean.getLongProperty());

    }


    /**
     * Test narrowing and widening conversions on short.
     */
    public void testCopyPropertyShort() throws Exception {

        BeanUtils.copyProperty(bean, "shortProperty", new Byte((byte) 123));
        assertEquals((short) 123, bean.getShortProperty());
        BeanUtils.copyProperty(bean, "shortProperty", new Double(123));
        assertEquals((short) 123, bean.getShortProperty());
        BeanUtils.copyProperty(bean, "shortProperty", new Float(123));
        assertEquals((short) 123, bean.getShortProperty());
        BeanUtils.copyProperty(bean, "shortProperty", new Integer(123));
        assertEquals((short) 123, bean.getShortProperty());
        BeanUtils.copyProperty(bean, "shortProperty", new Long(123));
        assertEquals((short) 123, bean.getShortProperty());
        BeanUtils.copyProperty(bean, "shortProperty", new Short((short) 123));
        assertEquals((short) 123, bean.getShortProperty());

    }


    /**
     * Test copying a property using a nested indexed array expression,
     * with and without conversions.
     */
    public void testCopyPropertyNestedIndexedArray() throws Exception {

        final int origArray[] = { 0, 10, 20, 30, 40 };
        final int intArray[] = { 0, 0, 0 };
        bean.getNested().setIntArray(intArray);
        final int intChanged[] = { 0, 0, 0 };

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
        BeanUtils.copyProperty(bean, "nested.intArray[1]", new Long(3));
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

        final Map<String, Object> origMap = new HashMap<String, Object>();
        origMap.put("First Key", "First Value");
        origMap.put("Second Key", "Second Value");
        final Map<String, Object> changedMap = new HashMap<String, Object>();
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
        BeanUtils.copyProperty(bean, "nested.intProperty", new Long(3));
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

    /**
     * Test setting a value out of a mapped Map
     */
    public void testSetMappedMap() {
        final TestBean bean = new TestBean();
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("sub-key-1", "sub-value-1");
        map.put("sub-key-2", "sub-value-2");
        map.put("sub-key-3", "sub-value-3");
        bean.getMapProperty().put("mappedMap", map);

        assertEquals("BEFORE", "sub-value-3", ((Map<?, ?>)bean.getMapProperty().get("mappedMap")).get("sub-key-3"));
        try {
            BeanUtils.setProperty(bean, "mapProperty(mappedMap)(sub-key-3)", "SUB-KEY-3-UPDATED");
        } catch (final Throwable t) {
            fail("Threw " + t + "");
        }
        assertEquals("AFTER", "SUB-KEY-3-UPDATED", ((Map<?, ?>)bean.getMapProperty().get("mappedMap")).get("sub-key-3"));
    }

    /** Tests that separate instances can register separate instances */
    public void testSeparateInstances() throws Exception {
        final BeanUtilsBean utilsOne = new BeanUtilsBean(
                                                new ConvertUtilsBean(),
                                                new PropertyUtilsBean());
        final BeanUtilsBean utilsTwo = new BeanUtilsBean(
                                                new ConvertUtilsBean(),
                                                new PropertyUtilsBean());


        final TestBean bean = new TestBean();

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

        } catch (final PassTestException e) { /* Do nothing */ }

        // make sure that this conversion has no been registered in the other instance
        try {

            bean.setBooleanProperty(false);
            utilsTwo.setProperty(bean, "booleanProperty", "true");
            assertEquals("Set property failed (3)", bean.getBooleanProperty(), true);

        } catch (final PassTestException e) {
            fail("Registed converter is used by other instances");
        }
    }

    public void testArrayPropertyConversion() throws Exception {
        final BeanUtilsBean beanUtils = new BeanUtilsBean(
                                                    new ConvertUtilsBean(),
                                                    new PropertyUtilsBean());

        final TestBean bean = new TestBean();
        final String [] results = beanUtils.getArrayProperty(bean, "intArray");

        final int[] values = bean.getIntArray();
        assertEquals(
                    "Converted array size not equal to property array size.",
                    results.length,
                    values.length);
        for (int i=0, size=values.length ;  i<size; i++) {
            assertEquals(
                    "Value " + i + " incorrectly converted ",
                    values[i] + "",
                    results[i]);
        }
    }

    // Ensure that the actual int[] matches the expected int[]
    protected void checkIntArray(final int actual[], final int expected[]) {
        assertNotNull("actual array not null", actual);
        assertEquals("actual array length", expected.length, actual.length);
        for (int i = 0; i < actual.length; i++) {
            assertEquals("actual array value[" + i + "]",
                         expected[i], actual[i]);
        }
    }


    // Ensure that the actual Map matches the expected Map
    protected void checkMap(final Map<?, ?> actual, final Map<?, ?> expected) {
        assertNotNull("actual map not null", actual);
        assertEquals("actual map size", expected.size(), actual.size());
        final Iterator<?> keys = expected.keySet().iterator();
        while (keys.hasNext()) {
            final Object key = keys.next();
            assertEquals("actual map value(" + key + ")",
                         expected.get(key), actual.get(key));
        }
    }

    public void testMappedProperty() throws Exception {
        final MappedPropertyTestBean bean = new MappedPropertyTestBean();

        BeanUtils.setProperty(bean, "mapproperty(this.that.the-other)", "some.dotty.value");

        assertEquals(
                        "Mapped property set correctly",
                        "some.dotty.value",
                        bean.getMapproperty("this.that.the-other"));
    }

    /**
     * Test for {@link BeanUtilsBean#initCause(Throwable, Throwable)} method.
     */
    public void testInitCause() {
        if (isPre14JVM()) {
            return;
        }
        final String parentMsg = "PARENT-THROWABLE";
        final String causeMsg  = "THROWABLE-CAUSE";
        try {
            initCauseAndThrowException(parentMsg, causeMsg);
        } catch (final Throwable thrownParent) {
            assertEquals("Parent", parentMsg, thrownParent.getMessage());
            try {
                assertEquals("Parent", parentMsg, thrownParent.getMessage());
                final Throwable thrownCause = getCause(thrownParent);
                assertNotNull("Cause Null", thrownCause);
                assertEquals("Cause", causeMsg, thrownCause.getMessage());
            } catch (final Throwable testError) {
                fail("If you're running JDK 1.3 then don't worry this should fail," +
                        " if not then needs checking out: " + testError);
            }
        }
    }

    /**
     * Use reflection to get the cause
     */
    private Throwable getCause(final Throwable t) throws Throwable {
        return (Throwable)PropertyUtils.getProperty(t, "cause");
    }

    /**
     * Catch a cause, initialize using BeanUtils.initCause() and throw new exception
     */
    private void initCauseAndThrowException(final String parent, final String cause) throws Throwable {
        try {
            throwException(cause);
        } catch (final Throwable e) {
            final Throwable t = new Exception(parent);
            BeanUtils.initCause(t, e);
            throw t;
        }
    }

    /**
     * Throw an exception with the specified message.
     */
    private void throwException(final String msg) throws Throwable {
        throw new Exception(msg);
    }

    /**
     * Test for JDK 1.4
     */
    public static boolean isPre14JVM() {
        final String version = System.getProperty("java.specification.version");
        final StringTokenizer tokenizer = new StringTokenizer(version,".");
        if (tokenizer.nextToken().equals("1")) {
            final String minorVersion = tokenizer.nextToken();
            if (minorVersion.equals("0")) {
                return true;
            }
            if (minorVersion.equals("1")) {
                return true;
            }
            if (minorVersion.equals("2")) {
                return true;
            }
            if (minorVersion.equals("3")) {
                return true;
            }
        }
        return false;
    }
}
