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


import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.priv.PrivateBeanFactory;
import org.apache.commons.beanutils.priv.PrivateDirect;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;


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
 * @author Craig R. McClanahan
 * @author Jan Sorensen
 * @version $Revision$ $Date$
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
    public PropertyUtilsTestCase(String name) {

        super(name);

    }


    // -------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() {

        bean = new TestBean();
        beanPackageSubclass = new TestBeanPackageSubclass();
        beanPrivate = PrivateBeanFactory.create();
        beanPrivateSubclass = PrivateBeanFactory.createSubclass();
        beanPublicSubclass = new TestBeanPublicSubclass();

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
    public void tearDown() {

        bean = null;
        beanPackageSubclass = null;
        beanPrivate = null;
        beanPrivateSubclass = null;
        beanPublicSubclass = null;

    }



    // ------------------------------------------------ Individual Test Methods


    /**
     * Test copyProperties() when the origin is a a <code>Map</code>.
     */
    public void testCopyPropertiesMap() {

        Map map = new HashMap();
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
        } catch (Throwable t) {
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
     * Test the describe() method.
     */
    public void testDescribe() {

        Map map = null;
        try {
            map = PropertyUtils.describe(bean);
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
                     Boolean.TRUE,
                     (Boolean) map.get("booleanProperty"));
        assertEquals("Value of 'doubleProperty'",
                     new Double(321.0),
                     (Double) map.get("doubleProperty"));
        assertEquals("Value of 'floatProperty'",
                     new Float((float) 123.0),
                     (Float) map.get("floatProperty"));
        assertEquals("Value of 'intProperty'",
                     new Integer(123),
                     (Integer) map.get("intProperty"));
        assertEquals("Value of 'longProperty'",
                     new Long(321),
                     (Long) map.get("longProperty"));
        assertEquals("Value of 'shortProperty'",
                     new Short((short) 987),
                     (Short) map.get("shortProperty"));
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
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.getPropertyDescriptor(bean, null);
            fail("Should throw IllegalArgumentException 2");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
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

	PropertyDescriptor pd =
	    PropertyUtils.getPropertyDescriptor(bean, "invalidBoolean");
	assertNotNull("invalidBoolean is a property", pd);
	assertNotNull("invalidBoolean has a getter method",
		      pd.getReadMethod());
	assertNull("invalidBoolean has no write method",
		   pd.getWriteMethod());
	assertTrue("invalidBoolean getter method is isInvalidBoolean",
		   "isInvalidBoolean".equals(pd.getReadMethod().getName()));

    }


    /**
     * Positive getPropertyDescriptor on property <code>longProperty</code>.
     */
    public void testGetDescriptorLong() {

        testGetDescriptorBase("longProperty", "getLongProperty",
                "setLongProperty");

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

        PropertyDescriptor pd[] =
                PropertyUtils.getPropertyDescriptors(bean);
        assertNotNull("Got descriptors", pd);
        int count[] = new int[properties.length];
        for (int i = 0; i < pd.length; i++) {
            String name = pd[i].getName();
            for (int j = 0; j < properties.length; j++) {
                if (name.equals(properties[j]))
                    count[j]++;
            }
        }
        for (int j = 0; j < properties.length; j++) {
            if (count[j] < 0)
                fail("Missing property " + properties[j]);
            else if (count[j] > 1)
                fail("Duplicate property " + properties[j]);
        }

    }


    /**
     * Corner cases on getPropertyDescriptors invalid arguments.
     */
    public void testGetDescriptorsArguments() {

        try {
            PropertyUtils.getPropertyDescriptors(null);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
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
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.getIndexedProperty(bean, null, 0);
            fail("Should throw IllegalArgumentException 2");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

        // Use index expression

        try {
            PropertyUtils.getIndexedProperty(null,
                    "intArray[0]");
            fail("Should throw IllegalArgumentException 3");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 3");
        }

        try {
            PropertyUtils.getIndexedProperty(bean, "[0]");
            fail("Should throw NoSuchMethodException 4");
        } catch (NoSuchMethodException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of NoSuchMethodException 4");
        }

        try {
            PropertyUtils.getIndexedProperty(bean, "intArray");
            fail("Should throw IllegalArgumentException 5");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 5");
        }

        // Use explicit index argument

        try {
            PropertyUtils.getIndexedProperty(null, "intIndexed", 0);
            fail("Should throw IllegalArgumentException 1");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.getIndexedProperty(bean, null, 0);
            fail("Should throw IllegalArgumentException 2");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

        // Use index expression

        try {
            PropertyUtils.getIndexedProperty(null,
                    "intIndexed[0]");
            fail("Should throw IllegalArgumentException 3");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 3");
        }

        try {
            PropertyUtils.getIndexedProperty(bean, "[0]");
            fail("Should throw NoSuchMethodException 4");
        } catch (NoSuchMethodException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of NoSuchMethodException 4");
        }

        try {
            PropertyUtils.getIndexedProperty(bean, "intIndexed");
            fail("Should throw IllegalArgumentException 5");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
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
            } catch (Throwable t) {
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
            } catch (Throwable t) {
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
            } catch (Throwable t) {
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
            } catch (Throwable t) {
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
            } catch (Throwable t) {
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
            } catch (Throwable t) {
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
            } catch (Throwable t) {
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
            } catch (Throwable t) {
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
            } catch (Throwable t) {
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
            } catch (Throwable t) {
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
            } catch (Throwable t) {
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
            } catch (Throwable t) {
                fail("stringIndexed " + i + " threw " + t);
            }

        }

        // Index out of bounds tests

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "dupProperty", -1);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException t) {
            ; // Expected results
        } catch (Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "dupProperty", 5);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException t) {
            ; // Expected results
        } catch (Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "intArray", -1);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException t) {
            ; // Expected results
        } catch (Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "intArray", 5);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException t) {
            ; // Expected results
        } catch (Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "intIndexed", -1);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException t) {
            ; // Expected results
        } catch (Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "intIndexed", 5);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException t) {
            ; // Expected results
        } catch (Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "listIndexed", -1);
            fail("Should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException t) {
            ; // Expected results
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "listIndexed", 5);
            fail("Should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException t) {
            ; // Expected results
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "stringArray", -1);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException t) {
            ; // Expected results
        } catch (Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "stringArray", 5);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException t) {
            ; // Expected results
        } catch (Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "stringIndexed", -1);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException t) {
            ; // Expected results
        } catch (Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            value =
                    PropertyUtils.getIndexedProperty(bean,
                            "stringIndexed", 5);
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException t) {
            ; // Expected results
        } catch (Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
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
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.getMappedProperty(bean, null, "First Key");
            fail("Should throw IllegalArgumentException 2");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

        try {
            PropertyUtils.getMappedProperty(bean, "mappedProperty", null);
            fail("Should throw IllegalArgumentException 3");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 3");
        }

        // Use key expression

        try {
            PropertyUtils.getMappedProperty(null,
                    "mappedProperty(First Key)");
            fail("Should throw IllegalArgumentException 4");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 4");
        }

        try {
            PropertyUtils.getMappedProperty(bean, "(Second Key)");
            fail("Should throw IllegalArgumentException 5");
        } catch (NoSuchMethodException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of NoSuchMethodException 5");
        }

        try {
            PropertyUtils.getMappedProperty(bean, "mappedProperty");
            fail("Should throw IllegalArgumentException 6");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 6");
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
        } catch (Exception e) {
            fail("Thew exception: " + e);
        }
        try {
            assertEquals("Can retrieve via getNestedProperty",
                         "Special Value",
                         PropertyUtils.getNestedProperty
                         (bean, "mappedProperty(key.with.a.dot)"));
        } catch (Exception e) {
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
        } catch (Exception e) {
            fail("Thew exception: " + e);
        }

        try 
        {
            assertEquals("Can't retrieved nested with mapped property",
                         "Mapped Value",
                         PropertyUtils.getNestedProperty(
                             bean,"mappedNested.value(Mapped Key)"));
        } catch (Exception e) 
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
        } catch (Exception e) {
            fail("Thew exception: " + e);
        }
        try {
            assertEquals("Can retrieve via getNestedProperty",
                         "Special Value",
                         PropertyUtils.getNestedProperty
                         (bean, "mappedProperty(key/with/a/slash)"));
        } catch (Exception e) {
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
        } catch (Exception e) {
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
        } catch (Throwable t) {
            fail("Finding first value threw " + t);
        }

        try {
            value = PropertyUtils.getMappedProperty(bean, "mappedProperty",
                    "Second Key");
            assertEquals("Can find second value", "Second Value", value);
        } catch (Throwable t) {
            fail("Finding second value threw " + t);
        }

        try {
            value = PropertyUtils.getMappedProperty(bean, "mappedProperty",
                    "Third Key");
            assertNull("Can not find third value", value);
        } catch (Throwable t) {
            fail("Finding third value threw " + t);
        }

        // Use key expression with parentheses

        try {
            value =
                    PropertyUtils.getMappedProperty(bean,
                            "mappedProperty(First Key)");
            assertEquals("Can find first value", "First Value", value);
        } catch (Throwable t) {
            fail("Finding first value threw " + t);
        }

        try {
            value =
                    PropertyUtils.getMappedProperty(bean,
                            "mappedProperty(Second Key)");
            assertEquals("Can find second value", "Second Value", value);
        } catch (Throwable t) {
            fail("Finding second value threw " + t);
        }

        try {
            value =
                    PropertyUtils.getMappedProperty(bean,
                            "mappedProperty(Third Key)");
            assertNull("Can not find third value", value);
        } catch (Throwable t) {
            fail("Finding third value threw " + t);
        }

        // Use key expression with dotted syntax

        try {
            value =
                    PropertyUtils.getNestedProperty(bean,
                            "mapProperty.First Key");
            assertEquals("Can find first value", "First Value", value);
        } catch (Throwable t) {
            fail("Finding first value threw " + t);
        }

        try {
            value =
                    PropertyUtils.getNestedProperty(bean,
                            "mapProperty.Second Key");
            assertEquals("Can find second value", "Second Value", value);
        } catch (Throwable t) {
            fail("Finding second value threw " + t);
        }

        try {
            value =
                    PropertyUtils.getNestedProperty(bean,
                            "mapProperty.Third Key");
            assertNull("Can not find third value", value);
        } catch (Throwable t) {
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
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.getNestedProperty(bean, null);
            fail("Should throw IllegalArgumentException 2");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

    }


    /**
     * Test getNestedProperty on a boolean property.
     */
    public void testGetNestedBoolean() {

        try {
            Object value =
                    PropertyUtils.getNestedProperty
                    (bean, "nested.booleanProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Boolean));
            assertTrue("Got correct value",
                    ((Boolean) value).booleanValue() ==
                    bean.getNested().getBooleanProperty());
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getNestedProperty on a double property.
     */
    public void testGetNestedDouble() {

        try {
            Object value =
                    PropertyUtils.getNestedProperty
                    (bean, "nested.doubleProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Double));
            assertEquals("Got correct value",
                    ((Double) value).doubleValue(),
                    bean.getNested().getDoubleProperty(),
                    0.005);
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getNestedProperty on a float property.
     */
    public void testGetNestedFloat() {

        try {
            Object value =
                    PropertyUtils.getNestedProperty
                    (bean, "nested.floatProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Float));
            assertEquals("Got correct value",
                    ((Float) value).floatValue(),
                    bean.getNested().getFloatProperty(),
                    (float) 0.005);
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getNestedProperty on an int property.
     */
    public void testGetNestedInt() {

        try {
            Object value =
                    PropertyUtils.getNestedProperty
                    (bean, "nested.intProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Integer));
            assertEquals("Got correct value",
                    ((Integer) value).intValue(),
                    bean.getNested().getIntProperty());
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getNestedProperty on a long property.
     */
    public void testGetNestedLong() {

        try {
            Object value =
                    PropertyUtils.getNestedProperty
                    (bean, "nested.longProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Long));
            assertEquals("Got correct value",
                    ((Long) value).longValue(),
                    bean.getNested().getLongProperty());
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getNestedProperty on a read-only String property.
     */
    public void testGetNestedReadOnly() {

        try {
            Object value =
                    PropertyUtils.getNestedProperty
                    (bean, "nested.readOnlyProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof String));
            assertEquals("Got correct value",
                    (String) value,
                    bean.getReadOnlyProperty());
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getNestedProperty on a short property.
     */
    public void testGetNestedShort() {

        try {
            Object value =
                    PropertyUtils.getNestedProperty
                    (bean, "nested.shortProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Short));
            assertEquals("Got correct value",
                    ((Short) value).shortValue(),
                    bean.getNested().getShortProperty());
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getNestedProperty on a String property.
     */
    public void testGetNestedString() {

        try {
            Object value =
                    PropertyUtils.getNestedProperty
                    (bean, "nested.stringProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof String));
            assertEquals("Got correct value",
                    ((String) value),
                    bean.getNested().getStringProperty());
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
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
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            ; // Correct result for this test
        }

    }

    /** 
     * When a bean has a null property which is reference by the standard access language,
     * this should throw a NestedNullException.
     */
    public void testThrowNestedNull() throws Exception {
        NestedTestBean nestedBean = new NestedTestBean("base");
        // don't init!
        
        try {
            NestedTestBean value = (NestedTestBean) PropertyUtils.getProperty(
                                nestedBean,
                                "simpleBeanProperty.indexedProperty[0]");
            fail("NestedNullException not thrown");
        } catch (NestedNullException e) {
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
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            ; // Correct result for this test
        }

    }


    /**
     * Test getPropertyType() on all kinds of properties.
     */
    public void testGetPropertyType() {

        Class clazz = null;
        int intArray[] = new int[0];
        String stringArray[] = new String[0];

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

        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }

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
        PropertyDescriptor pd[] =
                PropertyUtils.getPropertyDescriptors(beanPrivate);
        int n = -1;
        for (int i = 0; i < pd.length; i++) {
            if ("foo".equals(pd[i].getName())) {
                n = i;
                break;
            }
        }
        assertTrue("Found foo descriptor", n >= 0);
        Method reader = pd[n].getReadMethod();
        assertNotNull("Found foo read method", reader);
        Object value = null;
        try {
            value = reader.invoke(beanPrivate, new Class[0]);
            fail("Foo reader did throw IllegalAccessException");
        } catch (IllegalAccessException e) {
            ; // Expected result for this test
        } catch (Throwable t) {
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
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.getSimpleProperty(bean, null);
            fail("Should throw IllegalArgumentException 2");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

    }


    /**
     * Test getSimpleProperty on a boolean property.
     */
    public void testGetSimpleBoolean() {

        try {
            Object value =
                    PropertyUtils.getSimpleProperty(bean,
                            "booleanProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Boolean));
            assertTrue("Got correct value",
                    ((Boolean) value).booleanValue() ==
                    bean.getBooleanProperty());
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getSimpleProperty on a double property.
     */
    public void testGetSimpleDouble() {

        try {
            Object value =
                    PropertyUtils.getSimpleProperty(bean,
                            "doubleProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Double));
            assertEquals("Got correct value",
                    ((Double) value).doubleValue(),
                    bean.getDoubleProperty(),
                    (double) 0.005);
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getSimpleProperty on a float property.
     */
    public void testGetSimpleFloat() {

        try {
            Object value =
                    PropertyUtils.getSimpleProperty(bean,
                            "floatProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Float));
            assertEquals("Got correct value",
                    ((Float) value).floatValue(),
                    bean.getFloatProperty(),
                    (float) 0.005);
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Negative test getSimpleProperty on an indexed property.
     */
    public void testGetSimpleIndexed() {

        Object value = null;
        try {
            value = PropertyUtils.getSimpleProperty(bean,
                    "intIndexed[0]");
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            ; // Correct result for this test
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getSimpleProperty on an int property.
     */
    public void testGetSimpleInt() {

        try {
            Object value =
                    PropertyUtils.getSimpleProperty(bean,
                            "intProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Integer));
            assertEquals("Got correct value",
                    ((Integer) value).intValue(),
                    bean.getIntProperty());
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getSimpleProperty on a long property.
     */
    public void testGetSimpleLong() {

        try {
            Object value =
                    PropertyUtils.getSimpleProperty(bean,
                            "longProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Long));
            assertEquals("Got correct value",
                    ((Long) value).longValue(),
                    bean.getLongProperty());
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Negative test getSimpleProperty on a nested property.
     */
    public void testGetSimpleNested() {

        Object value = null;
        try {
            value = PropertyUtils.getSimpleProperty(bean,
                    "nested.stringProperty");
            fail("Should have thrown IllegaArgumentException");
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            ; // Correct result for this test
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getSimpleProperty on a read-only String property.
     */
    public void testGetSimpleReadOnly() {

        try {
            Object value =
                    PropertyUtils.getSimpleProperty(bean,
                            "readOnlyProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof String));
            assertEquals("Got correct value",
                    (String) value,
                    bean.getReadOnlyProperty());
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getSimpleProperty on a short property.
     */
    public void testGetSimpleShort() {

        try {
            Object value =
                    PropertyUtils.getSimpleProperty(bean,
                            "shortProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Short));
            assertEquals("Got correct value",
                    ((Short) value).shortValue(),
                    bean.getShortProperty());
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test getSimpleProperty on a String property.
     */
    public void testGetSimpleString() {

        try {
            Object value =
                    PropertyUtils.getSimpleProperty(bean,
                            "stringProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof String));
            assertEquals("Got correct value",
                    (String) value,
                    bean.getStringProperty());
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
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
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            ; // Correct result for this test
        }

    }


    /**
     * Test getSimpleProperty on a write-only String property.
     */
    public void testGetSimpleWriteOnly() {

        try {
            PropertyUtils.getSimpleProperty(bean, "writeOnlyProperty");
            fail("Should have thrown NoSuchMethodException");
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            ; // Correct result for this test
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
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.setIndexedProperty(bean, null, 0,
                    new Integer(1));
            fail("Should throw IllegalArgumentException 2");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

        // Use index expression

        try {
            PropertyUtils.setIndexedProperty(null,
                    "intArray[0]",
                    new Integer(1));
            fail("Should throw IllegalArgumentException 3");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 3");
        }

        try {
            PropertyUtils.setIndexedProperty(bean, "[0]",
                    new Integer(1));
            fail("Should throw NoSuchMethodException 4");
        } catch (NoSuchMethodException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of NoSuchMethodException 4");
        }

        try {
            PropertyUtils.setIndexedProperty(bean, "intArray",
                    new Integer(1));
            fail("Should throw IllegalArgumentException 5");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 5");
        }

        // Use explicit index argument

        try {
            PropertyUtils.setIndexedProperty(null, "intIndexed", 0,
                    new Integer(1));
            fail("Should throw IllegalArgumentException 1");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.setIndexedProperty(bean, null, 0,
                    new Integer(1));
            fail("Should throw IllegalArgumentException 2");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

        // Use index expression

        try {
            PropertyUtils.setIndexedProperty(null,
                    "intIndexed[0]",
                    new Integer(1));
            fail("Should throw IllegalArgumentException 3");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 3");
        }

        try {
            PropertyUtils.setIndexedProperty(bean, "[0]",
                    new Integer(1));
            fail("Should throw NoSuchMethodException 4");
        } catch (NoSuchMethodException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of NoSuchMethodException 4");
        }

        try {
            PropertyUtils.setIndexedProperty(bean, "intIndexed",
                    new Integer(1));
            fail("Should throw IllegalArgumentException 5");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 5");
        }

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
        } catch (Throwable t) {
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
        } catch (Throwable t) {
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
        } catch (Throwable t) {
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
        } catch (Throwable t) {
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
        } catch (Throwable t) {
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
        } catch (Throwable t) {
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
        } catch (Throwable t) {
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
        } catch (Throwable t) {
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
        } catch (Throwable t) {
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
        } catch (Throwable t) {
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
        } catch (Throwable t) {
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
        } catch (Throwable t) {
            fail("Threw " + t);
        }

        // Index out of bounds tests

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "dupProperty", -1,
                    "New -1");
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException t) {
            ; // Expected results
        } catch (Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "dupProperty", 5,
                    "New 5");
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException t) {
            ; // Expected results
        } catch (Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "intArray", -1,
                    new Integer(0));
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException t) {
            ; // Expected results
        } catch (Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "intArray", 5,
                    new Integer(0));
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException t) {
            ; // Expected results
        } catch (Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "intIndexed", -1,
                    new Integer(0));
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException t) {
            ; // Expected results
        } catch (Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "intIndexed", 5,
                    new Integer(0));
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException t) {
            ; // Expected results
        } catch (Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "listIndexed", 5,
                    "New String");
            fail("Should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException t) {
            ; // Expected results
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "listIndexed", -1,
                    "New String");
            fail("Should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException t) {
            ; // Expected results
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "stringArray", -1,
                    "New String");
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException t) {
            ; // Expected results
        } catch (Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "stringArray", 5,
                    "New String");
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException t) {
            ; // Expected results
        } catch (Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "stringIndexed", -1,
                    "New String");
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException t) {
            ; // Expected results
        } catch (Throwable t) {
            fail("Threw " + t + " instead of ArrayIndexOutOfBoundsException");
        }

        try {
            PropertyUtils.setIndexedProperty(bean,
                    "stringIndexed", 5,
                    "New String");
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException t) {
            ; // Expected results
        } catch (Throwable t) {
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
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.setMappedProperty(bean, null, "First Key",
                    "First Value");
            fail("Should throw IllegalArgumentException 2");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

        try {
            PropertyUtils.setMappedProperty(bean, "mappedProperty", null,
                    "First Value");
            fail("Should throw IllegalArgumentException 3");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 3");
        }

        // Use key expression

        try {
            PropertyUtils.setMappedProperty(null,
                    "mappedProperty(First Key)",
                    "First Value");
            fail("Should throw IllegalArgumentException 4");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 4");
        }

        try {
            PropertyUtils.setMappedProperty(bean, "(Second Key)",
                    "Second Value");
            fail("Should throw IllegalArgumentException 5");
        } catch (NoSuchMethodException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of NoSuchMethodException 5");
        }

        try {
            PropertyUtils.setMappedProperty(bean, "mappedProperty",
                    "Third Value");
            fail("Should throw IllegalArgumentException 6");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 6");
        }

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
        } catch (Throwable t) {
            fail("Finding fourth value threw " + t);
        }

        try {
            PropertyUtils.setMappedProperty(bean, "mappedProperty",
                    "Fourth Key", "Fourth Value");
        } catch (Throwable t) {
            fail("Setting fourth value threw " + t);
        }

        try {
            value = PropertyUtils.getMappedProperty(bean, "mappedProperty",
                    "Fourth Key");
            assertEquals("Can find fourth value", "Fourth Value", value);
        } catch (Throwable t) {
            fail("Finding fourth value threw " + t);
        }

        // Use key expression with parentheses

        try {
            value =
                    PropertyUtils.getMappedProperty(bean,
                            "mappedProperty(Fifth Key)");
            assertNull("Can not find fifth value", value);
        } catch (Throwable t) {
            fail("Finding fifth value threw " + t);
        }

        try {
            PropertyUtils.setMappedProperty(bean,
                    "mappedProperty(Fifth Key)",
                    "Fifth Value");
        } catch (Throwable t) {
            fail("Setting fifth value threw " + t);
        }

        try {
            value =
                    PropertyUtils.getMappedProperty(bean,
                            "mappedProperty(Fifth Key)");
            assertEquals("Can find fifth value", "Fifth Value", value);
        } catch (Throwable t) {
            fail("Finding fifth value threw " + t);
        }

        // Use key expression with dotted expression

        try {
            value =
                    PropertyUtils.getNestedProperty(bean,
                            "mapProperty.Sixth Key");
            assertNull("Can not find sixth value", value);
        } catch (Throwable t) {
            fail("Finding fifth value threw " + t);
        }

        try {
            PropertyUtils.setNestedProperty(bean,
                    "mapProperty.Sixth Key",
                    "Sixth Value");
        } catch (Throwable t) {
            fail("Setting sixth value threw " + t);
        }

        try {
            value =
                    PropertyUtils.getNestedProperty(bean,
                            "mapProperty.Sixth Key");
            assertEquals("Can find sixth value", "Sixth Value", value);
        } catch (Throwable t) {
            fail("Finding sixth value threw " + t);
        }

    }


    /**
     * Corner cases on setNestedProperty invalid arguments.
     */
    public void testSetNestedArguments() {

        try {
            PropertyUtils.setNestedProperty(null, "stringProperty", "");
            fail("Should throw IllegalArgumentException 1");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.setNestedProperty(bean, null, "");
            fail("Should throw IllegalArgumentException 2");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

    }


    /**
     * Test setNextedProperty on a boolean property.
     */
    public void testSetNestedBoolean() {

        try {
            boolean oldValue = bean.getNested().getBooleanProperty();
            boolean newValue = !oldValue;
            PropertyUtils.setNestedProperty(bean,
                    "nested.booleanProperty",
                    new Boolean(newValue));
            assertTrue("Matched new value",
                    newValue ==
                    bean.getNested().getBooleanProperty());
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setNestedProperty on a double property.
     */
    public void testSetNestedDouble() {

        try {
            double oldValue = bean.getNested().getDoubleProperty();
            double newValue = oldValue + 1.0;
            PropertyUtils.setNestedProperty(bean,
                    "nested.doubleProperty",
                    new Double(newValue));
            assertEquals("Matched new value",
                    newValue,
                    bean.getNested().getDoubleProperty(),
                    0.005);
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setNestedProperty on a float property.
     */
    public void testSetNestedFloat() {

        try {
            float oldValue = bean.getNested().getFloatProperty();
            float newValue = oldValue + (float) 1.0;
            PropertyUtils.setNestedProperty(bean,
                    "nested.floatProperty",
                    new Float(newValue));
            assertEquals("Matched new value",
                    newValue,
                    bean.getNested().getFloatProperty(),
                    (float) 0.005);
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setNestedProperty on a int property.
     */
    public void testSetNestedInt() {

        try {
            int oldValue = bean.getNested().getIntProperty();
            int newValue = oldValue + 1;
            PropertyUtils.setNestedProperty(bean,
                    "nested.intProperty",
                    new Integer(newValue));
            assertEquals("Matched new value",
                    newValue,
                    bean.getNested().getIntProperty());
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setNestedProperty on a long property.
     */
    public void testSetNestedLong() {

        try {
            long oldValue = bean.getNested().getLongProperty();
            long newValue = oldValue + 1;
            PropertyUtils.setNestedProperty(bean,
                    "nested.longProperty",
                    new Long(newValue));
            assertEquals("Matched new value",
                    newValue,
                    bean.getNested().getLongProperty());
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setNestedProperty on a read-only String property.
     */
    public void testSetNestedReadOnly() {

        try {
            String oldValue = bean.getNested().getWriteOnlyPropertyValue();
            String newValue = oldValue + " Extra Value";
            PropertyUtils.setNestedProperty(bean,
                    "nested.readOnlyProperty",
                    newValue);
            fail("Should have thrown NoSuchMethodException");
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            ; // Correct result for this test
        }

    }


    /**
     * Test setNestedProperty on a short property.
     */
    public void testSetNestedShort() {

        try {
            short oldValue = bean.getNested().getShortProperty();
            short newValue = oldValue;
            newValue++;
            PropertyUtils.setNestedProperty(bean,
                    "nested.shortProperty",
                    new Short(newValue));
            assertEquals("Matched new value",
                    newValue,
                    bean.getNested().getShortProperty());
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setNestedProperty on a String property.
     */
    public void testSetNestedString() {

        try {
            String oldValue = bean.getNested().getStringProperty();
            String newValue = oldValue + " Extra Value";
            PropertyUtils.setNestedProperty(bean,
                    "nested.stringProperty",
                    newValue);
            assertEquals("Matched new value",
                    newValue,
                    bean.getNested().getStringProperty());
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setNestedProperty on an unknown property name.
     */
    public void testSetNestedUnknown() {

        try {
            String newValue = "New String Value";
            PropertyUtils.setNestedProperty(bean,
                    "nested.unknown",
                    newValue);
            fail("Should have thrown NoSuchMethodException");
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            ; // Correct result for this test
        }

    }


    /**
     * Test setNestedProperty on a write-only String property.
     */
    public void testSetNestedWriteOnly() {

        try {
            String oldValue = bean.getNested().getWriteOnlyPropertyValue();
            String newValue = oldValue + " Extra Value";
            PropertyUtils.setNestedProperty(bean,
                    "nested.writeOnlyProperty",
                    newValue);
            assertEquals("Matched new value",
                    newValue,
                    bean.getNested().getWriteOnlyPropertyValue());
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
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
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 1");
        }

        try {
            PropertyUtils.setSimpleProperty(bean, null, "");
            fail("Should throw IllegalArgumentException 2");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException 2");
        }

    }


    /**
     * Test setSimpleProperty on a boolean property.
     */
    public void testSetSimpleBoolean() {

        try {
            boolean oldValue = bean.getBooleanProperty();
            boolean newValue = !oldValue;
            PropertyUtils.setSimpleProperty(bean,
                    "booleanProperty",
                    new Boolean(newValue));
            assertTrue("Matched new value",
                    newValue ==
                    bean.getBooleanProperty());
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setSimpleProperty on a double property.
     */
    public void testSetSimpleDouble() {

        try {
            double oldValue = bean.getDoubleProperty();
            double newValue = oldValue + 1.0;
            PropertyUtils.setSimpleProperty(bean,
                    "doubleProperty",
                    new Double(newValue));
            assertEquals("Matched new value",
                    newValue,
                    bean.getDoubleProperty(),
                    0.005);
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setSimpleProperty on a float property.
     */
    public void testSetSimpleFloat() {

        try {
            float oldValue = bean.getFloatProperty();
            float newValue = oldValue + (float) 1.0;
            PropertyUtils.setSimpleProperty(bean,
                    "floatProperty",
                    new Float(newValue));
            assertEquals("Matched new value",
                    newValue,
                    bean.getFloatProperty(),
                    (float) 0.005);
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
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
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            ; // Correct result for this test
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setSimpleProperty on a int property.
     */
    public void testSetSimpleInt() {

        try {
            int oldValue = bean.getIntProperty();
            int newValue = oldValue + 1;
            PropertyUtils.setSimpleProperty(bean,
                    "intProperty",
                    new Integer(newValue));
            assertEquals("Matched new value",
                    newValue,
                    bean.getIntProperty());
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setSimpleProperty on a long property.
     */
    public void testSetSimpleLong() {

        try {
            long oldValue = bean.getLongProperty();
            long newValue = oldValue + 1;
            PropertyUtils.setSimpleProperty(bean,
                    "longProperty",
                    new Long(newValue));
            assertEquals("Matched new value",
                    newValue,
                    bean.getLongProperty());
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
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
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            ; // Correct result for this test
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setSimpleProperty on a read-only String property.
     */
    public void testSetSimpleReadOnly() {

        try {
            String oldValue = bean.getWriteOnlyPropertyValue();
            String newValue = oldValue + " Extra Value";
            PropertyUtils.setSimpleProperty(bean,
                    "readOnlyProperty",
                    newValue);
            fail("Should have thrown NoSuchMethodException");
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            ; // Correct result for this test
        }

    }


    /**
     * Test setSimpleProperty on a short property.
     */
    public void testSetSimpleShort() {

        try {
            short oldValue = bean.getShortProperty();
            short newValue = oldValue;
            newValue++;
            PropertyUtils.setSimpleProperty(bean,
                    "shortProperty",
                    new Short(newValue));
            assertEquals("Matched new value",
                    newValue,
                    bean.getShortProperty());
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setSimpleProperty on a String property.
     */
    public void testSetSimpleString() {

        try {
            String oldValue = bean.getStringProperty();
            String newValue = oldValue + " Extra Value";
            PropertyUtils.setSimpleProperty(bean,
                    "stringProperty",
                    newValue);
            assertEquals("Matched new value",
                    newValue,
                    bean.getStringProperty());
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            fail("NoSuchMethodException");
        }

    }


    /**
     * Test setSimpleProperty on an unknown property name.
     */
    public void testSetSimpleUnknown() {

        try {
            String newValue = "New String Value";
            PropertyUtils.setSimpleProperty(bean,
                    "unknown",
                    newValue);
            fail("Should have thrown NoSuchMethodException");
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            ; // Correct result for this test
        }

    }


    /**
     * Test setSimpleProperty on a write-only String property.
     */
    public void testSetSimpleWriteOnly() {

        try {
            String oldValue = bean.getWriteOnlyPropertyValue();
            String newValue = oldValue + " Extra Value";
            PropertyUtils.setSimpleProperty(bean,
                    "writeOnlyProperty",
                    newValue);
            assertEquals("Matched new value",
                    newValue,
                    bean.getWriteOnlyPropertyValue());
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
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
    protected void testGetDescriptorBase(String name, String read,
                                         String write) {

        try {
            PropertyDescriptor pd =
                    PropertyUtils.getPropertyDescriptor(bean, name);
            if ((read != null) || (write != null)) {
                assertNotNull("Got descriptor", pd);
            } else {
                assertNull("Got descriptor", pd);
                return;
            }
            Method rm = pd.getReadMethod();
            if (read != null) {
                assertNotNull("Got read method", rm);
                assertEquals("Got correct read method",
                        rm.getName(), read);
            } else {
                assertNull("Got read method", rm);
            }
            Method wm = pd.getWriteMethod();
            if (write != null) {
                assertNotNull("Got write method", wm);
                assertEquals("Got correct write method",
                        wm.getName(), write);
            } else {
                assertNull("Got write method", wm);
            }
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
        } catch (InvocationTargetException e) {
            fail("InvocationTargetException");
        } catch (NoSuchMethodException e) {
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
    protected void testGetReadMethod(Object bean, String properties[],
                                     String className) {

        PropertyDescriptor pd[] =
                PropertyUtils.getPropertyDescriptors(bean);
        for (int i = 0; i < properties.length; i++) {

            // Identify the property descriptor for this property
            if (properties[i].equals("intIndexed"))
                continue;
            if (properties[i].equals("stringIndexed"))
                continue;
            if (properties[i].equals("writeOnlyProperty"))
                continue;
            int n = -1;
            for (int j = 0; j < pd.length; j++) {
                if (properties[i].equals(pd[j].getName())) {
                    n = j;
                    break;
                }
            }
            assertTrue("PropertyDescriptor for " + properties[i],
                    n >= 0);

            // Locate an accessible property reader method for it
            Method reader = PropertyUtils.getReadMethod(pd[n]);
            assertNotNull("Reader for " + properties[i],
                    reader);
            Class clazz = reader.getDeclaringClass();
            assertNotNull("Declaring class for " + properties[i],
                    clazz);
            assertEquals("Correct declaring class for " + properties[i],
                    clazz.getName(),
                    className);

            // Actually call the reader method we received
            try {
                reader.invoke(bean, new Class[0]);
            } catch (Throwable t) {
                fail("Call for " + properties[i] + ": " + t);
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
    protected void testGetWriteMethod(Object bean, String properties[],
                                      String className) {


        PropertyDescriptor pd[] =
                PropertyUtils.getPropertyDescriptors(bean);
        for (int i = 0; i < properties.length; i++) {

            // Identify the property descriptor for this property
            if (properties[i].equals("intIndexed"))
                continue;
            if (properties[i].equals("listIndexed"))
                continue;
            if (properties[i].equals("nested"))
                continue; // This property is read only
            if (properties[i].equals("readOnlyProperty"))
                continue;
            if (properties[i].equals("stringIndexed"))
                continue;
            int n = -1;
            for (int j = 0; j < pd.length; j++) {
                if (properties[i].equals(pd[j].getName())) {
                    n = j;
                    break;
                }
            }
            assertTrue("PropertyDescriptor for " + properties[i],
                    n >= 0);

            // Locate an accessible property reader method for it
            Method writer = PropertyUtils.getWriteMethod(pd[n]);
            assertNotNull("Writer for " + properties[i],
                    writer);
            Class clazz = writer.getDeclaringClass();
            assertNotNull("Declaring class for " + properties[i],
                    clazz);
            assertEquals("Correct declaring class for " + properties[i],
                    clazz.getName(),
                    className);

        }

    }

    public void testNestedWithIndex() throws Exception
    {
        NestedTestBean nestedBean = new NestedTestBean("base");
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
        BeanWithInnerBean bean = new BeanWithInnerBean();
        
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

        SonOfAlphaBean bean = new SonOfAlphaBean("Roger");
        
        String out = (String) PropertyUtils.getProperty(bean, "name");
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
        BetaBean bean = new BetaBean("Cedric");
        
        // test standard no getter
        bean.setNoGetterProperty("Sigma");
        assertEquals("BetaBean test failed", "Sigma", bean.getSecret());
        
        assertNotNull("Descriptor is null", PropertyUtils.getPropertyDescriptor(bean, "noGetterProperty"));
        
        BeanUtils.setProperty(bean, "noGetterProperty",  "Omega");
        assertEquals("Cannot set no-getter property", "Omega", bean.getSecret());
        
        // test mapped no getter descriptor
        MappedPropertyDescriptor descriptor 
            = new MappedPropertyDescriptor("noGetterMappedProperty", BetaBean.class);
        
        assertNotNull("Map Descriptor is null", PropertyUtils.getPropertyDescriptor(bean, "noGetterMappedProperty"));
        
        PropertyUtils.setMappedProperty(bean, "noGetterMappedProperty",  "Epsilon", "Epsilon");
        assertEquals("Cannot set mapped no-getter property", "MAP:Epsilon", bean.getSecret());
    }
    
    /** 
     * This tests to see that classes that implement Map can have 
     * their standard properties set.
     */
    public void testSetMapExtension() throws Exception {
        ExtendMapBean bean = new ExtendMapBean();
        
        bean.setUnusuallyNamedProperty("bean value");
        assertEquals("Set property direct failed", "bean value", bean.getUnusuallyNamedProperty());
        
        PropertyUtils.setSimpleProperty(bean, "unusuallyNamedProperty", "new value");
        assertEquals("Set property on map failed (1)", "new value", bean.getUnusuallyNamedProperty());
        
        PropertyUtils.setProperty(bean, "unusuallyNamedProperty", "next value");
        assertEquals("Set property on map failed (2)", "next value", bean.getUnusuallyNamedProperty());
    }
}
