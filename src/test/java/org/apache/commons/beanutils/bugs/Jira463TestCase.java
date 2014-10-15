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

import junit.framework.TestCase;

import org.apache.commons.beanutils.AlphaBean;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.SuppressPropertiesBeanIntrospector;

/**
 * Class loader vulnerability in DefaultResolver
 *
 * @version $Id$
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-463">https://issues.apache.org/jira/browse/BEANUTILS-463</a>
 */
public class Jira463TestCase extends TestCase {
    /**
     * Tests that with a specialized {@code BeanIntrospector} implementation the class
     * property can be suppressed.
     */
    public void testSuppressClassProperty() throws Exception {
        final BeanUtilsBean bub = new BeanUtilsBean();
        bub.getPropertyUtils().addBeanIntrospector(
                SuppressPropertiesBeanIntrospector.SUPPRESS_CLASS);
        final AlphaBean bean = new AlphaBean();
        try {
            bub.getProperty(bean, "class");
            fail("Could access class property!");
        } catch (final NoSuchMethodException ex) {
            // ok
        }
    }
}
