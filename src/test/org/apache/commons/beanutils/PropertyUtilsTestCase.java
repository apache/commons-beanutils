/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/test/org/apache/commons/beanutils/PropertyUtilsTestCase.java,v 1.1 2001/03/27 05:33:07 craigmcc Exp $
 * $Revision: 1.1 $
 * $Date: 2001/03/27 05:33:07 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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


import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * <p>Test Case for the PropertyUtils class.  The majority of these tests use
 * instances of the TestBean class, so be sure to update the tests if you
 * change the characteristics of that class.</p>
 *
 * <p>So far, this test case has tests for the following methods of the
 * <code>PropertyUtils</code> class:</p>
 * <ul>
 * <li>getPropertyDescriptor(Object,String)</li>
 * <li>getPropertyDescriptors(Object)</li>
 * <li>getSimpleProperty(Object,String)</li>
 * <li>setSimpleProperty(Object,String,Object)</li>
 * </ul>
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.1 $ $Date: 2001/03/27 05:33:07 $
 */

public class PropertyUtilsTestCase extends TestCase {


    // ---------------------------------------------------- Instance Variables


    /**
     * The test bean for each test.
     */
    protected TestBean bean = null;


    /**
     * The set of property names we expect to have returned when calling
     * <code>getPropertyDescriptors()</code>.  You should update this list
     * when new properties are added to TestBean.
     */
    protected final static String[] properties = {
        "booleanProperty",
        "doubleProperty",
        "floatProperty",
        "intArray",
        "intIndexed",
        "intProperty",
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

    }



    // ------------------------------------------------ Individual Test Methods


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
     * Test getSimpleProperty on a boolean property.
     */
    public void testGetSimpleBoolean() {

        try {
            Object value =
                PropertyUtils.getSimpleProperty(bean,
                                                "booleanProperty");
            assertNotNull("Got a value", value);
            assert("Got correct type", (value instanceof Boolean));
            assertEquals("Got correct value",
                         ((Boolean) value).booleanValue(),
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
            assert("Got correct type", (value instanceof Double));
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
            assert("Got correct type", (value instanceof Float));
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
            assert("Got correct type", (value instanceof Integer));
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
            assert("Got correct type", (value instanceof Long));
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
            assert("Got correct type", (value instanceof String));
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
            assert("Got correct type", (value instanceof Short));
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
            assert("Got correct type", (value instanceof String));
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
            Object value =
                PropertyUtils.getSimpleProperty(bean,
                                                "unknown");
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
            Object value =
                PropertyUtils.getSimpleProperty(bean,
                                                "writeOnlyProperty");
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
     * Test setSimpleProperty on a boolean property.
     */
    public void testSetSimpleBoolean() {

        try {
            boolean oldValue = bean.getBooleanProperty();
            boolean newValue = !oldValue;
            PropertyUtils.setSimpleProperty(bean,
                                            "booleanProperty",
                                            new Boolean(newValue));
            assertEquals("Matched new value",
                         newValue,
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
            short newValue = oldValue; newValue++;
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


}
