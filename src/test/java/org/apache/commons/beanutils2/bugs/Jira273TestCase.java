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

import org.apache.commons.beanutils2.PropertyUtils;
import org.apache.commons.beanutils2.bugs.other.Jira273BeanFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Public methods overridden in anonymous or private subclasses are not recognized by PropertyUtils - see issue# BEANUTILS-273.
 *
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-273">https://issues.apache.org/jira/browse/BEANUTILS-273</a>
 */
public class Jira273TestCase {

    private static final Log LOG = LogFactory.getLog(Jira273TestCase.class);

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
     * Test with an anonymous class that inherits a public method of a public class.
     */
    @Test
    public void testIssue_BEANUTILS_273_AnonymousNotOverridden() throws Exception {
        final Object bean = Jira273BeanFactory.createAnonymousNotOverridden();
        Object result = PropertyUtils.getProperty(bean, "beanValue");
        assertEquals("PublicBeanWithMethod", result);
    }

    /**
     * Test with an anonymous class that overrides a public method of a public class.
     */
    @Test
    public void testIssue_BEANUTILS_273_AnonymousOverridden() throws Exception {
        final Object bean = Jira273BeanFactory.createAnonymousOverridden();
        Object result = PropertyUtils.getProperty(bean, "beanValue");
        assertEquals("AnonymousOverridden", result);
    }

    /**
     * Test with an private class that inherits a public method of a "grand parent" public class.
     */
    @Test
    public void testIssue_BEANUTILS_273_PrivatePrivatePublicNotOverridden() throws Exception {
        final Object bean = Jira273BeanFactory.createPrivatePrivatePublicNotOverridden();
        Object result = PropertyUtils.getProperty(bean, "beanValue");
        assertEquals("PublicBeanWithMethod", result);
    }

    /**
     * Test with an private class that overrides a public method of a "grand parent" public class.
     */
    @Test
    public void testIssue_BEANUTILS_273_PrivatePrivatePublicOverridden() throws Exception {
        final Object bean = Jira273BeanFactory.createPrivatePrivatePublicOverridden();
        Object result = PropertyUtils.getProperty(bean, "beanValue");
        assertEquals("PrivatePrivatePublicOverridden", result);
    }

    /**
     * Test with an private class that inherits a public method of a public class.
     */
    @Test
    public void testIssue_BEANUTILS_273_PrivatePublicNotOverridden() throws Exception {
        final Object bean = Jira273BeanFactory.createPrivatePublicNotOverridden();
        Object result = PropertyUtils.getProperty(bean, "beanValue");
        assertEquals("PublicBeanWithMethod", result);
    }

    /**
     * Test with an private class that overrides a public method of a public class.
     */
    @Test
    public void testIssue_BEANUTILS_273_PrivatePublicOverridden() throws Exception {
        final Object bean = Jira273BeanFactory.createPrivatePublicOverridden();
        Object result = PropertyUtils.getProperty(bean, "beanValue");
        assertEquals("PrivatePublicOverridden", result);
    }
}
