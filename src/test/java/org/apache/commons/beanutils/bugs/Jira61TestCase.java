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
package org.apache.commons.beanutils.bugs;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.WrapDynaBean;
import org.apache.commons.beanutils.bugs.other.Jira61BeanFactory;
import org.apache.commons.beanutils.bugs.other.Jira61BeanFactory.TestBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Test case for Jira issue# BEANUTILS-61.
 *
 * <p>
 * {@link WrapDynaBean} is a secial case for the PropertyUtils's
 * isReadable() and isWriteable() methods - since the bean being
 * wrapped may have read-only or write-only properties (unlike
 * regular DynaBeans.
 *
 * @version $Id$
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-61">https://issues.apache.org/jira/browse/BEANUTILS-61</a>
 */
public class Jira61TestCase extends TestCase {

    private final Log log = LogFactory.getLog(Jira61TestCase.class);
    private Jira61BeanFactory.TestBean testBean;
    private WrapDynaBean wrapDynaBean;

    /**
     * Create a test case with the specified name.
     *
     * @param name The name of the test
     */
    public Jira61TestCase(final String name) {
        super(name);
    }

    /**
     * Run the Test.
     *
     * @param args Arguments
     */
    public static void main(final String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Create a test suite for this test.
     *
     * @return a test suite
     */
    public static Test suite() {
        return (new TestSuite(Jira61TestCase.class));
    }

    /**
     * Set up.
     *
     * @throws java.lang.Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        testBean = Jira61BeanFactory.createBean();
        PropertyUtils.getPropertyDescriptor(testBean, "mappedReadOnly");
        PropertyUtils.getPropertyDescriptor(testBean, "mappedWriteOnly");
        wrapDynaBean = new WrapDynaBean(testBean);
    }

    /**
     * Tear Down.
     *
     * @throws java.lang.Exception
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test {@link PropertyUtils#isReadable(Object, String)}
     * for simple properties.
     */
    public void testIssue_BEANUTILS_61_PropertyUtils_isReadable() {
        boolean result = false;

        try {
            result = PropertyUtils.isReadable(wrapDynaBean, "simpleReadOnly");
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("simpleReadOnly Threw exception: " + t);
        }
        assertTrue("PropertyUtils.isReadable(bean, \"simpleReadOnly\") returned " + result, result);

        try {
            result = PropertyUtils.isReadable(wrapDynaBean, "simpleWriteOnly");
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("simpleWriteOnly Threw exception: " + t);
        }
        assertFalse("PropertyUtils.isReadable(bean, \"simpleWriteOnly\") returned " + result, result);
    }

    /**
     * Test {@link PropertyUtils#isWriteable(Object, String)}
     * for simple properties.
     */
    public void testIssue_BEANUTILS_61_PropertyUtils_isWriteable() {
        boolean result = false;

        try {
            result = PropertyUtils.isWriteable(wrapDynaBean, "simpleReadOnly");
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("simpleReadOnly Threw exception: " + t);
        }
        assertFalse("PropertyUtils.isWriteable(bean, \"simpleReadOnly\") returned " + result, result);

        try {
            result = PropertyUtils.isWriteable(wrapDynaBean, "simpleWriteOnly");
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("simpleWriteOnly Threw exception: " + t);
        }
        assertTrue("PropertyUtils.isWriteable(bean, \"simpleWriteOnly\") returned " + result, result);
    }

    /**
     * Test {@link PropertyUtils#isReadable(Object, String)}
     * for indexed properties.
     */
    public void testIssue_BEANUTILS_61_PropertyUtils_isReadable_Indexed() {
        boolean result = false;

        try {
            result = PropertyUtils.isReadable(wrapDynaBean, "indexedReadOnly");
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("indexedReadOnly Threw exception: " + t);
        }
        assertTrue("PropertyUtils.isReadable(bean, \"indexedReadOnly\") returned " + result, result);

        try {
            result = PropertyUtils.isReadable(wrapDynaBean, "indexedWriteOnly");
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("indexedWriteOnly Threw exception: " + t);
        }
        assertFalse("PropertyUtils.isReadable(bean, \"indexedWriteOnly\") returned " + result, result);
    }

    /**
     * Test {@link PropertyUtils#isReadable(Object, String)}
     * for mapped properties.
     */
    public void testIssue_BEANUTILS_61_PropertyUtils_isReadable_Mapped() {
        boolean result = false;

        try {
            result = PropertyUtils.isReadable(wrapDynaBean, "mappedReadOnly");
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("mappedReadOnly Threw exception: " + t);
        }
        assertTrue("PropertyUtils.isReadable(bean, \"mappedReadOnly\") returned " + result, result);

        try {
            result = PropertyUtils.isReadable(wrapDynaBean, "mappedWriteOnly");
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("mappedWriteOnly Threw exception: " + t);
        }
        assertFalse("PropertyUtils.isReadable(bean, \"mappedWriteOnly\") returned " + result, result);
    }

    /**
     * Test {@link PropertyUtils#isWriteable(Object, String)}
     * for indexed properties.
     */
    public void testIssue_BEANUTILS_61_PropertyUtils_isWriteable_Indexed() {
        boolean result = false;

        try {
            result = PropertyUtils.isWriteable(wrapDynaBean, "indexedReadOnly");
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("indexedReadOnly Threw exception: " + t);
        }
        assertFalse("PropertyUtils.isWriteable(bean, \"indexedReadOnly\") returned " + result, result);

        try {
            result = PropertyUtils.isWriteable(wrapDynaBean, "indexedWriteOnly");
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("indexedWriteOnly Threw exception: " + t);
        }
        assertTrue("PropertyUtils.isWriteable(bean, \"indexedWriteOnly\") returned " + result, result);
    }

    /**
     * Test {@link PropertyUtils#isWriteable(Object, String)}
     * for mapped properties.
     */
    public void testIssue_BEANUTILS_61_PropertyUtils_isWriteable_Mapped() {
        boolean result = false;

        try {
            result = PropertyUtils.isWriteable(wrapDynaBean, "mappedReadOnly");
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("mappedReadOnly Threw exception: " + t);
        }
        assertFalse("PropertyUtils.isWriteable(bean, \"mappedReadOnly\") returned " + result, result);

        try {
            result = PropertyUtils.isWriteable(wrapDynaBean, "mappedWriteOnly");
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("mappedWriteOnly Threw exception: " + t);
        }
        assertTrue("PropertyUtils.isWriteable(bean, \"mappedWriteOnly\") returned " + result, result);
    }

    /**
     * Test {@link PropertyUtils#getProperty(Object, String)}
     * for simple properties.
     */
    public void testIssue_BEANUTILS_61_PropertyUtils_getProperty() {
        boolean threwIllegalArgumentException = false;
        Object result = null;
        try {
            result = PropertyUtils.getProperty(wrapDynaBean, "simpleReadOnly");
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("simpleWriteOnly Threw exception: " + t);
        }
        assertEquals("simpleReadOnly", testBean.getSimpleReadOnly(), result);

        try {
            result = PropertyUtils.getProperty(wrapDynaBean, "simpleWriteOnly");
        } catch (final IllegalArgumentException ex) {
            threwIllegalArgumentException = true; // expected result
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("simpleWriteOnly Threw exception: " + t);
        }
        assertTrue("Expected IllegalArgumentException but returned '" + result + "'", threwIllegalArgumentException);
    }

    /**
     * Test {@link PropertyUtils#setProperty(Object, String, Object)}
     * for simple properties.
     */
    public void testIssue_BEANUTILS_61_PropertyUtils_setProperty() {
        boolean threwIllegalArgumentException = false;
        try {
            PropertyUtils.setProperty(wrapDynaBean, "simpleReadOnly", "READONLY-SIMPLE-BAR");
        } catch (final IllegalArgumentException ex) {
            threwIllegalArgumentException = true; // expected result
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("simpleReadOnly Threw exception: " + t);
        }
        assertTrue("Expected IllegalArgumentException", threwIllegalArgumentException);

        try {
            PropertyUtils.setProperty(wrapDynaBean, "simpleWriteOnly", "SIMPLE-BAR");
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("simpleWriteOnly Threw exception: " + t);
        }
        assertEquals("simpleWriteOnly", testBean.getSimpleReadOnly(), "SIMPLE-BAR");
    }

    /**
     * Test {@link PropertyUtils#getProperty(Object, String)}
     * for indexed properties.
     */
    public void testIssue_BEANUTILS_61_PropertyUtils_getProperty_Indexed() {
        boolean threwIllegalArgumentException = false;
        Object result = null;
        try {
            result = PropertyUtils.getProperty(wrapDynaBean, "indexedReadOnly[0]");
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("indexedReadOnly Threw exception: " + t);
        }
        assertEquals("indexedReadOnly", testBean.getIndexedReadOnly(0), result);

        try {
            result = PropertyUtils.getProperty(wrapDynaBean, "indexedWriteOnly[0]");
        } catch (final IllegalArgumentException ex) {
            threwIllegalArgumentException = true; // expected result
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("indexedWriteOnly Threw exception: " + t);
        }
        assertTrue("Expected IllegalArgumentException but returned '" + result + "'", threwIllegalArgumentException);
    }

    /**
     * Test {@link PropertyUtils#setProperty(Object, String, Object)}
     * for indexed properties.
     */
    public void testIssue_BEANUTILS_61_PropertyUtils_setProperty_Indexed() {
        boolean threwIllegalArgumentException = false;
        try {
            PropertyUtils.setProperty(wrapDynaBean, "indexedReadOnly[0]", "READONLY-INDEXED-BAR");
        } catch (final IllegalArgumentException ex) {
            threwIllegalArgumentException = true; // expected result
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("indexedReadOnly Threw exception: " + t);
        }
        assertTrue("Expected IllegalArgumentException", threwIllegalArgumentException);

        try {
            PropertyUtils.setProperty(wrapDynaBean, "indexedWriteOnly[0]", "INDEXED-BAR");
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("indexedWriteOnly Threw exception: " + t);
        }
        assertEquals("indexedWriteOnly", testBean.getIndexedReadOnly(0), "INDEXED-BAR");
    }

    /**
     * Test {@link PropertyUtils#getProperty(Object, String)}
     * for mapped properties.
     */
    public void testIssue_BEANUTILS_61_PropertyUtils_getProperty_Mapped() {
        boolean threwIllegalArgumentException = false;
        Object result = null;
        try {
            result = PropertyUtils.getProperty(wrapDynaBean, "mappedReadOnly(foo-key)");
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("mappedReadOnly Threw exception: " + t);
        }
        assertEquals("mappedReadOnly", testBean.getMappedReadOnly("foo-key"), result);

        try {
            result = PropertyUtils.getProperty(wrapDynaBean, "mappedWriteOnly(foo-key)");
        } catch (final IllegalArgumentException ex) {
            threwIllegalArgumentException = true; // expected result
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("mappedWriteOnly Threw exception: " + t);
        }
        assertTrue("Expected IllegalArgumentException but returned '" + result + "'", threwIllegalArgumentException);
    }

    /**
     * Test {@link PropertyUtils#setProperty(Object, String, Object)}
     * for mapped properties.
     */
    public void testIssue_BEANUTILS_61_PropertyUtils_setProperty_Mapped() {
        boolean threwIllegalArgumentException = false;
        try {
            PropertyUtils.setProperty(wrapDynaBean, "mappedReadOnly(foo-key)", "READONLY-MAPPED-BAR");
        } catch (final IllegalArgumentException ex) {
            threwIllegalArgumentException = true; // expected result
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("mappedReadOnly Threw exception: " + t);
        }
        assertTrue("Expected IllegalArgumentException", threwIllegalArgumentException);

        try {
            PropertyUtils.setProperty(wrapDynaBean, "mappedWriteOnly(foo-key)", "MAPPED-BAR");
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("mappedWriteOnly Threw exception: " + t);
        }
        assertEquals("mappedWriteOnly", testBean.getMappedReadOnly("foo-key"), "MAPPED-BAR");
    }

    /**
     * Test {@link PropertyUtils#copyProperties(Object, Object)}
     * to a read-only WrapDynaBean property.
     */
    public void testIssue_BEANUTILS_61_PropertyUtils_copyProperties_to_WrapDynaBean() {
        final String value = "copied simpleReadOnly";
        final Map<String, Object> source = new HashMap<String, Object>();
        source.put("simpleReadOnly", value);
        try {
            PropertyUtils.copyProperties(wrapDynaBean, source);
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("copyProperties Threw exception: " + t);
        }
        assertFalse("Target value='" + value + "'", value.equals(testBean.getSimpleReadOnly()));
    }

    /**
     * Test {@link PropertyUtils#copyProperties(Object, Object)}
     * to a read-only WrapDynaBean property.
     */
    public void testIssue_BEANUTILS_61_PropertyUtils_copyProperties_from_WrapDynaBean() {
        final String value = "ORIG TARGET VALUE";
        final TestBean targetBean = Jira61BeanFactory.createBean();
        targetBean.setSimpleWriteOnly(value);
        try {
            PropertyUtils.copyProperties(targetBean, wrapDynaBean);
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("copyProperties Threw exception: " + t);
        }
        assertTrue("Target value='" + targetBean.getSimpleReadOnly() + "'", value.equals(targetBean.getSimpleReadOnly()));
    }

    /**
     * Test {@link BeanUtils#copyProperties(Object, Object)}
     * to a read-only WrapDynaBean property.
     */
    public void testIssue_BEANUTILS_61_BeanUtils_copyProperties_to_WrapDynaBean() {
        final String value = "copied simpleReadOnly";
        final Map<String, Object> source = new HashMap<String, Object>();
        source.put("simpleReadOnly", value);
        try {
            BeanUtils.copyProperties(wrapDynaBean, source);
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("copyProperties Threw exception: " + t);
        }
        assertFalse("Target value='" + value + "'", value.equals(testBean.getSimpleReadOnly()));
    }

    /**
     * Test {@link BeanUtils#copyProperties(Object, Object)}
     * to a read-only WrapDynaBean property.
     */
    public void testIssue_BEANUTILS_61_BeanUtils_copyProperties_from_WrapDynaBean() {
        final String value = "ORIG TARGET VALUE";
        final TestBean targetBean = Jira61BeanFactory.createBean();
        targetBean.setSimpleWriteOnly(value);
        try {
            BeanUtils.copyProperties(targetBean, wrapDynaBean);
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("copyProperties Threw exception: " + t);
        }
        assertTrue("Target value='" + targetBean.getSimpleReadOnly() + "'", value.equals(targetBean.getSimpleReadOnly()));
    }
}
