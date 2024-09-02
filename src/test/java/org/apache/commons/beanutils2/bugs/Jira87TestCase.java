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
import org.apache.commons.beanutils2.bugs.other.Jira87BeanFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test case for Jiar issue# BEANUTILS-87.
 *
 * <p>
 * In BeanUtils 1.7.0 a "package friendly" implementation of a public interface with defined a "mapped property" caused an {@link IllegalAccessException} to be
 * thrown by PropertyUtils's getMappedProperty method.
 *
 * <p>
 * This test case demonstrates the issue.
 *
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-87">https://issues.apache.org/jira/browse/BEANUTILS-87</a>
 */
public class Jira87TestCase {

    private static final Log LOG = LogFactory.getLog(Jira87TestCase.class);

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
     * Interface definition with a mapped property
     */
    @Test
    public void testJira87() throws Exception {

        final Jira87BeanFactory.PublicMappedInterface bean = Jira87BeanFactory.createMappedPropertyBean();
        // N.B. The test impl. returns the key value
        assertEquals("foo", PropertyUtils.getMappedProperty(bean, "value(foo)"));
    }
}
