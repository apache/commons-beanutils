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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.beanutils2.BeanUtils;
import org.apache.commons.beanutils2.BeanUtilsBean;
import org.apache.commons.beanutils2.SuppressPropertiesBeanIntrospector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Beanutils's describe() method cannot determine reader methods for anonymous class - see Jira issue# BEANUTILS-157.
 *
 * See <a href="https://issues.apache.org/jira/browse/BEANUTILS-157">https://issues.apache.org/jira/browse/BEANUTILS-157<a/>
 */
public class Jira157TestCase {

    public static class FooBar {
        String getPackageFoo() {
            return "Package Value";
        }

        @SuppressWarnings("unused")
        private String getPrivateFoo() {
            return "PrivateFoo Value";
        }

        protected String getProtectedFoo() {
            return "ProtectedFoo Value";
        }

        public String getPublicFoo() {
            return "PublicFoo Value";
        }
    }

    public interface XY {
        String getX();

        String getY();
    }

    private static final Log LOG = LogFactory.getLog(Jira157TestCase.class);

    /**
     * Sets up.
     *
     * @throws Exception
     */
    @BeforeEach
    protected void setUp() throws Exception {

        final BeanUtilsBean custom = new BeanUtilsBean();
        custom.getPropertyUtils().removeBeanIntrospector(SuppressPropertiesBeanIntrospector.SUPPRESS_CLASS);
        BeanUtilsBean.setInstance(custom);
    }

    /**
     * Tear Down.
     *
     * @throws Exception
     */
    @AfterEach
    protected void tearDown() throws Exception {
    }

    /**
     * Test with an private class that overrides a public method of a "grand parent" public class.
     * <p />
     * See Jira issue# BEANUTILS-157.
     */
    @Test
    public void testIssue_BEANUTILS_157_BeanUtils_Describe_Bean() throws Exception {
        final Object bean = new FooBar();
        Map<String, String> result = null;
        result = BeanUtils.describe(bean);
        assertEquals(2, result.size(), "Check Size");
        assertTrue(result.containsKey("class"), "Class");
        assertTrue(result.containsKey("publicFoo"), "publicFoo Key");
        assertEquals("PublicFoo Value", result.get("publicFoo"), "publicFoo Value");
    }

    /**
     * Test with an private class that overrides a public method of a "grand parent" public class.
     * <p />
     * See Jira issue# BEANUTILS-157.
     */
    @Test
    public void testIssue_BEANUTILS_157_BeanUtils_Describe_Interface() throws Exception {
        final Object bean = new XY() {
            @Override
            public String getX() {
                return "x-value";
            }

            @Override
            public String getY() {
                return "y-value";
            }
        };
        final Map<String, String> result = BeanUtils.describe(bean);
        assertEquals(3, result.size(), "Check Size");
        assertTrue(result.containsKey("class"), "Class");
        assertTrue(result.containsKey("x"), "X Key");
        assertTrue(result.containsKey("y"), "Y Key");
        assertEquals("x-value", result.get("x"), "X Value");
        assertEquals("y-value", result.get("y"), "Y Value");
    }

    /**
     * Test with an private class that overrides a public method of a "grand parent" public class.
     * <p />
     * See Jira issue# BEANUTILS-157.
     */
    @Test
    public void testIssue_BEANUTILS_157_BeanUtils_Describe_Serializable() throws Exception {
        final Object bean = new Serializable() {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unused")
            public String getX() {
                return "x-value";
            }

            @SuppressWarnings("unused")
            public String getY() {
                return "y-value";
            }
        };
        final Map<String, String> result = BeanUtils.describe(bean);
        assertEquals(1, result.size(), "Check Size");
        assertTrue(result.containsKey("class"), "Class");
    }
}
