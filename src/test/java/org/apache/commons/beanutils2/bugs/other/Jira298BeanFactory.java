/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.beanutils2.bugs.other;

import org.apache.commons.beanutils2.bugs.Jira298Test;

/**
 * Factory which creates beans for {@link Jira298Test}.
 */
public class Jira298BeanFactory {

    static class BaseX {
        private String name = "BaseX name value";

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }
    }

    static class ImplX extends BaseX implements IX {
    }

    public interface IX {
        String getName();

        void setName(String name);
    }

    /**
     * Factory method which creates ImplX.
     *
     * @return a new ImplX.
     */
    public static IX createImplX() {
        return new ImplX();
    }
}
