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

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test Case for the {@link BeanUtilsBean2}.
 *
 * @version $Revision$
 */
public class BeanUtils2TestCase extends BeanUtilsTestCase {

    // ---------------------------------------------------------- Constructors

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public BeanUtils2TestCase(String name) {
        super(name);
    }


    // -------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() {
        ConvertUtils.deregister();
        BeanUtilsBean.setInstance(new BeanUtilsBean2());
        setUpShared();
    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {
        return (new TestSuite(BeanUtils2TestCase.class));
    }

    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {
        bean = null;
    }

    /**
     * Test <code>copyProperty()</code> converting to a String.
     */
    public void testCopyPropertyConvertToString() {
        try {
            BeanUtils.copyProperty(bean, "stringProperty", testUtilDate);
        } catch (Throwable t) {
            fail("Threw " + t);
        }
        assertEquals("java.util.Date --> String", testStringDate, bean.getStringProperty());
    }

    /**
     * Test <code>copyProperty()</code> converting to a String.
     */
    public void testCopyPropertyConvertToStringArray() {
        try {
            bean.setStringArray(null);
            BeanUtils.copyProperty(bean, "stringArray", new java.util.Date[] {testUtilDate});
        } catch (Throwable t) {
            fail("Threw " + t);
        }
        assertEquals("java.util.Date[] --> String[] length", 1, bean.getStringArray().length);
        assertEquals("java.util.Date[] --> String[] value ", testStringDate, bean.getStringArray()[0]);
    }

    /**
     * Test <code>copyProperty()</code> converting to a String on indexed property
     */
    public void testCopyPropertyConvertToStringIndexed() {
        try {
            bean.setStringArray(new String[1]);
            BeanUtils.copyProperty(bean, "stringArray[0]", testUtilDate);
        } catch (Throwable t) {
            fail("Threw " + t);
        }
        assertEquals("java.util.Date[] --> String[] length", 1, bean.getStringArray().length);
        assertEquals("java.util.Date[] --> String[] value ", testStringDate, bean.getStringArray()[0]);
    }

    /**
     * Test <code>getArrayProperty()</code> converting to a String.
     */
    public void testGetArrayPropertyDate() {
        String[] value = null;
        try {
            bean.setDateArrayProperty(new java.util.Date[] {testUtilDate});
            value = BeanUtils.getArrayProperty(bean, "dateArrayProperty");
        } catch (Throwable t) {
            fail("Threw " + t);
        }
        assertEquals("java.util.Date[] --> String[] length", 1, value.length);
        assertEquals("java.util.Date[] --> String[] value ", testStringDate, value[0]);
    }

    /**
     * Test <code>getArrayProperty()</code> converting to a String.
     */
    public void testGetIndexedPropertyDate() {
        String value = null;
        try {
            bean.setDateArrayProperty(new java.util.Date[] {testUtilDate});
            value = BeanUtils.getIndexedProperty(bean, "dateArrayProperty[0]");
        } catch (Throwable t) {
            fail("Threw " + t);
        }
        assertEquals("java.util.Date[0] --> String", testStringDate, value);
    }

    /**
     * Test <code>getSimpleProperty()</code> converting to a String.
     */
    public void testGetSimplePropertyDate() {
        String value = null;
        try {
            bean.setDateProperty(testUtilDate);
            value = BeanUtils.getSimpleProperty(bean, "dateProperty");
        } catch (Throwable t) {
            fail("Threw " + t);
        }
        assertEquals("java.util.Date --> String", testStringDate, value);
    }

    /**
     * Test <code>setProperty()</code> converting to a String.
     */
    public void testSetPropertyConvertToString() {
        try {
            BeanUtils.setProperty(bean, "stringProperty", testUtilDate);
        } catch (Throwable t) {
            fail("Threw " + t);
        }
        assertEquals("java.util.Date --> String", testStringDate, bean.getStringProperty());
    }

    /**
     * Test <code>setProperty()</code> converting to a String array.
     */
    public void testSetPropertyConvertToStringArray() {
        try {
            bean.setStringArray(null);
            BeanUtils.setProperty(bean, "stringArray", new java.util.Date[] {testUtilDate});
        } catch (Throwable t) {
            fail("Threw " + t);
        }
        assertEquals("java.util.Date[] --> String[] length", 1, bean.getStringArray().length);
        assertEquals("java.util.Date[] --> String[] value ", testStringDate, bean.getStringArray()[0]);
    }

    /**
     * Test <code>setProperty()</code> converting to a String on indexed property
     */
    public void testSetPropertyConvertToStringIndexed() {
        try {
            bean.setStringArray(new String[1]);
            BeanUtils.setProperty(bean, "stringArray[0]", testUtilDate);
        } catch (Throwable t) {
            fail("Threw " + t);
        }
        assertEquals("java.util.Date --> String[]", testStringDate, bean.getStringArray()[0]);
    }

}
