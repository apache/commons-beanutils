/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/test/org/apache/commons/beanutils/BasicDynaBeanTestCase.java,v 1.1 2002/01/06 06:01:08 craigmcc Exp $
 * $Revision: 1.1 $
 * $Date: 2002/01/06 06:01:08 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */


package org.apache.commons.beanutils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.beanutils.priv.PrivateBeanFactory;
import org.apache.commons.beanutils.priv.PrivateDirect;
import org.apache.commons.beanutils.priv.PrivateIndirect;


/**
 * <p>Test Case for the <code>BasicDynaBean</code> implementation class.
 * These tests were based on the ones in <code>PropertyUtilsTestCase</code>
 * because the two classes provide similar levels of functionality.</p>
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.1 $ $Date: 2002/01/06 06:01:08 $
 */

public class BasicDynaBeanTestCase extends TestCase {


    // ---------------------------------------------------- Instance Variables


    /**
     * The basic test bean for each test.
     */
    protected DynaBean bean = null;


    /**
     * The set of property names we expect to have returned when calling
     * <code>getPropertyDescriptors()</code>.  You should update this list
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
    public BasicDynaBeanTestCase(String name) {

        super(name);

    }


    // -------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() throws Exception {

        // Instantiate a new DynaBean instance
        DynaClass dynaClass = createDynaClass();
        bean = dynaClass.newInstance();

        // Initialize the DynaBean's property values (like TestBean)
        bean.set("booleanProperty", new Boolean(true));
        bean.set("booleanSecond", new Boolean(true));
        bean.set("doubleProperty", new Double(321.0));
        bean.set("floatProperty", new Float((float) 123.0));
        int intArray[] = { 0, 10, 20, 30, 40 };
        bean.set("intArray", intArray);
        int intIndexed[] = { 0, 10, 20, 30, 40 };
        bean.set("intIndexed", intIndexed);
        bean.set("intProperty", new Integer(123));
        List listIndexed = new ArrayList();
        listIndexed.add("String 0");
        listIndexed.add("String 1");
        listIndexed.add("String 2");
        listIndexed.add("String 3");
        listIndexed.add("String 4");
        bean.set("listIndexed", listIndexed);
        bean.set("longProperty", new Long((long) 321));
        HashMap mappedProperty = new HashMap();
        mappedProperty.put("First Key", "First Value");
        mappedProperty.put("Second Key", "Second Value");
        bean.set("mappedProperty", mappedProperty);
        HashMap mappedIntProperty = new HashMap();
        mappedIntProperty.put("One", new Integer(1));
        mappedIntProperty.put("Two", new Integer(2));
        bean.set("mappedIntProperty", mappedIntProperty);
        // Property "nullProperty" is not initialized, so it should return null
        bean.set("shortProperty", new Short((short) 987));
        String stringArray[] =
            { "String 0", "String 1", "String 2", "String 3", "String 4" };
        bean.set("stringArray", stringArray);
        String stringIndexed[] =
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
    public void tearDown() {

        bean = null;

    }



    // ------------------------------------------------ Individual Test Methods


    /**
     * Corner cases on getPropertyDescriptor invalid arguments.
     */
    public void testGetDescriptorArguments() {

        try {
            DynaProperty descriptor =
                bean.getDynaClass().getPropertyDescriptor("unknown");
            assertNull("Unknown property descriptor should be null",
                       descriptor);
        } catch (Throwable t) {
            fail("Threw " + t + " instead of returning null");
        }

        try {
            bean.getDynaClass().getPropertyDescriptor(null);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException");
        }

    }


    /**
     * Positive getPropertyDescriptor on property <code>booleanProperty</code>.
     */
    public void testGetDescriptorBoolean() {

        testGetDescriptorBase("booleanProperty", Boolean.TYPE);

    }


    /**
     * Positive getPropertyDescriptor on property <code>doubleProperty</code>.
     */
    public void testGetDescriptorDouble() {

        testGetDescriptorBase("doubleProperty", Double.TYPE);

    }


    /**
     * Positive getPropertyDescriptor on property <code>floatProperty</code>.
     */
    public void testGetDescriptorFloat() {

        testGetDescriptorBase("floatProperty", Float.TYPE);

    }


    /**
     * Positive getPropertyDescriptor on property <code>intProperty</code>.
     */
    public void testGetDescriptorInt() {

        testGetDescriptorBase("intProperty", Integer.TYPE);

    }


    /**
     * Positive getPropertyDescriptor on property <code>longProperty</code>.
     */
    public void testGetDescriptorLong() {

        testGetDescriptorBase("longProperty", Long.TYPE);

    }


    /**
     * Positive getPropertyDescriptor on property <code>booleanSecond</code>
     * that uses an "is" method as the getter.
     */
    public void testGetDescriptorSecond() {

        testGetDescriptorBase("booleanSecond", Boolean.TYPE);

    }


    /**
     * Positive getPropertyDescriptor on property <code>shortProperty</code>.
     */
    public void testGetDescriptorShort() {

        testGetDescriptorBase("shortProperty", Short.TYPE);

    }


    /**
     * Positive getPropertyDescriptor on property <code>stringProperty</code>.
     */
    public void testGetDescriptorString() {

        testGetDescriptorBase("stringProperty", String.class);

    }


    /**
     * Positive test for getPropertyDescriptors().  Each property name
     * listed in <code>properties</code> should be returned exactly once.
     */
    public void testGetDescriptors() {

        DynaProperty pd[] = bean.getDynaClass().getPropertyDescriptors();
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
     * Corner cases on getIndexedProperty invalid arguments.
     */
    public void testGetIndexedArguments() {

        try {
            bean.get("intArray", -1);
            fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            ; // Expected response
        } catch (Throwable t) {
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
            } catch (Throwable t) {
                fail("intArray " + i + " threw " + t);
            }

            try {
                value = bean.get("intIndexed", i);
                assertNotNull("intIndexed returned value " + i, value);
                assertTrue("intIndexed returned Integer " + i,
                           value instanceof Integer);
                assertEquals("intIndexed returned correct " + i, i * 10,
                             ((Integer) value).intValue());
            } catch (Throwable t) {
                fail("intIndexed " + i + " threw " + t);
            }

            try {
                value = bean.get("listIndexed", i);
                assertNotNull("listIndexed returned value " + i, value);
                assertTrue("list returned String " + i,
                           value instanceof String);
                assertEquals("listIndexed returned correct " + i,
                             "String " + i, (String) value);
            } catch (Throwable t) {
                fail("listIndexed " + i + " threw " + t);
            }

            try {
                value = bean.get("stringArray", i);
                assertNotNull("stringArray returned value " + i, value);
                assertTrue("stringArray returned String " + i,
                           value instanceof String);
                assertEquals("stringArray returned correct " + i,
                             "String " + i, (String) value);
            } catch (Throwable t) {
                fail("stringArray " + i + " threw " + t);
            }

            try {
                value = bean.get("stringIndexed", i);
                assertNotNull("stringIndexed returned value " + i, value);
                assertTrue("stringIndexed returned String " + i,
                           value instanceof String);
                assertEquals("stringIndexed returned correct " + i,
                             "String " + i, (String) value);
            } catch (Throwable t) {
                fail("stringIndexed " + i + " threw " + t);
            }

        }


    }


    /**
     * Corner cases on getMappedProperty invalid arguments.
     */
    public void testGetMappedArguments() {


        try {
            Object value = bean.get("mappedProperty", "unknown");
            assertNull("Should not return a value", value);
        } catch (Throwable t) {
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
        } catch (Throwable t) {
            fail("Finding first value threw " + t);
        }

        try {
            value = bean.get("mappedProperty", "Second Key");
            assertEquals("Can find second value", "Second Value", value);
        } catch (Throwable t) {
            fail("Finding second value threw " + t);
        }

        try {
            value = bean.get("mappedProperty", "Third Key");
            assertNull("Can not find third value", value);
        } catch (Throwable t) {
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
        } catch (IllegalArgumentException e) {
            ; // Expected response
        } catch (Throwable t) {
            fail("Threw " + t + " instead of IllegalArgumentException");
        }

    }


    /**
     * Test getSimpleProperty on a boolean property.
     */
    public void testGetSimpleBoolean() {

        try {
            Object value = bean.get("booleanProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Boolean));
            assertTrue("Got correct value",
                       ((Boolean) value).booleanValue() == true);
        } catch (Throwable e) {
            fail("Exception: " + e);
        }

    }


    /**
     * Test getSimpleProperty on a double property.
     */
    public void testGetSimpleDouble() {

        try {
            Object value = bean.get("doubleProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Double));
            assertEquals("Got correct value",
                         ((Double) value).doubleValue(),
                         (double) 321.0,
                         (double) 0.005);
        } catch (Throwable t) {
            fail("Exception: " + t);
        }

    }


    /**
     * Test getSimpleProperty on a float property.
     */
    public void testGetSimpleFloat() {

        try {
            Object value = bean.get("floatProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Float));
            assertEquals("Got correct value",
                         ((Float) value).floatValue(),
                         (float) 123.0,
                         (float) 0.005);
        } catch (Throwable t) {
            fail("Exception: " + t);
        }

    }


    /**
     * Test getSimpleProperty on an indexed property.
     */
    public void testGetSimpleIndexed() {

        int intIndexed[] = new int[0];
        Object value = null;
        try {
            value = bean.get("intIndexed");
            assertTrue
                ("Got correct type",
                 intIndexed.getClass().isAssignableFrom(value.getClass()));
        } catch (Throwable t) {
            fail("Exception: " + t);
        }

    }


    /**
     * Test getSimpleProperty on a int property.
     */
    public void testGetSimpleInt() {

        try {
            Object value = bean.get("intProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Integer));
            assertEquals("Got correct value",
                         ((Integer) value).intValue(),
                         (int) 123);
        } catch (Throwable t) {
            fail("Exception: " + t);
        }

    }


    /**
     * Test getSimpleProperty on a long property.
     */
    public void testGetSimpleLong() {

        try {
            Object value = bean.get("longProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Long));
            assertEquals("Got correct value",
                         ((Long) value).longValue(),
                         (long) 321);
        } catch (Throwable t) {
            fail("Exception: " + t);
        }

    }


    /**
     * Test getSimpleProperty on a short property.
     */
    public void testGetSimpleShort() {

        try {
            Object value = bean.get("shortProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof Short));
            assertEquals("Got correct value",
                         ((Short) value).shortValue(),
                         (short) 987);
        } catch (Throwable t) {
            fail("Exception: " + t);
        }

    }


    /**
     * Test getSimpleProperty on a String property.
     */
    public void testGetSimpleString() {

        try {
            Object value = bean.get("stringProperty");
            assertNotNull("Got a value", value);
            assertTrue("Got correct type", (value instanceof String));
            assertEquals("Got correct value",
                         (String) value,
                         "This is a string");
        } catch (Throwable t) {
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
        } catch (Throwable t) {
            fail("Exception: " + t);
        }


        try {
            assertTrue("Can not see unknown key",
                       !bean.contains("mappedProperty", "Unknown Key"));
        } catch (Throwable t) {
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
        } catch (Throwable t) {
            fail("Exception: " + t);
        }

        try {
            assertTrue("Can not see unknown key",
                       !bean.contains("mappedProperty", "Unknown Key"));
            bean.remove("mappedProperty", "Unknown Key");
            assertTrue("Can not see unknown key",
                       !bean.contains("mappedProperty", "Unknown Key"));
        } catch (Throwable t) {
            fail("Exception: " + t);
        }

    }


    /**
     * Corner cases on setIndexedProperty invalid arguments.
     */
    public void testSetIndexedArguments() {

        try {
            bean.set("intArray", -1, new Integer(0));
            fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            ; // Expected response
        } catch (Throwable t) {
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
            value = (Integer) bean.get("intArray", 0);
            assertNotNull("Returned new value 0", value);
            assertTrue("Returned Integer new value 0",
                       value instanceof Integer);
            assertEquals("Returned correct new value 0", 1,
                         ((Integer) value).intValue());
        } catch (Throwable t) {
            fail("Threw " + t);
        }

        try {
            bean.set("intIndexed", 1, new Integer(11));
            value = (Integer) bean.get("intIndexed", 1);
            assertNotNull("Returned new value 1", value);
            assertTrue("Returned Integer new value 1",
                       value instanceof Integer);
            assertEquals("Returned correct new value 1", 11,
                         ((Integer) value).intValue());
        } catch (Throwable t) {
            fail("Threw " + t);
        }

        try {
            bean.set("listIndexed", 2, "New Value 2");
            value = (String) bean.get("listIndexed", 2);
            assertNotNull("Returned new value 2", value);
            assertTrue("Returned String new value 2",
                       value instanceof String);
            assertEquals("Returned correct new value 2", "New Value 2",
                         (String) value);
        } catch (Throwable t) {
            fail("Threw " + t);
        }

        try {
            bean.set("stringArray", 3, "New Value 3");
            value = (String) bean.get("stringArray", 3);
            assertNotNull("Returned new value 3", value);
            assertTrue("Returned String new value 3",
                       value instanceof String);
            assertEquals("Returned correct new value 3", "New Value 3",
                         (String) value);
        } catch (Throwable t) {
            fail("Threw " + t);
        }

        try {
            bean.set("stringIndexed", 4, "New Value 4");
            value = (String) bean.get("stringIndexed", 4);
            assertNotNull("Returned new value 4", value);
            assertTrue("Returned String new value 4",
                       value instanceof String);
            assertEquals("Returned correct new value 4", "New Value 4",
                         (String) value);
        } catch (Throwable t) {
            fail("Threw " + t);
        }


    }


    /**
     * Positive and negative tests on setMappedProperty valid arguments.
     */
    public void testSetMappedValues() {

        Object value = null;

        try {
            bean.set("mappedProperty", "First Key", "New First Value");
            assertEquals("Can replace old value",
                         "New First Value",
                         (String) bean.get("mappedProperty", "First Key"));
        } catch (Throwable t) {
            fail("Finding fourth value threw " + t);
        }

        try {
            bean.set("mappedProperty", "Fourth Key", "Fourth Value");
            assertEquals("Can set new value",
                         "Fourth Value",
                         (String) bean.get("mappedProperty", "Fourth Key"));
        } catch (Throwable t) {
            fail("Finding fourth value threw " + t);
        }


    }


    /**
     * Test setSimpleProperty on a boolean property.
     */
    public void testSetSimpleBoolean() {

        try {
            boolean oldValue =
                ((Boolean) bean.get("booleanProperty")).booleanValue();
            boolean newValue = !oldValue;
            bean.set("booleanProperty", new Boolean(newValue));
            assertTrue("Matched new value",
                       newValue ==
                       ((Boolean) bean.get("booleanProperty")).booleanValue());
        } catch (Throwable e) {
            fail("Exception: " + e);
        }

    }


    /**
     * Test setSimpleProperty on a double property.
     */
    public void testSetSimpleDouble() {

        try {
            double oldValue =
                ((Double) bean.get("doubleProperty")).doubleValue();
            double newValue = oldValue + 1.0;
            bean.set("doubleProperty", new Double(newValue));
            assertEquals("Matched new value",
                         newValue,
                         ((Double) bean.get("doubleProperty")).doubleValue(),
                         (double) 0.005);
        } catch (Throwable e) {
            fail("Exception: " + e);
        }

    }


    /**
     * Test setSimpleProperty on a float property.
     */
    public void testSetSimpleFloat() {

        try {
            float oldValue =
                ((Float) bean.get("floatProperty")).floatValue();
            float newValue = oldValue + (float) 1.0;
            bean.set("floatProperty", new Float(newValue));
            assertEquals("Matched new value",
                         newValue,
                         ((Float) bean.get("floatProperty")).floatValue(),
                         (float) 0.005);
        } catch (Throwable e) {
            fail("Exception: " + e);
        }

    }


    /**
     * Negative test setSimpleProperty on an indexed property.
     */
    /*
    public void testSetSimpleIndexed() {

        try {
            BasicDynaBean.setSimpleProperty(bean,
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
    */


    /**
     * Test setSimpleProperty on a int property.
     */
    public void testSetSimpleInt() {

        try {
            int oldValue =
                ((Integer) bean.get("intProperty")).intValue();
            int newValue = oldValue + 1;
            bean.set("intProperty", new Integer(newValue));
            assertEquals("Matched new value",
                         newValue,
                         ((Integer) bean.get("intProperty")).intValue());
        } catch (Throwable e) {
            fail("Exception: " + e);
        }

    }


    /**
     * Test setSimpleProperty on a long property.
     */
    public void testSetSimpleLong() {

        try {
            long oldValue =
                ((Long) bean.get("longProperty")).longValue();
            long newValue = oldValue + 1;
            bean.set("longProperty", new Long(newValue));
            assertEquals("Matched new value",
                         newValue,
                         ((Long) bean.get("longProperty")).longValue());
        } catch (Throwable e) {
            fail("Exception: " + e);
        }

    }


    /**
     * Test setSimpleProperty on a short property.
     */
    public void testSetSimpleShort() {

        try {
            short oldValue =
                ((Short) bean.get("shortProperty")).shortValue();
            short newValue = (short) (oldValue + 1);
            bean.set("shortProperty", new Short(newValue));
            assertEquals("Matched new value",
                         newValue,
                         ((Short) bean.get("shortProperty")).shortValue());
        } catch (Throwable e) {
            fail("Exception: " + e);
        }

    }


    /**
     * Test setSimpleProperty on a String property.
     */
    public void testSetSimpleString() {

        try {
            String oldValue = (String) bean.get("stringProperty");
            String newValue = oldValue + " Extra Value";
            bean.set("stringProperty", newValue);
            assertEquals("Matched new value",
                         newValue,
                         (String) bean.get("stringProperty"));
        } catch (Throwable e) {
            fail("Exception: " + e);
        }

    }


    // ------------------------------------------------------ Protected Methods


    /**
     * Create and return a <code>DynaClass</code> instance for our test
     * <code>DynaBean</code>.
     */
    protected DynaClass createDynaClass() {

        int intArray[] = new int[0];
        String stringArray[] = new String[0];

        DynaClass dynaClass = new BasicDynaClass
            ("TestDynaClass", null,
             new DynaProperty[] {
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
    protected void testGetDescriptorBase(String name, Class type) {

        try {
            DynaProperty descriptor =
                bean.getDynaClass().getPropertyDescriptor(name);
            assertNotNull("Got descriptor", descriptor);
            assertEquals("Got correct type", type, descriptor.getType());
        } catch (Throwable t) {
            fail("Threw an exception: " + t);
        }

    }


}
