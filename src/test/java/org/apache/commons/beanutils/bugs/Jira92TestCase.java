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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.TestBean;

/**
 * Test case for Jira issue# BEANUTILS-92.
 *
 * @version $Id$
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-92">https://issues.apache.org/jira/browse/BEANUTILS-92</a>
 */
public class Jira92TestCase extends TestCase {

    /**
     * Test copy properties where the target bean only
     * has an indexed setter.
     */
    public void testIssue_BEANUTILS_92_copyProperties_indexed_only_setter() throws Exception {
        PropertyUtils.copyProperties(new Jira92TestBean(), new TestBean());
    }

    /**
     * Test bean which has only indexed setter
     */
    public static class Jira92TestBean {

        private final java.util.Date[] dateArrayProperty = new java.util.Date[10];

        /**
         * Indexed Setter.
         *
         * @param index index
         * @param date indexed value to set
         */
        public void setDateArrayProperty(final int index, final java.util.Date date) {
            this.dateArrayProperty[index] = date;
        }
    }
}
