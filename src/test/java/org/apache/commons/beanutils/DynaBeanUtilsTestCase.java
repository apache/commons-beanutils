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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Test case for BeanUtils when the underlying bean is actually a DynaBean.
 *
 * @version $Id$
 */

public class DynaBeanUtilsTestCase extends TestCase {


    // ----------------------------------------------------- Instance Variables


    /**
     * The basic test bean for each test.
     */
    protected DynaBean bean = null;


    /**
     * The nested bean pointed at by the "nested" property.
     */
    protected TestBean nested = null;


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
      "intIndexed",
      "intProperty",
      "listIndexed",
      "longProperty",
      "mapProperty",
      "mappedProperty",
      "mappedIntProperty",
      "nested",
      "nullProperty",
      //      "readOnlyProperty",
      "shortProperty",
      "stringArray",
      "stringIndexed",
      "stringProperty"
    };


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public DynaBeanUtilsTestCase(final String name) {

        super(name);

    }


    // --------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    @Override
    public void setUp() throws Exception {

        ConvertUtils.deregister();

        // Instantiate a new DynaBean instance
        final DynaClass dynaClass = createDynaClass();
        bean = dynaClass.newInstance();

        // Initialize the DynaBean's property values (like TestBean)
        bean.set("booleanProperty", new Boolean(true));
        bean.set("booleanSecond", new Boolean(true));
        bean.set("byteProperty", new Byte((byte) 121));
        bean.set("doubleProperty", new Double(321.0));
        bean.set("floatProperty", new Float((float) 123.0));
        final String dupProperty[] = { "Dup 0", "Dup 1", "Dup 2", "Dup 3", "Dup 4"};
        bean.set("dupProperty", dupProperty);
        final int intArray[] = { 0, 10, 20, 30, 40 };
        bean.set("intArray", intArray);
        final int intIndexed[] = { 0, 10, 20, 30, 40 };
        bean.set("intIndexed", intIndexed);
        bean.set("intProperty", new Integer(123));
        final List<String> listIndexed = new ArrayList<String>();
        listIndexed.add("String 0");
        listIndexed.add("String 1");
        listIndexed.add("String 2");
        listIndexed.add("String 3");
        listIndexed.add("String 4");
        bean.set("listIndexed", listIndexed);
        bean.set("longProperty", new Long(321));
        final HashMap<String, Object> mapProperty = new HashMap<String, Object>();
        mapProperty.put("First Key", "First Value");
        mapProperty.put("Second Key", "Second Value");
        bean.set("mapProperty", mapProperty);
        final HashMap<String, Object> mappedProperty = new HashMap<String, Object>();
        mappedProperty.put("First Key", "First Value");
        mappedProperty.put("Second Key", "Second Value");
        bean.set("mappedProperty", mappedProperty);
        final HashMap<String, Integer> mappedIntProperty = new HashMap<String, Integer>();
        mappedIntProperty.put("One", new Integer(1));
        mappedIntProperty.put("Two", new Integer(2));
        bean.set("mappedIntProperty", mappedIntProperty);
        nested = new TestBean();
        bean.set("nested", nested);
        // Property "nullProperty" is not initialized, so it should return null
        bean.set("shortProperty", new Short((short) 987));
        final String stringArray[] =
                { "String 0", "String 1", "String 2", "String 3", "String 4" };
        bean.set("stringArray", stringArray);
        final String stringIndexed[] =
                { "String 0", "String 1", "String 2", "String 3", "String 4" };
        bean.set("stringIndexed", stringIndexed);
        bean.set("stringProperty", "This is a string");

    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {

        return (new TestSuite(DynaBeanUtilsTestCase.class));

    }


    /**
     * Tear down instance variables required by this test case.
     */
    @Override
    public void tearDown() {

        bean = null;
        nested = null;

    }



    // ------------------------------------------------ Individual Test Methods

    /**
     * Test the cloneBean() method from a DynaBean.
     */
    public void testCloneDynaBean() {

        // Set up an origin bean with customized properties
        final DynaClass dynaClass = DynaBeanUtilsTestCase.createDynaClass();
        DynaBean orig = null;
        try {
            orig = dynaClass.newInstance();
        } catch (final Exception e) {
            fail("newInstance(): " + e);
        }
        orig.set("booleanProperty", Boolean.FALSE);
        orig.set("byteProperty", new Byte((byte)111));
        orig.set("doubleProperty", new Double(333.33));
        orig.set("dupProperty", new String[] { "New 0", "New 1", "New 2" });
        orig.set("intArray", new int[] { 100, 200, 300 });
        orig.set("intProperty", new Integer(333));
        orig.set("longProperty", new Long(3333));
        orig.set("shortProperty", new Short((short) 33));
        orig.set("stringArray", new String[] { "New 0", "New 1" });
        orig.set("stringProperty", "Custom string");

        // Copy the origin bean to our destination test bean
        DynaBean clonedBean = null;
        try {
            clonedBean = (DynaBean) BeanUtils.cloneBean(orig);
        } catch (final Exception e) {
            fail("Threw exception: " + e);
        }

        // Validate the results for scalar properties
        assertEquals("Cloned boolean property",
                     false,
                     ((Boolean) clonedBean.get("booleanProperty")).booleanValue());
        assertEquals("Cloned byte property",
                     (byte) 111,
                     ((Byte) clonedBean.get("byteProperty")).byteValue());
        assertEquals("Cloned double property",
                     333.33,
                     ((Double) clonedBean.get("doubleProperty")).doubleValue(),
                     0.005);
        assertEquals("Cloned int property",
                     333,
                     ((Integer) clonedBean.get("intProperty")).intValue());
        assertEquals("Cloned long property",
                     3333,
                     ((Long) clonedBean.get("longProperty")).longValue());
        assertEquals("Cloned short property",
                     (short) 33,
                     ((Short) clonedBean.get("shortProperty")).shortValue());
        assertEquals("Cloned string property",
                     "Custom string",
                     (String) clonedBean.get("stringProperty"));

        // Validate the results for array properties
        final String dupProperty[] = (String[]) clonedBean.get("dupProperty");
        assertNotNull("dupProperty present", dupProperty);
        assertEquals("dupProperty length", 3, dupProperty.length);
        assertEquals("dupProperty[0]", "New 0", dupProperty[0]);
        assertEquals("dupProperty[1]", "New 1", dupProperty[1]);
        assertEquals("dupProperty[2]", "New 2", dupProperty[2]);
        final int intArray[] = (int[]) clonedBean.get("intArray");
        assertNotNull("intArray present", intArray);
        assertEquals("intArray length", 3, intArray.length);
        assertEquals("intArray[0]", 100, intArray[0]);
        assertEquals("intArray[1]", 200, intArray[1]);
        assertEquals("intArray[2]", 300, intArray[2]);
        final String stringArray[] = (String[]) clonedBean.get("stringArray");
        assertNotNull("stringArray present", stringArray);
        assertEquals("stringArray length", 2, stringArray.length);
        assertEquals("stringArray[0]", "New 0", stringArray[0]);
        assertEquals("stringArray[1]", "New 1", stringArray[1]);

    }

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
        orig.set("byteProperty", new Byte((byte)111));
        orig.set("doubleProperty", new Double(333.33));
        orig.set("dupProperty", new String[] { "New 0", "New 1", "New 2" });
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
                     ((Boolean) bean.get("booleanProperty")).booleanValue());
        assertEquals("Copied byte property",
                     (byte) 111,
                     ((Byte) bean.get("byteProperty")).byteValue());
        assertEquals("Copied double property",
                     333.33,
                     ((Double) bean.get("doubleProperty")).doubleValue(),
                     0.005);
        assertEquals("Copied int property",
                     333,
                     ((Integer) bean.get("intProperty")).intValue());
        assertEquals("Copied long property",
                     3333,
                     ((Long) bean.get("longProperty")).longValue());
        assertEquals("Copied short property",
                     (short) 33,
                     ((Short) bean.get("shortProperty")).shortValue());
        assertEquals("Copied string property",
                     "Custom string",
                     (String) bean.get("stringProperty"));

        // Validate the results for array properties
        final String dupProperty[] = (String[]) bean.get("dupProperty");
        assertNotNull("dupProperty present", dupProperty);
        assertEquals("dupProperty length", 3, dupProperty.length);
        assertEquals("dupProperty[0]", "New 0", dupProperty[0]);
        assertEquals("dupProperty[1]", "New 1", dupProperty[1]);
        assertEquals("dupProperty[2]", "New 2", dupProperty[2]);
        final int intArray[] = (int[]) bean.get("intArray");
        assertNotNull("intArray present", intArray);
        assertEquals("intArray length", 3, intArray.length);
        assertEquals("intArray[0]", 100, intArray[0]);
        assertEquals("intArray[1]", 200, intArray[1]);
        assertEquals("intArray[2]", 300, intArray[2]);
        final String stringArray[] = (String[]) bean.get("stringArray");
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
                     ((Boolean) bean.get("booleanProperty")).booleanValue());
        assertEquals("byteProperty", (byte) 111,
                     ((Byte) bean.get("byteProperty")).byteValue());
        assertEquals("doubleProperty", 333.0,
                     ((Double) bean.get("doubleProperty")).doubleValue(),
                     0.005);
        assertEquals("floatProperty", (float) 222.0,
                     ((Float) bean.get("floatProperty")).floatValue(),
                     (float) 0.005);
        assertEquals("intProperty", 111,
                     ((Integer) bean.get("intProperty")).intValue());
        assertEquals("longProperty", 444,
                     ((Long) bean.get("longProperty")).longValue());
        assertEquals("shortProperty", (short) 555,
                     ((Short) bean.get("shortProperty")).shortValue());
        assertEquals("stringProperty", "New String Property",
                     (String) bean.get("stringProperty"));

        // Indexed Properties
        final String dupProperty[] = (String[]) bean.get("dupProperty");
        assertNotNull("dupProperty present", dupProperty);
        assertEquals("dupProperty length", 3, dupProperty.length);
        assertEquals("dupProperty[0]", "New 0", dupProperty[0]);
        assertEquals("dupProperty[1]", "New 1", dupProperty[1]);
        assertEquals("dupProperty[2]", "New 2", dupProperty[2]);
        final int intArray[] = (int[]) bean.get("intArray");
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
                     ((Boolean) bean.get("booleanProperty")).booleanValue());
        assertEquals("Copied byte property",
                     (byte) 111,
                     ((Byte) bean.get("byteProperty")).byteValue());
        assertEquals("Copied double property",
                     333.33,
                     ((Double) bean.get("doubleProperty")).doubleValue(),
                     0.005);
        assertEquals("Copied int property",
                     333,
                     ((Integer) bean.get("intProperty")).intValue());
        assertEquals("Copied long property",
                     3333,
                     ((Long) bean.get("longProperty")).longValue());
        assertEquals("Copied short property",
                     (short) 33,
                     ((Short) bean.get("shortProperty")).shortValue());
        assertEquals("Copied string property",
                     "Custom string",
                     (String) bean.get("stringProperty"));

        // Validate the results for array properties
        final String dupProperty[] = (String[]) bean.get("dupProperty");
        assertNotNull("dupProperty present", dupProperty);
        assertEquals("dupProperty length", 3, dupProperty.length);
        assertEquals("dupProperty[0]", "New 0", dupProperty[0]);
        assertEquals("dupProperty[1]", "New 1", dupProperty[1]);
        assertEquals("dupProperty[2]", "New 2", dupProperty[2]);
        final int intArray[] = (int[]) bean.get("intArray");
        assertNotNull("intArray present", intArray);
        assertEquals("intArray length", 3, intArray.length);
        assertEquals("intArray[0]", 100, intArray[0]);
        assertEquals("intArray[1]", 200, intArray[1]);
        assertEquals("intArray[2]", 300, intArray[2]);
        final String stringArray[] = (String[]) bean.get("stringArray");
        assertNotNull("stringArray present", stringArray);
        assertEquals("stringArray length", 2, stringArray.length);
        assertEquals("stringArray[0]", "New 0", stringArray[0]);
        assertEquals("stringArray[1]", "New 1", stringArray[1]);

    }


    /**
     * Test the describe() method.
     */
    public void testDescribe() {

        Map<String, Object> map = null;
        try {
            map = PropertyUtils.describe(bean);
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
                     Boolean.TRUE,
                     map.get("booleanProperty"));
        assertEquals("Value of 'byteProperty'",
                     new Byte((byte) 121),
                     map.get("byteProperty"));
        assertEquals("Value of 'doubleProperty'",
                     new Double(321.0),
                     map.get("doubleProperty"));
        assertEquals("Value of 'floatProperty'",
                     new Float((float) 123.0),
                     map.get("floatProperty"));
        assertEquals("Value of 'intProperty'",
                     new Integer(123),
                     map.get("intProperty"));
        assertEquals("Value of 'longProperty'",
                     new Long(321),
                     map.get("longProperty"));
        assertEquals("Value of 'shortProperty'",
                     new Short((short) 987),
                     map.get("shortProperty"));
        assertEquals("Value of 'stringProperty'",
                     "This is a string",
                     (String) map.get("stringProperty"));

    }


    /**
     * Test populate() method on array properties as a whole.
     */
    public void testPopulateArrayProperties() {

        try {

            final HashMap<String, Object> map = new HashMap<String, Object>();
            //            int intArray[] = new int[] { 123, 456, 789 };
            final String intArrayIn[] = new String[] { "123", "456", "789" };
            map.put("intArray", intArrayIn);
            String stringArray[] = new String[]
                { "New String 0", "New String 1" };
            map.put("stringArray", stringArray);

            BeanUtils.populate(bean, map);

            final int intArray[] = (int[]) bean.get("intArray");
            assertNotNull("intArray is present", intArray);
            assertEquals("intArray length",
                         3, intArray.length);
            assertEquals("intArray[0]", 123, intArray[0]);
            assertEquals("intArray[1]", 456, intArray[1]);
            assertEquals("intArray[2]", 789, intArray[2]);
            stringArray = (String[]) bean.get("stringArray");
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
     *  tests the string and int arrays of TestBean
     */
    public void testGetArrayProperty() {
        try {
            String arr[] = BeanUtils.getArrayProperty(bean, "stringArray");
            final String comp[] = (String[]) bean.get("stringArray");

            assertTrue("String array length = " + comp.length,
                    (comp.length == arr.length));

            arr = BeanUtils.getArrayProperty(bean, "intArray");
            final int iarr[] = (int[]) bean.get("intArray");

            assertTrue("String array length = " + iarr.length,
                    (iarr.length == arr.length));
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     *  tests getting an indexed property
     */
    public void testGetIndexedProperty1() {
        try {
            String val = BeanUtils.getIndexedProperty(bean, "intIndexed[3]");
            String comp = String.valueOf(bean.get("intIndexed", 3));
            assertTrue("intIndexed[3] == " + comp, val.equals(comp));

            val = BeanUtils.getIndexedProperty(bean, "stringIndexed[3]");
            comp = (String) bean.get("stringIndexed", 3);
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
     *  tests getting an indexed property
     */
    public void testGetIndexedProperty2() {
        try {
            String val = BeanUtils.getIndexedProperty(bean, "intIndexed", 3);
            String comp = String.valueOf(bean.get("intIndexed", 3));

            assertTrue("intIndexed,3 == " + comp, val.equals(comp));

            val = BeanUtils.getIndexedProperty(bean, "stringIndexed", 3);
            comp = (String) bean.get("stringIndexed", 3);

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
            final String comp = nested.getStringProperty();
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
            final String comp = String.valueOf(bean.get("intIndexed", 2));

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
            final String comp = String.valueOf(bean.get("shortProperty"));

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
     * Test populate() method on individual array elements.
     */
    public void testPopulateArrayElements() {

        try {

            final HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("intIndexed[0]", "100");
            map.put("intIndexed[2]", "120");
            map.put("intIndexed[4]", "140");

            BeanUtils.populate(bean, map);
            final Integer intIndexed0 = (Integer) bean.get("intIndexed", 0);
            assertEquals("intIndexed[0] is 100",
                         100, intIndexed0.intValue());
            final Integer intIndexed1 = (Integer) bean.get("intIndexed", 1);
            assertEquals("intIndexed[1] is 10",
                         10, intIndexed1.intValue());
            final Integer intIndexed2 = (Integer) bean.get("intIndexed", 2);
            assertEquals("intIndexed[2] is 120",
                         120, intIndexed2.intValue());
            final Integer intIndexed3 = (Integer) bean.get("intIndexed", 3);
            assertEquals("intIndexed[3] is 30",
                         30, intIndexed3.intValue());
            final Integer intIndexed4 = (Integer) bean.get("intIndexed", 4);
            assertEquals("intIndexed[4] is 140",
                         140, intIndexed4.intValue());

            map.clear();
            map.put("stringIndexed[1]", "New String 1");
            map.put("stringIndexed[3]", "New String 3");

            BeanUtils.populate(bean, map);

            assertEquals("stringIndexed[0] is \"String 0\"",
                         "String 0",
                         (String) bean.get("stringIndexed", 0));
            assertEquals("stringIndexed[1] is \"New String 1\"",
                         "New String 1",
                         (String) bean.get("stringIndexed", 1));
            assertEquals("stringIndexed[2] is \"String 2\"",
                         "String 2",
                         (String) bean.get("stringIndexed", 2));
            assertEquals("stringIndexed[3] is \"New String 3\"",
                         "New String 3",
                         (String) bean.get("stringIndexed", 3));
            assertEquals("stringIndexed[4] is \"String 4\"",
                         "String 4",
                         (String) bean.get("stringIndexed", 4));

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
                         (String) bean.get("mappedProperty", "First Key"));
            assertEquals("mappedProperty(Second Key)",
                         "Second Value",
                         (String) bean.get("mappedProperty", "Second Key"));
            assertEquals("mappedProperty(Third Key)",
                         "New Third Value",
                         (String) bean.get("mappedProperty", "Third Key"));
            assertNull("mappedProperty(Fourth Key",
                       bean.get("mappedProperty", "Fourth Key"));

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

            BeanUtils.populate(bean, map);

            final TestBean nested = (TestBean) bean.get("nested");
            assertTrue("booleanProperty is false",
                       !nested.getBooleanProperty());
            assertTrue("booleanSecond is true",
                       nested.isBooleanSecond());
            assertEquals("doubleProperty is 432.0",
                         432.0,
                         nested.getDoubleProperty(),
                         0.005);
            assertEquals("floatProperty is 123.0",
                         (float) 123.0,
                         nested.getFloatProperty(),
                         (float) 0.005);
            assertEquals("intProperty is 543",
                         543, nested.getIntProperty());
            assertEquals("longProperty is 321",
                         321, nested.getLongProperty());
            assertEquals("shortProperty is 654",
                         (short) 654, nested.getShortProperty());
            assertEquals("stringProperty is \"This is a string\"",
                         "This is a string",
                         nested.getStringProperty());

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

            bean.set("nullProperty", "non-null value");

            final HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("booleanProperty", "false");
            // booleanSecond is left at true
            map.put("doubleProperty", "432.0");
            // floatProperty is left at 123.0
            map.put("intProperty", "543");
            // longProperty is left at 321
            map.put("nullProperty", null);
            map.put("shortProperty", "654");
            // stringProperty is left at "This is a string"

            BeanUtils.populate(bean, map);

            final Boolean booleanProperty = (Boolean) bean.get("booleanProperty");
            assertTrue("booleanProperty is false", !booleanProperty.booleanValue());
            final Boolean booleanSecond = (Boolean) bean.get("booleanSecond");
            assertTrue("booleanSecond is true", booleanSecond.booleanValue());
            final Double doubleProperty = (Double) bean.get("doubleProperty");
            assertEquals("doubleProperty is 432.0",
                         432.0, doubleProperty.doubleValue(), 0.005);
            final Float floatProperty = (Float) bean.get("floatProperty");
            assertEquals("floatProperty is 123.0",
                         (float) 123.0, floatProperty.floatValue(),
                         (float) 0.005);
            final Integer intProperty = (Integer) bean.get("intProperty");
            assertEquals("intProperty is 543",
                         543, intProperty.intValue());
            final Long longProperty = (Long) bean.get("longProperty");
            assertEquals("longProperty is 321",
                         321, longProperty.longValue());
            assertNull("nullProperty is null", bean.get("nullProperty"));
            final Short shortProperty = (Short) bean.get("shortProperty");
            assertEquals("shortProperty is 654",
                         (short) 654, shortProperty.shortValue());
            assertEquals("stringProperty is \"This is a string\"",
                         "This is a string",
                         (String) bean.get("stringProperty"));

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
        assertEquals(1,((Integer) bean.get("intProperty")).intValue());
        BeanUtils.setProperty(bean,"stringProperty", new Integer(1));
        assertEquals(1, Integer.parseInt((String) bean.get("stringProperty")));

    }


    /**
     * Test setting a null property value.
     */
    public void testSetPropertyNull() throws Exception {

        bean.set("nullProperty", "non-null value");
        BeanUtils.setProperty(bean, "nullProperty", null);
        assertNull("nullProperty is null", bean.get("nullProperty"));

    }


    /**
     * Test narrowing and widening conversions on byte.
     */
    public void testCopyPropertyByte() throws Exception {

        BeanUtils.setProperty(bean, "byteProperty", new Byte((byte) 123));
        assertEquals((byte) 123, ((Byte) bean.get("byteProperty")).byteValue());
/*
        BeanUtils.setProperty(bean, "byteProperty", new Double((double) 123));
        assertEquals((byte) 123, ((Byte) bean.get("byteProperty")).byteValue());
        BeanUtils.setProperty(bean, "byteProperty", new Float((float) 123));
        assertEquals((byte) 123, ((Byte) bean.get("byteProperty")).byteValue());
*/
        BeanUtils.setProperty(bean, "byteProperty", new Integer(123));
        assertEquals((byte) 123, ((Byte) bean.get("byteProperty")).byteValue());
        BeanUtils.setProperty(bean, "byteProperty", new Long(123));
        assertEquals((byte) 123, ((Byte) bean.get("byteProperty")).byteValue());
        BeanUtils.setProperty(bean, "byteProperty", new Short((short) 123));
        assertEquals((byte) 123, ((Byte) bean.get("byteProperty")).byteValue());

    }


    /**
     * Test narrowing and widening conversions on double.
     */
    public void testCopyPropertyDouble() throws Exception {

        BeanUtils.setProperty(bean, "doubleProperty", new Byte((byte) 123));
        assertEquals(123, ((Double) bean.get("doubleProperty")).doubleValue(), 0.005);
        BeanUtils.setProperty(bean, "doubleProperty", new Double(123));
        assertEquals(123, ((Double) bean.get("doubleProperty")).doubleValue(), 0.005);
        BeanUtils.setProperty(bean, "doubleProperty", new Float(123));
        assertEquals(123, ((Double) bean.get("doubleProperty")).doubleValue(), 0.005);
        BeanUtils.setProperty(bean, "doubleProperty", new Integer(123));
        assertEquals(123, ((Double) bean.get("doubleProperty")).doubleValue(), 0.005);
        BeanUtils.setProperty(bean, "doubleProperty", new Long(123));
        assertEquals(123, ((Double) bean.get("doubleProperty")).doubleValue(), 0.005);
        BeanUtils.setProperty(bean, "doubleProperty", new Short((short) 123));
        assertEquals(123, ((Double) bean.get("doubleProperty")).doubleValue(), 0.005);

    }


    /**
     * Test narrowing and widening conversions on float.
     */
    public void testCopyPropertyFloat() throws Exception {

        BeanUtils.setProperty(bean, "floatProperty", new Byte((byte) 123));
        assertEquals(123, ((Float) bean.get("floatProperty")).floatValue(), 0.005);
        BeanUtils.setProperty(bean, "floatProperty", new Double(123));
        assertEquals(123, ((Float) bean.get("floatProperty")).floatValue(), 0.005);
        BeanUtils.setProperty(bean, "floatProperty", new Float(123));
        assertEquals(123, ((Float) bean.get("floatProperty")).floatValue(), 0.005);
        BeanUtils.setProperty(bean, "floatProperty", new Integer(123));
        assertEquals(123, ((Float) bean.get("floatProperty")).floatValue(), 0.005);
        BeanUtils.setProperty(bean, "floatProperty", new Long(123));
        assertEquals(123, ((Float) bean.get("floatProperty")).floatValue(), 0.005);
        BeanUtils.setProperty(bean, "floatProperty", new Short((short) 123));
        assertEquals(123, ((Float) bean.get("floatProperty")).floatValue(), 0.005);

    }


    /**
     * Test narrowing and widening conversions on int.
     */
    public void testCopyPropertyInteger() throws Exception {

        BeanUtils.setProperty(bean, "longProperty", new Byte((byte) 123));
        assertEquals(123, ((Integer) bean.get("intProperty")).intValue());
/*
        BeanUtils.setProperty(bean, "longProperty", new Double((double) 123));
        assertEquals((int) 123, ((Integer) bean.get("intProperty")).intValue());
        BeanUtils.setProperty(bean, "longProperty", new Float((float) 123));
        assertEquals((int) 123, ((Integer) bean.get("intProperty")).intValue());
*/
        BeanUtils.setProperty(bean, "longProperty", new Integer(123));
        assertEquals(123, ((Integer) bean.get("intProperty")).intValue());
        BeanUtils.setProperty(bean, "longProperty", new Long(123));
        assertEquals(123, ((Integer) bean.get("intProperty")).intValue());
        BeanUtils.setProperty(bean, "longProperty", new Short((short) 123));
        assertEquals(123, ((Integer) bean.get("intProperty")).intValue());

    }


    /**
     * Test narrowing and widening conversions on long.
     */
    public void testCopyPropertyLong() throws Exception {

        BeanUtils.setProperty(bean, "longProperty", new Byte((byte) 123));
        assertEquals(123, ((Long) bean.get("longProperty")).longValue());
/*
        BeanUtils.setProperty(bean, "longProperty", new Double((double) 123));
        assertEquals((long) 123, ((Long) bean.get("longProperty")).longValue());
        BeanUtils.setProperty(bean, "longProperty", new Float((float) 123));
        assertEquals((long) 123, ((Long) bean.get("longProperty")).longValue());
*/
        BeanUtils.setProperty(bean, "longProperty", new Integer(123));
        assertEquals(123, ((Long) bean.get("longProperty")).longValue());
        BeanUtils.setProperty(bean, "longProperty", new Long(123));
        assertEquals(123, ((Long) bean.get("longProperty")).longValue());
        BeanUtils.setProperty(bean, "longProperty", new Short((short) 123));
        assertEquals(123, ((Long) bean.get("longProperty")).longValue());

    }


    /**
     * Test copying a null property value.
     */
    public void testCopyPropertyNull() throws Exception {

        bean.set("nullProperty", "non-null value");
        BeanUtils.copyProperty(bean, "nullProperty", null);
        assertNull("nullProperty is null", bean.get("nullProperty"));

    }


    /**
     * Test narrowing and widening conversions on short.
     */
    public void testCopyPropertyShort() throws Exception {

        BeanUtils.setProperty(bean, "shortProperty", new Byte((byte) 123));
        assertEquals((short) 123, ((Short) bean.get("shortProperty")).shortValue());
/*
        BeanUtils.setProperty(bean, "shortProperty", new Double((double) 123));
        assertEquals((short) 123, ((Short) bean.get("shortProperty")).shortValue());
        BeanUtils.setProperty(bean, "shortProperty", new Float((float) 123));
        assertEquals((short) 123, ((Short) bean.get("shortProperty")).shortValue());
*/
        BeanUtils.setProperty(bean, "shortProperty", new Integer(123));
        assertEquals((short) 123, ((Short) bean.get("shortProperty")).shortValue());
        BeanUtils.setProperty(bean, "shortProperty", new Long(123));
        assertEquals((short) 123, ((Short) bean.get("shortProperty")).shortValue());
        BeanUtils.setProperty(bean, "shortProperty", new Short((short) 123));
        assertEquals((short) 123, ((Short) bean.get("shortProperty")).shortValue());

    }


    /**
     * Test copying a property using a nested indexed array expression,
     * with and without conversions.
     */
    public void testCopyPropertyNestedIndexedArray() throws Exception {

        final int origArray[] = { 0, 10, 20, 30, 40};
        final int intArray[] = { 0, 0, 0 };
        ((TestBean) bean.get("nested")).setIntArray(intArray);
        final int intChanged[] = { 0, 0, 0 };

        // No conversion required
        BeanUtils.copyProperty(bean, "nested.intArray[1]", new Integer(1));
        checkIntArray((int[]) bean.get("intArray"), origArray);
        intChanged[1] = 1;
        checkIntArray(((TestBean) bean.get("nested")).getIntArray(),
                      intChanged);

        // Widening conversion required
        BeanUtils.copyProperty(bean, "nested.intArray[1]", new Byte((byte) 2));
        checkIntArray((int[]) bean.get("intArray"), origArray);
        intChanged[1] = 2;
        checkIntArray(((TestBean) bean.get("nested")).getIntArray(),
                      intChanged);

        // Narrowing conversion required
        BeanUtils.copyProperty(bean, "nested.intArray[1]", new Long(3));
        checkIntArray((int[]) bean.get("intArray"), origArray);
        intChanged[1] = 3;
        checkIntArray(((TestBean) bean.get("nested")).getIntArray(),
                      intChanged);

        // String conversion required
        BeanUtils.copyProperty(bean, "nested.intArray[1]", "4");
        checkIntArray((int[]) bean.get("intArray"), origArray);
        intChanged[1] = 4;
        checkIntArray(((TestBean) bean.get("nested")).getIntArray(),
                      intChanged);

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
        checkMap((Map<?, ?>) bean.get("mapProperty"), origMap);
        changedMap.put("Second Key", "New Second Value");
        checkMap(((TestBean) bean.get("nested")).getMapProperty(), changedMap);

    }


    /**
     * Test copying a property using a nested simple expression, with and
     * without conversions.
     */
    public void testCopyPropertyNestedSimple() throws Exception {

        bean.set("intProperty", new Integer(0));
        nested.setIntProperty(0);

        // No conversion required
        BeanUtils.copyProperty(bean, "nested.intProperty", new Integer(1));
        assertEquals(0, ((Integer) bean.get("intProperty")).intValue());
        assertEquals(1, nested.getIntProperty());

        // Widening conversion required
        BeanUtils.copyProperty(bean, "nested.intProperty", new Byte((byte) 2));
        assertEquals(0, ((Integer) bean.get("intProperty")).intValue());
        assertEquals(2, nested.getIntProperty());

        // Narrowing conversion required
        BeanUtils.copyProperty(bean, "nested.intProperty", new Long(3));
        assertEquals(0, ((Integer) bean.get("intProperty")).intValue());
        assertEquals(3, nested.getIntProperty());

        // String conversion required
        BeanUtils.copyProperty(bean, "nested.intProperty", "4");
        assertEquals(0, ((Integer) bean.get("intProperty")).intValue());
        assertEquals(4, nested.getIntProperty());

    }


    // ------------------------------------------------------ Protected Methods


    // Ensure that the nested intArray matches the specified values
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


    /**
     * Create and return a <code>DynaClass</code> instance for our test
     * <code>DynaBean</code>.
     */
    protected static DynaClass createDynaClass() {

        final int intArray[] = new int[0];
        final String stringArray[] = new String[0];

        final DynaClass dynaClass = new BasicDynaClass
                ("TestDynaClass", null,
                        new DynaProperty[]{
                            new DynaProperty("booleanProperty", Boolean.TYPE),
                            new DynaProperty("booleanSecond", Boolean.TYPE),
                            new DynaProperty("byteProperty", Byte.TYPE),
                            new DynaProperty("doubleProperty", Double.TYPE),
                            new DynaProperty("dupProperty", stringArray.getClass()),
                            new DynaProperty("floatProperty", Float.TYPE),
                            new DynaProperty("intArray", intArray.getClass()),
                            new DynaProperty("intIndexed", intArray.getClass()),
                            new DynaProperty("intProperty", Integer.TYPE),
                            new DynaProperty("listIndexed", List.class),
                            new DynaProperty("longProperty", Long.TYPE),
                            new DynaProperty("mapProperty", Map.class),
                            new DynaProperty("mappedProperty", Map.class),
                            new DynaProperty("mappedIntProperty", Map.class),
                            new DynaProperty("nested", TestBean.class),
                            new DynaProperty("nullProperty", String.class),
                            new DynaProperty("shortProperty", Short.TYPE),
                            new DynaProperty("stringArray", stringArray.getClass()),
                            new DynaProperty("stringIndexed", stringArray.getClass()),
                            new DynaProperty("stringProperty", String.class),
                        });
        return (dynaClass);

    }


}
