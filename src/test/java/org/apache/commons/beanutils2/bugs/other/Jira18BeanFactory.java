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
package org.apache.commons.beanutils2.bugs.other;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils2.bugs.Jira18TestCase;

/**
 * Factory which creates <i>package</i> scope beans with
 * public methods for {@link Jira18TestCase}.
 */
public class Jira18BeanFactory {

    /**
     * Factory method which creates package friendly beans.
     *
     * @return The a package friendly bean with public methods
     */
    public static Object createBean() {
        return new PackageFriendlyBean();
    }

    /* =============== Package Friendly Bean =============== */
    static class PackageFriendlyBean {

        private final String[] indexed = new String[] {"one", "two", "three"};
        private String simple = "FOO";
        private final Map<String, Object> mapped = new HashMap<>();

        /** Default Constructor */
        public PackageFriendlyBean() {
            mapped.put("foo-key", "foo-value");
            mapped.put("bar-key", "bar-value");
        }
        /**
         * Gets simple property.
         *
         * @return The simple value
         */
        public String getSimple() {
            return simple;
        }

        /**
         * Sets simple property.
         *
         * @param simple The simple value
         */
        public void setSimple(final String simple) {
            this.simple = simple;
        }

        /**
         * Gets indexed property.
         *
         * @param index The index
         * @return The indexed value
         */
        public String getIndexed(final int index) {
            return indexed[index];
        }

        /**
         * Sets indexed property.
         *
         * @param index The index
         * @param value The indexed value
         */
        public void setIndexed(final int index, final String value) {
            this.indexed[index] = value;
        }

        /**
         * Gets mapped property.
         *
         * @param key The mapped key
         * @return The mapped value
         */
        public String getMapped(final String key) {
            return (String)mapped.get(key);
        }

        /**
         * Sets mapped property.
         *
         * @param key The mapped key
         * @param value The mapped value
         */
        public void setMapped(final String key, final String value) {
            mapped.put(key, value);
        }
    }
}
