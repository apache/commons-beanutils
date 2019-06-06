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

import org.apache.commons.beanutils.AlphaBean;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.SuppressPropertiesBeanIntrospector;

import junit.framework.TestCase;

/**
 * Fix CVE: https://nvd.nist.gov/vuln/detail/CVE-2014-0114
 *
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-520">https://issues.apache.org/jira/browse/BEANUTILS-520</a>
 */
public class Jira520TestCase extends TestCase {
    /**
     * By default opt-in to security that does not allow access to "class".
     */
    public void testSuppressClassPropertyByDefault() throws Exception {
        final BeanUtilsBean bub = new BeanUtilsBean();
        final AlphaBean bean = new AlphaBean();
        try {
            bub.getProperty(bean, "class");
            fail("Could access class property!");
        } catch (final NoSuchMethodException ex) {
            // ok
        }
    }

    /**
     * Allow opt-out to make your app less secure but allow access to "class".
     */
    public void testAllowAccessToClassProperty() throws Exception {
        final BeanUtilsBean bub = new BeanUtilsBean();
        bub.getPropertyUtils().removeBeanIntrospector(SuppressPropertiesBeanIntrospector.SUPPRESS_CLASS);
        final AlphaBean bean = new AlphaBean();
        String result = bub.getProperty(bean, "class");
        assertEquals("Class property should have been accessed", "class org.apache.commons.beanutils.AlphaBean", result);
    }
}