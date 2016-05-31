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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * <p>Test Case for the <code>BasicDynaBean</code> implementation class.
 * These tests were based on the ones in <code>PropertyUtilsTestCase</code>
 * because the two classes provide similar levels of functionality.</p>
 *
 * @version $Id$
 */

public class BasicDynaBeanTestCase extends TestCase {


    // ---------------------------------------------------- Instance Variables


    /**
     * The basic test bean for each test.
     */
    protected DynaBean bean = null;


    /**
     * The set of property names we expect to have returned when calling
     * <code>getDynaProperties()</code>.  You should update this list
     * when new properties are added to TestBean.
     */
    protected final static String[] properties = {
        "booleanProperty",
        "booleanSecond",
        "doubleProperty",
        "floatProperty",
        "intArray",
        "intIndexed",
        "intProperty",
        "listIndexed",
        "longProperty",
        "mappedProperty",
        "mappedIntProperty",
        "nullProperty",
        "shortProperty",
        "stringArray",
        "stringIndexed",
        "stringProperty",
    };


    // ---------------------------------------------------------- Constructors


    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public BasicDynaBeanTestCase(final String name) {

        super(name);

    }


    // -------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    @Override
    public void setUp() throws Exception {

        // Instantiate a new DynaBean instance
        final DynaClass dynaClass = createDynaClass();
        bean = dynaClass.newInstance();

        // Initialize the DynaBean's property values (like TestBean)
        bean.set("booleanProperty", new Boolean(true));
        bean.set("booleanSecond", new Boolean(true));
        bean.set("doubleProperty", new Double(321.0));
        bean.set("floatProperty", new Float((float) 123.0));
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
        final HashMap<String, String> mappedProperty = new HashMap<String, String>();
        mappedProperty.put("First Key", "First Value");
        mappedProperty.put("Second Key", "Second Value");
        bean.set("mappedProperty", mappedProperty);
        final HashMap<String, Integer> mappedIntProperty = new HashMap<String, Integer>();
        mappedIntProperty.put("One", new Integer(1));
        mappedIntProperty.put("Two", new Integer(2));
        bean.set("mappedIntProperty", mappedIntProperty);
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

        return (new TestSuite(BasicDynaBeanTestCase.class));

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
     * Corner cases on getDynaProperty invalid arguments.
     */
    public void testGetDescriptorArguments() {

        try {
            final DynaProperty descriptor =
                    bean.getDynaClass().getDynaProperty("unknown");
            assertNull("Unknown property descriptor should be null",
                    descriptor);
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of returning null");
        }

        try {
            bean.getDynaClass().getDynaProperty(null);
            fail("Should throw IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException");
        }

    }


    /**
     * Positive getDynaProperty on property <code>booleanProperty</code>.
     */
    public void testGetDescriptorBoolean() {

        testGetDescriptorBase("booleanProperty", Boolean.TYPE);

    }


    /**
     * Positive getDynaProperty on property <code>doubleProperty</code>.
     */
    public void testGetDescriptorDouble() {

        testGetDescriptorBase("doubleProperty", Double.TYPE);

    }


    /**
     * Positive getDynaProperty on property <code>floatProperty</code>.
     */
    public void testGetDescriptorFloat() {

        testGetDescriptorBase("floatProperty", Float.TYPE);

    }


    /**
     * Positive getDynaProperty on property <code>intProperty</code>.
     */
    public void testGetDescriptorInt() {

        testGetDescriptorBase("intProperty", Integer.TYPE);

    }


    /**
     * Positive getDynaProperty on property <code>longProperty</code>.
     */
    public void testGetDescriptorLong() {

        testGetDescriptorBase("longProperty", Long.TYPE);

    }


    /**
     * Positive getDynaProperty on property <code>booleanSecond</code>
     * that uses an "is" method as the getter.
     */
    public void testGetDescriptorSecond() {

        testGetDescriptorBase("booleanSecond", Boolean.TYPE);

    }


    /**
     * Positive getDynaProperty on property <code>shortProperty</code>.
     */
    public void testGetDescriptorShort() {

        testGetDescriptorBase("shortProperty", Short.TYPE);

    }


    /**
     * Positive getDynaProperty on property <code>stringProperty</code>.
     */
    public void testGetDescriptorString() {

        testGetDescriptorBase("stringProperty", String.class);

    }


    /**
     * Positive test for getDynaPropertys().  Each property name
     * listed in <code>properties</code> should be returned exactly once.
     */
    public void testGetDescriptors() {

        final DynaProperty pd[] = bean.getDynaClass().getDynaProperties();
        assertNotNull("Got descriptors", pd);
        final int count[] = new int[properties.length];
        for (DynaProperty element : pd) {
            final String name = element.getName();
            for (int j = 0; j < properties.length; j++) {
                if (name.equals(properties[j])) {
                    count[j]++;
                }
            }
        }
        for (int j = 0; j < properties.length; j++) {
            if (count[j] < 0) {
                fail("Missing property " + properties[j]);
            } else if (count[j] > 1) {
                fail("Duplicate property " + properties[j]);
            }
        }

    }


    /**
     * Corner cases on getIndexedProperty invalid arguments.
     */
    public void testGetIndexedArguments() {

        try {
            bean.get("intArray", -1);
            fail("Should throw IndexOutOfBoundsException");
        } catch (final IndexOutOfBoundsException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IndexOutOfBoundsException");
        }


    }


    /**
     * Positive and negative tests on getIndexedProperty valid arguments.
     */
    public void testGetIndexedValues() {

        Object value = null;

        for (int i = 0; i < 5; i++) {

            try {
                value = bean.get("intArray", i);
                assertNotNull("intArray returned value " + i, value);
                assertTrue("intArray returned Integer " + i,
                        value instanceof Integer);
                assertEquals("intArray returned correct " + i, i * 10,
                        ((Integer) value).intValue());
            } catch (final Throwable t) {
                fail("intArray " + i + " threw " + t);
            }

            try {
                value = bean.get("intIndexed", i);
                assertNotNull("intIndexed returned value " + i, value);
                assertTrue("intIndexed returned Integer " + i,
                        value instanceof Integer);
                assertEquals("intIndexed returned correct " + i, i * 10,
                        ((Integer) value).intValue());
            } catch (final Throwable t) {
                fail("intIndexed " + i + " threw " + t);
            }

            try {
                value = bean.get("listIndexed", i);
                assertNotNull("listIndexed returned value " + i, value);
                assertTrue("list returned String " + i,
                        value instanceof String);
                assertEquals("listIndexed returned correct " + i,
                        "String " + i, (String) value);
            } catch (final Throwable t) {
                fail("listIndexed " + i + " threw " + t);
            }

            try {
                value = bean.get("stringArray", i);
                assertNotNull("stringArray returned value " + i, value);
                assertTrue("stringArray returned String " + i,
                        value instanceof String);
                assertEquals("stringArray returned correct " + i,
                        "String " + i, (String) value);
            } catch (final Throwable t) {
                fail("stringArray " + i + " threw " + t);
            }

            try {
                value = bean.get("stringIndexed", i);
                assertNotNull("stringIndexed returned value " + i, value);
                assertTrue("stringIndexed returned String " + i,
                        value instanceof String);
                assertEquals("stringIndexed returned correct " + i,
                        "String " + i, (String) value);
            } catch (final Throwable t) {
                fail("stringIndexed " + i + " threw " + t);
            }

        }


    }


    /**
     * Corner cases on getMappedProperty invalid arguments.
     */
    public void testGetMappedArguments() {


        try {
            final Object value = bean.get("mappedProperty", "unknown");
            assertNull("Should not return a value", value);
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of returning null");
        }


    }


    /**
     * Positive and negative tests on getMappedProperty valid arguments.
     */
    public void testGetMappedValues() {

        Object value = null;

        try {
            value = bean.get("mappedProperty", "First Key");
            assertEquals("Can find first value", "First Value", value);
        } catch (final Throwable t) {
            fail("Finding first value threw " + t);
        }

        try {
            value = bean.get("mappedProperty", "Second Key");
            assertEquals("Can find second value", "Second Value", value);
        } catch (final Throwable t) {
            fail("Finding second value threw " + t);
        }

        try {
            value = bean.get("mappedProperty", "Third Key");
            assertNull("Can not find third value", value);
        } catch (final Throwable t) {
            fail("Finding third value threw " + t);
        }

    }


    /**
     * Corner cases on getSimpleProperty invalid arguments.
     */
    public void testGetSimpleArguments() {

        try {
            bean.get(null);
            fail("Should throw IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException");
        }

    }


    /**
     * Test getSimpleProperty on a boolean property.
     */
    public void testGetSimpleBoolean() {

        try {
            final Object value = bean.get("booleanProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Boolean));
            assertTrue("Got correct value",
                    ((Boolean) value).booleanValue() == true);
        } catch (final Throwable e) {
            fail("Exception: " + e);
        }

    }


    /**
     * Test getSimpleProperty on a double property.
     */
    public void testGetSimpleDouble() {

        try {
            final Object value = bean.get("doubleProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Double));
            assertEquals("Got correct value",
                    ((Double) value).doubleValue(),
                    321.0, 0.005);
        } catch (final Throwable t) {
            fail("Exception: " + t);
        }

    }


    /**
     * Test getSimpleProperty on a float property.
     */
    public void testGetSimpleFloat() {

        try {
            final Object value = bean.get("floatProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Float));
            assertEquals("Got correct value",
                    ((Float) value).floatValue(),
                    (float) 123.0,
                    (float) 0.005);
        } catch (final Throwable t) {
            fail("Exception: " + t);
        }

    }


    /**
     * Test getSimpleProperty on a int property.
     */
    public void testGetSimpleInt() {

        try {
            final Object value = bean.get("intProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Integer));
            assertEquals("Got correct value",
                    ((Integer) value).intValue(),
                    123);
        } catch (final Throwable t) {
            fail("Exception: " + t);
        }

    }


    /**
     * Test getSimpleProperty on a long property.
     */
    public void testGetSimpleLong() {

        try {
            final Object value = bean.get("longProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Long));
            assertEquals("Got correct value",
                    ((Long) value).longValue(),
                    321);
        } catch (final Throwable t) {
            fail("Exception: " + t);
        }

    }


    /**
     * Test getSimpleProperty on a short property.
     */
    public void testGetSimpleShort() {

        try {
            final Object value = bean.get("shortProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Short));
            assertEquals("Got correct value",
                    ((Short) value).shortValue(),
                    (short) 987);
        } catch (final Throwable t) {
            fail("Exception: " + t);
        }

    }


    /**
     * Test getSimpleProperty on a String property.
     */
    public void testGetSimpleString() {

        try {
            final Object value = bean.get("stringProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof String));
            assertEquals("Got correct value",
                    (String) value,
                    "This is a string");
        } catch (final Throwable t) {
            fail("Exception: " + t);
        }

    }


    /**
     * Test <code>contains()</code> method for mapped properties.
     */
    public void testMappedContains() {

        try {
            assertTrue("Can see first key",
                    bean.contains("mappedProperty", "First Key"));
        } catch (final Throwable t) {
            fail("Exception: " + t);
        }


        try {
            assertTrue("Can not see unknown key",
                    !bean.contains("mappedProperty", "Unknown Key"));
        } catch (final Throwable t) {
            fail("Exception: " + t);
        }

    }


    /**
     * Test <code>remove()</code> method for mapped properties.
     */
    public void testMappedRemove() {

        try {
            assertTrue("Can see first key",
                    bean.contains("mappedProperty", "First Key"));
            bean.remove("mappedProperty", "First Key");
            assertTrue("Can not see first key",
                    !bean.contains("mappedProperty", "First Key"));
        } catch (final Throwable t) {
            fail("Exception: " + t);
        }

        try {
            assertTrue("Can not see unknown key",
                    !bean.contains("mappedProperty", "Unknown Key"));
            bean.remove("mappedProperty", "Unknown Key");
            assertTrue("Can not see unknown key",
                    !bean.contains("mappedProperty", "Unknown Key"));
        } catch (final Throwable t) {
            fail("Exception: " + t);
        }

    }


    /**
     * Test serialization and deserialization.
     */
    public void testSerialization() {

        // Serialize the test bean
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            final ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(bean);
            oos.flush();
            oos.close();
        } catch (final Exception e) {
            fail("Exception during serialization: " + e);
        }

        // Deserialize the test bean
        try {
            bean = null;
            final ByteArrayInputStream bais =
                new ByteArrayInputStream(baos.toByteArray());
            final ObjectInputStream ois = new ObjectInputStream(bais);
            bean = (DynaBean) ois.readObject();
            bais.close();
        } catch (final Exception e) {
            fail("Exception during deserialization: " + e);
        }

        // Confirm property values
        testGetDescriptorArguments();
        testGetDescriptorBoolean();
        testGetDescriptorDouble();
        testGetDescriptorFloat();
        testGetDescriptorInt();
        testGetDescriptorLong();
        testGetDescriptorSecond();
        testGetDescriptorShort();
        testGetDescriptorString();
        testGetDescriptors();
        testGetIndexedArguments();
        testGetIndexedValues();
        testGetMappedArguments();
        testGetMappedValues();
        testGetSimpleArguments();
        testGetSimpleBoolean();
        testGetSimpleDouble();
        testGetSimpleFloat();
        testGetSimpleInt();
        testGetSimpleLong();
        testGetSimpleShort();
        testGetSimpleString();
        testMappedContains();
        testMappedRemove();

        // Ensure that we can create a new instance of the same DynaClass
        try {
            bean = bean.getDynaClass().newInstance();
        } catch (final Exception e) {
            fail("Exception creating new instance: " + e);
        }
        testGetDescriptorArguments();
        testGetDescriptorBoolean();
        testGetDescriptorDouble();
        testGetDescriptorFloat();
        testGetDescriptorInt();
        testGetDescriptorLong();
        testGetDescriptorSecond();
        testGetDescriptorShort();
        testGetDescriptorString();
        testGetDescriptors();

    }


    /**
     * Corner cases on setIndexedProperty invalid arguments.
     */
    public void testSetIndexedArguments() {

        try {
            bean.set("intArray", -1, new Integer(0));
            fail("Should throw IndexOutOfBoundsException");
        } catch (final IndexOutOfBoundsException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IndexOutOfBoundsException");
        }

    }


    /**
     * Positive and negative tests on setIndexedProperty valid arguments.
     */
    public void testSetIndexedValues() {

        Object value = null;

        try {
            bean.set("intArray", 0, new Integer(1));
            value = bean.get("intArray", 0);
            assertNotNull("Returned new value 0", value);
            assertTrue("Returned Integer new value 0",
                    value instanceof Integer);
            assertEquals("Returned correct new value 0", 1,
                    ((Integer) value).intValue());
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            bean.set("intIndexed", 1, new Integer(11));
            value = bean.get("intIndexed", 1);
            assertNotNull("Returned new value 1", value);
            assertTrue("Returned Integer new value 1",
                    value instanceof Integer);
            assertEquals("Returned correct new value 1", 11,
                    ((Integer) value).intValue());
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            bean.set("listIndexed", 2, "New Value 2");
            value = bean.get("listIndexed", 2);
            assertNotNull("Returned new value 2", value);
            assertTrue("Returned String new value 2",
                    value instanceof String);
            assertEquals("Returned correct new value 2", "New Value 2",
                    (String) value);
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            bean.set("stringArray", 3, "New Value 3");
            value = bean.get("stringArray", 3);
            assertNotNull("Returned new value 3", value);
            assertTrue("Returned String new value 3",
                    value instanceof String);
            assertEquals("Returned correct new value 3", "New Value 3",
                    (String) value);
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            bean.set("stringIndexed", 4, "New Value 4");
            value = bean.get("stringIndexed", 4);
            assertNotNull("Returned new value 4", value);
            assertTrue("Returned String new value 4",
                    value instanceof String);
            assertEquals("Returned correct new value 4", "New Value 4",
                    (String) value);
        } catch (final Throwable t) {
            fail("Threw " + t);
        }


    }


    /**
     * Positive and negative tests on setMappedProperty valid arguments.
     */
    public void testSetMappedValues() {

        try {
            bean.set("mappedProperty", "First Key", "New First Value");
            assertEquals("Can replace old value",
                    "New First Value",
                    (String) bean.get("mappedProperty", "First Key"));
        } catch (final Throwable t) {
            fail("Finding fourth value threw " + t);
        }

        try {
            bean.set("mappedProperty", "Fourth Key", "Fourth Value");
            assertEquals("Can set new value",
                    "Fourth Value",
                    (String) bean.get("mappedProperty", "Fourth Key"));
        } catch (final Throwable t) {
            fail("Finding fourth value threw " + t);
        }


    }


    /**
     * Test setSimpleProperty on a boolean property.
     */
    public void testSetSimpleBoolean() {

        try {
            final boolean oldValue =
                    ((Boolean) bean.get("booleanProperty")).booleanValue();
            final boolean newValue = !oldValue;
            bean.set("booleanProperty", new Boolean(newValue));
            assertTrue("Matched new value",
                    newValue ==
                    ((Boolean) bean.get("booleanProperty")).booleanValue());
        } catch (final Throwable e) {
            fail("Exception: " + e);
        }

    }


    /**
     * Test setSimpleProperty on a double property.
     */
    public void testSetSimpleDouble() {

        try {
            final double oldValue =
                    ((Double) bean.get("doubleProperty")).doubleValue();
            final double newValue = oldValue + 1.0;
            bean.set("doubleProperty", new Double(newValue));
            assertEquals("Matched new value",
                    newValue,
                    ((Double) bean.get("doubleProperty")).doubleValue(),
                    0.005);
        } catch (final Throwable e) {
            fail("Exception: " + e);
        }

    }


    /**
     * Test setSimpleProperty on a float property.
     */
    public void testSetSimpleFloat() {

        try {
            final float oldValue =
                    ((Float) bean.get("floatProperty")).floatValue();
            final float newValue = oldValue + (float) 1.0;
            bean.set("floatProperty", new Float(newValue));
            assertEquals("Matched new value",
                    newValue,
                    ((Float) bean.get("floatProperty")).floatValue(),
                    (float) 0.005);
        } catch (final Throwable e) {
            fail("Exception: " + e);
        }

    }


    /**
     * Test setSimpleProperty on a int property.
     */
    public void testSetSimpleInt() {

        try {
            final int oldValue =
                    ((Integer) bean.get("intProperty")).intValue();
            final int newValue = oldValue + 1;
            bean.set("intProperty", new Integer(newValue));
            assertEquals("Matched new value",
                    newValue,
                    ((Integer) bean.get("intProperty")).intValue());
        } catch (final Throwable e) {
            fail("Exception: " + e);
        }

    }


    /**
     * Test setSimpleProperty on a long property.
     */
    public void testSetSimpleLong() {

        try {
            final long oldValue =
                    ((Long) bean.get("longProperty")).longValue();
            final long newValue = oldValue + 1;
            bean.set("longProperty", new Long(newValue));
            assertEquals("Matched new value",
                    newValue,
                    ((Long) bean.get("longProperty")).longValue());
        } catch (final Throwable e) {
            fail("Exception: " + e);
        }

    }


    /**
     * Test setSimpleProperty on a short property.
     */
    public void testSetSimpleShort() {

        try {
            final short oldValue =
                    ((Short) bean.get("shortProperty")).shortValue();
            final short newValue = (short) (oldValue + 1);
            bean.set("shortProperty", new Short(newValue));
            assertEquals("Matched new value",
                    newValue,
                    ((Short) bean.get("shortProperty")).shortValue());
        } catch (final Throwable e) {
            fail("Exception: " + e);
        }

    }


    /**
     * Test setSimpleProperty on a String property.
     */
    public void testSetSimpleString() {

        try {
            final String oldValue = (String) bean.get("stringProperty");
            final String newValue = oldValue + " Extra Value";
            bean.set("stringProperty", newValue);
            assertEquals("Matched new value",
                    newValue,
                    (String) bean.get("stringProperty"));
        } catch (final Throwable e) {
            fail("Exception: " + e);
        }

    }


    // ------------------------------------------------------ Protected Methods


    /**
     * Create and return a <code>DynaClass</code> instance for our test
     * <code>DynaBean</code>.
     */
    protected DynaClass createDynaClass() {

        final int intArray[] = new int[0];
        final String stringArray[] = new String[0];

        final DynaClass dynaClass = new BasicDynaClass
                ("TestDynaClass", null,
                        new DynaProperty[]{
                            new DynaProperty("booleanProperty", Boolean.TYPE),
                            new DynaProperty("booleanSecond", Boolean.TYPE),
                            new DynaProperty("doubleProperty", Double.TYPE),
                            new DynaProperty("floatProperty", Float.TYPE),
                            new DynaProperty("intArray", intArray.getClass()),
                            new DynaProperty("intIndexed", intArray.getClass()),
                            new DynaProperty("intProperty", Integer.TYPE),
                            new DynaProperty("listIndexed", List.class),
                            new DynaProperty("longProperty", Long.TYPE),
                            new DynaProperty("mappedProperty", Map.class),
                            new DynaProperty("mappedIntProperty", Map.class),
                            new DynaProperty("nullProperty", String.class),
                            new DynaProperty("shortProperty", Short.TYPE),
                            new DynaProperty("stringArray", stringArray.getClass()),
                            new DynaProperty("stringIndexed", stringArray.getClass()),
                            new DynaProperty("stringProperty", String.class),
                        });
        return (dynaClass);

    }


    /**
     * Base for testGetDescriptorXxxxx() series of tests.
     *
     * @param name Name of the property to be retrieved
     * @param type Expected class type of this property
     */
    protected void testGetDescriptorBase(final String name, final Class<?> type) {

        try {
            final DynaProperty descriptor =
                    bean.getDynaClass().getDynaProperty(name);
            assertNotNull("Got descriptor", descriptor);
            assertEquals("Got correct type", type, descriptor.getType());
        } catch (final Throwable t) {
            fail("Threw an exception: " + t);
        }

    }


}
