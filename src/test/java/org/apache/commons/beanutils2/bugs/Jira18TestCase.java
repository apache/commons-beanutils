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

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.apache.commons.beanutils2.PropertyUtils;
import org.apache.commons.beanutils2.bugs.other.Jira18BeanFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test case for Jira issue# BEANUTILS-18.
 *
 * <p>
 * This test case demonstrates the issue.
 *
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-18">https://issues.apache.org/jira/browse/BEANUTILS-18</a>
 */
public class Jira18TestCase {

    private static Object bean;

    @BeforeAll
    public static void beforeAll() {
        bean = Jira18BeanFactory.createBean();
    }

    /**
     * Test {@link PropertyUtils#getProperty(Object, String)} for simple properties.
     */
    @Test
    public void testIssue_BEANUTILS_18_PropertyUtils_getProperty() {
        assertThrows(NoSuchMethodException.class, () -> PropertyUtils.getProperty(bean, "simple"));
    }

    /**
     * Test {@link PropertyUtils#getProperty(Object, String)} for indexed properties.
     */
    @Test
    public void testIssue_BEANUTILS_18_PropertyUtils_getProperty_Indexed() {
        assertThrows(NoSuchMethodException.class, () -> PropertyUtils.getProperty(bean, "indexed[0]"));
    }

    /**
     * Test {@link PropertyUtils#getProperty(Object, String)} for mapped properties.
     */
    @Test
    public void testIssue_BEANUTILS_18_PropertyUtils_getProperty_Mapped() {
        assertThrows(NoSuchMethodException.class, () -> PropertyUtils.getProperty(bean, "mapped(foo-key)"));
    }

    /**
     * Test {@link PropertyUtils#isReadable(Object, String)} for simple properties.
     */
    @Test
    public void testIssue_BEANUTILS_18_PropertyUtils_isReadable() {
        assertFalse(PropertyUtils.isReadable(bean, "simple"));
    }

    /**
     * Test {@link PropertyUtils#isReadable(Object, String)} for indexed properties.
     */
    @Test
    public void testIssue_BEANUTILS_18_PropertyUtils_isReadable_Indexed() {
        assertFalse(PropertyUtils.isReadable(bean, "indexed"));
    }

    /**
     * Test {@link PropertyUtils#isReadable(Object, String)} for Mapped properties.
     */
    @Test
    public void testIssue_BEANUTILS_18_PropertyUtils_isReadable_Mapped() {
        assertFalse(PropertyUtils.isReadable(bean, "mapped"));
    }

    /**
     * Test {@link PropertyUtils#isWriteable(Object, String)} for simple properties.
     */
    @Test
    public void testIssue_BEANUTILS_18_PropertyUtils_isWriteable() {
        assertFalse(PropertyUtils.isWriteable(bean, "simple"));
    }

    /**
     * Test {@link PropertyUtils#isWriteable(Object, String)} for indexed properties.
     */
    @Test
    public void testIssue_BEANUTILS_18_PropertyUtils_isWriteable_Indexed() {
        assertFalse(PropertyUtils.isWriteable(bean, "indexed"));
    }

    /**
     * Test {@link PropertyUtils#isWriteable(Object, String)} for Mapped properties.
     */
    @Test
    public void testIssue_BEANUTILS_18_PropertyUtils_isWriteable_Mapped() {
        assertFalse(PropertyUtils.isWriteable(bean, "mapped"));
    }

    /**
     * Test {@link PropertyUtils#setProperty(Object, String, Object)} for simple properties.
     */
    @Test
    public void testIssue_BEANUTILS_18_PropertyUtils_setProperty() {
        assertThrows(NoSuchMethodException.class, () -> PropertyUtils.setProperty(bean, "simple", "BAR"));
    }

    /**
     * Test {@link PropertyUtils#setProperty(Object, String, Object)} for indexed properties.
     */
    @Test
    public void testIssue_BEANUTILS_18_PropertyUtils_setProperty_Indexed() {
        assertThrows(NoSuchMethodException.class, () -> PropertyUtils.setProperty(bean, "indexed[0]", "BAR"));
    }

    /**
     * Test {@link PropertyUtils#setProperty(Object, String, Object)} for mapped properties.
     */
    @Test
    public void testIssue_BEANUTILS_18_PropertyUtils_setProperty_Mapped() {
        assertThrows(NoSuchMethodException.class, () -> PropertyUtils.setProperty(bean, "mapped(foo-key)", "BAR"));
    }
}
