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
package org.apache.commons.beanutils2.bugs;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils2.BeanUtils;
import org.apache.commons.beanutils2.PropertyUtils;
import org.apache.commons.beanutils2.WrapDynaBean;
import org.apache.commons.beanutils2.bugs.other.Jira61BeanFactory;
import org.apache.commons.beanutils2.bugs.other.Jira61BeanFactory.TestBean;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test case for Jira issue# BEANUTILS-61.
 *
 * <p>
 * {@link WrapDynaBean} is a secial case for the PropertyUtils's isReadable() and isWriteable() methods - since the bean being wrapped may have read-only or
 * write-only properties (unlike regular DynaBeans.
 *
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-61">https://issues.apache.org/jira/browse/BEANUTILS-61</a>
 */
public class Jira61TestCase {

    private static Jira61BeanFactory.TestBean testBean;
    private static WrapDynaBean wrapDynaBean;

    @BeforeAll
    public static void beforeAll() throws Exception {
        testBean = Jira61BeanFactory.createBean();
        PropertyUtils.getPropertyDescriptor(testBean, "mappedReadOnly");
        PropertyUtils.getPropertyDescriptor(testBean, "mappedWriteOnly");
        wrapDynaBean = new WrapDynaBean(testBean);
    }

    /**
     * Test {@link BeanUtils#copyProperties(Object, Object)} to a read-only WrapDynaBean property.
     */
    @Test
    public void testIssue_BEANUTILS_61_BeanUtils_copyProperties_from_WrapDynaBean() throws Exception {
        final String value = "ORIG TARGET VALUE";
        final TestBean targetBean = Jira61BeanFactory.createBean();
        targetBean.setSimpleWriteOnly(value);
        BeanUtils.copyProperties(targetBean, wrapDynaBean);

        assertEquals(value, targetBean.getSimpleReadOnly());
    }

    /**
     * Test {@link BeanUtils#copyProperties(Object, Object)} to a read-only WrapDynaBean property.
     */
    @Test
    public void testIssue_BEANUTILS_61_BeanUtils_copyProperties_to_WrapDynaBean() throws Exception {
        final String value = "copied simpleReadOnly";
        final Map<String, Object> source = new HashMap<>();
        source.put("simpleReadOnly", value);
        BeanUtils.copyProperties(wrapDynaBean, source);

        assertNotEquals(value, testBean.getSimpleReadOnly());
    }

    /**
     * Test {@link PropertyUtils#copyProperties(Object, Object)} to a read-only WrapDynaBean property.
     */
    @Test
    public void testIssue_BEANUTILS_61_PropertyUtils_copyProperties_from_WrapDynaBean() throws Exception {
        final String value = "ORIG TARGET VALUE";
        final TestBean targetBean = Jira61BeanFactory.createBean();
        targetBean.setSimpleWriteOnly(value);
        PropertyUtils.copyProperties(targetBean, wrapDynaBean);

        assertEquals(value, targetBean.getSimpleReadOnly());
    }

    /**
     * Test {@link PropertyUtils#copyProperties(Object, Object)} to a read-only WrapDynaBean property.
     */
    @Test
    public void testIssue_BEANUTILS_61_PropertyUtils_copyProperties_to_WrapDynaBean() throws Exception {
        final String expected = "copied simpleReadOnly";
        final Map<String, Object> source = new HashMap<>();
        source.put("simpleReadOnly", expected);
        PropertyUtils.copyProperties(wrapDynaBean, source);

        assertNotEquals(expected, testBean.getSimpleReadOnly());
    }

    /**
     * Test {@link PropertyUtils#getProperty(Object, String)} for simple properties.
     */
    @Test
    public void testIssue_BEANUTILS_61_PropertyUtils_getProperty() {
        assertAll(() -> assertEquals(testBean.getSimpleReadOnly(), PropertyUtils.getProperty(wrapDynaBean, "simpleReadOnly")),
                () -> assertThrows(IllegalArgumentException.class, () -> {
                    PropertyUtils.getProperty(wrapDynaBean, "simpleWriteOnly");
                }));
    }

    /**
     * Test {@link PropertyUtils#getProperty(Object, String)} for indexed properties.
     */
    @Test
    public void testIssue_BEANUTILS_61_PropertyUtils_getProperty_Indexed() {
        assertAll(() -> assertEquals(testBean.getIndexedReadOnly(0), PropertyUtils.getProperty(wrapDynaBean, "indexedReadOnly[0]")),
                () -> assertThrows(IllegalArgumentException.class, () -> {
                    PropertyUtils.getProperty(wrapDynaBean, "indexedWriteOnly[0]");
                }));
    }

    /**
     * Test {@link PropertyUtils#getProperty(Object, String)} for mapped properties.
     */
    @Test
    public void testIssue_BEANUTILS_61_PropertyUtils_getProperty_Mapped() {
        assertAll(() -> assertEquals(testBean.getMappedReadOnly("foo-key"), PropertyUtils.getProperty(wrapDynaBean, "mappedReadOnly(foo-key)")),
                () -> assertThrows(IllegalArgumentException.class, () -> {
                    PropertyUtils.getProperty(wrapDynaBean, "mappedWriteOnly(foo-key)");
                }));
    }

    /**
     * Test {@link PropertyUtils#isReadable(Object, String)} for simple properties.
     */
    @Test
    public void testIssue_BEANUTILS_61_PropertyUtils_isReadable() {
        assertAll(() -> assertTrue(PropertyUtils.isReadable(wrapDynaBean, "simpleReadOnly")),
                () -> assertFalse(PropertyUtils.isReadable(wrapDynaBean, "simpleWriteOnly")));
    }

    /**
     * Test {@link PropertyUtils#isReadable(Object, String)} for indexed properties.
     */
    @Test
    public void testIssue_BEANUTILS_61_PropertyUtils_isReadable_Indexed() {
        assertAll(() -> assertTrue(PropertyUtils.isReadable(wrapDynaBean, "indexedReadOnly")),
                () -> assertFalse(PropertyUtils.isReadable(wrapDynaBean, "indexedWriteOnly")));
    }

    /**
     * Test {@link PropertyUtils#isReadable(Object, String)} for mapped properties.
     */
    @Test
    public void testIssue_BEANUTILS_61_PropertyUtils_isReadable_Mapped() {
        assertAll(() -> assertTrue(PropertyUtils.isReadable(wrapDynaBean, "mappedReadOnly")),
                () -> assertFalse(PropertyUtils.isReadable(wrapDynaBean, "mappedWriteOnly")));
    }

    /**
     * Test {@link PropertyUtils#isWriteable(Object, String)} for simple properties.
     */
    @Test
    public void testIssue_BEANUTILS_61_PropertyUtils_isWriteable() {
        assertAll(() -> assertFalse(PropertyUtils.isWriteable(wrapDynaBean, "simpleReadOnly")),
                () -> assertTrue(PropertyUtils.isWriteable(wrapDynaBean, "simpleWriteOnly")));
    }

    /**
     * Test {@link PropertyUtils#isWriteable(Object, String)} for indexed properties.
     */
    @Test
    public void testIssue_BEANUTILS_61_PropertyUtils_isWriteable_Indexed() {
        assertAll(() -> assertFalse(PropertyUtils.isWriteable(wrapDynaBean, "indexedReadOnly")),
                () -> assertTrue(PropertyUtils.isWriteable(wrapDynaBean, "indexedWriteOnly")));
    }

    /**
     * Test {@link PropertyUtils#isWriteable(Object, String)} for mapped properties.
     */
    @Test
    public void testIssue_BEANUTILS_61_PropertyUtils_isWriteable_Mapped() {
        assertAll(() -> assertFalse(PropertyUtils.isWriteable(wrapDynaBean, "mappedReadOnly")),
                () -> assertTrue(PropertyUtils.isWriteable(wrapDynaBean, "mappedWriteOnly")));
    }

    /**
     * Test {@link PropertyUtils#setProperty(Object, String, Object)} for simple properties.
     */
    @Test
    public void testIssue_BEANUTILS_61_PropertyUtils_setProperty() {
        assertAll(() -> assertThrows(IllegalArgumentException.class, () -> {
            PropertyUtils.setProperty(wrapDynaBean, "simpleReadOnly", "READONLY-SIMPLE-BAR");
        }), () -> {
            PropertyUtils.setProperty(wrapDynaBean, "simpleWriteOnly", "SIMPLE-BAR");
            assertEquals("SIMPLE-BAR", testBean.getSimpleReadOnly());
        });
    }

    /**
     * Test {@link PropertyUtils#setProperty(Object, String, Object)} for indexed properties.
     */
    @Test
    public void testIssue_BEANUTILS_61_PropertyUtils_setProperty_Indexed() {
        assertAll(() -> assertThrows(IllegalArgumentException.class, () -> {
            PropertyUtils.setProperty(wrapDynaBean, "indexedReadOnly[0]", "READONLY-INDEXED-BAR");
        }), () -> {
            PropertyUtils.setProperty(wrapDynaBean, "indexedWriteOnly[0]", "INDEXED-BAR");
            assertEquals("INDEXED-BAR", testBean.getIndexedReadOnly(0));
        });
    }

    /**
     * Test {@link PropertyUtils#setProperty(Object, String, Object)} for mapped properties.
     */
    @Test
    public void testIssue_BEANUTILS_61_PropertyUtils_setProperty_Mapped() {
        assertAll(() -> assertThrows(IllegalArgumentException.class, () -> {
            PropertyUtils.setProperty(wrapDynaBean, "mappedReadOnly(foo-key)", "READONLY-MAPPED-BAR");
        }), () -> {
            PropertyUtils.setProperty(wrapDynaBean, "mappedWriteOnly(foo-key)", "MAPPED-BAR");
            assertEquals("MAPPED-BAR", testBean.getMappedReadOnly("foo-key"));
        });
    }
}
