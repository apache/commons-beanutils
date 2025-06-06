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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * Test Case for the <code>BasicDynaBean</code> implementation class. These tests were based on the ones in <code>PropertyUtilsTestCase</code> because the two
 * classes provide similar levels of functionality.
 * </p>
 *
 */
public class BasicDynaBeanTestCase {

    /**
     * The set of property names we expect to have returned when calling <code>getDynaProperties()</code>. You should update this list when new properties are
     * added to TestBean.
     */
    protected final static String[] properties = { "booleanProperty", "booleanSecond", "doubleProperty", "floatProperty", "intArray", "intIndexed",
            "intProperty", "listIndexed", "longProperty", "mappedProperty", "mappedIntProperty", "nullProperty", "shortProperty", "stringArray",
            "stringIndexed", "stringProperty", };
    /**
     * The basic test bean for each test.
     */
    protected DynaBean bean;

    /**
     * Create and return a <code>DynaClass</code> instance for our test <code>DynaBean</code>.
     */
    protected DynaClass createDynaClass() {
        final int intArray[] = {};
        final String stringArray[] = {};
        return new BasicDynaClass("TestDynaClass", null, new DynaProperty[] { new DynaProperty("booleanProperty", Boolean.TYPE),
                new DynaProperty("booleanSecond", Boolean.TYPE), new DynaProperty("doubleProperty", Double.TYPE), new DynaProperty("floatProperty", Float.TYPE),
                new DynaProperty("intArray", intArray.getClass()), new DynaProperty("intIndexed", intArray.getClass()),
                new DynaProperty("intProperty", Integer.TYPE), new DynaProperty("listIndexed", List.class), new DynaProperty("longProperty", Long.TYPE),
                new DynaProperty("mappedProperty", Map.class), new DynaProperty("mappedIntProperty", Map.class), new DynaProperty("nullProperty", String.class),
                new DynaProperty("shortProperty", Short.TYPE), new DynaProperty("stringArray", stringArray.getClass()),
                new DynaProperty("stringIndexed", stringArray.getClass()), new DynaProperty("stringProperty", String.class), });
    }

    /**
     * Set up instance variables required by this test case.
     */
    @BeforeEach
    public void setUp() throws Exception {
        // Instantiate a new DynaBean instance
        final DynaClass dynaClass = createDynaClass();
        bean = dynaClass.newInstance();
        // Initialize the DynaBean's property values (like TestBean)
        bean.set("booleanProperty", Boolean.valueOf(true));
        bean.set("booleanSecond", Boolean.valueOf(true));
        bean.set("doubleProperty", Double.valueOf(321.0));
        bean.set("floatProperty", Float.valueOf((float) 123.0));
        final int intArray[] = { 0, 10, 20, 30, 40 };
        bean.set("intArray", intArray);
        final int intIndexed[] = { 0, 10, 20, 30, 40 };
        bean.set("intIndexed", intIndexed);
        bean.set("intProperty", Integer.valueOf(123));
        final List<String> listIndexed = new ArrayList<>();
        listIndexed.add("String 0");
        listIndexed.add("String 1");
        listIndexed.add("String 2");
        listIndexed.add("String 3");
        listIndexed.add("String 4");
        bean.set("listIndexed", listIndexed);
        bean.set("longProperty", Long.valueOf(321));
        final HashMap<String, String> mappedProperty = new HashMap<>();
        mappedProperty.put("First Key", "First Value");
        mappedProperty.put("Second Key", "Second Value");
        bean.set("mappedProperty", mappedProperty);
        final HashMap<String, Integer> mappedIntProperty = new HashMap<>();
        mappedIntProperty.put("One", Integer.valueOf(1));
        mappedIntProperty.put("Two", Integer.valueOf(2));
        bean.set("mappedIntProperty", mappedIntProperty);
        // Property "nullProperty" is not initialized, so it should return null
        bean.set("shortProperty", Short.valueOf((short) 987));
        final String stringArray[] = { "String 0", "String 1", "String 2", "String 3", "String 4" };
        bean.set("stringArray", stringArray);
        final String stringIndexed[] = { "String 0", "String 1", "String 2", "String 3", "String 4" };
        bean.set("stringIndexed", stringIndexed);
        bean.set("stringProperty", "This is a string");
    }

    /**
     * Tear down instance variables required by this test case.
     */
    @AfterEach
    public void tearDown() {
        bean = null;
    }

    /**
     * Corner cases on getDynaProperty invalid arguments.
     */
    @Test
    public void testGetDescriptorArguments() {
        try {
            final DynaProperty descriptor = bean.getDynaClass().getDynaProperty("unknown");
            assertNull(descriptor, "Unknown property descriptor should be null");
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
     * Base for testGetDescriptorXxxxx() series of tests.
     *
     * @param name Name of the property to be retrieved
     * @param type Expected class type of this property
     */
    protected void testGetDescriptorBase(final String name, final Class<?> type) {
        try {
            final DynaProperty descriptor = bean.getDynaClass().getDynaProperty(name);
            assertNotNull(descriptor, "Got descriptor");
            assertEquals(type, descriptor.getType(), "Got correct type");
        } catch (final Throwable t) {
            fail("Threw an exception: " + t);
        }
    }

    /**
     * Positive getDynaProperty on property <code>booleanProperty</code>.
     */
    @Test
    public void testGetDescriptorBoolean() {
        testGetDescriptorBase("booleanProperty", Boolean.TYPE);
    }

    /**
     * Positive getDynaProperty on property <code>doubleProperty</code>.
     */
    @Test
    public void testGetDescriptorDouble() {
        testGetDescriptorBase("doubleProperty", Double.TYPE);
    }

    /**
     * Positive getDynaProperty on property <code>floatProperty</code>.
     */
    @Test
    public void testGetDescriptorFloat() {
        testGetDescriptorBase("floatProperty", Float.TYPE);
    }

    /**
     * Positive getDynaProperty on property <code>intProperty</code>.
     */
    @Test
    public void testGetDescriptorInt() {
        testGetDescriptorBase("intProperty", Integer.TYPE);
    }

    /**
     * Positive getDynaProperty on property <code>longProperty</code>.
     */
    @Test
    public void testGetDescriptorLong() {
        testGetDescriptorBase("longProperty", Long.TYPE);
    }

    /**
     * Positive test for getDynaPropertys(). Each property name listed in <code>properties</code> should be returned exactly once.
     */
    @Test
    public void testGetDescriptors() {
        final DynaProperty pd[] = bean.getDynaClass().getDynaProperties();
        assertNotNull(pd, "Got descriptors");
        final int count[] = new int[properties.length];
        for (final DynaProperty element : pd) {
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
     * Positive getDynaProperty on property <code>booleanSecond</code> that uses an "is" method as the getter.
     */
    @Test
    public void testGetDescriptorSecond() {
        testGetDescriptorBase("booleanSecond", Boolean.TYPE);
    }

    /**
     * Positive getDynaProperty on property <code>shortProperty</code>.
     */
    @Test
    public void testGetDescriptorShort() {
        testGetDescriptorBase("shortProperty", Short.TYPE);
    }

    /**
     * Positive getDynaProperty on property <code>stringProperty</code>.
     */
    @Test
    public void testGetDescriptorString() {
        testGetDescriptorBase("stringProperty", String.class);
    }

    /**
     * Corner cases on getIndexedProperty invalid arguments.
     */
    @Test
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
    @Test
    public void testGetIndexedValues() {
        Object value = null;
        for (int i = 0; i < 5; i++) {
            try {
                value = bean.get("intArray", i);
                assertNotNull(value, "intArray returned value " + i);
                assertTrue(value instanceof Integer, "intArray returned Integer " + i);
                assertEquals(i * 10, ((Integer) value).intValue(), "intArray returned correct " + i);
            } catch (final Throwable t) {
                fail("intArray " + i + " threw " + t);
            }
            try {
                value = bean.get("intIndexed", i);
                assertNotNull(value, "intIndexed returned value " + i);
                assertTrue(value instanceof Integer, "intIndexed returned Integer " + i);
                assertEquals(i * 10, ((Integer) value).intValue(), "intIndexed returned correct " + i);
            } catch (final Throwable t) {
                fail("intIndexed " + i + " threw " + t);
            }
            try {
                value = bean.get("listIndexed", i);
                assertNotNull(value, "listIndexed returned value " + i);
                assertTrue(value instanceof String, "list returned String " + i);
                assertEquals("String " + i, (String) value, "listIndexed returned correct " + i);
            } catch (final Throwable t) {
                fail("listIndexed " + i + " threw " + t);
            }
            try {
                value = bean.get("stringArray", i);
                assertNotNull(value, "stringArray returned value " + i);
                assertTrue(value instanceof String, "stringArray returned String " + i);
                assertEquals("String " + i, (String) value, "stringArray returned correct " + i);
            } catch (final Throwable t) {
                fail("stringArray " + i + " threw " + t);
            }
            try {
                value = bean.get("stringIndexed", i);
                assertNotNull(value, "stringIndexed returned value " + i);
                assertTrue(value instanceof String, "stringIndexed returned String " + i);
                assertEquals("String " + i, (String) value, "stringIndexed returned correct " + i);
            } catch (final Throwable t) {
                fail("stringIndexed " + i + " threw " + t);
            }
        }
    }

    /**
     * Corner cases on getMappedProperty invalid arguments.
     */
    @Test
    public void testGetMappedArguments() {
        try {
            final Object value = bean.get("mappedProperty", "unknown");
            assertNull(value, "Should not return a value");
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of returning null");
        }
    }

    /**
     * Positive and negative tests on getMappedProperty valid arguments.
     */
    @Test
    public void testGetMappedValues() {
        Object value = null;
        try {
            value = bean.get("mappedProperty", "First Key");
            assertEquals("First Value", value, "Can find first value");
        } catch (final Throwable t) {
            fail("Finding first value threw " + t);
        }
        try {
            value = bean.get("mappedProperty", "Second Key");
            assertEquals("Second Value", value, "Can find second value");
        } catch (final Throwable t) {
            fail("Finding second value threw " + t);
        }
        try {
            value = bean.get("mappedProperty", "Third Key");
            assertNull(value, "Can not find third value");
        } catch (final Throwable t) {
            fail("Finding third value threw " + t);
        }
    }

    /**
     * Corner cases on getSimpleProperty invalid arguments.
     */
    @Test
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
    @Test
    public void testGetSimpleBoolean() {
        try {
            final Object value = bean.get("booleanProperty");
            assertNotNull(value, "Got a value");
            assertTrue(value instanceof Boolean, "Got correct type");
            assertTrue(((Boolean) value).booleanValue(), "Got correct value");
        } catch (final Throwable e) {
            fail("Exception: " + e);
        }
    }

    /**
     * Test getSimpleProperty on a double property.
     */
    @Test
    public void testGetSimpleDouble() {
        try {
            final Object value = bean.get("doubleProperty");
            assertNotNull(value, "Got a value");
            assertTrue(value instanceof Double, "Got correct type");
            assertEquals(((Double) value).doubleValue(), 321.0, 0.005, "Got correct value");
        } catch (final Throwable t) {
            fail("Exception: " + t);
        }
    }

    /**
     * Test getSimpleProperty on a float property.
     */
    @Test
    public void testGetSimpleFloat() {
        try {
            final Object value = bean.get("floatProperty");
            assertNotNull(value, "Got a value");
            assertTrue(value instanceof Float, "Got correct type");
            assertEquals(((Float) value).floatValue(), (float) 123.0, (float) 0.005, "Got correct value");
        } catch (final Throwable t) {
            fail("Exception: " + t);
        }
    }

    /**
     * Test getSimpleProperty on a int property.
     */
    @Test
    public void testGetSimpleInt() {
        try {
            final Object value = bean.get("intProperty");
            assertNotNull(value, "Got a value");
            assertTrue(value instanceof Integer, "Got correct type");
            assertEquals(((Integer) value).intValue(), 123, "Got correct value");
        } catch (final Throwable t) {
            fail("Exception: " + t);
        }
    }

    /**
     * Test getSimpleProperty on a long property.
     */
    @Test
    public void testGetSimpleLong() {
        try {
            final Object value = bean.get("longProperty");
            assertNotNull(value, "Got a value");
            assertTrue(value instanceof Long, "Got correct type");
            assertEquals(((Long) value).longValue(), 321, "Got correct value");
        } catch (final Throwable t) {
            fail("Exception: " + t);
        }
    }

    /**
     * Test getSimpleProperty on a short property.
     */
    @Test
    public void testGetSimpleShort() {
        try {
            final Object value = bean.get("shortProperty");
            assertNotNull(value, "Got a value");
            assertTrue(value instanceof Short, "Got correct type");
            assertEquals(((Short) value).shortValue(), (short) 987, "Got correct value");
        } catch (final Throwable t) {
            fail("Exception: " + t);
        }
    }

    /**
     * Test getSimpleProperty on a String property.
     */
    @Test
    public void testGetSimpleString() {
        try {
            final Object value = bean.get("stringProperty");
            assertNotNull(value, "Got a value");
            assertTrue(value instanceof String, "Got correct type");
            assertEquals((String) value, "This is a string", "Got correct value");
        } catch (final Throwable t) {
            fail("Exception: " + t);
        }
    }

    /**
     * Test <code>contains()</code> method for mapped properties.
     */
    @Test
    public void testMappedContains() {
        try {
            assertTrue(bean.contains("mappedProperty", "First Key"), "Can see first key");
        } catch (final Throwable t) {
            fail("Exception: " + t);
        }
        try {
            assertTrue(!bean.contains("mappedProperty", "Unknown Key"), "Can not see unknown key");
        } catch (final Throwable t) {
            fail("Exception: " + t);
        }
    }

    /**
     * Test <code>remove()</code> method for mapped properties.
     */
    @Test
    public void testMappedRemove() {
        try {
            assertTrue(bean.contains("mappedProperty", "First Key"), "Can see first key");
            bean.remove("mappedProperty", "First Key");
            assertTrue(!bean.contains("mappedProperty", "First Key"), "Can not see first key");
        } catch (final Throwable t) {
            fail("Exception: " + t);
        }
        try {
            assertTrue(!bean.contains("mappedProperty", "Unknown Key"), "Can not see unknown key");
            bean.remove("mappedProperty", "Unknown Key");
            assertTrue(!bean.contains("mappedProperty", "Unknown Key"), "Can not see unknown key");
        } catch (final Throwable t) {
            fail("Exception: " + t);
        }
    }

    /**
     * Test serialization and deserialization.
     */
    @Test
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
            final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
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
    @Test
    public void testSetIndexedArguments() {
        try {
            bean.set("intArray", -1, Integer.valueOf(0));
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
    @Test
    public void testSetIndexedValues() {
        Object value = null;
        try {
            bean.set("intArray", 0, Integer.valueOf(1));
            value = bean.get("intArray", 0);
            assertNotNull(value, "Returned new value 0");
            assertTrue(value instanceof Integer, "Returned Integer new value 0");
            assertEquals(1, ((Integer) value).intValue(), "Returned correct new value 0");
        } catch (final Throwable t) {
            fail("Threw " + t);
        }
        try {
            bean.set("intIndexed", 1, Integer.valueOf(11));
            value = bean.get("intIndexed", 1);
            assertNotNull(value, "Returned new value 1");
            assertTrue(value instanceof Integer, "Returned Integer new value 1");
            assertEquals(11, ((Integer) value).intValue(), "Returned correct new value 1");
        } catch (final Throwable t) {
            fail("Threw " + t);
        }
        try {
            bean.set("listIndexed", 2, "New Value 2");
            value = bean.get("listIndexed", 2);
            assertNotNull(value, "Returned new value 2");
            assertTrue(value instanceof String, "Returned String new value 2");
            assertEquals("New Value 2", (String) value, "Returned correct new value 2");
        } catch (final Throwable t) {
            fail("Threw " + t);
        }
        try {
            bean.set("stringArray", 3, "New Value 3");
            value = bean.get("stringArray", 3);
            assertNotNull(value, "Returned new value 3");
            assertTrue(value instanceof String, "Returned String new value 3");
            assertEquals("New Value 3", (String) value, "Returned correct new value 3");
        } catch (final Throwable t) {
            fail("Threw " + t);
        }
        try {
            bean.set("stringIndexed", 4, "New Value 4");
            value = bean.get("stringIndexed", 4);
            assertNotNull(value, "Returned new value 4");
            assertTrue(value instanceof String, "Returned String new value 4");
            assertEquals("New Value 4", (String) value, "Returned correct new value 4");
        } catch (final Throwable t) {
            fail("Threw " + t);
        }
    }

    /**
     * Positive and negative tests on setMappedProperty valid arguments.
     */
    @Test
    public void testSetMappedValues() {
        try {
            bean.set("mappedProperty", "First Key", "New First Value");
            assertEquals("New First Value", (String) bean.get("mappedProperty", "First Key"), "Can replace old value");
        } catch (final Throwable t) {
            fail("Finding fourth value threw " + t);
        }
        try {
            bean.set("mappedProperty", "Fourth Key", "Fourth Value");
            assertEquals("Fourth Value", (String) bean.get("mappedProperty", "Fourth Key"), "Can set new value");
        } catch (final Throwable t) {
            fail("Finding fourth value threw " + t);
        }
    }

    /**
     * Test setSimpleProperty on a boolean property.
     */
    @Test
    public void testSetSimpleBoolean() {
        try {
            final boolean oldValue = ((Boolean) bean.get("booleanProperty")).booleanValue();
            final boolean newValue = !oldValue;
            bean.set("booleanProperty", Boolean.valueOf(newValue));
            assertTrue(newValue == ((Boolean) bean.get("booleanProperty")).booleanValue(), "Matched new value");
        } catch (final Throwable e) {
            fail("Exception: " + e);
        }
    }

    /**
     * Test setSimpleProperty on a double property.
     */
    @Test
    public void testSetSimpleDouble() {
        try {
            final double oldValue = ((Double) bean.get("doubleProperty")).doubleValue();
            final double newValue = oldValue + 1.0;
            bean.set("doubleProperty", Double.valueOf(newValue));
            assertEquals(newValue, ((Double) bean.get("doubleProperty")).doubleValue(), 0.005, "Matched new value");
        } catch (final Throwable e) {
            fail("Exception: " + e);
        }
    }

    /**
     * Test setSimpleProperty on a float property.
     */
    @Test
    public void testSetSimpleFloat() {
        try {
            final float oldValue = ((Float) bean.get("floatProperty")).floatValue();
            final float newValue = oldValue + (float) 1.0;
            bean.set("floatProperty", Float.valueOf(newValue));
            assertEquals(newValue, ((Float) bean.get("floatProperty")).floatValue(), (float) 0.005, "Matched new value");
        } catch (final Throwable e) {
            fail("Exception: " + e);
        }
    }

    /**
     * Test setSimpleProperty on a int property.
     */
    @Test
    public void testSetSimpleInt() {
        try {
            final int oldValue = ((Integer) bean.get("intProperty")).intValue();
            final int newValue = oldValue + 1;
            bean.set("intProperty", Integer.valueOf(newValue));
            assertEquals(newValue, ((Integer) bean.get("intProperty")).intValue(), "Matched new value");
        } catch (final Throwable e) {
            fail("Exception: " + e);
        }
    }

    /**
     * Test setSimpleProperty on a long property.
     */
    @Test
    public void testSetSimpleLong() {
        try {
            final long oldValue = ((Long) bean.get("longProperty")).longValue();
            final long newValue = oldValue + 1;
            bean.set("longProperty", Long.valueOf(newValue));
            assertEquals(newValue, ((Long) bean.get("longProperty")).longValue(), "Matched new value");
        } catch (final Throwable e) {
            fail("Exception: " + e);
        }
    }

    /**
     * Test setSimpleProperty on a short property.
     */
    @Test
    public void testSetSimpleShort() {
        try {
            final short oldValue = ((Short) bean.get("shortProperty")).shortValue();
            final short newValue = (short) (oldValue + 1);
            bean.set("shortProperty", Short.valueOf(newValue));
            assertEquals(newValue, ((Short) bean.get("shortProperty")).shortValue(), "Matched new value");
        } catch (final Throwable e) {
            fail("Exception: " + e);
        }
    }

    /**
     * Test setSimpleProperty on a String property.
     */
    @Test
    public void testSetSimpleString() {
        try {
            final String oldValue = (String) bean.get("stringProperty");
            final String newValue = oldValue + " Extra Value";
            bean.set("stringProperty", newValue);
            assertEquals(newValue, (String) bean.get("stringProperty"), "Matched new value");
        } catch (final Throwable e) {
            fail("Exception: " + e);
        }
    }
}
