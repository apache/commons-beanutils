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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils2.priv.PrivateBeanFactory;
import org.apache.commons.beanutils2.priv.PrivateDirect;
import org.apache.commons.beanutils2.priv.PublicSubBean;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * Test Case for the PropertyUtils class. The majority of these tests use instances of the TestBean class, so be sure to update the tests if you change the
 * characteristics of that class.
 * </p>
 *
 * <p>
 * So far, this test case has tests for the following methods of the {@code PropertyUtils} class:
 * </p>
 * <ul>
 * <li>getIndexedProperty(Object,String)</li>
 * <li>getIndexedProperty(Object,String,int)</li>
 * <li>getMappedProperty(Object,String)</li>
 * <li>getMappedProperty(Object,String,String</li>
 * <li>getNestedProperty(Object,String)</li>
 * <li>getPropertyDescriptor(Object,String)</li>
 * <li>getPropertyDescriptors(Object)</li>
 * <li>getPropertyType(Object,String)</li>
 * <li>getSimpleProperty(Object,String)</li>
 * <li>setIndexedProperty(Object,String,Object)</li>
 * <li>setIndexedProperty(Object,String,String,Object)</li>
 * <li>setMappedProperty(Object,String,Object)</li>
 * <li>setMappedProperty(Object,String,String,Object)</li>
 * <li>setNestedProperty(Object,String,Object)</li>
 * <li>setSimpleProperty(Object,String,Object)</li>
 * </ul>
 */
public class PropertyUtilsTestCase {

    /**
     * The fully qualified class name of our private directly implemented interface.
     */
    private static final String PRIVATE_DIRECT_CLASS = "org.apache.commons.beanutils2.priv.PrivateDirect";

    /**
     * The fully qualified class name of our private indirectly implemented interface.
     */
    private static final String PRIVATE_INDIRECT_CLASS = "org.apache.commons.beanutils2.priv.PrivateIndirect";

    /**
     * The fully qualified class name of our test bean class.
     */
    private static final String TEST_BEAN_CLASS = "org.apache.commons.beanutils2.TestBean";

    /**
     * The set of property names we expect to have returned when calling {@code getPropertyDescriptors()}. You should update this list when new properties are
     * added to TestBean.
     */
    protected final static String[] properties = { "booleanProperty", "booleanSecond", "doubleProperty", "dupProperty", "floatProperty", "intArray",
            "intIndexed", "intProperty", "listIndexed", "longProperty", "nested", "nullProperty", "readOnlyProperty", "shortProperty", "stringArray",
            "stringIndexed", "stringProperty", "writeOnlyProperty", };

    /**
     * Finds the descriptor of the name property.
     *
     * @param desc the array with descriptors
     * @return the found descriptor or null
     */
    private static PropertyDescriptor findNameDescriptor(final PropertyDescriptor[] desc) {
        for (final PropertyDescriptor element : desc) {
            if (element.getName().equals("name")) {
                return element;
            }
        }
        return null;
    }

    /**
     * The basic test bean for each test.
     */
    protected TestBean bean;

    /**
     * The "package private subclass" test bean for each test.
     */
    protected TestBeanPackageSubclass beanPackageSubclass;

    /**
     * The test bean for private access tests.
     */
    protected PrivateDirect beanPrivate;

    /**
     * The test bean for private access tests of subclasses.
     */
    protected PrivateDirect beanPrivateSubclass;

    /**
     * The "public subclass" test bean for each test.
     */
    protected TestBeanPublicSubclass beanPublicSubclass;

    /**
     * The set of properties that should be described.
     */
    protected String[] describes = { "booleanProperty", "booleanSecond", "doubleProperty", "floatProperty", "intArray",
            // "intIndexed",
            "intProperty", "listIndexed", "longProperty",
            // "mappedObjects",
            // "mappedProperty",
            // "mappedIntProperty",
            "nested", "nullProperty",
            // "readOnlyProperty",
            "shortProperty", "stringArray",
            // "stringIndexed",
            "stringProperty" };

    /**
     * Returns a single string containing all the keys in the map, sorted in alphabetical order and separated by ", ".
     * <p>
     * If there are no keys, an empty string is returned.
     */
    private String keysToString(final Map<?, ?> map) {
        final Object[] mapKeys = map.keySet().toArray();
        Arrays.sort(mapKeys);
        final StringBuilder buf = new StringBuilder();
        for (int i = 0; i < mapKeys.length; ++i) {
            if (i != 0) {
                buf.append(", ");
            }
            buf.append(mapKeys[i]);
        }
        return buf.toString();
    }

    /**
     * Sets up instance variables required by this test case.
     */
    @BeforeEach
    public void setUp() {

        bean = new TestBean();
        beanPackageSubclass = new TestBeanPackageSubclass();
        beanPrivate = PrivateBeanFactory.create();
        beanPrivateSubclass = PrivateBeanFactory.createSubclass();
        beanPublicSubclass = new TestBeanPublicSubclass();

        final DynaProperty[] properties = { new DynaProperty("stringProperty", String.class), new DynaProperty("nestedBean", TestBean.class),
                new DynaProperty("nullDynaBean", DynaBean.class) };
        final BasicDynaClass dynaClass = new BasicDynaClass("nestedDynaBean", BasicDynaBean.class, properties);
        final BasicDynaBean nestedDynaBean = new BasicDynaBean(dynaClass);
        nestedDynaBean.set("nestedBean", bean);
        bean.setNestedDynaBean(nestedDynaBean);
        PropertyUtils.clearDescriptors();
    }

    /**
     * Tear down instance variables required by this test case.
     */
    @AfterEach
    public void tearDown() {

        bean = null;
        beanPackageSubclass = null;
        beanPrivate = null;
        beanPrivateSubclass = null;
        beanPublicSubclass = null;

        PropertyUtils.resetBeanIntrospectors();
    }

    /**
     * Tries to add a null BeanIntrospector.
     */
    @Test
    public void testAddBeanIntrospectorNull() {
        assertThrows(NullPointerException.class, () -> PropertyUtils.addBeanIntrospector(null));
    }

    /**
     * Test copyProperties() when the origin is a {@code Map}.
     */
    @Test
    public void testCopyPropertiesMap() throws Exception {

        final Map<String, Object> map = new HashMap<>();
        map.put("booleanProperty", Boolean.FALSE);
        map.put("doubleProperty", Double.valueOf(333.0));
        map.put("dupProperty", new String[] { "New 0", "New 1", "New 2" });
        map.put("floatProperty", Float.valueOf((float) 222.0));
        map.put("intArray", new int[] { 0, 100, 200 });
        map.put("intProperty", Integer.valueOf(111));
        map.put("longProperty", Long.valueOf(444));
        map.put("shortProperty", Short.valueOf((short) 555));
        map.put("stringProperty", "New String Property");

        PropertyUtils.copyProperties(bean, map);

        // Scalar properties
        assertEquals(false, bean.getBooleanProperty(), "booleanProperty");
        assertEquals(333.0, bean.getDoubleProperty(), 0.005, "doubleProperty");
        assertEquals((float) 222.0, bean.getFloatProperty(), (float) 0.005, "floatProperty");
        assertEquals(111, bean.getIntProperty(), "intProperty");
        assertEquals(444, bean.getLongProperty(), "longProperty");
        assertEquals((short) 555, bean.getShortProperty(), "shortProperty");
        assertEquals("New String Property", bean.getStringProperty(), "stringProperty");

        // Indexed Properties
        final String[] dupProperty = bean.getDupProperty();
        assertNotNull(dupProperty, "dupProperty present");
        assertEquals(3, dupProperty.length, "dupProperty length");
        assertEquals("New 0", dupProperty[0], "dupProperty[0]");
        assertEquals("New 1", dupProperty[1], "dupProperty[1]");
        assertEquals("New 2", dupProperty[2], "dupProperty[2]");
        final int[] intArray = bean.getIntArray();
        assertNotNull(intArray, "intArray present");
        assertEquals(3, intArray.length, "intArray length");
        assertEquals(0, intArray[0], "intArray[0]");
        assertEquals(100, intArray[1], "intArray[1]");
        assertEquals(200, intArray[2], "intArray[2]");

    }

    /**
     * Tests whether the default introspection mechanism can be replaced by a custom BeanIntrospector.
     */
    @Test
    public void testCustomIntrospection() {
        final PropertyDescriptor[] desc1 = PropertyUtils.getPropertyDescriptors(AlphaBean.class);
        PropertyDescriptor nameDescriptor = findNameDescriptor(desc1);
        assertNotNull(nameDescriptor.getWriteMethod(), "No write method");

        final BeanIntrospector bi = icontext -> {
            final Set<String> names = icontext.propertyNames();
            final PropertyDescriptor[] newDescs = new PropertyDescriptor[names.size()];
            int idx = 0;
            for (final Iterator<String> it = names.iterator(); it.hasNext(); idx++) {
                final String propName = it.next();
                final PropertyDescriptor pd = icontext.getPropertyDescriptor(propName);
                newDescs[idx] = new PropertyDescriptor(pd.getName(), pd.getReadMethod(), null);
            }
            icontext.addPropertyDescriptors(newDescs);
        };
        PropertyUtils.clearDescriptors();
        PropertyUtils.addBeanIntrospector(bi);
        final PropertyDescriptor[] desc2 = PropertyUtils.getPropertyDescriptors(AlphaBean.class);
        assertEquals(desc1.length, desc2.length, "Different number of properties");
        nameDescriptor = findNameDescriptor(desc2);
        assertNull(nameDescriptor.getWriteMethod(), "Got a write method");
        PropertyUtils.removeBeanIntrospector(bi);
    }

    /**
     * Tests whether exceptions during custom introspection are handled.
     */
    @Test
    public void testCustomIntrospectionEx() {
        final BeanIntrospector bi = icontext -> {
            throw new IntrospectionException("TestException");
        };
        PropertyUtils.clearDescriptors();
        PropertyUtils.addBeanIntrospector(bi);
        final PropertyDescriptor[] desc = PropertyUtils.getPropertyDescriptors(AlphaBean.class);
        assertNotNull(findNameDescriptor(desc), "Introspection did not work");
        PropertyUtils.removeBeanIntrospector(bi);
    }

    /**
     * Test the describe() method.
     */
    @Test
    public void testDescribe() throws Exception {

        final Map<String, Object> map = PropertyUtils.describe(bean);

        // Verify existence of all the properties that should be present
        for (final String describe : describes) {
            assertTrue(map.containsKey(describe), "Property '" + describe + "' is present");
        }
        assertFalse(map.containsKey("writeOnlyProperty"), "Property 'writeOnlyProperty' is not present");

        // Verify the values of scalar properties
        assertEquals(Boolean.TRUE, map.get("booleanProperty"), "Value of 'booleanProperty'");
        assertEquals(Double.valueOf(321.0), map.get("doubleProperty"), "Value of 'doubleProperty'");
        assertEquals(Float.valueOf((float) 123.0), map.get("floatProperty"), "Value of 'floatProperty'");
        assertEquals(Integer.valueOf(123), map.get("intProperty"), "Value of 'intProperty'");
        assertEquals(Long.valueOf(321), map.get("longProperty"), "Value of 'longProperty'");
        assertEquals(Short.valueOf((short) 987), map.get("shortProperty"), "Value of 'shortProperty'");
        assertEquals("This is a string", (String) map.get("stringProperty"), "Value of 'stringProperty'");

    }

    /**
     * Test {@link PropertyUtilsBean}'s invoke method throwing an IllegalArgumentException and check that the "cause" has been properly initialized for JDK 1.4+
     * See BEANUTILS-266 for changes and reason for test
     */
    @Test
    public void testExceptionFromInvoke() throws Exception {
        try {
            PropertyUtils.setSimpleProperty(bean, "intProperty", "XXX");
        } catch (final IllegalArgumentException t) {
            final Throwable cause = (Throwable) PropertyUtils.getProperty(t, "cause");
            assertNotNull(cause, "Cause not found");
            assertInstanceOf(IllegalArgumentException.class, cause, "Expected cause to be IllegalArgumentException, but was: " + cause.getClass());
            // JDK 1.6 doesn't have "argument type mismatch" message
            // assertEquals("Check error message", "argument type mismatch", cause.getMessage());
        }
    }

    /**
     * Corner cases on getPropertyDescriptor invalid arguments.
     */
    @Test
    public void testGetDescriptorArguments() {
        assertThrows(NullPointerException.class, () -> PropertyUtils.getPropertyDescriptor(null, "stringProperty"));
        assertThrows(NullPointerException.class, () -> PropertyUtils.getPropertyDescriptor(bean, null));
    }

    /**
     * Base for testGetDescriptorXxxxx() series of tests.
     *
     * @param name  Name of the property to be retrieved
     * @param read  Expected name of the read method (or null)
     * @param write Expected name of the write method (or null)
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void testGetDescriptorBase(final String name, final String read, final String write)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(bean, name);
        if (read == null && write == null) {
            assertNull(pd, "Got descriptor");
            return;
        }
        assertNotNull(pd, "Got descriptor");
        final Method rm = pd.getReadMethod();
        if (read != null) {
            assertNotNull(rm, "Got read method");
            assertEquals(rm.getName(), read, "Got correct read method");
        } else {
            assertNull(rm, "Got read method");
        }
        final Method wm = pd.getWriteMethod();
        if (write != null) {
            assertNotNull(wm, "Got write method");
            assertEquals(wm.getName(), write, "Got correct write method");
        } else {
            assertNull(wm, "Got write method");
        }
    }

    /**
     * Positive getPropertyDescriptor on property {@code booleanProperty}.
     */
    @Test
    public void testGetDescriptorBoolean() throws Exception {
        testGetDescriptorBase("booleanProperty", "getBooleanProperty", "setBooleanProperty");
    }

    /**
     * Positive getPropertyDescriptor on property {@code doubleProperty}.
     */
    @Test
    public void testGetDescriptorDouble() throws Exception {
        testGetDescriptorBase("doubleProperty", "getDoubleProperty", "setDoubleProperty");
    }

    /**
     * Positive getPropertyDescriptor on property {@code floatProperty}.
     */
    @Test
    public void testGetDescriptorFloat() throws Exception {
        testGetDescriptorBase("floatProperty", "getFloatProperty", "setFloatProperty");
    }

    /**
     * Positive getPropertyDescriptor on property {@code intProperty}.
     */
    @Test
    public void testGetDescriptorInt() throws Exception {
        testGetDescriptorBase("intProperty", "getIntProperty", "setIntProperty");
    }

    /**
     * <p>
     * Negative tests on an invalid property with two different boolean getters (which is fine, according to the JavaBeans spec) but a String setter instead of
     * a boolean setter.
     * </p>
     *
     * <p>
     * Although one could logically argue that this combination of method signatures should not identify a property at all, there is a sentence in Section 8.3.1
     * making it clear that the behavior tested for here is correct: "If we find only one of these methods, then we regard it as defining either a read-only or
     * write-only property called <em>&lt;property-name&gt;</em>.
     * </p>
     */
    @Test
    public void testGetDescriptorInvalidBoolean() throws Exception {

        final PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(bean, "invalidBoolean");
        assertNotNull(pd, "invalidBoolean is a property");
        assertNotNull(pd.getReadMethod(), "invalidBoolean has a getter method");
        assertNull(pd.getWriteMethod(), "invalidBoolean has no write method");
        assertTrue(Arrays.asList("isInvalidBoolean", "getInvalidBoolean").contains(pd.getReadMethod().getName()),
                "invalidBoolean getter method is isInvalidBoolean or getInvalidBoolean");
    }

    /**
     * Positive getPropertyDescriptor on property {@code longProperty}.
     */
    @Test
    public void testGetDescriptorLong() throws Exception {
        testGetDescriptorBase("longProperty", "getLongProperty", "setLongProperty");
    }

    /**
     * Test getting mapped descriptor with periods in the key.
     */
    @Test
    public void testGetDescriptorMappedPeriods() throws Exception {

        bean.getMappedIntProperty("xyz"); // initializes mappedIntProperty

        PropertyDescriptor desc;
        final Integer testIntegerValue = Integer.valueOf(1234);

        bean.setMappedIntProperty("key.with.a.dot", testIntegerValue.intValue());
        assertEquals(testIntegerValue, Integer.valueOf(bean.getMappedIntProperty("key.with.a.dot")), "Can retrieve directly");
        desc = PropertyUtils.getPropertyDescriptor(bean, "mappedIntProperty(key.with.a.dot)");
        assertEquals(Integer.TYPE, ((MappedPropertyDescriptor) desc).getMappedPropertyType(), "Check descriptor type (A)");

        bean.setMappedObjects("nested.property", new TestBean(testIntegerValue.intValue()));
        assertEquals(testIntegerValue, Integer.valueOf(((TestBean) bean.getMappedObjects("nested.property")).getIntProperty()), "Can retrieve directly");
        desc = PropertyUtils.getPropertyDescriptor(bean, "mappedObjects(nested.property).intProperty");
        assertEquals(Integer.TYPE, desc.getPropertyType(), "Check descriptor type (B)");
    }

    /**
     * Positive getPropertyDescriptor on property {@code readOnlyProperty}.
     */
    @Test
    public void testGetDescriptorReadOnly() throws Exception {
        testGetDescriptorBase("readOnlyProperty", "getReadOnlyProperty", null);
    }

    /**
     * Positive test for getPropertyDescriptors(). Each property name listed in {@code properties} should be returned exactly once.
     */
    @Test
    public void testGetDescriptors() {

        final PropertyDescriptor[] pd = PropertyUtils.getPropertyDescriptors(bean);
        assertNotNull(pd, "Got descriptors");
        final int[] count = new int[properties.length];
        for (final PropertyDescriptor element : pd) {
            final String name = element.getName();
            for (int j = 0; j < properties.length; j++) {
                if (name.equals(properties[j])) {
                    count[j]++;
                }
            }
        }
        for (int j = 0; j < properties.length; j++) {
            assertFalse(count[j] < 0, "Missing property " + properties[j]);
            assertFalse(count[j] > 1, "Missing property " + properties[j]);
        }

    }

    /**
     * Corner cases on getPropertyDescriptors invalid arguments.
     */
    @Test
    public void testGetDescriptorsArguments() {
        assertThrows(NullPointerException.class, () -> PropertyUtils.getPropertyDescriptors(null));
    }

    /**
     * Positive getPropertyDescriptor on property {@code booleanSecond} that uses an "is" method as the getter.
     */
    @Test
    public void testGetDescriptorSecond() throws Exception {
        testGetDescriptorBase("booleanSecond", "isBooleanSecond", "setBooleanSecond");
    }

    /**
     * Positive getPropertyDescriptor on property {@code shortProperty}.
     */
    @Test
    public void testGetDescriptorShort() throws Exception {
        testGetDescriptorBase("shortProperty", "getShortProperty", "setShortProperty");
    }

    /**
     * Positive getPropertyDescriptor on property {@code stringProperty}.
     */
    @Test
    public void testGetDescriptorString() throws Exception {
        testGetDescriptorBase("stringProperty", "getStringProperty", "setStringProperty");
    }

    /**
     * Negative getPropertyDescriptor on property {@code unknown}.
     */
    @Test
    public void testGetDescriptorUnknown() throws Exception {
        testGetDescriptorBase("unknown", null, null);
    }

    /**
     * Positive getPropertyDescriptor on property {@code writeOnlyProperty}.
     */
    @Test
    public void testGetDescriptorWriteOnly() throws Exception {
        testGetDescriptorBase("writeOnlyProperty", null, "setWriteOnlyProperty");
    }

    /**
     * Corner cases on getIndexedProperty invalid arguments.
     */
    @Test
    public void testGetIndexedArguments() {
        // Use explicit index argument
        assertThrows(NullPointerException.class, () -> PropertyUtils.getIndexedProperty(null, "intArray", 0));
        assertThrows(NullPointerException.class, () -> PropertyUtils.getIndexedProperty(bean, null, 0));
        // Use index expression
        assertThrows(NullPointerException.class, () -> PropertyUtils.getIndexedProperty(null, "intArray[0]"));
        assertThrows(NoSuchMethodException.class, () -> PropertyUtils.getIndexedProperty(bean, "[0]"));
        assertThrows(IllegalArgumentException.class, () -> PropertyUtils.getIndexedProperty(bean, "intArray"));
        // Use explicit index argument
        assertThrows(NullPointerException.class, () -> PropertyUtils.getIndexedProperty(null, "intIndexed", 0));
        assertThrows(NullPointerException.class, () -> PropertyUtils.getIndexedProperty(bean, null, 0));
        // Use index expression
        assertThrows(NullPointerException.class, () -> PropertyUtils.getIndexedProperty(null, "intIndexed[0]"));
        assertThrows(NoSuchMethodException.class, () -> PropertyUtils.getIndexedProperty(bean, "[0]"));
        assertThrows(IllegalArgumentException.class, () -> PropertyUtils.getIndexedProperty(bean, "intIndexed"));
    }

    /**
     * Test getting an indexed value out of a multi-dimensional array
     */
    @Test
    public void testGetIndexedArray() throws Exception {
        final String[] firstArray = { "FIRST-1", "FIRST-2", "FIRST-3" };
        final String[] secondArray = { "SECOND-1", "SECOND-2", "SECOND-3", "SECOND-4" };
        final String[][] mainArray = { firstArray, secondArray };
        final TestBean bean = new TestBean(mainArray);
        assertEquals(firstArray[0], PropertyUtils.getProperty(bean, "string2dArray[0][0]"), "firstArray[0]");
        assertEquals(firstArray[1], PropertyUtils.getProperty(bean, "string2dArray[0][1]"), "firstArray[1]");
        assertEquals(firstArray[2], PropertyUtils.getProperty(bean, "string2dArray[0][2]"), "firstArray[2]");
        assertEquals(secondArray[0], PropertyUtils.getProperty(bean, "string2dArray[1][0]"), "secondArray[0]");
        assertEquals(secondArray[1], PropertyUtils.getProperty(bean, "string2dArray[1][1]"), "secondArray[1]");
        assertEquals(secondArray[2], PropertyUtils.getProperty(bean, "string2dArray[1][2]"), "secondArray[2]");
        assertEquals(secondArray[3], PropertyUtils.getProperty(bean, "string2dArray[1][3]"), "secondArray[3]");
    }

    /**
     * Test getting an indexed value out of List of Lists
     */
    @Test
    public void testGetIndexedList() throws Exception {
        final String[] firstArray = { "FIRST-1", "FIRST-2", "FIRST-3" };
        final String[] secondArray = { "SECOND-1", "SECOND-2", "SECOND-3", "SECOND-4" };
        final List<Object> mainList = new ArrayList<>();
        mainList.add(Arrays.asList(firstArray));
        mainList.add(Arrays.asList(secondArray));
        final TestBean bean = new TestBean(mainList);
        assertEquals(firstArray[0], PropertyUtils.getProperty(bean, "listIndexed[0][0]"), "firstArray[0]");
        assertEquals(firstArray[1], PropertyUtils.getProperty(bean, "listIndexed[0][1]"), "firstArray[1]");
        assertEquals(firstArray[2], PropertyUtils.getProperty(bean, "listIndexed[0][2]"), "firstArray[2]");
        assertEquals(secondArray[0], PropertyUtils.getProperty(bean, "listIndexed[1][0]"), "secondArray[0]");
        assertEquals(secondArray[1], PropertyUtils.getProperty(bean, "listIndexed[1][1]"), "secondArray[1]");
        assertEquals(secondArray[2], PropertyUtils.getProperty(bean, "listIndexed[1][2]"), "secondArray[2]");
        assertEquals(secondArray[3], PropertyUtils.getProperty(bean, "listIndexed[1][3]"), "secondArray[3]");
    }

    /**
     * Test getting a value out of a mapped Map
     */
    @Test
    public void testGetIndexedMap() throws Exception {
        final Map<String, Object> firstMap = new HashMap<>();
        firstMap.put("FIRST-KEY-1", "FIRST-VALUE-1");
        firstMap.put("FIRST-KEY-2", "FIRST-VALUE-2");
        final Map<String, Object> secondMap = new HashMap<>();
        secondMap.put("SECOND-KEY-1", "SECOND-VALUE-1");
        secondMap.put("SECOND-KEY-2", "SECOND-VALUE-2");

        final List<Object> mainList = new ArrayList<>();
        mainList.add(firstMap);
        mainList.add(secondMap);
        final TestBean bean = new TestBean(mainList);
        assertEquals("FIRST-VALUE-1", PropertyUtils.getProperty(bean, "listIndexed[0](FIRST-KEY-1)"), "listIndexed[0](FIRST-KEY-1)");
        assertEquals("FIRST-VALUE-2", PropertyUtils.getProperty(bean, "listIndexed[0](FIRST-KEY-2)"), "listIndexed[0](FIRST-KEY-2)");
        assertEquals("SECOND-VALUE-1", PropertyUtils.getProperty(bean, "listIndexed[1](SECOND-KEY-1)"), "listIndexed[1](SECOND-KEY-1)");
        assertEquals("SECOND-VALUE-2", PropertyUtils.getProperty(bean, "listIndexed[1](SECOND-KEY-2)"), "listIndexed[1](SECOND-KEY-2)");
    }

    /**
     * Positive and negative tests on getIndexedProperty valid arguments.
     */
    @Test
    public void testGetIndexedValues() throws Exception {
        Object value = null;
        // Use explicit key argument
        for (int i = 0; i < 5; i++) {
            value = PropertyUtils.getIndexedProperty(bean, "dupProperty", i);
            assertNotNull(value, "dupProperty returned value " + i);
            assertInstanceOf(String.class, value, "dupProperty returned String " + i);
            assertEquals("Dup " + i, (String) value, "dupProperty returned correct " + i);

            value = PropertyUtils.getIndexedProperty(bean, "intArray", i);
            assertNotNull(value, "intArray returned value " + i);
            assertInstanceOf(Integer.class, value, "intArray returned Integer " + i);
            assertEquals(i * 10, ((Integer) value).intValue(), "intArray returned correct " + i);

            value = PropertyUtils.getIndexedProperty(bean, "intIndexed", i);
            assertNotNull(value, "intIndexed returned value " + i);
            assertInstanceOf(Integer.class, value, "intIndexed returned Integer " + i);
            assertEquals(i * 10, ((Integer) value).intValue(), "intIndexed returned correct " + i);

            value = PropertyUtils.getIndexedProperty(bean, "listIndexed", i);
            assertNotNull(value, "listIndexed returned value " + i);
            assertInstanceOf(String.class, value, "list returned String " + i);
            assertEquals("String " + i, (String) value, "listIndexed returned correct " + i);

            value = PropertyUtils.getIndexedProperty(bean, "stringArray", i);
            assertNotNull(value, "stringArray returned value " + i);
            assertInstanceOf(String.class, value, "stringArray returned String " + i);
            assertEquals("String " + i, (String) value, "stringArray returned correct " + i);

            value = PropertyUtils.getIndexedProperty(bean, "stringIndexed", i);
            assertNotNull(value, "stringIndexed returned value " + i);
            assertInstanceOf(String.class, value, "stringIndexed returned String " + i);
            assertEquals("String " + i, (String) value, "stringIndexed returned correct " + i);

        }

        // Use key expression

        for (int i = 0; i < 5; i++) {

            value = PropertyUtils.getIndexedProperty(bean, "dupProperty[" + i + "]");
            assertNotNull(value, "dupProperty returned value " + i);
            assertInstanceOf(String.class, value, "dupProperty returned String " + i);
            assertEquals("Dup " + i, (String) value, "dupProperty returned correct " + i);

            value = PropertyUtils.getIndexedProperty(bean, "intArray[" + i + "]");
            assertNotNull(value, "intArray returned value " + i);
            assertInstanceOf(Integer.class, value, "intArray returned Integer " + i);
            assertEquals(i * 10, ((Integer) value).intValue(), "intArray returned correct " + i);

            value = PropertyUtils.getIndexedProperty(bean, "intIndexed[" + i + "]");
            assertNotNull(value, "intIndexed returned value " + i);
            assertInstanceOf(Integer.class, value, "intIndexed returned Integer " + i);
            assertEquals(i * 10, ((Integer) value).intValue(), "intIndexed returned correct " + i);

            value = PropertyUtils.getIndexedProperty(bean, "listIndexed[" + i + "]");
            assertNotNull(value, "listIndexed returned value " + i);
            assertInstanceOf(String.class, value, "listIndexed returned String " + i);
            assertEquals("String " + i, (String) value, "listIndexed returned correct " + i);

            value = PropertyUtils.getIndexedProperty(bean, "stringArray[" + i + "]");
            assertNotNull(value, "stringArray returned value " + i);
            assertInstanceOf(String.class, value, "stringArray returned String " + i);
            assertEquals("String " + i, (String) value, "stringArray returned correct " + i);

            value = PropertyUtils.getIndexedProperty(bean, "stringIndexed[" + i + "]");
            assertNotNull(value, "stringIndexed returned value " + i);
            assertInstanceOf(String.class, value, "stringIndexed returned String " + i);
            assertEquals("String " + i, (String) value, "stringIndexed returned correct " + i);

        }

        // Index out of bounds tests

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> PropertyUtils.getIndexedProperty(bean, "dupProperty", -1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> PropertyUtils.getIndexedProperty(bean, "dupProperty", 5));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> PropertyUtils.getIndexedProperty(bean, "intArray", -1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> PropertyUtils.getIndexedProperty(bean, "intArray", 5));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> PropertyUtils.getIndexedProperty(bean, "intIndexed", -1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> PropertyUtils.getIndexedProperty(bean, "intIndexed", 5));
        assertThrows(IndexOutOfBoundsException.class, () -> PropertyUtils.getIndexedProperty(bean, "listIndexed", -1));
        assertThrows(IndexOutOfBoundsException.class, () -> PropertyUtils.getIndexedProperty(bean, "listIndexed", 5));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> PropertyUtils.getIndexedProperty(bean, "stringArray", -1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> PropertyUtils.getIndexedProperty(bean, "stringArray", 5));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> PropertyUtils.getIndexedProperty(bean, "stringIndexed", -1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> PropertyUtils.getIndexedProperty(bean, "stringIndexed", 5));
    }

    /**
     * Corner cases on getMappedProperty invalid arguments.
     */
    @Test
    public void testGetMappedArguments() {
        // Use explicit key argument
        assertThrows(NullPointerException.class, () -> PropertyUtils.getMappedProperty(null, "mappedProperty", "First Key"));
        assertThrows(NullPointerException.class, () -> PropertyUtils.getMappedProperty(bean, null, "First Key"));
        assertThrows(NullPointerException.class, () -> PropertyUtils.getMappedProperty(bean, "mappedProperty", null));
        // Use key expression
        assertThrows(NullPointerException.class, () -> PropertyUtils.getMappedProperty(null, "mappedProperty(First Key)"));
        assertThrows(NoSuchMethodException.class, () -> PropertyUtils.getMappedProperty(bean, "(Second Key)"));
        assertThrows(IllegalArgumentException.class, () -> PropertyUtils.getMappedProperty(bean, "mappedProperty"));
    }

    /**
     * Test getting an indexed value out of a mapped array
     */
    @Test
    public void testGetMappedArray() throws Exception {
        final TestBean bean = new TestBean();
        final String[] array = { "abc", "def", "ghi" };
        bean.getMapProperty().put("mappedArray", array);
        assertEquals("abc", PropertyUtils.getProperty(bean, "mapProperty(mappedArray)[0]"));
        assertEquals("def", PropertyUtils.getProperty(bean, "mapProperty(mappedArray)[1]"));
        assertEquals("ghi", PropertyUtils.getProperty(bean, "mapProperty(mappedArray)[2]"));
    }

    /**
     * Test getting an indexed value out of a mapped List
     */
    @Test
    public void testGetMappedList() throws Exception {
        final TestBean bean = new TestBean();
        final List<Object> list = new ArrayList<>();
        list.add("klm");
        list.add("nop");
        list.add("qrs");
        bean.getMapProperty().put("mappedList", list);
        assertEquals("klm", PropertyUtils.getProperty(bean, "mapProperty(mappedList)[0]"));
        assertEquals("nop", PropertyUtils.getProperty(bean, "mapProperty(mappedList)[1]"));
        assertEquals("qrs", PropertyUtils.getProperty(bean, "mapProperty(mappedList)[2]"));
    }

    /**
     * Test getting a value out of a mapped Map
     */
    @Test
    public void testGetMappedMap() throws Exception {
        final TestBean bean = new TestBean();
        final Map<String, Object> map = new HashMap<>();
        map.put("sub-key-1", "sub-value-1");
        map.put("sub-key-2", "sub-value-2");
        map.put("sub-key-3", "sub-value-3");
        bean.getMapProperty().put("mappedMap", map);
        assertEquals("sub-value-1", PropertyUtils.getProperty(bean, "mapProperty(mappedMap)(sub-key-1)"));
        assertEquals("sub-value-2", PropertyUtils.getProperty(bean, "mapProperty(mappedMap)(sub-key-2)"));
        assertEquals("sub-value-3", PropertyUtils.getProperty(bean, "mapProperty(mappedMap)(sub-key-3)"));
    }

    /**
     * Test getting mapped values with periods in the key.
     */
    @Test
    public void testGetMappedPeriods() throws Exception {

        bean.setMappedProperty("key.with.a.dot", "Special Value");
        assertEquals("Special Value", bean.getMappedProperty("key.with.a.dot"), "Can retrieve directly");
        assertEquals("Special Value", PropertyUtils.getMappedProperty(bean, "mappedProperty", "key.with.a.dot"), "Can retrieve via getMappedProperty");
        assertEquals("Special Value", PropertyUtils.getNestedProperty(bean, "mappedProperty(key.with.a.dot)"), "Can retrieve via getNestedProperty");

        bean.setMappedObjects("nested.property", new TestBean());
        assertNotNull(bean.getMappedObjects("nested.property"), "Can retrieve directly");
        assertEquals("This is a string", PropertyUtils.getNestedProperty(bean, "mappedObjects(nested.property).stringProperty"), "Can retrieve nested");

        assertEquals("Mapped Value", PropertyUtils.getNestedProperty(bean, "mappedNested.value(Mapped Key)"), "Can't retrieved nested with mapped property");
    }

    /**
     * Test getting mapped values with slashes in the key. This is different from periods because slashes are not syntactically significant.
     */
    @Test
    public void testGetMappedSlashes() throws Exception {

        bean.setMappedProperty("key/with/a/slash", "Special Value");
        assertEquals("Special Value", bean.getMappedProperty("key/with/a/slash"), "Can retrieve directly");
        assertEquals("Special Value", PropertyUtils.getMappedProperty(bean, "mappedProperty", "key/with/a/slash"), "Can retrieve via getMappedProperty");
        assertEquals("Special Value", PropertyUtils.getNestedProperty(bean, "mappedProperty(key/with/a/slash)"), "Can retrieve via getNestedProperty");

        bean.setMappedObjects("nested/property", new TestBean());
        assertNotNull(bean.getMappedObjects("nested/property"), "Can retrieve directly");
        assertEquals("This is a string", PropertyUtils.getNestedProperty(bean, "mappedObjects(nested/property).stringProperty"), "Can retrieve nested");
    }

    /**
     * Positive and negative tests on getMappedProperty valid arguments.
     */
    @Test
    public void testGetMappedValues() throws Exception {
        Object value = null;
        // Use explicit key argument
        value = PropertyUtils.getMappedProperty(bean, "mappedProperty", "First Key");
        assertEquals("First Value", value, "Can find first value");

        value = PropertyUtils.getMappedProperty(bean, "mappedProperty", "Second Key");
        assertEquals("Second Value", value, "Can find second value");

        value = PropertyUtils.getMappedProperty(bean, "mappedProperty", "Third Key");
        assertNull(value, "Can not find third value");

        // Use key expression with parentheses
        value = PropertyUtils.getMappedProperty(bean, "mappedProperty(First Key)");
        assertEquals("First Value", value, "Can find first value");

        value = PropertyUtils.getMappedProperty(bean, "mappedProperty(Second Key)");
        assertEquals("Second Value", value, "Can find second value");

        value = PropertyUtils.getMappedProperty(bean, "mappedProperty(Third Key)");
        assertNull(value, "Can not find third value");

        // Use key expression with dotted syntax
        value = PropertyUtils.getNestedProperty(bean, "mapProperty.First Key");
        assertEquals("First Value", value, "Can find first value");

        value = PropertyUtils.getNestedProperty(bean, "mapProperty.Second Key");
        assertEquals("Second Value", value, "Can find second value");

        value = PropertyUtils.getNestedProperty(bean, "mapProperty.Third Key");
        assertNull(value, "Can not find third value");
    }

    /**
     * Corner cases on getNestedProperty invalid arguments.
     */
    @Test
    public void testGetNestedArguments() {
        assertThrows(NullPointerException.class, () -> PropertyUtils.getNestedProperty(null, "stringProperty"));
        assertThrows(NullPointerException.class, () -> PropertyUtils.getNestedProperty(bean, null));
    }

    /**
     * Test getNestedProperty on a boolean property.
     */
    @Test
    public void testGetNestedBoolean() throws Exception {
        final Object value = PropertyUtils.getNestedProperty(bean, "nested.booleanProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(Boolean.class, value, "Got correct type");
        assertEquals(((Boolean) value).booleanValue(), bean.getNested().getBooleanProperty(), "Got correct value");
    }

    /**
     * Test getNestedProperty on a double property.
     */
    @Test
    public void testGetNestedDouble() throws Exception {
        final Object value = PropertyUtils.getNestedProperty(bean, "nested.doubleProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(Double.class, value, "Got correct type");
        assertEquals(((Double) value).doubleValue(), bean.getNested().getDoubleProperty(), 0.005, "Got correct value");
    }

    /**
     * Test getNestedProperty on a float property.
     */
    @Test
    public void testGetNestedFloat() throws Exception {
        final Object value = PropertyUtils.getNestedProperty(bean, "nested.floatProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(Float.class, value, "Got correct type");
        assertEquals(((Float) value).floatValue(), bean.getNested().getFloatProperty(), (float) 0.005, "Got correct value");
    }

    /**
     * Test getNestedProperty on an int property.
     */
    @Test
    public void testGetNestedInt() throws Exception {
        final Object value = PropertyUtils.getNestedProperty(bean, "nested.intProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(Integer.class, value, "Got correct type");
        assertEquals(((Integer) value).intValue(), bean.getNested().getIntProperty(), "Got correct value");
    }

    /**
     * Test getNestedProperty on a long property.
     */
    @Test
    public void testGetNestedLong() throws Exception {
        final Object value = PropertyUtils.getNestedProperty(bean, "nested.longProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(Long.class, value, "Got correct type");
        assertEquals(((Long) value).longValue(), bean.getNested().getLongProperty(), "Got correct value");
    }

    /**
     * Test getNestedProperty on a read-only String property.
     */
    @Test
    public void testGetNestedReadOnly() throws Exception {
        final Object value = PropertyUtils.getNestedProperty(bean, "nested.readOnlyProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(String.class, value, "Got correct type");
        assertEquals((String) value, bean.getReadOnlyProperty(), "Got correct value");
    }

    /**
     * Test getNestedProperty on a short property.
     */
    @Test
    public void testGetNestedShort() throws Exception {
        final Object value = PropertyUtils.getNestedProperty(bean, "nested.shortProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(Short.class, value, "Got correct type");
        assertEquals(((Short) value).shortValue(), bean.getNested().getShortProperty(), "Got correct value");
    }

    /**
     * Test getNestedProperty on a String property.
     */
    @Test
    public void testGetNestedString() throws Exception {
        final Object value = PropertyUtils.getNestedProperty(bean, "nested.stringProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(String.class, value, "Got correct type");
        assertEquals((String) value, bean.getNested().getStringProperty(), "Got correct value");
    }

    /**
     * Negative test getNestedProperty on an unknown property.
     */
    @Test
    public void testGetNestedUnknown() throws Exception {
        assertThrows(NoSuchMethodException.class, () -> PropertyUtils.getNestedProperty(bean, "nested.unknown"));
    }

    /**
     * Test getNestedProperty on a write-only String property.
     */
    @Test
    public void testGetNestedWriteOnly() throws Exception {
        assertThrows(NoSuchMethodException.class, () -> PropertyUtils.getNestedProperty(bean, "writeOnlyProperty"));
    }

    /**
     * Test getPropertyType() on all kinds of properties.
     */
    @Test
    public void testGetPropertyType() throws Exception {

        Class<?> clazz = null;
        final int[] intArray = {};
        final String[] stringArray = {};

        // Scalar and Indexed Properties
        clazz = PropertyUtils.getPropertyType(bean, "booleanProperty");
        assertEquals(Boolean.TYPE, clazz, "booleanProperty type");
        clazz = PropertyUtils.getPropertyType(bean, "booleanSecond");
        assertEquals(Boolean.TYPE, clazz, "booleanSecond type");
        clazz = PropertyUtils.getPropertyType(bean, "doubleProperty");
        assertEquals(Double.TYPE, clazz, "doubleProperty type");
        clazz = PropertyUtils.getPropertyType(bean, "dupProperty");
        assertEquals(String.class, clazz, "dupProperty type");
        clazz = PropertyUtils.getPropertyType(bean, "floatProperty");
        assertEquals(Float.TYPE, clazz, "floatProperty type");
        clazz = PropertyUtils.getPropertyType(bean, "intArray");
        assertEquals(intArray.getClass(), clazz, "intArray type");
        clazz = PropertyUtils.getPropertyType(bean, "intIndexed");
        assertEquals(Integer.TYPE, clazz, "intIndexed type");
        clazz = PropertyUtils.getPropertyType(bean, "intProperty");
        assertEquals(Integer.TYPE, clazz, "intProperty type");
        clazz = PropertyUtils.getPropertyType(bean, "listIndexed");
        assertEquals(List.class, clazz, "listIndexed type");
        clazz = PropertyUtils.getPropertyType(bean, "longProperty");
        assertEquals(Long.TYPE, clazz, "longProperty type");
        clazz = PropertyUtils.getPropertyType(bean, "mappedProperty");
        assertEquals(String.class, clazz, "mappedProperty type");
        clazz = PropertyUtils.getPropertyType(bean, "mappedIntProperty");
        assertEquals(Integer.TYPE, clazz, "mappedIntProperty type");
        clazz = PropertyUtils.getPropertyType(bean, "readOnlyProperty");
        assertEquals(String.class, clazz, "readOnlyProperty type");
        clazz = PropertyUtils.getPropertyType(bean, "shortProperty");
        assertEquals(Short.TYPE, clazz, "shortProperty type");
        clazz = PropertyUtils.getPropertyType(bean, "stringArray");
        assertEquals(stringArray.getClass(), clazz, "stringArray type");
        clazz = PropertyUtils.getPropertyType(bean, "stringIndexed");
        assertEquals(String.class, clazz, "stringIndexed type");
        clazz = PropertyUtils.getPropertyType(bean, "stringProperty");
        assertEquals(String.class, clazz, "stringProperty type");
        clazz = PropertyUtils.getPropertyType(bean, "writeOnlyProperty");
        assertEquals(String.class, clazz, "writeOnlyProperty type");

        // Nested Properties
        clazz = PropertyUtils.getPropertyType(bean, "nested.booleanProperty");
        assertEquals(Boolean.TYPE, clazz, "booleanProperty type");
        clazz = PropertyUtils.getPropertyType(bean, "nested.booleanSecond");
        assertEquals(Boolean.TYPE, clazz, "booleanSecond type");
        clazz = PropertyUtils.getPropertyType(bean, "nested.doubleProperty");
        assertEquals(Double.TYPE, clazz, "doubleProperty type");
        clazz = PropertyUtils.getPropertyType(bean, "nested.dupProperty");
        assertEquals(String.class, clazz, "dupProperty type");
        clazz = PropertyUtils.getPropertyType(bean, "nested.floatProperty");
        assertEquals(Float.TYPE, clazz, "floatProperty type");
        clazz = PropertyUtils.getPropertyType(bean, "nested.intArray");
        assertEquals(intArray.getClass(), clazz, "intArray type");
        clazz = PropertyUtils.getPropertyType(bean, "nested.intIndexed");
        assertEquals(Integer.TYPE, clazz, "intIndexed type");
        clazz = PropertyUtils.getPropertyType(bean, "nested.intProperty");
        assertEquals(Integer.TYPE, clazz, "intProperty type");
        clazz = PropertyUtils.getPropertyType(bean, "nested.listIndexed");
        assertEquals(List.class, clazz, "listIndexed type");
        clazz = PropertyUtils.getPropertyType(bean, "nested.longProperty");
        assertEquals(Long.TYPE, clazz, "longProperty type");
        clazz = PropertyUtils.getPropertyType(bean, "nested.mappedProperty");
        assertEquals(String.class, clazz, "mappedProperty type");
        clazz = PropertyUtils.getPropertyType(bean, "nested.mappedIntProperty");
        assertEquals(Integer.TYPE, clazz, "mappedIntProperty type");
        clazz = PropertyUtils.getPropertyType(bean, "nested.readOnlyProperty");
        assertEquals(String.class, clazz, "readOnlyProperty type");
        clazz = PropertyUtils.getPropertyType(bean, "nested.shortProperty");
        assertEquals(Short.TYPE, clazz, "shortProperty type");
        clazz = PropertyUtils.getPropertyType(bean, "nested.stringArray");
        assertEquals(stringArray.getClass(), clazz, "stringArray type");
        clazz = PropertyUtils.getPropertyType(bean, "nested.stringIndexed");
        assertEquals(String.class, clazz, "stringIndexed type");
        clazz = PropertyUtils.getPropertyType(bean, "nested.stringProperty");
        assertEquals(String.class, clazz, "stringProperty type");
        clazz = PropertyUtils.getPropertyType(bean, "nested.writeOnlyProperty");
        assertEquals(String.class, clazz, "writeOnlyProperty type");

        // Nested DynaBean
        clazz = PropertyUtils.getPropertyType(bean, "nestedDynaBean");
        assertEquals(DynaBean.class, clazz, "nestedDynaBean type");
        clazz = PropertyUtils.getPropertyType(bean, "nestedDynaBean.stringProperty");
        assertEquals(String.class, clazz, "nestedDynaBean.stringProperty type");
        clazz = PropertyUtils.getPropertyType(bean, "nestedDynaBean.nestedBean");
        assertEquals(TestBean.class, clazz, "nestedDynaBean.nestedBean type");
        clazz = PropertyUtils.getPropertyType(bean, "nestedDynaBean.nestedBean.nestedDynaBean");
        assertEquals(DynaBean.class, clazz, "nestedDynaBean.nestedBean.nestedDynaBean type");
        clazz = PropertyUtils.getPropertyType(bean, "nestedDynaBean.nestedBean.nestedDynaBean.stringProperty");
        assertEquals(String.class, clazz, "nestedDynaBean.nestedBean.nestedDynaBean.stringPropert type");

        // test Null
        clazz = PropertyUtils.getPropertyType(bean, "nestedDynaBean.nullDynaBean");
        assertEquals(DynaBean.class, clazz, "nestedDynaBean.nullDynaBean type");
        assertThrows(NestedNullException.class, () -> PropertyUtils.getPropertyType(bean, "nestedDynaBean.nullDynaBean.foo"));
    }

    /**
     * Test accessing a public sub-bean of a package scope bean
     */
    @Test
    public void testGetPublicSubBean_of_PackageBean() throws Exception {

        final PublicSubBean bean = new PublicSubBean();
        bean.setFoo("foo-start");
        bean.setBar("bar-start");
        Object result = null;

        // Get Foo
        result = PropertyUtils.getProperty(bean, "foo");
        assertEquals("foo-start", result, "foo property");

        // Get Bar
        result = PropertyUtils.getProperty(bean, "bar");
        assertEquals("bar-start", result, "bar property");
    }

    /**
     * Base for testGetReadMethod() series of tests.
     *
     * @param bean       Bean for which to retrieve read methods.
     * @param properties Property names to search for
     * @param className  Class name where this method should be defined
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private void testGetReadMethod(final Object bean, final String[] properties, final String className)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        final PropertyDescriptor[] pd = PropertyUtils.getPropertyDescriptors(bean);
        for (final String propertie : properties) {

            // Identify the property descriptor for this property
            if (propertie.equals("intIndexed")) {
                continue;
            }
            if (propertie.equals("stringIndexed")) {
                continue;
            }
            if (propertie.equals("writeOnlyProperty")) {
                continue;
            }
            int n = -1;
            for (int j = 0; j < pd.length; j++) {
                if (propertie.equals(pd[j].getName())) {
                    n = j;
                    break;
                }
            }
            assertTrue(n >= 0, "PropertyDescriptor for " + propertie);

            // Locate an accessible property reader method for it
            final Method reader = PropertyUtils.getReadMethod(pd[n]);
            assertNotNull(reader, "Reader for " + propertie);
            final Class<?> clazz = reader.getDeclaringClass();
            assertNotNull(clazz, "Declaring class for " + propertie);
            assertEquals(clazz.getName(), className, "Correct declaring class for " + propertie);

            // Actually call the reader method we received
            reader.invoke(bean, (Object[]) new Class<?>[0]);
        }

    }

    /**
     * Test getting accessible property reader methods for a specified list of properties of our standard test bean.
     */
    @Test
    public void testGetReadMethodBasic() throws Exception {
        testGetReadMethod(bean, properties, TEST_BEAN_CLASS);

    }

    /**
     * Test getting accessible property reader methods for a specified list of properties of a package private subclass of our standard test bean.
     */
    @Test
    public void testGetReadMethodPackageSubclass() throws Exception {
        testGetReadMethod(beanPackageSubclass, properties, TEST_BEAN_CLASS);

    }

    /**
     * Test getting accessible property reader methods for a specified list of properties that are declared either directly or via implemented interfaces.
     */
    @Test
    public void testGetReadMethodPublicInterface() throws Exception {

        // Properties "bar" and "baz" are visible via implemented interfaces
        // (one direct and one indirect)
        testGetReadMethod(beanPrivate, new String[] { "bar" }, PRIVATE_DIRECT_CLASS);
        testGetReadMethod(beanPrivate, new String[] { "baz" }, PRIVATE_INDIRECT_CLASS);

        // Properties "bar" and "baz" are visible via implemented interfaces
        // (one direct and one indirect). The interface is implemented in
        // a superclass
        testGetReadMethod(beanPrivateSubclass, new String[] { "bar" }, PRIVATE_DIRECT_CLASS);
        testGetReadMethod(beanPrivateSubclass, new String[] { "baz" }, PRIVATE_INDIRECT_CLASS);

        // Property "foo" is not accessible because the underlying
        // class has package scope
        final PropertyDescriptor[] pd = PropertyUtils.getPropertyDescriptors(beanPrivate);
        int n = -1;
        for (int i = 0; i < pd.length; i++) {
            if ("foo".equals(pd[i].getName())) {
                n = i;
                break;
            }
        }
        assertTrue(n >= 0, "Found foo descriptor");
        final Method reader = pd[n].getReadMethod();
        assertNotNull(reader, "Found foo read method");
        assertThrows(IllegalAccessException.class, () -> reader.invoke(beanPrivate, (Object[]) new Class<?>[0]));
    }

    /**
     * Test getting accessible property reader methods for a specified list of properties of a public subclass of our standard test bean.
     */
    @Test
    public void testGetReadMethodPublicSubclass() throws Exception {
        testGetReadMethod(beanPublicSubclass, properties, TEST_BEAN_CLASS);
    }

    /** Text case for setting properties on inner classes */
    @Test
    public void testGetSetInnerBean() throws Exception {
        final BeanWithInnerBean bean = new BeanWithInnerBean();

        PropertyUtils.setProperty(bean, "innerBean.fish(loiterTimer)", "5");
        String out = (String) PropertyUtils.getProperty(bean.getInnerBean(), "fish(loiterTimer)");
        assertEquals("5", out, "(1) Inner class property set/get property failed.");

        out = (String) PropertyUtils.getProperty(bean, "innerBean.fish(loiterTimer)");

        assertEquals("5", out, "(2) Inner class property set/get property failed.");
    }

    /** Text case for setting properties on parent */
    @Test
    public void testGetSetParentBean() throws Exception {

        final SonOfAlphaBean bean = new SonOfAlphaBean("Roger");

        final String out = (String) PropertyUtils.getProperty(bean, "name");
        assertEquals("Roger", out, "(1) Get/Set On Parent.");

        PropertyUtils.setProperty(bean, "name", "abcd");
        assertEquals("abcd", bean.getName(), "(2) Get/Set On Parent.");
    }

    /**
     * Corner cases on getSimpleProperty invalid arguments.
     */
    @Test
    public void testGetSimpleArguments() {
        assertThrows(NullPointerException.class, () -> PropertyUtils.getSimpleProperty(null, "stringProperty"));
        assertThrows(NullPointerException.class, () -> PropertyUtils.getSimpleProperty(bean, null));
    }

    /**
     * Test getSimpleProperty on a boolean property.
     */
    @Test
    public void testGetSimpleBoolean() throws Exception {
        final Object value = PropertyUtils.getSimpleProperty(bean, "booleanProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(Boolean.class, value, "Got correct type");
        assertEquals(((Boolean) value).booleanValue(), bean.getBooleanProperty(), "Got correct value");
    }

    /**
     * Test getSimpleProperty on a double property.
     */
    @Test
    public void testGetSimpleDouble() throws Exception {
        final Object value = PropertyUtils.getSimpleProperty(bean, "doubleProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(Double.class, value, "Got correct type");
        assertEquals(((Double) value).doubleValue(), bean.getDoubleProperty(), 0.005, "Got correct value");
    }

    /**
     * Test getSimpleProperty on a float property.
     */
    @Test
    public void testGetSimpleFloat() throws Exception {
        final Object value = PropertyUtils.getSimpleProperty(bean, "floatProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(Float.class, value, "Got correct type");
        assertEquals(((Float) value).floatValue(), bean.getFloatProperty(), (float) 0.005, "Got correct value");
    }

    /**
     * Negative test getSimpleProperty on an indexed property.
     */
    @Test
    public void testGetSimpleIndexed() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> PropertyUtils.getSimpleProperty(bean, "intIndexed[0]"));
    }

    /**
     * Test getSimpleProperty on an int property.
     */
    @Test
    public void testGetSimpleInt() throws Exception {
        final Object value = PropertyUtils.getSimpleProperty(bean, "intProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(Integer.class, value, "Got correct type");
        assertEquals(((Integer) value).intValue(), bean.getIntProperty(), "Got correct value");
    }

    /**
     * Test getSimpleProperty on a long property.
     */
    @Test
    public void testGetSimpleLong() throws Exception {
        final Object value = PropertyUtils.getSimpleProperty(bean, "longProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(Long.class, value, "Got correct type");
        assertEquals(((Long) value).longValue(), bean.getLongProperty(), "Got correct value");
    }

    /**
     * Negative test getSimpleProperty on a nested property.
     */
    @Test
    public void testGetSimpleNested() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> PropertyUtils.getSimpleProperty(bean, "nested.stringProperty"));
    }

    /**
     * Test getSimpleProperty on a read-only String property.
     */
    @Test
    public void testGetSimpleReadOnly() throws Exception {
        final Object value = PropertyUtils.getSimpleProperty(bean, "readOnlyProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(String.class, value, "Got correct type");
        assertEquals((String) value, bean.getReadOnlyProperty(), "Got correct value");
    }

    /**
     * Test getSimpleProperty on a short property.
     */
    @Test
    public void testGetSimpleShort() throws Exception {
        final Object value = PropertyUtils.getSimpleProperty(bean, "shortProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(Short.class, value, "Got correct type");
        assertEquals(((Short) value).shortValue(), bean.getShortProperty(), "Got correct value");
    }

    /**
     * Test getSimpleProperty on a String property.
     */
    @Test
    public void testGetSimpleString() throws Exception {
        final Object value = PropertyUtils.getSimpleProperty(bean, "stringProperty");
        assertNotNull(value, "Got a value");
        assertInstanceOf(String.class, value, "Got correct type");
        assertEquals((String) value, bean.getStringProperty(), "Got correct value");
    }

    /**
     * Negative test getSimpleProperty on an unknown property.
     */
    @Test
    public void testGetSimpleUnknown() throws Exception {
        assertThrows(NoSuchMethodException.class, () -> PropertyUtils.getSimpleProperty(bean, "unknown"));
    }

    /**
     * Test getSimpleProperty on a write-only String property.
     */
    @Test
    public void testGetSimpleWriteOnly() throws Exception {
        assertThrows(NoSuchMethodException.class, () -> PropertyUtils.getSimpleProperty(bean, "writeOnlyProperty"));

    }

    /**
     * Base for testGetWriteMethod() series of tests.
     *
     * @param bean       Bean for which to retrieve write methods.
     * @param properties Property names to search for
     * @param className  Class name where this method should be defined
     */
    private void testGetWriteMethod(final Object bean, final String[] properties, final String className) {

        final PropertyDescriptor[] pd = PropertyUtils.getPropertyDescriptors(bean);
        for (final String property : properties) {

            // Identify the property descriptor for this property
            if (property.equals("intIndexed")) {
                continue;
            }
            if (property.equals("listIndexed")) {
                continue;
            }
            if (property.equals("nested")) {
                continue; // This property is read only
            }
            if (property.equals("readOnlyProperty")) {
                continue;
            }
            if (property.equals("stringIndexed")) {
                continue;
            }
            int n = -1;
            for (int j = 0; j < pd.length; j++) {
                if (property.equals(pd[j].getName())) {
                    n = j;
                    break;
                }
            }
            assertTrue(n >= 0, "PropertyDescriptor for " + property);

            // Locate an accessible property reader method for it
            final Method writer = PropertyUtils.getWriteMethod(pd[n]);
            assertNotNull(writer, "Writer for " + property);
            final Class<?> clazz = writer.getDeclaringClass();
            assertNotNull(clazz, "Declaring class for " + property);
            assertEquals(clazz.getName(), className, "Correct declaring class for " + property);

        }

    }

    /**
     * Test getting accessible property writer methods for a specified list of properties of our standard test bean.
     */
    @Test
    public void testGetWriteMethodBasic() {
        testGetWriteMethod(bean, properties, TEST_BEAN_CLASS);
    }

    /**
     * Test getting accessible property writer methods for a specified list of properties of a package private subclass of our standard test bean.
     */
    @Test
    public void testGetWriteMethodPackageSubclass() {
        testGetWriteMethod(beanPackageSubclass, properties, TEST_BEAN_CLASS);
    }

    /**
     * Test getting accessible property writer methods for a specified list of properties of a public subclass of our standard test bean.
     */
    @Test
    public void testGetWriteMethodPublicSubclass() {
        testGetWriteMethod(beanPublicSubclass, properties, TEST_BEAN_CLASS);
    }

    /**
     * Test isReadable() method.
     */
    @Test
    public void testIsReadable() throws Exception {
        String property = null;
        property = "stringProperty";
        assertTrue(PropertyUtils.isReadable(bean, property), "Property " + property + " isReadable expected TRUE");
        property = "stringIndexed";
        assertTrue(PropertyUtils.isReadable(bean, property), "Property " + property + " isReadable expected TRUE");
        property = "mappedProperty";
        assertTrue(PropertyUtils.isReadable(bean, property), "Property " + property + " isReadable expected TRUE");
        property = "nestedDynaBean";
        assertTrue(PropertyUtils.isReadable(bean, property), "Property " + property + " isReadable expected TRUE");
        property = "nestedDynaBean.stringProperty";
        assertTrue(PropertyUtils.isReadable(bean, property), "Property " + property + " isReadable expected TRUE");
        property = "nestedDynaBean.nestedBean";
        assertTrue(PropertyUtils.isReadable(bean, property), "Property " + property + " isReadable expected TRUE");
        property = "nestedDynaBean.nestedBean.nestedDynaBean";
        assertTrue(PropertyUtils.isReadable(bean, property), "Property " + property + " isReadable expected TRUE");
        property = "nestedDynaBean.nestedBean.nestedDynaBean.stringProperty";
        assertTrue(PropertyUtils.isReadable(bean, property), "Property " + property + " isReadable expected TRUE");
        property = "nestedDynaBean.nullDynaBean";
        assertTrue(PropertyUtils.isReadable(bean, property), "Property " + property + " isReadable expected TRUE");

        assertThrows(NestedNullException.class, () -> PropertyUtils.isReadable(bean, "nestedDynaBean.nullDynaBean.foo"));
    }

    /**
     * Test isWriteable() method.
     */
    @Test
    public void testIsWriteable() throws Exception {
        String property = null;
        property = "stringProperty";
        assertTrue(PropertyUtils.isWriteable(bean, property), "Property " + property + " isWriteable expected TRUE");
        property = "stringIndexed";
        assertTrue(PropertyUtils.isWriteable(bean, property), "Property " + property + " isWriteable expected TRUE");
        property = "mappedProperty";
        assertTrue(PropertyUtils.isWriteable(bean, property), "Property " + property + " isWriteable expected TRUE");
        property = "nestedDynaBean";
        assertTrue(PropertyUtils.isWriteable(bean, property), "Property " + property + " isWriteable expected TRUE");
        property = "nestedDynaBean.stringProperty";
        assertTrue(PropertyUtils.isWriteable(bean, property), "Property " + property + " isWriteable expected TRUE");
        property = "nestedDynaBean.nestedBean";
        assertTrue(PropertyUtils.isWriteable(bean, property), "Property " + property + " isWriteable expected TRUE");
        property = "nestedDynaBean.nestedBean.nestedDynaBean";
        assertTrue(PropertyUtils.isWriteable(bean, property), "Property " + property + " isWriteable expected TRUE");
        property = "nestedDynaBean.nestedBean.nestedDynaBean.stringProperty";
        assertTrue(PropertyUtils.isWriteable(bean, property), "Property " + property + " isWriteable expected TRUE");
        property = "nestedDynaBean.nullDynaBean";
        assertTrue(PropertyUtils.isWriteable(bean, property), "Property " + property + " isWriteable expected TRUE");
        assertThrows(NestedNullException.class, () -> PropertyUtils.isWriteable(bean, "nestedDynaBean.nullDynaBean.foo"));
    }

    /**
     * This tests to see that it is possible to subclass PropertyUtilsBean and change the behavior of setNestedProperty/getNestedProperty when dealing with
     * objects that implement Map.
     */
    @Test
    public void testMapExtensionCustom() throws Exception {
        final PropsFirstPropertyUtilsBean utilsBean = new PropsFirstPropertyUtilsBean();
        final ExtendMapBean bean = new ExtendMapBean();

        // hardly worth testing this, really :-)
        bean.setUnusuallyNamedProperty("bean value");
        assertEquals("bean value", bean.getUnusuallyNamedProperty(), "Set property direct failed");

        // setSimpleProperty should affect the simple property
        utilsBean.setSimpleProperty(bean, "unusuallyNamedProperty", "new value");
        assertEquals("new value", bean.getUnusuallyNamedProperty(), "Set property on map failed (1)");

        // setNestedProperty with setter should affect the simple property
        // getNestedProperty with getter should obtain the simple property
        utilsBean.setProperty(bean, "unusuallyNamedProperty", "next value");
        assertEquals("next value", bean.getUnusuallyNamedProperty(), "Set property on map failed (2)");
        assertEquals("next value", utilsBean.getNestedProperty(bean, "unusuallyNamedProperty"), "setNestedProperty on non-simple property failed");

        // setting property without setter should update the map
        // getting property without setter should fetch from the map
        utilsBean.setProperty(bean, "mapProperty", "value1");
        assertEquals("value1", utilsBean.getNestedProperty(bean, "mapProperty"), "setNestedProperty on non-simple property failed");

        final HashMap<String, Object> myMap = new HashMap<>();
        myMap.put("thebean", bean);
        utilsBean.getNestedProperty(myMap, "thebean.mapitem");
        utilsBean.getNestedProperty(myMap, "thebean(mapitem)");
    }

    /**
     * This tests to see that classes that implement Map always have their custom properties ignored.
     * <p>
     * Note that this behavior has changed several times over past releases of beanutils, breaking backwards compatibility each time. Here's hoping that the
     * current 1.7.1 release is the last time this behavior changes!
     */
    @Test
    public void testMapExtensionDefault() throws Exception {
        final ExtendMapBean bean = new ExtendMapBean();

        // setting property direct should work, and not affect map
        bean.setUnusuallyNamedProperty("bean value");
        assertEquals("bean value", bean.getUnusuallyNamedProperty(), "Set property direct failed");
        assertNull(PropertyUtils.getNestedProperty(bean, "unusuallyNamedProperty"), "Get on unset map property failed");

        // setting simple property should call the setter method only, and not
        // affect the map.
        PropertyUtils.setSimpleProperty(bean, "unusuallyNamedProperty", "new value");
        assertEquals("new value", bean.getUnusuallyNamedProperty(), "Set property on map failed (1)");
        assertNull(PropertyUtils.getNestedProperty(bean, "unusuallyNamedProperty"), "Get on unset map property failed");

        // setting via setNestedProperty should affect the map only, and not
        // call the setter method.
        PropertyUtils.setProperty(bean, "unusuallyNamedProperty", "next value");
        assertEquals("next value", PropertyUtils.getNestedProperty(bean, "unusuallyNamedProperty"),
                "setNestedProperty on map not visible to getNestedProperty");
        assertEquals("new value", bean.getUnusuallyNamedProperty(), "Set nested property on map unexpected affected simple property");
    }

    /**
     * Test the mappedPropertyType of MappedPropertyDescriptor.
     */
    @Test
    public void testMappedPropertyType() throws Exception {

        MappedPropertyDescriptor desc;

        // Check a String property
        desc = (MappedPropertyDescriptor) PropertyUtils.getPropertyDescriptor(bean, "mappedProperty");
        assertEquals(String.class, desc.getMappedPropertyType());

        // Check an int property
        desc = (MappedPropertyDescriptor) PropertyUtils.getPropertyDescriptor(bean, "mappedIntProperty");
        assertEquals(Integer.TYPE, desc.getMappedPropertyType());

    }

    /**
     * There is an issue in setNestedProperty/getNestedProperty when the target bean is a map and the name string requests mapped or indexed operations on a
     * field. These are not supported for fields of a Map, but it's an easy mistake to make and this test case ensures that an appropriate exception is thrown
     * when a user does this.
     * <p>
     * The problem is with passing strings of form "a(b)" or "a[3]" to setNestedProperty or getNestedProperty when the target bean they are applied to
     * implements Map. These strings are actually requesting "the result of calling mapped method a on the target object with a parameter of b" or "the result
     * of calling indexed method a on the target object with a parameter of 3". And these requests are not valid when the target is a Map as a Map only supports
     * calling get(fieldName) or put(fieldName), neither of which can be further indexed with a string or an integer.
     * <p>
     * However it is likely that some users will assume that "a[3]" when applied to a map will be equivalent to (map.get("a"))[3] with the appropriate
     * typecasting, or for "a(b)" to be equivalent to map.get("a").get("b").
     * <p>
     * Here we verify that an exception is thrown if the user makes this mistake.
     */
    @Test
    public void testNestedPropertyKeyOrIndexOnBeanImplementingMap() throws Exception {
        final HashMap<String, Object> map = new HashMap<>();
        final HashMap<String, Object> submap = new HashMap<>();
        final BetaBean betaBean1 = new BetaBean("test1");
        final BetaBean betaBean2 = new BetaBean("test2");

        // map.put("submap", submap)
        PropertyUtils.setNestedProperty(map, "submap", submap);

        // map.get("submap").put("beta1", betaBean1)
        PropertyUtils.setNestedProperty(map, "submap.beta1", betaBean1);
        assertEquals("submap", keysToString(map), "Unexpected keys in map");
        assertEquals("beta1", keysToString(submap), "Unexpected keys in submap");

        // One would expect that the command below would be equivalent to
        // Map m = (Map) map.get("submap");
        // m.put("beta2", betaBean2)
        // However this isn't how javabeans property methods work. A map
        // only effectively has "simple" properties, even when the
        // returned object is a Map or Array.
        final IllegalArgumentException e1 = assertThrows(IllegalArgumentException.class,
                () -> PropertyUtils.setNestedProperty(map, "submap(beta2)", betaBean2));
        // What, no exception? In that case, setNestedProperties has
        // probably just tried to do
        // map.set("submap(beta2)", betaBean2)
        // which is almost certainly not what the used expected. This is
        // what BeanUtils 1.5.0 to 1.7.1 did....
        // ok, getting an exception was expected. As it is of a generic
        // type, let's check the message string to make sure it really
        // was caused by the issue we expected.
        assertTrue(e1.getMessage().contains("Indexed or mapped properties are not supported"), "Unexpected exception message");

        // One would expect that "submap[3]" would be equivalent to
        // Object[] objects = (Object[]) map.get("submap");
        // return objects[3];
        // However this isn't how javabeans property methods work. A map
        // only effectively has "simple" properties, even when the
        // returned object is a Map or Array.
        final IllegalArgumentException e2 = assertThrows(IllegalArgumentException.class, () -> PropertyUtils.getNestedProperty(map, "submap[3]"));
        // What, no exception? In that case, getNestedProperties has
        // probably just tried to do
        // map.get("submap[3]")
        // which is almost certainly not what the used expected. This is
        // what BeanUtils 1.5.0 to 1.7.1 did....
        // ok, getting an exception was expected. As it is of a generic
        // type, let's check the message string to make sure it really
        // was caused by the issue we expected.
        final int index = e2.getMessage().indexOf("Indexed or mapped properties are not supported");
        assertTrue(index >= 0, "Unexpected exception message");
    }

    @Test
    public void testNestedWithIndex() throws Exception {
        final NestedTestBean nestedBean = new NestedTestBean("base");
        nestedBean.init();
        nestedBean.getSimpleBeanProperty().init();

        NestedTestBean

        // test first calling properties on indexed beans

        value = (NestedTestBean) PropertyUtils.getProperty(nestedBean, "indexedProperty[0]");
        assertEquals("Bean@0", value.getName(), "Cannot get simple index(1)");
        assertEquals("NOT SET", value.getTestString(), "Bug in NestedTestBean");

        value = (NestedTestBean) PropertyUtils.getProperty(nestedBean, "indexedProperty[1]");
        assertEquals("Bean@1", value.getName(), "Cannot get simple index(1)");
        assertEquals("NOT SET", value.getTestString(), "Bug in NestedTestBean");

        String prop = (String) PropertyUtils.getProperty(nestedBean, "indexedProperty[0].testString");
        assertEquals("NOT SET", prop, "Get property on indexes failed (1)");

        prop = (String) PropertyUtils.getProperty(nestedBean, "indexedProperty[1].testString");
        assertEquals("NOT SET", prop, "Get property on indexes failed (2)");

        PropertyUtils.setProperty(nestedBean, "indexedProperty[0].testString", "Test#1");
        assertEquals("Test#1", nestedBean.getIndexedProperty(0).getTestString(), "Cannot set property on indexed bean (1)");

        PropertyUtils.setProperty(nestedBean, "indexedProperty[1].testString", "Test#2");
        assertEquals("Test#2", nestedBean.getIndexedProperty(1).getTestString(), "Cannot set property on indexed bean (2)");

        // test first calling indexed properties on a simple property

        value = (NestedTestBean) PropertyUtils.getProperty(nestedBean, "simpleBeanProperty");
        assertEquals("Simple Property Bean", value.getName(), "Cannot get simple bean");
        assertEquals("NOT SET", value.getTestString(), "Bug in NestedTestBean");

        value = (NestedTestBean) PropertyUtils.getProperty(nestedBean, "simpleBeanProperty.indexedProperty[3]");
        assertEquals("Bean@3", value.getName(), "Cannot get index property on property");
        assertEquals("NOT SET", value.getTestString(), "Bug in NestedTestBean");

        PropertyUtils.setProperty(nestedBean, "simpleBeanProperty.indexedProperty[3].testString", "Test#3");
        assertEquals("Test#3", nestedBean.getSimpleBeanProperty().getIndexedProperty(3).getTestString(), "Cannot set property on indexed property on property");
    }

    /**
     * Tests whether a BeanIntrospector can be removed.
     */
    @Test
    public void testRemoveBeanIntrospector() {
        assertTrue(PropertyUtils.removeBeanIntrospector(DefaultBeanIntrospector.INSTANCE), "Wrong result");
        final PropertyDescriptor[] desc = PropertyUtils.getPropertyDescriptors(AlphaBean.class);
        assertEquals(0, desc.length, "Got descriptors");
        PropertyUtils.addBeanIntrospector(DefaultBeanIntrospector.INSTANCE);
    }

    /**
     * Tests whether a reset of the registered BeanIntrospectors can be performed.
     */
    @Test
    public void testResetBeanIntrospectors() {
        assertTrue(PropertyUtils.removeBeanIntrospector(DefaultBeanIntrospector.INSTANCE), "Wrong result");
        PropertyUtils.resetBeanIntrospectors();
        final PropertyDescriptor[] desc = PropertyUtils.getPropertyDescriptors(AlphaBean.class);
        assertTrue(desc.length > 0, "Got no descriptors");
    }

    /**
     * Corner cases on setIndexedProperty invalid arguments.
     */
    @Test
    public void testSetIndexedArguments() {
        // Use explicit index argument
        assertThrows(NullPointerException.class, () -> PropertyUtils.setIndexedProperty(null, "intArray", 0, Integer.valueOf(1)));
        assertThrows(NullPointerException.class, () -> PropertyUtils.setIndexedProperty(bean, null, 0, Integer.valueOf(1)));
        // Use index expression
        assertThrows(NullPointerException.class, () -> PropertyUtils.setIndexedProperty(null, "intArray[0]", Integer.valueOf(1)));
        assertThrows(NoSuchMethodException.class, () -> PropertyUtils.setIndexedProperty(bean, "[0]", Integer.valueOf(1)));
        assertThrows(IllegalArgumentException.class, () -> PropertyUtils.setIndexedProperty(bean, "intArray", Integer.valueOf(1)));
        // Use explicit index argument
        assertThrows(NullPointerException.class, () -> PropertyUtils.setIndexedProperty(null, "intIndexed", 0, Integer.valueOf(1)));
        assertThrows(NullPointerException.class, () -> PropertyUtils.setIndexedProperty(bean, null, 0, Integer.valueOf(1)));
        // Use index expression
        assertThrows(NullPointerException.class, () -> PropertyUtils.setIndexedProperty(null, "intIndexed[0]", Integer.valueOf(1)));
        assertThrows(NoSuchMethodException.class, () -> PropertyUtils.setIndexedProperty(bean, "[0]", Integer.valueOf(1)));
        assertThrows(IllegalArgumentException.class, () -> PropertyUtils.setIndexedProperty(bean, "intIndexed", Integer.valueOf(1)));
    }

    /**
     * Test setting an indexed value out of a multi-dimensional array
     */
    @Test
    public void testSetIndexedArray() throws Exception {
        final String[] firstArray = { "FIRST-1", "FIRST-2", "FIRST-3" };
        final String[] secondArray = { "SECOND-1", "SECOND-2", "SECOND-3", "SECOND-4" };
        final String[][] mainArray = { firstArray, secondArray };
        final TestBean bean = new TestBean(mainArray);
        assertEquals("SECOND-3", bean.getString2dArray(1)[2], "BEFORE");
        PropertyUtils.setProperty(bean, "string2dArray[1][2]", "SECOND-3-UPDATED");
        assertEquals("SECOND-3-UPDATED", bean.getString2dArray(1)[2], "AFTER");
    }

    /**
     * Test setting an indexed value out of List of Lists
     */
    @Test
    public void testSetIndexedList() throws Exception {
        final String[] firstArray = { "FIRST-1", "FIRST-2", "FIRST-3" };
        final String[] secondArray = { "SECOND-1", "SECOND-2", "SECOND-3", "SECOND-4" };
        final List<Object> mainList = new ArrayList<>();
        mainList.add(Arrays.asList(firstArray));
        mainList.add(Arrays.asList(secondArray));
        final TestBean bean = new TestBean(mainList);
        assertEquals("SECOND-4", ((List<?>) bean.getListIndexed().get(1)).get(3), "BEFORE");
        PropertyUtils.setProperty(bean, "listIndexed[1][3]", "SECOND-4-UPDATED");
        assertEquals("SECOND-4-UPDATED", ((List<?>) bean.getListIndexed().get(1)).get(3), "AFTER");
    }

    /**
     * Test setting a value out of a mapped Map
     */
    @Test
    public void testSetIndexedMap() throws Exception {
        final Map<String, Object> firstMap = new HashMap<>();
        firstMap.put("FIRST-KEY-1", "FIRST-VALUE-1");
        firstMap.put("FIRST-KEY-2", "FIRST-VALUE-2");
        final Map<String, Object> secondMap = new HashMap<>();
        secondMap.put("SECOND-KEY-1", "SECOND-VALUE-1");
        secondMap.put("SECOND-KEY-2", "SECOND-VALUE-2");

        final List<Object> mainList = new ArrayList<>();
        mainList.add(firstMap);
        mainList.add(secondMap);
        final TestBean bean = new TestBean(mainList);

        assertEquals(null, ((Map<?, ?>) bean.getListIndexed().get(0)).get("FIRST-NEW-KEY"), "BEFORE");
        assertEquals("SECOND-VALUE-1", ((Map<?, ?>) bean.getListIndexed().get(1)).get("SECOND-KEY-1"), "BEFORE");
        PropertyUtils.setProperty(bean, "listIndexed[0](FIRST-NEW-KEY)", "FIRST-NEW-VALUE");
        PropertyUtils.setProperty(bean, "listIndexed[1](SECOND-KEY-1)", "SECOND-VALUE-1-UPDATED");
        assertEquals("FIRST-NEW-VALUE", ((Map<?, ?>) bean.getListIndexed().get(0)).get("FIRST-NEW-KEY"), "BEFORE");
        assertEquals("SECOND-VALUE-1-UPDATED", ((Map<?, ?>) bean.getListIndexed().get(1)).get("SECOND-KEY-1"), "AFTER");
    }

    /**
     * Positive and negative tests on setIndexedProperty valid arguments.
     */
    @Test
    public void testSetIndexedValues() throws Exception {
        Object value = null;
        // Use explicit index argument
        PropertyUtils.setIndexedProperty(bean, "dupProperty", 0, "New 0");
        value = PropertyUtils.getIndexedProperty(bean, "dupProperty", 0);
        assertNotNull(value, "Returned new value 0");
        assertInstanceOf(String.class, value, "Returned String new value 0");
        assertEquals("New 0", (String) value, "Returned correct new value 0");

        PropertyUtils.setIndexedProperty(bean, "intArray", 0, Integer.valueOf(1));
        value = PropertyUtils.getIndexedProperty(bean, "intArray", 0);
        assertNotNull(value, "Returned new value 0");
        assertInstanceOf(Integer.class, value, "Returned Integer new value 0");
        assertEquals(1, ((Integer) value).intValue(), "Returned correct new value 0");

        PropertyUtils.setIndexedProperty(bean, "intIndexed", 1, Integer.valueOf(11));
        value = PropertyUtils.getIndexedProperty(bean, "intIndexed", 1);
        assertNotNull(value, "Returned new value 1");
        assertInstanceOf(Integer.class, value, "Returned Integer new value 1");
        assertEquals(11, ((Integer) value).intValue(), "Returned correct new value 1");

        PropertyUtils.setIndexedProperty(bean, "listIndexed", 2, "New Value 2");
        value = PropertyUtils.getIndexedProperty(bean, "listIndexed", 2);
        assertNotNull(value, "Returned new value 2");
        assertInstanceOf(String.class, value, "Returned String new value 2");
        assertEquals("New Value 2", (String) value, "Returned correct new value 2");

        PropertyUtils.setIndexedProperty(bean, "stringArray", 2, "New Value 2");
        value = PropertyUtils.getIndexedProperty(bean, "stringArray", 2);
        assertNotNull(value, "Returned new value 2");
        assertInstanceOf(String.class, value, "Returned String new value 2");
        assertEquals("New Value 2", (String) value, "Returned correct new value 2");

        PropertyUtils.setIndexedProperty(bean, "stringArray", 3, "New Value 3");
        value = PropertyUtils.getIndexedProperty(bean, "stringArray", 3);
        assertNotNull(value, "Returned new value 3");
        assertInstanceOf(String.class, value, "Returned String new value 3");
        assertEquals("New Value 3", (String) value, "Returned correct new value 3");

        // Use index expression

        PropertyUtils.setIndexedProperty(bean, "dupProperty[4]", "New 4");
        value = PropertyUtils.getIndexedProperty(bean, "dupProperty[4]");
        assertNotNull(value, "Returned new value 4");
        assertInstanceOf(String.class, value, "Returned String new value 4");
        assertEquals("New 4", (String) value, "Returned correct new value 4");

        PropertyUtils.setIndexedProperty(bean, "intArray[4]", Integer.valueOf(1));
        value = PropertyUtils.getIndexedProperty(bean, "intArray[4]");
        assertNotNull(value, "Returned new value 4");
        assertInstanceOf(Integer.class, value, "Returned Integer new value 4");
        assertEquals(1, ((Integer) value).intValue(), "Returned correct new value 4");

        PropertyUtils.setIndexedProperty(bean, "intIndexed[3]", Integer.valueOf(11));
        value = PropertyUtils.getIndexedProperty(bean, "intIndexed[3]");
        assertNotNull(value, "Returned new value 5");
        assertInstanceOf(Integer.class, value, "Returned Integer new value 5");
        assertEquals(11, ((Integer) value).intValue(), "Returned correct new value 5");

        PropertyUtils.setIndexedProperty(bean, "listIndexed[1]", "New Value 2");
        value = PropertyUtils.getIndexedProperty(bean, "listIndexed[1]");
        assertNotNull(value, "Returned new value 6");
        assertInstanceOf(String.class, value, "Returned String new value 6");
        assertEquals("New Value 2", (String) value, "Returned correct new value 6");

        PropertyUtils.setIndexedProperty(bean, "stringArray[1]", "New Value 2");
        value = PropertyUtils.getIndexedProperty(bean, "stringArray[2]");
        assertNotNull(value, "Returned new value 6");
        assertInstanceOf(String.class, value, "Returned String new value 6");
        assertEquals("New Value 2", (String) value, "Returned correct new value 6");

        PropertyUtils.setIndexedProperty(bean, "stringArray[0]", "New Value 3");
        value = PropertyUtils.getIndexedProperty(bean, "stringArray[0]");
        assertNotNull(value, "Returned new value 7");
        assertInstanceOf(String.class, value, "Returned String new value 7");
        assertEquals("New Value 3", (String) value, "Returned correct new value 7");

        // Index out of bounds tests
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> PropertyUtils.setIndexedProperty(bean, "dupProperty", -1, "New -1"));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> PropertyUtils.setIndexedProperty(bean, "dupProperty", 5, "New 5"));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> PropertyUtils.setIndexedProperty(bean, "intArray", -1, Integer.valueOf(0)));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> PropertyUtils.setIndexedProperty(bean, "intArray", 5, Integer.valueOf(0)));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> PropertyUtils.setIndexedProperty(bean, "intIndexed", -1, Integer.valueOf(0)));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> PropertyUtils.setIndexedProperty(bean, "intIndexed", 5, Integer.valueOf(0)));
        assertThrows(IndexOutOfBoundsException.class, () -> PropertyUtils.setIndexedProperty(bean, "listIndexed", 5, "New String"));
        assertThrows(IndexOutOfBoundsException.class, () -> PropertyUtils.setIndexedProperty(bean, "listIndexed", -1, "New String"));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> PropertyUtils.setIndexedProperty(bean, "stringArray", -1, "New String"));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> PropertyUtils.setIndexedProperty(bean, "stringArray", 5, "New String"));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> PropertyUtils.setIndexedProperty(bean, "stringIndexed", -1, "New String"));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> PropertyUtils.setIndexedProperty(bean, "stringIndexed", 5, "New String"));
    }

    /**
     * Corner cases on getMappedProperty invalid arguments.
     */
    @Test
    public void testSetMappedArguments() {
        // Use explicit key argument
        assertThrows(NullPointerException.class, () -> PropertyUtils.setMappedProperty(null, "mappedProperty", "First Key", "First Value"));
        assertThrows(NullPointerException.class, () -> PropertyUtils.setMappedProperty(bean, null, "First Key", "First Value"));
        assertThrows(NullPointerException.class, () -> PropertyUtils.setMappedProperty(bean, "mappedProperty", null, "First Value"));
        // Use key expression
        assertThrows(NullPointerException.class, () -> PropertyUtils.setMappedProperty(null, "mappedProperty(First Key)", "First Value"));
        assertThrows(NoSuchMethodException.class, () -> PropertyUtils.setMappedProperty(bean, "(Second Key)", "Second Value"));
        assertThrows(IllegalArgumentException.class, () -> PropertyUtils.setMappedProperty(bean, "mappedProperty", "Third Value"));
    }

    /**
     * Test setting an indexed value out of a mapped array
     */
    @Test
    public void testSetMappedArray() throws Exception {
        final TestBean bean = new TestBean();
        final String[] array = { "abc", "def", "ghi" };
        bean.getMapProperty().put("mappedArray", array);

        assertEquals("def", ((String[]) bean.getMapProperty().get("mappedArray"))[1], "BEFORE");
        PropertyUtils.setProperty(bean, "mapProperty(mappedArray)[1]", "DEF-UPDATED");
        assertEquals("DEF-UPDATED", ((String[]) bean.getMapProperty().get("mappedArray"))[1], "AFTER");
    }

    /**
     * Test setting an indexed value out of a mapped List
     */
    @Test
    public void testSetMappedList() throws Exception {
        final TestBean bean = new TestBean();
        final List<Object> list = new ArrayList<>();
        list.add("klm");
        list.add("nop");
        list.add("qrs");
        bean.getMapProperty().put("mappedList", list);

        assertEquals("klm", ((List<?>) bean.getMapProperty().get("mappedList")).get(0), "BEFORE");
        PropertyUtils.setProperty(bean, "mapProperty(mappedList)[0]", "KLM-UPDATED");
        assertEquals("KLM-UPDATED", ((List<?>) bean.getMapProperty().get("mappedList")).get(0), "AFTER");
    }

    /**
     * Test setting a value out of a mapped Map
     */
    @Test
    public void testSetMappedMap() throws Exception {
        final TestBean bean = new TestBean();
        final Map<String, Object> map = new HashMap<>();
        map.put("sub-key-1", "sub-value-1");
        map.put("sub-key-2", "sub-value-2");
        map.put("sub-key-3", "sub-value-3");
        bean.getMapProperty().put("mappedMap", map);

        assertEquals("sub-value-3", ((Map<?, ?>) bean.getMapProperty().get("mappedMap")).get("sub-key-3"), "BEFORE");
        PropertyUtils.setProperty(bean, "mapProperty(mappedMap)(sub-key-3)", "SUB-KEY-3-UPDATED");
        assertEquals("SUB-KEY-3-UPDATED", ((Map<?, ?>) bean.getMapProperty().get("mappedMap")).get("sub-key-3"), "AFTER");
    }

    /**
     * Test setting mapped values with periods in the key.
     */
    @Test
    public void testSetMappedPeriods() throws Exception {

        // PropertyUtils.setMappedProperty()--------
        bean.setMappedProperty("key.with.a.dot", "Special Value");
        assertEquals("Special Value", bean.getMappedProperty("key.with.a.dot"), "Can retrieve directly (A)");

        PropertyUtils.setMappedProperty(bean, "mappedProperty", "key.with.a.dot", "Updated Special Value");
        assertEquals("Updated Special Value", bean.getMappedProperty("key.with.a.dot"), "Check set via setMappedProperty");

        // PropertyUtils.setNestedProperty()
        bean.setMappedProperty("key.with.a.dot", "Special Value");
        assertEquals("Special Value", bean.getMappedProperty("key.with.a.dot"), "Can retrieve directly (B)");
        PropertyUtils.setNestedProperty(bean, "mappedProperty(key.with.a.dot)", "Updated Special Value");
        assertEquals("Updated Special Value", bean.getMappedProperty("key.with.a.dot"), "Check set via setNestedProperty (B)");

        // PropertyUtils.setNestedProperty()
        final TestBean testBean = new TestBean();
        bean.setMappedObjects("nested.property", testBean);
        assertEquals("This is a string", testBean.getStringProperty(), "Can retrieve directly (C)");
        PropertyUtils.setNestedProperty(bean, "mappedObjects(nested.property).stringProperty", "Updated String Value");
        assertEquals("Updated String Value", testBean.getStringProperty(), "Check set via setNestedProperty (C)");

        // PropertyUtils.setNestedProperty()
        bean.getNested().setMappedProperty("Mapped Key", "Nested Mapped Value");
        assertEquals("Nested Mapped Value", PropertyUtils.getNestedProperty(bean, "nested.mappedProperty(Mapped Key)"),
                "Can retrieve via getNestedProperty (D)");
        PropertyUtils.setNestedProperty(bean, "nested.mappedProperty(Mapped Key)", "Updated Nested Mapped Value");
        assertEquals("Updated Nested Mapped Value", PropertyUtils.getNestedProperty(bean, "nested.mappedProperty(Mapped Key)"),
                "Check set via setNestedProperty (D)");
    }

    /**
     * Positive and negative tests on setMappedProperty valid arguments.
     */
    @Test
    public void testSetMappedValues() throws Exception {
        Object value = null;
        // Use explicit key argument
        value = PropertyUtils.getMappedProperty(bean, "mappedProperty", "Fourth Key");
        assertNull(value, "Can not find fourth value");

        PropertyUtils.setMappedProperty(bean, "mappedProperty", "Fourth Key", "Fourth Value");

        value = PropertyUtils.getMappedProperty(bean, "mappedProperty", "Fourth Key");
        assertEquals("Fourth Value", value, "Can find fourth value");

        // Use key expression with parentheses
        value = PropertyUtils.getMappedProperty(bean, "mappedProperty(Fifth Key)");
        assertNull(value, "Can not find fifth value");

        PropertyUtils.setMappedProperty(bean, "mappedProperty(Fifth Key)", "Fifth Value");

        value = PropertyUtils.getMappedProperty(bean, "mappedProperty(Fifth Key)");
        assertEquals("Fifth Value", value, "Can find fifth value");

        // Use key expression with dotted expression

        value = PropertyUtils.getNestedProperty(bean, "mapProperty.Sixth Key");
        assertNull(value, "Can not find sixth value");

        PropertyUtils.setNestedProperty(bean, "mapProperty.Sixth Key", "Sixth Value");

        value = PropertyUtils.getNestedProperty(bean, "mapProperty.Sixth Key");
        assertEquals("Sixth Value", value, "Can find sixth value");
    }

    /**
     * Corner cases on setNestedProperty invalid arguments.
     */
    @Test
    public void testSetNestedArguments() {
        assertThrows(NullPointerException.class, () -> PropertyUtils.setNestedProperty(null, "stringProperty", ""));
        assertThrows(NullPointerException.class, () -> PropertyUtils.setNestedProperty(bean, null, ""));
    }

    /**
     * Test setNextedProperty on a boolean property.
     */
    @Test
    public void testSetNestedBoolean() throws Exception {
        final boolean oldValue = bean.getNested().getBooleanProperty();
        final boolean newValue = !oldValue;
        PropertyUtils.setNestedProperty(bean, "nested.booleanProperty", Boolean.valueOf(newValue));
        assertEquals(newValue, bean.getNested().getBooleanProperty(), "Matched new value");
    }

    /**
     * Test setNestedProperty on a double property.
     */
    @Test
    public void testSetNestedDouble() throws Exception {
        final double oldValue = bean.getNested().getDoubleProperty();
        final double newValue = oldValue + 1.0;
        PropertyUtils.setNestedProperty(bean, "nested.doubleProperty", Double.valueOf(newValue));
        assertEquals(newValue, bean.getNested().getDoubleProperty(), 0.005, "Matched new value");
    }

    /**
     * Test setNestedProperty on a float property.
     */
    @Test
    public void testSetNestedFloat() throws Exception {
        final float oldValue = bean.getNested().getFloatProperty();
        final float newValue = oldValue + (float) 1.0;
        PropertyUtils.setNestedProperty(bean, "nested.floatProperty", Float.valueOf(newValue));
        assertEquals(newValue, bean.getNested().getFloatProperty(), (float) 0.005, "Matched new value");
    }

    /**
     * Test setNestedProperty on a int property.
     */
    @Test
    public void testSetNestedInt() throws Exception {
        final int oldValue = bean.getNested().getIntProperty();
        final int newValue = oldValue + 1;
        PropertyUtils.setNestedProperty(bean, "nested.intProperty", Integer.valueOf(newValue));
        assertEquals(newValue, bean.getNested().getIntProperty(), "Matched new value");
    }

    /**
     * Test setNestedProperty on a long property.
     */
    @Test
    public void testSetNestedLong() throws Exception {
        final long oldValue = bean.getNested().getLongProperty();
        final long newValue = oldValue + 1;
        PropertyUtils.setNestedProperty(bean, "nested.longProperty", Long.valueOf(newValue));
        assertEquals(newValue, bean.getNested().getLongProperty(), "Matched new value");
    }

    /**
     * Test setNestedProperty on a read-only String property.
     */
    @Test
    public void testSetNestedReadOnly() throws Exception {
        final String oldValue = bean.getNested().getWriteOnlyPropertyValue();
        final String newValue = oldValue + " Extra Value";
        assertThrows(NoSuchMethodException.class, () -> PropertyUtils.setNestedProperty(bean, "nested.readOnlyProperty", newValue));
    }

    /**
     * Test setNestedProperty on a short property.
     */
    @Test
    public void testSetNestedShort() throws Exception {
        final short oldValue = bean.getNested().getShortProperty();
        short newValue = oldValue;
        newValue++;
        PropertyUtils.setNestedProperty(bean, "nested.shortProperty", Short.valueOf(newValue));
        assertEquals(newValue, bean.getNested().getShortProperty(), "Matched new value");
    }

    /**
     * Test setNestedProperty on a String property.
     */
    @Test
    public void testSetNestedString() throws Exception {
        final String oldValue = bean.getNested().getStringProperty();
        final String newValue = oldValue + " Extra Value";
        PropertyUtils.setNestedProperty(bean, "nested.stringProperty", newValue);
        assertEquals(newValue, bean.getNested().getStringProperty(), "Matched new value");
    }

    /**
     * Test setNestedProperty on an unknown property name.
     */
    @Test
    public void testSetNestedUnknown() throws Exception {
        final String newValue = "New String Value";
        assertThrows(NoSuchMethodException.class, () -> PropertyUtils.setNestedProperty(bean, "nested.unknown", newValue));
    }

    /**
     * Test setNestedProperty on a write-only String property.
     */
    @Test
    public void testSetNestedWriteOnly() throws Exception {
        final String oldValue = bean.getNested().getWriteOnlyPropertyValue();
        final String newValue = oldValue + " Extra Value";
        PropertyUtils.setNestedProperty(bean, "nested.writeOnlyProperty", newValue);
        assertEquals(newValue, bean.getNested().getWriteOnlyPropertyValue(), "Matched new value");
    }

    @Test
    public void testSetNoGetter() throws Exception {
        final BetaBean bean = new BetaBean("Cedric");

        // test standard no getter
        bean.setNoGetterProperty("Sigma");
        assertEquals("Sigma", bean.getSecret(), "BetaBean test failed");

        assertNotNull(PropertyUtils.getPropertyDescriptor(bean, "noGetterProperty"), "Descriptor is null");

        BeanUtils.setProperty(bean, "noGetterProperty", "Omega");
        assertEquals("Omega", bean.getSecret(), "Cannot set no-getter property");

        // test mapped no getter descriptor
        assertNotNull(PropertyUtils.getPropertyDescriptor(bean, "noGetterMappedProperty"), "Map Descriptor is null");

        PropertyUtils.setMappedProperty(bean, "noGetterMappedProperty", "Epsilon", "Epsilon");
        assertEquals("MAP:Epsilon", bean.getSecret(), "Cannot set mapped no-getter property");
    }

    /**
     * Test accessing a public sub-bean of a package scope bean
     */
    @Test
    public void testSetPublicSubBean_of_PackageBean() throws Exception {

        final PublicSubBean bean = new PublicSubBean();
        bean.setFoo("foo-start");
        bean.setBar("bar-start");
        // Set Foo
        PropertyUtils.setProperty(bean, "foo", "foo-updated");
        assertEquals("foo-updated", bean.getFoo(), "foo property");
        // Set Bar
        PropertyUtils.setProperty(bean, "bar", "bar-updated");
        assertEquals("bar-updated", bean.getBar(), "bar property");
    }

    /**
     * Corner cases on setSimpleProperty invalid arguments.
     */
    @Test
    public void testSetSimpleArguments() {
        assertThrows(NullPointerException.class, () -> PropertyUtils.setSimpleProperty(null, "stringProperty", ""));
        assertThrows(NullPointerException.class, () -> PropertyUtils.setSimpleProperty(bean, null, ""));
    }

    /**
     * Test setSimpleProperty on a boolean property.
     */
    @Test
    public void testSetSimpleBoolean() throws Exception {
        final boolean oldValue = bean.getBooleanProperty();
        final boolean newValue = !oldValue;
        PropertyUtils.setSimpleProperty(bean, "booleanProperty", Boolean.valueOf(newValue));
        assertEquals(newValue, bean.getBooleanProperty(), "Matched new value");
    }

    /**
     * Test setSimpleProperty on a double property.
     */
    @Test
    public void testSetSimpleDouble() throws Exception {
        final double oldValue = bean.getDoubleProperty();
        final double newValue = oldValue + 1.0;
        PropertyUtils.setSimpleProperty(bean, "doubleProperty", Double.valueOf(newValue));
        assertEquals(newValue, bean.getDoubleProperty(), 0.005, "Matched new value");
    }

    /**
     * Test setSimpleProperty on a float property.
     */
    @Test
    public void testSetSimpleFloat() throws Exception {
        final float oldValue = bean.getFloatProperty();
        final float newValue = oldValue + (float) 1.0;
        PropertyUtils.setSimpleProperty(bean, "floatProperty", Float.valueOf(newValue));
        assertEquals(newValue, bean.getFloatProperty(), (float) 0.005, "Matched new value");
    }

    /**
     * Negative test setSimpleProperty on an indexed property.
     */
    @Test
    public void testSetSimpleIndexed() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> PropertyUtils.setSimpleProperty(bean, "stringIndexed[0]", "New String Value"));
    }

    /**
     * Test setSimpleProperty on a int property.
     */
    @Test
    public void testSetSimpleInt() throws Exception {
        final int oldValue = bean.getIntProperty();
        final int newValue = oldValue + 1;
        PropertyUtils.setSimpleProperty(bean, "intProperty", Integer.valueOf(newValue));
        assertEquals(newValue, bean.getIntProperty(), "Matched new value");
    }

    /**
     * Test setSimpleProperty on a long property.
     */
    @Test
    public void testSetSimpleLong() throws Exception {
        final long oldValue = bean.getLongProperty();
        final long newValue = oldValue + 1;
        PropertyUtils.setSimpleProperty(bean, "longProperty", Long.valueOf(newValue));
        assertEquals(newValue, bean.getLongProperty(), "Matched new value");
    }

    /**
     * Negative test setSimpleProperty on a nested property.
     */
    @Test
    public void testSetSimpleNested() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> PropertyUtils.setSimpleProperty(bean, "nested.stringProperty", "New String Value"));
    }

    /**
     * Test setSimpleProperty on a read-only String property.
     */
    @Test
    public void testSetSimpleReadOnly() throws Exception {
        final String oldValue = bean.getWriteOnlyPropertyValue();
        final String newValue = oldValue + " Extra Value";
        final NoSuchMethodException e = assertThrows(NoSuchMethodException.class, () -> PropertyUtils.setSimpleProperty(bean, "readOnlyProperty", newValue));
        assertEquals("Property 'readOnlyProperty' has no setter method in class '" + bean.getClass() + "'", e.getMessage());
    }

    /**
     * Test setSimpleProperty on a short property.
     */
    @Test
    public void testSetSimpleShort() throws Exception {
        final short oldValue = bean.getShortProperty();
        short newValue = oldValue;
        newValue++;
        PropertyUtils.setSimpleProperty(bean, "shortProperty", Short.valueOf(newValue));
        assertEquals(newValue, bean.getShortProperty(), "Matched new value");
    }

    /**
     * Test setSimpleProperty on a String property.
     */
    @Test
    public void testSetSimpleString() throws Exception {
        final String oldValue = bean.getStringProperty();
        final String newValue = oldValue + " Extra Value";
        PropertyUtils.setSimpleProperty(bean, "stringProperty", newValue);
        assertEquals(newValue, bean.getStringProperty(), "Matched new value");
    }

    /**
     * Test setSimpleProperty on an unknown property name.
     */
    @Test
    public void testSetSimpleUnknown() throws Exception {
        final String newValue = "New String Value";
        final NoSuchMethodException e = assertThrows(NoSuchMethodException.class, () -> PropertyUtils.setSimpleProperty(bean, "unknown", newValue));
        assertEquals("Unknown property 'unknown' on class '" + bean.getClass() + "'", e.getMessage());
    }

    /**
     * Test setSimpleProperty on a write-only String property.
     */
    @Test
    public void testSetSimpleWriteOnly() throws Exception {
        final String oldValue = bean.getWriteOnlyPropertyValue();
        final String newValue = oldValue + " Extra Value";
        PropertyUtils.setSimpleProperty(bean, "writeOnlyProperty", newValue);
        assertEquals(newValue, bean.getWriteOnlyPropertyValue(), "Matched new value");
    }

    /**
     * When a bean has a null property which is reference by the standard access language, this should throw a NestedNullException.
     */
    @Test
    public void testThrowNestedNull() throws Exception {
        final NestedTestBean nestedBean = new NestedTestBean("base");
        // don't init!
        assertThrows(NestedNullException.class, () -> PropertyUtils.getProperty(nestedBean, "simpleBeanProperty.indexedProperty[0]"));
    }
}
