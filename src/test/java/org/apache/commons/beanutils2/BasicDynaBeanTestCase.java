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

package org.apache.commons.beanutils2;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * <p>
 * Test Case for the {@code BasicDynaBean} implementation class. These tests were based on the ones in {@code PropertyUtilsTestCase} because the two classes
 * provide similar levels of functionality.
 * </p>
 */
public class BasicDynaBeanTestCase extends TestCase {

    /**
     * The set of property names we expect to have returned when calling {@code getDynaProperties()}. You should update this list when new properties are added
     * to TestBean.
     */
    protected final static String[] properties = { "booleanProperty", "booleanSecond", "doubleProperty", "floatProperty", "intArray", "intIndexed",
            "intProperty", "listIndexed", "longProperty", "mappedProperty", "mappedIntProperty", "nullProperty", "shortProperty", "stringArray",
            "stringIndexed", "stringProperty", };

    /**
     * Creates the tests included in this test suite.
     */
    public static Test suite() {
        return new TestSuite(BasicDynaBeanTestCase.class);
    }

    /**
     * The basic test bean for each test.
     */
    protected DynaBean bean;

    /**
     * Constructs a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public BasicDynaBeanTestCase(final String name) {
        super(name);
    }

    /**
     * Create and return a {@code DynaClass} instance for our test {@code DynaBean}.
     */
    protected DynaClass createDynaClass() {
        final int[] intArray = {};
        final String[] stringArray = {};
        final DynaClass dynaClass = new BasicDynaClass("TestDynaClass", null, new DynaProperty[] { new DynaProperty("booleanProperty", Boolean.TYPE),
                new DynaProperty("booleanSecond", Boolean.TYPE), new DynaProperty("doubleProperty", Double.TYPE), new DynaProperty("floatProperty", Float.TYPE),
                new DynaProperty("intArray", intArray.getClass()), new DynaProperty("intIndexed", intArray.getClass()),
                new DynaProperty("intProperty", Integer.TYPE), new DynaProperty("listIndexed", List.class), new DynaProperty("longProperty", Long.TYPE),
                new DynaProperty("mappedProperty", Map.class), new DynaProperty("mappedIntProperty", Map.class), new DynaProperty("nullProperty", String.class),
                new DynaProperty("shortProperty", Short.TYPE), new DynaProperty("stringArray", stringArray.getClass()),
                new DynaProperty("stringIndexed", stringArray.getClass()), new DynaProperty("stringProperty", String.class), });
        return dynaClass;
    }

    /**
     * Sets up instance variables required by this test case.
     */
    @Override
    public void setUp() throws Exception {
        // Instantiate a new DynaBean instance
        final DynaClass dynaClass = createDynaClass();
        bean = dynaClass.newInstance();
        // Initialize the DynaBean's property values (like TestBean)
        bean.set("booleanProperty", Boolean.valueOf(true));
        bean.set("booleanSecond", Boolean.valueOf(true));
        bean.set("doubleProperty", Double.valueOf(321.0));
        bean.set("floatProperty", Float.valueOf((float) 123.0));
        final int[] intArray = { 0, 10, 20, 30, 40 };
        bean.set("intArray", intArray);
        final int[] intIndexed = { 0, 10, 20, 30, 40 };
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
        final String[] stringArray = { "String 0", "String 1", "String 2", "String 3", "String 4" };
        bean.set("stringArray", stringArray);
        final String[] stringIndexed = { "String 0", "String 1", "String 2", "String 3", "String 4" };
        bean.set("stringIndexed", stringIndexed);
        bean.set("stringProperty", "This is a string");
    }

    /**
     * Tear down instance variables required by this test case.
     */
    @Override
    public void tearDown() {

        bean = null;

    }

    /**
     * Corner cases on getDynaProperty invalid arguments.
     */
    public void testGetDescriptorArguments() {
        assertNull(bean.getDynaClass().getDynaProperty("unknown"));
        assertThrows(NullPointerException.class, () -> bean.getDynaClass().getDynaProperty(null));
    }

    /**
     * Base for testGetDescriptorXxxxx() series of tests.
     *
     * @param name Name of the property to be retrieved
     * @param type Expected class type of this property
     */
    protected void testGetDescriptorBase(final String name, final Class<?> type) {
        final DynaProperty descriptor = bean.getDynaClass().getDynaProperty(name);
        assertNotNull("Got descriptor", descriptor);
        assertEquals("Got correct type", type, descriptor.getType());
    }

    /**
     * Positive getDynaProperty on property {@code booleanProperty}.
     */
    public void testGetDescriptorBoolean() {
        testGetDescriptorBase("booleanProperty", Boolean.TYPE);
    }

    /**
     * Positive getDynaProperty on property {@code doubleProperty}.
     */
    public void testGetDescriptorDouble() {
        testGetDescriptorBase("doubleProperty", Double.TYPE);
    }

    /**
     * Positive getDynaProperty on property {@code floatProperty}.
     */
    public void testGetDescriptorFloat() {
        testGetDescriptorBase("floatProperty", Float.TYPE);
    }

    /**
     * Positive getDynaProperty on property {@code intProperty}.
     */
    public void testGetDescriptorInt() {
        testGetDescriptorBase("intProperty", Integer.TYPE);
    }

    /**
     * Positive getDynaProperty on property {@code longProperty}.
     */
    public void testGetDescriptorLong() {
        testGetDescriptorBase("longProperty", Long.TYPE);
    }

    /**
     * Positive test for getDynaProperties(). Each property name listed in {@code properties} should be returned exactly once.
     */
    public void testGetDescriptors() {
        final DynaProperty[] pd = bean.getDynaClass().getDynaProperties();
        assertNotNull("Got descriptors", pd);
        final int[] count = new int[properties.length];
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
     * Positive getDynaProperty on property {@code booleanSecond} that uses an "is" method as the getter.
     */
    public void testGetDescriptorSecond() {
        testGetDescriptorBase("booleanSecond", Boolean.TYPE);
    }

    /**
     * Positive getDynaProperty on property {@code shortProperty}.
     */
    public void testGetDescriptorShort() {
        testGetDescriptorBase("shortProperty", Short.TYPE);
    }

    /**
     * Positive getDynaProperty on property {@code stringProperty}.
     */
    public void testGetDescriptorString() {
        testGetDescriptorBase("stringProperty", String.class);
    }

    /**
     * Corner cases on getIndexedProperty invalid arguments.
     */
    public void testGetIndexedArguments() {
        assertThrows(IndexOutOfBoundsException.class, () -> bean.get("intArray", -1));
    }

    /**
     * Positive and negative tests on getIndexedProperty valid arguments.
     */
    public void testGetIndexedValues() {
        Object value = null;
        for (int i = 0; i < 5; i++) {
            value = bean.get("intArray", i);
            assertNotNull("intArray returned value " + i, value);
            assertTrue("intArray returned Integer " + i, value instanceof Integer);
            assertEquals("intArray returned correct " + i, i * 10, ((Integer) value).intValue());
            value = bean.get("intIndexed", i);
            assertNotNull("intIndexed returned value " + i, value);
            assertTrue("intIndexed returned Integer " + i, value instanceof Integer);
            assertEquals("intIndexed returned correct " + i, i * 10, ((Integer) value).intValue());
            value = bean.get("listIndexed", i);
            assertNotNull("listIndexed returned value " + i, value);
            assertTrue("list returned String " + i, value instanceof String);
            assertEquals("listIndexed returned correct " + i, "String " + i, (String) value);
            value = bean.get("stringArray", i);
            assertNotNull("stringArray returned value " + i, value);
            assertTrue("stringArray returned String " + i, value instanceof String);
            assertEquals("stringArray returned correct " + i, "String " + i, (String) value);
            value = bean.get("stringIndexed", i);
            assertNotNull("stringIndexed returned value " + i, value);
            assertTrue("stringIndexed returned String " + i, value instanceof String);
            assertEquals("stringIndexed returned correct " + i, "String " + i, (String) value);
        }
    }

    /**
     * Corner cases on getMappedProperty invalid arguments.
     */
    public void testGetMappedArguments() {
        final Object value = bean.get("mappedProperty", "unknown");
        assertNull("Should not return a value", value);
    }

    /**
     * Positive and negative tests on getMappedProperty valid arguments.
     */
    public void testGetMappedValues() {
        Object value = null;
        value = bean.get("mappedProperty", "First Key");
        assertEquals("Can find first value", "First Value", value);
        value = bean.get("mappedProperty", "Second Key");
        assertEquals("Can find second value", "Second Value", value);
        value = bean.get("mappedProperty", "Third Key");
        assertNull("Can not find third value", value);
    }

    /**
     * Corner cases on getSimpleProperty invalid arguments.
     */
    public void testGetSimpleArguments() {
        assertThrows(NullPointerException.class, () -> bean.get(null));
    }

    /**
     * Test getSimpleProperty on a boolean property.
     */
    public void testGetSimpleBoolean() {
        final Object value = bean.get("booleanProperty");
        assertNotNull("Got a value", value);
        assertTrue("Got correct type", value instanceof Boolean);
        assertTrue("Got correct value", (Boolean) value);
    }

    /**
     * Test getSimpleProperty on a double property.
     */
    public void testGetSimpleDouble() {
        final Object value = bean.get("doubleProperty");
        assertNotNull("Got a value", value);
        assertTrue("Got correct type", value instanceof Double);
        assertEquals("Got correct value", ((Double) value).doubleValue(), 321.0, 0.005);
    }

    /**
     * Test getSimpleProperty on a float property.
     */
    public void testGetSimpleFloat() {
        final Object value = bean.get("floatProperty");
        assertNotNull("Got a value", value);
        assertTrue("Got correct type", value instanceof Float);
        assertEquals("Got correct value", ((Float) value).floatValue(), (float) 123.0, (float) 0.005);
    }

    /**
     * Test getSimpleProperty on a int property.
     */
    public void testGetSimpleInt() {
        final Object value = bean.get("intProperty");
        assertNotNull("Got a value", value);
        assertTrue("Got correct type", value instanceof Integer);
        assertEquals("Got correct value", ((Integer) value).intValue(), 123);
    }

    /**
     * Test getSimpleProperty on a long property.
     */
    public void testGetSimpleLong() {
        final Object value = bean.get("longProperty");
        assertNotNull("Got a value", value);
        assertTrue("Got correct type", value instanceof Long);
        assertEquals("Got correct value", ((Long) value).longValue(), 321);
    }

    /**
     * Test getSimpleProperty on a short property.
     */
    public void testGetSimpleShort() {
        final Object value = bean.get("shortProperty");
        assertNotNull("Got a value", value);
        assertTrue("Got correct type", value instanceof Short);
        assertEquals("Got correct value", ((Short) value).shortValue(), (short) 987);
    }

    /**
     * Test getSimpleProperty on a String property.
     */
    public void testGetSimpleString() {
        final Object value = bean.get("stringProperty");
        assertNotNull("Got a value", value);
        assertTrue("Got correct type", value instanceof String);
        assertEquals("Got correct value", (String) value, "This is a string");
    }

    /**
     * Test {@code contains()} method for mapped properties.
     */
    public void testMappedContains() {
        assertTrue("Can see first key", bean.contains("mappedProperty", "First Key"));
        assertTrue("Can not see unknown key", !bean.contains("mappedProperty", "Unknown Key"));
    }

    /**
     * Test {@code remove()} method for mapped properties.
     */
    public void testMappedRemove() {
        assertTrue("Can see first key", bean.contains("mappedProperty", "First Key"));
        bean.remove("mappedProperty", "First Key");
        assertTrue("Can not see first key", !bean.contains("mappedProperty", "First Key"));
        assertTrue("Can not see unknown key", !bean.contains("mappedProperty", "Unknown Key"));
        bean.remove("mappedProperty", "Unknown Key");
        assertTrue("Can not see unknown key", !bean.contains("mappedProperty", "Unknown Key"));
    }

    /**
     * Test serialization and deserialization.
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     */
    public void testSerialization() throws Exception {
        // Serialize the test bean
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(bean);
        oos.flush();
        oos.close();
        // Deserialize the test bean
        bean = null;
        final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        final ObjectInputStream ois = new ObjectInputStream(bais);
        bean = (DynaBean) ois.readObject();
        bais.close();
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
        bean = bean.getDynaClass().newInstance();
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
        assertThrows(IndexOutOfBoundsException.class, () -> bean.set("intArray", -1, Integer.valueOf(0)));
    }

    /**
     * Positive and negative tests on setIndexedProperty valid arguments.
     */
    public void testSetIndexedValues() {
        Object value = null;
        bean.set("intArray", 0, Integer.valueOf(1));
        value = bean.get("intArray", 0);
        assertNotNull("Returned new value 0", value);
        assertTrue("Returned Integer new value 0", value instanceof Integer);
        assertEquals("Returned correct new value 0", 1, ((Integer) value).intValue());
        bean.set("intIndexed", 1, Integer.valueOf(11));
        value = bean.get("intIndexed", 1);
        assertNotNull("Returned new value 1", value);
        assertTrue("Returned Integer new value 1", value instanceof Integer);
        assertEquals("Returned correct new value 1", 11, ((Integer) value).intValue());
        bean.set("listIndexed", 2, "New Value 2");
        value = bean.get("listIndexed", 2);
        assertNotNull("Returned new value 2", value);
        assertTrue("Returned String new value 2", value instanceof String);
        assertEquals("Returned correct new value 2", "New Value 2", (String) value);
        bean.set("stringArray", 3, "New Value 3");
        value = bean.get("stringArray", 3);
        assertNotNull("Returned new value 3", value);
        assertTrue("Returned String new value 3", value instanceof String);
        assertEquals("Returned correct new value 3", "New Value 3", (String) value);
        bean.set("stringIndexed", 4, "New Value 4");
        value = bean.get("stringIndexed", 4);
        assertNotNull("Returned new value 4", value);
        assertTrue("Returned String new value 4", value instanceof String);
        assertEquals("Returned correct new value 4", "New Value 4", (String) value);
    }

    /**
     * Positive and negative tests on setMappedProperty valid arguments.
     */
    public void testSetMappedValues() {
        bean.set("mappedProperty", "First Key", "New First Value");
        assertEquals("Can replace old value", "New First Value", (String) bean.get("mappedProperty", "First Key"));
        bean.set("mappedProperty", "Fourth Key", "Fourth Value");
        assertEquals("Can set new value", "Fourth Value", (String) bean.get("mappedProperty", "Fourth Key"));
    }

    /**
     * Test setSimpleProperty on a boolean property.
     */
    public void testSetSimpleBoolean() {
        final boolean oldValue = ((Boolean) bean.get("booleanProperty")).booleanValue();
        final boolean newValue = !oldValue;
        bean.set("booleanProperty", Boolean.valueOf(newValue));
        assertEquals("Matched new value", newValue, ((Boolean) bean.get("booleanProperty")).booleanValue());
    }

    /**
     * Test setSimpleProperty on a double property.
     */
    public void testSetSimpleDouble() {
        final double oldValue = ((Double) bean.get("doubleProperty")).doubleValue();
        final double newValue = oldValue + 1.0;
        bean.set("doubleProperty", Double.valueOf(newValue));
        assertEquals("Matched new value", newValue, ((Double) bean.get("doubleProperty")).doubleValue(), 0.005);
    }

    /**
     * Test setSimpleProperty on a float property.
     */
    public void testSetSimpleFloat() {
        final float oldValue = ((Float) bean.get("floatProperty")).floatValue();
        final float newValue = oldValue + (float) 1.0;
        bean.set("floatProperty", Float.valueOf(newValue));
        assertEquals("Matched new value", newValue, ((Float) bean.get("floatProperty")).floatValue(), (float) 0.005);
    }

    /**
     * Test setSimpleProperty on a int property.
     */
    public void testSetSimpleInt() {
        final int oldValue = ((Integer) bean.get("intProperty")).intValue();
        final int newValue = oldValue + 1;
        bean.set("intProperty", Integer.valueOf(newValue));
        assertEquals("Matched new value", newValue, ((Integer) bean.get("intProperty")).intValue());
    }

    /**
     * Test setSimpleProperty on a long property.
     */
    public void testSetSimpleLong() {
        final long oldValue = ((Long) bean.get("longProperty")).longValue();
        final long newValue = oldValue + 1;
        bean.set("longProperty", Long.valueOf(newValue));
        assertEquals("Matched new value", newValue, ((Long) bean.get("longProperty")).longValue());
    }

    /**
     * Test setSimpleProperty on a short property.
     */
    public void testSetSimpleShort() {
        final short oldValue = ((Short) bean.get("shortProperty")).shortValue();
        final short newValue = (short) (oldValue + 1);
        bean.set("shortProperty", Short.valueOf(newValue));
        assertEquals("Matched new value", newValue, ((Short) bean.get("shortProperty")).shortValue());
    }

    /**
     * Test setSimpleProperty on a String property.
     */
    public void testSetSimpleString() {
        final String oldValue = (String) bean.get("stringProperty");
        final String newValue = oldValue + " Extra Value";
        bean.set("stringProperty", newValue);
        assertEquals("Matched new value", newValue, (String) bean.get("stringProperty"));
    }

}
