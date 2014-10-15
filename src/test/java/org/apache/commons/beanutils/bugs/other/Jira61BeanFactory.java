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
package org.apache.commons.beanutils.bugs.other;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.bugs.Jira61TestCase;

/**
 * Factory which creates beans for {@link Jira61TestCase}.
 *
 * @version $Id$
 */
public class Jira61BeanFactory {

    /**
     * Factory method which creates a new {@link TestBean}.
     *
     * @return a new {@link TestBean}.
     */
    public static TestBean createBean() {
        return new TestBean();
    }

    /**
     * Test Bean
     */
    public static class TestBean {

        private final String[] indexed = new String[] {"one", "two", "three"};
        private String simple = "FOO";
        private final Map<String, Object> mapped = new HashMap<String, Object>();

        /** Default Constructor */
        public TestBean() {
            mapped.put("foo-key", "foo-value");
            mapped.put("bar-key", "bar-value");
        }

        /**
         * Return simpleReadOnly
         *
         * @return the simple value
         */
        public String getSimpleReadOnly() {
            return simple;
        }

        /**
         * Set simpleWriteOnly
         *
         * @param simple simple value
         */
        public void setSimpleWriteOnly(final String simple) {
            this.simple = simple;
        }

        /**
         * Return indexed property.
         *
         * @param index The index
         * @return The indexed value
         */
        public String getIndexedReadOnly(final int index) {
            return indexed[index];
        }

        /**
         * Set indexed property.
         *
         * @param index The index
         * @param value The indexed value
         */
        public void setIndexedWriteOnly(final int index, final String value) {
            this.indexed[index] = value;
        }

        /**
         * Return mapped property.
         *
         * @param key The mapped key
         * @return The mapped value
         */
        public String getMappedReadOnly(final String key) {
            return (String)mapped.get(key);
        }

        /**
         * Set mapped property.
         *
         * @param key The mapped key
         * @param value The mapped value
         */
        public void setMappedWriteOnly(final String key, final String value) {
            mapped.put(key, value);
        }

    }

}
