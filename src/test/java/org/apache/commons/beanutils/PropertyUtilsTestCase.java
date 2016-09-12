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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.beanutils.priv.PrivateBeanFactory;
import org.apache.commons.beanutils.priv.PrivateDirect;
import org.apache.commons.beanutils.priv.PublicSubBean;


/**
 * <p>Test Case for the PropertyUtils class.  The majority of these tests use
 * instances of the TestBean class, so be sure to update the tests if you
 * change the characteristics of that class.</p>
 *
 * <p>So far, this test case has tests for the following methods of the
 * <code>PropertyUtils</code> class:</p>
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
 *
 * @version $Id$
 */

public class PropertyUtilsTestCase extends TestCase {


    // ---------------------------------------------------- Instance Variables


    /**
     * The fully qualified class name of our private directly
     * implemented interface.
     */
    private static final String PRIVATE_DIRECT_CLASS =
            "org.apache.commons.beanutils.priv.PrivateDirect";


    /**
     * The fully qualified class name of our private indirectly
     * implemented interface.
     */
    private static final String PRIVATE_INDIRECT_CLASS =
            "org.apache.commons.beanutils.priv.PrivateIndirect";


    /**
     * The fully qualified class name of our test bean class.
     */
    private static final String TEST_BEAN_CLASS =
            "org.apache.commons.beanutils.TestBean";


    /**
     * The basic test bean for each test.
     */
    protected TestBean bean = null;


    /**
     * The "package private subclass" test bean for each test.
     */
    protected TestBeanPackageSubclass beanPackageSubclass = null;


    /**
     * The test bean for private access tests.
     */
    protected PrivateDirect beanPrivate = null;


    /**
     * The test bean for private access tests of subclasses.
     */
    protected PrivateDirect beanPrivateSubclass = null;


    /**
     * The "public subclass" test bean for each test.
     */
    protected TestBeanPublicSubclass beanPublicSubclass = null;


    /**
     * The set of properties that should be described.
     */
    protected String describes[] =
    { "booleanProperty",
      "booleanSecond",
      "doubleProperty",
      "floatProperty",
      "intArray",
      //      "intIndexed",
      "intProperty",
      "listIndexed",
      "longProperty",
      //      "mappedObjects",
      //      "mappedProperty",
      //      "mappedIntProperty",
      "nested",
      "nullProperty",
      //      "readOnlyProperty",
      "shortProperty",
      "stringArray",
      //      "stringIndexed",
      "stringProperty"
    };


    /**
     * The set of property names we expect to have returned when calling
     * <code>getPropertyDescriptors()</code>.  You should update this list
     * when new properties are added to TestBean.
     */
    protected final static String[] properties = {
        "booleanProperty",
        "booleanSecond",
        "doubleProperty",
        "dupProperty",
        "floatProperty",
        "intArray",
        "intIndexed",
        "intProperty",
        "listIndexed",
        "longProperty",
        "nested",
        "nullProperty",
        "readOnlyProperty",
        "shortProperty",
        "stringArray",
        "stringIndexed",
        "stringProperty",
        "writeOnlyProperty",
    };


    // ---------------------------------------------------------- Constructors


    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public PropertyUtilsTestCase(final String name) {

        super(name);

    }


    // -------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    @Override
    public void setUp() {

        bean = new TestBean();
        beanPackageSubclass = new TestBeanPackageSubclass();
        beanPrivate = PrivateBeanFactory.create();
        beanPrivateSubclass = PrivateBeanFactory.createSubclass();
        beanPublicSubclass = new TestBeanPublicSubclass();

        final DynaProperty[] properties = new DynaProperty[] {
                new DynaProperty("stringProperty", String.class),
                new DynaProperty("nestedBean", TestBean.class),
                new DynaProperty("nullDynaBean", DynaBean.class)
                };
        final BasicDynaClass dynaClass = new BasicDynaClass("nestedDynaBean", BasicDynaBean.class, properties);
        final BasicDynaBean nestedDynaBean = new BasicDynaBean(dynaClass);
        nestedDynaBean.set("nestedBean", bean);
        bean.setNestedDynaBean(nestedDynaBean);
        PropertyUtils.clearDescriptors();
    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {

        return (new TestSuite(PropertyUtilsTestCase.class));

    }


    /**
     * Tear down instance variables required by this test case.
     */
    @Override
    public void tearDown() {

        bean = null;
        beanPackageSubclass = null;
        beanPrivate = null;
        beanPrivateSubclass = null;
        beanPublicSubclass = null;

        PropertyUtils.resetBeanIntrospectors();
    }



    // ------------------------------------------------ Individual Test Methods


    /**
     * Test copyProperties() when the origin is a a <code>Map</code>.
     */
    public void testCopyPropertiesMap() {

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("booleanProperty", Boolean.FALSE);
        map.put("doubleProperty", new Double(333.0));
        map.put("dupProperty", new String[] { "New 0", "New 1", "New 2" });
        map.put("floatProperty", new Float((float) 222.0));
        map.put("intArray", new int[] { 0, 100, 200 });
        map.put("intProperty", new Integer(111));
        map.put("longProperty", new Long(444));
        map.put("shortProperty", new Short((short) 555));
        map.put("stringProperty", "New String Property");

        try {
            PropertyUtils.copyProperties(bean, map);
        } catch (final Throwable t) {
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
                     Boolean.TRUE, map.get("booleanProperty"));
        assertEquals("Value of 'doubleProperty'",
                     new Double(321.0), map.get("doubleProperty"));
        assertEquals("Value of 'floatProperty'",
                     new Float((float) 123.0), map.get("floatProperty"));
        assertEquals("Value of 'intProperty'",
                     new Integer(123), map.get("intProperty"));
        assertEquals("Value of 'longProperty'",
                     new Long(321), map.get("longProperty"));
        assertEquals("Value of 'shortProperty'",
                     new Short((short) 987), map.get("shortProperty"));
        assertEquals("Value of 'stringProperty'",
                     "This is a string",
                     (String) map.get("stringProperty"));

    }


    /**
     * Corner cases on getPropertyDescriptor invalid arguments.
     */
    public void testGetDescriptorArguments() {

        try {
            PropertyUtils.getPropertyDescriptor(null, "stringProperty");
            fail("Should throw IllegalArgumentException 1");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.getPropertyDescriptor(bean, null);
            fail("Should throw IllegalArgumentException 2");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

    }


    /**
     * Positive getPropertyDescriptor on property <code>booleanProperty</code>.
     */
    public void testGetDescriptorBoolean() {

        testGetDescriptorBase("booleanProperty", "getBooleanProperty",
                "setBooleanProperty");

    }


    /**
     * Positive getPropertyDescriptor on property <code>doubleProperty</code>.
     */
    public void testGetDescriptorDouble() {

        testGetDescriptorBase("doubleProperty", "getDoubleProperty",
                "setDoubleProperty");

    }


    /**
     * Positive getPropertyDescriptor on property <code>floatProperty</code>.
     */
    public void testGetDescriptorFloat() {

        testGetDescriptorBase("floatProperty", "getFloatProperty",
                "setFloatProperty");

    }


    /**
     * Positive getPropertyDescriptor on property <code>intProperty</code>.
     */
    public void testGetDescriptorInt() {

        testGetDescriptorBase("intProperty", "getIntProperty",
                "setIntProperty");

    }


    /**
     * <p>Negative tests on an invalid property with two different boolean
     * getters (which is fine, according to the JavaBeans spec) but a
     * String setter instead of a boolean setter.</p>
     *
     * <p>Although one could logically argue that this combination of method
     * signatures should not identify a property at all, there is a sentence
     * in Section 8.3.1 making it clear that the behavior tested for here
     * is correct:  "If we find only one of these methods, then we regard
     * it as defining either a read-only or write-only property called
     * <em>&lt;property-name&gt;</em>.</p>
     */
    public void testGetDescriptorInvalidBoolean() throws Exception {

    final PropertyDescriptor pd =
        PropertyUtils.getPropertyDescriptor(bean, "invalidBoolean");
    assertNotNull("invalidBoolean is a property", pd);
    assertNotNull("invalidBoolean has a getter method",
              pd.getReadMethod());
    assertNull("invalidBoolean has no write method",
           pd.getWriteMethod());
    assertTrue("invalidBoolean getter method is isInvalidBoolean or getInvalidBoolean",
           Arrays.asList("isInvalidBoolean", "getInvalidBoolean")
           .contains(pd.getReadMethod().getName()));
    }


    /**
     * Positive getPropertyDescriptor on property <code>longProperty</code>.
     */
    public void testGetDescriptorLong() {

        testGetDescriptorBase("longProperty", "getLongProperty",
                "setLongProperty");

    }

    /**
     * Test getting mapped descriptor with periods in the key.
     */
    public void testGetDescriptorMappedPeriods() {

        bean.getMappedIntProperty("xyz"); // initializes mappedIntProperty

        PropertyDescriptor desc;
        final Integer testIntegerValue = new Integer(1234);

        bean.setMappedIntProperty("key.with.a.dot", testIntegerValue.intValue());
        assertEquals("Can retrieve directly",
                     testIntegerValue,
                     new Integer(bean.getMappedIntProperty("key.with.a.dot")));
        try {
            desc = PropertyUtils.getPropertyDescriptor
                         (bean, "mappedIntProperty(key.with.a.dot)");
            assertEquals("Check descriptor type (A)",
                         Integer.TYPE,
                         ((MappedPropertyDescriptor)desc).getMappedPropertyType());
        } catch (final Exception e) {
            fail("Threw exception (A): " + e);
        }

        bean.setMappedObjects("nested.property", new TestBean(testIntegerValue.intValue()));
        assertEquals("Can retrieve directly",
                      testIntegerValue,
                      new Integer(((TestBean)bean.getMappedObjects("nested.property")).getIntProperty()));
        try {
            desc = PropertyUtils.getPropertyDescriptor
                         (bean, "mappedObjects(nested.property).intProperty");
            assertEquals("Check descriptor type (B)",
                         Integer.TYPE,
                         desc.getPropertyType());
        } catch (final Exception e) {
            fail("Threw exception (B): " + e);
        }
    }


    /**
     * Positive getPropertyDescriptor on property
     * <code>readOnlyProperty</code>.
     */
    public void testGetDescriptorReadOnly() {

        testGetDescriptorBase("readOnlyProperty", "getReadOnlyProperty",
                null);

    }


    /**
     * Positive getPropertyDescriptor on property <code>booleanSecond</code>
     * that uses an "is" method as the getter.
     */
    public void testGetDescriptorSecond() {

        testGetDescriptorBase("booleanSecond", "isBooleanSecond",
                "setBooleanSecond");

    }


    /**
     * Positive getPropertyDescriptor on property <code>shortProperty</code>.
     */
    public void testGetDescriptorShort() {

        testGetDescriptorBase("shortProperty", "getShortProperty",
                "setShortProperty");

    }


    /**
     * Positive getPropertyDescriptor on property <code>stringProperty</code>.
     */
    public void testGetDescriptorString() {

        testGetDescriptorBase("stringProperty", "getStringProperty",
                "setStringProperty");

    }


    /**
     * Negative getPropertyDescriptor on property <code>unknown</code>.
     */
    public void testGetDescriptorUnknown() {

        testGetDescriptorBase("unknown", null, null);

    }


    /**
     * Positive getPropertyDescriptor on property
     * <code>writeOnlyProperty</code>.
     */
    public void testGetDescriptorWriteOnly() {

        testGetDescriptorBase("writeOnlyProperty", null,
                "setWriteOnlyProperty");

    }


    /**
     * Positive test for getPropertyDescriptors().  Each property name
     * listed in <code>properties</code> should be returned exactly once.
     */
    public void testGetDescriptors() {

        final PropertyDescriptor pd[] =
                PropertyUtils.getPropertyDescriptors(bean);
        assertNotNull("Got descriptors", pd);
        final int count[] = new int[properties.length];
        for (PropertyDescriptor element : pd) {
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
     * Corner cases on getPropertyDescriptors invalid arguments.
     */
    public void testGetDescriptorsArguments() {

        try {
            PropertyUtils.getPropertyDescriptors(null);
            fail("Should throw IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException");
        }

    }


    /**
     * Corner cases on getIndexedProperty invalid arguments.
     */
    public void testGetIndexedArguments() {

        // Use explicit index argument

        try {
            PropertyUtils.getIndexedProperty(null, "intArray", 0);
            fail("Should throw IllegalArgumentException 1");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.getIndexedProperty(bean, null, 0);
            fail("Should throw IllegalArgumentException 2");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

        // Use index expression

        try {
            PropertyUtils.getIndexedProperty(null,
                    "intArray[0]");
            fail("Should throw IllegalArgumentException 3");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 3");
        }

        try {
            PropertyUtils.getIndexedProperty(bean, "[0]");
            fail("Should throw NoSuchMethodException 4");
        } catch (final NoSuchMethodException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of NoSuchMethodException 4");
        }

        try {
            PropertyUtils.getIndexedProperty(bean, "intArray");
            fail("Should throw IllegalArgumentException 5");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 5");
        }

        // Use explicit index argument

        try {
            PropertyUtils.getIndexedProperty(null, "intIndexed", 0);
            fail("Should throw IllegalArgumentException 1");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.getIndexedProperty(bean, null, 0);
            fail("Should throw IllegalArgumentException 2");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

        // Use index expression

        try {
            PropertyUtils.getIndexedProperty(null,
                    "intIndexed[0]");
            fail("Should throw IllegalArgumentException 3");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 3");
        }

        try {
            PropertyUtils.getIndexedProperty(bean, "[0]");
            fail("Should throw NoSuchMethodException 4");
        } catch (final NoSuchMethodException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of NoSuchMethodException 4");
        }

        try {
            PropertyUtils.getIndexedProperty(bean, "intIndexed");
            fail("Should throw IllegalArgumentException 5");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 5");
        }

    }


    /**
     * Positive and negative tests on getIndexedProperty valid arguments.
     */
    public void testGetIndexedValues() {

        Object value = null;

        // Use explicit key argument

        for (int i = 0; i < 5; i++) {

            try {
                value = PropertyUtils.getIndexedProperty
                    (bean, "dupProperty", i);
                assertNotNull("dupProperty returned value " + i, value);
                assertTrue("dupProperty returned String " + i,
                        value instanceof String);
                assertEquals("dupProperty returned correct " + i,
                             "Dup " + i,
                             (String) value);
            } catch (final Throwable t) {
                fail("dupProperty " + i + " threw " + t);
            }

            try {
                value =
                        PropertyUtils.getIndexedProperty(bean, "intArray", i);
                assertNotNull("intArray returned value " + i, value);
                assertTrue("intArray returned Integer " + i,
                        value instanceof Integer);
                assertEquals("intArray returned correct " + i, i * 10,
                        ((Integer) value).intValue());
            } catch (final Throwable t) {
                fail("intArray " + i + " threw " + t);
            }

            try {
                value =
                        PropertyUtils.getIndexedProperty(bean, "intIndexed", i);
                assertNotNull("intIndexed returned value " + i, value);
                assertTrue("intIndexed returned Integer " + i,
                        value instanceof Integer);
                assertEquals("intIndexed returned correct " + i, i * 10,
                        ((Integer) value).intValue());
            } catch (final Throwable t) {
                fail("intIndexed " + i + " threw " + t);
            }

            try {
                value =
                        PropertyUtils.getIndexedProperty(bean, "listIndexed", i);
                assertNotNull("listIndexed returned value " + i, value);
                assertTrue("list returned String " + i,
                        value instanceof String);
                assertEquals("listIndexed returned correct " + i,
                        "String " + i, (String) value);
            } catch (final Throwable t) {
                fail("listIndexed " + i + " threw " + t);
            }

            try {
                value =
                        PropertyUtils.getIndexedProperty(bean, "stringArray", i);
                assertNotNull("stringArray returned value " + i, value);
                assertTrue("stringArray returned String " + i,
                        value instanceof String);
                assertEquals("stringArray returned correct " + i,
                        "String " + i, (String) value);
            } catch (final Throwable t) {
                fail("stringArray " + i + " threw " + t);
            }

            try {
                value =
                        PropertyUtils.getIndexedProperty(bean, "stringIndexed", i);
                assertNotNull("stringIndexed returned value " + i, value);
                assertTrue("stringIndexed returned String " + i,
                        value instanceof String);
                assertEquals("stringIndexed returned correct " + i,
                        "String " + i, (String) value);
            } catch (final Throwable t) {
                fail("stringIndexed " + i + " threw " + t);
            }

        }

        // Use key expression

        for (int i = 0; i < 5; i++) {

            try {
                value = PropertyUtils.getIndexedProperty
                    (bean, "dupProperty[" + i + "]");
                assertNotNull("dupProperty returned value " + i, value);
                assertTrue("dupProperty returned String " + i,
                        value instanceof String);
                assertEquals("dupProperty returned correct " + i,
                             "Dup " + i,
                             (String) value);
            } catch (final Throwable t) {
                fail("dupProperty " + i + " threw " + t);
            }

            try {
                value =
                        PropertyUtils.getIndexedProperty(bean,
                                "intArray[" + i + "]");
                assertNotNull("intArray returned value " + i, value);
                assertTrue("intArray returned Integer " + i,
                        value instanceof Integer);
                assertEquals("intArray returned correct " + i, i * 10,
                        ((Integer) value).intValue());
            } catch (final Throwable t) {
                fail("intArray " + i + " threw " + t);
            }

            try {
                value =
                        PropertyUtils.getIndexedProperty(bean,
                                "intIndexed[" + i + "]");
                assertNotNull("intIndexed returned value " + i, value);
                assertTrue("intIndexed returned Integer " + i,
                        value instanceof Integer);
                assertEquals("intIndexed returned correct " + i, i * 10,
                        ((Integer) value).intValue());
            } catch (final Throwable t) {
                fail("intIndexed " + i + " threw " + t);
            }

            try {
                value =
                        PropertyUtils.getIndexedProperty(bean,
                                "listIndexed[" + i + "]");
                assertNotNull("listIndexed returned value " + i, value);
                assertTrue("listIndexed returned String " + i,
                        value instanceof String);
                assertEquals("listIndexed returned correct " + i,
                        "String " + i, (String) value);
            } catch (final Throwable t) {
                fail("listIndexed " + i + " threw " + t);
            }

            try {
                value =
                        PropertyUtils.getIndexedProperty(bean,
                                "stringArray[" + i + "]");
                assertNotNull("stringArray returned value " + i, value);
                assertTrue("stringArray returned String " + i,
                        value instanceof String);
                assertEquals("stringArray returned correct " + i,
                        "String " + i, (String) value);
            } catch (final Throwable t) {
                fail("stringArray " + i + " threw " + t);
            }

            try {
                value =
                        PropertyUtils.getIndexedProperty(bean,
                                "stringIndexed[" + i + "]");
                assertNotNull("stringIndexed returned value " + i, value);
                assertTrue("stringIndexed returned String " + i,
                        value instanceof String);
                assertEquals("stringIndexed returned correct " + i,
                        "String " + i, (String) value);
            } catch (final Throwable t) {
                fail("stringIndexed " + i + " threw " + t);
            }

        }

        // Index out of bounds tests

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "dupProperty", -1);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "dupProperty", 5);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "intArray", -1);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "intArray", 5);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "intIndexed", -1);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "intIndexed", 5);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "listIndexed", -1);
            fail("Should have thrown IndexOutOfBoundsException");
        } catch (final IndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "listIndexed", 5);
            fail("Should have thrown IndexOutOfBoundsException");
        } catch (final IndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "stringArray", -1);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "stringArray", 5);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "stringIndexed", -1);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "stringIndexed", 5);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

    }


    /**
     * Test getting an indexed value out of a multi-dimensional array
     */
    public void testGetIndexedArray() {
        final String[] firstArray = new String[] {"FIRST-1", "FIRST-2", "FIRST-3"};
        final String[] secondArray = new String[] {"SECOND-1", "SECOND-2", "SECOND-3",  "SECOND-4"};
        final String[][] mainArray = {firstArray, secondArray};
        final TestBean bean = new TestBean(mainArray);
        try {
            assertEquals("firstArray[0]", firstArray[0],  PropertyUtils.getProperty(bean, "string2dArray[0][0]"));
            assertEquals("firstArray[1]", firstArray[1],  PropertyUtils.getProperty(bean, "string2dArray[0][1]"));
            assertEquals("firstArray[2]", firstArray[2],  PropertyUtils.getProperty(bean, "string2dArray[0][2]"));
            assertEquals("secondArray[0]", secondArray[0], PropertyUtils.getProperty(bean, "string2dArray[1][0]"));
            assertEquals("secondArray[1]", secondArray[1], PropertyUtils.getProperty(bean, "string2dArray[1][1]"));
            assertEquals("secondArray[2]", secondArray[2], PropertyUtils.getProperty(bean, "string2dArray[1][2]"));
            assertEquals("secondArray[3]", secondArray[3], PropertyUtils.getProperty(bean, "string2dArray[1][3]"));
        } catch (final Throwable t) {
            fail("Threw " + t + "");
        }
    }

    /**
     * Test getting an indexed value out of List of Lists
     */
    public void testGetIndexedList() {
        final String[] firstArray = new String[] {"FIRST-1", "FIRST-2", "FIRST-3"};
        final String[] secondArray = new String[] {"SECOND-1", "SECOND-2", "SECOND-3",  "SECOND-4"};
        final List<Object> mainList = new ArrayList<Object>();
        mainList.add(Arrays.asList(firstArray));
        mainList.add(Arrays.asList(secondArray));
        final TestBean bean = new TestBean(mainList);
        try {
            assertEquals("firstArray[0]", firstArray[0],  PropertyUtils.getProperty(bean, "listIndexed[0][0]"));
            assertEquals("firstArray[1]", firstArray[1],  PropertyUtils.getProperty(bean, "listIndexed[0][1]"));
            assertEquals("firstArray[2]", firstArray[2],  PropertyUtils.getProperty(bean, "listIndexed[0][2]"));
            assertEquals("secondArray[0]", secondArray[0], PropertyUtils.getProperty(bean, "listIndexed[1][0]"));
            assertEquals("secondArray[1]", secondArray[1], PropertyUtils.getProperty(bean, "listIndexed[1][1]"));
            assertEquals("secondArray[2]", secondArray[2], PropertyUtils.getProperty(bean, "listIndexed[1][2]"));
            assertEquals("secondArray[3]", secondArray[3], PropertyUtils.getProperty(bean, "listIndexed[1][3]"));
        } catch (final Throwable t) {
            fail("Threw " + t + "");
        }
    }

    /**
     * Test getting a value out of a mapped Map
     */
    public void testGetIndexedMap() {
        final Map<String, Object> firstMap  = new HashMap<String, Object>();
        firstMap.put("FIRST-KEY-1", "FIRST-VALUE-1");
        firstMap.put("FIRST-KEY-2", "FIRST-VALUE-2");
        final Map<String, Object> secondMap  = new HashMap<String, Object>();
        secondMap.put("SECOND-KEY-1", "SECOND-VALUE-1");
        secondMap.put("SECOND-KEY-2", "SECOND-VALUE-2");

        final List<Object> mainList   = new ArrayList<Object>();
        mainList.add(firstMap);
        mainList.add(secondMap);
        final TestBean bean = new TestBean(mainList);
        try {
            assertEquals("listIndexed[0](FIRST-KEY-1)",  "FIRST-VALUE-1",   PropertyUtils.getProperty(bean, "listIndexed[0](FIRST-KEY-1)"));
            assertEquals("listIndexed[0](FIRST-KEY-2)",  "FIRST-VALUE-2",   PropertyUtils.getProperty(bean, "listIndexed[0](FIRST-KEY-2)"));
            assertEquals("listIndexed[1](SECOND-KEY-1)", "SECOND-VALUE-1",  PropertyUtils.getProperty(bean, "listIndexed[1](SECOND-KEY-1)"));
            assertEquals("listIndexed[1](SECOND-KEY-2)", "SECOND-VALUE-2",  PropertyUtils.getProperty(bean, "listIndexed[1](SECOND-KEY-2)"));
        } catch (final Throwable t) {
            fail("Threw " + t + "");
        }
    }

    /**
     * Corner cases on getMappedProperty invalid arguments.
     */
    public void testGetMappedArguments() {

        // Use explicit key argument

        try {
            PropertyUtils.getMappedProperty(null, "mappedProperty",
                    "First Key");
            fail("Should throw IllegalArgumentException 1");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.getMappedProperty(bean, null, "First Key");
            fail("Should throw IllegalArgumentException 2");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

        try {
            PropertyUtils.getMappedProperty(bean, "mappedProperty", null);
            fail("Should throw IllegalArgumentException 3");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 3");
        }

        // Use key expression

        try {
            PropertyUtils.getMappedProperty(null,
                    "mappedProperty(First Key)");
            fail("Should throw IllegalArgumentException 4");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 4");
        }

        try {
            PropertyUtils.getMappedProperty(bean, "(Second Key)");
            fail("Should throw IllegalArgumentException 5");
        } catch (final NoSuchMethodException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of NoSuchMethodException 5");
        }

        try {
            PropertyUtils.getMappedProperty(bean, "mappedProperty");
            fail("Should throw IllegalArgumentException 6");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 6");
        }

    }

    /**
     * Test getting an indexed value out of a mapped array
     */
    public void testGetMappedArray() {
        final TestBean bean = new TestBean();
        final String[] array = new String[] {"abc", "def", "ghi"};
        bean.getMapProperty().put("mappedArray", array);
        try {
            assertEquals("abc", PropertyUtils.getProperty(bean, "mapProperty(mappedArray)[0]"));
            assertEquals("def", PropertyUtils.getProperty(bean, "mapProperty(mappedArray)[1]"));
            assertEquals("ghi", PropertyUtils.getProperty(bean, "mapProperty(mappedArray)[2]"));
        } catch (final Throwable t) {
            fail("Threw " + t + "");
        }
    }

    /**
     * Test getting an indexed value out of a mapped List
     */
    public void testGetMappedList() {
        final TestBean bean = new TestBean();
        final List<Object> list = new ArrayList<Object>();
        list.add("klm");
        list.add("nop");
        list.add("qrs");
        bean.getMapProperty().put("mappedList", list);
        try {
            assertEquals("klm", PropertyUtils.getProperty(bean, "mapProperty(mappedList)[0]"));
            assertEquals("nop", PropertyUtils.getProperty(bean, "mapProperty(mappedList)[1]"));
            assertEquals("qrs", PropertyUtils.getProperty(bean, "mapProperty(mappedList)[2]"));
        } catch (final Throwable t) {
            fail("Threw " + t + "");
        }
    }

    /**
     * Test getting a value out of a mapped Map
     */
    public void testGetMappedMap() {
        final TestBean bean = new TestBean();
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("sub-key-1", "sub-value-1");
        map.put("sub-key-2", "sub-value-2");
        map.put("sub-key-3", "sub-value-3");
        bean.getMapProperty().put("mappedMap", map);
        try {
            assertEquals("sub-value-1", PropertyUtils.getProperty(bean, "mapProperty(mappedMap)(sub-key-1)"));
            assertEquals("sub-value-2", PropertyUtils.getProperty(bean, "mapProperty(mappedMap)(sub-key-2)"));
            assertEquals("sub-value-3", PropertyUtils.getProperty(bean, "mapProperty(mappedMap)(sub-key-3)"));
        } catch (final Throwable t) {
            fail("Threw " + t + "");
        }
    }

    /**
     * Test getting mapped values with periods in the key.
     */
    public void testGetMappedPeriods() {

        bean.setMappedProperty("key.with.a.dot", "Special Value");
        assertEquals("Can retrieve directly",
                     "Special Value",
                     bean.getMappedProperty("key.with.a.dot"));
        try {
            assertEquals("Can retrieve via getMappedProperty",
                         "Special Value",
                         PropertyUtils.getMappedProperty
                         (bean, "mappedProperty", "key.with.a.dot"));
        } catch (final Exception e) {
            fail("Thew exception: " + e);
        }
        try {
            assertEquals("Can retrieve via getNestedProperty",
                         "Special Value",
                         PropertyUtils.getNestedProperty
                         (bean, "mappedProperty(key.with.a.dot)"));
        } catch (final Exception e) {
            fail("Thew exception: " + e);
        }

        bean.setMappedObjects("nested.property", new TestBean());
        assertNotNull("Can retrieve directly",
                      bean.getMappedObjects("nested.property"));
        try {
            assertEquals("Can retrieve nested",
                         "This is a string",
                         PropertyUtils.getNestedProperty
                         (bean,
                          "mappedObjects(nested.property).stringProperty"));
        } catch (final Exception e) {
            fail("Thew exception: " + e);
        }

        try
        {
            assertEquals("Can't retrieved nested with mapped property",
                         "Mapped Value",
                         PropertyUtils.getNestedProperty(
                             bean,"mappedNested.value(Mapped Key)"));
        } catch (final Exception e)
        {
            fail("Thew exception: " + e);
        }
    }


    /**
     * Test getting mapped values with slashes in the key.  This is different
     * from periods because slashes are not syntactically significant.
     */
    public void testGetMappedSlashes() {

        bean.setMappedProperty("key/with/a/slash", "Special Value");
        assertEquals("Can retrieve directly",
                     "Special Value",
                     bean.getMappedProperty("key/with/a/slash"));
        try {
            assertEquals("Can retrieve via getMappedProperty",
                         "Special Value",
                         PropertyUtils.getMappedProperty
                         (bean, "mappedProperty", "key/with/a/slash"));
        } catch (final Exception e) {
            fail("Thew exception: " + e);
        }
        try {
            assertEquals("Can retrieve via getNestedProperty",
                         "Special Value",
                         PropertyUtils.getNestedProperty
                         (bean, "mappedProperty(key/with/a/slash)"));
        } catch (final Exception e) {
            fail("Thew exception: " + e);
        }

        bean.setMappedObjects("nested/property", new TestBean());
        assertNotNull("Can retrieve directly",
                      bean.getMappedObjects("nested/property"));
        try {
            assertEquals("Can retrieve nested",
                         "This is a string",
                         PropertyUtils.getNestedProperty
                         (bean,
                          "mappedObjects(nested/property).stringProperty"));
        } catch (final Exception e) {
            fail("Thew exception: " + e);
        }

    }


    /**
     * Positive and negative tests on getMappedProperty valid arguments.
     */
    public void testGetMappedValues() {

        Object value = null;

        // Use explicit key argument

        try {
            value = PropertyUtils.getMappedProperty(bean, "mappedProperty",
                    "First Key");
            assertEquals("Can find first value", "First Value", value);
        } catch (final Throwable t) {
            fail("Finding first value threw " + t);
        }

        try {
            value = PropertyUtils.getMappedProperty(bean, "mappedProperty",
                    "Second Key");
            assertEquals("Can find second value", "Second Value", value);
        } catch (final Throwable t) {
            fail("Finding second value threw " + t);
        }

        try {
            value = PropertyUtils.getMappedProperty(bean, "mappedProperty",
                    "Third Key");
            assertNull("Can not find third value", value);
        } catch (final Throwable t) {
            fail("Finding third value threw " + t);
        }

        // Use key expression with parentheses

        try {
            value =
                    PropertyUtils.getMappedProperty(bean,
                            "mappedProperty(First Key)");
            assertEquals("Can find first value", "First Value", value);
        } catch (final Throwable t) {
            fail("Finding first value threw " + t);
        }

        try {
            value =
                    PropertyUtils.getMappedProperty(bean,
                            "mappedProperty(Second Key)");
            assertEquals("Can find second value", "Second Value", value);
        } catch (final Throwable t) {
            fail("Finding second value threw " + t);
        }

        try {
            value =
                    PropertyUtils.getMappedProperty(bean,
                            "mappedProperty(Third Key)");
            assertNull("Can not find third value", value);
        } catch (final Throwable t) {
            fail("Finding third value threw " + t);
        }

        // Use key expression with dotted syntax

        try {
            value =
                    PropertyUtils.getNestedProperty(bean,
                            "mapProperty.First Key");
            assertEquals("Can find first value", "First Value", value);
        } catch (final Throwable t) {
            fail("Finding first value threw " + t);
        }

        try {
            value =
                    PropertyUtils.getNestedProperty(bean,
                            "mapProperty.Second Key");
            assertEquals("Can find second value", "Second Value", value);
        } catch (final Throwable t) {
            fail("Finding second value threw " + t);
        }

        try {
            value =
                    PropertyUtils.getNestedProperty(bean,
                            "mapProperty.Third Key");
            assertNull("Can not find third value", value);
        } catch (final Throwable t) {
            fail("Finding third value threw " + t);
        }

    }


    /**
     * Corner cases on getNestedProperty invalid arguments.
     */
    public void testGetNestedArguments() {

        try {
            PropertyUtils.getNestedProperty(null, "stringProperty");
            fail("Should throw IllegalArgumentException 1");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.getNestedProperty(bean, null);
            fail("Should throw IllegalArgumentException 2");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

    }


    /**
     * Test getNestedProperty on a boolean property.
     */
    public void testGetNestedBoolean() {

        try {
            final Object value =
                    PropertyUtils.getNestedProperty
                    (bean, "nested.booleanProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Boolean));
            assertTrue("Got correct value",
                    ((Boolean) value).booleanValue() ==
                    bean.getNested().getBooleanProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getNestedProperty on a double property.
     */
    public void testGetNestedDouble() {

        try {
            final Object value =
                    PropertyUtils.getNestedProperty
                    (bean, "nested.doubleProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Double));
            assertEquals("Got correct value",
                    ((Double) value).doubleValue(),
                    bean.getNested().getDoubleProperty(),
                    0.005);
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getNestedProperty on a float property.
     */
    public void testGetNestedFloat() {

        try {
            final Object value =
                    PropertyUtils.getNestedProperty
                    (bean, "nested.floatProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Float));
            assertEquals("Got correct value",
                    ((Float) value).floatValue(),
                    bean.getNested().getFloatProperty(),
                    (float) 0.005);
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getNestedProperty on an int property.
     */
    public void testGetNestedInt() {

        try {
            final Object value =
                    PropertyUtils.getNestedProperty
                    (bean, "nested.intProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Integer));
            assertEquals("Got correct value",
                    ((Integer) value).intValue(),
                    bean.getNested().getIntProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getNestedProperty on a long property.
     */
    public void testGetNestedLong() {

        try {
            final Object value =
                    PropertyUtils.getNestedProperty
                    (bean, "nested.longProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Long));
            assertEquals("Got correct value",
                    ((Long) value).longValue(),
                    bean.getNested().getLongProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getNestedProperty on a read-only String property.
     */
    public void testGetNestedReadOnly() {

        try {
            final Object value =
                    PropertyUtils.getNestedProperty
                    (bean, "nested.readOnlyProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof String));
            assertEquals("Got correct value",
                    (String) value,
                    bean.getReadOnlyProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getNestedProperty on a short property.
     */
    public void testGetNestedShort() {

        try {
            final Object value =
                    PropertyUtils.getNestedProperty
                    (bean, "nested.shortProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Short));
            assertEquals("Got correct value",
                    ((Short) value).shortValue(),
                    bean.getNested().getShortProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getNestedProperty on a String property.
     */
    public void testGetNestedString() {

        try {
            final Object value =
                    PropertyUtils.getNestedProperty
                    (bean, "nested.stringProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof String));
            assertEquals("Got correct value",
                    ((String) value),
                    bean.getNested().getStringProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Negative test getNestedProperty on an unknown property.
     */
    public void testGetNestedUnknown() {

        try {
            PropertyUtils.getNestedProperty(bean, "nested.unknown");
            fail("Should have thrown NoSuchMethodException");
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            // Correct result for this test
        }

    }

    /**
     * When a bean has a null property which is reference by the standard access language,
     * this should throw a NestedNullException.
     */
    public void testThrowNestedNull() throws Exception {
        final NestedTestBean nestedBean = new NestedTestBean("base");
        // don't init!

        try {
            PropertyUtils.getProperty(
                                nestedBean,
                                "simpleBeanProperty.indexedProperty[0]");
            fail("NestedNullException not thrown");
        } catch (final NestedNullException e) {
            // that's what we wanted!
        }
    }

    /**
     * Test getNestedProperty on a write-only String property.
     */
    public void testGetNestedWriteOnly() {

        try {
            PropertyUtils.getNestedProperty(bean, "writeOnlyProperty");
            fail("Should have thrown NoSuchMethodException");
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            // Correct result for this test
        }

    }


    /**
     * Test getPropertyType() on all kinds of properties.
     */
    public void testGetPropertyType() {

        Class<?> clazz = null;
        final int intArray[] = new int[0];
        final String stringArray[] = new String[0];

        try {

            // Scalar and Indexed Properties
            clazz = PropertyUtils.getPropertyType(bean, "booleanProperty");
            assertEquals("booleanProperty type", Boolean.TYPE, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "booleanSecond");
            assertEquals("booleanSecond type", Boolean.TYPE, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "doubleProperty");
            assertEquals("doubleProperty type", Double.TYPE, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "dupProperty");
            assertEquals("dupProperty type", String.class, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "floatProperty");
            assertEquals("floatProperty type", Float.TYPE, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "intArray");
            assertEquals("intArray type", intArray.getClass(), clazz);
            clazz = PropertyUtils.getPropertyType(bean, "intIndexed");
            assertEquals("intIndexed type", Integer.TYPE, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "intProperty");
            assertEquals("intProperty type", Integer.TYPE, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "listIndexed");
            assertEquals("listIndexed type", List.class, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "longProperty");
            assertEquals("longProperty type", Long.TYPE, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "mappedProperty");
            assertEquals("mappedProperty type", String.class, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "mappedIntProperty");
            assertEquals("mappedIntProperty type", Integer.TYPE, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "readOnlyProperty");
            assertEquals("readOnlyProperty type", String.class, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "shortProperty");
            assertEquals("shortProperty type", Short.TYPE, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "stringArray");
            assertEquals("stringArray type", stringArray.getClass(), clazz);
            clazz = PropertyUtils.getPropertyType(bean, "stringIndexed");
            assertEquals("stringIndexed type", String.class, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "stringProperty");
            assertEquals("stringProperty type", String.class, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "writeOnlyProperty");
            assertEquals("writeOnlyProperty type", String.class, clazz);

            // Nested Properties
            clazz = PropertyUtils.getPropertyType(bean, "nested.booleanProperty");
            assertEquals("booleanProperty type", Boolean.TYPE, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "nested.booleanSecond");
            assertEquals("booleanSecond type", Boolean.TYPE, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "nested.doubleProperty");
            assertEquals("doubleProperty type", Double.TYPE, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "nested.dupProperty");
            assertEquals("dupProperty type", String.class, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "nested.floatProperty");
            assertEquals("floatProperty type", Float.TYPE, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "nested.intArray");
            assertEquals("intArray type", intArray.getClass(), clazz);
            clazz = PropertyUtils.getPropertyType(bean, "nested.intIndexed");
            assertEquals("intIndexed type", Integer.TYPE, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "nested.intProperty");
            assertEquals("intProperty type", Integer.TYPE, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "nested.listIndexed");
            assertEquals("listIndexed type", List.class, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "nested.longProperty");
            assertEquals("longProperty type", Long.TYPE, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "nested.mappedProperty");
            assertEquals("mappedProperty type", String.class, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "nested.mappedIntProperty");
            assertEquals("mappedIntProperty type", Integer.TYPE, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "nested.readOnlyProperty");
            assertEquals("readOnlyProperty type", String.class, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "nested.shortProperty");
            assertEquals("shortProperty type", Short.TYPE, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "nested.stringArray");
            assertEquals("stringArray type", stringArray.getClass(), clazz);
            clazz = PropertyUtils.getPropertyType(bean, "nested.stringIndexed");
            assertEquals("stringIndexed type", String.class, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "nested.stringProperty");
            assertEquals("stringProperty type", String.class, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "nested.writeOnlyProperty");
            assertEquals("writeOnlyProperty type", String.class, clazz);

            // Nested DynaBean
            clazz = PropertyUtils.getPropertyType(bean, "nestedDynaBean");
            assertEquals("nestedDynaBean type", DynaBean.class, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "nestedDynaBean.stringProperty");
            assertEquals("nestedDynaBean.stringProperty type", String.class, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "nestedDynaBean.nestedBean");
            assertEquals("nestedDynaBean.nestedBean type", TestBean.class, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "nestedDynaBean.nestedBean.nestedDynaBean");
            assertEquals("nestedDynaBean.nestedBean.nestedDynaBean type", DynaBean.class, clazz);
            clazz = PropertyUtils.getPropertyType(bean, "nestedDynaBean.nestedBean.nestedDynaBean.stringProperty");
            assertEquals("nestedDynaBean.nestedBean.nestedDynaBean.stringPropert type", String.class, clazz);

            // test Null
            clazz = PropertyUtils.getPropertyType(bean, "nestedDynaBean.nullDynaBean");
            assertEquals("nestedDynaBean.nullDynaBean type", DynaBean.class, clazz);
            try {
                clazz = PropertyUtils.getPropertyType(bean, "nestedDynaBean.nullDynaBean.foo");
                fail("Expected NestedNullException for nestedDynaBean.nullDynaBean.foo");
            } catch (final NestedNullException e) {
                // expected
            }

        } catch (final Exception e) {
            fail("Exception: " + e.getMessage());
        }

    }


    /**
     * Test accessing a public sub-bean of a package scope bean
     */
    public void testGetPublicSubBean_of_PackageBean() {

        final PublicSubBean bean = new PublicSubBean();
        bean.setFoo("foo-start");
        bean.setBar("bar-start");
        Object result = null;

        // Get Foo
        try {
            result = PropertyUtils.getProperty(bean, "foo");
        } catch (final Throwable t) {
            fail("getProperty(foo) threw " + t);
        }
        assertEquals("foo property", "foo-start", result);

        // Get Bar
        try {
            result = PropertyUtils.getProperty(bean, "bar");
        } catch (final Throwable t) {
            fail("getProperty(bar) threw " + t);
        }
        assertEquals("bar property", "bar-start", result);
    }


    /**
     * Test getting accessible property reader methods for a specified
     * list of properties of our standard test bean.
     */
    public void testGetReadMethodBasic() {

        testGetReadMethod(bean, properties, TEST_BEAN_CLASS);

    }


    /**
     * Test getting accessible property reader methods for a specified
     * list of properties of a package private subclass of our standard
     * test bean.
     */
    public void testGetReadMethodPackageSubclass() {

        testGetReadMethod(beanPackageSubclass, properties, TEST_BEAN_CLASS);

    }


    /**
     * Test getting accessible property reader methods for a specified
     * list of properties that are declared either directly or via
     * implemented interfaces.
     */
    public void testGetReadMethodPublicInterface() {

        // Properties "bar" and "baz" are visible via implemented interfaces
        // (one direct and one indirect)
        testGetReadMethod(beanPrivate,
                new String[]{ "bar" },
                PRIVATE_DIRECT_CLASS);
        testGetReadMethod(beanPrivate,
                new String[]{ "baz" },
                PRIVATE_INDIRECT_CLASS);

        // Properties "bar" and "baz" are visible via implemented interfaces
        // (one direct and one indirect).  The interface is implemented in
        // a superclass
        testGetReadMethod(beanPrivateSubclass,
                new String[]{ "bar" },
                PRIVATE_DIRECT_CLASS);
        testGetReadMethod(beanPrivateSubclass,
                new String[]{ "baz" },
                PRIVATE_INDIRECT_CLASS);

        // Property "foo" is not accessible because the underlying
        // class has package scope
        final PropertyDescriptor pd[] =
                PropertyUtils.getPropertyDescriptors(beanPrivate);
        int n = -1;
        for (int i = 0; i < pd.length; i++) {
            if ("foo".equals(pd[i].getName())) {
                n = i;
                break;
            }
        }
        assertTrue("Found foo descriptor", n >= 0);
        final Method reader = pd[n].getReadMethod();
        assertNotNull("Found foo read method", reader);
        try {
            reader.invoke(beanPrivate, (Object[]) new Class<?>[0]);
            fail("Foo reader did throw IllegalAccessException");
        } catch (final IllegalAccessException e) {
            // Expected result for this test
        } catch (final Throwable t) {
            fail("Invoke foo reader: " + t);
        }

    }


    /**
     * Test getting accessible property reader methods for a specified
     * list of properties of a public subclass of our standard test bean.
     */
    public void testGetReadMethodPublicSubclass() {

        testGetReadMethod(beanPublicSubclass, properties, TEST_BEAN_CLASS);

    }


    /**
     * Corner cases on getSimpleProperty invalid arguments.
     */
    public void testGetSimpleArguments() {

        try {
            PropertyUtils.getSimpleProperty(null, "stringProperty");
            fail("Should throw IllegalArgumentException 1");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.getSimpleProperty(bean, null);
            fail("Should throw IllegalArgumentException 2");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

    }


    /**
     * Test getSimpleProperty on a boolean property.
     */
    public void testGetSimpleBoolean() {

        try {
            final Object value =
                    PropertyUtils.getSimpleProperty(bean,
                            "booleanProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Boolean));
            assertTrue("Got correct value",
                    ((Boolean) value).booleanValue() ==
                    bean.getBooleanProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getSimpleProperty on a double property.
     */
    public void testGetSimpleDouble() {

        try {
            final Object value =
                    PropertyUtils.getSimpleProperty(bean,
                            "doubleProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Double));
            assertEquals("Got correct value",
                    ((Double) value).doubleValue(),
                    bean.getDoubleProperty(), 0.005);
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getSimpleProperty on a float property.
     */
    public void testGetSimpleFloat() {

        try {
            final Object value =
                    PropertyUtils.getSimpleProperty(bean,
                            "floatProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Float));
            assertEquals("Got correct value",
                    ((Float) value).floatValue(),
                    bean.getFloatProperty(),
                    (float) 0.005);
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Negative test getSimpleProperty on an indexed property.
     */
    public void testGetSimpleIndexed() {

        try {
            PropertyUtils.getSimpleProperty(bean,
                    "intIndexed[0]");
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            // Correct result for this test
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getSimpleProperty on an int property.
     */
    public void testGetSimpleInt() {

        try {
            final Object value =
                    PropertyUtils.getSimpleProperty(bean,
                            "intProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Integer));
            assertEquals("Got correct value",
                    ((Integer) value).intValue(),
                    bean.getIntProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getSimpleProperty on a long property.
     */
    public void testGetSimpleLong() {

        try {
            final Object value =
                    PropertyUtils.getSimpleProperty(bean,
                            "longProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Long));
            assertEquals("Got correct value",
                    ((Long) value).longValue(),
                    bean.getLongProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Negative test getSimpleProperty on a nested property.
     */
    public void testGetSimpleNested() {

        try {
            PropertyUtils.getSimpleProperty(bean,
                    "nested.stringProperty");
            fail("Should have thrown IllegaArgumentException");
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            // Correct result for this test
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getSimpleProperty on a read-only String property.
     */
    public void testGetSimpleReadOnly() {

        try {
            final Object value =
                    PropertyUtils.getSimpleProperty(bean,
                            "readOnlyProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof String));
            assertEquals("Got correct value",
                    (String) value,
                    bean.getReadOnlyProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getSimpleProperty on a short property.
     */
    public void testGetSimpleShort() {

        try {
            final Object value =
                    PropertyUtils.getSimpleProperty(bean,
                            "shortProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Short));
            assertEquals("Got correct value",
                    ((Short) value).shortValue(),
                    bean.getShortProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getSimpleProperty on a String property.
     */
    public void testGetSimpleString() {

        try {
            final Object value =
                    PropertyUtils.getSimpleProperty(bean,
                            "stringProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof String));
            assertEquals("Got correct value",
                    (String) value,
                    bean.getStringProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Negative test getSimpleProperty on an unknown property.
     */
    public void testGetSimpleUnknown() {

        try {
            PropertyUtils.getSimpleProperty(bean, "unknown");
            fail("Should have thrown NoSuchMethodException");
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            // Correct result for this test
            assertEquals("Unknown property 'unknown' on class '" +
                         bean.getClass() + "'", e.getMessage() );
        }

    }


    /**
     * Test getSimpleProperty on a write-only String property.
     */
    public void testGetSimpleWriteOnly() {

        try {
            PropertyUtils.getSimpleProperty(bean, "writeOnlyProperty");
            fail("Should have thrown NoSuchMethodException");
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            // Correct result for this test
            assertEquals("Property 'writeOnlyProperty' has no getter method in class '" +
                         bean.getClass() + "'", e.getMessage() );
        }

    }


    /**
     * Test getting accessible property writer methods for a specified
     * list of properties of our standard test bean.
     */
    public void testGetWriteMethodBasic() {

        testGetWriteMethod(bean, properties, TEST_BEAN_CLASS);

    }


    /**
     * Test getting accessible property writer methods for a specified
     * list of properties of a package private subclass of our standard
     * test bean.
     */
    public void testGetWriteMethodPackageSubclass() {

        testGetWriteMethod(beanPackageSubclass, properties, TEST_BEAN_CLASS);

    }


    /**
     * Test getting accessible property writer methods for a specified
     * list of properties of a public subclass of our standard test bean.
     */
    public void testGetWriteMethodPublicSubclass() {

        testGetWriteMethod(beanPublicSubclass, properties, TEST_BEAN_CLASS);

    }

    /**
     * Test isReadable() method.
     */
    public void testIsReadable() {
        String property = null;
        try {
            property = "stringProperty";
            assertTrue("Property " + property +" isReadable expeced TRUE", PropertyUtils.isReadable(bean, property));
        } catch (final Throwable t) {
            fail("Property " + property +" isReadable Threw exception: " + t);
        }
        try {
            property = "stringIndexed";
            assertTrue("Property " + property +" isReadable expeced TRUE", PropertyUtils.isReadable(bean, property));
        } catch (final Throwable t) {
            fail("Property " + property +" isReadable Threw exception: " + t);
        }
        try {
            property = "mappedProperty";
            assertTrue("Property " + property +" isReadable expeced TRUE", PropertyUtils.isReadable(bean, property));
        } catch (final Throwable t) {
            fail("Property " + property +" isReadable Threw exception: " + t);
        }

        try {
            property = "nestedDynaBean";
            assertTrue("Property " + property +" isReadable expeced TRUE", PropertyUtils.isReadable(bean, property));
        } catch (final Throwable t) {
            fail("Property " + property +" isReadable Threw exception: " + t);
        }

        try {
            property = "nestedDynaBean.stringProperty";
            assertTrue("Property " + property +" isReadable expeced TRUE", PropertyUtils.isReadable(bean, property));
        } catch (final Throwable t) {
            fail("Property " + property +" isReadable Threw exception: " + t);
        }

        try {
            property = "nestedDynaBean.nestedBean";
            assertTrue("Property " + property +" isReadable expeced TRUE", PropertyUtils.isReadable(bean, property));
        } catch (final Throwable t) {
            fail("Property " + property +" isReadable Threw exception: " + t);
        }

        try {
            property = "nestedDynaBean.nestedBean.nestedDynaBean";
            assertTrue("Property " + property +" isReadable expeced TRUE", PropertyUtils.isReadable(bean, property));
        } catch (final Throwable t) {
            fail("Property " + property +" isReadable Threw exception: " + t);
        }

        try {
            property = "nestedDynaBean.nestedBean.nestedDynaBean.stringProperty";
            assertTrue("Property " + property +" isReadable expeced TRUE", PropertyUtils.isReadable(bean, property));
        } catch (final Throwable t) {
            fail("Property " + property +" isReadable Threw exception: " + t);
        }

        try {
            property = "nestedDynaBean.nullDynaBean";
            assertTrue("Property " + property +" isReadable expeced TRUE", PropertyUtils.isReadable(bean, property));
        } catch (final Throwable t) {
            fail("Property " + property +" isReadable Threw exception: " + t);
        }

        try {
            property = "nestedDynaBean.nullDynaBean.foo";
            assertTrue("Property " + property +" isReadable expeced TRUE", PropertyUtils.isReadable(bean, property));
            fail("Property " + property +" isReadable expected NestedNullException");
        } catch (final NestedNullException e) {
            // expected result
        } catch (final Throwable t) {
            fail("Property " + property +" isReadable Threw exception: " + t);
        }
    }

    /**
     * Test isWriteable() method.
     */
    public void testIsWriteable() {
        String property = null;
        try {
            property = "stringProperty";
            assertTrue("Property " + property +" isWriteable expeced TRUE", PropertyUtils.isWriteable(bean, property));
        } catch (final Throwable t) {
            fail("Property " + property +" isWriteable Threw exception: " + t);
        }
        try {
            property = "stringIndexed";
            assertTrue("Property " + property +" isWriteable expeced TRUE", PropertyUtils.isWriteable(bean, property));
        } catch (final Throwable t) {
            fail("Property " + property +" isWriteable Threw exception: " + t);
        }
        try {
            property = "mappedProperty";
            assertTrue("Property " + property +" isWriteable expeced TRUE", PropertyUtils.isWriteable(bean, property));
        } catch (final Throwable t) {
            fail("Property " + property +" isWriteable Threw exception: " + t);
        }

        try {
            property = "nestedDynaBean";
            assertTrue("Property " + property +" isWriteable expeced TRUE", PropertyUtils.isWriteable(bean, property));
        } catch (final Throwable t) {
            fail("Property " + property +" isWriteable Threw exception: " + t);
        }

        try {
            property = "nestedDynaBean.stringProperty";
            assertTrue("Property " + property +" isWriteable expeced TRUE", PropertyUtils.isWriteable(bean, property));
        } catch (final Throwable t) {
            t.printStackTrace();
            fail("Property " + property +" isWriteable Threw exception: " + t);
        }

        try {
            property = "nestedDynaBean.nestedBean";
            assertTrue("Property " + property +" isWriteable expeced TRUE", PropertyUtils.isWriteable(bean, property));
        } catch (final Throwable t) {
            fail("Property " + property +" isWriteable Threw exception: " + t);
        }

        try {
            property = "nestedDynaBean.nestedBean.nestedDynaBean";
            assertTrue("Property " + property +" isWriteable expeced TRUE", PropertyUtils.isWriteable(bean, property));
        } catch (final Throwable t) {
            fail("Property " + property +" isWriteable Threw exception: " + t);
        }

        try {
            property = "nestedDynaBean.nestedBean.nestedDynaBean.stringProperty";
            assertTrue("Property " + property +" isWriteable expeced TRUE", PropertyUtils.isWriteable(bean, property));
        } catch (final Throwable t) {
            fail("Property " + property +" isWriteable Threw exception: " + t);
        }

        try {
            property = "nestedDynaBean.nullDynaBean";
            assertTrue("Property " + property +" isWriteable expeced TRUE", PropertyUtils.isWriteable(bean, property));
        } catch (final Throwable t) {
            fail("Property " + property +" isWriteable Threw exception: " + t);
        }

        try {
            property = "nestedDynaBean.nullDynaBean.foo";
            assertTrue("Property " + property +" isWriteable expeced TRUE", PropertyUtils.isWriteable(bean, property));
            fail("Property " + property +" isWriteable expected NestedNullException");
        } catch (final NestedNullException e) {
            // expected result
        } catch (final Throwable t) {
            fail("Property " + property +" isWriteable Threw exception: " + t);
        }
    }


    /**
     * Test the mappedPropertyType of MappedPropertyDescriptor.
     */
    public void testMappedPropertyType() throws Exception {

        MappedPropertyDescriptor desc;

        // Check a String property
        desc = (MappedPropertyDescriptor)
                PropertyUtils.getPropertyDescriptor(bean,
                        "mappedProperty");
        assertEquals(String.class, desc.getMappedPropertyType());

        // Check an int property
        desc = (MappedPropertyDescriptor)
                PropertyUtils.getPropertyDescriptor(bean,
                        "mappedIntProperty");
        assertEquals(Integer.TYPE, desc.getMappedPropertyType());

    }


    /**
     * Corner cases on setIndexedProperty invalid arguments.
     */
    public void testSetIndexedArguments() {

        // Use explicit index argument

        try {
            PropertyUtils.setIndexedProperty(null, "intArray", 0,
                    new Integer(1));
            fail("Should throw IllegalArgumentException 1");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.setIndexedProperty(bean, null, 0,
                    new Integer(1));
            fail("Should throw IllegalArgumentException 2");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

        // Use index expression

        try {
            PropertyUtils.setIndexedProperty(null,
                    "intArray[0]",
                    new Integer(1));
            fail("Should throw IllegalArgumentException 3");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 3");
        }

        try {
            PropertyUtils.setIndexedProperty(bean, "[0]",
                    new Integer(1));
            fail("Should throw NoSuchMethodException 4");
        } catch (final NoSuchMethodException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of NoSuchMethodException 4");
        }

        try {
            PropertyUtils.setIndexedProperty(bean, "intArray",
                    new Integer(1));
            fail("Should throw IllegalArgumentException 5");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 5");
        }

        // Use explicit index argument

        try {
            PropertyUtils.setIndexedProperty(null, "intIndexed", 0,
                    new Integer(1));
            fail("Should throw IllegalArgumentException 1");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.setIndexedProperty(bean, null, 0,
                    new Integer(1));
            fail("Should throw IllegalArgumentException 2");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

        // Use index expression

        try {
            PropertyUtils.setIndexedProperty(null,
                    "intIndexed[0]",
                    new Integer(1));
            fail("Should throw IllegalArgumentException 3");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 3");
        }

        try {
            PropertyUtils.setIndexedProperty(bean, "[0]",
                    new Integer(1));
            fail("Should throw NoSuchMethodException 4");
        } catch (final NoSuchMethodException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of NoSuchMethodException 4");
        }

        try {
            PropertyUtils.setIndexedProperty(bean, "intIndexed",
                    new Integer(1));
            fail("Should throw IllegalArgumentException 5");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 5");
        }

    }

    /**
     * Test setting an indexed value out of a multi-dimensional array
     */
    public void testSetIndexedArray() {
        final String[] firstArray = new String[] {"FIRST-1", "FIRST-2", "FIRST-3"};
        final String[] secondArray = new String[] {"SECOND-1", "SECOND-2", "SECOND-3",  "SECOND-4"};
        final String[][] mainArray = {firstArray, secondArray};
        final TestBean bean = new TestBean(mainArray);
        assertEquals("BEFORE", "SECOND-3", bean.getString2dArray(1)[2]);
        try {
            PropertyUtils.setProperty(bean, "string2dArray[1][2]", "SECOND-3-UPDATED");
        } catch (final Throwable t) {
            fail("Threw " + t + "");
        }
        assertEquals("AFTER", "SECOND-3-UPDATED", bean.getString2dArray(1)[2]);
    }

    /**
     * Test setting an indexed value out of List of Lists
     */
    public void testSetIndexedList() {
        final String[] firstArray = new String[] {"FIRST-1", "FIRST-2", "FIRST-3"};
        final String[] secondArray = new String[] {"SECOND-1", "SECOND-2", "SECOND-3",  "SECOND-4"};
        final List<Object> mainList   = new ArrayList<Object>();
        mainList.add(Arrays.asList(firstArray));
        mainList.add(Arrays.asList(secondArray));
        final TestBean bean = new TestBean(mainList);
        assertEquals("BEFORE", "SECOND-4", ((List<?>)bean.getListIndexed().get(1)).get(3));
        try {
            PropertyUtils.setProperty(bean, "listIndexed[1][3]", "SECOND-4-UPDATED");
        } catch (final Throwable t) {
            fail("Threw " + t + "");
        }
        assertEquals("AFTER", "SECOND-4-UPDATED", ((List<?>)bean.getListIndexed().get(1)).get(3));
    }

    /**
     * Test setting a value out of a mapped Map
     */
    public void testSetIndexedMap() {
        final Map<String, Object> firstMap  = new HashMap<String, Object>();
        firstMap.put("FIRST-KEY-1", "FIRST-VALUE-1");
        firstMap.put("FIRST-KEY-2", "FIRST-VALUE-2");
        final Map<String, Object> secondMap  = new HashMap<String, Object>();
        secondMap.put("SECOND-KEY-1", "SECOND-VALUE-1");
        secondMap.put("SECOND-KEY-2", "SECOND-VALUE-2");

        final List<Object> mainList = new ArrayList<Object>();
        mainList.add(firstMap);
        mainList.add(secondMap);
        final TestBean bean = new TestBean(mainList);

        assertEquals("BEFORE",  null,              ((Map<?, ?>)bean.getListIndexed().get(0)).get("FIRST-NEW-KEY"));
        assertEquals("BEFORE",  "SECOND-VALUE-1",  ((Map<?, ?>)bean.getListIndexed().get(1)).get("SECOND-KEY-1"));
        try {
            PropertyUtils.setProperty(bean, "listIndexed[0](FIRST-NEW-KEY)", "FIRST-NEW-VALUE");
            PropertyUtils.setProperty(bean, "listIndexed[1](SECOND-KEY-1)",  "SECOND-VALUE-1-UPDATED");
        } catch (final Throwable t) {
            fail("Threw " + t + "");
        }
        assertEquals("BEFORE", "FIRST-NEW-VALUE",         ((Map<?, ?>)bean.getListIndexed().get(0)).get("FIRST-NEW-KEY"));
        assertEquals("AFTER",  "SECOND-VALUE-1-UPDATED",  ((Map<?, ?>)bean.getListIndexed().get(1)).get("SECOND-KEY-1"));
    }


    /**
     * Positive and negative tests on setIndexedProperty valid arguments.
     */
    public void testSetIndexedValues() {

        Object value = null;

        // Use explicit index argument

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "dupProperty", 0,
                    "New 0");
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "dupProperty", 0);
            assertNotNull("Returned new value 0", value);
            assertTrue("Returned String new value 0",
                    value instanceof String);
            assertEquals("Returned correct new value 0", "New 0",
                    (String) value);
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "intArray", 0,
                    new Integer(1));
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "intArray", 0);
            assertNotNull("Returned new value 0", value);
            assertTrue("Returned Integer new value 0",
                    value instanceof Integer);
            assertEquals("Returned correct new value 0", 1,
                    ((Integer) value).intValue());
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "intIndexed", 1,
                    new Integer(11));
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "intIndexed", 1);
            assertNotNull("Returned new value 1", value);
            assertTrue("Returned Integer new value 1",
                    value instanceof Integer);
            assertEquals("Returned correct new value 1", 11,
                    ((Integer) value).intValue());
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "listIndexed", 2,
                    "New Value 2");
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "listIndexed", 2);
            assertNotNull("Returned new value 2", value);
            assertTrue("Returned String new value 2",
                    value instanceof String);
            assertEquals("Returned correct new value 2", "New Value 2",
                    (String) value);
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "stringArray", 2,
                    "New Value 2");
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "stringArray", 2);
            assertNotNull("Returned new value 2", value);
            assertTrue("Returned String new value 2",
                    value instanceof String);
            assertEquals("Returned correct new value 2", "New Value 2",
                    (String) value);
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "stringArray", 3,
                    "New Value 3");
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "stringArray", 3);
            assertNotNull("Returned new value 3", value);
            assertTrue("Returned String new value 3",
                    value instanceof String);
            assertEquals("Returned correct new value 3", "New Value 3",
                    (String) value);
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        // Use index expression

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "dupProperty[4]",
                    "New 4");
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "dupProperty[4]");
            assertNotNull("Returned new value 4", value);
            assertTrue("Returned String new value 4",
                    value instanceof String);
            assertEquals("Returned correct new value 4", "New 4",
                         (String) value);
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "intArray[4]",
                    new Integer(1));
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "intArray[4]");
            assertNotNull("Returned new value 4", value);
            assertTrue("Returned Integer new value 4",
                    value instanceof Integer);
            assertEquals("Returned correct new value 4", 1,
                    ((Integer) value).intValue());
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "intIndexed[3]",
                    new Integer(11));
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "intIndexed[3]");
            assertNotNull("Returned new value 5", value);
            assertTrue("Returned Integer new value 5",
                    value instanceof Integer);
            assertEquals("Returned correct new value 5", 11,
                    ((Integer) value).intValue());
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "listIndexed[1]",
                    "New Value 2");
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "listIndexed[1]");
            assertNotNull("Returned new value 6", value);
            assertTrue("Returned String new value 6",
                    value instanceof String);
            assertEquals("Returned correct new value 6", "New Value 2",
                    (String) value);
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "stringArray[1]",
                    "New Value 2");
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "stringArray[2]");
            assertNotNull("Returned new value 6", value);
            assertTrue("Returned String new value 6",
                    value instanceof String);
            assertEquals("Returned correct new value 6", "New Value 2",
                    (String) value);
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "stringArray[0]",
                    "New Value 3");
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "stringArray[0]");
            assertNotNull("Returned new value 7", value);
            assertTrue("Returned String new value 7",
                    value instanceof String);
            assertEquals("Returned correct new value 7", "New Value 3",
                    (String) value);
        } catch (final Throwable t) {
            fail("Threw " + t);
        }

        // Index out of bounds tests

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "dupProperty", -1,
                    "New -1");
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "dupProperty", 5,
                    "New 5");
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "intArray", -1,
                    new Integer(0));
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "intArray", 5,
                    new Integer(0));
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "intIndexed", -1,
                    new Integer(0));
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "intIndexed", 5,
                    new Integer(0));
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "listIndexed", 5,
                    "New String");
            fail("Should have thrown IndexOutOfBoundsException");
        } catch (final IndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "listIndexed", -1,
                    "New String");
            fail("Should have thrown IndexOutOfBoundsException");
        } catch (final IndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "stringArray", -1,
                    "New String");
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "stringArray", 5,
                    "New String");
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "stringIndexed", -1,
                    "New String");
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "stringIndexed", 5,
                    "New String");
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException t) {
            // Expected results
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

    }


    /**
     * Corner cases on getMappedProperty invalid arguments.
     */
    public void testSetMappedArguments() {

        // Use explicit key argument

        try {
            PropertyUtils.setMappedProperty(null, "mappedProperty",
                    "First Key", "First Value");
            fail("Should throw IllegalArgumentException 1");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.setMappedProperty(bean, null, "First Key",
                    "First Value");
            fail("Should throw IllegalArgumentException 2");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

        try {
            PropertyUtils.setMappedProperty(bean, "mappedProperty", null,
                    "First Value");
            fail("Should throw IllegalArgumentException 3");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 3");
        }

        // Use key expression

        try {
            PropertyUtils.setMappedProperty(null,
                    "mappedProperty(First Key)",
                    "First Value");
            fail("Should throw IllegalArgumentException 4");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 4");
        }

        try {
            PropertyUtils.setMappedProperty(bean, "(Second Key)",
                    "Second Value");
            fail("Should throw IllegalArgumentException 5");
        } catch (final NoSuchMethodException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of NoSuchMethodException 5");
        }

        try {
            PropertyUtils.setMappedProperty(bean, "mappedProperty",
                    "Third Value");
            fail("Should throw IllegalArgumentException 6");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 6");
        }

    }


    /**
     * Test setting an indexed value out of a mapped array
     */
    public void testSetMappedArray() {
        final TestBean bean = new TestBean();
        final String[] array = new String[] {"abc", "def", "ghi"};
        bean.getMapProperty().put("mappedArray", array);

        assertEquals("BEFORE", "def", ((String[])bean.getMapProperty().get("mappedArray"))[1]);
        try {
            PropertyUtils.setProperty(bean, "mapProperty(mappedArray)[1]", "DEF-UPDATED");
        } catch (final Throwable t) {
            fail("Threw " + t + "");
        }
        assertEquals("AFTER", "DEF-UPDATED", ((String[])bean.getMapProperty().get("mappedArray"))[1]);
    }

    /**
     * Test setting an indexed value out of a mapped List
     */
    public void testSetMappedList() {
        final TestBean bean = new TestBean();
        final List<Object> list = new ArrayList<Object>();
        list.add("klm");
        list.add("nop");
        list.add("qrs");
        bean.getMapProperty().put("mappedList", list);

        assertEquals("BEFORE", "klm", ((List<?>)bean.getMapProperty().get("mappedList")).get(0));
        try {
            PropertyUtils.setProperty(bean, "mapProperty(mappedList)[0]", "KLM-UPDATED");
        } catch (final Throwable t) {
            fail("Threw " + t + "");
        }
        assertEquals("AFTER", "KLM-UPDATED", ((List<?>)bean.getMapProperty().get("mappedList")).get(0));
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
            PropertyUtils.setProperty(bean, "mapProperty(mappedMap)(sub-key-3)", "SUB-KEY-3-UPDATED");
        } catch (final Throwable t) {
            fail("Threw " + t + "");
        }
        assertEquals("AFTER", "SUB-KEY-3-UPDATED", ((Map<?, ?>)bean.getMapProperty().get("mappedMap")).get("sub-key-3"));
    }

    /**
     * Positive and negative tests on setMappedProperty valid arguments.
     */
    public void testSetMappedValues() {

        Object value = null;

        // Use explicit key argument

        try {
            value = PropertyUtils.getMappedProperty(bean, "mappedProperty",
                    "Fourth Key");
            assertNull("Can not find fourth value", value);
        } catch (final Throwable t) {
            fail("Finding fourth value threw " + t);
        }

        try {
            PropertyUtils.setMappedProperty(bean, "mappedProperty",
                    "Fourth Key", "Fourth Value");
        } catch (final Throwable t) {
            fail("Setting fourth value threw " + t);
        }

        try {
            value = PropertyUtils.getMappedProperty(bean, "mappedProperty",
                    "Fourth Key");
            assertEquals("Can find fourth value", "Fourth Value", value);
        } catch (final Throwable t) {
            fail("Finding fourth value threw " + t);
        }

        // Use key expression with parentheses

        try {
            value =
                    PropertyUtils.getMappedProperty(bean,
                            "mappedProperty(Fifth Key)");
            assertNull("Can not find fifth value", value);
        } catch (final Throwable t) {
            fail("Finding fifth value threw " + t);
        }

        try {
            PropertyUtils.setMappedProperty(bean,
                    "mappedProperty(Fifth Key)",
                    "Fifth Value");
        } catch (final Throwable t) {
            fail("Setting fifth value threw " + t);
        }

        try {
            value =
                    PropertyUtils.getMappedProperty(bean,
                            "mappedProperty(Fifth Key)");
            assertEquals("Can find fifth value", "Fifth Value", value);
        } catch (final Throwable t) {
            fail("Finding fifth value threw " + t);
        }

        // Use key expression with dotted expression

        try {
            value =
                    PropertyUtils.getNestedProperty(bean,
                            "mapProperty.Sixth Key");
            assertNull("Can not find sixth value", value);
        } catch (final Throwable t) {
            fail("Finding fifth value threw " + t);
        }

        try {
            PropertyUtils.setNestedProperty(bean,
                    "mapProperty.Sixth Key",
                    "Sixth Value");
        } catch (final Throwable t) {
            fail("Setting sixth value threw " + t);
        }

        try {
            value =
                    PropertyUtils.getNestedProperty(bean,
                            "mapProperty.Sixth Key");
            assertEquals("Can find sixth value", "Sixth Value", value);
        } catch (final Throwable t) {
            fail("Finding sixth value threw " + t);
        }

    }

    /**
     * Test setting mapped values with periods in the key.
     */
    public void testSetMappedPeriods() {


        // -------- PropertyUtils.setMappedProperty()--------
        bean.setMappedProperty("key.with.a.dot", "Special Value");
        assertEquals("Can retrieve directly (A)",
                     "Special Value",
                     bean.getMappedProperty("key.with.a.dot"));

        try {
            PropertyUtils.setMappedProperty(bean, "mappedProperty", "key.with.a.dot", "Updated Special Value");
            assertEquals("Check set via setMappedProperty",
                         "Updated Special Value",
                          bean.getMappedProperty("key.with.a.dot"));
        } catch (final Exception e) {
            fail("Thew exception: " + e);
        }

        // -------- PropertyUtils.setNestedProperty() --------
        bean.setMappedProperty("key.with.a.dot", "Special Value");
        assertEquals("Can retrieve directly (B)",
                     "Special Value",
                     bean.getMappedProperty("key.with.a.dot"));
        try {
            PropertyUtils.setNestedProperty(bean, "mappedProperty(key.with.a.dot)", "Updated Special Value");
            assertEquals("Check set via setNestedProperty (B)",
                         "Updated Special Value",
                         bean.getMappedProperty("key.with.a.dot"));
        } catch (final Exception e) {
            fail("Thew exception: " + e);
        }


        // -------- PropertyUtils.setNestedProperty() --------
        final TestBean testBean = new TestBean();
        bean.setMappedObjects("nested.property", testBean);
        assertEquals("Can retrieve directly (C)",
                     "This is a string",
                     testBean.getStringProperty());
        try {
            PropertyUtils.setNestedProperty(bean, "mappedObjects(nested.property).stringProperty",
                                                  "Updated String Value");
            assertEquals("Check set via setNestedProperty (C)",
                         "Updated String Value",
                         testBean.getStringProperty());
        } catch (final Exception e) {
            fail("Thew exception: " + e);
        }

        // -------- PropertyUtils.setNestedProperty() --------
        bean.getNested().setMappedProperty("Mapped Key", "Nested Mapped Value");
        try {
            assertEquals("Can retrieve via getNestedProperty (D)",
                         "Nested Mapped Value",
                         PropertyUtils.getNestedProperty(
                             bean,"nested.mappedProperty(Mapped Key)"));
            PropertyUtils.setNestedProperty(bean, "nested.mappedProperty(Mapped Key)",
                                                  "Updated Nested Mapped Value");
            assertEquals("Check set via setNestedProperty (D)",
                         "Updated Nested Mapped Value",
                         PropertyUtils.getNestedProperty(
                             bean,"nested.mappedProperty(Mapped Key)"));
        } catch (final Exception e) {
            fail("Thew exception: " + e);
        }
    }


    /**
     * Corner cases on setNestedProperty invalid arguments.
     */
    public void testSetNestedArguments() {

        try {
            PropertyUtils.setNestedProperty(null, "stringProperty", "");
            fail("Should throw IllegalArgumentException 1");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.setNestedProperty(bean, null, "");
            fail("Should throw IllegalArgumentException 2");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

    }


    /**
     * Test setNextedProperty on a boolean property.
     */
    public void testSetNestedBoolean() {

        try {
            final boolean oldValue = bean.getNested().getBooleanProperty();
            final boolean newValue = !oldValue;
            PropertyUtils.setNestedProperty(bean,
                    "nested.booleanProperty",
                    new Boolean(newValue));
            assertTrue("Matched new value",
                    newValue ==
                    bean.getNested().getBooleanProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setNestedProperty on a double property.
     */
    public void testSetNestedDouble() {

        try {
            final double oldValue = bean.getNested().getDoubleProperty();
            final double newValue = oldValue + 1.0;
            PropertyUtils.setNestedProperty(bean,
                    "nested.doubleProperty",
                    new Double(newValue));
            assertEquals("Matched new value",
                    newValue,
                    bean.getNested().getDoubleProperty(),
                    0.005);
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setNestedProperty on a float property.
     */
    public void testSetNestedFloat() {

        try {
            final float oldValue = bean.getNested().getFloatProperty();
            final float newValue = oldValue + (float) 1.0;
            PropertyUtils.setNestedProperty(bean,
                    "nested.floatProperty",
                    new Float(newValue));
            assertEquals("Matched new value",
                    newValue,
                    bean.getNested().getFloatProperty(),
                    (float) 0.005);
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setNestedProperty on a int property.
     */
    public void testSetNestedInt() {

        try {
            final int oldValue = bean.getNested().getIntProperty();
            final int newValue = oldValue + 1;
            PropertyUtils.setNestedProperty(bean,
                    "nested.intProperty",
                    new Integer(newValue));
            assertEquals("Matched new value",
                    newValue,
                    bean.getNested().getIntProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setNestedProperty on a long property.
     */
    public void testSetNestedLong() {

        try {
            final long oldValue = bean.getNested().getLongProperty();
            final long newValue = oldValue + 1;
            PropertyUtils.setNestedProperty(bean,
                    "nested.longProperty",
                    new Long(newValue));
            assertEquals("Matched new value",
                    newValue,
                    bean.getNested().getLongProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setNestedProperty on a read-only String property.
     */
    public void testSetNestedReadOnly() {

        try {
            final String oldValue = bean.getNested().getWriteOnlyPropertyValue();
            final String newValue = oldValue + " Extra Value";
            PropertyUtils.setNestedProperty(bean,
                    "nested.readOnlyProperty",
                    newValue);
            fail("Should have thrown NoSuchMethodException");
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            // Correct result for this test
        }

    }


    /**
     * Test setNestedProperty on a short property.
     */
    public void testSetNestedShort() {

        try {
            final short oldValue = bean.getNested().getShortProperty();
            short newValue = oldValue;
            newValue++;
            PropertyUtils.setNestedProperty(bean,
                    "nested.shortProperty",
                    new Short(newValue));
            assertEquals("Matched new value",
                    newValue,
                    bean.getNested().getShortProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setNestedProperty on a String property.
     */
    public void testSetNestedString() {

        try {
            final String oldValue = bean.getNested().getStringProperty();
            final String newValue = oldValue + " Extra Value";
            PropertyUtils.setNestedProperty(bean,
                    "nested.stringProperty",
                    newValue);
            assertEquals("Matched new value",
                    newValue,
                    bean.getNested().getStringProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setNestedProperty on an unknown property name.
     */
    public void testSetNestedUnknown() {

        try {
            final String newValue = "New String Value";
            PropertyUtils.setNestedProperty(bean,
                    "nested.unknown",
                    newValue);
            fail("Should have thrown NoSuchMethodException");
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            // Correct result for this test
        }

    }


    /**
     * Test setNestedProperty on a write-only String property.
     */
    public void testSetNestedWriteOnly() {

        try {
            final String oldValue = bean.getNested().getWriteOnlyPropertyValue();
            final String newValue = oldValue + " Extra Value";
            PropertyUtils.setNestedProperty(bean,
                    "nested.writeOnlyProperty",
                    newValue);
            assertEquals("Matched new value",
                    newValue,
                    bean.getNested().getWriteOnlyPropertyValue());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Corner cases on setSimpleProperty invalid arguments.
     */
    public void testSetSimpleArguments() {

        try {
            PropertyUtils.setSimpleProperty(null, "stringProperty", "");
            fail("Should throw IllegalArgumentException 1");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.setSimpleProperty(bean, null, "");
            fail("Should throw IllegalArgumentException 2");
        } catch (final IllegalArgumentException e) {
            // Expected response
        } catch (final Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

    }


    /**
     * Test setSimpleProperty on a boolean property.
     */
    public void testSetSimpleBoolean() {

        try {
            final boolean oldValue = bean.getBooleanProperty();
            final boolean newValue = !oldValue;
            PropertyUtils.setSimpleProperty(bean,
                    "booleanProperty",
                    new Boolean(newValue));
            assertTrue("Matched new value",
                    newValue ==
                    bean.getBooleanProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setSimpleProperty on a double property.
     */
    public void testSetSimpleDouble() {

        try {
            final double oldValue = bean.getDoubleProperty();
            final double newValue = oldValue + 1.0;
            PropertyUtils.setSimpleProperty(bean,
                    "doubleProperty",
                    new Double(newValue));
            assertEquals("Matched new value",
                    newValue,
                    bean.getDoubleProperty(),
                    0.005);
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setSimpleProperty on a float property.
     */
    public void testSetSimpleFloat() {

        try {
            final float oldValue = bean.getFloatProperty();
            final float newValue = oldValue + (float) 1.0;
            PropertyUtils.setSimpleProperty(bean,
                    "floatProperty",
                    new Float(newValue));
            assertEquals("Matched new value",
                    newValue,
                    bean.getFloatProperty(),
                    (float) 0.005);
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Negative test setSimpleProperty on an indexed property.
     */
    public void testSetSimpleIndexed() {

        try {
            PropertyUtils.setSimpleProperty(bean,
                    "stringIndexed[0]",
                    "New String Value");
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            // Correct result for this test
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setSimpleProperty on a int property.
     */
    public void testSetSimpleInt() {

        try {
            final int oldValue = bean.getIntProperty();
            final int newValue = oldValue + 1;
            PropertyUtils.setSimpleProperty(bean,
                    "intProperty",
                    new Integer(newValue));
            assertEquals("Matched new value",
                    newValue,
                    bean.getIntProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setSimpleProperty on a long property.
     */
    public void testSetSimpleLong() {

        try {
            final long oldValue = bean.getLongProperty();
            final long newValue = oldValue + 1;
            PropertyUtils.setSimpleProperty(bean,
                    "longProperty",
                    new Long(newValue));
            assertEquals("Matched new value",
                    newValue,
                    bean.getLongProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Negative test setSimpleProperty on a nested property.
     */
    public void testSetSimpleNested() {

        try {
            PropertyUtils.setSimpleProperty(bean,
                    "nested.stringProperty",
                    "New String Value");
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            // Correct result for this test
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setSimpleProperty on a read-only String property.
     */
    public void testSetSimpleReadOnly() {

        try {
            final String oldValue = bean.getWriteOnlyPropertyValue();
            final String newValue = oldValue + " Extra Value";
            PropertyUtils.setSimpleProperty(bean,
                    "readOnlyProperty",
                    newValue);
            fail("Should have thrown NoSuchMethodException");
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            // Correct result for this test
            assertEquals("Property 'readOnlyProperty' has no setter method in class '" +
                         bean.getClass() + "'", e.getMessage() );
        }

    }


    /**
     * Test setSimpleProperty on a short property.
     */
    public void testSetSimpleShort() {

        try {
            final short oldValue = bean.getShortProperty();
            short newValue = oldValue;
            newValue++;
            PropertyUtils.setSimpleProperty(bean,
                    "shortProperty",
                    new Short(newValue));
            assertEquals("Matched new value",
                    newValue,
                    bean.getShortProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setSimpleProperty on a String property.
     */
    public void testSetSimpleString() {

        try {
            final String oldValue = bean.getStringProperty();
            final String newValue = oldValue + " Extra Value";
            PropertyUtils.setSimpleProperty(bean,
                    "stringProperty",
                    newValue);
            assertEquals("Matched new value",
                    newValue,
                    bean.getStringProperty());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setSimpleProperty on an unknown property name.
     */
    public void testSetSimpleUnknown() {

        try {
            final String newValue = "New String Value";
            PropertyUtils.setSimpleProperty(bean,
                    "unknown",
                    newValue);
            fail("Should have thrown NoSuchMethodException");
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            // Correct result for this test
            assertEquals("Unknown property 'unknown' on class '" +
                         bean.getClass() + "'", e.getMessage() );
        }

    }


    /**
     * Test setSimpleProperty on a write-only String property.
     */
    public void testSetSimpleWriteOnly() {

        try {
            final String oldValue = bean.getWriteOnlyPropertyValue();
            final String newValue = oldValue + " Extra Value";
            PropertyUtils.setSimpleProperty(bean,
                    "writeOnlyProperty",
                    newValue);
            assertEquals("Matched new value",
                    newValue,
                    bean.getWriteOnlyPropertyValue());
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    // ------------------------------------------------------ Protected Methods


    /**
     * Base for testGetDescriptorXxxxx() series of tests.
     *
     * @param name Name of the property to be retrieved
     * @param read Expected name of the read method (or null)
     * @param write Expected name of the write method (or null)
     */
    protected void testGetDescriptorBase(final String name, final String read,
                                         final String write) {

        try {
            final PropertyDescriptor pd =
                    PropertyUtils.getPropertyDescriptor(bean, name);
            if ((read != null) || (write != null)) {
                assertNotNull("Got descriptor", pd);
            } else {
                assertNull("Got descriptor", pd);
                return;
            }
            final Method rm = pd.getReadMethod();
            if (read != null) {
                assertNotNull("Got read method", rm);
                assertEquals("Got correct read method",
                        rm.getName(), read);
            } else {
                assertNull("Got read method", rm);
            }
            final Method wm = pd.getWriteMethod();
            if (write != null) {
                assertNotNull("Got write method", wm);
                assertEquals("Got correct write method",
                        wm.getName(), write);
            } else {
                assertNull("Got write method", wm);
            }
        } catch (final IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (final InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (final NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Base for testGetReadMethod() series of tests.
     *
     * @param bean Bean for which to retrieve read methods.
     * @param properties Property names to search for
     * @param className Class name where this method should be defined
     */
    protected void testGetReadMethod(final Object bean, final String properties[],
                                     final String className) {

        final PropertyDescriptor pd[] =
                PropertyUtils.getPropertyDescriptors(bean);
        for (String propertie : properties) {

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
            assertTrue("PropertyDescriptor for " + propertie,
                    n >= 0);

            // Locate an accessible property reader method for it
            final Method reader = PropertyUtils.getReadMethod(pd[n]);
            assertNotNull("Reader for " + propertie,
                    reader);
            final Class<?> clazz = reader.getDeclaringClass();
            assertNotNull("Declaring class for " + propertie,
                    clazz);
            assertEquals("Correct declaring class for " + propertie,
                    clazz.getName(),
                    className);

            // Actually call the reader method we received
            try {
                reader.invoke(bean, (Object[]) new Class<?>[0]);
            } catch (final Throwable t) {
                fail("Call for " + propertie + ": " + t);
            }

        }

    }


    /**
     * Base for testGetWriteMethod() series of tests.
     *
     * @param bean Bean for which to retrieve write methods.
     * @param properties Property names to search for
     * @param className Class name where this method should be defined
     */
    protected void testGetWriteMethod(final Object bean, final String properties[],
                                      final String className) {


        final PropertyDescriptor pd[] =
                PropertyUtils.getPropertyDescriptors(bean);
        for (String propertie : properties) {

            // Identify the property descriptor for this property
            if (propertie.equals("intIndexed")) {
                continue;
            }
            if (propertie.equals("listIndexed")) {
                continue;
            }
            if (propertie.equals("nested"))
             {
                continue; // This property is read only
            }
            if (propertie.equals("readOnlyProperty")) {
                continue;
            }
            if (propertie.equals("stringIndexed")) {
                continue;
            }
            int n = -1;
            for (int j = 0; j < pd.length; j++) {
                if (propertie.equals(pd[j].getName())) {
                    n = j;
                    break;
                }
            }
            assertTrue("PropertyDescriptor for " + propertie,
                    n >= 0);

            // Locate an accessible property reader method for it
            final Method writer = PropertyUtils.getWriteMethod(pd[n]);
            assertNotNull("Writer for " + propertie,
                    writer);
            final Class<?> clazz = writer.getDeclaringClass();
            assertNotNull("Declaring class for " + propertie,
                    clazz);
            assertEquals("Correct declaring class for " + propertie,
                    clazz.getName(),
                    className);

        }

    }

    public void testNestedWithIndex() throws Exception
    {
        final NestedTestBean nestedBean = new NestedTestBean("base");
        nestedBean.init();
        nestedBean.getSimpleBeanProperty().init();

        NestedTestBean

        // test first calling properties on indexed beans

        value = (NestedTestBean) PropertyUtils.getProperty(
                                nestedBean,
                                "indexedProperty[0]");
        assertEquals("Cannot get simple index(1)", "Bean@0", value.getName());
        assertEquals("Bug in NestedTestBean", "NOT SET", value.getTestString());

        value = (NestedTestBean) PropertyUtils.getProperty(
                                nestedBean,
                                "indexedProperty[1]");
        assertEquals("Cannot get simple index(1)", "Bean@1", value.getName());
        assertEquals("Bug in NestedTestBean", "NOT SET", value.getTestString());

        String
        prop = (String) PropertyUtils.getProperty(
                                nestedBean,
                                "indexedProperty[0].testString");
        assertEquals("Get property on indexes failed (1)", "NOT SET", prop);

        prop = (String) PropertyUtils.getProperty(
                                nestedBean,
                                "indexedProperty[1].testString");
        assertEquals("Get property on indexes failed (2)", "NOT SET", prop);

        PropertyUtils.setProperty(
                                nestedBean,
                                "indexedProperty[0].testString",
                                "Test#1");
        assertEquals(
                "Cannot set property on indexed bean (1)",
                "Test#1",
                nestedBean.getIndexedProperty(0).getTestString());

        PropertyUtils.setProperty(
                                nestedBean,
                                "indexedProperty[1].testString",
                                "Test#2");
        assertEquals(
                "Cannot set property on indexed bean (2)",
                "Test#2",
                nestedBean.getIndexedProperty(1).getTestString());


        // test first calling indexed properties on a simple property

        value = (NestedTestBean) PropertyUtils.getProperty(
                                nestedBean,
                                "simpleBeanProperty");
        assertEquals("Cannot get simple bean", "Simple Property Bean", value.getName());
        assertEquals("Bug in NestedTestBean", "NOT SET", value.getTestString());

        value = (NestedTestBean) PropertyUtils.getProperty(
                                nestedBean,
                                "simpleBeanProperty.indexedProperty[3]");
        assertEquals("Cannot get index property on property", "Bean@3", value.getName());
        assertEquals("Bug in NestedTestBean", "NOT SET", value.getTestString());

        PropertyUtils.setProperty(
                                nestedBean,
                                "simpleBeanProperty.indexedProperty[3].testString",
                                "Test#3");
        assertEquals(
            "Cannot set property on indexed property on property",
            "Test#3",
            nestedBean.getSimpleBeanProperty().getIndexedProperty(3).getTestString());
    }

    /** Text case for setting properties on inner classes */
    public void testGetSetInnerBean() throws Exception {
        final BeanWithInnerBean bean = new BeanWithInnerBean();

        PropertyUtils.setProperty(bean, "innerBean.fish(loiterTimer)", "5");
        String out = (String) PropertyUtils.getProperty(bean.getInnerBean(), "fish(loiterTimer)");
        assertEquals(
                "(1) Inner class property set/get property failed.",
                "5",
                out);

        out = (String) PropertyUtils.getProperty(bean, "innerBean.fish(loiterTimer)");

        assertEquals(
                "(2) Inner class property set/get property failed.",
                "5",
                out);
    }

    /** Text case for setting properties on parent */
    public void testGetSetParentBean() throws Exception {

        final SonOfAlphaBean bean = new SonOfAlphaBean("Roger");

        final String out = (String) PropertyUtils.getProperty(bean, "name");
        assertEquals(
                "(1) Get/Set On Parent.",
                "Roger",
                out);

        PropertyUtils.setProperty(bean, "name", "abcd");
        assertEquals(
                "(2) Get/Set On Parent.",
                "abcd",
                bean.getName());
    }

    public void testSetNoGetter() throws Exception
    {
        final BetaBean bean = new BetaBean("Cedric");

        // test standard no getter
        bean.setNoGetterProperty("Sigma");
        assertEquals("BetaBean test failed", "Sigma", bean.getSecret());

        assertNotNull("Descriptor is null", PropertyUtils.getPropertyDescriptor(bean, "noGetterProperty"));

        BeanUtils.setProperty(bean, "noGetterProperty",  "Omega");
        assertEquals("Cannot set no-getter property", "Omega", bean.getSecret());

        // test mapped no getter descriptor
        assertNotNull("Map Descriptor is null", PropertyUtils.getPropertyDescriptor(bean, "noGetterMappedProperty"));

        PropertyUtils.setMappedProperty(bean, "noGetterMappedProperty",  "Epsilon", "Epsilon");
        assertEquals("Cannot set mapped no-getter property", "MAP:Epsilon", bean.getSecret());
    }

    /**
     * Test accessing a public sub-bean of a package scope bean
     */
    public void testSetPublicSubBean_of_PackageBean() {

        final PublicSubBean bean = new PublicSubBean();
        bean.setFoo("foo-start");
        bean.setBar("bar-start");

        // Set Foo
        try {
            PropertyUtils.setProperty(bean, "foo", "foo-updated");
        } catch (final Throwable t) {
            fail("setProperty(foo) threw " + t);
        }
        assertEquals("foo property", "foo-updated", bean.getFoo());

        // Set Bar
        try {
            PropertyUtils.setProperty(bean, "bar", "bar-updated");
        } catch (final Throwable t) {
            fail("setProperty(bar) threw " + t);
        }
        assertEquals("bar property", "bar-updated", bean.getBar());
    }

    /**
     * There is an issue in setNestedProperty/getNestedProperty when the
     * target bean is a map and the name string requests mapped or indexed
     * operations on a field. These are not supported for fields of a Map,
     * but it's an easy mistake to make and this test case ensures that an
     * appropriate exception is thrown when a user does this.
     * <p>
     * The problem is with passing strings of form "a(b)" or "a[3]" to
     * setNestedProperty or getNestedProperty when the target bean they
     * are applied to implements Map. These strings are actually requesting
     * "the result of calling mapped method a on the target object with
     * a parameter of b" or "the result of calling indexed method a on the
     * target object with a parameter of 3". And these requests are not valid
     * when the target is a Map as a Map only supports calling get(fieldName)
     * or put(fieldName), neither of which can be further indexed with a
     * string or an integer.
     * <p>
     * However it is likely that some users will assume that "a[3]" when applied
     * to a map will be equivalent to (map.get("a"))[3] with the appropriate
     * typecasting, or for "a(b)" to be equivalent to map.get("a").get("b").
     * <p>
     * Here we verify that an exception is thrown if the user makes this
     * mistake.
     */
    public void testNestedPropertyKeyOrIndexOnBeanImplementingMap() throws Exception {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        final HashMap<String, Object> submap = new HashMap<String, Object>();
        final BetaBean betaBean1 = new BetaBean("test1");
        final BetaBean betaBean2 = new BetaBean("test2");

        // map.put("submap", submap)
        PropertyUtils.setNestedProperty(map, "submap", submap);

        // map.get("submap").put("beta1", betaBean1)
        PropertyUtils.setNestedProperty(map, "submap.beta1", betaBean1);
        assertEquals("Unexpected keys in map", "submap", keysToString(map));
        assertEquals("Unexpected keys in submap", "beta1", keysToString(submap));

        try {
            // One would expect that the command below would be equivalent to
            //   Map m = (Map) map.get("submap");
            //   m.put("beta2", betaBean2)
            // However this isn't how javabeans property methods work. A map
            // only effectively has "simple" properties, even when the
            // returned object is a Map or Array.
            PropertyUtils.setNestedProperty(map, "submap(beta2)", betaBean2);

            // What, no exception? In that case, setNestedProperties has
            // probably just tried to do
            //    map.set("submap(beta2)", betaBean2)
            // which is almost certainly not what the used expected. This is
            // what beanutils 1.5.0 to 1.7.1 did....
            fail("Exception not thrown for invalid setNestedProperty syntax");
        } catch(final IllegalArgumentException ex) {
            // ok, getting an exception was expected. As it is of a generic
            // type, let's check the message string to make sure it really
            // was caused by the issue we expected.
            final int index = ex.getMessage().indexOf(
                    "Indexed or mapped properties are not supported");
            assertTrue("Unexpected exception message", index>=0);
        }

        try {
            // One would expect that "submap[3]" would be equivalent to
            //   Object[] objects = (Object[]) map.get("submap");
            //   return objects[3];
            // However this isn't how javabeans property methods work. A map
            // only effectively has "simple" properties, even when the
            // returned object is a Map or Array.
            PropertyUtils.getNestedProperty(map, "submap[3]");

            // What, no exception? In that case, getNestedProperties has
            // probably just tried to do
            //    map.get("submap[3]")
            // which is almost certainly not what the used expected. This is
            // what beanutils 1.5.0 to 1.7.1 did....
            fail("Exception not thrown for invalid setNestedProperty syntax");
        } catch(final IllegalArgumentException ex) {
            // ok, getting an exception was expected. As it is of a generic
            // type, let's check the message string to make sure it really
            // was caused by the issue we expected.
            final int index = ex.getMessage().indexOf(
                    "Indexed or mapped properties are not supported");
            assertTrue("Unexpected exception message", index>=0);
        }
    }

    /**
     * Returns a single string containing all the keys in the map,
     * sorted in alphabetical order and separated by ", ".
     * <p>
     * If there are no keys, an empty string is returned.
     */
    private String keysToString(final Map<?, ?> map) {
        final Object[] mapKeys = map.keySet().toArray();
        java.util.Arrays.sort(mapKeys);
        final StringBuilder buf = new StringBuilder();
        for(int i=0; i<mapKeys.length; ++i) {
            if (i != 0) {
                buf.append(", ");
            }
            buf.append(mapKeys[i]);
        }
        return buf.toString();
    }

    /**
     * This tests to see that classes that implement Map always have their
     * custom properties ignored.
     * <p>
     * Note that this behaviour has changed several times over past releases
     * of beanutils, breaking backwards compatibility each time. Here's hoping
     * that the current 1.7.1 release is the last time this behaviour changes!
     */
    public void testMapExtensionDefault() throws Exception {
        final ExtendMapBean bean = new ExtendMapBean();

        // setting property direct should work, and not affect map
        bean.setUnusuallyNamedProperty("bean value");
        assertEquals("Set property direct failed", "bean value", bean.getUnusuallyNamedProperty());
        assertNull("Get on unset map property failed",
                PropertyUtils.getNestedProperty(bean, "unusuallyNamedProperty"));

        // setting simple property should call the setter method only, and not
        // affect the map.
        PropertyUtils.setSimpleProperty(bean, "unusuallyNamedProperty", "new value");
        assertEquals("Set property on map failed (1)", "new value", bean.getUnusuallyNamedProperty());
        assertNull("Get on unset map property failed",
                PropertyUtils.getNestedProperty(bean, "unusuallyNamedProperty"));

        // setting via setNestedProperty should affect the map only, and not
        // call the setter method.
        PropertyUtils.setProperty(bean, "unusuallyNamedProperty", "next value");
        assertEquals(
                "setNestedProperty on map not visible to getNestedProperty",
                "next value",
                PropertyUtils.getNestedProperty(bean, "unusuallyNamedProperty"));
        assertEquals(
            "Set nested property on map unexpected affected simple property",
            "new value",
            bean.getUnusuallyNamedProperty());
    }

    /**
     * This tests to see that it is possible to subclass PropertyUtilsBean
     * and change the behaviour of setNestedProperty/getNestedProperty when
     * dealing with objects that implement Map.
     */
    public void testMapExtensionCustom() throws Exception {
        final PropsFirstPropertyUtilsBean utilsBean = new PropsFirstPropertyUtilsBean();
        final ExtendMapBean bean = new ExtendMapBean();

        // hardly worth testing this, really :-)
        bean.setUnusuallyNamedProperty("bean value");
        assertEquals("Set property direct failed", "bean value", bean.getUnusuallyNamedProperty());

        // setSimpleProperty should affect the simple property
        utilsBean.setSimpleProperty(bean, "unusuallyNamedProperty", "new value");
        assertEquals("Set property on map failed (1)", "new value", bean.getUnusuallyNamedProperty());

        // setNestedProperty with setter should affect the simple property
        // getNestedProperty with getter should obtain the simple property
        utilsBean.setProperty(bean, "unusuallyNamedProperty", "next value");
        assertEquals("Set property on map failed (2)", "next value", bean.getUnusuallyNamedProperty());
        assertEquals("setNestedProperty on non-simple property failed",
                "next value",
                utilsBean.getNestedProperty(bean, "unusuallyNamedProperty"));

        // setting property without setter should update the map
        // getting property without setter should fetch from the map
        utilsBean.setProperty(bean, "mapProperty", "value1");
        assertEquals("setNestedProperty on non-simple property failed",
                "value1", utilsBean.getNestedProperty(bean, "mapProperty"));

        final HashMap<String, Object> myMap = new HashMap<String, Object>();
        myMap.put("thebean", bean);
        utilsBean.getNestedProperty(myMap, "thebean.mapitem");
        utilsBean.getNestedProperty(myMap, "thebean(mapitem)");
    }

    /**
     * Test {@link PropertyUtilsBean}'s invoke method throwing an IllegalArgumentException
     * and check that the "cause" has been properly initialized for JDK 1.4+
     * See BEANUTILS-266 for changes and reason for test
     */
    public void testExceptionFromInvoke() throws Exception {
        if (BeanUtilsTestCase.isPre14JVM()) {
            return;
        }
        try {
            PropertyUtils.setSimpleProperty(bean, "intProperty","XXX");
        } catch(final IllegalArgumentException t) {
            final Throwable cause = (Throwable)PropertyUtils.getProperty(t, "cause");
            assertNotNull("Cause not found", cause);
            assertTrue("Expected cause to be IllegalArgumentException, but was: " + cause.getClass(),
                    cause instanceof IllegalArgumentException);
            // JDK 1.6 doesn't have "argument type mismatch" message
            // assertEquals("Check error message", "argument type mismatch", cause.getMessage());
        } catch(final Throwable t) {
            fail("Expected IllegalArgumentException, but threw " + t);
        }
    }

    /**
     * Tests whether the default introspection mechanism can be replaced by a
     * custom BeanIntrospector.
     */
    public void testCustomIntrospection() {
        final PropertyDescriptor[] desc1 = PropertyUtils
                .getPropertyDescriptors(AlphaBean.class);
        PropertyDescriptor nameDescriptor = findNameDescriptor(desc1);
        assertNotNull("No write method", nameDescriptor.getWriteMethod());

        final BeanIntrospector bi = new BeanIntrospector() {
            // Only produce read-only property descriptors
            public void introspect(final IntrospectionContext icontext)
                    throws IntrospectionException {
                final Set<String> names = icontext.propertyNames();
                final PropertyDescriptor[] newDescs = new PropertyDescriptor[names
                        .size()];
                int idx = 0;
                for (final Iterator<String> it = names.iterator(); it.hasNext(); idx++) {
                    final String propName = it.next();
                    final PropertyDescriptor pd = icontext
                            .getPropertyDescriptor(propName);
                    newDescs[idx] = new PropertyDescriptor(pd.getName(),
                            pd.getReadMethod(), null);
                }
                icontext.addPropertyDescriptors(newDescs);
            }
        };
        PropertyUtils.clearDescriptors();
        PropertyUtils.addBeanIntrospector(bi);
        final PropertyDescriptor[] desc2 = PropertyUtils
                .getPropertyDescriptors(AlphaBean.class);
        assertEquals("Different number of properties", desc1.length,
                desc2.length);
        nameDescriptor = findNameDescriptor(desc2);
        assertNull("Got a write method", nameDescriptor.getWriteMethod());
        PropertyUtils.removeBeanIntrospector(bi);
    }

    /**
     * Finds the descriptor of the name property.
     *
     * @param desc the array with descriptors
     * @return the found descriptor or null
     */
    private static PropertyDescriptor findNameDescriptor(
            final PropertyDescriptor[] desc) {
        for (PropertyDescriptor element : desc) {
            if (element.getName().equals("name")) {
                return element;
            }
        }
        return null;
    }

    /**
     * Tests whether exceptions during custom introspection are handled.
     */
    public void testCustomIntrospectionEx() {
        final BeanIntrospector bi = new BeanIntrospector() {
            public void introspect(final IntrospectionContext icontext)
                    throws IntrospectionException {
                throw new IntrospectionException("TestException");
            }
        };
        PropertyUtils.clearDescriptors();
        PropertyUtils.addBeanIntrospector(bi);
        final PropertyDescriptor[] desc = PropertyUtils
                .getPropertyDescriptors(AlphaBean.class);
        assertNotNull("Introspection did not work", findNameDescriptor(desc));
        PropertyUtils.removeBeanIntrospector(bi);
    }

    /**
     * Tests whether a BeanIntrospector can be removed.
     */
    public void testRemoveBeanIntrospector() {
        assertTrue(
                "Wrong result",
                PropertyUtils
                        .removeBeanIntrospector(DefaultBeanIntrospector.INSTANCE));
        final PropertyDescriptor[] desc = PropertyUtils
                .getPropertyDescriptors(AlphaBean.class);
        assertEquals("Got descriptors", 0, desc.length);
        PropertyUtils.addBeanIntrospector(DefaultBeanIntrospector.INSTANCE);
    }

    /**
     * Tries to add a null BeanIntrospector.
     */
    public void testAddBeanIntrospectorNull() {
        try {
            PropertyUtils.addBeanIntrospector(null);
            fail("Could add null BeanIntrospector!");
        } catch (final IllegalArgumentException iex) {
            // ok
        }
    }

    /**
     * Tests whether a reset of the registered BeanIntrospectors can be performed.
     */
    public void testResetBeanIntrospectors() {
        assertTrue("Wrong result",
                PropertyUtils.removeBeanIntrospector(DefaultBeanIntrospector.INSTANCE));
        PropertyUtils.resetBeanIntrospectors();
        final PropertyDescriptor[] desc = PropertyUtils.getPropertyDescriptors(AlphaBean.class);
        assertTrue("Got no descriptors", desc.length > 0);
    }
}
