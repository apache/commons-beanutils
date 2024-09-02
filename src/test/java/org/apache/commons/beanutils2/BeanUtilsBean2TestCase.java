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
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test Case for the {@link BeanUtilsBean2}.
 */
public class BeanUtilsBean2TestCase extends BeanUtilsBeanTestCase {

    /**
     * Sets up instance variables required by this test case.
     */
    @Override
    @BeforeEach
    public void setUp() {
        ConvertUtils.deregister();
        BeanUtilsBean.setInstance(new BeanUtilsBean2());
        setUpShared();
    }

    /**
     * Tear down instance variables required by this test case.
     */
    @Override
    @AfterEach
    public void tearDown() {
        bean = null;
    }

    /**
     * Test {@code copyProperty()} converting to a String.
     */
    @Override
    @Test
    public void testCopyPropertyConvertToString() throws Exception {
        BeanUtils.copyProperty(bean, "stringProperty", testUtilDate);
        assertEquals(testStringDate, bean.getStringProperty(), "java.util.Date --> String");
    }

    /**
     * Test {@code copyProperty()} converting to a String.
     */
    @Override
    @Test
    public void testCopyPropertyConvertToStringArray() {
        try {
            bean.setStringArray(null);
            BeanUtils.copyProperty(bean, "stringArray", new java.util.Date[] { testUtilDate });
        } catch (final Throwable t) {
            fail("Threw " + t);
        }
        assertEquals(1, bean.getStringArray().length, "java.util.Date[] --> String[] length");
        assertEquals(testStringDate, bean.getStringArray()[0], "java.util.Date[] --> String[] value ");
    }

    /**
     * Test {@code copyProperty()} converting to a String on indexed property
     */
    @Override
    @Test
    public void testCopyPropertyConvertToStringIndexed() {
        try {
            bean.setStringArray(new String[1]);
            BeanUtils.copyProperty(bean, "stringArray[0]", testUtilDate);
        } catch (final Throwable t) {
            fail("Threw " + t);
        }
        assertEquals(1, bean.getStringArray().length, "java.util.Date[] --> String[] length");
        assertEquals(testStringDate, bean.getStringArray()[0], "java.util.Date[] --> String[] value ");
    }

    /**
     * Test {@code getArrayProperty()} converting to a String.
     */
    @Override
    @Test
    public void testGetArrayPropertyDate() {
        String[] value = null;
        try {
            bean.setDateArrayProperty(new java.util.Date[] { testUtilDate });
            value = BeanUtils.getArrayProperty(bean, "dateArrayProperty");
        } catch (final Throwable t) {
            fail("Threw " + t);
        }
        assertEquals(1, value.length, "java.util.Date[] --> String[] length");
        assertEquals(testStringDate, value[0], "java.util.Date[] --> String[] value ");
    }

    /**
     * Test {@code getArrayProperty()} converting to a String.
     */
    @Override
    @Test
    public void testGetIndexedPropertyDate() {
        String value = null;
        try {
            bean.setDateArrayProperty(new java.util.Date[] { testUtilDate });
            value = BeanUtils.getIndexedProperty(bean, "dateArrayProperty[0]");
        } catch (final Throwable t) {
            fail("Threw " + t);
        }
        assertEquals(testStringDate, value, "java.util.Date[0] --> String");
    }

    /**
     * Test {@code getSimpleProperty()} converting to a String.
     */
    @Override
    @Test
    public void testGetSimplePropertyDate() {
        String value = null;
        try {
            bean.setDateProperty(testUtilDate);
            value = BeanUtils.getSimpleProperty(bean, "dateProperty");
        } catch (final Throwable t) {
            fail("Threw " + t);
        }
        assertEquals(testStringDate, value, "java.util.Date --> String");
    }

    /**
     * Test {@code setProperty()} converting to a String.
     */
    @Override
    @Test
    public void testSetPropertyConvertToString() {
        try {
            BeanUtils.setProperty(bean, "stringProperty", testUtilDate);
        } catch (final Throwable t) {
            fail("Threw " + t);
        }
        assertEquals(testStringDate, bean.getStringProperty(), "java.util.Date --> String");
    }

    /**
     * Test {@code setProperty()} converting to a String array.
     */
    @Override
    @Test
    public void testSetPropertyConvertToStringArray() {
        try {
            bean.setStringArray(null);
            BeanUtils.setProperty(bean, "stringArray", new java.util.Date[] { testUtilDate });
        } catch (final Throwable t) {
            fail("Threw " + t);
        }
        assertEquals(1, bean.getStringArray().length, "java.util.Date[] --> String[] length");
        assertEquals(testStringDate, bean.getStringArray()[0], "java.util.Date[] --> String[] value ");
    }

    /**
     * Test {@code setProperty()} converting to a String on indexed property
     */
    @Override
    @Test
    public void testSetPropertyConvertToStringIndexed() {
        try {
            bean.setStringArray(new String[1]);
            BeanUtils.setProperty(bean, "stringArray[0]", testUtilDate);
        } catch (final Throwable t) {
            fail("Threw " + t);
        }
        assertEquals(testStringDate, bean.getStringArray()[0], "java.util.Date --> String[]");
    }

}
