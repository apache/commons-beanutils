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

import org.apache.commons.beanutils2.bugs.Jira87TestCase;

/**
 * Factory which creates beans for {@link Jira87TestCase}.
 */
public class Jira87BeanFactory {

    /**
     * Factory method which creates beans bean with mapped method.
     *
     * @return The the mapped property bean instance
     */
    public static PublicMappedInterface createMappedPropertyBean() {
        return new PackageMappedImpl();
    }

    /* =================== Public interface with Mapped Property ========================= */
    /**
     * Public interface with a mapped property.
     */
    public interface PublicMappedInterface {

        /**
         * Mapped Property method
         * @param key The key of the mapped value
         * @return The value
         */
        Object getValue(String key);
    }

    /* =============== Package Friendly implementation of public interface =============== */
    static class PackageMappedImpl implements PublicMappedInterface {

        /**
         * This implementation returns the key value.
         *
         * @param key The key of the mapped value
         * @return The key value
         */
        @Override
        public Object getValue(final String key) {
            return key;
        }
    }
}
