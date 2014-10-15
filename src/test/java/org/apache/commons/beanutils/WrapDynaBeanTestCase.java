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

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * <p>Test Case for the <code>WrapDynaBean</code> implementation class.
 * These tests were based on the ones in <code>PropertyUtilsTestCase</code>
 * because the two classes provide similar levels of functionality.</p>
 *
 * @version $Id$
 */

public class WrapDynaBeanTestCase extends BasicDynaBeanTestCase {


    // ---------------------------------------------------- Instance Variables


    // ---------------------------------------------------------- Constructors


    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public WrapDynaBeanTestCase(final String name) {

        super(name);

    }


    // -------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    @Override
    public void setUp() throws Exception {

        bean = new WrapDynaBean(new TestBean());

    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {

        return (new TestSuite(WrapDynaBeanTestCase.class));

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
     * The <code>set()</code> method.
     */
    public void testSimpleProperties() {

        checkSimplePropertyAccess();

    }


    /**
     * Helper method for testing whether basic access to properties works as
     * expected.
     */
    private void checkSimplePropertyAccess() {
        // Invalid getter
        try {
            bean.get("invalidProperty");
            fail("Invalid get should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException t) {
            // Expected result
        }

        // Invalid setter
        try {
            bean.set("invalidProperty", "XYZ");
            fail("Invalid set should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException t) {
            // Expected result
        }

        // Set up initial Value
        String testValue = "Original Value";
        final String testProperty = "stringProperty";
        final TestBean instance = (TestBean)((WrapDynaBean)bean).getInstance();
        instance.setStringProperty(testValue);
        assertEquals("Check String property", testValue, instance.getStringProperty());

        // Test Valid Get & Set
        try {
            testValue = "Some new value";
            bean.set(testProperty, testValue);
            assertEquals("Test Set", testValue, instance.getStringProperty());
            assertEquals("Test Get", testValue, bean.get(testProperty));
        } catch (final IllegalArgumentException t) {
            fail("Get threw exception: " + t);
        }
    }

    /**
     * The <code>set()</code> method.
     */
    public void testIndexedProperties() {

        // Invalid getter
        try {
            bean.get("invalidProperty", 0);
            fail("Invalid get should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException t) {
            // Expected result
        }

        // Invalid setter
        try {
            bean.set("invalidProperty", 0, "XYZ");
            fail("Invalid set should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException t) {
            // Expected result
        }

        // Set up initial Value
        String testValue = "Original Value";
        final String testProperty = "stringIndexed";
        final TestBean instance = (TestBean)((WrapDynaBean)bean).getInstance();
        instance.setStringIndexed(0, testValue);
        assertEquals("Check String property", testValue, instance.getStringIndexed(0));

        // Test Valid Get & Set
        try {
            testValue = "Some new value";
            bean.set(testProperty, 0, testValue);
            assertEquals("Test Set", testValue, instance.getStringIndexed(0));
            assertEquals("Test Get", testValue, bean.get(testProperty, 0));
        } catch (final IllegalArgumentException t) {
            fail("Get threw exception: " + t);
        }

    }

    /**
     * The <code>contains()</code> method is not supported by the
     * <code>WrapDynaBean</code> implementation class.
     */
    @Override
    public void testMappedContains() {

        try {
            assertTrue("Can see first key",
                    bean.contains("mappedProperty", "First Key"));
            fail("Should have thrown UnsupportedOperationException");
        } catch (final UnsupportedOperationException t) {
            // Expected result
        } catch (final Throwable t) {
            fail("Exception: " + t);
        }


        try {
            assertTrue("Can not see unknown key",
                    !bean.contains("mappedProperty", "Unknown Key"));
            fail("Should have thrown UnsupportedOperationException");
        } catch (final UnsupportedOperationException t) {
            // Expected result
        } catch (final Throwable t) {
            fail("Exception: " + t);
        }

    }


    /**
     * The <code>remove()</code> method is not supported by the
     * <code>WrapDynaBean</code> implementation class.
     */
    @Override
    public void testMappedRemove() {

        try {
            assertTrue("Can see first key",
                    bean.contains("mappedProperty", "First Key"));
            bean.remove("mappedProperty", "First Key");
            fail("Should have thrown UnsupportedOperationException");
            //            assertTrue("Can not see first key",
            //         !bean.contains("mappedProperty", "First Key"));
        } catch (final UnsupportedOperationException t) {
            // Expected result
        } catch (final Throwable t) {
            fail("Exception: " + t);
        }

        try {
            assertTrue("Can not see unknown key",
                    !bean.contains("mappedProperty", "Unknown Key"));
            bean.remove("mappedProperty", "Unknown Key");
            fail("Should have thrown UnsupportedOperationException");
            //            assertTrue("Can not see unknown key",
            //         !bean.contains("mappedProperty", "Unknown Key"));
        } catch (final UnsupportedOperationException t) {
            // Expected result
        } catch (final Throwable t) {
            fail("Exception: " + t);
        }

    }

    /** Tests getInstance method */
    public void testGetInstance() {
        final AlphaBean alphaBean = new AlphaBean("Now On Air... John Peel");
        final WrapDynaBean dynaBean = new WrapDynaBean(alphaBean);
        final Object wrappedInstance = dynaBean.getInstance();
        assertTrue("Object type is AlphaBean", wrappedInstance instanceof AlphaBean);
        final AlphaBean wrappedAlphaBean = (AlphaBean) wrappedInstance;
        assertTrue("Same Object", wrappedAlphaBean == alphaBean);
    }

    /** Tests the newInstance implementation for WrapDynaClass */
    public void testNewInstance() throws Exception {
        final WrapDynaClass dynaClass = WrapDynaClass.createDynaClass(AlphaBean.class);
        final Object createdInstance = dynaClass.newInstance();
        assertTrue("Object type is WrapDynaBean", createdInstance instanceof WrapDynaBean);
        final WrapDynaBean dynaBean = (WrapDynaBean) createdInstance;
        assertTrue("Object type is AlphaBean", dynaBean.getInstance() instanceof AlphaBean);
    }


    /**
     * Serialization and deserialization tests.
     * (WrapDynaBean is now serializable, although WrapDynaClass still is not)
     */
    @Override
    public void testSerialization() {

        // Create a bean and set a value
        final WrapDynaBean origBean = new WrapDynaBean(new TestBean());
        final Integer newValue = new Integer(789);
        assertEquals("origBean default", new Integer(123), origBean.get("intProperty"));
        origBean.set("intProperty", newValue);
        assertEquals("origBean new value", newValue, origBean.get("intProperty"));

        // Serialize/Deserialize & test value
        final WrapDynaBean bean = (WrapDynaBean)serializeDeserialize(origBean, "First Test");
        assertEquals("bean value", newValue, bean.get("intProperty"));

    }

    /**
     * Do serialization and deserialization.
     */
    private Object serializeDeserialize(final Object target, final String text) {

        // Serialize the test object
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            final ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(target);
            oos.flush();
            oos.close();
        } catch (final Exception e) {
            fail(text + ": Exception during serialization: " + e);
        }

        // Deserialize the test object
        Object result = null;
        try {
            final ByteArrayInputStream bais =
                new ByteArrayInputStream(baos.toByteArray());
            final ObjectInputStream ois = new ObjectInputStream(bais);
            result = ois.readObject();
            bais.close();
        } catch (final Exception e) {
            fail(text + ": Exception during deserialization: " + e);
        }
        return result;

    }

    /**
     * Tests whether a WrapDynaClass can be provided when constructing a bean.
     */
    public void testInitWithDynaClass() {
        final WrapDynaClass clazz = WrapDynaClass.createDynaClass(TestBean.class);
        bean = new WrapDynaBean(new TestBean(), clazz);
        assertSame("Wrong DynaClass", clazz, bean.getDynaClass());
        checkSimplePropertyAccess();
    }

    /**
     * Tests whether caching works for WrapDynaClass objects.
     */
    public void testGetWrapDynaClassFromCache() {
        final WrapDynaClass clazz = WrapDynaClass.createDynaClass(TestBean.class);
        assertSame("Instance not cached", clazz,
                WrapDynaClass.createDynaClass(TestBean.class));
    }

    /**
     * Tests whether the PropertyUtilsBean instance associated with a WrapDynaClass is
     * taken into account when accessing an instance from the cache.
     */
    public void testGetWrapDynaClassFromCacheWithPropUtils() {
        final WrapDynaClass clazz = WrapDynaClass.createDynaClass(TestBean.class);
        final PropertyUtilsBean pu = new PropertyUtilsBean();
        final WrapDynaClass clazz2 = WrapDynaClass.createDynaClass(TestBean.class, pu);
        assertNotSame("Got same instance from cache", clazz, clazz2);
    }

    /**
     * Tests whether a custom PropertyUtilsBean instance can be used for introspection of
     * bean properties.
     */
    public void testIntrospectionWithCustomPropUtils() {
        final PropertyUtilsBean pu = new PropertyUtilsBean();
        pu.addBeanIntrospector(new FluentPropertyBeanIntrospector());
        final WrapDynaClass dynaClass = WrapDynaClass.createDynaClass(
                FluentIntrospectionTestBean.class, pu);
        final FluentIntrospectionTestBean obj = new FluentIntrospectionTestBean();
        bean = new WrapDynaBean(obj, dynaClass);
        bean.set("fluentProperty", "testvalue");
        assertEquals("Property not set", "testvalue", obj.getStringProperty());
    }
}
