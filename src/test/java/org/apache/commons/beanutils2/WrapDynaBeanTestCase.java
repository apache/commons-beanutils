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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * Test Case for the {@code WrapDynaBean} implementation class. These tests were based on the ones in {@code PropertyUtilsTestCase} because the two classes
 * provide similar levels of functionality.
 * </p>
 */
public class WrapDynaBeanTestCase extends BasicDynaBeanTestCase {

    /**
     * Helper method for testing whether basic access to properties works as expected.
     */
    private void checkSimplePropertyAccess() {
        // Invalid getter
        assertThrows(IllegalArgumentException.class, () -> bean.get("invalidProperty"));

        // Invalid setter
        assertThrows(IllegalArgumentException.class, () -> bean.set("invalidProperty", "XYZ"));

        // Set up initial Value
        String testValue = "Original Value";
        final String testProperty = "stringProperty";
        final TestBean instance = (TestBean) ((WrapDynaBean) bean).getInstance();
        instance.setStringProperty(testValue);
        assertEquals(testValue, instance.getStringProperty(), "Check String property");

        // Test Valid Get & Set
        testValue = "Some new value";
        bean.set(testProperty, testValue);
        assertEquals(testValue, instance.getStringProperty(), "Test Set");
        assertEquals(testValue, bean.get(testProperty), "Test Get");
    }

    /**
     * Do serialization and deserialization.
     */
    private Object serializeDeserialize(final Object target, final String text) throws IOException, ClassNotFoundException {
        // Serialize the test object
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (final ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(target);
            oos.flush();
        }
        // Deserialize the test object
        Object result = null;
        try (final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                final ObjectInputStream ois = new ObjectInputStream(bais)) {
            result = ois.readObject();
        }
        return result;
    }

    /**
     * Sets up instance variables required by this test case.
     */
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        bean = new WrapDynaBean(new TestBean());
    }

    /**
     * Tear down instance variables required by this test case.
     */
    @Override
    @AfterEach
    public void tearDown() {
        bean = null;
    }

    /** Tests getInstance method */
    @Test
    public void testGetInstance() {
        final AlphaBean alphaBean = new AlphaBean("Now On Air... John Peel");
        final WrapDynaBean dynaBean = new WrapDynaBean(alphaBean);
        final Object wrappedInstance = dynaBean.getInstance();
        assertInstanceOf(AlphaBean.class, wrappedInstance, "Object type is AlphaBean");
        final AlphaBean wrappedAlphaBean = (AlphaBean) wrappedInstance;
        assertSame(wrappedAlphaBean, alphaBean, "Same Object");
    }

    /**
     * Tests whether caching works for WrapDynaClass objects.
     */
    @Test
    public void testGetWrapDynaClassFromCache() {
        final WrapDynaClass clazz = WrapDynaClass.createDynaClass(TestBean.class);
        assertSame(clazz, WrapDynaClass.createDynaClass(TestBean.class), "Instance not cached");
    }

    /**
     * Tests whether the PropertyUtilsBean instance associated with a WrapDynaClass is taken into account when accessing an instance from the cache.
     */
    @Test
    public void testGetWrapDynaClassFromCacheWithPropUtils() {
        final WrapDynaClass clazz = WrapDynaClass.createDynaClass(TestBean.class);
        final PropertyUtilsBean pu = new PropertyUtilsBean();
        final WrapDynaClass clazz2 = WrapDynaClass.createDynaClass(TestBean.class, pu);
        assertNotSame(clazz, clazz2, "Got same instance from cache");
    }

    /**
     * The {@code set()} method.
     */
    @Test
    public void testIndexedProperties() {

        // Invalid getter
        assertThrows(IllegalArgumentException.class, () -> bean.get("invalidProperty", 0));

        // Invalid setter
        assertThrows(IllegalArgumentException.class, () -> bean.set("invalidProperty", 0, "XYZ"));

        // Set up initial Value
        String testValue = "Original Value";
        final String testProperty = "stringIndexed";
        final TestBean instance = (TestBean) ((WrapDynaBean) bean).getInstance();
        instance.setStringIndexed(0, testValue);
        assertEquals(testValue, instance.getStringIndexed(0), "Check String property");

        // Test Valid Get & Set
        testValue = "Some new value";
        bean.set(testProperty, 0, testValue);
        assertEquals(testValue, instance.getStringIndexed(0), "Test Set");
        assertEquals(testValue, bean.get(testProperty, 0), "Test Get");
    }

    /**
     * Tests whether a WrapDynaClass can be provided when constructing a bean.
     */
    @Test
    public void testInitWithDynaClass() {
        final WrapDynaClass clazz = WrapDynaClass.createDynaClass(TestBean.class);
        bean = new WrapDynaBean(new TestBean(), clazz);
        assertSame(clazz, bean.getDynaClass(), "Wrong DynaClass");
        checkSimplePropertyAccess();
    }

    /**
     * Tests whether a custom PropertyUtilsBean instance can be used for introspection of bean properties.
     */
    @Test
    public void testIntrospectionWithCustomPropUtils() {
        final PropertyUtilsBean pu = new PropertyUtilsBean();
        pu.addBeanIntrospector(new FluentPropertyBeanIntrospector());
        final WrapDynaClass dynaClass = WrapDynaClass.createDynaClass(FluentIntrospectionTestBean.class, pu);
        final FluentIntrospectionTestBean obj = new FluentIntrospectionTestBean();
        bean = new WrapDynaBean(obj, dynaClass);
        bean.set("fluentProperty", "testvalue");
        assertEquals("testvalue", obj.getStringProperty(), "Property not set");
    }

    /**
     * The {@code contains()} method is not supported by the {@code WrapDynaBean} implementation class.
     */
    @Override
    @Test
    public void testMappedContains() {
        assertThrows(UnsupportedOperationException.class, () -> bean.contains("mappedProperty", "First Key"));
        assertThrows(UnsupportedOperationException.class, () -> bean.contains("mappedProperty", "Unknown Key"));
    }

    /**
     * The {@code remove()} method is not supported by the {@code WrapDynaBean} implementation class.
     */
    @Override
    @Test
    public void testMappedRemove() {

        assertThrows(UnsupportedOperationException.class, () -> bean.contains("mappedProperty", "First Key"));
        assertThrows(UnsupportedOperationException.class, () -> bean.remove("mappedProperty", "First Key"));

        assertThrows(UnsupportedOperationException.class, () -> bean.contains("mappedProperty", "Unknown Key"));
        assertThrows(UnsupportedOperationException.class, () -> bean.remove("mappedProperty", "Unknown Key"));

    }

    /** Tests the newInstance implementation for WrapDynaClass */
    @Test
    public void testNewInstance() throws Exception {
        final WrapDynaClass dynaClass = WrapDynaClass.createDynaClass(AlphaBean.class);
        final Object createdInstance = dynaClass.newInstance();
        assertInstanceOf(WrapDynaBean.class, createdInstance, "Object type is WrapDynaBean");
        final WrapDynaBean dynaBean = (WrapDynaBean) createdInstance;
        assertInstanceOf(AlphaBean.class, dynaBean.getInstance(), "Object type is AlphaBean");
    }

    /**
     * Serialization and deserialization tests. (WrapDynaBean is now serializable, although WrapDynaClass still is not)
     */
    @Override
    @Test
    public void testSerialization() throws Exception {

        // Create a bean and set a value
        final WrapDynaBean origBean = new WrapDynaBean(new TestBean());
        final Integer newValue = Integer.valueOf(789);
        assertEquals(Integer.valueOf(123), origBean.get("intProperty"), "origBean default");
        origBean.set("intProperty", newValue);
        assertEquals(newValue, origBean.get("intProperty"), "origBean new value");

        // Serialize/Deserialize & test value
        final WrapDynaBean bean = (WrapDynaBean) serializeDeserialize(origBean, "First Test");
        assertEquals(newValue, bean.get("intProperty"), "bean value");

    }

    /**
     * The {@code set()} method.
     */
    @Test
    public void testSimpleProperties() {

        checkSimplePropertyAccess();

    }
}
