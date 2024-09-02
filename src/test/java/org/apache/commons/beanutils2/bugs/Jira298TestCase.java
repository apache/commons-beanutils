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

import java.lang.reflect.Method;

import org.apache.commons.beanutils2.MethodUtils;
import org.apache.commons.beanutils2.PropertyUtils;
import org.apache.commons.beanutils2.bugs.other.Jira298BeanFactory;
import org.apache.commons.beanutils2.bugs.other.Jira298BeanFactory.IX;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-298">https://issues.apache.org/jira/browse/BEANUTILS-298</a>
 */
public class Jira298TestCase {

    private static final Log LOG = LogFactory.getLog(Jira298TestCase.class);

    /**
     * Sets up.
     *
     * @throws Exception
     */
    @BeforeEach
    protected void setUp() throws Exception {
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
     * Test {@link MethodUtils#getAccessibleMethod(Class, Method)}
     */
    @Test
    public void testIssue_BEANUTILS_298_MethodUtils_getAccessibleMethod() throws Exception {
        final Object bean = Jira298BeanFactory.createImplX();
        Object result = null;
        final Method m2 = MethodUtils.getAccessibleMethod(bean.getClass(), "getName", new Class[0]);
        result = m2.invoke(bean);
        assertEquals("BaseX name value", result);
    }

    /**
     * Test {@link PropertyUtils#getProperty(Object, String)}
     */
    @Test
    public void testIssue_BEANUTILS_298_PropertyUtils_getProperty() throws Exception {
        final Object bean = Jira298BeanFactory.createImplX();
        Object result = PropertyUtils.getProperty(bean, "name");
        assertEquals("BaseX name value", result);
    }

    /**
     * Test {@link PropertyUtils#setProperty(Object, String, Object)}
     */
    @Test
    public void testIssue_BEANUTILS_298_PropertyUtils_setProperty() throws Exception {
        final Object bean = Jira298BeanFactory.createImplX();
        assertEquals("BaseX name value", ((IX) bean).getName());
        PropertyUtils.setProperty(bean, "name", "new name");
        assertEquals("new name", ((IX) bean).getName());
    }
}
